package view;

import model.Empleado;
import model.Nomina;
import service.NominaService;
import utils.Validaciones;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class NominaFrame extends JFrame {
    private NominaService nominaService;
    private static final Color BG = new Color(0xF5F2F1), GR = new Color(0x8C8C8C), BK = Color.BLACK;
    private final Map<JComponent, double[]> refs = new LinkedHashMap<>();
    private double horas = 0, bonos = 0, S = 1, ox, oy, lastS;
    private boolean seguro = true;
    private String sesion = "Nadie ha iniciado sesión";
    private Fld fN, fC, fG, fS, fB;
    private JPanel root;

    public NominaFrame() {
        nominaService = new NominaService();
        setTitle("LASKT - Nómina de empleados");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        root = buildCustomUI();
        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(800, 450));
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ---------- Utilidades de dibujo y texto exactas de NominaLaskt ----------
    private void t(Graphics2D g, String s, double x, double cy, double sz, Color c, boolean ctr) {
        Font f = new Font("Arial", Font.BOLD, 100).deriveFont((float) sz);
        double w = f.getStringBounds(s, g.getFontRenderContext()).getWidth();
        g.setFont(f); g.setColor(c); g.drawString(s, (float) (ctr ? x - w / 2 : x), (float) (cy + .36 * sz));
    }

    private Stroke st(double w) { return new BasicStroke((float) w, 1, 1); }
    private Shape o(double x, double y, double r) { return new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r); }
    private Shape rr(double x, double y, double w, double h, double a) { return new RoundRectangle2D.Double(x, y, w, h, a, a); }
    private void msg(String t, Object m) { JOptionPane.showMessageDialog(root, m, t, JOptionPane.PLAIN_MESSAGE); }
    private String ask(String m) { String s = JOptionPane.showInputDialog(root, m); return s == null ? null : s.trim(); }
    private double num(String m) { try { return Double.parseDouble(ask(m).replace(",", "")); } catch (Exception e) { return -1; } }
    private String ced(String c) { if (c.length() < 11) return c; return c.substring(0, 3) + "-" + c.substring(3, 10) + "-" + c.substring(10); }

    // ---------- Componentes de Texto Personalizados (Píldoras) ----------
    private static class Fld extends JTextField {
        boolean pill; String ph;
        Fld(boolean p, String ph) {
            pill = p; this.ph = ph; setOpaque(false); setBorder(null); setHorizontalAlignment(p ? LEFT : CENTER);
            addFocusListener(new FocusAdapter() { 
                public void focusLost(FocusEvent e) { repaint(); } 
                public void focusGained(FocusEvent e) { repaint(); } 
            });
        }
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create(); g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            if (pill) { 
                g.setColor(Color.WHITE); g.fillRoundRect(0, 0, w, h, h, h); 
                g.setColor(new Color(0xBDBDBD)); g.setStroke(new BasicStroke(1.5f)); g.drawRoundRect(1, 1, w - 3, h - 3, h, h); 
            }
            super.paintComponent(g0);
            if (ph != null && getText().isEmpty() && !isFocusOwner()) {
                FontMetrics fm = g.getFontMetrics(getFont()); g.setFont(getFont()); g.setColor(new Color(0xCFCFCF));
                g.drawString(ph, (w - fm.stringWidth(ph)) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
            }
            g.dispose();
        }
    }

    private void put(JComponent c, double x, double y, double w, double h) { refs.put(c, new double[]{x, y, w, h}); root.add(c); }
    private void hot(double x, double y, double w, double h, Runnable r) {
        JComponent c = new JComponent() {}; c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { r.run(); } }); put(c, x, y, w, h);
    }
    private Fld fld(double x, double y, double w, double h, boolean pill, String ph) { Fld f = new Fld(pill, ph); put(f, x, y, w, h); return f; }

 // ---------- Dibujo Vectorial del Menú Lateral (Original de NominaLaskt) ----------
    private void drawIcons(Graphics2D g) {
        g.setColor(BK);
        
        // 1. Notificaciones (Campana) y Perfil (Barra Superior)
        g.setStroke(st(2.6));
        g.draw(o(1752, 58, 12)); g.fill(o(1752, 73, 3)); g.draw(o(1842, 58, 22)); g.draw(o(1842, 50, 8));
        Path2D p = new Path2D.Double(); p.moveTo(1824, 75); p.curveTo(1828, 65, 1856, 65, 1860, 75); g.draw(p);

        // 2. Menú Lateral - Código exacto de NominaLaskt
        Path2D.Double h = new Path2D.Double(); 
        double[] pts = {83, 155, 124, 192, 113, 192, 113, 243, 94, 243, 94, 215, 72, 215, 72, 243, 53, 243, 53, 192, 42, 192};
        h.moveTo(pts[0], pts[1]); 
        for (int i = 2; i < pts.length; i += 2) h.lineTo(pts[i], pts[i + 1]); 
        h.closePath(); 
        g.setColor(BK); 
        g.fill(h);

        // Ícono Nosotros (2 personas)
        g.fill(o(103, 325, 14)); 
        g.fill(new Arc2D.Double(78, 344, 50, 54, 0, 180, Arc2D.PIE));
        Shape c1 = o(70, 330, 15), c2 = new Arc2D.Double(40, 349, 56, 50, 0, 180, Arc2D.PIE); 
        g.setColor(BG); g.setStroke(st(5)); 
        g.draw(c1); g.draw(c2); 
        g.setColor(BK); g.fill(c1); g.fill(c2);

        // Ícono Contactos (Teléfono)
        g.setStroke(st(13)); 
        Path2D.Double ph = new Path2D.Double(); 
        ph.moveTo(56, 462); ph.curveTo(54, 490, 72, 508, 100, 510); 
        g.draw(ph); 
        for (double[] q : new double[][]{{40, 440, 32, 26}, {92, 494, 28, 32}}) {
            g.fill(AffineTransform.getRotateInstance(Math.toRadians(-35), q[0] + q[2] / 2, q[1] + q[3] / 2).createTransformedShape(rr(q[0], q[1], q[2], q[3], 12)));
        }
        g.setStroke(st(4)); 
        for (double r : new double[]{14, 26, 38}) {
            g.draw(new Arc2D.Double(92 - r, 480 - r, 2 * r, 2 * r, 5, 80, Arc2D.OPEN));
        }

        // Ícono Calculadora
        g.fill(rr(52, 575, 62, 88, 9)); 
        g.setColor(Color.WHITE); 
        g.fill(new Rectangle2D.Double(58, 581, 50, 15)); 
        t(g, "12x89=456", 83, 589, 6.5, BK, true); 
        g.setColor(Color.WHITE); g.setStroke(st(2));
        for (double yy : new double[]{612, 631}) {
            for (double xx : new double[]{67, 83, 99}) {
                g.draw(o(xx, yy, 6));
            }
        }
        g.draw(rr(62, 643, 42, 14, 14)); 
        
        // Ícono Empleados
        g.setColor(BK); 
        g.fill(o(83, 741, 23));
        Path2D.Double pe = new Path2D.Double(); 
        pe.moveTo(46, 800); pe.curveTo(46, 772, 60, 765, 83, 765); 
        pe.curveTo(106, 765, 120, 772, 120, 800); 
        pe.closePath(); 
        g.fill(pe);

        // Ícono Configuración (Engranaje)
        for (int i = 0; i < 12; i++) {
            g.fill(AffineTransform.getRotateInstance(Math.toRadians(i * 30), 83, 899).createTransformedShape(rr(76.5, 852, 13, 17, 3)));
        }
        g.fill(o(83, 899, 37)); 
        g.setColor(BG); g.fill(o(83, 899, 22)); 
        g.setColor(BK); g.fill(o(83, 899, 13)); 
        g.setColor(BG); g.fill(o(83, 899, 6));
    }

    // ---------- Construcción de la Interfaz Visual (1920x1080) ----------
    private JPanel buildCustomUI() {
        // Inicializamos 'root' de una vez para garantizar que no sea nulo al registrar componentes
        root = new JPanel(null) {
            public void doLayout() {
                S = Math.min(getWidth() / 1920.0, getHeight() / 1080.0); ox = (getWidth() - 1920 * S) / 2; oy = (getHeight() - 1080 * S) / 2;
                for (Map.Entry<JComponent, double[]> en : refs.entrySet()) {
                    double[] r = en.getValue(); JComponent c = en.getKey();
                    c.setBounds((int) Math.round(ox + r[0] * S), (int) Math.round(oy + r[1] * S), (int) Math.round(r[2] * S), (int) Math.round(r[3] * S));
                    if (c instanceof Fld && S != lastS) {
                        boolean b = ((Fld) c).pill; c.setFont(new Font("Arial", b ? Font.PLAIN : Font.BOLD, (int) Math.round((b ? 30 : 34) * S)));
                        if (b) ((Fld) c).setBorder(BorderFactory.createEmptyBorder(0, (int) (32 * S), 0, (int) (32 * S)));
                    }
                }
                lastS = S;
            }
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(BG); g.fillRect(0, 0, getWidth(), getHeight()); g.translate(ox, oy); g.scale(S, S);
                double L = -ox / S, R = (getWidth() - ox) / S, T = -oy / S;
                
                // Header
                g.setColor(Color.WHITE); g.fill(new Rectangle2D.Double(L, T, R - L, 119 - T)); 
                g.setColor(GR); g.setStroke(new BasicStroke(1.5f)); g.draw(new Line2D.Double(L, 119.5, R, 119.5));
                
                t(g, "LASKT", 60, 62, 70, BK, false); 
                g.setColor(BK); g.fill(new Rectangle2D.Double(331, 30, 38, 4)); g.fill(new Rectangle2D.Double(325, 37, 50, 5)); g.fill(rr(319, 44, 61, 42, 4)); t(g, "$", 349.5, 65, 38, Color.WHITE, true);
                t(g, "Administración", 503, 61, 34, BK, false); t(g, "Calendario", 834, 61, 34, BK, false); t(g, "Soporte", 1081, 61, 34, BK, false);
                
                // Campo de Búsqueda
                g.setColor(Color.WHITE); g.fill(rr(1366, 34, 342, 52, 52)); g.setColor(BK); g.setStroke(new BasicStroke(3f)); g.draw(rr(1366, 34, 342, 52, 52));
                g.setStroke(st(2.6)); g.draw(o(1667, 56, 11)); g.draw(new Line2D.Double(1675, 64, 1686, 75));
                
                // Menú Lateral
                Shape side = rr(L - 100, 138, 419 - (L - 100), 922, 80); g.setColor(Color.WHITE); g.fill(side); g.setColor(GR); g.setStroke(new BasicStroke(1.6f)); g.draw(side);
                String[] nb = {"Inicio", "Nosotros", "Contactos", "Calculadora", "Empleados", "Configuración"}; double[] tx = {203, 168, 158, 150, 150, 145};
                for (int i = 0; i < 6; i++) t(g, nb[i], tx[i], 200 + 140 * i, 33, BK, false);
                
                // Título y Botones
                t(g, "Nómina de empleados", 883, 197, 48, BK, true);
                for (double bx : new double[]{1266, 1592}) { g.setColor(Color.WHITE); g.fill(rr(bx, 157, 270, 84, 84)); g.setColor(GR); g.setStroke(new BasicStroke(2f)); g.draw(rr(bx, 157, 270, 84, 84)); }
                t(g, "Iniciar", 1401, 200, 30, BK, true); t(g, "Registrar", 1727, 200, 30, BK, true);
                
                // Tarjetas de Extras (Horas extra, Bonos, Seguro) EXACTAS DE NOMINALASKT
                String[] ct = {"Horas extras", "Bonos", "Seguro"}; 
                double[] cx = {441, 931, 1421}, tc = {658, 1143, 1656};
                for (int i = 0; i < 3; i++) {
                    Shape c = rr(cx[i] + 1, 284, 449, 258, 72); g.setColor(BG); g.fill(c); 
                    Graphics2D gi = (Graphics2D) g.create(); gi.clip(c); gi.setColor(BK);
                    if (i == 0) { 
                        gi.fill(o(500, 498, 64)); gi.fill(o(545, 443, 27)); gi.fill(o(450, 438, 20)); 
                        gi.setColor(BG); gi.setStroke(new BasicStroke(8f)); 
                        gi.draw(new Line2D.Double(490, 455, 490, 513)); gi.draw(new Line2D.Double(448, 513, 490, 513)); 
                    } else if (i == 1) { 
                        gi.fill(o(1152, 527, 113)); gi.setColor(BG); 
                        gi.fill(new Rectangle2D.Double(1105, 432, 93, 9)); gi.fill(new Rectangle2D.Double(1105, 449, 93, 15)); 
                        t(gi, "$", 1152, 508, 110, BG, true); 
                    } else { 
                        gi.setStroke(new BasicStroke(12f)); gi.draw(o(1872, 533, 123)); 
                        gi.fill(rr(1795, 478, 90, 70, 14)); gi.draw(new Arc2D.Double(1816, 452, 52, 52, 0, 180, Arc2D.OPEN)); 
                    }
                    gi.dispose(); 
                    g.setColor(BK); g.setStroke(new BasicStroke(4.5f)); g.draw(c); 
                    t(g, ct[i], tc[i], 353, 48, BK, true);
                }
                
                // Campos de Entrada
                String[] et = {"Nombre", "Cédula", "Cargo", "Salario"}; 
                for (int i = 0; i < 4; i++) { 
                    double y = 574 + 123 * i; g.setColor(BG); g.fill(rr(441, y, 1443, 102, 102)); g.setColor(BK); g.setStroke(new BasicStroke(4.5f)); g.draw(rr(441, y, 1443, 102, 102)); 
                    t(g, et[i], i == 3 ? 498 : 501, y + 51, 48, BK, false); 
                }

                // Íconos vectoriales del menú y cabecera
                drawIcons(g);

                g.dispose();
            }
        };

        // Instanciación de campos de texto en píldora
        fN = fld(748, 588, 1035, 71, true, null); 
        fC = fld(753, 714, 1035, 72, true, null); 
        fG = fld(749, 835, 1035, 72, true, null); 
        fS = fld(753, 961, 1035, 72, true, null); 
        fB = fld(1388, 36, 256, 48, false, "Buscar");

        // Formato automático de cédula
        ((AbstractDocument) fC.getDocument()).setDocumentFilter(new Validaciones.CedulaDocumentFilter());

        // Selector de cargos emergente
        String[] cargos = {"Gerente", "Contador", "Supervisor", "Secretaria", "Vendedor", "Técnico", "Cajero", "Conserje", "Chofer", "Seguridad", "Otro..."}; 
        fG.setEditable(false); 
        fG.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fG.addMouseListener(new MouseAdapter() { 
            public void mousePressed(MouseEvent e) { 
                JPopupMenu pm = new JPopupMenu(); 
                for (String c : cargos) { 
                    JMenuItem it = new JMenuItem(c); it.setFont(fG.getFont());
                    it.addActionListener(x -> { 
                        String v = c.equals("Otro...") ? ask("Escriba el cargo:") : c; 
                        if (v != null && !v.trim().isEmpty()) fG.setText(v.trim()); 
                    }); 
                    pm.add(it); 
                } 
                pm.show(fG, 0, fG.getHeight()); 
            } 
        });

        fS.addActionListener(e -> registrar()); 
        fB.addActionListener(e -> buscar());

        LocalDate hoy = LocalDate.now(), pago = hoy.getDayOfMonth() <= 15 ? hoy.withDayOfMonth(15) : hoy.withDayOfMonth(hoy.lengthOfMonth());
        
        // Acciones de los botones superiores
        hot(495, 30, 270, 62, () -> msg("Administración", "Empleados: " + nominaService.getEmpleados().size() + "\nTotal neto: RD$ " + String.format(Locale.US, "%,.2f", nominaService.calcularTotalNominaGeneral())));
        hot(826, 30, 200, 62, () -> msg("Calendario", "Hoy: " + hoy + "\nPróximo pago: " + pago));
        hot(1073, 30, 150, 62, () -> msg("Soporte", "Teléfono: +1 (809) 555-0100\nCorreo: soporte@laskt.com"));
        hot(1645, 35, 62, 50, this::buscar);
        hot(1718, 28, 74, 66, () -> msg("Notificaciones", nominaService.getEmpleados().isEmpty() ? "No hay notificaciones." : "Último pago próximo: " + pago));
        hot(1798, 15, 92, 90, () -> msg("Perfil", "Sesión: " + sesion));

        hot(1266, 157, 270, 84, this::iniciarSesion); 
        hot(1592, 157, 270, 84, this::registrar);

        // Bloques interactivos de extras
        hot(441, 283, 450, 259, () -> { double v = num("Horas extra trabajadas:"); if (v >= 0) { horas = v; msg("Horas extra", v + " hora(s) aplicadas al próximo registro."); } });
        hot(931, 283, 450, 259, () -> { double v = num("Monto del bono (RD$):"); if (v >= 0) { bonos = v; msg("Bonos", "RD$ " + v + " aplicados al próximo registro."); } });
        hot(1421, 283, 450, 259, () -> seguro = JOptionPane.showConfirmDialog(root, "¿Aplicar seguro (SFS 3.04% + AFP 2.87%)?", "Seguro", JOptionPane.YES_NO_OPTION) == 0);

        // Menú lateral
        Runnable[] acc = {
            this::limpiar,
            () -> msg("Nosotros", "LASKT - Sistema de Nómina UASD.\nEmpleados registrados: " + nominaService.getEmpleados().size()),
            () -> msg("Contactos", "soporte@laskt.com | RRHH"),
            this::calculadora, 
            () -> abrirVentanaLista(""),
            () -> msg("Configuración", "Base de datos local CSV sincronizada.\nSFS 3.04% + AFP 2.87% | Hora extra +35%")
        };
        for (int i = 0; i < 6; i++) { Runnable r = acc[i]; hot(20, 145 + 140 * i, 390, 110, r); }

        return root;
    }

    // ---------- Acciones del Negocio conectadas al Service ----------
    private void limpiar() { 
        for (JTextField f : new JTextField[]{fN, fC, fG, fS}) f.setText(""); 
        horas = bonos = 0; seguro = true; fN.requestFocusInWindow(); 
    }

    private void registrar() {
        String n = fN.getText().trim().replace(';', ','), 
               c = fC.getText().replaceAll("\\D", ""), 
               g = fG.getText().trim().replace(';', ','), 
               s = fS.getText().replace(",", "").trim();
        try { 
            if (n.length() < 3 || g.isEmpty() || c.length() != 11 || Double.parseDouble(s) <= 0) throw new Exception(); 
        } catch (Exception x) { 
            msg("Atención", "Complete bien los campos: nombre, cédula (11 dígitos), cargo y salario."); return; 
        }
        
        for (Empleado e : nominaService.getEmpleados()) {
            if (e.getCedula().equals(c)) { 
                msg("Atención", "Esa cédula ya está registrada."); return; 
            }
        }
        
        Empleado emp = new Empleado(n, c, g, Double.parseDouble(s), horas, bonos, seguro);
        nominaService.agregarEmpleado(emp);
        
        Nomina nom = new Nomina(emp);
        String resumen = String.format(Locale.US, "Nombre: %s%nCédula: %s%nCargo: %s%nSalario: RD$ %,.2f%nHoras extra: RD$ %,.2f%nBonos: RD$ %,.2f%nDescuento SFS+AFP: RD$ %,.2f%nNETO A PAGAR: RD$ %,.2f",
                emp.getNombre(), ced(emp.getCedula()), emp.getCargo(), emp.getSalarioBase(), nom.calcularPagoHorasExtras(), emp.getBonos(), nom.calcularDeducciones(), nom.calcularSalarioNeto());
        
        msg("Empleado registrado y guardado", resumen); 
        limpiar();
    }

    private void iniciarSesion() {
        String q = ask("Escriba su número de cédula o su nombre:");
        if (q == null || q.isEmpty()) return; 
        String d = q.replaceAll("\\D", ""), nq = Validaciones.normalizarTexto(q); 
        StringBuilder sb = new StringBuilder();
        
        for (Empleado emp : nominaService.getEmpleados()) {
            if (!d.isEmpty() ? emp.getCedula().equals(d) : Validaciones.normalizarTexto(emp.getNombre()).contains(nq)) { 
                sesion = emp.getNombre(); 
                Nomina nom = new Nomina(emp);
                sb.append(String.format(Locale.US, "Nombre: %s%nCédula: %s%nCargo: %s%nSalario: RD$ %,.2f%nNETO A PAGAR: RD$ %,.2f%n%n",
                        emp.getNombre(), ced(emp.getCedula()), emp.getCargo(), emp.getSalarioBase(), nom.calcularSalarioNeto())); 
            }
        }
        msg(sb.length() > 0 ? "Bienvenido(a)" : "No encontrado", sb.length() > 0 ? sb.toString() : "No hay ningún empleado con esa cédula o nombre.");
    }

    private void abrirVentanaLista(String filtro) {
        EmpleadosFrame dialog = new EmpleadosFrame(this, nominaService);
        dialog.setVisible(true);
    }

    private void buscar() {
        String f = fB.getText().trim(); 
        if (f.isEmpty()) msg("Buscar", "Escriba lo que desea buscar: nombre, cédula, cargo o salario."); 
        else abrirVentanaLista(f);
    }

    private void calculadora() {
        String s = ask("Operación (ej. 1500*12 o 45000+3200):"); if (s == null) return;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(-?[\\d.]+)\\s*([-+*/x])\\s*(-?[\\d.]+)").matcher(s);
        if (!m.matches()) { msg("Calculadora", "Use el formato: número operador número"); return; }
        double a = Double.parseDouble(m.group(1)), b = Double.parseDouble(m.group(3)); 
        String p = m.group(2); 
        msg("Resultado", s + " = " + (p.equals("+") ? a + b : p.equals("-") ? a - b : p.equals("/") ? a / b : a * b));
    }
}

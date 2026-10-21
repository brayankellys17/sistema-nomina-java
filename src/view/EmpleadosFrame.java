package view;

import model.Empleado;
import model.Nomina;
import service.NominaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Locale;

public class EmpleadosFrame extends JDialog {
    private NominaService nominaService;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public EmpleadosFrame(JFrame parent, NominaService service) {
        super(parent, "Empleados Registrados y Nómina", true);
        this.nominaService = service;
        setSize(800, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        cargarDatosTabla();
    }

    private void initComponents() {
        String[] columnas = {"Nombre", "Cédula", "Cargo", "Salario Base", "Neto a Pagar"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        JButton btnCerrar = new JButton("Cerrar");

        panelInferior.add(btnEliminar);
        panelInferior.add(btnCerrar);
        add(panelInferior, BorderLayout.SOUTH);

        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tabla.getSelectedRow();
            if (filaSeleccionada < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un empleado de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cedula = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
            int confirmar = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este empleado?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            
            if (confirmar == JOptionPane.YES_OPTION) {
                nominaService.eliminarEmpleadoPorCedula(cedula);
                cargarDatosTabla();
                JOptionPane.showMessageDialog(this, "Empleado eliminado correctamente.");
            }
        });

        btnCerrar.addActionListener(e -> dispose());
    }

    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        for (Empleado e : nominaService.getEmpleados()) {
            Nomina nom = new Nomina(e);
            modeloTabla.addRow(new Object[]{
                e.getNombre(),
                e.getCedula(),
                e.getCargo(),
                String.format(Locale.US, "%,.2f", e.getSalarioBase()),
                String.format(Locale.US, "%,.2f", nom.calcularSalarioNeto())
            });
        }
    }
}

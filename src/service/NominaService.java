package service;

import model.Empleado;
import model.Nomina;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class NominaService {
    private static final Path DB = Paths.get("empleados.csv");
    private List<Empleado> empleados;

    public NominaService() {
        this.empleados = new ArrayList<>();
        cargarDesdeArchivo();
    }

    public void agregarEmpleado(Empleado emp) {
        empleados.add(emp);
        guardarEnArchivo();
    }

    public void eliminarEmpleadoPorCedula(String cedula) {
        empleados.removeIf(e -> e.getCedula().equals(cedula));
        guardarEnArchivo();
    }

    public List<Empleado> getEmpleados() {
        return empleados;
    }

    public void guardarEnArchivo() {
        try {
            List<String> lineas = new ArrayList<>();
            for (Empleado e : empleados) {
                lineas.add(e.aTextoCSV());
            }
            Files.write(DB, lineas, StandardCharsets.UTF_8);
        } catch (IOException x) {
            System.err.println("Error al guardar la base de datos: " + x.getMessage());
        }
    }

    public void cargarDesdeArchivo() {
        try {
            if (Files.exists(DB)) {
                empleados.clear();
                for (String s : Files.readAllLines(DB, StandardCharsets.UTF_8)) {
                    String[] p = s.split(";", -1);
                    if (p.length == 7) {
                        Empleado e = new Empleado(
                            p[0], p[1], p[2],
                            Double.parseDouble(p[3]),
                            Double.parseDouble(p[4]),
                            Double.parseDouble(p[5]),
                            Boolean.parseBoolean(p[6])
                        );
                        empleados.add(e);
                    }
                }
            }
        } catch (IOException x) {
            System.err.println("Error al cargar la base de datos.");
        }
    }

    public double calcularTotalNominaGeneral() {
        double total = 0;
        for (Empleado e : empleados) {
            Nomina nom = new Nomina(e);
            total += nom.calcularSalarioNeto();
        }
        return total;
    }
}

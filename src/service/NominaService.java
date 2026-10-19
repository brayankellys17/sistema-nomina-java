package service;

import model.Empleado;
import model.Nomina;
import java.util.ArrayList;
import java.util.List;

public class NominaService {
    private List<Nomina> listaNominas;

    public NominaService() {
        this.listaNominas = new ArrayList<>();
    }

    // Agregar una nómina (que ya contiene al empleado y sus bonificaciones/deducciones)
    public void agregarNomina(Nomina nomina) {
        listaNominas.add(nomina);
    }

    // Obtener la lista completa de nóminas procesadas
    public List<Nomina> obtenerNominas() {
        return listaNominas;
    }

    // Buscar una nómina por el ID del empleado
    public Nomina buscarNominaPorIdEmpleado(int idEmpleado) {
        for (Nomina n : listaNominas) {
            if (n.getEmpleado().getId() == idEmpleado) {
                return n;
            }
        }
        return null; // Retorna nulo si no lo encuentra
    }

    // Calcular las deducciones de ley estándar (TSS: AFP 2.87% + SFS 3.04%) sobre un salario bruto dado
    public double calcularTSS(double salarioBruto) {
        double afp = salarioBruto * 0.0287;
        double sfs = salarioBruto * 0.0304;
        return afp + sfs;
    }

    // Cálculo estimado de la retención del ISR mensual según las escalas de la DGII
    public double calcularISR(double salarioBruto) {
        double tss = calcularTSS(salarioBruto);
        double sueldoImponibleMensual = salarioBruto - tss;
        double sueldoAnual = sueldoImponibleMensual * 12;
        
        double impuestoAnual = 0;
        // Escalas estándar de referencia DGII
        if (sueldoAnual > 416220.01 && sueldoAnual <= 624329.00) {
            impuestoAnual = (sueldoAnual - 416220.01) * 0.15;
        } else if (sueldoAnual > 624329.00 && sueldoAnual <= 867123.00) {
            impuestoAnual = 31216.00 + ((sueldoAnual - 624329.00) * 0.20);
        } else if (sueldoAnual > 867123.00) {
            impuestoAnual = 79776.00 + ((sueldoAnual - 867123.00) * 0.25);
        }
        
        return impuestoAnual / 12; // Retención mensual proporcional
    }
}

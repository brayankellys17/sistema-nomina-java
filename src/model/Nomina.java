package model;

public class Nomina {
    private Empleado empleado;

    public Nomina(Empleado empleado) {
        this.empleado = empleado;
    }

    public double calcularPagoHorasExtras() {
        if (empleado.getHorasExtras() == 0) return 0;
        // 190.64 horas laborables al mes con recargo del 35% (1.35)
        double valorHora = empleado.getSalarioBase() / 190.64;
        return valorHora * 1.35 * empleado.getHorasExtras();
    }

    public double calcularSalarioBruto() {
        return empleado.getSalarioBase() + calcularPagoHorasExtras() + empleado.getBonos();
    }

    public double calcularDeducciones() {
        // TSS (SFS 3.04% + AFP 2.87% = 5.91%)
        return empleado.isSeguro() ? calcularSalarioBruto() * 0.0591 : 0;
    }

    public double calcularSalarioNeto() {
        return calcularSalarioBruto() - calcularDeducciones();
    }

    public Empleado getEmpleado() { return empleado; }
}

package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
        "jdbc:sqlserver://nominas-empleados.database.windows.net:1433;"
        + "databaseName=BD_Nomina_Empleados;"
        + "encrypt=true;"
        + "trustServerCertificate=false;";

    private static final String USUARIO = "nominajava";
    private static final String CONTRASENA = "Java12345678";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(
            URL, USUARIO, CONTRASENA
        );
    }

    public static void main(String[] args) {
        String sql = "SELECT Id_Empleado, Nombre, Cargo, Salario_Base FROM Empleados";

        try (Connection con = conectar();
             java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
    System.out.println("ID: " + rs.getInt("Id_Empleado"));
    System.out.println("Nombre: " + rs.getString("Nombre"));
    System.out.println("Cargo: " + rs.getString("Cargo"));
    System.out.println("Salario: " + rs.getBigDecimal("Salario_Base"));
    System.out.println("--------------------");
}

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

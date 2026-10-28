public class PruebaDriver {
    public static void main(String[] args) {
        try {
            Class.forName(
                "com.microsoft.sqlserver.jdbc.SQLServerDriver"
            );

            System.out.println(
                "Controlador JDBC cargado correctamente."
            );

        } catch (ClassNotFoundException e) {
            System.out.println(
                "No se encontró el controlador JDBC."
            );

            e.printStackTrace();
        }
    }
}
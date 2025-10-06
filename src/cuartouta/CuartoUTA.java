import cuartouta.Conexion;
import java.sql.Connection;
import java.sql.SQLException;

public class CuartoUTA {
    public static void main(String[] args) {
        try {
            Conexion conexion = new Conexion();
            Connection conn = conexion.conectar();
            System.out.println("✅ Conexión establecida con éxito.");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Error SQL: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error general: " + e.getMessage());
        }
    }
}

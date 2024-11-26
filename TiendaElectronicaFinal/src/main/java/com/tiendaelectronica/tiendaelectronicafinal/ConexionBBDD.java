
package com.tiendaelectronica.tiendaelectronicafinal;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBBDD {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/tienda_online";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = ""; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

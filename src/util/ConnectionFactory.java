package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

	
	//Classe de conexão do banco.
	
    private static final String URL = "";
    private static final String USER = "";
    private static final String PASSWORD = ""; 
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER_CLASS);
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ERRO: Driver do MySQL não encontrado no classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("ERRO: Falha ao conectar ao banco de dados.", e);
        }
    }
}

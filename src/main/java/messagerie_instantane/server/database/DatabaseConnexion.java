package messagerie_instantane.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnexion {
    public static Connection connection;
    private String database_url;

    public DatabaseConnexion(){
        database_url = "jdbc:sqlite:./data/messagerie.db";
        try{
            connection = DriverManager.getConnection(database_url);
        } catch(SQLException e){
            DbInit.init();
        }
    }

    public static Connection getConn(){
        return connection;
    }
}

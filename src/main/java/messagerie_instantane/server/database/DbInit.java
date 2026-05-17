package messagerie_instantane.server.database;

import java.sql.SQLException;

public class DbInit {
    

    public static boolean init(){
        try{
            return true;
        } catch(SQLException e){
            System.out.println("erreur lors de l'initialisation de la db :" + e.getMessage());
            return false;
        }
    }
}

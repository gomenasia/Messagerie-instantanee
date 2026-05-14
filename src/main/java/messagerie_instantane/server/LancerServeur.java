package messagerie_instantane.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class LancerServeur {

    public static final int PORT = 8090;
    public static final String NOM = "messagerie";

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(PORT);
            ServeurMessagerie serveur = new ServeurMessagerie();
            Naming.bind("//localhost:" + PORT + "/" + NOM, serveur);
            System.out.println("✅ Serveur démarré sur le port " + PORT);
            System.out.println("   Adresse : //localhost:" + PORT + "/" + NOM);
        } catch (Exception e) {
            System.err.println("❌ Erreur au démarrage du serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package messagerie_instantane.client;

import messagerie_instantane.commun.InterfaceAffichageClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implémentation du callback RMI côté client.
 * Le serveur appelle affiche() pour pousser les messages reçus.
 * Le Consumer<String> reçu à la construction transmet le message au contrôleur JavaFX.
 */
public class ClientMessagerie extends UnicastRemoteObject implements InterfaceAffichageClient {

    private final java.util.function.Consumer<String> onMessage;

    public ClientMessagerie(java.util.function.Consumer<String> onMessage) throws RemoteException {
        this.onMessage = onMessage;
    }

    /**
     * Appelé par le serveur (thread RMI) quand un message arrive.
     * On délègue au Consumer qui appellera Platform.runLater() dans le contrôleur.
     */
    @Override
    public void affiche(String message) throws RemoteException {
        onMessage.accept(message);
    }
}

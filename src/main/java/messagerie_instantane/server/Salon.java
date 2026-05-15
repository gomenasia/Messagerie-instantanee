package messagerie_instantane.server;

import messagerie_instantane.commun.InterfaceAffichageClient;
import messagerie_instantane.commun.InterfaceSujetDiscussion;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Salon extends UnicastRemoteObject implements InterfaceSujetDiscussion {

    private final String titre;
    private final List<InterfaceAffichageClient> abonnes = new ArrayList<>();

    public Salon(String titre) throws RemoteException {
        this.titre = titre;
    }

    public String getTitre() {
        return titre;
    }

    /**
     * Gere l'ajout d'un client a un groupe de discussion
     */
    @Override
    public synchronized void inscription(InterfaceAffichageClient c) throws RemoteException {
        if (!abonnes.contains(c)) {
            abonnes.add(c);
        }
    }

    /**
     * Gere la supression d'un client a un groupe de discussion
     */
    @Override
    public synchronized void desInscription(InterfaceAffichageClient c) throws RemoteException {
        abonnes.remove(c);
    }

    /**
     * transmet un message a tout les client inscrit dans le groupe de discussion
     */
    @Override
    public synchronized void diffuse(String message) throws RemoteException {
        List<InterfaceAffichageClient> aSupprimer = new ArrayList<>();
        for (InterfaceAffichageClient c : abonnes) {
            try {
                c.affiche(message);
            } catch (RemoteException e) {
                // client déconnecté, on le retire
                aSupprimer.add(c);
            }
        }
        abonnes.removeAll(aSupprimer);
    }
}

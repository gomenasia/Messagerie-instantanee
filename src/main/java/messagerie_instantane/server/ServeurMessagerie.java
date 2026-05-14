package messagerie_instantane.server;

import messagerie_instantane.commun.InterfaceServeurForum;
import messagerie_instantane.commun.InterfaceSujetDiscussion;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServeurMessagerie extends UnicastRemoteObject implements InterfaceServeurForum {

    private final Map<String, Salon> salons = new HashMap<>();

    public ServeurMessagerie() throws RemoteException {
        // salons par défaut
        salons.put("général",   new Salon("général"));
        salons.put("blabla",    new Salon("blabla"));
        salons.put("entraide",  new Salon("entraide"));
    }

    @Override
    public synchronized InterfaceSujetDiscussion obtientSujet(String titre) throws RemoteException {
        // crée le salon à la volée s'il n'existe pas
        salons.putIfAbsent(titre, new Salon(titre));
        return salons.get(titre);
    }

    /** Retourne la liste des titres de salons disponibles. */
    public synchronized List<String> listerSalons() {
        return new ArrayList<>(salons.keySet());
    }
}

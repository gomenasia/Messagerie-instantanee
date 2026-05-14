package messagerie_instantane.client.models;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Représente un message affiché dans la vue chat.
 * Le champ "moi" indique si c'est le message de l'utilisateur local (bulle droite).
 */
public class MessageItem {

    private final String expediteur;
    private final String contenu;
    private final String heure;
    private final boolean moi;

    public MessageItem(String expediteur, String contenu, boolean moi) {
        this.expediteur = expediteur;
        this.contenu    = contenu;
        this.moi        = moi;
        this.heure      = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getExpediteur() { return expediteur; }
    public String getContenu()    { return contenu; }
    public String getHeure()      { return heure; }
    public boolean isMoi()        { return moi; }
}

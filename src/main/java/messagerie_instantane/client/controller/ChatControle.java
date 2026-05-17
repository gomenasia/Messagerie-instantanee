package messagerie_instantane.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import messagerie_instantane.client.ClientMessagerie;
import messagerie_instantane.commun.InterfaceServeurForum;
import messagerie_instantane.commun.InterfaceSujetDiscussion;

import java.rmi.RemoteException;

public class ChatControle {

    // ── FXML ─────────────────────────────────────────────────────────
    @FXML private ListView<String>   salonList;
    @FXML private VBox               messagesBox;
    @FXML private ScrollPane         scrollPane;
    @FXML private TextField          inputField;
    @FXML private Label              salonLabel;

    // ── État ─────────────────────────────────────────────────────────
    private InterfaceServeurForum    serveur;
    private InterfaceSujetDiscussion salonCourant;
    private ClientMessagerie         clientRMI;
    private String                   pseudo;

    // ── Initialisation appelée par LoginChat via NavigationManager ────
    public void init(InterfaceServeurForum serveur, String pseudo) {
        this.serveur = serveur;
        this.pseudo  = pseudo;

        try {
            clientRMI = new ClientMessagerie(msg ->
                Platform.runLater(() -> afficherBulle(msg, false))
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        chargerSalons();

        salonList.getSelectionModel().selectedItemProperty().addListener(
            (obs, ancien, nouveau) -> {
                if (nouveau != null) rejoindre(nouveau);
            }
        );

        if (!salonList.getItems().isEmpty()) {
            salonList.getSelectionModel().selectFirst();
        }
    }

    // ── Chargement des salons ─────────────────────────────────────────
    private void chargerSalons() {
        try {
            for (String titre : new String[]{"général", "blabla", "entraide"}) {
                salonList.getItems().add(titre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Rejoindre un salon ────────────────────────────────────────────
    private void rejoindre(String titre) {
        try {
            if (salonCourant != null && clientRMI != null) {
                salonCourant.desInscription(clientRMI);
            }
            salonCourant = serveur.obtientSujet(titre);
            salonCourant.inscription(clientRMI);
            salonLabel.setText("# " + titre);
            messagesBox.getChildren().clear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // ── Envoi d'un message ────────────────────────────────────────────
    @FXML
    private void onEnvoi() {
        String texte = inputField.getText().trim();
        if (texte.isEmpty() || salonCourant == null) return;
        try {
            salonCourant.diffuse("[" + pseudo + "] " + texte);
            inputField.clear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // ── Affichage d'une bulle ─────────────────────────────────────────
    private void afficherBulle(String messageComplet, boolean force) {
        boolean estMoi = messageComplet.startsWith("[" + pseudo + "]");

        String expediteur;
        String contenu;
        if (messageComplet.matches("\\[.*\\] .*")) {
            expediteur = messageComplet.substring(1, messageComplet.indexOf(']'));
            contenu    = messageComplet.substring(messageComplet.indexOf(']') + 2);
        } else {
            expediteur = "?";
            contenu    = messageComplet;
        }

        HBox ligne = new HBox();
        ligne.setAlignment(estMoi ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        ligne.setPrefWidth(Double.MAX_VALUE);

        VBox bulle = new VBox(3);
        bulle.getStyleClass().add(estMoi ? "bulle-moi" : "bulle-autre");
        bulle.setMaxWidth(320);

        Label auteurLabel = new Label(expediteur);
        auteurLabel.getStyleClass().add("bulle-auteur");

        Label contenuLabel = new Label(contenu);
        contenuLabel.setWrapText(true);
        contenuLabel.getStyleClass().add("bulle-texte");

        bulle.getChildren().addAll(auteurLabel, contenuLabel);
        ligne.getChildren().add(bulle);
        messagesBox.getChildren().add(ligne);

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    public void ouvrir(InterfaceServeurForum server, String pseudo){
        
    }
}

package messagerie_instantane.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messagerie_instantane.client.ClientMessagerie;
import messagerie_instantane.commun.InterfaceServeurForum;
import messagerie_instantane.commun.InterfaceSujetDiscussion;

import java.rmi.RemoteException;

public class ChatControle {

    // ── FXML ────────────────────────────────────────────────────────
    @FXML private ListView<String> salonList;
    @FXML private VBox             messagesBox;
    @FXML private ScrollPane       scrollPane;
    @FXML private TextField        inputField;
    @FXML private Label            salonLabel;

    // ── État ─────────────────────────────────────────────────────────
    private InterfaceServeurForum   serveur;
    private InterfaceSujetDiscussion salonCourant;
    private ClientMessagerie        clientRMI;
    private String                  pseudo;

    // ── Initialisation appelée par LoginChat ─────────────────────────
    public void init(InterfaceServeurForum serveur, String pseudo) {
        this.serveur = serveur;
        this.pseudo  = pseudo;

        // Création du callback RMI : transmet les messages au fil JavaFX
        try {
            clientRMI = new ClientMessagerie(msg ->
                Platform.runLater(() -> afficherBulle(msg, false))
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Charger la liste des salons
        chargerSalons();

        // Sélection automatique du premier salon
        salonList.getSelectionModel().selectedItemProperty().addListener(
            (obs, ancien, nouveau) -> {
                if (nouveau != null) rejoindre(nouveau);
            }
        );
        if (!salonList.getItems().isEmpty()) {
            salonList.getSelectionModel().selectFirst();
        }
    }

    private void chargerSalons() {
        try {
            // On tente d'obtenir les salons par défaut connus
            for (String titre : new String[]{"général", "blabla", "entraide"}) {
                salonList.getItems().add(titre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rejoindre(String titre) {
        try {
            // Se désinscrire de l'ancien salon
            if (salonCourant != null && clientRMI != null) {
                salonCourant.desInscription(clientRMI);
            }
            // S'inscrire au nouveau
            salonCourant = serveur.obtientSujet(titre);
            salonCourant.inscription(clientRMI);
            salonLabel.setText("# " + titre);
            messagesBox.getChildren().clear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // ── Envoi d'un message ───────────────────────────────────────────
    @FXML
    private void onEnvoi() {
        String texte = inputField.getText().trim();
        if (texte.isEmpty() || salonCourant == null) return;
        try {
            // On préfixe le message avec le pseudo
            salonCourant.diffuse("[" + pseudo + "] " + texte);
            inputField.clear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // ── Affichage d'une bulle ────────────────────────────────────────
    private void afficherBulle(String messageComplet, boolean force) {
        // Détecter si c'est notre propre message
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

        // Auto-scroll
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    // ── Ouverture de la fenêtre Chat ─────────────────────────────────
    public static void ouvrir(InterfaceServeurForum serveur, String pseudo) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            ChatControle.class.getResource("/fxml/ChatView.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Messagerie – " + pseudo);
        Scene scene = new Scene(loader.load(), 800, 550);
        scene.getStylesheets().add(
            ChatControle.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        ChatControle ctrl = loader.getController();
        ctrl.init(serveur, pseudo);
    }
}

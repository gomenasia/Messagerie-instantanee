package messagerie_instantane.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import messagerie_instantane.NavigationManager;
import messagerie_instantane.commun.InterfaceServeurForum;

import java.rmi.Naming;

public class LoginChat {

    @FXML private TextField pseudoField;
    @FXML private TextField serverField;
    @FXML private Label     errorLabel;

    @FXML
    private void onConnect() {
        String pseudo = pseudoField.getText().trim();
        String server = serverField.getText().trim();

        if (pseudo.isEmpty() || server.isEmpty()) {
            errorLabel.setText("Tous les champs sont requis.");
            return;
        }

        try {
            InterfaceServeurForum serveur = (InterfaceServeurForum)
                Naming.lookup("//" + server + ":8090/messagerie");

            // ── Charge ChatView.fxml et récupère son controller ───────
            // naviguerVers() retourne le FXMLLoader, ce qui permet
            // d'appeler init() sur le controller juste après
            ChatControle ctrl = NavigationManager.getInstance()
                .naviguerVers("/fxml/ChatView.fxml")
                .getController();

            // ── Passe le serveur et le pseudo au controller Chat ──────
            ctrl.init(serveur, pseudo);

        } catch (Exception e) {
            errorLabel.setText("Connexion impossible : " + e.getMessage());
        }
    }
}

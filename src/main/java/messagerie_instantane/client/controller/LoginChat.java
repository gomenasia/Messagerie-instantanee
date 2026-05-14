package messagerie_instantane.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

            // Ouvrir la fenêtre Chat
            ChatControle.ouvrir(serveur, pseudo);

            // Fermer la fenêtre Login
            ((Stage) pseudoField.getScene().getWindow()).close();

        } catch (Exception e) {
            errorLabel.setText("Connexion impossible : " + e.getMessage());
        }
    }
}

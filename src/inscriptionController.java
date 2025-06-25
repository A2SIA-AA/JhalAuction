import Communication.Inscription;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la gestion de l'interface d'inscription.
 * Permet de créer un utilisateur et de rediriger vers le menu principal après l'inscription.
 */
public class inscriptionController implements Initializable {

    /**
     * Champ de texte pour le nom d'utilisateur à l'inscription.
     */
    @FXML
    private TextField usernameInscription;

    /**
     * Champ de texte pour le mot de passe à l'inscription.
     */
    @FXML
    private PasswordField passwordInscription;

    /**
     * Champ de texte pour l'adresse IP du serveur.
     */
    @FXML
    private TextField adresseIP;

    /**
     * Gère l'inscription de l'utilisateur et redirige vers le menu principal si réussie.
     *
     * @param event L'événement déclenché lors du clic sur le bouton d'inscription.
     * @throws Exception Si une erreur survient lors du changement de scène.
     */
    @FXML
    private void toMenu(ActionEvent event) throws Exception {
        // Récupérer les informations de l'inscription
        String username = usernameInscription.getText();
        String password = passwordInscription.getText();
        String adrServeur = adresseIP.getText();

        if (username.isEmpty() || password.isEmpty() || adrServeur.isEmpty()) {
            fctUtile.showErrorDialog("Inscription échouée", "Tous les champs doivent être remplis.", null);
            return;
        }


        // Créer un utilisateur
        Utilisateur user = new Utilisateur(username, password, adrServeur);
        Inscription response = user.inscription();

        if (response != null && response.isSucces()) {
            // Si l'inscription réussit, charger menuCo.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menuCo.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur associé et passer l'utilisateur
            menuCoController menuCo = loader.getController();

            // Changer de scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            // Afficher une erreur si l'inscription échoue
            fctUtile.showErrorDialog("Inscription échouée", "Utilisateur déjà existant", null);
        }
    }

    /**
     * Redirige directement vers le menu principal sans inscription.
     *
     * @param event L'événement déclenché lors du clic sur le bouton pour ignorer l'inscription.
     * @throws Exception Si une erreur survient lors du changement de scène.
     */
    @FXML
    private void toMenu1(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menuCo.fxml"));
        Parent root = loader.load();

        // Obtenir le contrôleur associé et passer l'utilisateur
        menuCoController menuCo = loader.getController();

        // Changer de scène
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Initialise les composants de la scène.
     * Permet de configurer des éléments de l'interface au chargement si nécessaire.
     *
     * @param url Emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine, ou {@code null}.
     * @param rb  Ressources utilisées pour localiser les objets racines, ou {@code null}.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Récupérer l'utilisateur connecté depuis Main (actuellement inutilisé).
        Utilisateur utilisateur = Main.getUtilisateurConnecte();
    }
}

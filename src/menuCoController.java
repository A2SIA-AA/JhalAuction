import Communication.Connexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.net.UnknownHostException;
import java.net.InetAddress;

/**
 * Contrôleur pour la gestion du menu de connexion.
 * Permet de connecter un utilisateur ou de le rediriger vers l'inscription.
 */
public class menuCoController implements Initializable {

    /**
     * Champ de texte pour le nom d'utilisateur.
     */
    @FXML
    private TextField usernameTextField;

    /**
     * Champ de texte pour le mot de passe.
     */
    @FXML
    private PasswordField passwordTextField;

    /**
     * Champ de texte pour l'adresse IP du serveur.
     */
    @FXML
    private TextField adresse_serveur;

    /**
     * Bouton pour initier la connexion.
     */
    @FXML
    private Button loginButton;

    /**
     * Bouton pour accéder à l'inscription.
     */
    @FXML
    private Button inscriptionButton;

    /**
     * Label pour afficher les messages d'erreur.
     */
    @FXML
    private Label errorLabel;

    /**
     * Gère la connexion d'un utilisateur et redirige vers la page des choix client.
     *
     * @param event L'événement déclenché lors du clic sur le bouton de connexion.
     */
    @FXML
    public void tochoix_Client(ActionEvent event) {
        try {
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();
            String adresseIP = adresse_serveur.getText();

            // Vérification des champs obligatoires
            if (username.isEmpty() || password.isEmpty() || adresseIP.isEmpty()) {
                fctUtile.showErrorDialog("Connexion échouée", "Tous les champs doivent être remplis.", null);
                return;
            }

            // Création de l'utilisateur en fonction des identifiants
            Utilisateur user;
            if (username.equals("commissaire") && password.equals("c")) {
                user = new Commissaire(username, password, adresseIP);
            } else {
                user = new Utilisateur(username, password, adresseIP);
            }

            // Définit l'utilisateur connecté dans la classe principale
            Main.setUtilisateurConnecte(user);

            // Vérifie la connexion avec le serveur
            Connexion rep = user.connexion();
            if (rep != null && rep.isSucces()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("choix_Client.fxml"));
                Parent root = loader.load();

                // Passe l'utilisateur au contrôleur suivant
                choix_ClientController choixClient = loader.getController();

                // Change de scène
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

                // Récupère tous les produits disponibles pour l'utilisateur
                user.recupererTousLesProduits();
            } else {
                // Affiche un message si la connexion échoue
                fctUtile.showErrorDialog("Connexion échouée", "Veuillez vérifier vos identifiants.", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fctUtile.showErrorDialog("Erreur", "Impossible de changer de scène", e.getMessage());
        }
    }

    /**
     * Redirige l'utilisateur vers la page d'inscription.
     *
     * @param event L'événement déclenché lors du clic sur le bouton d'inscription.
     * @throws Exception Si une erreur survient lors du changement de scène.
     */
    @FXML
    private void toInscription(ActionEvent event) throws Exception {
        fctUtile.changeToScene(getClass(), event, "inscription.fxml");
    }

    /**
     * Initialise les composants de la scène. Permet d'effectuer des préparations si nécessaire.
     *
     * @param url Emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine, ou {@code null}.
     * @param rb  Ressources utilisées pour localiser les objets racines, ou {@code null}.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialisation si nécessaire
    }

    /**
     * Lance un serveur local pour gérer les enchères.
     * Affiche l'adresse IP du serveur et s'assure de l'arrêt correct lors de la fermeture.
     */
    public static void lancerServeur() {
        ServeurEnchere server = new ServeurEnchere();
        try {
            System.out.println("Serveur lancé à l'adresse : " + InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // Ajoute un hook pour arrêter le serveur proprement
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Arrêt du serveur...");
            server.arreterServeur();
        }));
        server.demarrerServeur();
    }
}

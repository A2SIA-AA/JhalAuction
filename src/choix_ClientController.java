import Communication.Historique;
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
 * Contrôleur pour la gestion de l'interface de choix des clients.
 * Cette classe gère les interactions entre les boutons de l'interface et la logique métier associée.
 */
public class choix_ClientController implements Initializable {

    /**
     * Bouton pour accéder à la page de réclamation (visible uniquement pour les clients non commissaires).
     */
    @FXML
    private Button reclamer;

    /**
     * Bouton pour ajouter des offres (visible uniquement pour les commissaires).
     */
    @FXML
    private Button AddOffres;

    /**
     * Bouton pour lancer une enchère (visible uniquement pour les commissaires).
     */
    @FXML
    private Button debutEnchere;

    /**
     * Bouton pour voir les achats (visible uniquement pour les clients).
     */
    @FXML
    private Button voirAchats;

    /**
     * Redirige vers la scène de la réclamation.
     *
     * @param event L'événement déclenché lors du clic sur le bouton.
     * @throws Exception Si une erreur survient lors du chargement de la scène.
     */
    @FXML
    public void toReclamation(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Reclamation.fxml"));
        Parent root = loader.load();

        ReclamationController reclamer = loader.getController();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Envoie une demande pour récupérer l'historique de l'utilisateur connecté.
     */
    @FXML
    public void FaireDemandeHistorique() {
        Utilisateur user = Main.getUtilisateurConnecte();
        try {
            Historique historique = user.demanderHistorique();

            if (historique == null) {
                fctUtile.showInfoDialog("Info", "Aucun historique trouvé pour cet utilisateur.", "");
            } else {
                fctUtile.showInfoDialog("Info", "Voici votre historique: " + historique, "");
            }
        } catch (Exception e) {
            fctUtile.showInfoDialog("Erreur", "Erreur lors de la récupération de l'historique: ", e.getMessage());
        }
    }

    /**
     * Redirige vers la scène des offres disponibles.
     *
     * @param event L'événement déclenché lors du clic sur le bouton.
     * @throws Exception Si une erreur survient lors du chargement de la scène.
     */
    @FXML
    private void toOffresDispo(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("OffresDispo.fxml"));
        Parent root = loader.load();

        OffresDispoController dispo = loader.getController();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Redirige vers la scène pour ajouter une offre.
     *
     * @param event L'événement déclenché lors du clic sur le bouton.
     * @throws Exception Si une erreur survient lors du chargement de la scène.
     */
    @FXML
    private void toAddOffre(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Vendre.fxml"));
        Parent root = loader.load();

        VendreController vendreController = loader.getController();
        vendreController.setCommissaire((Commissaire) Main.getUtilisateurConnecte());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Déconnecte l'utilisateur et redirige vers la scène de connexion.
     *
     * @param event L'événement déclenché lors du clic sur le bouton.
     * @throws Exception Si une erreur survient lors du changement de scène.
     */
    @FXML
    private void Deconnexion(ActionEvent event) throws Exception {
        Main.setUtilisateurConnecte(null);
        fctUtile.changeToScene(getClass(), event, "menuCo.fxml");
    }

    /**
     * Lance une enchère pour le commissaire actuellement connecté.
     */
    @FXML
    private void LancerEnchere() {
        Commissaire user = (Commissaire) Main.getUtilisateurConnecte();
        user.lancerEnchere();
    }

    /**
     * Initialise les composants de la scène en fonction du rôle de l'utilisateur.
     * Affiche ou masque les boutons selon que l'utilisateur est un commissaire ou un client.
     *
     * @param url Emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine, ou {@code null}.
     * @param rb  Ressources utilisées pour localiser les objets racines, ou {@code null}.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Utilisateur utilisateur = Main.getUtilisateurConnecte();

        if (utilisateur instanceof Commissaire) {
            AddOffres.setVisible(true);
            debutEnchere.setVisible(true);
            voirAchats.setVisible(false);
            reclamer.setVisible(false);
        } else {
            AddOffres.setVisible(false);
            debutEnchere.setVisible(false);
            voirAchats.setVisible(true);
            reclamer.setVisible(true);
        }
    }
}

import Communication.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;



/**
 * Contrôleur pour la gestion de réclamation des utilisateurs.
 * Permet à l'utilisateur connecté de faire une réclamation concernant un produit acheté.
 * Gère la navigation vers le menu principal et le traitement des demandes de réclamation.
 */
public class ReclamationController implements Initializable {

    /**
     * Redirige l'utilisateur vers le menu principal.
     * Cette méthode est appelée lorsque l'utilisateur clique sur un bouton pour revenir au menu principal.
     *
     * @param event L'événement déclenché lors de l'action de l'utilisateur (clic sur le bouton).
     * @throws Exception Si une erreur survient lors du changement de scène.
     */
    @FXML
    private void toMenu(ActionEvent event) throws Exception {
        fctUtile.changeToScene(getClass(), event, "choix_Client.fxml");
    }

    /**
     * Initialise le contrôleur.
     * Cette méthode est appelée automatiquement après le chargement de la vue pour initialiser des composants ou des variables.
     *
     * @param url L'URL utilisée pour résoudre les chemins relatifs pour l'objet racine, ou {@code null}.
     * @param rb  Le ResourceBundle utilisé pour localiser les objets racines, ou {@code null}.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Récupérer l'utilisateur connecté depuis la classe Main
        Utilisateur utilisateur = Main.getUtilisateurConnecte();
    }

    /**
     * Champ de texte pour entrer le carID (identifiant du produit acheté).
     * L'utilisateur doit entrer l'identifiant du produit pour lequel il souhaite faire une réclamation.
     */
    @FXML
    private TextField carIDField;

    /**
     * Envoie une demande de réclamation.
     * Cette méthode est appelée lorsque l'utilisateur soumet une réclamation en entrant le carID dans le champ de texte.
     * Le système cherche les informations liées à la réclamation et les affiche.
     *
     * Si une réclamation est trouvée, les informations de celle-ci sont affichées dans un dialogue.
     * Si aucune réclamation n'est trouvée pour le carID donné, un message d'erreur est montré.
     */
    @FXML
    public void FaireReclamation() {
        try {
            // Récupérer l'utilisateur connecté
            Utilisateur user = Main.getUtilisateurConnecte();

            // Lire l'identifiant du produit
            String carId = carIDField.getText();

            // Effectuer la réclamation en utilisant l'identifiant du produit
            Reclamation reclamer = user.reclamer(carId);

            // Vérifier si la réclamation a retourné une réponse
            if (reclamer != null) {
                fctUtile.showInfoDialog("Info", "Voici les infos concernant votre réclamation: " + reclamer, "");
            } else {
                fctUtile.showInfoDialog("Info", "Aucune information de réclamation disponible.", "");
            }
        } catch (Exception e) {
            // En cas d'erreur, afficher un message d'erreur
            fctUtile.showInfoDialog("Erreur", "Erreur lors de la réclamation : ", e.getMessage());
        }
    }
}
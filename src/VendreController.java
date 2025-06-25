
import Communication.AjoutProduit;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;

/**
 * Contrôleur pour la gestion de l'ajout d'un produit à vendre par un commissaire.
 * Permet au commissaire de saisir les informations concernant un produit à vendre aux enchères.
 */
public class VendreController implements Initializable {

    /**
     * Référence à l'utilisateur connecté, ici un Commissaire.
     * Cela permet de lier l'utilisateur connecté à l'interface.
     */
    private Commissaire commissaire;

    /**
     * Champ de texte pour entrer l'email du vendeur.
     * Utilisé pour saisir l'email du vendeur du produit.
     */
    @FXML
    private TextField emailVendeurField;

    /**
     * Zone de texte pour afficher les messages d'information ou d'erreur.
     */
    @FXML
    private TextArea messageLabel;

    /**
     * Champ de texte pour entrer la description du produit.
     * Utilisé pour saisir la description du produit à vendre.
     */
    @FXML
    private TextField descriptionField;

    /**
     * Champ de texte pour entrer le prix de départ de l'enchère.
     * Utilisé pour saisir le prix initial de vente du produit.
     */
    @FXML
    private TextField prixDepartField;

    /**
     * Champ de texte pour entrer l'incrément minimum pour l'enchère.
     * Utilisé pour saisir la valeur minimale d'augmentation pour chaque nouvelle offre.
     */
    @FXML
    private TextField incrementMinField;

    /**
     * Champ de texte pour entrer la durée de l'enchère en heures.
     * Utilisé pour définir combien de temps l'enchère doit durer.
     */
    @FXML
    private TextField dureeEnchereField;

    /**
     * Champ de texte pour entrer le carID (identifiant unique du produit).
     * Utilisé pour saisir un identifiant unique pour chaque produit.
     */
    @FXML
    private TextField carIDField;

    /**
     * Méthode pour configurer l'utilisateur connecté (Commissaire).
     * Cette méthode permet de lier un commissaire à ce contrôleur, afin d'effectuer des actions de vente.
     *
     * @param commissaire Le commissaire connecté à l'application.
     */
    public void setCommissaire(Commissaire commissaire) {
        this.commissaire = commissaire;
    }

    /**
     * Gère l'ajout d'un produit à vendre aux enchères.
     * Cette méthode est appelée lorsque l'utilisateur (commissaire) clique sur le bouton pour ajouter un produit.
     * Elle récupère les valeurs des champs de saisie et envoie une demande d'ajout au serveur.
     * Si tous les champs sont remplis correctement, elle tente d'ajouter le produit et affiche un message de confirmation.
     * En cas d'erreur, un message d'erreur est affiché à l'utilisateur.
     *
     * @param event L'événement déclenché lors de l'action de l'utilisateur (clic sur le bouton).
     */
    @FXML
    private void ajouterProduit(ActionEvent event) {
        try {
            // Récupérer les valeurs des champs texte
            String emailVendeur = emailVendeurField.getText();
            String description = descriptionField.getText();
            String carID = carIDField.getText();
            double prixDepart = Double.parseDouble(prixDepartField.getText());
            int incrementMin = Integer.parseInt(incrementMinField.getText());
            int dureeEnchere = Integer.parseInt(dureeEnchereField.getText());

            // Vérifier que tous les champs sont remplis
            if (emailVendeur.isEmpty() || description.isEmpty()) {
                fctUtile.showErrorDialog("Erreur", "Tous les champs doivent être remplis.", null);
                return;
            }

            // Appeler la méthode d'ajout de produit
            AjoutProduit rep = commissaire.ajouterProduit(emailVendeur, carID, description, prixDepart, incrementMin, dureeEnchere);

            // Afficher la réponse du serveur
            messageLabel.setText(rep.toString());

        } catch (NumberFormatException e) {
            fctUtile.showErrorDialog("Erreur", "Veuillez entrer des valeurs valides pour le prix, l'incrément et la durée.", e.getMessage());
        } catch (Exception e) {
            fctUtile.showErrorDialog("Erreur", "Une erreur est survenue.", e.getMessage());
        }
    }

    /**
     * Redirige l'utilisateur vers le menu principal.
     * Cette méthode est appelée lorsque l'utilisateur souhaite revenir au menu principal.
     *
     * @param event L'événement déclenché lors de l'action (clic sur le bouton).
     * @throws Exception Si une erreur survient lors du changement de scène.
     */
    @FXML
    private void toMenu(ActionEvent event) throws Exception {
        fctUtile.changeToScene(getClass(), event, "choix_Client.fxml");
    }

    /**
     * Méthode d'initialisation.
     * Cette méthode est appelée automatiquement après le chargement de la vue FXML pour effectuer des initialisations si nécessaire.
     *
     * @param url L'URL utilisée pour résoudre les chemins relatifs pour l'objet racine, ou {@code null}.
     * @param rb  Le ResourceBundle utilisé pour localiser les objets racines, ou {@code null}.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Récupérer l'utilisateur connecté depuis Main
        Utilisateur utilisateur = Main.getUtilisateurConnecte();
    }
}
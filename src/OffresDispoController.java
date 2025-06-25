import Communication.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

/**
 * Classe de rappel pour afficher une fenêtre pop-up lorsque l'enchère commence.
 * Implémente l'interface {@link MessageCallback} pour traiter les messages reçus.
 */
class PopUpCallback implements MessageCallback {

	/**
	 * Traite le message reçu et affiche une fenêtre d'information lorsque l'enchère débute.
	 * Cette méthode est exécutée dans le thread principal de l'interface utilisateur.
	 *
	 * @param message Le message reçu du serveur qui contient des informations sur l'enchère.
	 */
	@Override
	public void traiterMessage(Message message) {
		// Afficher une fenêtre pop-up avec un message d'information
		Platform.runLater(() -> fctUtile.showInfoDialog("Info", "L'enchère a débuté", ""));
	}
}



/**
 * Contrôleur pour la gestion des offres disponibles.
 * Permet à l'utilisateur de visualiser, rafraîchir et interagir avec les produits disponibles pour enchères.
 */
public class OffresDispoController implements Initializable, MessageCallback {

	/**
	 * Liste des produits affichés dans l'interface utilisateur.
	 */
	@FXML
	private ListView<Produit> listViewProduits;

	/**
	 * Zone de texte utilisée pour afficher les messages et mises à jour.
	 */
	@FXML
	private TextArea messageLabel;

	/**
	 * Liste observable contenant les produits disponibles.
	 */
	private ObservableList<Produit> produitsList = FXCollections.observableList(new ArrayList<>());

	/**
	 * Champ de texte pour l'identifiant de l'enchère.
	 */
	@FXML
	private TextField auctionIdField;

	/**
	 * Champ de texte pour le nouveau prix à proposer pour une enchère.
	 */
	@FXML
	private TextField newPriceField;

	/**
	 * Redirige l'utilisateur vers le menu principal.
	 *
	 * @param event L'événement déclenché lors du clic sur le bouton pour retourner au menu.
	 * @throws Exception Si une erreur survient lors du changement de scène.
	 */
	@FXML
	private void toMenu(ActionEvent event) throws Exception {
		fctUtile.changeToScene(getClass(), event, "choix_Client.fxml");
	}

	/**
	 * Initialise les données et événements pour la scène.
	 * Configure l'écoute des mises à jour et remplit la liste initiale des produits.
	 *
	 * @param location  L'emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine, ou {@code null}.
	 * @param resources Les ressources utilisées pour localiser les objets racines, ou {@code null}.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listViewProduits.setItems(produitsList);

		Utilisateur utilisateur = Main.getUtilisateurConnecte();
		utilisateur.ecouterMiseAJour(this);
		utilisateur.ecouterDebutEnchere(new PopUpCallback());

		RecupererProduits message = utilisateur.recupererTousLesProduits();
		produitsList.clear();
		produitsList.addAll(message.getProduits());
		messageLabel.setText("Offres mises à jour avec succès !");
	}

	/**
	 * Rafraîchit la liste des offres disponibles en récupérant les dernières données du serveur.
	 */
	@FXML
	private void rafraichirOffres() {
		Utilisateur utilisateur = Main.getUtilisateurConnecte();
		RecupererProduits message = utilisateur.recupererTousLesProduits();

		if (message != null) {
			Platform.runLater(() -> {
				produitsList.clear();
				produitsList.addAll(message.getProduits());
				messageLabel.setText("Offres mises à jour avec succès !");
			});
		} else {
			Platform.runLater(() -> messageLabel.setText("Aucune nouvelle offre disponible."));
		}
	}

	/**
	 * Traite les messages reçus via le callback.
	 * Met à jour l'interface utilisateur en conséquence.
	 *
	 * @param message Le message reçu du serveur.
	 */
	@Override
	public void traiterMessage(Message message) {
		Platform.runLater(() -> {
			if (message instanceof MiseAJour) {
				MiseAJour miseAJour = (MiseAJour) message;
				Produit produit = miseAJour.getProduit();
				messageLabel.setText("Mise à jour reçue : " + message.getInfo());
				if (!produitsList.contains(produit)) {
					produitsList.add(produit);
				}
			} else if (message instanceof Encherir) {
				Encherir encherir = (Encherir) message;
				Produit produit = encherir.getProduit();
				messageLabel.setText("Mise à jour reçue : " + message.getInfo() + " - Nouveau prix : " + produit.getPrixCourant());
			}
		});
	}

	/**
	 * Permet à l'utilisateur de placer une enchère sur un produit spécifique.
	 *
	 * @param event L'événement déclenché lors du clic sur le bouton pour placer une enchère.
	 */
	@FXML
	private void placerEnchere(ActionEvent event) {
		Utilisateur utilisateur = Main.getUtilisateurConnecte();

		if (utilisateur != null) {
			try {
				String auctionId = auctionIdField.getText();
				double newPrice = Double.parseDouble(newPriceField.getText());

				Encherir reponse = utilisateur.encherir(auctionId, newPrice);

				if (reponse != null && reponse.isSucces()) {
					fctUtile.showInfoDialog("Succès", "Votre enchère a été placée avec succès !", null);
				} else {
					fctUtile.showErrorDialog("Erreur", "Impossible de placer l'enchère.", null);
				}
			} catch (NumberFormatException e) {
				fctUtile.showErrorDialog("Erreur", "Prix invalide.", null);
			} catch (Exception e) {
				fctUtile.showErrorDialog("Erreur", "Une erreur est survenue.", e.getMessage());
			}
		} else {
			fctUtile.showErrorDialog("Erreur", "Utilisateur non connecté.", null);
		}
	}
}

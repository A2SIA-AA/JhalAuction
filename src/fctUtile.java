import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

/**
 * Classe utilitaire contenant des méthodes statiques pour des opérations courantes,
 * telles que le changement de scène et l'affichage de dialogues d'alerte.
 */
public class fctUtile {

    /**
     * Change la scène actuelle vers une nouvelle scène définie par le fichier FXML donné.
     *
     * @param aClass        La classe depuis laquelle la ressource FXML sera chargée.
     * @param aEvent        L'événement déclencheur, utilisé pour récupérer le stage actuel.
     * @param sceneFileStr  Le chemin du fichier FXML à charger.
     * @throws Exception Si le fichier FXML ne peut pas être chargé ou si une erreur se produit.
     */
    public static void changeToScene(Class aClass, Event aEvent, String sceneFileStr) throws Exception {
        Parent root = FXMLLoader.load(aClass.getResource(sceneFileStr));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) aEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Affiche une boîte de dialogue d'erreur avec un titre, un en-tête et un contenu personnalisés.
     *
     * @param title   Le titre de la boîte de dialogue.
     * @param header  L'en-tête de la boîte de dialogue.
     * @param content Le contenu principal de la boîte de dialogue.
     */
    public static void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue d'information avec un titre, un en-tête et un contenu personnalisés.
     *
     * @param title   Le titre de la boîte de dialogue.
     * @param header  L'en-tête de la boîte de dialogue.
     * @param content Le contenu principal de la boîte de dialogue.
     */
    public static void showInfoDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

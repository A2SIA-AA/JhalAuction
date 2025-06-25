import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Main extends Application {

    private static Utilisateur utilisateurConnecte; // État global de l'utilisateur connecté

    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public static void setUtilisateurConnecte(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Aucun argument fourni !");
            return;
        }

        switch (args[0]) {
            case "-s":
                Terminale.lancerServeur(); // Le serveur reste en mode console
                break;
            case "-u":
                Terminale.lancerUtilisateur(new Scanner(System.in)); // Le serveur reste en mode console
                break;
            case "-c":
                Terminale.lancerCommissaire(new Scanner(System.in)); // Le serveur reste en mode console
                break;
            case "-g":
                launch(args); // Lance l'application graphique
                break;
            default:
                System.out.println("Argument invalide. Utilisez :");
                System.out.println("-s pour lancer le serveur en mode console");
                System.out.println("-u pour lancer l'utilisateur en mode console");
                System.out.println("-c pour lancer le commissaire en mode console");
                System.out.println("-g pour lancer l'interface graphique");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charge l'écran de connexion graphique au démarrage
        Parent root = FXMLLoader.load(getClass().getResource("menuCo.fxml"));
        primaryStage.setTitle("Connexion");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}


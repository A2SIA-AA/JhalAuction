import Communication.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Integer.parseInt;


public class Terminale {

    public static void main(String[] args) {
        // Vérifier si des arguments ont été fournis
        if (args.length == 0) {
            System.out.println("Aucun argument fourni !");
            return;
        } else if (Objects.equals(args[0], "-u")) {
            lancerUtilisateur(new Scanner(System.in));
        } else if (Objects.equals(args[0], "-s")) {
            lancerServeur();
        } else if (Objects.equals(args[0], "-c")) {
            lancerCommissaire(new Scanner(System.in));
        } else {
            throw new IllegalArgumentException(
                    "vous devez passer en parametre: " +
                    "soit -u suivi du num de tel et du mail pour lancer un utilisateur " +
                    "soit -s pour lancer un serveur " +
                    "soit -c pour lancer le commissaire"
            );
        }
    }

    public static void lancerServeur() {
        ServeurEnchere server = new ServeurEnchere();
        try {
            System.out.println("serveur lancé à l'adresse : " + InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.arreterServeur();
        }));
        server.demarrerServeur();
    }

    public static void lancerUtilisateur(Scanner scanner) {
        System.out.println("veuillez entrer l'adresse du serveur pour commencer");
        String adrServeur = scanner.nextLine();
        // Boucle principale pour le menu
        while (true) {
            // Afficher le menu principal
            System.out.println("\n===============================");
            System.out.println("=== Menu Démarrage ===");
            System.out.println("===============================");
            System.out.println("0. Quitter");
            System.out.println("1. Se connecter");
            System.out.println("2. S'inscrire");
            System.out.print("Choisissez une option (0-2): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 0:
                        System.out.println("[INFO] Au revoir !");
                        scanner.close();
                        return;
                    case 1:
                        seConnecter(scanner, adrServeur);
                        break;
                    case 2:
                        sinscrire(scanner, adrServeur);
                        break;
                    default:
                        System.out.println("[ERREUR] Option invalide. Veuillez choisir une option valide.");
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            scanner.nextLine();  // Consommer la nouvelle ligne après l'input de l'utilisateur
        }

    }

    public static void lancerCommissaire(Scanner scanner) {
        System.out.println("veuillez entrer l'adresse du serveur pour commencer");
        String adrServeur = scanner.nextLine();

        System.out.println("\n===============================");
        System.out.println("=== Bienvenue ===");
        System.out.println("===============================");

        System.out.print("\nEntrez le nom d'utilisateur : ");
        String username = scanner.nextLine();
        System.out.print("Entrez le mot de passe : ");
        String password = scanner.nextLine();
        Commissaire user = new Commissaire(username, password, adrServeur);
        Message rep = user.connexion();
        if (rep != null && rep.isSucces()) {
            user.ecouterMiseAJour(new PrintCallback());
            commissaireMenu(scanner, user);
        } else {
            System.err.println("connexion echouée " + rep);
            lancerCommissaire(scanner);
        }
    }


    private static void seConnecter(Scanner scanner, String adrServeur)  {
        System.out.print("\nEntrez le nom d'utilisateur : ");
        String username = scanner.nextLine();
        System.out.print("Entrez le mot de passe : ");
        String password = scanner.nextLine();
        Utilisateur user = new Utilisateur(username, password, adrServeur);
        Connexion rep = user.connexion();
        System.out.println(rep);
        if (rep != null && rep.isSucces()){
            user.recupererTousLesProduits();
            for (Produit produit :user.getProduits().values()) {
                System.out.println(produit);
            }
            user.ecouterMiseAJour(new PrintCallback());
            user.ecouterDebutEnchere(new PrintCallback());
            usermenu(scanner, user);
        }else{
            System.err.println("connexion echouée");
            seConnecter(scanner, adrServeur);
        }
    }


    // Méthode pour s'inscrire
    private static void sinscrire(Scanner scanner, String adrServeur)  {
        System.out.print("\nEntrez le nom d'utilisateur : ");
        String username = scanner.nextLine();
        System.out.print("Entrez le mot de passe : ");
        String password = scanner.nextLine();
        Utilisateur user = new Utilisateur(username, password, adrServeur);
        Inscription message = user.inscription();
        System.out.println(message);
    }

    private static void ajouterProduit(Scanner scanner, Commissaire user)  {
        System.out.print("\nEntrez l'email du vendeur : ");
        String emailVendeur = scanner.nextLine();
        System.out.print("Entrez votre carId : ");
        String carId = scanner.nextLine();
        System.out.print("Entrez votre description : ");
        String description = scanner.nextLine();
        System.out.print("Entrez le prix de départ : ");
        double price = scanner.nextDouble();
        System.out.print("Entrez l'incrément minimal : ");
        int minIncrement = scanner.nextInt();
        System.out.print("Entrez la durée de l'enchere en seconde: ");
        int dureeEnchere = scanner.nextInt();
        AjoutProduit message = user.ajouterProduit(emailVendeur, carId, description, price, minIncrement, dureeEnchere);
        System.out.println(message);

    }

    private static void PlacerEnchere(Scanner scanner, Utilisateur user) {
        System.out.print("\nID de l'enchère : ");
        String auctionId = scanner.nextLine();
        System.out.print("Votre offre : ");
        double newPrice = scanner.nextDouble();
        Encherir message = user.encherir(auctionId, newPrice);
        System.out.println(message);
    }

    public static void FaireReclamation(Scanner scanner, Utilisateur user) {
        System.out.print("\nEntrez le carId de la voiture sur laquelle vous avez encherit : ");
        String carId = scanner.nextLine();
        System.out.println(user.reclamer(carId));
    }

    public static void FaireDemandeHistorique(Scanner scanner, Utilisateur user){
        System.out.println(user.demanderHistorique());
    }



    private static void usermenu(Scanner scanner, Utilisateur client) {
        while (client.estConnecte()) {
            // Afficher le menu utilisateur
            System.out.println("\n===============================");
            System.out.println("=== Menu Utilisateur ===");
            System.out.println("===============================");
            System.out.println("1. placer enchere");
            System.out.println("2. FaireReclamation");
            System.out.println("3: VoirHistorique");
            System.out.println("4. quitter ");
            System.out.print("Choisissez une option (1-4): ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consommer la nouvelle ligne après l'input de l'utilisateur

            try {


                switch (choice) {
                    case 1:
                        // Ajouter une enchère
                        PlacerEnchere(scanner, client);
                        break;
                    case 2:
                        // Ajouter une enchère
                        FaireReclamation(scanner, client);
                        break;
                    case 3:
                        // Ajouter une enchère
                        FaireDemandeHistorique(scanner, client);
                        break;
                    case 4:
                        System.out.println("[INFO] Au revoir !");
                        scanner.close();
                        return;
                    default:
                        System.out.println("[ERREUR] Option invalide. Veuillez choisir une option valide.");
                }
            }catch (Exception e){
                System.out.println( e.getMessage());
            }
                scanner.nextLine();  // Consommer la nouvelle ligne après l'input de l'utilisateur
        }
    }

    private static void commissaireMenu(Scanner scanner, Commissaire user) {
        while (user.estConnecte()) {
            // Afficher le menu utilisateur
            System.out.println("\n===============================");
            System.out.println("=== Menu Utilisateur ===");
            System.out.println("===============================");
            System.out.println("1. ajouter produit");
            System.out.println("2. debuter enchere");
            System.out.println("3. supprimer produit ");
            System.out.print("Choisissez une option (1-2): ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consommer la nouvelle ligne après l'input de l'utilisateur

            try {

                switch (choice) {
                    case 1:
                        // Ajouter une enchère
                        ajouterProduit(scanner, user);
                        break;
                    case 2:
                        System.out.println(user.lancerEnchere());
                        break;
                    case 3:
                        supprimerProduit(scanner, user);

                        break;
                    default:
                        System.out.println("[ERREUR] Option invalide. Veuillez choisir une option valide.");
                }
            }catch (Exception e){
                System.out.println("données entrée invalide " + e.getMessage());
            }
            scanner.nextLine();  // Consommer la nouvelle ligne après l'input de l'utilisateur
        }

    }

    public static void supprimerProduit(Scanner scanner, Utilisateur user){

    }



}


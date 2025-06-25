import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.List;
import java.time.Duration;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import Communication.*;


import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.List;
import java.time.Duration;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import Communication.*;

/**
 * ServeurEnchere gère un serveur d'enchères en temps réel.
 * Il permet de gérer les clients, les produits, les enchères et les communications réseau.
 */
public class ServeurEnchere extends Serveur {
    private static final int PORT = 9876; // Port par défaut pour le serveur
    private DatagramSocket socket; // Socket pour les communications UDP
    private DataBase database; // Instance de la base de données
    private Map<InetAddress, Integer> connectedClients; // Liste des clients connectés
    private volatile boolean running; // Indique si le serveur est en cours d'exécution

    /**
     * Constructeur pour initialiser le serveur d'enchères.
     * Configure le socket, la base de données et démarre les processus de vérification.
     */
    public ServeurEnchere() {
        try {
            socket = new DatagramSocket(PORT);
            database = new DataBase();
            connectedClients = new ConcurrentHashMap<>();
            running = true;
            new Thread(() -> verifierExpirationProduit()).start();
            System.out.println("Server initialisé avec succès sur le port " + PORT);
        } catch (SocketException e) {
            System.err.println("Echec de la creation du socket " + PORT);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Démarre le serveur pour accepter les connexions et gérer les messages des clients.
     */
    public void demarrerServeur() {
        if (socket == null || socket.isClosed()) {
            System.err.println("Le socket pas initialisé ou est fermé");
            return;
        }
        System.out.println("Demarrage du serveur...");

        new Thread(() -> {
            while (running) {
                try {
                    byte[] receiveData = new byte[8192];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    System.out.println("En attente des messages des clients...");
                    socket.receive(receivePacket);
                    InetAddress clientAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    System.out.println("Message recue de " + clientAddress + ":" + clientPort);
                    new Thread(() -> gererMessage(receivePacket)).start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erreur de reception des data: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Arrête le serveur et libère les ressources associées.
     */
    public void arreterServeur() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (database != null) {
            database.close();
        }
        System.out.println("Server stopped");
    }

    /**
     * Vérifie si un utilisateur est inscrit et si ses informations d'authentification sont correctes.
     *
     * @param utilisateur L'utilisateur à vérifier.
     * @return L'utilisateur vérifié.
     * @throws IllegalArgumentException Si l'utilisateur n'est pas inscrit ou si le mot de passe est incorrect.
     */
    public User verifierClient(User utilisateur) throws IllegalArgumentException {
        User user = database.getUserByUsername(utilisateur.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("l'utilisateur n'est pas inscrit");
        } else if (!HashMDP(utilisateur.getPassword()).equals(user.getPassword())) {
            throw new IllegalArgumentException("le mot de passe entré est incorrect");
        } else {
            return user;
        }
    }

    /**
     * Inscrit un nouvel utilisateur.
     *
     * @param utilisateur L'utilisateur à inscrire.
     * @throws IllegalArgumentException Si l'utilisateur existe déjà ou si une erreur survient dans la base de données.
     */
    public void incrireNouveauClient(User utilisateur) throws IllegalArgumentException {
        try {
            if (database.getUserByUsername(utilisateur.getUsername()) == null) {
                database.addUser(utilisateur.getUsername(), HashMDP(utilisateur.getPassword()));
            } else {
                throw new IllegalArgumentException("l'utilisateur existe deja");
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("erreur survenu dans la data base " + e.getMessage());
        }
    }

    /**
     * Ajoute un client connecté à la liste des clients connectés.
     *
     * @param clientAddress L'adresse IP du client.
     * @param clientPort    Le port du client.
     */
    public void ajouterClientConnecte(InetAddress clientAddress, int clientPort) {
        connectedClients.put(clientAddress, clientPort);
    }

    /**
     * Envoie un message à un client spécifique.
     *
     * @param address L'adresse du client.
     * @param port    Le port du client.
     * @param message Le message à envoyer.
     */
    protected void envoyerMessage(InetAddress address, int port, Message message) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);

            byte[] sendData = baos.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);
            System.out.println("Response sent to " + address + ":" + port);
        } catch (IOException e) {
            System.err.println("Error sending response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Diffuse un message à tous les clients connectés.
     *
     * @param message Le message à diffuser.
     */
    public void broadcastMessage(Message message) {
        for (Map.Entry<InetAddress, Integer> client : connectedClients.entrySet()) {
            envoyerMessage(client.getKey(), client.getValue(), message);
        }
    }


    /**
     * Ajoute un nouveau produit à la base de données.
     *
     * @param produit Le produit à ajouter.
     * @throws IllegalArgumentException Si le produit existe déjà ou si une erreur survient dans la base de données.
     */
    public void ajouterProduit(Produit produit) {
        try {
            if (database.getCar(produit.getCarId()) == null) {
                database.addCar(produit);
            } else {
                throw new IllegalArgumentException("Produit deja existant");
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Erreur survenue dans la data base " + e.getMessage());
        }
    }

    /**
     * Permet à un utilisateur de placer une enchère sur un produit.
     *
     * @param produit Le produit sur lequel enchérir avec les informations mises à jour.
     * @return Le produit mis à jour après l'enchère.
     * @throws IllegalArgumentException Si une erreur survient dans la base de données.
     */
    public Produit encherir(Produit produit) {
        try {
            database.addBid(produit.getCarId(), produit.getUserAcheteur(), produit.getPrixCourant());
            return database.getCar(produit.getCarId());
        } catch (SQLException e) {
            throw new IllegalArgumentException("Erreur survenu dans la data base " + e.getMessage());
        }
    }

    /**
     * Récupère la liste de tous les produits disponibles dans la base de données.
     *
     * @return Une liste contenant tous les produits.
     */
    public List<Produit> recupererTousLesProduits() {
        return database.getAllCars();
    }

    /**
     * Permet à un utilisateur de faire une réclamation concernant un produit.
     *
     * @param user  L'utilisateur effectuant la réclamation.
     * @param carId L'identifiant du produit concerné.
     * @return Les détails de la réclamation effectuée.
     * @throws IllegalArgumentException Si le produit fourni est nul.
     */
    public ReclamationDetails faireReclamation(User user, String carId) {
        if (carId == null) {
            throw new IllegalArgumentException("Le produit fourni est nulle");
        }
        return database.makeReclamation(user.getUsername(), carId);
    }

    /**
     * Récupère l'historique des enchères pour un utilisateur donné.
     *
     * @param user L'utilisateur dont l'historique est demandé.
     * @return Une liste contenant les détails de l'historique de l'utilisateur.
     */
    public List<HistoryDetails> demanderHistorique(User user) {
        return database.getHistoriqueByUsername(user.getUsername());
    }

    /**
     * Vérifie si des produits sont arrivés à expiration et met à jour leur disponibilité.
     * Diffuse un message de fin d'enchère pour les produits expirés.
     */
    protected void verifierExpirationProduit() {
        try {
            while (running) {
                if (getDebutEnchere() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    long elapsed = Duration.between(getDebutEnchere(), now).toSeconds();
                    for (Produit produit : database.getAllCars()) {
                        if (elapsed > produit.getDureeEnchereSecondes()) {
                            produit.setDisponible(false);
                            try {
                                database.updateCar(produit);
                                broadcastMessage(new FinEnchere(produit));
                                System.out.println(now + " le produit suivant n'est plus disponible " + produit);
                            } catch (SQLException e) {
                                System.out.println("echec de la mise a jour du produit dans la data base: " + e.getMessage());
                            }
                        }
                    }
                }
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
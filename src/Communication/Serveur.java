package Communication;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Classe abstraite représentant un serveur de gestion d'enchères.
 * Ce serveur gère les connexions des clients, les enchères, les produits et la gestion des utilisateurs.
 * Il inclut des méthodes pour démarrer et arrêter le serveur, vérifier les utilisateurs, gérer les produits,
 * traiter les enchères et les réclamations, et gérer la communication avec les clients.
 */
public abstract class Serveur {
    protected LocalDateTime debutEnchere = null; // Date de début de l'enchère

    /**
     * Retourne la date de début de l'enchère.
     *
     * @return La date de début de l'enchère.
     */
    public LocalDateTime getDebutEnchere() {
        return debutEnchere;
    }

    /**
     * Initialise la date de début de l'enchère à la date et l'heure actuelle.
     */
    public void setDebutEnchere() {
        this.debutEnchere = LocalDateTime.now();
    }

    /**
     * Permet de hasher le mot de passe de l'utilisateur en utilisant l'algorithme SHA-256.
     *
     * @param input Le mot de passe de l'utilisateur à hasher.
     * @return Le mot de passe hashé.
     * @throws Exception Si l'algorithme de hashage échoue.
     */
    public static String HashMDP(String input)  {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Echec de l'encryption du message ...");
        }
        return input;
    }

    /**
     * Démarre le serveur et le prépare à écouter les connexions.
     * La mise en place du serveur ne lance pas encore l'enchère.
     */
    public abstract void demarrerServeur();

    /**
     * Arrête le serveur et ferme toutes les connexions en cours.
     */
    public abstract void arreterServeur();

    /**
     * Vérifie l'identité d'un utilisateur à partir de son email et de son mot de passe.
     * Si l'utilisateur est trouvé dans la base de données, il est retourné.
     * Sinon, une exception est lancée.
     *
     * @param utilisateur L'utilisateur à vérifier.
     * @return L'utilisateur si l'identité est valide.
     * @throws IllegalArgumentException Si l'utilisateur n'est pas trouvé ou si les informations sont incorrectes.
     */
    public abstract User verifierClient(User utilisateur) throws IllegalArgumentException;

    /**
     * Enregistre un nouvel utilisateur dans la base de données du serveur.
     * Si l'utilisateur existe déjà, une exception IllegalArgumentException est lancée.
     *
     * @param utilisateur L'utilisateur à inscrire.
     * @throws IllegalArgumentException Si l'utilisateur existe déjà.
     */
    public abstract void incrireNouveauClient(User utilisateur) throws IllegalArgumentException;

    /**
     * Ajoute un client connecté à la liste des clients du serveur.
     *
     * @param clientAddress L'adresse IP du client.
     * @param clientPort Le port du client.
     */
    public abstract void ajouterClientConnecte(InetAddress clientAddress, int clientPort);

    /**
     * Envoie un message à un client spécifique.
     *
     * @param clientAddress L'adresse IP du destinataire.
     * @param clientPort Le port du destinataire.
     * @param message Le message à envoyer.
     */
    protected abstract void envoyerMessage(InetAddress clientAddress, int clientPort, Message message);

    /**
     * Envoie un message à tous les clients actuellement connectés au serveur.
     *
     * @param message Le message à envoyer à tous les clients.
     */
    public abstract void broadcastMessage(Message message);

    /**
     * Ajoute un nouveau produit à l'enchère du serveur.
     *
     * @param produit Le produit à ajouter à l'enchère.
     */
    public abstract void ajouterProduit(Produit produit);

    /**
     * Permet à un utilisateur de faire une offre sur un produit.
     * La méthode vérifie si le montant de l'offre est supérieur ou égal au prix actuel du produit plus le pas minimal.
     *
     * @param produit Le produit sur lequel l'utilisateur veut faire une enchère.
     * @return Le produit mis à jour avec le nouveau prix.
     */
    public abstract Produit encherir(Produit produit);

    /**
     * Récupère tous les produits encore disponibles sur le serveur.
     *
     * @return La liste des produits disponibles.
     */
    public abstract List<Produit> recupererTousLesProduits();

    /**
     * Gère une réclamation d'un utilisateur sur un produit spécifique.
     *
     * @param user L'utilisateur qui fait la réclamation.
     * @param carId L'ID du produit sur lequel l'utilisateur fait la réclamation.
     * @return Les détails de la réclamation effectuée.
     */
    public abstract ReclamationDetails faireReclamation(User user, String carId);

    /**
     * Permet à un utilisateur d'obtenir l'historique de ses enchères lors de la session en cours.
     *
     * @param user L'utilisateur pour lequel on veut obtenir l'historique.
     * @return La liste des détails de l'historique des enchères de l'utilisateur.
     */
    public abstract List<HistoryDetails> demanderHistorique(User user);

    /**
     * Méthode pour traiter les paquets reçus. Elle récupère le message du paquet UDP et effectue le traitement approprié
     * en appelant la méthode `traitementServeur` du message reçu.
     *
     * @param packet Le paquet reçu contenant le message.
     */
    protected void gererMessage(DatagramPacket packet) {
        try {
            int clientPort = packet.getPort();
            InetAddress clientAddress = packet.getAddress();

            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            ObjectInputStream ois = new ObjectInputStream(bais);

            Message message = (Message) ois.readObject();
            try {
                message.traitementServeur(this, clientAddress, clientPort);
                message.setSucces(true);
            } catch (Exception e) {
                message.setInfo(e.getMessage());
                message.setSucces(false);
            } finally {
                envoyerMessage(clientAddress, clientPort, message);
            }

        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

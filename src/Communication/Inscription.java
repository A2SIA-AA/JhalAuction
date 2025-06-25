package Communication;

import java.net.InetAddress;

/**
 * Classe représentant un message d'inscription d'un utilisateur.
 * Cette classe étend la classe {@link Message} et est utilisée pour l'inscription d'un nouveau client
 * sur le serveur.
 *
 * Elle contient un constructeur pour initialiser un auteur (utilisateur) et une méthode pour
 * traiter le message côté serveur.
 */
public class Inscription extends Message {

    /**
     * Constructeur de la classe Inscription.
     * Initialisation de l'auteur du message (l'utilisateur qui s'inscrit).
     *
     * @param auteur L'utilisateur qui envoie le message d'inscription.
     */
    public Inscription(User auteur) {
        super(auteur);
    }

    /**
     * Méthode traitant l'inscription côté serveur.
     * Elle vérifie que l'utilisateur n'est pas déjà inscrit et ajoute ce dernier
     * comme un nouveau client sur le serveur.
     *
     * @param serveur      Le serveur qui gère les inscriptions.
     * @param clientAddress L'adresse IP du client.
     * @param clientPort   Le port du client.
     */
    public void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort) {
        serveur.incrireNouveauClient(getAuteur());
    }
}

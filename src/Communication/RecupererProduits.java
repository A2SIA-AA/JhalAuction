package Communication;

import java.net.InetAddress;
import java.util.LinkedList;

/**
 * Représente une demande d'un client pour récupérer la liste de tous les produits disponibles.
 * Cette classe permet à un utilisateur de récupérer tous les produits du serveur, qui seront stockés
 * dans une liste interne.
 */
public class RecupererProduits extends Message {
    protected LinkedList<Produit> produits; // Liste des produits récupérés

    /**
     * Constructeur pour initialiser un message de type RecupererProduits.
     * Il initialise la liste des produits à une nouvelle liste vide.
     *
     * @param auteur L'utilisateur (client) qui effectue la demande de récupération des produits.
     */
    public RecupererProduits(User auteur) {
        super(auteur);
        produits = new LinkedList<Produit>();
    }

    /**
     * Retourne la liste des produits récupérés.
     *
     * @return Une liste de produits récupérés par la demande.
     */
    public LinkedList<Produit> getProduits() {
        return produits;
    }

    /**
     * Traitement de la requête du client par le serveur.
     * Cette méthode demande au serveur de récupérer tous les produits et
     * les ajoute à la liste de produits interne.
     *
     * @param serveur L'instance du serveur qui doit traiter la demande.
     * @param clientAddress L'adresse IP du client qui a fait la demande.
     * @param clientPort Le port du client qui a fait la demande.
     */
    public void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort){
        produits.addAll(serveur.recupererTousLesProduits()); // Récupère tous les produits du serveur
    }
}

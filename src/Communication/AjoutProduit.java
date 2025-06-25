package Communication;

import java.net.InetAddress;

/**
 * Classe représentant un message pour l'ajout d'un produit à une enchère.
 * Hérite de {@link MessageAvecProduit} et inclut les informations de l'auteur et du produit.
 */
public class AjoutProduit extends MessageAvecProduit {

    /**
     * Constructeur de la classe AjoutProduit.
     *
     * @param auteur  L'utilisateur auteur de l'ajout du produit.
     * @param produit Le produit à ajouter.
     */
    public AjoutProduit(User auteur, Produit produit) {
        super(auteur, produit);
    }

    /**
     * Traite le message côté serveur pour ajouter un produit à l'enchère.
     *
     * @param serveur       L'instance du serveur qui traite le message.
     * @param clientAddress L'adresse du client qui a envoyé le message.
     * @param clientPort    Le port du client qui a envoyé le message.
     * @throws IllegalArgumentException Si l'enchère a déjà commencé, empêchant l'ajout de nouveaux produits.
     */
    @Override
    public void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort) {
        if (serveur.getDebutEnchere() != null) {
            throw new IllegalArgumentException("Impossible de rajouter des produits car l'enchere a deja commence");
        }

        // Vérifie que l'utilisateur est inscrit et autorisé
        serveur.verifierClient(getAuteur());

        // Ajoute le produit au serveur
        serveur.ajouterProduit(getProduit());

        // Envoie une mise à jour indiquant qu'un nouveau produit a été ajouté
        new MiseAJour(getProduit(), "Nouveau Produit rajoute aux encheres").traitementServeur(serveur);
    }
}

package Communication;

import java.net.InetAddress;

/**
 * Classe représentant une mise à jour d'un produit dans le système d'enchères.
 * Cette classe étend `MessageAvecProduit` et est utilisée pour transmettre des
 * informations sur un produit à tous les clients connectés, généralement pour
 * informer d'un changement important, tel qu'une nouvelle offre ou la fin de l'enchère.
 */
public class MiseAJour extends MessageAvecProduit {

    /**
     * Constructeur de la classe MiseAJour.
     *
     * @param produit Le produit qui a été mis à jour.
     * @param info Un message contenant des informations sur la mise à jour.
     */
    public MiseAJour(Produit produit, String info) {
        super(null, produit);  // L'auteur est nul, car cette mise à jour est un message systématique.
        setSucces(true);  // La mise à jour est toujours un succès.
        setInfo(info);  // Le message d'information concernant la mise à jour.
    }

    /**
     * Traite la mise à jour du produit et la diffuse à tous les clients connectés.
     * Ce traitement est effectué sur le serveur en envoyant la mise à jour à tous
     * les clients via un broadcast.
     *
     * @param serveur Le serveur qui gère la communication avec les clients.
     * @param clientAddress L'adresse du client à qui envoyer la mise à jour (peut être null pour un broadcast).
     * @param clientPort Le port du client (peut être 0 pour un broadcast).
     */
    public void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort) {
        serveur.broadcastMessage(this);  // Diffuse la mise à jour à tous les clients connectés.
    }

    /**
     * Variante de la méthode `traitementServeur` qui ne nécessite pas les informations de l'adresse et du port client.
     * Elle est utilisée pour envoyer une mise à jour à tous les clients connectés sans spécifier de client particulier.
     *
     * @param serveur Le serveur qui gère la communication avec les clients.
     */
    public void traitementServeur(Serveur serveur) {
        traitementServeur(serveur, null, 0);  // Appelle la méthode principale avec des valeurs par défaut pour un broadcast.
    }
}

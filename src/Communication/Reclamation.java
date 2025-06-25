package Communication;

import java.net.InetAddress;

/**
 * Représente une réclamation effectuée par un utilisateur concernant un produit spécifique dans le système d'enchères.
 * Cette classe permet à un utilisateur de faire une réclamation sur un produit en fournissant des détails sur la réclamation.
 */
public class Reclamation extends Message {
    protected String carId; // Identifiant du produit sur lequel la réclamation est faite
    protected ReclamationDetails reclamationDetails; // Détails de la réclamation

    /**
     * Constructeur pour créer une réclamation avec un auteur et un identifiant de produit.
     *
     * @param auteur L'utilisateur qui fait la réclamation.
     * @param carId L'identifiant du produit concerné par la réclamation.
     */
    public Reclamation(User auteur, String carId) {
        super(auteur);
        this.carId = carId;
        this.reclamationDetails = null;
    }

    /**
     * Retourne les détails de la réclamation.
     *
     * @return Les détails de la réclamation.
     */
    public ReclamationDetails getReclamationDetails() {
        return reclamationDetails;
    }

    /**
     * Retourne l'identifiant du produit concerné par la réclamation.
     *
     * @return L'identifiant du produit.
     */
    public String getCarId() {
        return carId;
    }

    /**
     * Définit l'identifiant du produit concerné par la réclamation.
     *
     * @param carId L'identifiant du produit.
     */
    public void setCarId(String carId) {
        this.carId = carId;
    }

    /**
     * Définit les détails de la réclamation.
     *
     * @param reclamationDetails Les détails de la réclamation à associer.
     * @throws IllegalArgumentException Si les détails de la réclamation sont null.
     */
    public void setReclamationDetails(ReclamationDetails reclamationDetails) {
        if (reclamationDetails == null) {
            throw new IllegalArgumentException("La réclamation fournie est null.");
        }
        this.reclamationDetails = reclamationDetails;
    }

    /**
     * Traite la réclamation sur le serveur. Vérifie l'utilisateur, puis fait appel à la méthode pour effectuer la réclamation
     * et récupérer les détails de la réclamation.
     *
     * @param serveur Le serveur sur lequel la réclamation est traitée.
     * @param clientAddress L'adresse IP du client.
     * @param clientPort Le port du client.
     */
    public void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort) {
        serveur.verifierClient(getAuteur());
        setReclamationDetails(serveur.faireReclamation(getAuteur(), getCarId()));
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de la réclamation, y compris les détails de la réclamation,
     * l'identifiant du produit, la date de création et le statut de succès.
     *
     * @return Une chaîne représentant la réclamation.
     */
    @Override
    public String toString() {
        return reclamationDetails +
                ",\n carId : " + carId +
                ",\n Date : " + dateCreation +
                ",\n succes : " + succes;
    }
}

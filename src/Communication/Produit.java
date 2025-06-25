package Communication;

import java.io.Serializable;

/**
 * Représente un produit (ou une voiture) mis en vente dans un système d'enchères.
 * Cette classe contient des informations sur le produit, son prix, sa description,
 * l'état de l'enchère, et l'acheteur ainsi que le vendeur associés.
 */
public class Produit implements Serializable {

    protected String carId; // Identifiant unique du produit (par exemple, le numéro d'immatriculation)
    protected String description; // Description du produit
    protected double prixInitial; // Prix initial du produit lors de la mise en vente
    protected double prixCourant; // Prix courant du produit lors de l'enchère
    protected int pasEnchere; // Montant minimal de l'enchère (pas d'enchère)
    protected boolean disponible; // Indique si le produit est disponible pour l'enchère
    protected Integer dureeEnchereSecondes; // Durée de l'enchère en secondes
    protected String userAcheteur; // Utilisateur ayant effectué la dernière offre
    protected String userVendeur; // Vendeur du produit

    /**
     * Constructeur complet pour un produit.
     *
     * @param carId L'identifiant du produit.
     * @param description La description du produit.
     * @param prixInitial Le prix initial du produit.
     * @param prixCourant Le prix courant du produit.
     * @param pasEnchere Le montant minimal d'une enchère.
     * @param dureeEnchereSecondes La durée de l'enchère en secondes.
     * @param disponible L'état de disponibilité du produit pour l'enchère.
     * @param userVendeur L'utilisateur vendeur du produit.
     * @param userAcheteur L'utilisateur acheteur du produit.
     */
    public Produit(String carId, String description, double prixInitial, double prixCourant, int pasEnchere, int dureeEnchereSecondes, boolean disponible, String userVendeur, String userAcheteur) {
        this.carId = carId;
        this.description = description;
        this.prixInitial = prixInitial;
        this.prixCourant = prixCourant;
        this.pasEnchere = pasEnchere;
        this.disponible = disponible;
        this.dureeEnchereSecondes = dureeEnchereSecondes;
        this.userAcheteur = userAcheteur;
        this.userVendeur = userVendeur;
    }

    /**
     * Constructeur avec un prix initial et une durée d'enchère.
     *
     * @param carId L'identifiant du produit.
     * @param description La description du produit.
     * @param prixInitial Le prix initial du produit.
     * @param pasEnchere Le montant minimal d'une enchère.
     * @param dureeEnchereSecondes La durée de l'enchère en secondes.
     */
    public Produit(String carId, String description, double prixInitial, int pasEnchere, int dureeEnchereSecondes) {
        this(carId, description, prixInitial, prixInitial, pasEnchere, dureeEnchereSecondes, true, "", "");
    }

    /**
     * Constructeur de copie pour dupliquer un produit existant.
     *
     * @param another Le produit à copier.
     */
    public Produit(Produit another){
        this.carId = another.carId;
        this.description = another.description;
        this.prixInitial = another.prixInitial;
        this.prixCourant = another.prixCourant;
        this.pasEnchere = another.pasEnchere;
        this.disponible = another.disponible;
        this.dureeEnchereSecondes = another.dureeEnchereSecondes;
        this.userAcheteur = another.userAcheteur;
        this.userVendeur = another.userVendeur;
    }

    /**
     * Effectue une enchère sur ce produit avec un montant proposé.
     *
     * @param user L'utilisateur qui fait l'enchère.
     * @param montant Le montant de l'enchère.
     * @return Une nouvelle instance de `Produit` avec le prix mis à jour et l'acheteur.
     */
    public Produit encherir(User user, double montant){
        Produit proposition = new Produit(this);  // Crée une copie du produit actuel
        proposition.setPrixCourant(montant);  // Met à jour le prix avec la nouvelle offre
        proposition.setUserAcheteur(user.getUsername());  // Associe l'acheteur à l'offre
        return proposition;  // Retourne le produit avec la nouvelle enchère
    }

    // Getters et Setters pour tous les champs de la classe

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrixInitial() {
        return prixInitial;
    }

    public void setPrixInitial(double prixInitial) {
        this.prixInitial = prixInitial;
    }

    public double getPrixCourant() {
        return prixCourant;
    }

    public void setPrixCourant(double prixCourant) {
        this.prixCourant = prixCourant;
    }

    public int getPasEnchere() {
        return pasEnchere;
    }

    public void setPasEnchere(int pasEnchere) {
        this.pasEnchere = pasEnchere;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Integer getDureeEnchereSecondes() {
        return dureeEnchereSecondes;
    }

    public void setDureeEnchereSecondes(Integer dureeEnchereSecondes) {
        this.dureeEnchereSecondes = dureeEnchereSecondes;
    }

    public String getUserAcheteur() {
        return userAcheteur;
    }

    public void setUserAcheteur(String userAcheteur) {
        this.userAcheteur = userAcheteur;
    }

    public String getUserVendeur() {
        return userVendeur;
    }

    public void setUserVendeur(String userVendeur) {
        this.userVendeur = userVendeur;
    }

    /**
     * Vérifie si deux produits sont égaux en comparant leurs identifiants.
     *
     * @param obj L'objet à comparer avec le produit courant.
     * @return `true` si les produits ont le même identifiant, sinon `false`.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Produit) {
            Produit p = (Produit) obj;
            return p.getCarId().equals(carId);
        }
        return false;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères du produit.
     *
     * @return Une chaîne décrivant le produit avec ses informations principales.
     */
    @Override
    public String toString() {
        return "carId : " + carId + "\ndescription : " + description + "\nprixCourant : " + prixCourant +
                "\npasEnchere : " + pasEnchere + "\ndisponible : " + disponible;
    }
}

package Communication;

/**
 * Classe abstraite représentant un message qui contient un produit.
 * Cette classe étend la classe `Message` et ajoute un champ `produit` pour
 * transporter des informations concernant un produit spécifique avec le message.
 *
 * Les messages qui doivent transporter un produit (par exemple, l'ajout d'un produit
 * aux enchères ou une offre sur un produit) peuvent hériter de cette classe.
 */
public abstract class MessageAvecProduit extends Message {

    /** Le produit associé à ce message */
    protected Produit produit;

    /**
     * Constructeur de la classe MessageAvecProduit.
     *
     * @param auteur L'utilisateur qui envoie ce message. Si l'auteur est non nul,
     *               une copie de l'utilisateur est effectuée pour le champ `auteur`.
     * @param produit Le produit à associer à ce message.
     */
    public MessageAvecProduit(User auteur, Produit produit) {
        super(auteur); // Appelle le constructeur de la classe parente Message
        this.produit = produit;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères du message
     * avec le produit. Cette représentation inclut la classe, le produit, l'auteur,
     * la date de création, le statut de succès et l'information associée.
     *
     * @return Une chaîne représentant le message avec le produit.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "produit=" + produit +
                ", auteur=" + auteur +
                ", dateCreation=" + dateCreation +
                ", succes=" + succes +
                ", info='" + info + '\'' +
                '}';
    }

    /**
     * Retourne le produit associé à ce message.
     *
     * @return Le produit associé au message.
     */
    public Produit getProduit() {
        return produit;
    }

    /**
     * Définit le produit associé à ce message.
     *
     * @param produit Le produit à associer au message.
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
}

package Communication;

/**
 * Classe représentant la fin d'une enchère sur un produit.
 * Hérite de {@link MiseAJour}.
 */
public class FinEnchere extends MiseAJour {

    /**
     * Constructeur de la classe FinEnchere.
     *
     * @param produit Le produit pour lequel l'enchère est terminée.
     *                Le message inclut le gagnant de l'enchère (si disponible).
     */
    public FinEnchere(Produit produit) {
        super(produit, "Fin de l'enchère sur ce produit: le gagnant est " + produit.getUserAcheteur());
    }
}

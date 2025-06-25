import Communication.AjoutProduit;
import Communication.DebutEnchere;
import Communication.Encherir;
import Communication.Produit;

/**
 * Classe représentant un commissaire, qui hérite des fonctionnalités de la classe Utilisateur.
 * Le commissaire a des droits spécifiques, tels que l'ajout de produits et le lancement d'enchères,
 * mais ne peut pas enchérir.
 */
public class Commissaire extends Utilisateur {

    /**
     * Constructeur de la classe Commissaire.
     *
     * @param commissaire     Nom d'utilisateur ou identifiant du commissaire.
     * @param motDePasse      Mot de passe du commissaire.
     * @param addresseServeur Adresse du serveur auquel le commissaire est connecté.
     */
    public Commissaire(String commissaire, String motDePasse, String addresseServeur) {
        super(commissaire, motDePasse, addresseServeur);
    }

    /**
     * Ajoute un produit à vendre au système.
     *
     * @param emailVendeur          L'adresse e-mail du vendeur du produit.
     * @param carId                 L'identifiant unique du produit (carId).
     * @param description           La description du produit.
     * @param prixInitial           Le prix initial de l'enchère.
     * @param pasEnchere            Le pas minimum pour augmenter une enchère.
     * @param dureeEnchereSecondes  La durée de l'enchère en secondes.
     * @return Une instance de {@link AjoutProduit} représentant la réponse du système après l'ajout.
     */
    @Override
    public AjoutProduit ajouterProduit(String emailVendeur, String carId, String description, double prixInitial, int pasEnchere, int dureeEnchereSecondes) {
        Produit produit = new Produit(carId, description, prixInitial, pasEnchere, dureeEnchereSecondes);
        produit.setUserVendeur(emailVendeur);
        sendMessage(new AjoutProduit(this, produit));
        return filterReponsesByTypeWithRetry(AjoutProduit.class, 10).getLast();
    }

    /**
     * Lève une exception, car un commissaire n'a pas le droit d'enchérir sur des produits.
     *
     * @param carId   L'identifiant du produit sur lequel enchérir (non utilisé).
     * @param montant Le montant de l'enchère (non utilisé).
     * @throws IllegalArgumentException Toujours levée pour indiquer qu'un commissaire ne peut pas enchérir.
     */
    @Override
    public Encherir encherir(String carId, double montant) {
        throw new IllegalArgumentException("vous n'avez pas le droit d'encherir");
    }

    /**
     * Lance une nouvelle enchère dans le système.
     *
     * @return Une instance de {@link DebutEnchere} représentant la réponse du système après le lancement de l'enchère.
     */
    public DebutEnchere lancerEnchere() {
        sendMessage(new DebutEnchere(this));
        return filterReponsesByTypeWithRetry(DebutEnchere.class, 10).getLast();
    }

}

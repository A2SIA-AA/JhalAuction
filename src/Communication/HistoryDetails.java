package Communication;

import java.io.Serializable;

/**
 * Classe représentant les détails d'une enchère dans l'historique d'un utilisateur.
 * Chaque instance contient des informations spécifiques à une enchère passée, telles que l'ID de l'enchère,
 * l'ID du produit, l'heure de l'interaction et le montant de l'enchère.
 *
 * Cette classe implémente {@link Serializable}, permettant ainsi de sérialiser ses objets.
 */
public class HistoryDetails implements Serializable {

    /**
     * Identifiant unique de l'enchère.
     */
    protected int id;

    /**
     * Identifiant du produit (véhicule) concerné par l'enchère.
     */
    protected String car_id;

    /**
     * Heure à laquelle l'enchère a eu lieu.
     */
    protected String interaction_time;

    /**
     * Montant de l'enchère effectuée.
     */
    protected double bid_amount;

    /**
     * Récupère le montant de l'enchère.
     *
     * @return Le montant de l'enchère.
     */
    public double getBid_amount() {
        return bid_amount;
    }

    /**
     * Récupère l'identifiant unique de l'enchère.
     *
     * @return L'ID de l'enchère.
     */
    public int getId() {
        return id;
    }

    /**
     * Récupère l'identifiant du produit associé à l'enchère.
     *
     * @return L'ID du produit.
     */
    public String getCar_id() {
        return car_id;
    }

    /**
     * Récupère l'heure de l'interaction de l'enchère.
     *
     * @return L'heure à laquelle l'enchère a été effectuée.
     */
    public String getInteraction_time() {
        return interaction_time;
    }

    /**
     * Constructeur de la classe HistoryDetails.
     * Initialise les détails de l'enchère avec les informations fournies.
     *
     * @param id               L'identifiant unique de l'enchère.
     * @param car_id           L'identifiant du produit (véhicule) concerné.
     * @param interaction_time L'heure à laquelle l'enchère a été effectuée.
     * @param bid_amount       Le montant de l'enchère effectuée.
     */
    public HistoryDetails(int id, String car_id, String interaction_time, double bid_amount) {
        this.id = id;
        this.car_id = car_id;
        this.interaction_time = interaction_time;
        this.bid_amount = bid_amount;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères des détails de l'enchère.
     * Inclut l'ID de l'enchère, l'ID du produit, la date de l'interaction et le montant de l'enchère.
     *
     * @return Une chaîne décrivant les détails de l'enchère.
     */
    @Override
    public String toString() {
        return  "\n enchère numéro : " + id +
                ",\n car_id : " + car_id  +
                ",\n date de l'interaction: " + interaction_time +
                ",\n montant de l'enchérissement : " + bid_amount ;
    }
}


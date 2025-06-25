package Communication;

import java.net.InetAddress;

/**
 * Classe représentant une action d'enchère sur un produit.
 * Hérite de {@link MessageAvecProduit}.
 */
public class Encherir extends MessageAvecProduit {

    /**
     * Constructeur de la classe Encherir.
     *
     * @param auteur  L'utilisateur qui effectue l'enchère.
     * @param produit Le produit sur lequel l'enchère est effectuée.
     */
    public Encherir(User auteur, Produit produit) {
        super(auteur, produit);
    }

    /**
     * Traite le message côté serveur pour enregistrer une enchère.
     *
     * <p>Cette méthode effectue les étapes suivantes :
     * <ul>
     *   <li>Vérifie que l'enchère a commencé en appelant {@link Serveur#getDebutEnchere()}.</li>
     *   <li>Valide l'utilisateur effectuant l'enchère via {@link Serveur#verifierClient(User)}.</li>
     *   <li>Met à jour le produit avec la nouvelle enchère en appelant {@link Serveur#encherir(Produit)}.</li>
     *   <li>Diffuse une mise à jour à tous les utilisateurs via {@link MiseAJour#traitementServeur(Serveur)}.</li>
     * </ul>
     * </p>
     *
     * @param serveur       L'instance du serveur qui traite le message.
     * @param clientAddress L'adresse IP du client qui a initié l'enchère.
     * @param clientPort    Le port du client qui a initié l'enchère.
     * @throws IllegalArgumentException Si l'enchère n'a pas encore commencé.
     */
    @Override
    public void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort) {
        // Vérifie si l'enchère a commencé
        if (serveur.getDebutEnchere() == null) {
            throw new IllegalArgumentException("L'enchère n'a pas encore commencé");
        }

        // Vérifie la validité de l'utilisateur
        serveur.verifierClient(getAuteur());

        // Effectue l'enchère et récupère le produit mis à jour
        Produit produitAJour = serveur.encherir(getProduit());

        // Crée une mise à jour et la traite côté serveur
        new MiseAJour(
                produitAJour,
                "Nouvelle offre effectuée par " + getAuteur().getUsername() +
                        "\nSur le produit : " + produitAJour.getCarId() +
                        " au prix de : " + produitAJour.getPrixCourant()
        ).traitementServeur(serveur);

        // Marque le message comme traité avec succès
        setSucces(true);
    }
}

package Communication;

import java.net.InetAddress;

/**
 * Classe représentant un message pour démarrer une enchère.
 * Hérite de la classe {@link Message}.
 */
public class DebutEnchere extends Message {

    /**
     * Indique si l'enchère a commencé.
     */
    protected boolean debutEnchere = false;

    /**
     * Constructeur de la classe DebutEnchere.
     *
     * @param auteur L'utilisateur qui initie le début de l'enchère.
     */
    public DebutEnchere(User auteur) {
        super(auteur);
    }

    /**
     * Vérifie si l'enchère a débuté.
     *
     * @return {@code true} si l'enchère a commencé, {@code false} sinon.
     */
    public boolean isDebutEnchere() {
        return debutEnchere;
    }

    /**
     * Définit l'état de démarrage de l'enchère.
     *
     * @param debutEnchere {@code true} si l'enchère doit être marquée comme commencée, {@code false} sinon.
     */
    public void setDebutEnchere(boolean debutEnchere) {
        this.debutEnchere = debutEnchere;
    }

    /**
     * Traite le message côté serveur pour initier une enchère.
     *
     * <p>Cette méthode effectue plusieurs actions :
     * <ul>
     *   <li>Vérifie que l'utilisateur qui initie l'enchère est valide via {@link Serveur#verifierClient(User)}.</li>
     *   <li>Marque l'enchère comme commencée en appelant {@link Serveur#setDebutEnchere()}.</li>
     *   <li>Met à jour l'état interne et informe les autres utilisateurs via une diffusion globale.</li>
     * </ul>
     * </p>
     *
     * @param serveur       L'instance du serveur qui traite le message.
     * @param clientAddress L'adresse IP du client qui a initié l'enchère.
     * @param clientPort    Le port du client qui a initié l'enchère.
     */
    @Override
    public void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort) {
        // Vérifie si l'utilisateur est valide
        serveur.verifierClient(getAuteur());

        // Marque l'enchère comme commencée sur le serveur
        serveur.setDebutEnchere();

        // Met à jour l'état interne de l'objet
        setDebutEnchere(true);
        setSucces(true);
        setAuteur(null); // Réinitialise l'auteur pour éviter des fuites de données

        // Diffuse un message global aux clients connectés
        serveur.broadcastMessage(this);
    }
}

package Communication;

import java.net.InetAddress;

/**
 * Classe représentant un message de connexion d'un utilisateur au serveur.
 * Hérite de la classe {@link Message} et inclut les informations de l'auteur.
 */
public class Connexion extends Message {

    /**
     * Constructeur de la classe Connexion.
     *
     * @param auteur L'utilisateur qui demande la connexion.
     */
    public Connexion(User auteur) {
        super(auteur);
    }

    /**
     * Traite le message côté serveur pour gérer la connexion d'un utilisateur.
     *
     * <p>Cette méthode effectue deux actions principales :
     * <ul>
     *   <li>Vérifie si l'utilisateur est valide en appelant {@link Serveur#verifierClient(User)}.</li>
     *   <li>Ajoute le client (adresse et port) à la liste des clients connectés via {@link Serveur#ajouterClientConnecte(InetAddress, int)}.</li>
     * </ul>
     * </p>
     *
     * @param serveur       L'instance du serveur qui traite le message.
     * @param clientAddress L'adresse IP du client qui tente de se connecter.
     * @param clientPort    Le port du client qui tente de se connecter.
     * @throws IllegalArgumentException Si la vérification du client échoue.
     */
    @Override
    public void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort) {
        // Vérifie si l'utilisateur est inscrit et valide
        serveur.verifierClient(getAuteur());

        // Ajoute le client à la liste des clients connectés
        serveur.ajouterClientConnecte(clientAddress, clientPort);
    }
}

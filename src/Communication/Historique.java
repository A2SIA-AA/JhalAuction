package Communication;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe représentant une demande ou une réponse d'historique d'enchères d'un utilisateur.
 * Hérite de {@link Message}.
 */
public class Historique extends Message {

    /**
     * Liste des détails d'historique des enchères de l'utilisateur.
     */
    protected LinkedList<HistoryDetails> historyDetailsList;

    /**
     * Constructeur de la classe Historique.
     * Initialise une nouvelle liste d'historique vide.
     *
     * @param auteur L'utilisateur qui demande l'historique des enchères.
     */
    public Historique(User auteur) {
        super(auteur);
        historyDetailsList = new LinkedList<HistoryDetails>();
    }

    /**
     * Récupère la liste des détails d'historique des enchères.
     *
     * @return Liste des objets {@link HistoryDetails} représentant l'historique des enchères.
     */
    public List<HistoryDetails> getHistoryDetailsList() {
        return historyDetailsList;
    }

    /**
     * Définit la liste des détails d'historique des enchères.
     *
     * @param historyDetailsList Liste des objets {@link HistoryDetails} à ajouter.
     * @throws IllegalArgumentException Si la liste fournie est vide ou null.
     */
    public void setHistoryDetailsList(List<HistoryDetails> historyDetailsList) {
        if (historyDetailsList == null || historyDetailsList.isEmpty()) {
            throw new IllegalArgumentException("Aucun historique trouvé");
        }
        this.historyDetailsList.addAll(historyDetailsList);
    }

    /**
     * Traite la demande d'historique côté serveur.
     * Cette méthode vérifie d'abord la validité de l'utilisateur, puis récupère l'historique des enchères
     * associés à cet utilisateur.
     *
     * @param serveur       L'instance du serveur qui traite la demande.
     * @param clientAddress L'adresse IP du client ayant envoyé la demande.
     * @param clientPort    Le port du client ayant envoyé la demande.
     */
    public void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort) {
        serveur.verifierClient(getAuteur());
        setHistoryDetailsList(serveur.demanderHistorique(getAuteur()));
    }

    /**
     * Retourne une représentation textuelle de l'objet Historique.
     *
     * @return Une chaîne de caractères représentant les détails de l'historique,
     *         ainsi que l'état de succès et les informations supplémentaires.
     */
    @Override
    public String toString() {

        return  historyDetailsList +
                ",\nsucces : " + succes +
                ",\ninfo : " + info;
    }
}


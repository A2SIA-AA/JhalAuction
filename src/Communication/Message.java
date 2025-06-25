package Communication;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;

/**
 * Classe abstraite représentant un message échangé entre le client et le serveur.
 * Cette classe sert de base pour d'autres types de messages, tels que l'inscription,
 * la connexion, et d'autres messages de communication.
 *
 * Elle contient des informations de base sur l'auteur du message, la date de création,
 * un statut de succès, et un champ d'information pour des messages supplémentaires.
 */
public abstract class Message implements Serializable {

    /** L'utilisateur qui envoie ce message */
    protected User auteur;

    /** La date et l'heure de création du message */
    protected LocalDateTime dateCreation;

    /** Indicateur de succès du message (par défaut à false) */
    protected boolean succes = false;

    /** Information complémentaire à propos du message */
    protected String info;

    /**
     * Constructeur de la classe Message.
     *
     * @param auteur L'utilisateur qui envoie ce message. Si l'auteur est non nul,
     *               une copie de l'utilisateur est effectuée pour le champ `auteur`.
     */
    public Message(User auteur) {
        this.dateCreation = LocalDateTime.now(); // Enregistre la date de création du message.
        if (auteur != null) {
            this.auteur = new User(auteur.getId(), auteur.getUsername(), auteur.getPassword()); // Crée une copie de l'utilisateur.
        }
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères du message.
     * Cette représentation inclut le nom de la classe, l'auteur, la date de création,
     * le statut de succès et l'information associée au message.
     *
     * @return Une chaîne représentant le message.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "auteur=" + auteur +
                ", dateCreation=" + dateCreation +
                ", succes=" + succes +
                ", info='" + info + '\'' +
                '}';
    }

    /**
     * Retourne l'utilisateur qui a envoyé ce message.
     *
     * @return L'utilisateur (auteur) du message.
     */
    public User getAuteur() {
        return auteur;
    }

    /**
     * Définit l'utilisateur (auteur) de ce message.
     *
     * @param auteur L'utilisateur à assigner au message.
     */
    public void setAuteur(User auteur) {
        this.auteur = auteur;
    }

    /**
     * Retourne la date de création du message.
     *
     * @return La date de création du message.
     */
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    /**
     * Définit la date de création du message.
     *
     * @param dateCreation La date de création à assigner au message.
     */
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    /**
     * Retourne si le message a été traité avec succès.
     *
     * @return true si le message a été traité avec succès, false sinon.
     */
    public boolean isSucces() {
        return succes;
    }

    /**
     * Définit si le message a été traité avec succès.
     *
     * @param succes true si le message a été traité avec succès, false sinon.
     */
    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    /**
     * Retourne des informations supplémentaires concernant le message.
     *
     * @return L'information supplémentaire associée au message.
     */
    public String getInfo() {
        return info;
    }

    /**
     * Définit les informations supplémentaires concernant le message.
     *
     * @param info L'information à associer au message.
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Méthode abstraite qui définit le traitement spécifique du message côté serveur.
     * Chaque type de message doit fournir son propre traitement via cette méthode.
     *
     * @param serveur       Le serveur qui reçoit et traite le message.
     * @param clientAddress L'adresse IP du client qui a envoyé le message.
     * @param clientPort    Le port du client qui a envoyé le message.
     */
    public abstract void traitementServeur(Serveur serveur, InetAddress clientAddress, int clientPort);
}

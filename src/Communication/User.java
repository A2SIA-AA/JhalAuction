package Communication;

import java.util.Objects;
import java.io.Serializable;

/**
 * Représente un utilisateur du système d'enchères.
 * Cette classe contient des informations sur l'utilisateur, telles que son ID, son nom d'utilisateur, et son mot de passe.
 * Elle inclut également des méthodes pour manipuler ces informations, ainsi que des méthodes pour la comparaison et le hachage des objets.
 */
public class User implements Serializable {
    protected int id;           // Identifiant unique de l'utilisateur
    protected String username;  // Nom d'utilisateur
    protected String password;  // Mot de passe de l'utilisateur

    /**
     * Constructeur de l'utilisateur avec un ID, un nom d'utilisateur et un mot de passe.
     *
     * @param id L'identifiant unique de l'utilisateur.
     * @param username Le nom d'utilisateur.
     * @param password Le mot de passe.
     */
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Constructeur de l'utilisateur sans ID, l'ID sera défini à 0 par défaut.
     *
     * @param username Le nom d'utilisateur.
     * @param password Le mot de passe.
     */
    public User(String username, String password){
        this(0, username, password);
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de l'utilisateur.
     *
     * @return Une chaîne contenant l'ID et le nom d'utilisateur.
     */
    public String toString(){
        return "User[id=" + id + ", username=" + username + "]";
    }

    /**
     * Retourne l'identifiant unique de l'utilisateur.
     *
     * @return L'ID de l'utilisateur.
     */
    public int getId(){
        return id;
    }

    /**
     * Définit l'identifiant unique de l'utilisateur.
     *
     * @param id L'ID de l'utilisateur.
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Retourne le nom d'utilisateur.
     *
     * @return Le nom d'utilisateur.
     */
    public String getUsername(){
        return username;
    }

    /**
     * Retourne le mot de passe de l'utilisateur.
     *
     * @return Le mot de passe de l'utilisateur.
     */
    public String getPassword(){
        return password;
    }

    /**
     * Définit le mot de passe de l'utilisateur.
     *
     * @param password Le mot de passe.
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Compare cet utilisateur à un autre objet. Deux utilisateurs sont considérés égaux s'ils ont le même nom d'utilisateur et le même mot de passe.
     *
     * @param o L'objet à comparer avec cet utilisateur.
     * @return true si les utilisateurs sont égaux, false sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return username.equals(user.username) && password.equals(user.password);
    }

    /**
     * Retourne un code de hachage pour cet utilisateur basé sur son nom d'utilisateur et son mot de passe.
     *
     * @return Le code de hachage de l'utilisateur.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}

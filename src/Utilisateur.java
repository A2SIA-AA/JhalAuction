import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import Communication.*;


public class Utilisateur extends User {
    protected DatagramSocket socket;
    protected InetAddress serverAddress;
    protected boolean debutEnchere = false;
    protected final int SERVER_PORT = 9876;
    protected Map<String, Produit> produits;
    protected ConcurrentLinkedQueue<Message> reponses;


    public boolean isDebutEnchere() {
        return debutEnchere;
    }

    public boolean estConnecte() {
        return socket != null && !socket.isClosed();
    }

    /**
     * Permet de fermer proprement un socket.
     */
    public void close() {
        if (estConnecte()) {
            socket.close();
        }
    }

    public Map<String, Produit> getProduits() {
        return this.produits;
    }


    /**
     * initialise un nouvel utilisateur: Initialise l'username, le mot de passe hashe et la connexion vers le serveur.
     *
     * @param username
     * @param motDePasse
     * @param addresseServeur
     */
    public Utilisateur(String username, String motDePasse, String addresseServeur) {
        super(username, motDePasse);

        try {
            socket = new DatagramSocket();  // crée un soocket UDP (attribue un port libre aléatoire à l'utilisateur)
            reponses = new ConcurrentLinkedQueue<Message>();
            produits = new HashMap<String, Produit>();
            serverAddress = InetAddress.getByName(addresseServeur);  // l'utilisateur se prépare à interagir avec un serveur en définissant son adresse IP
            System.out.println(username + " cre avec un port dynamique " + socket.getLocalPort());

            startListening();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void ecouterMiseAJour(MessageCallback miseAJourCallback) {
        new Thread(() -> {
            LinkedList<MiseAJour> miseAJourServeur = new LinkedList<>();
            try {
                while (estConnecte()) {
                    filterReponsesByType(MiseAJour.class, miseAJourServeur);
                    while (!miseAJourServeur.isEmpty()) {
                        MiseAJour miseAJour = miseAJourServeur.pollFirst();
                        Produit produit = miseAJour.getProduit();
                        produits.put(produit.getCarId(), produit);
                        miseAJourCallback.traiterMessage(miseAJour);
                    }
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void ecouterDebutEnchere(MessageCallback debutEnchereCallback) {
        new Thread(() -> {
            LinkedList<DebutEnchere> debutEncheres = new LinkedList<>();
            try {
                while (!isDebutEnchere()){
                    filterReponsesByType(DebutEnchere.class, debutEncheres);
                    while (!debutEncheres.isEmpty()){
                        DebutEnchere debutEnchere = debutEncheres.pollFirst();
                        this.debutEnchere = debutEnchere.isDebutEnchere();
                        debutEnchereCallback.traiterMessage(debutEnchere);
                    }
                    Thread.sleep(75);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Méthode permettant d'inscrire une personne en verifiant tout d'abord si le mdp entré est non vide.
     * Met les données que l'utilisateur a rentré dans la base de données.
     * Envoie au serveur les informations d'identification
     */
    public Inscription inscription() {
        if (password.isEmpty()) {
            throw new IllegalArgumentException("veuillez entrer un mot de passe");
        }
        sendMessage(new Inscription(this));
        Inscription message = filterReponsesByTypeWithRetry(Inscription.class, 10).getLast();
        return message;
    }

    /**
     * Methode permettant a 'utilisateur de se connecter au serveur. Si le mot de passe n'est pas vide,
     * le message est envoye au serveur.
     */
    public Connexion connexion() {
        if (password.isEmpty()) {
            throw new IllegalArgumentException("veuillez entrer un mot de passe");
        }
        sendMessage(new Connexion(this));
        Connexion message = filterReponsesByTypeWithRetry(Connexion.class, 10).getLast();
        return message;
    }

    /**
     * Envoie une requete au serveur pour recuperer tous les produits encore actifs de l'enchere.
     */
    public RecupererProduits recupererTousLesProduits() {
        sendMessage(new RecupererProduits(this));
        RecupererProduits message =  filterReponsesByTypeWithRetry(RecupererProduits.class, 10).getLast();
        for (Produit p: message.getProduits()){
            produits.put(p.getCarId(), p);
        }
        return message;
    }

    /**
     * Cette methode se contente de retourner une Exception car un utilisateur n'a pas le droit d'ajouter un produit.
     *
     * @param carId
     * @param description
     * @param prixInitial
     * @param pasEnchere
     * @param dureeEnchereSecondes
     */
    public AjoutProduit ajouterProduit(String usernameVendeur, String carId, String description, double prixInitial, int pasEnchere, int dureeEnchereSecondes) {
        throw new IllegalArgumentException("un utilisateur n'a pas le droit  d'ajouter des produits");
    }

    /**
     * Permet à un utilisateur de placer une enchère sur un produit spécifique
     * Met à jour son prix courant en notifiant le serveur.
     *
     * @param produit: la voiture sur laquelle placer l'enchere
     * @param montant: la nouvelle offre
     */
    public Encherir encherir(Produit produit, double montant) throws IllegalArgumentException {
        if (produit == null||!produit.isDisponible()) {
            throw new IllegalArgumentException("Le produit propose n'existe pas ou n'est plus disponible");
        }
        if (!debutEnchere) {
            throw new IllegalArgumentException("L'enchere n'a pas encore debute");
        }
        sendMessage(new Encherir(this, produit.encherir(this, montant)));
        Encherir message = filterReponsesByTypeWithRetry(Encherir.class, 10).getLast();
        return message;
    }

    public Encherir encherir(String carId, double montant) throws IllegalArgumentException {
        return this.encherir(produits.get(carId), montant);
    }

    /**
     * Envoie une reclamation au serveur sur un produit specifique;
     * @param produit: produit a reclamer
     */
    public Reclamation reclamer(Produit produit) {
        return this.reclamer(produit.getCarId());
    };

    /**
     * Envoie une reclamation au serveur sur un produit specifique;
     * @param carId: identifiant du produit a reclamer
     */
    public Reclamation reclamer(String carId) {
        sendMessage(new Reclamation(this, carId));
        Reclamation message = filterReponsesByTypeWithRetry(Reclamation.class, 10).getLast();
        return message;
    }

    /**
     * Envoie une demande d'historique au serveur;
     */
    public Historique demanderHistorique() {
        sendMessage(new Historique(this));
        Historique message = filterReponsesByTypeWithRetry(Historique.class, 10).getLast();
        return message;
    }

    /**
     * Démarre un thread qui écoute en continue les messages entrants sur le socket UDP tant que le socket n'est pas fermé
     */
    protected void startListening() {
        //Lance un nouveau Thread, cela permet de ne pas bloquer le thread principal de l'application.
        new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    //Réception des données
                    byte[] receiveData = new byte[8192];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);

                    //Désérialisation du message (données reçues converties en objet Message
                    ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    Message msg = (Message) ois.readObject();
                    reponses.add(msg);
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        System.err.println(username + " Receiving error: " + e.getMessage());
                    }
                }
            }
        }).start();
    }

    public <T extends Message> List<T> filterReponsesByTypeWithRetry(Class<T> type, int retry) {
        List<T> filtered = new ArrayList<>();
        for (int i=0; i<retry; i++){
            filterReponsesByType(type, filtered);
            try {
                if (!filtered.isEmpty()){
                    break;
                } else{
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return filtered;
    }

    // Generic function to filter messages by type
    protected <T extends Message> void filterReponsesByType(Class<T> type, List<T> filtered) {
        Iterator<Message> iterator = reponses.iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (type.isInstance(message)) {
                filtered.add(type.cast(message));
                iterator.remove();
            }
        }
    }

    /**
     * Envoie un objet sérialisé de type Message à un serveur via un socket UDP
     *
     * @param message
     */
    protected void sendMessage(Message message) {
        try {
            //Stock les données sous forme de tableau de bytes en mémoire
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //Sérialise l'objet message en format binaire qui pourra être transmis via le réseau
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);

            byte[] sendData = baos.toByteArray();

            // DatagramPacket : unité de transmission pour les communications UDP.
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);    // Send to server's port

            socket.send(sendPacket);

        } catch (IOException e) {
            System.err.println(username + " Sending error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}



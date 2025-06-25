import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Communication.*;


public class DataBase {
    private Connection connection;
    private static final String URL = "jdbc:sqlite:auction.db";

    /**
     * Constructeur de la base de données
     */
    public DataBase() {
        try {
            connection = DriverManager.getConnection(URL);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            System.out.println("Database connection established successfully.");
            initializeTables();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * initialisation des tables de la basse de données: table Users qui contient un id, username et mdp d'un utiisateur
     * Table Cars qui contient tous les produits et leurs informations
     * Table Bids qui contient toutes les offres qui ont été placé sur un Produit donc elle contient l'id de la voiture, le mail de l'encherisseur, le montant et la date de l'enchere
     * Table History qui contient les id des produits sur lesquelles un utilisateur a encherit et le temps de son enchère
     * @throws SQLException
     */
    private void initializeTables() throws SQLException {
        String createCarsTable = """
                CREATE TABLE IF NOT EXISTS cars (
                    car_id TEXT PRIMARY KEY,
                    description TEXT NOT NULL,
                    starting_price REAL NOT NULL,
                    current_price REAL NOT NULL,
                    seller_mail TEXT NOT NULL,
                    user_mail TEXT,
                    increment INTEGER NOT NULL,
                    duration INTEGER NOT NULL,
                    status TEXT NOT NULL DEFAULT 'ACTIVE',
                    created_at TEXT NOT NULL DEFAULT (datetime('now')),
                    FOREIGN KEY (seller_mail) REFERENCES users(username),
                    FOREIGN KEY (user_mail) REFERENCES users(username),
                    CHECK (status IN ('ACTIVE', 'SOLD'))
                )""";

        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )""";

        String createBidsTable = """
                CREATE TABLE IF NOT EXISTS bids (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    car_id TEXT NOT NULL,
                    user_mail TEXT NOT NULL,
                    bid_amount REAL NOT NULL,
                    bid_time TEXT NOT NULL DEFAULT (datetime('now')),
                    FOREIGN KEY (car_id) REFERENCES cars(car_id),
                    FOREIGN KEY (user_mail) REFERENCES users(username)
                )""";
        String historyTable = """
                CREATE TABLE IF NOT EXISTS history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_mail TEXT NOT NULL,
                    car_id TEXT NOT NULL,
                    interaction_time TEXT NOT NULL DEFAULT (datetime('now')),
                    bid_amount REAL NOT NULL,
                    FOREIGN KEY (user_mail) REFERENCES users(username),
                    FOREIGN KEY (car_id) REFERENCES cars(car_id)
                )""";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createCarsTable);
            stmt.execute(createBidsTable);
            stmt.execute(historyTable);
            System.out.println("Database tables initialized successfully.");
        }
    }

    /**
     * Cette fonction ferme la connexion à la base de données si elle est encore ouverte, tout en gérant les éventuelles exceptions liées à la fermeture.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cette fonction vérifie si la connexion à la base de données est valide et ouverte ou non.
     * @return boolean qui prend true si la connection est valide et ouverte et false sinon ou en cas d'erreur
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * ajouter un utilisateur avec son username et son mot de passe à la table Users de la base de données
     * @param username
     * @param password
     * @throws SQLException
     */
    public void addUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }
    }

    /**
     * ajouter un produit à la table Cars de la base de données
     * @param produit
     * @throws SQLException
     */
    public void addCar(Produit produit) throws SQLException {
        String sql = """
                INSERT INTO cars (car_id, description, starting_price, current_price, increment, duration, seller_mail)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, produit.getCarId());
            pstmt.setString(2, produit.getDescription());
            pstmt.setDouble(3, produit.getPrixInitial());
            pstmt.setDouble(4, produit.getPrixCourant());
            pstmt.setInt(5, produit.getPasEnchere());
            pstmt.setInt(6, produit.getDureeEnchereSecondes());
            pstmt.setString(7, produit.getUserVendeur());
            pstmt.executeUpdate();
        }
    }

    /**
     * mettre à jour les informations d'un produit dans la base de données après qu'un utilisateur place une enchère
     * @param produit
     * @throws SQLException
     */
    public void updateCar(Produit produit) throws SQLException {
        String sql = """
                UPDATE cars SET
                    current_price = ?,
                    user_mail = ?,
                    status = ?,
                    description = ?,
                    starting_price = ?,
                    increment = ?,
                    duration = ?,
                    seller_mail = ?
                    WHERE car_id = ?
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, produit.getPrixCourant());
            pstmt.setString(2, produit.getUserAcheteur());
            pstmt.setString(3, produit.isDisponible() ? "ACTIVE" : "SOLD");
            pstmt.setString(4, produit.getDescription());
            pstmt.setDouble(5, produit.getPrixInitial());
            pstmt.setInt(6, produit.getPasEnchere());
            pstmt.setInt(7, produit.getDureeEnchereSecondes());
            pstmt.setString(8, produit.getUserVendeur());
            pstmt.setString(9, produit.getCarId());
            pstmt.executeUpdate();
        }
    }

    /**
     * obtenir le produit à partir de son ID afin de vérifier si le produit existe déjà ou pas dans la base de données (cars) avant de le rajouter
     * @param carID
     * @return un produit avec tous ses attributs (toutes les informations correspondantes)
     * @throws SQLException
     */
    public Produit getCar(String carID) throws SQLException {
        String sql = "SELECT * FROM cars WHERE car_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, carID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Produit produit = new Produit(
                            rs.getString("car_id"),
                            rs.getString("description"),
                            rs.getDouble("starting_price"),
                            rs.getDouble("current_price"),
                            rs.getInt("increment"),
                            rs.getInt("duration"),
                            rs.getString("status").equals("ACTIVE"),
                            rs.getString("seller_mail"),
                            rs.getString("user_mail"));
                    return produit;
                }
            }
        }
        return null;
    }

    /**
     * ajouter des offres faites sur un produit aux tables Bids et History dans la base de données en enregistrant son email et le montant avec lequel il a encherit
     * Ainsi que mettre à jour les informations du produit dans la table Cars
     * @param carId : Id de la voiture sur laquelle on place une enchère
     * @param user_mail : le mail de l'utilisateur qui fait l'offre
     * @param amount : le montant de l'offre
     * @throws SQLException
     */
    public void addBid(String carId, String user_mail, double amount) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String checkSql = "SELECT status, current_price, increment FROM cars WHERE car_id = ? AND status = 'ACTIVE'";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, carId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    throw new SQLException("Car is not available for bidding");
                }

                double currentPrice = rs.getDouble("current_price");
                double increment = rs.getDouble("increment");
                if (amount < currentPrice + increment) {
                    throw new SQLException("Bid amount must be higher than current price");
                }
            }
            String updateSql = "UPDATE cars SET current_price = ?, user_mail = ? WHERE car_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setDouble(1, amount);
                updateStmt.setString(2, user_mail);
                updateStmt.setString(3, carId);
                updateStmt.executeUpdate();
            }
            String bidSql = "INSERT INTO bids (car_id, user_mail, bid_amount, bid_time) VALUES (?, ?, ?, datetime('now'))";
            try (PreparedStatement bidStmt = connection.prepareStatement(bidSql)) {
                bidStmt.setString(1, carId);
                bidStmt.setString(2, user_mail);
                bidStmt.setDouble(3, amount);
                bidStmt.executeUpdate();
            }

            String historySql = "INSERT INTO history (user_mail, car_id, bid_amount, interaction_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement historyStmt = connection.prepareStatement(historySql)) {
                historyStmt.setString(1, user_mail);
                historyStmt.setString(2, carId);
                historyStmt.setDouble(3, amount);

                String currentTime = java.time.LocalDateTime.now().toString();
                historyStmt.setString(4, currentTime);

                historyStmt.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * obtenir des informations d'un utilisateur à partir de son username dans la base de données (Users) pour verifier s'il existe déjà ou pas lors de son insription
     * @param username : le username de l'utilisateur pour lequel on veut obtenir les informations
     * @return
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * obtenir une liste des produits dont leurs enchères sont disponibles (ACTIVE)
     * @return une liste de tous les produits sur lesquels les enchères sont toujours actives
     */
    public List<Produit> getAllCars() {
        List<Produit> allCarsActive = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE status = 'ACTIVE'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getString("car_id"),
                        rs.getString("description"),
                        rs.getDouble("starting_price"),
                        rs.getDouble("current_price"),
                        rs.getInt("increment"),
                        rs.getInt("duration"),
                        rs.getString("status").equals("ACTIVE"),
                        rs.getString("seller_mail"),
                        rs.getString("user_mail")
                );
                allCarsActive.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving cars: " + e.getMessage());
            e.printStackTrace();
        }
        return allCarsActive;
    }

    /**
     * Retourne la liste des produits disponibles de la data base
     * @return List des Produits
     */
    public List<Produit> getAvailableCars() {
        List<Produit> cars = new ArrayList<Produit>();
        for (Produit p: getAllCars()){
            if(p.isDisponible()){
                cars.add(p);
            }
        }
        return cars;
    }

    /**
     * Obtenir le montant et la date d'enchèrissement d'un utilisateur qui fait une reclamation sur un produit qu'il a perdu ainsi que ceux du gagneur
     * @param currentUserMail : represente le mail de l'utilisateur qui fait la reclamation
     * @param carId : represente l'id du produit sur lequel il veut faire la reclamation
     * @return : un dictionnaire ayant comme labels winner_time, winner_amount, current_bidder_mail, current_bidder_amount et comme values les montants et les dates d'enchères du gagneur et le l'utilisateur
     */
    public ReclamationDetails makeReclamation(String currentUserMail, String carId) {
        //get the winner's history
        String query = """
   WITH 
LatestHistoryBid AS (
    SELECT 
        user_mail AS winner_mail, 
        bid_amount AS winner_amount, 
        interaction_time AS winner_time
    FROM history 
    WHERE car_id = ? 
    ORDER BY interaction_time DESC   
    LIMIT 1
),
LatestCurrentUserBid AS (
    SELECT 
        user_mail AS current_bidder_mail, 
        bid_amount AS current_bid_amount, 
        bid_time AS current_bid_time
    FROM bids 
    WHERE car_id = ? AND user_mail = ?
    ORDER BY bid_time DESC
    LIMIT 1
)
SELECT 
    COALESCE(h.winner_mail, 'No previous winner') AS winner_mail,
    COALESCE(h.winner_amount, 0.0) AS winner_amount,
    COALESCE(h.winner_time, 'N/A') AS winner_time,
    COALESCE(c.current_bidder_mail, 'No current bidder') AS current_bidder_mail,
    COALESCE(c.current_bid_amount, 0.0) AS current_bid_amount,
    COALESCE(c.current_bid_time, 'N/A') AS current_bid_time
FROM 
    LatestHistoryBid h
CROSS JOIN 
    LatestCurrentUserBid c
        """;
        // coalsce retourne la premiere valeur non NULL parmi une liste d'expressions
        //DESC (pour trier) le dernier qui a encherit sera en premier
        //cross join c'est intersection entre winner object w currentBid qu'on a crée pour regrouper toutes les infos ensemble
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, carId);
            pstmt.setString(2, carId);
            pstmt.setString(3, currentUserMail);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ReclamationDetails(
                            rs.getString("winner_mail"),
                            rs.getDouble("winner_amount"),
                            rs.getString("winner_time"),
                            rs.getString("current_bidder_mail"),
                            rs.getDouble("current_bid_amount"),
                            rs.getString("current_bid_time")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Reclamation query error: " + e.getMessage());
        }

        return null;
    }


    /**
     * obtenir l'historique d'un utilisateur avec toutes les informations correspondates à son offre ( id de la voiture sur laquelle il a encherit, le montant et la date)
     * @param user_mail : le mail de l'utilisateur qui fait la demande d'historique
     * @return une liste de dictionnaire tel que chaque dictionnaire admet comme label id de l'utilisateur, id du produit, le montant et la date d'encherissement et comme value les valeurs correspondates
     */
    public List<HistoryDetails> getHistoriqueByUsername(String user_mail) {
        List<HistoryDetails> historique = new ArrayList<>();
        String sql = "SELECT id, car_id, interaction_time, bid_amount FROM history WHERE user_mail = ?";

        System.out.println("Retrieving history for user: " + user_mail);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, user_mail);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return historique;
                }

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String carId = rs.getString("car_id");
                    String interactionTime = rs.getString("interaction_time");
                    double bidAmount = rs.getDouble("bid_amount");

                    historique.add(new HistoryDetails(id, carId, interactionTime, bidAmount));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historique;
    }
}


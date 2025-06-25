# JHAL-AUCTION  
Logiciel de **ventes aux enchères en ligne** – Modélisation & Développement Java  
*(GM4 – Programmation Orientée Objet Avancée)*  

> **Auteurs :** Ait Taleb Assia - Dicko Loraine - Doubli Hoda - Farhat Joëlle  
> **Date :** Décembre 2024  

---

## 1 . Objectif

Simuler une salle des ventes « ascendante » où :

* le **commissaire-priseur** dépose des véhicules, ouvre/ferme les enchères ;
* les **utilisateurs** s’inscrivent, enchérissent en temps réel, consultent historique & réclamations ;
* la **base SQLite** persiste utilisateurs, voitures, offres et historiques.  

Le tout s’appuie sur :

* Java 17  
* **JavaFX** (interface GUI)  
* Threads & `java.net.DatagramSocket` 
* Hash SHA-256 pour les mots de passe  

---

## 2 . Arborescence du dépôt


Ce dépôt contient **l’implémentation Java / JavaFX** complète de notre logiciel de ventes aux enchères
présenté dans le rapport & le README principal.  
Vous trouverez ci-dessous un aide-mémoire rapide :

| Dossier / fichier | Rôle principal |
|-------------------|----------------|
| `Communication/`  | *Package* qui regroupe **tous les messages UDP** (sérialisation, traitement serveur) |
| `Main.java`       | Point d’entrée |
| `Commissaire.java`| commissaire-priseur (ajout de voitures, lancement enchère) |
| `ServeurEnchere.java` | Serveur UDP : écoute, diffusion, accès BDD |
| `Utilisateur.java`| Client (inscription, connexion, enchères) |
| `Terminale.java`  | Petit shell de test permettant d’envoyer des messages bruts |
| `DataBase.java`   | Couche **JDBC / SQLite** : création & requêtes (`users`, `cars`, `bids`, `history`) |
| `fctUtile.java`   | Méthodes utilitaires (hash SHA-256, conversions, timers, etc.) |
| `menuCo.fxml` + `menuCoController.java` | **Menu principal** JavaFX après connexion |
| `choix_Client.fxml` + `choix_ClientController.java` | Sélection du rôle (Acheteur / Commissaire) |
| `inscription.fxml` + `inscriptionController.java` | Formulaire de **création de compte** |
| `OffresDispo.fxml` + `OffresDispoController.java` | Catalogue en temps réel (ListView + enchères) |
| `Vendre.fxml` + `VendreController.java` | Formulaire d’**ajout de voiture** (commissaire) |
| `Reclamation.fxml` + `ReclamationController.java` | Fenêtre de réclamation (comparatif enchères) |
| `PrintCallback.java` / `MessageCallback.java` | Helpers pour l’affichage console & GUI |


---

## 3 . Fonctionnalités clés

### Sécurité
* SHA-256 pour les mots de passe  
* Liste blanche des clients connectés → seules les IP reconnues peuvent enchérir.

### Réseau
* Protocole **UDP**.  
* Chaque requête = objet `Message` sérialisé (polymorphisme → `traitementServeur`).  
* Diffusion automatique sur **multicast** pour mettre à jour tous les GUI.

### Base de données
| Table       | Champs essentiels | Description |
|-------------|------------------|-------------|
| `users`     | `id`, `username`, `password` | Authentification |
| `cars`      | `car_id`, `description`, `starting_price`, `current_price`, `status` | Lots à vendre |
| `bids`      | `car_id`, `user_mail`, `amount`, `bid_date` | Toutes les offres |
| `history`   | `user_mail`, `car_id`, `amount`, `bid_time` | Traçabilité Many-to-Many |

### Interface JavaFX
* **Commissaire** : Liste des voitures, formulaire d’ajout, bouton « Démarrer enchère ».  
* **Acheteur** : Catalogue en temps réel, champs (`ID`, `Offre €`), historique & réclamations.  
* `PasswordField` pour saisie masquée.

---

## 4 . Mise en route

### 4.1  Prérequis
```bash
Java 17+
make serveur        # démarre ServeurEnchere (SQLite crée/maj au besoin)
make commissaire    # lance un commissaire
make utilisateur    # lance un utilisateur
make app            # lance l'interface graphique
```
---

> *« Vendez, enchérissez, gagnez… le tout en Java !»*

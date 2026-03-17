package users;

import gestionBD.UserBD;
import database.DBDriver;

public class UserManager {
    private UserBD userBD;
    public static final String CLE_ADMIN = "2026";


    public UserManager() {
        // Utiliser le même DBDriver partout
        DBDriver db = new DBDriver(
                "jdbc:mysql://localhost:3306/gestion_stock",
                "root",
                "My-19-Ql"  // Votre mot de passe
        );
        userBD = new UserBD(db);
    }

    /*Vérifie si l'identifiant et mot de passe sont corrects
     */
    public boolean login(String username, String password) {
        return userBD.checkLogin(username, password);
    }

    /*Retourne le rôle de l'utilisateur
     */
    public String getRole(String username) {
        return userBD.getRole(username);
    }

    /*Vérifie si l'utilisateur est admin
     */
    public boolean isAdmin(String username) {
        String role = getRole(username);
        return role != null && role.equalsIgnoreCase("admin");
    }

    /* Ajoute un nouvel utilisateur (uniquement pour l'admin)
     */

    public boolean creerUtilisateur(String username, String password) {
        return userBD.ajouterUtilisateur(username, password, "user");
    }

    public boolean creerUtilisateur(String username, String password, String role, String cle) {


        if (role.equalsIgnoreCase("admin")) {
            if (cle == null || !cle.equals(CLE_ADMIN)) {
                System.out.println("Création refusée : clé admin incorrecte !");
                return false;
            }
        } else {
            role = "user"; // sécurité : tout autre rôle devient "user"
        }

        return userBD.ajouterUtilisateur(username, password, role);
    }

}

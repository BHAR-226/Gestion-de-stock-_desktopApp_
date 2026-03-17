package gestionBD;

import database.DBDriver;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserBD {
    private DBDriver db;

    // Constructeur avec DBDriver
    public UserBD(DBDriver db) {
        this.db = db;
    }

    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";

        try {
            ResultSet rs = db.sendQuery(sql, username, password);
            return rs != null && rs.next();

        } catch (SQLException e) {
            System.err.println("Erreur checkLogin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getRole(String username) {
        String sql = "SELECT role FROM users WHERE username=?";

        try {
            ResultSet rs = db.sendQuery(sql, username);
            if (rs != null && rs.next()) {
                return rs.getString(1);
            }

        } catch (SQLException e) {
            System.err.println("Erreur getRole: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean ajouterUtilisateur(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try {
            // Vérifier d'abord si l'utilisateur existe déjà
            String checkSql = "SELECT * FROM users WHERE username=?";
            ResultSet rs = db.sendQuery(checkSql, username);
            if (rs != null && rs.next()) {
                System.out.println("Utilisateur " + username + " existe déjà");
                return false;
            }

            // Insérer le nouvel utilisateur
            int rows = db.sendUpdate(sql, username, password, role);
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur ajouterUtilisateur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
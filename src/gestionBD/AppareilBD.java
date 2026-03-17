package gestionBD;

import database.DBDriver;
import marchandise.Appareil;
import marchandise.Caracteristic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppareilBD {
    private DBDriver db;

    public AppareilBD(DBDriver db) {
        this.db = db;
    }

    // CREATE
    public boolean ajouterAppareil(Appareil a) {
        String sql = "INSERT INTO appareil (id, marque, modele, type, prix, quantite, seuil_alerte, " +
                "ram, memoire, cpu, gpu, size) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int rows = db.sendUpdate(sql,
                a.getId(),
                a.getMarque(),
                a.getModele(),
                a.getType(),
                a.getPrix(),
                a.getQuantite(),
                a.getSeuilAlerte(),
                a.getCaracteristic().getRam(),
                a.getCaracteristic().getMemoire(),
                a.getCaracteristic().getCpu(),
                a.getCaracteristic().getGpu(),
                a.getCaracteristic().getSize()
        );

        return rows > 0;
    }

    // READ (un seul appareil)
    public Appareil getAppareilById(String id) {
        try {
            ResultSet rs = db.sendQuery("SELECT * FROM appareil WHERE id = ?", id);

            if (rs != null && rs.next()) {
                Caracteristic c = new Caracteristic(
                        rs.getString("ram"),
                        rs.getString("memoire"),
                        rs.getString("cpu"),
                        rs.getString("gpu"),
                        rs.getString("size")
                );

                return new Appareil(
                        rs.getString("id"),
                        rs.getString("type"),
                        rs.getString("marque"),
                        rs.getString("modele"),
                        rs.getDouble("prix"),
                        rs.getInt("quantite"),
                        rs.getInt("seuil_alerte"),
                        c
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur SELECT BY ID : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    // READ

    public List<Appareil> rechercherAppareils(String type, String marque,
                                              Double prixMax, Boolean enAlerte) {
        StringBuilder sql = new StringBuilder("SELECT * FROM appareil WHERE 1=1");
        List<Object> params = new ArrayList<>();
        List<Appareil> liste = new ArrayList<>();

        if (type != null && !type.isEmpty() && !type.equals("Tous")) {
            sql.append(" AND type = ?");
            params.add(type);
        }
        if (marque != null && !marque.isEmpty() && !marque.equals("Toutes")) {
            sql.append(" AND marque = ?");
            params.add(marque);
        }
        if (prixMax != null) {
            sql.append(" AND prix <= ?");
            params.add(prixMax);
        }
        if (enAlerte != null && enAlerte) {
            sql.append(" AND quantite <= seuil_alerte");
        }
        // Ajouter d'autres critères...

        try {
            ResultSet rs = db.sendQuery(sql.toString(), params.toArray());
            // Parcours et construction de la liste...
            while (rs != null && rs.next()) {
                Caracteristic c = new Caracteristic(
                        rs.getString("ram"),
                        rs.getString("memoire"),
                        rs.getString("cpu"),
                        rs.getString("gpu"),
                        rs.getString("size")
                );

                Appareil a = new Appareil(
                        rs.getString("id"),
                        rs.getString("type"),
                        rs.getString("marque"),
                        rs.getString("modele"),
                        rs.getDouble("prix"),
                        rs.getInt("quantite"),
                        rs.getInt("seuil_alerte"),
                        c
                );

                liste.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return liste;
    }

    // UPDATE
    public boolean modifierAppareil(Appareil a) {
        String sql = "UPDATE appareil SET marque=?, modele=?, type=?, prix=?, quantite=?, seuil_alerte=?, " +
                "ram=?, memoire=?, cpu=?, gpu=?, size=? WHERE id=?";

        int rows = db.sendUpdate(sql,
                a.getMarque(),
                a.getModele(),
                a.getType(),
                a.getPrix(),
                a.getQuantite(),
                a.getSeuilAlerte(),
                a.getCaracteristic().getRam(),
                a.getCaracteristic().getMemoire(),
                a.getCaracteristic().getCpu(),
                a.getCaracteristic().getGpu(),
                a.getCaracteristic().getSize(),
                a.getId()
        );

        return rows > 0;
    }

    // DELETE
    public boolean supprimerAppareil(String id) {
        int rows = db.sendUpdate("DELETE FROM appareil WHERE id=?", id);
        return rows > 0;
    }
}
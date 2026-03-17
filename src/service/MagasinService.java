package service;

import database.DBDriver;
import gestionBD.AppareilBD;
import marchandise.Appareil;

import java.sql.SQLException;
import java.util.List;

public class MagasinService {

    private AppareilBD appareilBD;
    DBDriver db = new DBDriver("jdbc:mysql://localhost:3306/gestion_stock", "root", "My-19-Ql");

    public MagasinService() {
        appareilBD = new AppareilBD(db);
    }

    // AJOUT
    public boolean ajouterAppareil(Appareil appareil) {

        // règles métier
        if (appareil == null) {
            System.out.println("Appareil invalide !");
            return false;
        }

        if (appareil.getPrix() < 0) {
            System.out.println("Le prix ne peut pas être négatif !");
            return false;
        }

        if (appareil.getQuantite() < 0) {
            System.out.println("La quantité ne peut pas être négative !");
            return false;
        }

        return appareilBD.ajouterAppareil(appareil);
    }

    // MODIFICATION
    public boolean modifierAppareil(Appareil appareil) {

        if (appareil == null) {
            System.out.println("Appareil introuvable !");
            return false;
        }

        if (appareil.getPrix() < 0 || appareil.getQuantite() < 0) {
            System.out.println("Données invalides !");
            return false;
        }

        return appareilBD.modifierAppareil(appareil);
    }

    // SUPPRESSION
    public boolean supprimerAppareil(String id) {
        return appareilBD.supprimerAppareil(id);
    }

    // RECHERCHE

    public Appareil rechercherParId(String id) {

        return appareilBD.getAppareilById(id);
    }


    // LISTER LES APPAREILS
    //par type
    public List<Appareil> listerAppareilsParType(String type) throws SQLException{
        return appareilBD.rechercherAppareils(type,null,null,null);
    }
    // par marque
    public List<Appareil> listerAppareilsParMarque(String marque) throws SQLException{
        return appareilBD.rechercherAppareils(null,marque,null,null);
    }
    // en alerte
    public List<Appareil> appareilsEnAlerte(boolean alert) throws SQLException {
        return appareilBD.rechercherAppareils(null,null,null,alert);
    }
    // type et prixmax
    public List<Appareil> listerAppareils(String type, double prixMax) throws SQLException {
        return appareilBD.rechercherAppareils(type,null, prixMax, null);
    }
    //type et marque
    public List<Appareil> listerAppareils(String type, String marque) throws SQLException {
        return appareilBD.rechercherAppareils(type, marque,null,null);
    }
    //type marque et prixmax
    public List<Appareil> listerAppareils(String type, String marque, double prixMax) throws SQLException {
        return appareilBD.rechercherAppareils(type, marque,prixMax,null);
    }
    //touts les appareils
    public List<Appareil> listerAppareils() throws SQLException {
        return appareilBD.rechercherAppareils(null,null,null,null);
    }

}
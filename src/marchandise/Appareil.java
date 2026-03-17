package marchandise;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Scanner;

//Représente un appareil en stock.
public class Appareil {
    private String id;
    private String type;
    private String marque;
    private String modele;
    private double prix;
    private int quantite;
    private int seuilAlerte;
    private Caracteristic caracteristic;


    // Constructeurs
    public Appareil() {}

    public Appareil(String id, String type, String marque, String modele,
                    double prix, int quantite, int seuilAlerte,
                    Caracteristic caracteristic) {
        this.id = id;
        this.type = type;
        this.marque = marque;
        this.modele = modele;
        this.setPrix(prix);
        this.setQuantite(quantite);
        this.setSeuilAlerte(seuilAlerte);
        this.caracteristic = caracteristic;

    }

    // Getters / Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) {
        if (prix < 0) throw new IllegalArgumentException("Prix ne peut pas être négatif");
        this.prix = prix;
    }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) {
        if (quantite < 0) throw new IllegalArgumentException("Quantité ne peut pas être négative");
        this.quantite = quantite;
    }

    public int getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(int seuilAlerte) {
        if (seuilAlerte < 0) throw new IllegalArgumentException("Seuil d'alerte ne peut pas être négatif");
        this.seuilAlerte = seuilAlerte;
    }

    public Caracteristic getCaracteristic() { return caracteristic; }
    public void setCaracteristic(Caracteristic caracteristic) { this.caracteristic = caracteristic; }


    //Utilitaires
    @Override
    public String toString() {
        return "Appareil{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", marque='" + marque + '\'' +
                ", modele='" + modele + '\'' +
                ", prix=" + prix +
                ", quantite=" + quantite +
                ", seuilAlerte=" + seuilAlerte +
                ", caracteristic=" + (caracteristic != null ? caracteristic.toString() : "null") +

                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Appareil appareil = (Appareil) o;
        return Objects.equals(id, appareil.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

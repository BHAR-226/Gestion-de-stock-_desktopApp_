package interface_graphique;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import marchandise.Appareil;
import service.MagasinService;

public class UserUI extends JFrame {

    private JTable appareilsTable;  // Tableau pour afficher les appareils
    private DefaultTableModel tableModel;  // Modèle de données du tableau
    private JComboBox<String> typeComboBox, marqueComboBox;  // Listes déroulantes
    private JTextField prixMinField, prixMaxField, rechercheField;  // Champs texte
    private JButton rechercherButton, resetButton, ajouterPanierButton;  // Boutons
    private JLabel panierLabel;  // Label pour afficher le panier
    private List<String> panier = new ArrayList<>();  // Liste des articles dans le panier
    private MagasinService magasinService;
    private JCheckBox alerteCheckBox;

    public UserUI() throws SQLException {
        setTitle("Interface Utilisateur - Choix d'Appareils");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// pour que l'application se termine après la fermeture de la fenetre        setSize(1000, 700);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        magasinService = new MagasinService();

        initUI();
        chargerDonnees(); // Affiche tous les appareils dès le lancement
        setVisible(true);
    }

    private void initUI() {
        // Panel principal avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===================== FILTRES =====================
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtres de Recherche"));
        filterPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);//Marges internes
        gbc.fill = GridBagConstraints.HORIZONTAL; //  Etire horizontalement

        // Recherche par ID
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Rechercher ID:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        rechercheField = new JTextField();
        filterPanel.add(rechercheField, gbc);

        // Type
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        filterPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeComboBox = new JComboBox<>();
        filterPanel.add(typeComboBox, gbc);

        // Marque
        gbc.gridx = 2;
        filterPanel.add(new JLabel("Marque:"), gbc);
        gbc.gridx = 3;
        marqueComboBox = new JComboBox<>();
        filterPanel.add(marqueComboBox, gbc);

        // Prix max
        gbc.gridx = 0; gbc.gridy = 2;
        filterPanel.add(new JLabel("Prix max (€):"), gbc);
        gbc.gridx = 1;
        prixMaxField = new JTextField();
        filterPanel.add(prixMaxField, gbc);

        // Appareils en alerte

        gbc.gridx = 2;
        alerteCheckBox = new JCheckBox("Afficher uniquement appareils en alerte");
        gbc.gridwidth = 2;
        filterPanel.add(alerteCheckBox, gbc);

        // Boutons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        rechercherButton = new JButton("Rechercher");
        resetButton = new JButton("Réinitialiser");

        stylizeButton(rechercherButton, new Color(50, 72, 186));
        stylizeButton(resetButton, new Color(95, 25, 168));
        rechercherButton.setForeground(Color.BLACK);
        resetButton.setForeground(Color.BLACK);

        buttonPanel.add(rechercherButton);
        buttonPanel.add(resetButton);
        filterPanel.add(buttonPanel, gbc);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // ===================== TABLEAU =====================
        String[] colonnes = {"ID","Type", "Marque", "Modèle", "Prix (dh)", "Stock", "Description"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        appareilsTable = new JTable(tableModel);
        appareilsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appareilsTable.setRowHeight(25);

        // Centrer les données dans les cellules
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < appareilsTable.getColumnCount(); i++) {
            appareilsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        // on met le tableau dans un scrollpane pour le defillement
        JScrollPane scrollPane = new JScrollPane(appareilsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Appareils Disponibles"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
// Création du PANEL INFÉRIEUR (PANIER + BOUTONS)
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        // Panier
        JPanel panierPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panierLabel = new JLabel("Panier: 0 article(s)");
        panierLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panierPanel.add(panierLabel);

        bottomPanel.add(panierPanel, BorderLayout.WEST);

        // Boutons d'action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        ajouterPanierButton = new JButton("Ajouter au Panier");
        JButton voirPanierButton = new JButton("Voir le Panier");
        JButton retourButton = new JButton("Retour");

        stylizeButton(ajouterPanierButton, new Color(31, 149, 31));
        stylizeButton(voirPanierButton, new Color(203, 116, 13));
        stylizeButton(retourButton, new Color(192, 190, 190));

        ajouterPanierButton.setForeground(Color.BLACK);
        voirPanierButton.setForeground(Color.BLACK);
        retourButton.setForeground(Color.BLACK);

        actionPanel.add(ajouterPanierButton);
        actionPanel.add(voirPanierButton);
        actionPanel.add(retourButton);

        bottomPanel.add(actionPanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ===================== ÉVÉNEMENTS =====================
        //rechercer
        rechercherButton.addActionListener(e -> {
            try {
                rechercherAppareils();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        // reinitialiser
        voirPanierButton.addActionListener(e -> afficherPanier());

        resetButton.addActionListener(e -> {
            typeComboBox.setSelectedIndex(0);
            marqueComboBox.setSelectedIndex(0);
            rechercheField.setText("");
            prixMaxField.setText("");
            alerteCheckBox.setSelected(false);
            try {
                chargerDonnees();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

    // Ajout au panier
        ajouterPanierButton.addActionListener(e -> ajouterAuPanier());

    // Voir le panier
        voirPanierButton.addActionListener(e -> afficherPanier());
    // Retour à la sélection de rôle
        retourButton.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous retourner à la page d'accueil?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new SelectionUI();
        }
    });

    // Double-clic sur un appareil pour voir les détails
        appareilsTable.addMouseListener( new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                afficherDetailsAppareil();
            }
        }
    });

    // Désactiver ajouter au panier si aucune sélection
        ajouterPanierButton.setEnabled(false);
        appareilsTable.getSelectionModel().addListSelectionListener(e -> {
        boolean hasSelection = appareilsTable.getSelectedRow() != -1;
        ajouterPanierButton.setEnabled(hasSelection);
    });

    add(mainPanel);
}


private void stylizeButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    private void chargerDonnees() throws SQLException {
        tableModel.setRowCount(0);
        List<Appareil> appareils = magasinService.listerAppareils();

        for (Appareil a : appareils) {
            tableModel.addRow(new Object[]{
                    a.getId(), a.getType(), a.getMarque(), a.getModele(),
                    a.getPrix(), a.getQuantite(), a.getCaracteristic()
            });
        }

        remplirComboBoxes(appareils);
    }

    private void remplirComboBoxes(List<Appareil> appareils) {
        // Types dynamiques
        Set<String> types = appareils.stream().map(Appareil::getType).collect(Collectors.toSet());
        typeComboBox.removeAllItems();
        typeComboBox.addItem("Tous");
        types.forEach(typeComboBox::addItem);

        // Marques dynamiques
        Set<String> marques = appareils.stream().map(Appareil::getMarque).collect(Collectors.toSet());
        marqueComboBox.removeAllItems();
        marqueComboBox.addItem("Toutes");
        marques.forEach(marqueComboBox::addItem);
    }

    private void rechercherAppareils() throws SQLException {

        String idRecherche = rechercheField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String marque = (String) marqueComboBox.getSelectedItem();
        boolean alerte = alerteCheckBox.isSelected();

        double prixMax = Double.MAX_VALUE;
        if (!prixMaxField.getText().isEmpty()) {
            try {
                prixMax = Double.parseDouble(prixMaxField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Prix invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        List<Appareil> resultats;

        // 1 Recherche par ID

        if (!idRecherche.isEmpty()) {
            Appareil a = magasinService.rechercherParId(idRecherche);
            resultats = (a == null) ? List.of() : List.of(a);
        }

        // 2 Appareils en alerte
        else if (alerte) {
            resultats = magasinService.appareilsEnAlerte(alerte);
        }
        //type + marque+ prix max
        else if (!type.equals("Tous") && !marque.equals("Toutes") && prixMax != Double.MAX_VALUE){
            resultats = magasinService.listerAppareils(type, marque, prixMax);
        }
        //  Type + Marque
        else if (!type.equals("Tous") && !marque.equals("Toutes")) {
            resultats = magasinService.listerAppareils(type, marque);
        }
        //  Type + Prix max
        else if (!type.equals("Tous") && prixMax != Double.MAX_VALUE) {
            resultats = magasinService.listerAppareils(type, prixMax);
        }
        // Type seul
        else if (!type.equals("Tous")) {
            resultats = magasinService.listerAppareilsParType(type);
        }
        // Marque seule
        else if (!marque.equals("Toutes")) {
            resultats = magasinService.listerAppareilsParMarque(marque);
        }

        // Tous les appareils
        else {
            resultats = magasinService.listerAppareils();
        }

        // Mise à jour du tableau
        tableModel.setRowCount(0);
        for (Appareil a : resultats) {
            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getType(),
                    a.getMarque(),
                    a.getModele(),
                    a.getPrix(),
                    a.getQuantite(),
                    a.getSeuilAlerte(),
                    a.getCaracteristic()
            });
        }
    }

    private void ajouterAuPanier() {
        int selectedRow = appareilsTable.getSelectedRow(); // on récupère la ligne sélectionnée
        if (selectedRow != -1) { // -1 signifie pas de sélection

            // Récupérer les 5 éléments de la ligne
            String id = tableModel.getValueAt(selectedRow, 0).toString();       // Colonne 0: ID
            String type = tableModel.getValueAt(selectedRow, 1).toString();     // Colonne 1: Type
            String marque = tableModel.getValueAt(selectedRow, 2).toString();   // Colonne 2: Marque
            String modele = tableModel.getValueAt(selectedRow, 3).toString();   // Colonne 3: Modèle
            String prix = tableModel.getValueAt(selectedRow, 4).toString();     // Colonne 4: Prix

            // Créer une chaîne formatée avec les 5 éléments
            String articlePanier = String.format("%s | %s | %s %s | %s€",
                    id, type, marque, modele, prix);

            // Ajouter au panier
            panier.add(articlePanier);

            // Mettre à jour l'affichage
            panierLabel.setText("Panier: " + panier.size() + " article(s)");

            // Message de confirmation
            JOptionPane.showMessageDialog(this,
                    "Article ajouté au panier:\n" +
                            "ID: " + id + "\n" +
                            "Type: " + type + "\n" +
                            "Marque: " + marque + "\n" +
                            "Modèle: " + modele + "\n" +
                            "Prix: " + prix + "€",
                    "Article ajouté",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void afficherPanier() {
        if (panier.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Votre panier est vide.",
                    "Panier",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("VOTRE PANIER\n");
        sb.append("=".repeat(40)).append("\n\n");

        double total = 0;

        for (int i = 0; i < panier.size(); i++) {
            String article = panier.get(i);
            sb.append((i + 1)).append(". ").append(article).append("\n");

            // Extraire le prix pour calculer le total
            // Format attendu: "ID | Type | Marque Modèle | Prix"
            String[] parties = article.split("\\|");
            if (parties.length >= 4) {
                try {
                    String prixStr = parties[3].trim().replace("€", "");
                    double prix = Double.parseDouble(prixStr);
                    total += prix;
                } catch (NumberFormatException e) {
                    // Ignorer si le prix n'est pas parsable
                }
            }
        }

        sb.append("\n").append("=".repeat(40)).append("\n");
        sb.append(String.format("Nombre d'articles: %d\n", panier.size()));
        sb.append(String.format("Total: %.2f€", total));
        sb.append("\n").append("=".repeat(40));

        JOptionPane.showMessageDialog(this,
                sb.toString(),
                "Contenu du Panier",
                JOptionPane.INFORMATION_MESSAGE);
    }
    private void afficherDetailsAppareil() {

        // Ligne sélectionnée
        int selectedRow = appareilsTable.getSelectedRow();
        if (selectedRow == -1) return;

        // Récupération de l'ID depuis la table
        String id = tableModel.getValueAt(selectedRow, 0).toString();

        // Récupération complète depuis la BD
        Appareil a = magasinService.rechercherParId(id);
        if (a == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Appareil introuvable !",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Vérification alerte stock
        boolean enAlerte = a.getQuantite() <= a.getSeuilAlerte();

        // Message détaillé
        String message =
                "ID : " + a.getId() + "\n" +
                        "Type : " + a.getType() + "\n" +
                        "Marque : " + a.getMarque() + "\n" +
                        "Modèle : " + a.getModele() + "\n" +
                        "Prix : " + a.getPrix() + " €\n" +
                        "Quantité en stock : " + a.getQuantite() + "\n" +
                        "Seuil d'alerte : " + a.getSeuilAlerte() + "\n" +
                        "Description : " + a.getCaracteristic() + "\n\n" +
                        (enAlerte ? "⚠️ ATTENTION : APPAREIL EN ALERTE STOCK"
                                : "Stock normal");

        // Affichage
        JOptionPane.showMessageDialog(
                this,
                message,
                "Détails de l'appareil",
                enAlerte ? JOptionPane.WARNING_MESSAGE
                        : JOptionPane.INFORMATION_MESSAGE
        );
    }


}

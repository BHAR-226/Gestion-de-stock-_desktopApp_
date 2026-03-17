package interface_graphique;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import marchandise.Appareil;
import marchandise.Caracteristic;
import service.MagasinService;

public class AdminUI extends JFrame {
    private JTable appareilsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, statsButton, logoutButton;
    private JLabel statsLabel;
    private MagasinService magasinService;



    public AdminUI() {
        setTitle("Interface Administrateur - Gestion de Stock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        magasinService = new MagasinService();

        initUI();
        chargerDonneesBase(); // Charger directement depuis la base
        setVisible(true);
    }

    private void initUI() {
        // Panel principal avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));



        // BARRE DE TITRE
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("PANEL D'ADMINISTRATION - GESTION DE STOCK");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        logoutButton = new JButton("Déconnexion");
        stylizeButton(logoutButton, new Color(178, 34, 34));
        logoutButton.setForeground(Color.BLACK);
        titlePanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // BARRE D'OUTILS
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarPanel.setBorder(BorderFactory.createTitledBorder("Actions Rapides"));

        addButton = new JButton("Nouvel Appareil");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        statsButton = new JButton("Statistiques");

        stylizeButton(addButton, new Color(34, 139, 34));
        stylizeButton(editButton, new Color(255, 140, 0));
        stylizeButton(deleteButton, new Color(178, 34, 34));
        stylizeButton(refreshButton, new Color(70, 130, 180));
        stylizeButton(statsButton, new Color(138, 43, 226));

        addButton.setForeground(Color.BLACK);
        editButton.setForeground(Color.BLACK);
        deleteButton.setForeground(Color.BLACK);
        refreshButton.setForeground(Color.BLACK);
        statsButton.setForeground(Color.BLACK);

        // Champ de recherche
        searchField = new JTextField(20);
        searchField.setToolTipText("Rechercher par ID, type, marque ou modèle");
        JButton searchButton = new JButton("Rechercher");
        stylizeButton(searchButton, new Color(70, 130, 180));
        searchButton.setForeground(Color.BLACK);

        toolbarPanel.add(addButton);
        toolbarPanel.add(editButton);
        toolbarPanel.add(deleteButton);
        toolbarPanel.add(refreshButton);
        toolbarPanel.add(statsButton);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(new JLabel("Recherche:"));
        toolbarPanel.add(searchField);
        toolbarPanel.add(searchButton);



        // TABLEAU DES APPAREILS - Adapté à votre structure
        String[] colonnes = {"ID", "Type", "Marque", "Modèle", "Prix (dh)", "Quantité", "Seuil Alerte", "Description"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Double.class; // Prix
                if (columnIndex == 5 || columnIndex == 6) return Integer.class; // Quantité et Seuil Alerte
                return String.class;
            }
        };

        appareilsTable = new JTable(tableModel);
        appareilsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appareilsTable.setRowHeight(30);
        appareilsTable.setAutoCreateRowSorter(true);

        // Rendu conditionnel pour le stock
        appareilsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 5) { // Colonne Quantité
                    try {
                        int quantite = Integer.parseInt(value.toString());
                        int seuil = 0;

                        // Récupérer le seuil de la même ligne
                        Object seuilObj = table.getValueAt(row, 6);
                        if (seuilObj != null) {
                            seuil = Integer.parseInt(seuilObj.toString());
                        }

                        if (quantite == 0) {
                            c.setBackground(new Color(255, 200, 200)); // Rouge clair
                            c.setForeground(Color.RED);
                        } else if (quantite <= seuil) {
                            c.setBackground(new Color(255, 255, 200)); // Jaune clair
                            c.setForeground(Color.ORANGE);
                        } else {
                            c.setBackground(new Color(200, 255, 200)); // Vert clair
                            c.setForeground(Color.GREEN.darker());
                        }

                        if (isSelected) {
                            c.setBackground(table.getSelectionBackground());
                            c.setForeground(table.getSelectionForeground());
                        }
                    } catch (NumberFormatException e) {
                        // Si erreur de parsing, on garde le rendu par défaut
                    }
                } else {
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
                }

                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(appareilsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inventaire des Appareils"));

        // PANEL CENTRAL
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(toolbarPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // PANEL DE STATISTIQUES
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques Globales"));

        statsLabel = new JLabel("Chargement des statistiques...");
        statsPanel.add(statsLabel);

        // AJOUT FINAL À LA FENÊTRE PRINCIPALE
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);

        // GESTION DES ÉVÉNEMENTS
        addButton.addActionListener(e -> ajouterAppareil());
        editButton.addActionListener(e -> modifierAppareil());
        deleteButton.addActionListener(e -> supprimerAppareil());
        refreshButton.addActionListener(e -> {
            chargerDonneesBase();
            calculerStatistiques();
        });
        statsButton.addActionListener(e -> afficherStatistiquesDetaillees());
        logoutButton.addActionListener(e -> seDeconnecter());
        searchButton.addActionListener(e -> rechercherAppareils());

        // Désactiver les boutons Modifier/Supprimer si aucune sélection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        appareilsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean hasSelection = appareilsTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
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
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void chargerDonneesBase() {
        tableModel.setRowCount(0); // Vider le tableau

        try {
            if (magasinService == null) {
                magasinService = new MagasinService();
            }

            List<Appareil> appareils = magasinService.listerAppareils();

            for (Appareil appareil : appareils) {
                Object[] ligne = {
                        appareil.getId(),
                        appareil.getType(),
                        appareil.getMarque(),
                        appareil.getModele(),
                        appareil.getPrix(),
                        appareil.getQuantite(),
                        appareil.getSeuilAlerte(),
                        appareil.getCaracteristic()
                };
                tableModel.addRow(ligne);
            }

            calculerStatistiques();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des données: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Charger des données de test en cas d'erreur
            chargerDonneesTest();
        }
    }

    private void chargerDonneesTest() {
        tableModel.setRowCount(0);

        Object[][] donneesTest = {
                {"APP001", "Informatique", "HP", "840 G7", 1299.99, 15, 5, "PC Portable Professionnel"},
                {"APP002", "Communication", "Samsung", "S23", 899.99, 25, 10, "Smartphone Android"},
                {"APP003", "Hybride", "Apple", "iPad Pro 12.9", 1099.99, 8, 3, "Tablette Pro"}
        };

        for (Object[] ligne : donneesTest) {
            tableModel.addRow(ligne);
        }

        calculerStatistiques();
    }

    private void ajouterAppareil() {
        JDialog dialog = new JDialog(this, "Ajouter un Nouvel Appareil", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Champs du formulaire adaptés à votre structure
        JLabel idLabel = new JLabel("ID*:");
        JTextField idField = new JTextField(20);

        JLabel typeLabel = new JLabel("Type*:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"PC", "Téléphone", "Tablette", "Smartwach", "Autre"});

        JLabel marqueLabel = new JLabel("Marque*:");
        JTextField marqueField = new JTextField(20);

        JLabel modeleLabel = new JLabel("Modèle*:");
        JTextField modeleField = new JTextField(20);

        JLabel prixLabel = new JLabel("Prix (dh)*:");
        JTextField prixField = new JTextField(20);

        JLabel quantiteLabel = new JLabel("Quantité*:");
        JTextField quantiteField = new JTextField(20);

        JLabel seuilLabel = new JLabel("Seuil d'Alerte:");
        JTextField seuilField = new JTextField(20);
        seuilField.setText("5");

        // Description/Caractéristiques
        JLabel descriptionLabel = new JLabel("Description:");
        JTextArea descriptionArea = new JTextArea(3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

        // Ajout des composants
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(marqueLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(marqueField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(modeleLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(modeleField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(prixLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(prixField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(quantiteLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(quantiteField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(seuilLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(seuilField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionScroll, gbc);

        // Boutons
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        stylizeButton(saveButton, new Color(19, 143, 110));
        stylizeButton(cancelButton, new Color(87, 48, 48));

        saveButton.setForeground(Color.BLACK);
        cancelButton.setForeground(Color.BLACK);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        formPanel.add(buttonPanel, gbc);

        // Actions des boutons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validation
                if (idField.getText().trim().isEmpty() ||
                        typeCombo.getSelectedItem() == null ||
                        marqueField.getText().trim().isEmpty() ||
                        modeleField.getText().trim().isEmpty() ||
                        prixField.getText().trim().isEmpty() ||
                        quantiteField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs obligatoires (*)",
                            "Erreur de validation",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Créer l'appareil
                    Appareil appareil = new Appareil();

                    // Récupérer les valeurs
                    appareil.setId(idField.getText().trim());
                    appareil.setType((String) typeCombo.getSelectedItem());
                    appareil.setMarque(marqueField.getText().trim());
                    appareil.setModele(modeleField.getText().trim());
                    appareil.setPrix(Double.parseDouble(prixField.getText()));
                    appareil.setQuantite(Integer.parseInt(quantiteField.getText()));

                    int seuil = seuilField.getText().isEmpty() ? 5 : Integer.parseInt(seuilField.getText());
                    appareil.setSeuilAlerte(seuil);

                    // Créer la caractéristique avec la description
                    Caracteristic caracteristic = new Caracteristic();
                    appareil.setCaracteristic(caracteristic);

                    // Ajouter via MagasinService
                    if (magasinService.ajouterAppareil(appareil)) {
                        JOptionPane.showMessageDialog(dialog,
                                "Appareil ajouté avec succès!\nID: " + appareil.getId(),
                                "Succès",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Réinitialiser les champs
                        idField.setText("");
                        marqueField.setText("");
                        modeleField.setText("");
                        prixField.setText("");
                        quantiteField.setText("");
                        seuilField.setText("5");
                        descriptionArea.setText("");

                        // Recharger les données
                        chargerDonneesBase();
                        dialog.dispose();

                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Erreur lors de l'ajout en base de données\n" +
                                        "Vérifiez que l'ID n'existe pas déjà",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez entrer des valeurs numériques valides pour le prix, la quantité et le seuil",
                            "Erreur de format",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel);
        dialog.setVisible(true);
    }

    private void modifierAppareil() {
        int selectedRow = appareilsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = appareilsTable.convertRowIndexToModel(selectedRow);

            // Récupérer l'ID de l'appareil
            String id = tableModel.getValueAt(modelRow, 0).toString();

            // Charger l'appareil depuis la base
            Appareil appareil = magasinService.rechercherParId(id);

            if (appareil != null) {
                // Ouvrir le formulaire de modification
                modifierAppareilDialog(appareil, modelRow);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Impossible de charger l'appareil avec l'ID: " + id,
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un appareil à modifier.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void modifierAppareilDialog(Appareil appareil, int modelRow) {
        JDialog dialog = new JDialog(this, "Modifier l'Appareil", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Champs pré-remplis
        JLabel idLabel = new JLabel("ID (non modifiable):");
        JTextField idField = new JTextField(appareil.getId());
        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);

        JLabel typeLabel = new JLabel("Type*:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"PC", "Téléphone", "Tablette", "Smartwach", "Autre"});
        typeCombo.setSelectedItem(appareil.getType());

        JLabel marqueLabel = new JLabel("Marque*:");
        JTextField marqueField = new JTextField(appareil.getMarque(), 20);

        JLabel modeleLabel = new JLabel("Modèle*:");
        JTextField modeleField = new JTextField(appareil.getModele(), 20);

        JLabel prixLabel = new JLabel("Prix (dh)*:");
        JTextField prixField = new JTextField(String.valueOf(appareil.getPrix()), 20);

        JLabel quantiteLabel = new JLabel("Quantité*:");
        JTextField quantiteField = new JTextField(String.valueOf(appareil.getQuantite()), 20);

        JLabel seuilLabel = new JLabel("Seuil d'Alerte:");
        JTextField seuilField = new JTextField(String.valueOf(appareil.getSeuilAlerte()), 20);

        // Description/Caractéristiques
        Caracteristic caracteristic = appareil.getCaracteristic();
        JLabel descriptionLabel = new JLabel("Description:");
        JTextArea descriptionArea = new JTextArea(caracteristic != null ? caracteristic.toString() : "", 3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

        // Ajout des composants
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(marqueLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(marqueField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(modeleLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(modeleField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(prixLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(prixField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(quantiteLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(quantiteField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(seuilLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(seuilField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionScroll, gbc);

        // Boutons
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton saveButton = new JButton("Sauvegarder");
        JButton cancelButton = new JButton("Annuler");

        stylizeButton(saveButton, new Color(19, 143, 110));
        stylizeButton(cancelButton, new Color(87, 48, 48));

        saveButton.setForeground(Color.BLACK);
        cancelButton.setForeground(Color.BLACK);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel, gbc);

        // Actions
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Mettre à jour l'appareil
                    appareil.setType((String) typeCombo.getSelectedItem());
                    appareil.setMarque(marqueField.getText().trim());
                    appareil.setModele(modeleField.getText().trim());
                    appareil.setPrix(Double.parseDouble(prixField.getText()));
                    appareil.setQuantite(Integer.parseInt(quantiteField.getText()));
                    appareil.setSeuilAlerte(Integer.parseInt(seuilField.getText()));

                    // Mettre à jour la description
                    if (appareil.getCaracteristic() == null) {
                        appareil.setCaracteristic(new Caracteristic());
                    }

                    // Mettre à jour via MagasinService
                    if (magasinService.modifierAppareil(appareil)) {
                        // Mettre à jour le tableau
                        tableModel.setValueAt(appareil.getType(), modelRow, 1);
                        tableModel.setValueAt(appareil.getMarque(), modelRow, 2);
                        tableModel.setValueAt(appareil.getModele(), modelRow, 3);
                        tableModel.setValueAt(appareil.getPrix(), modelRow, 4);
                        tableModel.setValueAt(appareil.getQuantite(), modelRow, 5);
                        tableModel.setValueAt(appareil.getSeuilAlerte(), modelRow, 6);
                        tableModel.setValueAt(appareil.getCaracteristic(), modelRow, 7);

                        JOptionPane.showMessageDialog(dialog,
                                "Appareil modifié avec succès!",
                                "Succès",
                                JOptionPane.INFORMATION_MESSAGE);

                        dialog.dispose();
                        calculerStatistiques();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Erreur lors de la modification en base de données",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez entrer des valeurs numériques valides",
                            "Erreur de format",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel);
        dialog.setVisible(true);
    }

    private void supprimerAppareil() {
        int selectedRow = appareilsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = appareilsTable.convertRowIndexToModel(selectedRow);
            String id = tableModel.getValueAt(modelRow, 0).toString();
            String marque = tableModel.getValueAt(modelRow, 2).toString();
            String modele = tableModel.getValueAt(modelRow, 3).toString();

            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer l'appareil ?\n\n" +
                            "ID: " + id + "\n" +
                            "Marque: " + marque + "\n" +
                            "Modèle: " + modele + "\n\n" +
                            "Cette action est irréversible!",
                    "Confirmation de Suppression",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    if (magasinService.supprimerAppareil(id)) {
                        tableModel.removeRow(modelRow);
                        JOptionPane.showMessageDialog(this,
                                "Appareil supprimé avec succès!",
                                "Suppression réussie",
                                JOptionPane.INFORMATION_MESSAGE);
                        calculerStatistiques();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Erreur lors de la suppression en base de données",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur: " + e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un appareil à supprimer.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void rechercherAppareils() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            chargerDonneesBase();
            return;
        }

        // Filtrer les lignes existantes
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            boolean match = false;

            // Rechercher dans les colonnes Type (1), Marque (2), Modèle (3)
            for (int j = 1; j <= 3; j++) {
                String cellValue = tableModel.getValueAt(i, j).toString().toLowerCase();
                if (cellValue.contains(searchText)) {
                    match = true;
                    break;
                }
            }

            if (!match) {
                tableModel.removeRow(i);
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Aucun appareil ne correspond à votre recherche : \"" + searchText + "\"",
                    "Recherche infructueuse",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void calculerStatistiques() {
        int totalArticles = tableModel.getRowCount();
        int totalStock = 0;
        double valeurTotale = 0;
        int articlesFaibleStock = 0;
        int articlesRupture = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                int quantite = Integer.parseInt(tableModel.getValueAt(i, 5).toString());
                double prix = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
                int seuil = Integer.parseInt(tableModel.getValueAt(i, 6).toString());

                totalStock += quantite;
                valeurTotale += quantite * prix;

                if (quantite == 0) {
                    articlesRupture++;
                } else if (quantite <= seuil) {
                    articlesFaibleStock++;
                }
            } catch (NumberFormatException e) {
                // Ignorer les erreurs de parsing
            }
        }

        statsLabel.setText(String.format(
                "<html><b>Statistiques:</b> %d appareils | Stock total: %d unités | Valeur: %.2f dh | " +
                        "<font color='orange'>Faible stock: %d</font> | <font color='red'>Rupture: %d</font></html>",
                totalArticles, totalStock, valeurTotale, articlesFaibleStock, articlesRupture
        ));
    }

    private void afficherStatistiquesDetaillees() {
        try {
            int totalArticles = tableModel.getRowCount();
            int totalStock = 0;
            double valeurTotale = 0;
            int articlesFaibleStock = 0;
            int articlesRupture = 0;

            // Compter par type
            int pcCount = 0;
            int phoneCount = 0;
            int tabletCount = 0;
            int watchCount = 0;
            int otherCount = 0;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                try {
                    int quantite = Integer.parseInt(tableModel.getValueAt(i, 5).toString());
                    double prix = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
                    int seuil = Integer.parseInt(tableModel.getValueAt(i, 6).toString());
                    String type = tableModel.getValueAt(i, 1).toString().toLowerCase();

                    totalStock += quantite;
                    valeurTotale += quantite * prix;

                    if (quantite == 0) {
                        articlesRupture++;
                    } else if (quantite <= seuil) {
                        articlesFaibleStock++;
                    }

                    // Compter par type
                    if (type.contains("pc") || type.contains("ordinateur")) {
                        pcCount++;
                    } else if (type.contains("téléphone") || type.contains("phone")) {
                        phoneCount++;
                    } else if (type.contains("tablette")) {
                        tabletCount++;
                    } else if (type.contains("smartwach") || type.contains("montre")) {
                        watchCount++;
                    } else {
                        otherCount++;
                    }

                } catch (NumberFormatException e) {
                    // Ignorer
                }
            }

            String message = String.format(
                    "STATISTIQUES DÉTAILLÉES\n\n" +
                            "📊 INVENTAIRE TOTAL\n" +
                            "• Nombre d'appareils: %d\n" +
                            "• Stock total: %d unités\n" +
                            "• Valeur totale: %.2f dh\n\n" +

                            "⚠️  ÉTAT DU STOCK\n" +
                            "• En rupture: %d appareils\n" +
                            "• Stock faible: %d appareils\n" +
                            "• Stock normal: %d appareils\n\n" +

                            "📱 RÉPARTITION PAR TYPE\n" +
                            "• PC/Ordinateurs: %d\n" +
                            "• Téléphones: %d\n" +
                            "• Tablettes: %d\n" +
                            "• Smartwachs: %d\n" +
                            "• Autres: %d",
                    totalArticles, totalStock, valeurTotale,
                    articlesRupture, articlesFaibleStock, (totalArticles - articlesRupture - articlesFaibleStock),
                    pcCount, phoneCount, tabletCount, watchCount, otherCount
            );

            JOptionPane.showMessageDialog(this,
                    message,
                    "Statistiques Détaillées",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du calcul des statistiques: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seDeconnecter() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir vous déconnecter?",
                "Déconnexion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new SelectionUI(); // Assurez-vous que cette classe existe
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AdminUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
package interface_graphique;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import users.UserManager;

import static users.UserManager.CLE_ADMIN;

public class SelectionUI extends JFrame {

    public SelectionUI() {
        setTitle("Gestion de Stock - Sélection du Rôle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Panel principal avec BoxLayout vertical
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Panel central pour titre + boutons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Titre
        JLabel titleLabel = new JLabel("Bienvenue au Système de Gestion de Stock");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // plus grand
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 40))); // espace après le titre

        // Boutons
        JButton connectButton = new JButton("Se connecter");
        JButton createButton = new JButton("Créer un compte");

        // Styles des boutons
        Font btnFont = new Font("Arial", Font.BOLD, 16);
        connectButton.setFont(btnFont);
        createButton.setFont(new Font("Arial", Font.PLAIN, 14));

        connectButton.setMaximumSize(new Dimension(300, 50));
        createButton.setMaximumSize(new Dimension(200, 50));

        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajouter les boutons avec espace
        centerPanel.add(connectButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(createButton);

        // Centrer verticalement
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(centerPanel);
        mainPanel.add(wrapperPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("© 2025 Système de Gestion de Stock - Tous droits réservés");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);

        // Actions des boutons

        connectButton.addActionListener(e -> {
            try {
                authenticate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        createButton.addActionListener(e -> showCreateAccountDialog());
    }


    private void authenticate() throws SQLException {

        JPanel authPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        authPanel.add(new JLabel("Nom d'utilisateur :"));
        authPanel.add(userField);
        authPanel.add(new JLabel("Mot de passe :"));
        authPanel.add(passField);

        int option = JOptionPane.showConfirmDialog(
                this,
                authPanel,
                "Authentification",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            UserManager userManager = new UserManager();

            //  Vérification login
            if (!userManager.login(username, password)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Identifiants incorrects",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            //  Récupération du rôle
            String role = userManager.getRole(username);

            dispose(); // fermer SelectionUI

            //  Redirection
            if ("admin".equalsIgnoreCase(role)) {
                new AdminUI();

            } else {
                new UserUI();
            }
        }
    }
    private void showCreateAccountDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();
        JPasswordField keyField = new JPasswordField(); // Clé admin optionnelle

        panel.add(new JLabel("Nom d'utilisateur :"));
        panel.add(usernameField);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirmer le mot de passe :"));
        panel.add(confirmField);
        panel.add(new JLabel("Clé Admin (optionnelle) :"));
        panel.add(keyField);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Créer un compte",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());
            String key = keyField.getText().trim();

            // Vérifications basiques
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs obligatoires doivent être remplis");
                return;
            }
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas !");
                return;
            }

            UserManager um = new UserManager();
            boolean isAdmin = false;

            // Si clé renseignée → rôle admin
            if (!key.isEmpty()) {
                if (!key.equals(CLE_ADMIN)) {
                    JOptionPane.showMessageDialog(this, "Clé admin incorrecte !" + CLE_ADMIN.toString() );
                    return;
                }
                isAdmin = true;
            }

            // Création utilisateur via UserManager
            boolean success;
            if (isAdmin) {
                success = um.creerUtilisateur(username, password, "admin", key);
            } else {
                success = um.creerUtilisateur(username, password);
            }

            // Message de confirmation ou d'erreur
            if (success) {
                String msg = isAdmin ? "Nouvel administrateur créé !" : "Nouvel utilisateur créé !";
                JOptionPane.showMessageDialog(this, msg);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : nom d'utilisateur déjà existant ou clé incorrecte !");
            }
        }
    }

}

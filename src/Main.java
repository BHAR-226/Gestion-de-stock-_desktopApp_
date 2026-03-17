import interface_graphique.SelectionUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Utilisation de SwingUtilities pour garantir le thread-safe
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Définition du look and feel du système
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Lancement de l'application avec l'interface de sélection du rôle
                new SelectionUI();
            }
        });
    }

}
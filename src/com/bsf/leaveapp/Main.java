package com.bsf.leaveapp;

import com.bsf.leaveapp.database.DatabaseHelper;
import com.bsf.leaveapp.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 1. Setup system default look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Failed to set system look and feel. Using default.");
        }

        // 2. Bootstrap database and schema
        DatabaseHelper.initializeDatabase();

        // 3. Launch application window on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

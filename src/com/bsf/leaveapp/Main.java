package com.bsf.leaveapp;

import com.bsf.leaveapp.database.DatabaseHelper;
import com.bsf.leaveapp.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 1. Setup modern theme look and feel
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf theme. Falling back to default.");
        }

        // 2. Bootstrap database and schema
        DatabaseHelper.initializeDatabase();

        // 3. Launch application window on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

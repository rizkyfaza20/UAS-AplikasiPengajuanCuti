package com.bsf.leaveapp.ui;

import com.bsf.leaveapp.database.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;

    public LoginFrame() {
        setTitle("PT BSF - Sistem Pengajuan Cuti");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel lblTitle = new JLabel("Login - Sistem Pengajuan Cuti PT BSF", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 13f));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(6, 1, 5, 5));

        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Role / Hak Akses:"));
        cbRole = new JComboBox<>(new String[]{"ADMIN", "HRD"});
        formPanel.add(cbRole);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnLogin = new JButton("Login");
        JButton btnSignUp = new JButton("Daftar Akun");
        btnPanel.add(btnLogin);
        btnPanel.add(btnSignUp);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        btnLogin.addActionListener(e -> handleLogin());
        btnSignUp.addActionListener(e -> openSignUpDialog());

        // Allow Enter key on password field
        txtPassword.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT * FROM admin WHERE username = ? AND password = ? AND hak_akses = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("nama_admin");
                    new DashboardFrame(name, role).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Username, Password, atau Role salah!", "Gagal Login", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Kesalahan database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openSignUpDialog() {
        JDialog signupDialog = new JDialog(this, "Daftar Akun Admin/HRD", true);
        signupDialog.setSize(350, 320);
        signupDialog.setLocationRelativeTo(this);
        signupDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Daftar Akun Baru", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 13f));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(10, 1, 5, 3));

        JComboBox<String> cbNewRole = new JComboBox<>(new String[]{"ADMIN", "HRD"});
        JTextField txtCode = new JTextField();
        txtCode.setEditable(false);
        JTextField txtName = new JTextField();
        JTextField txtNewUser = new JTextField();
        JPasswordField txtNewPass = new JPasswordField();

        gridPanel.add(new JLabel("Hak Akses (Role):"));
        gridPanel.add(cbNewRole);
        gridPanel.add(new JLabel("Kode (Otomatis):"));
        gridPanel.add(txtCode);
        gridPanel.add(new JLabel("Nama Lengkap:"));
        gridPanel.add(txtName);
        gridPanel.add(new JLabel("Username:"));
        gridPanel.add(txtNewUser);
        gridPanel.add(new JLabel("Password:"));
        gridPanel.add(txtNewPass);

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        Runnable updateCode = () -> {
            String role = (String) cbNewRole.getSelectedItem();
            String prefix = "ADMIN".equals(role) ? "ADM-" : "HRD-";
            txtCode.setText(DatabaseHelper.generateNextId("admin", "kd_admin", prefix));
        };
        cbNewRole.addActionListener(e -> updateCode.run());
        updateCode.run();

        JButton btnSave = new JButton("Daftar Akun");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(btnSave);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            String code = txtCode.getText();
            String name = txtName.getText().trim();
            String user = txtNewUser.getText().trim();
            String pass = new String(txtNewPass.getPassword()).trim();
            String role = (String) cbNewRole.getSelectedItem();

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(signupDialog, "Semua kolom input wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO admin (kd_admin, nama_admin, username, password, hak_akses) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, code);
                pstmt.setString(2, name);
                pstmt.setString(3, user);
                pstmt.setString(4, pass);
                pstmt.setString(5, role);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(signupDialog, "Pendaftaran berhasil! Silakan login.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                signupDialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(signupDialog, "Username sudah digunakan atau ada kesalahan database!", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        signupDialog.add(mainPanel);
        signupDialog.setVisible(true);
    }
}

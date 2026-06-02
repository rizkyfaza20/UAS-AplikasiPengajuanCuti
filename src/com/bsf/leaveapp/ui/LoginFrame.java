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
        setTitle("PT BSF - Leave Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);
        setResizable(false);

        // Core layout container centering the card panel
        JPanel rootPanel = new JPanel(new GridBagLayout());
        rootPanel.setBackground(new Color(30, 30, 30));

        // Modern Login card wrapper
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        cardPanel.setBackground(new Color(43, 43, 43));

        // Title brand heading
        JLabel lblBrand = new JLabel("PT BSF");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBrand.setForeground(new Color(57, 137, 227));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sistem Informasi Pengajuan Cuti");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.LIGHT_GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(lblBrand);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(lblSub);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Input Fields Form
        JPanel formPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        formPanel.setBackground(new Color(43, 43, 43));

        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(Color.WHITE);
        txtUsername = new JTextField();

        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(Color.WHITE);
        txtPassword = new JPasswordField();

        JLabel lblRole = new JLabel("Role / Hak Akses");
        lblRole.setForeground(Color.WHITE);
        cbRole = new JComboBox<>(new String[]{"ADMIN", "HRD"});

        formPanel.add(lblUser);
        formPanel.add(txtUsername);
        formPanel.add(lblPass);
        formPanel.add(txtPassword);
        formPanel.add(lblRole);
        formPanel.add(cbRole);

        cardPanel.add(formPanel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Action Buttons Row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(new Color(43, 43, 43));

        JButton btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(100, 32));
        btnLogin.setBackground(new Color(57, 137, 227));
        btnLogin.setForeground(Color.WHITE);

        JButton btnSignUp = new JButton("Sign Up");
        btnSignUp.setPreferredSize(new Dimension(100, 32));

        btnPanel.add(btnLogin);
        btnPanel.add(btnSignUp);

        cardPanel.add(btnPanel);
        rootPanel.add(cardPanel);
        add(rootPanel);

        // Core Event flows
        btnLogin.addActionListener(e -> handleLogin());
        btnSignUp.addActionListener(e -> openSignUpDialog());
    }

    // Connects to DB and validates login credentials
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
                    // Authentication successful: Load main dashboard framework
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

    // Launches registration modal to sign up new accounts
    private void openSignUpDialog() {
        JDialog signupDialog = new JDialog(this, "Daftar Akun Admin/HRD", true);
        signupDialog.setSize(380, 420);
        signupDialog.setLocationRelativeTo(this);
        signupDialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Daftar Akun Baru");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel gridPanel = new JPanel(new GridLayout(10, 1, 5, 2));

        JLabel lblRoleSel = new JLabel("Hak Akses (Role)");
        JComboBox<String> cbNewRole = new JComboBox<>(new String[]{"ADMIN", "HRD"});

        JLabel lblCode = new JLabel("Kode Admin / HRD (Otomatis)");
        JTextField txtCode = new JTextField();
        txtCode.setEditable(false);

        JLabel lblName = new JLabel("Nama Lengkap");
        JTextField txtName = new JTextField();

        JLabel lblUser = new JLabel("Username");
        JTextField txtNewUser = new JTextField();

        JLabel lblPass = new JLabel("Password");
        JPasswordField txtNewPass = new JPasswordField();

        gridPanel.add(lblRoleSel);
        gridPanel.add(cbNewRole);
        gridPanel.add(lblCode);
        gridPanel.add(txtCode);
        gridPanel.add(lblName);
        gridPanel.add(txtName);
        gridPanel.add(lblUser);
        gridPanel.add(txtNewUser);
        gridPanel.add(lblPass);
        gridPanel.add(txtNewPass);

        mainPanel.add(gridPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Automatic code prefix assignment handler
        Runnable updateCode = () -> {
            String role = (String) cbNewRole.getSelectedItem();
            String prefix = "ADMIN".equals(role) ? "ADM-" : "HRD-";
            txtCode.setText(DatabaseHelper.generateNextId("admin", "kd_admin", prefix));
        };
        cbNewRole.addActionListener(e -> updateCode.run());
        updateCode.run(); // Initial fetch

        JButton btnSave = new JButton("Daftar Akun");
        btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
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

        mainPanel.add(btnSave);
        signupDialog.add(mainPanel);
        signupDialog.setVisible(true);
    }
}

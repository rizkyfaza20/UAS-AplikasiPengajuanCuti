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
        setSize(520, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel lblAppName = new JLabel("Sistem Pengajuan Cuti");
        lblAppName.setFont(lblAppName.getFont().deriveFont(Font.BOLD, 20f));
        lblAppName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCompany = new JLabel("PT BSF Indonesia");
        lblCompany.setFont(lblCompany.getFont().deriveFont(Font.PLAIN, 14f));
        lblCompany.setForeground(Color.GRAY);
        lblCompany.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblAppName);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(lblCompany);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Form with GridBagLayout for proper sizing
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Masukkan Kredensial"),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(lblUser.getFont().deriveFont(Font.PLAIN, 14f));
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtUsername = new JTextField(20);
        txtUsername.setFont(txtUsername.getFont().deriveFont(14f));
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(lblPass.getFont().deriveFont(Font.PLAIN, 14f));
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(txtPassword.getFont().deriveFont(14f));
        formPanel.add(txtPassword, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel lblRole = new JLabel("Role / Hak Akses:");
        lblRole.setFont(lblRole.getFont().deriveFont(Font.PLAIN, 14f));
        formPanel.add(lblRole, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        cbRole = new JComboBox<>(new String[]{"ADMIN", "HRD", "KARYAWAN"});
        cbRole.setFont(cbRole.getFont().deriveFont(14f));
        formPanel.add(cbRole, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(btnLogin.getFont().deriveFont(Font.BOLD, 14f));
        btnLogin.setPreferredSize(new Dimension(140, 38));

        JButton btnSignUp = new JButton("Daftar Akun");
        btnSignUp.setFont(btnSignUp.getFont().deriveFont(Font.PLAIN, 14f));
        btnSignUp.setPreferredSize(new Dimension(140, 38));

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

        if ("KARYAWAN".equals(role)) {
            String query = "SELECT * FROM karyawan WHERE username = ? AND password = ?";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String id = rs.getString("id_karyawan");
                        String name = rs.getString("nama");
                        new KaryawanDashboardFrame(id, name).setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Username atau Password Karyawan salah!", "Gagal Login", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Kesalahan database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
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
    }

    private void openSignUpDialog() {
        JDialog signupDialog = new JDialog(this, "Daftar Akun Admin/HRD", true);
        signupDialog.setSize(500, 450);
        signupDialog.setLocationRelativeTo(this);
        signupDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Registrasi Akun Baru", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Data Akun"),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cbNewRole = new JComboBox<>(new String[]{"ADMIN", "HRD"});
        cbNewRole.setFont(cbNewRole.getFont().deriveFont(14f));

        JTextField txtCode = new JTextField(20);
        txtCode.setEditable(false);
        txtCode.setFont(txtCode.getFont().deriveFont(14f));

        JTextField txtName = new JTextField(20);
        txtName.setFont(txtName.getFont().deriveFont(14f));

        JTextField txtNewUser = new JTextField(20);
        txtNewUser.setFont(txtNewUser.getFont().deriveFont(14f));

        JPasswordField txtNewPass = new JPasswordField(20);
        txtNewPass.setFont(txtNewPass.getFont().deriveFont(14f));

        String[] labels = {"Hak Akses (Role):", "Kode (Otomatis):", "Nama Lengkap:", "Username:", "Password:"};
        JComponent[] fields = {cbNewRole, txtCode, txtName, txtNewUser, txtNewPass};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 14f));
            formPanel.add(lbl, gbc);

            gbc.gridx = 1; gbc.gridy = i; gbc.weightx = 1.0;
            formPanel.add(fields[i], gbc);
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);

        Runnable updateCode = () -> {
            String role = (String) cbNewRole.getSelectedItem();
            String prefix = "ADMIN".equals(role) ? "ADM-" : "HRD-";
            txtCode.setText(DatabaseHelper.generateNextId("admin", "kd_admin", prefix));
        };
        cbNewRole.addActionListener(e -> updateCode.run());
        updateCode.run();

        JButton btnSave = new JButton("Daftar Akun");
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD, 14f));
        btnSave.setPreferredSize(new Dimension(160, 38));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
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

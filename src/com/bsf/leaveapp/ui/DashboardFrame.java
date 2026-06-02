package com.bsf.leaveapp.ui;

import com.bsf.leaveapp.database.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardFrame extends JFrame {
    private String userName;
    private String userRole;

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Overview statistics label refs
    private JLabel lblTotalEmp;
    private JLabel lblTotalDept;
    private JLabel lblPendingReq;
    private JLabel lblApprovedReq;

    public DashboardFrame(String userName, String userRole) {
        this.userName = userName;
        this.userRole = userRole;

        setTitle("PT BSF - Dashboard (" + userRole + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Core Layout structure: Header (North), Sidebar (West), Content (Center)
        setLayout(new BorderLayout());

        // Header Panel setup
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(21, 25, 28));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblLogo = new JLabel("PT BSF LEAVE APP");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(new Color(57, 137, 227));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel lblWelcome = new JLabel("Halo, " + userName + " (" + userRole + ")");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(180, 50, 50));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        userPanel.add(lblWelcome);
        userPanel.add(btnLogout);

        headerPanel.add(lblLogo, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Navigation Sidebar setup
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(30, 34, 37));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(43, 43, 43));

        // Create Panel Instances
        JPanel overviewPanel = createOverviewPanel();
        KaryawanPanel karyawanPanel = new KaryawanPanel(userRole);
        JabatanPanel jabatanPanel = new JabatanPanel(userRole);
        CutiPanel cutiPanel = new CutiPanel(userRole);
        PengajuanCutiPanel pengajuanPanel = new PengajuanCutiPanel(userName, userRole, this);
        LaporanPanel laporanPanel = new LaporanPanel(userName, userRole);

        // Register panels to the card container
        contentPanel.add(overviewPanel, "DASHBOARD");
        contentPanel.add(karyawanPanel, "KARYAWAN");
        contentPanel.add(jabatanPanel, "JABATAN");
        contentPanel.add(cutiPanel, "CUTI");
        contentPanel.add(pengajuanPanel, "PENGAJUAN");
        contentPanel.add(laporanPanel, "LAPORAN");

        // Navigation links
        addSidebarButton(sidebarPanel, "Dashboard", "DASHBOARD");
        addSidebarButton(sidebarPanel, "Data Karyawan", "KARYAWAN");
        addSidebarButton(sidebarPanel, "Data Jabatan", "JABATAN");
        addSidebarButton(sidebarPanel, "Data Cuti", "CUTI");
        addSidebarButton(sidebarPanel, "Pengajuan Cuti", "PENGAJUAN");
        addSidebarButton(sidebarPanel, "Laporan", "LAPORAN");

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Load initial dashboard statistics
        refreshStats();
    }

    // Helper to generate consistent navigation buttons in sidebar
    private void addSidebarButton(JPanel parent, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        btn.setBackground(new Color(43, 43, 43));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            if ("DASHBOARD".equals(cardName)) {
                refreshStats();
            }
            // Trigger refresh on specific panels when navigation changes
            Component comp = getActivePanel(cardName);
            if (comp instanceof KaryawanPanel) {
                ((KaryawanPanel) comp).refreshData();
            } else if (comp instanceof JabatanPanel) {
                ((JabatanPanel) comp).refreshData();
            } else if (comp instanceof CutiPanel) {
                ((CutiPanel) comp).refreshData();
            } else if (comp instanceof PengajuanCutiPanel) {
                ((PengajuanCutiPanel) comp).refreshData();
            } else if (comp instanceof LaporanPanel) {
                ((LaporanPanel) comp).refreshData();
            }
        });

        parent.add(btn);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private Component getActivePanel(String cardName) {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible()) {
                return comp;
            }
        }
        // Fallback matching card registration sequence
        switch (cardName) {
            case "KARYAWAN": return contentPanel.getComponent(1);
            case "JABATAN": return contentPanel.getComponent(2);
            case "CUTI": return contentPanel.getComponent(3);
            case "PENGAJUAN": return contentPanel.getComponent(4);
            case "LAPORAN": return contentPanel.getComponent(5);
            default: return contentPanel.getComponent(0);
        }
    }

    // Modern statistics widgets builder for Dashboard page
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("Statistik Pengajuan Cuti PT BSF");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsGrid.setBackground(new Color(30, 30, 30));
        cardsGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        lblTotalEmp = new JLabel("0");
        lblTotalDept = new JLabel("0");
        lblPendingReq = new JLabel("0");
        lblApprovedReq = new JLabel("0");

        cardsGrid.add(createStatCard("Total Karyawan", lblTotalEmp, new Color(57, 137, 227)));
        cardsGrid.add(createStatCard("Total Departemen/Jabatan", lblTotalDept, new Color(66, 179, 132)));
        cardsGrid.add(createStatCard("Pengajuan Pending (Menunggu)", lblPendingReq, new Color(241, 196, 15)));
        cardsGrid.add(createStatCard("Pengajuan Disetujui", lblApprovedReq, new Color(46, 204, 113)));

        panel.add(cardsGrid, BorderLayout.CENTER);
        return panel;
    }

    // Helper to generate modular dashboard cards
    private JPanel createStatCard(String title, JLabel valLabel, Color indicatorColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(43, 43, 43));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, indicatorColor),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.LIGHT_GRAY);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valLabel.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    // Reload counters from database in real-time
    public void refreshStats() {
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Count total employees
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM karyawan");
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) lblTotalEmp.setText(String.valueOf(rs.getInt(1)));
            }

            // Count total positions/departments
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(DISTINCT departemen) FROM jabatan");
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) lblTotalDept.setText(String.valueOf(rs.getInt(1)));
            }

            // Count pending leave requests
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM pengajuan_cuti WHERE status_pengajuan = 'PENDING'");
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) lblPendingReq.setText(String.valueOf(rs.getInt(1)));
            }

            // Count approved leave requests
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM pengajuan_cuti WHERE status_pengajuan = 'DISETUJUI'");
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) lblApprovedReq.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

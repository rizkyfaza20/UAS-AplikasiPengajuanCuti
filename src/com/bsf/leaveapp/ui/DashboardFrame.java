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

    private JLabel lblTotalEmp;
    private JLabel lblTotalDept;
    private JLabel lblPendingReq;
    private JLabel lblApprovedReq;

    public DashboardFrame(String userName, String userRole) {
        this.userName = userName;
        this.userRole = userRole;

        setTitle("PT BSF - Sistem Pengajuan Cuti (" + userRole + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblLogo = new JLabel("PT BSF - Sistem Pengajuan Cuti Karyawan");
        lblLogo.setFont(lblLogo.getFont().deriveFont(Font.BOLD, 14f));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JLabel lblWelcome = new JLabel("Halo, " + userName + " (" + userRole + ")");
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        userPanel.add(lblWelcome);
        userPanel.add(btnLogout);

        headerPanel.add(lblLogo, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Sidebar ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(190, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create panels
        JPanel overviewPanel = createOverviewPanel();
        KaryawanPanel karyawanPanel = new KaryawanPanel(userRole);
        JabatanPanel jabatanPanel = new JabatanPanel(userRole);
        CutiPanel cutiPanel = new CutiPanel(userRole);
        PengajuanCutiPanel pengajuanPanel = new PengajuanCutiPanel(userName, userRole, this);
        LaporanPanel laporanPanel = new LaporanPanel(userName, userRole);

        contentPanel.add(overviewPanel, "DASHBOARD");
        contentPanel.add(karyawanPanel, "KARYAWAN");
        contentPanel.add(jabatanPanel, "JABATAN");
        contentPanel.add(cutiPanel, "CUTI");
        contentPanel.add(pengajuanPanel, "PENGAJUAN");
        contentPanel.add(laporanPanel, "LAPORAN");

        addSidebarButton(sidebarPanel, "Dashboard", "DASHBOARD");
        addSidebarButton(sidebarPanel, "Data Karyawan", "KARYAWAN");
        addSidebarButton(sidebarPanel, "Data Jabatan", "JABATAN");
        addSidebarButton(sidebarPanel, "Data Cuti", "CUTI");
        addSidebarButton(sidebarPanel, "Pengajuan Cuti", "PENGAJUAN");
        addSidebarButton(sidebarPanel, "Laporan", "LAPORAN");
        sidebarPanel.add(Box.createVerticalGlue());

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        refreshStats();
    }

    private void addSidebarButton(JPanel parent, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 35));
        btn.setPreferredSize(new Dimension(180, 35));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            if ("DASHBOARD".equals(cardName)) refreshStats();
            Component comp = getActivePanel(cardName);
            if (comp instanceof KaryawanPanel) ((KaryawanPanel) comp).refreshData();
            else if (comp instanceof JabatanPanel) ((JabatanPanel) comp).refreshData();
            else if (comp instanceof CutiPanel) ((CutiPanel) comp).refreshData();
            else if (comp instanceof PengajuanCutiPanel) ((PengajuanCutiPanel) comp).refreshData();
            else if (comp instanceof LaporanPanel) ((LaporanPanel) comp).refreshData();
        });

        parent.add(btn);
        parent.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private Component getActivePanel(String cardName) {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible()) return comp;
        }
        switch (cardName) {
            case "KARYAWAN": return contentPanel.getComponent(1);
            case "JABATAN":  return contentPanel.getComponent(2);
            case "CUTI":     return contentPanel.getComponent(3);
            case "PENGAJUAN":return contentPanel.getComponent(4);
            case "LAPORAN":  return contentPanel.getComponent(5);
            default:         return contentPanel.getComponent(0);
        }
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Ringkasan Statistik Pengajuan Cuti PT BSF");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 15f));
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        cardsGrid.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        lblTotalEmp    = new JLabel("0", SwingConstants.CENTER);
        lblTotalDept   = new JLabel("0", SwingConstants.CENTER);
        lblPendingReq  = new JLabel("0", SwingConstants.CENTER);
        lblApprovedReq = new JLabel("0", SwingConstants.CENTER);

        cardsGrid.add(createStatCard("Total Karyawan", lblTotalEmp));
        cardsGrid.add(createStatCard("Total Jabatan/Departemen", lblTotalDept));
        cardsGrid.add(createStatCard("Pengajuan Menunggu (PENDING)", lblPendingReq));
        cardsGrid.add(createStatCard("Pengajuan Disetujui", lblApprovedReq));

        panel.add(cardsGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, JLabel valLabel) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.PLAIN, 12f));

        valLabel.setFont(valLabel.getFont().deriveFont(Font.BOLD, 32f));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshStats() {
        try (Connection conn = DatabaseHelper.getConnection()) {
            try (PreparedStatement p = conn.prepareStatement("SELECT COUNT(*) FROM karyawan");
                 ResultSet rs = p.executeQuery()) {
                if (rs.next()) lblTotalEmp.setText(String.valueOf(rs.getInt(1)));
            }
            try (PreparedStatement p = conn.prepareStatement("SELECT COUNT(DISTINCT departemen) FROM jabatan");
                 ResultSet rs = p.executeQuery()) {
                if (rs.next()) lblTotalDept.setText(String.valueOf(rs.getInt(1)));
            }
            try (PreparedStatement p = conn.prepareStatement("SELECT COUNT(*) FROM pengajuan_cuti WHERE status_pengajuan = 'PENDING'");
                 ResultSet rs = p.executeQuery()) {
                if (rs.next()) lblPendingReq.setText(String.valueOf(rs.getInt(1)));
            }
            try (PreparedStatement p = conn.prepareStatement("SELECT COUNT(*) FROM pengajuan_cuti WHERE status_pengajuan = 'DISETUJUI'");
                 ResultSet rs = p.executeQuery()) {
                if (rs.next()) lblApprovedReq.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

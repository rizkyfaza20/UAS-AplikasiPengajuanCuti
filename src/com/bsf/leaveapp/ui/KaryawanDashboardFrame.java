package com.bsf.leaveapp.ui;

import javax.swing.*;
import java.awt.*;

public class KaryawanDashboardFrame extends JFrame {
    private String idKaryawan;
    private String namaKaryawan;

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private KaryawanPengajuanPanel pengajuanPanel;

    public KaryawanDashboardFrame(String idKaryawan, String namaKaryawan) {
        this.idKaryawan = idKaryawan;
        this.namaKaryawan = namaKaryawan;

        setTitle("PT BSF - Dashboard Karyawan (" + namaKaryawan + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // --- Header (same style as DashboardFrame) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblLogo = new JLabel("PT BSF - Portal Karyawan");
        lblLogo.setFont(lblLogo.getFont().deriveFont(Font.BOLD, 16f));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JLabel lblWelcome = new JLabel("Halo, " + namaKaryawan + " (" + idKaryawan + ")");
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(Font.PLAIN, 14f));
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(btnLogout.getFont().deriveFont(14f));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });
        userPanel.add(lblWelcome);
        userPanel.add(btnLogout);

        headerPanel.add(lblLogo, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Sidebar (same plain style as DashboardFrame) ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));

        JButton btnPengajuan = new JButton("Pengajuan Cuti");
        btnPengajuan.setMaximumSize(new Dimension(190, 38));
        btnPengajuan.setPreferredSize(new Dimension(190, 38));
        btnPengajuan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPengajuan.setHorizontalAlignment(SwingConstants.LEFT);
        btnPengajuan.setFont(btnPengajuan.getFont().deriveFont(14f));

        sidebarPanel.add(btnPengajuan);
        sidebarPanel.add(Box.createVerticalGlue());

        add(sidebarPanel, BorderLayout.WEST);

        // --- Content Area ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        pengajuanPanel = new KaryawanPengajuanPanel(idKaryawan, namaKaryawan, this);
        contentPanel.add(pengajuanPanel, "PENGAJUAN");

        add(contentPanel, BorderLayout.CENTER);

        // Event Listeners
        btnPengajuan.addActionListener(e -> {
            cardLayout.show(contentPanel, "PENGAJUAN");
            pengajuanPanel.refreshData();
        });

        // Initial view
        cardLayout.show(contentPanel, "PENGAJUAN");
    }
}

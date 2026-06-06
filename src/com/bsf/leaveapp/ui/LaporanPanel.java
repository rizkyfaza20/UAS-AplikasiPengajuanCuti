package com.bsf.leaveapp.ui;

import com.bsf.leaveapp.database.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LaporanPanel extends JPanel {
    private String userName;
    private String userRole;
    private JComboBox<String> cbReportType;
    private JTextArea txtReportArea;

    public LaporanPanel(String userName, String userRole) {
        this.userName = userName;
        this.userRole = userRole;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Laporan & Cetak Dokumen");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        add(lblTitle, BorderLayout.NORTH);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createEtchedBorder());

        cbReportType = new JComboBox<>(new String[]{
                "Laporan Data Karyawan",
                "Laporan Data Jabatan",
                "Laporan Data Cuti",
                "Laporan Pengajuan Cuti",
                "Laporan Data Admin"
        });

        JButton btnGenerate = new JButton("Tampilkan");
        JButton btnPrint    = new JButton("Cetak Laporan");
        JButton btnExport   = new JButton("Ekspor ke File (.txt)");

        controlPanel.add(new JLabel("Jenis Laporan:"));
        controlPanel.add(cbReportType);
        controlPanel.add(btnGenerate);
        controlPanel.add(btnPrint);
        controlPanel.add(btnExport);

        add(controlPanel, BorderLayout.NORTH);

        // Report preview
        txtReportArea = new JTextArea();
        txtReportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtReportArea.setEditable(false);
        txtReportArea.setMargin(new Insets(10, 15, 10, 15));
        add(new JScrollPane(txtReportArea), BorderLayout.CENTER);

        btnGenerate.addActionListener(e -> generateReport());
        btnPrint.addActionListener(e -> printReport());
        btnExport.addActionListener(e -> exportReport());

        generateReport();
    }

    public void refreshData() { generateReport(); }

    private void generateReport() {
        String type = (String) cbReportType.getSelectedItem();
        StringBuilder sb = new StringBuilder();

        sb.append("=========================================================================\n");
        sb.append("                          PT BSF INDONESIA                               \n");
        sb.append("              Jl. Lingkar Luar Selatan Kavling 56, Ciracas               \n");
        sb.append("                             JAKARTA TIMUR                               \n");
        sb.append("=========================================================================\n");
        sb.append(" Laporan   : ").append(type).append("\n");
        sb.append(" Dicetak   : ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append(" Petugas   : ").append(userName).append(" (").append(userRole).append(")\n");
        sb.append("=========================================================================\n\n");

        try (Connection conn = DatabaseHelper.getConnection()) {
            if ("Laporan Data Karyawan".equals(type)) {
                sb.append(String.format("%-12s | %-25s | %-13s | %-10s | %-15s\n",
                        "ID Karyawan", "Nama Karyawan", "No HP", "Status", "Jabatan"));
                sb.append("-".repeat(85)).append("\n");
                try (PreparedStatement p = conn.prepareStatement(
                        "SELECT k.*, j.nama_jabatan FROM karyawan k LEFT JOIN jabatan j ON k.kd_jabatan = j.kd_jabatan");
                     ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        sb.append(String.format("%-12s | %-25s | %-13s | %-10s | %-15s\n",
                                rs.getString("id_karyawan"), rs.getString("nama"),
                                rs.getString("nomor_hp"), rs.getString("status"),
                                rs.getString("nama_jabatan") != null ? rs.getString("nama_jabatan") : "-"));
                    }
                }
            } else if ("Laporan Data Jabatan".equals(type)) {
                sb.append(String.format("%-12s | %-25s | %-25s\n", "Kode Jabatan", "Nama Jabatan", "Departemen"));
                sb.append("-".repeat(70)).append("\n");
                try (PreparedStatement p = conn.prepareStatement("SELECT * FROM jabatan");
                     ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        sb.append(String.format("%-12s | %-25s | %-25s\n",
                                rs.getString("kd_jabatan"), rs.getString("nama_jabatan"), rs.getString("departemen")));
                    }
                }
            } else if ("Laporan Data Cuti".equals(type)) {
                sb.append(String.format("%-10s | %-12s | %-25s | %-15s\n",
                        "Kode Cuti", "ID Karyawan", "Nama Karyawan", "Sisa Saldo"));
                sb.append("-".repeat(70)).append("\n");
                try (PreparedStatement p = conn.prepareStatement(
                        "SELECT c.*, k.nama FROM cuti c JOIN karyawan k ON c.id_karyawan = k.id_karyawan");
                     ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        sb.append(String.format("%-10s | %-12s | %-25s | %-15s\n",
                                rs.getString("kd_cuti"), rs.getString("id_karyawan"),
                                rs.getString("nama"), rs.getInt("jumlah_cuti") + " Hari"));
                    }
                }
            } else if ("Laporan Pengajuan Cuti".equals(type)) {
                sb.append(String.format("%-10s | %-20s | %-12s | %-12s | %-10s | %-10s\n",
                        "Kode Req", "Nama Karyawan", "Tgl Mulai", "Tgl Akhir", "Masa", "Status"));
                sb.append("-".repeat(85)).append("\n");
                try (PreparedStatement p = conn.prepareStatement(
                        "SELECT p.*, k.nama FROM pengajuan_cuti p JOIN karyawan k ON p.id_karyawan = k.id_karyawan");
                     ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        sb.append(String.format("%-10s | %-20s | %-12s | %-12s | %-10s | %-10s\n",
                                rs.getString("kd_daftar_cuti"), rs.getString("nama"),
                                rs.getString("tanggal_awal"), rs.getString("tanggal_akhir"),
                                rs.getInt("masa_cuti") + " Hari", rs.getString("status_pengajuan")));
                    }
                }
            } else if ("Laporan Data Admin".equals(type)) {
                sb.append(String.format("%-12s | %-25s | %-20s | %-10s\n",
                        "Kode Admin", "Nama Lengkap", "Username", "Role"));
                sb.append("-".repeat(75)).append("\n");
                try (PreparedStatement p = conn.prepareStatement("SELECT * FROM admin");
                     ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        sb.append(String.format("%-12s | %-25s | %-20s | %-10s\n",
                                rs.getString("kd_admin"), rs.getString("nama_admin"),
                                rs.getString("username"), rs.getString("hak_akses")));
                    }
                }
            }
        } catch (SQLException e) {
            sb.append("Kesalahan database: ").append(e.getMessage());
        }

        sb.append("\n\n=========================================================================\n");
        sb.append("                                               Petugas,\n\n\n\n");
        sb.append("                                               ( ").append(userName).append(" )\n");
        sb.append("=========================================================================\n");

        txtReportArea.setText(sb.toString());
        txtReportArea.setCaretPosition(0);
    }

    private void printReport() {
        if (txtReportArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            boolean done = txtReportArea.print();
            if (done) JOptionPane.showMessageDialog(this, "Cetak dokumen selesai!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReport() {
        if (txtReportArea.getText().trim().isEmpty()) return;
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Laporan");
        fc.setSelectedFile(new File(((String) cbReportType.getSelectedItem()).replace(" ", "_") + ".txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                fw.write(txtReportArea.getText());
                JOptionPane.showMessageDialog(this, "Laporan diekspor ke:\n" + fc.getSelectedFile().getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

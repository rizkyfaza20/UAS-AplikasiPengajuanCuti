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

    private JButton btnPrint, btnExport;

    public LaporanPanel(String userName, String userRole) {
        this.userName = userName;
        this.userRole = userRole;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTitle = new JLabel("Laporan & Cetak Dokumen");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        add(lblTitle, BorderLayout.NORTH);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        controlPanel.setBackground(new Color(43, 43, 43));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblSel = new JLabel("Pilih Jenis Laporan:");
        lblSel.setForeground(Color.WHITE);

        cbReportType = new JComboBox<>(new String[]{
                "Laporan Data Karyawan",
                "Laporan Data Jabatan",
                "Laporan Data Cuti",
                "Laporan Pengajuan Cuti",
                "Laporan Data Admin"
        });

        JButton btnGenerate = new JButton("Tampilkan");
        btnPrint = new JButton("Cetak Laporan");
        btnPrint.setBackground(new Color(46, 204, 113));
        btnPrint.setForeground(Color.WHITE);

        btnExport = new JButton("Ekspor ke File");

        controlPanel.add(lblSel);
        controlPanel.add(cbReportType);
        controlPanel.add(btnGenerate);
        controlPanel.add(btnPrint);
        controlPanel.add(btnExport);

        add(controlPanel, BorderLayout.CENTER);

        // Report Preview Area
        txtReportArea = new JTextArea();
        txtReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReportArea.setBackground(new Color(25, 25, 25));
        txtReportArea.setForeground(Color.LIGHT_GRAY);
        txtReportArea.setEditable(false);
        txtReportArea.setMargin(new Insets(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(txtReportArea);
        scrollPane.setPreferredSize(new Dimension(0, 480));
        add(scrollPane, BorderLayout.SOUTH);

        // Core Event bindings
        btnGenerate.addActionListener(e -> generateReport());
        btnPrint.addActionListener(e -> printReport());
        btnExport.addActionListener(e -> exportReport());

        // Default initial load
        generateReport();
    }

    public void refreshData() {
        generateReport();
    }

    // Connects to DB, queries requested report, and formats text preview
    private void generateReport() {
        String type = (String) cbReportType.getSelectedItem();
        StringBuilder sb = new StringBuilder();

        // Print header brand
        sb.append("=========================================================================\n");
        sb.append("                            PT BSF INDONESIA                             \n");
        sb.append("               Jl. Lingkar Luar Selatan Kavling 56, Ciracas               \n");
        sb.append("                               JAKARTA TIMUR                              \n");
        sb.append("=========================================================================\n");
        sb.append(" Jenis Laporan : ").append(type.toUpperCase()).append("\n");
        sb.append(" Waktu Cetak   : ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("=========================================================================\n\n");

        if ("Laporan Data Karyawan".equals(type)) {
            sb.append(String.format("%-10s | %-25s | %-12s | %-12s | %-15s\n", "ID Karyawan", "Nama Karyawan", "No HP", "Status", "Jabatan"));
            sb.append("-------------------------------------------------------------------------\n");
            String sql = "SELECT k.*, j.nama_jabatan FROM karyawan k LEFT JOIN jabatan j ON k.kd_jabatan = j.kd_jabatan";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sb.append(String.format("%-10s | %-25s | %-12s | %-12s | %-15s\n",
                            rs.getString("id_karyawan"),
                            rs.getString("nama"),
                            rs.getString("nomor_hp"),
                            rs.getString("status"),
                            rs.getString("nama_jabatan") != null ? rs.getString("nama_jabatan") : "-"));
                }
            } catch (SQLException e) {
                sb.append("Kesalahan database saat memuat data: ").append(e.getMessage());
            }
        } else if ("Laporan Data Jabatan".equals(type)) {
            sb.append(String.format("%-12s | %-25s | %-25s\n", "Kode Jabatan", "Nama Jabatan", "Departemen"));
            sb.append("-------------------------------------------------------------------------\n");
            String sql = "SELECT * FROM jabatan";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sb.append(String.format("%-12s | %-25s | %-25s\n",
                            rs.getString("kd_jabatan"),
                            rs.getString("nama_jabatan"),
                            rs.getString("departemen")));
                }
            } catch (SQLException e) {
                sb.append("Kesalahan database saat memuat data: ").append(e.getMessage());
            }
        } else if ("Laporan Data Cuti".equals(type)) {
            sb.append(String.format("%-10s | %-12s | %-25s | %-15s\n", "Kode Cuti", "ID Karyawan", "Nama Karyawan", "Sisa Saldo Cuti"));
            sb.append("-------------------------------------------------------------------------\n");
            String sql = "SELECT c.*, k.nama FROM cuti c JOIN karyawan k ON c.id_karyawan = k.id_karyawan";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sb.append(String.format("%-10s | %-12s | %-25s | %-15s\n",
                            rs.getString("kd_cuti"),
                            rs.getString("id_karyawan"),
                            rs.getString("nama"),
                            rs.getInt("jumlah_cuti") + " Hari"));
                }
            } catch (SQLException e) {
                sb.append("Kesalahan database saat memuat data: ").append(e.getMessage());
            }
        } else if ("Laporan Pengajuan Cuti".equals(type)) {
            sb.append(String.format("%-10s | %-25s | %-12s | %-12s | %-12s | %-10s\n", "Kode Req", "Nama Karyawan", "Tgl Mulai", "Tgl Akhir", "Masa Cuti", "Status"));
            sb.append("-------------------------------------------------------------------------\n");
            String sql = "SELECT p.*, k.nama FROM pengajuan_cuti p JOIN karyawan k ON p.id_karyawan = k.id_karyawan";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sb.append(String.format("%-10s | %-25s | %-12s | %-12s | %-12s | %-10s\n",
                            rs.getString("kd_daftar_cuti"),
                            rs.getString("nama"),
                            rs.getString("tanggal_awal"),
                            rs.getString("tanggal_akhir"),
                            rs.getInt("masa_cuti") + " Hari",
                            rs.getString("status_pengajuan")));
                }
            } catch (SQLException e) {
                sb.append("Kesalahan database saat memuat data: ").append(e.getMessage());
            }
        } else if ("Laporan Data Admin".equals(type)) {
            sb.append(String.format("%-12s | %-25s | %-20s | %-10s\n", "Kode Admin", "Nama Lengkap", "Username", "Role"));
            sb.append("-------------------------------------------------------------------------\n");
            String sql = "SELECT * FROM admin";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sb.append(String.format("%-12s | %-25s | %-20s | %-10s\n",
                            rs.getString("kd_admin"),
                            rs.getString("nama_admin"),
                            rs.getString("username"),
                            rs.getString("hak_akses")));
                }
            } catch (SQLException e) {
                sb.append("Kesalahan database saat memuat data: ").append(e.getMessage());
            }
        }

        sb.append("\n\n=========================================================================\n");
        sb.append("                                                 Petugas Tanda Tangan     \n\n\n\n");
        sb.append("                                                 ( ").append(userName).append(" )\n");
        sb.append("=========================================================================\n");

        txtReportArea.setText(sb.toString());
        txtReportArea.setCaretPosition(0);
    }

    // Invokes the system printer dialog to print the generated text report directly
    private void printReport() {
        if (txtReportArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data laporan untuk dicetak!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            boolean complete = txtReportArea.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Cetak dokumen selesai!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Exports report text content into a local file
    private void exportReport() {
        if (txtReportArea.getText().trim().isEmpty()) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan");
        String filename = ((String) cbReportType.getSelectedItem()).replace(" ", "_") + ".txt";
        fileChooser.setSelectedFile(new File(filename));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(fileToSave)) {
                fw.write(txtReportArea.getText());
                JOptionPane.showMessageDialog(this, "Laporan berhasil diekspor ke:\n" + fileToSave.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

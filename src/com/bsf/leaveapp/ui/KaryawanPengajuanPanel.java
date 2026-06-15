package com.bsf.leaveapp.ui;

import com.bsf.leaveapp.database.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class KaryawanPengajuanPanel extends JPanel {
    private String idKaryawan;
    private String namaKaryawan;
    private KaryawanDashboardFrame parentFrame;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtKdDaftar;
    private JComboBox<String> cbTglAwal;
    private JTextField txtTglAkhir;
    private JComboBox<Integer> cbDurasi;
    private JComboBox<String> cbJenisCuti;
    private JTextField txtKeterangan;

    private JLabel lblSisaCuti;
    private int currentSisaCuti = 0;

    private JButton btnAdd, btnClear;

    public KaryawanPengajuanPanel(String idKaryawan, String namaKaryawan, KaryawanDashboardFrame parentFrame) {
        this.idKaryawan = idKaryawan;
        this.namaKaryawan = namaKaryawan;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Pengajuan Cuti Saya");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        lblSisaCuti = new JLabel("Sisa Saldo Cuti: Memuat...");
        lblSisaCuti.setFont(lblSisaCuti.getFont().deriveFont(Font.BOLD, 12f));
        lblSisaCuti.setForeground(new Color(39, 174, 96));
        headerPanel.add(lblSisaCuti, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "Kode Req", "Tgl Awal", "Tgl Akhir", "Masa Cuti", "Jenis Cuti", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        // Form panel
        JPanel formPanel = new JPanel(new BorderLayout(5, 10));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Form Pengajuan Cuti Baru"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.setPreferredSize(new Dimension(300, 0));

        JPanel fields = new JPanel(new GridLayout(12, 1, 5, 3));

        fields.add(new JLabel("Kode Pengajuan:"));
        txtKdDaftar = new JTextField();
        txtKdDaftar.setEditable(false);
        fields.add(txtKdDaftar);

        fields.add(new JLabel("Tanggal Mulai:"));
        cbTglAwal = new JComboBox<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            cbTglAwal.addItem(today.plusDays(i).toString());
        }
        fields.add(cbTglAwal);

        fields.add(new JLabel("Durasi (Hari):"));
        cbDurasi = new JComboBox<>();
        for (int i = 1; i <= 14; i++) {
            cbDurasi.addItem(i);
        }
        fields.add(cbDurasi);

        fields.add(new JLabel("Tanggal Selesai (Otomatis):"));
        txtTglAkhir = new JTextField();
        txtTglAkhir.setEditable(false);
        fields.add(txtTglAkhir);

        fields.add(new JLabel("Jenis Cuti:"));
        cbJenisCuti = new JComboBox<>(new String[]{"Cuti Sakit", "Cuti Melahirkan", "Cuti Tahunan", "Cuti Alasan Penting"});
        fields.add(cbJenisCuti);

        fields.add(new JLabel("Keterangan:"));
        txtKeterangan = new JTextField();
        fields.add(txtKeterangan);

        formPanel.add(fields, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        btnAdd    = new JButton("Ajukan Cuti");
        btnClear  = new JButton("Bersihkan");
        actionPanel.add(btnAdd);
        actionPanel.add(btnClear);
        formPanel.add(actionPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> handleAdd());
        btnClear.addActionListener(e -> clearForm());

        cbTglAwal.addActionListener(e -> updateEndDate());
        cbDurasi.addActionListener(e -> updateEndDate());

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);

        refreshData();
        updateEndDate();
    }

    private void updateEndDate() {
        if (cbTglAwal.getSelectedItem() != null && cbDurasi.getSelectedItem() != null) {
            LocalDate start = LocalDate.parse((String) cbTglAwal.getSelectedItem());
            int duration = (Integer) cbDurasi.getSelectedItem();
            // End date is start date + (duration - 1) days
            LocalDate end = start.plusDays(duration - 1);
            txtTglAkhir.setText(end.toString());
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        
        // Fetch leave requests for this employee
        String sql = "SELECT * FROM pengajuan_cuti WHERE id_karyawan = ? ORDER BY kd_daftar_cuti ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, idKaryawan);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getString("kd_daftar_cuti"),
                            rs.getString("tanggal_awal"),
                            rs.getString("tanggal_akhir"),
                            rs.getInt("masa_cuti"),
                            rs.getString("jenis_cuti"),
                            rs.getString("status_pengajuan")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fetch remaining quota
        String sqlQuota = "SELECT jumlah_cuti FROM cuti WHERE id_karyawan = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuota)) {
            pstmt.setString(1, idKaryawan);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentSisaCuti = rs.getInt("jumlah_cuti");
                    lblSisaCuti.setText("Sisa Saldo Cuti: " + currentSisaCuti + " Hari");
                } else {
                    lblSisaCuti.setText("Sisa Saldo Cuti: Tidak Ditemukan");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        clearForm();
    }

    private void clearForm() {
        txtKdDaftar.setText(DatabaseHelper.generateNextId("pengajuan_cuti", "kd_daftar_cuti", "REQ-"));
        cbTglAwal.setSelectedIndex(0);
        cbDurasi.setSelectedIndex(0);
        cbJenisCuti.setSelectedIndex(0);
        txtKeterangan.setText("");
        table.clearSelection();
    }

    private void handleAdd() {
        if (cbTglAwal.getSelectedItem() == null || cbDurasi.getSelectedItem() == null) return;

        int duration = (Integer) cbDurasi.getSelectedItem();
        String code  = txtKdDaftar.getText();
        String start = (String) cbTglAwal.getSelectedItem();
        String end   = txtTglAkhir.getText();
        String jenis = (String) cbJenisCuti.getSelectedItem();
        String desc  = txtKeterangan.getText().trim();

        if ("Cuti Tahunan".equals(jenis) && duration > currentSisaCuti) {
            JOptionPane.showMessageDialog(this, "Sisa kuota cuti Anda hanya " + currentSisaCuti + " hari!", "Batas Kuota", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pQ = conn.prepareStatement("SELECT kd_cuti FROM cuti WHERE id_karyawan = ?")) {
            pQ.setString(1, idKaryawan);
            try (ResultSet rs = pQ.executeQuery()) {
                if (rs.next()) {
                    String kdCuti = rs.getString("kd_cuti");
                    try (PreparedStatement p = conn.prepareStatement(
                            "INSERT INTO pengajuan_cuti (kd_daftar_cuti,id_karyawan,kd_cuti,tanggal_awal,tanggal_akhir,masa_cuti,jenis_cuti,keterangan,status_pengajuan) VALUES (?,?,?,?,?,?,?,?,'PENDING')")) {
                        p.setString(1, code); p.setString(2, idKaryawan); p.setString(3, kdCuti);
                        p.setString(4, start); p.setString(5, end); p.setInt(6, duration);
                        p.setString(7, jenis); p.setString(8, desc);
                        p.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Pengajuan berhasil! Menunggu persetujuan HRD.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        refreshData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Data cuti tidak ditemukan! Hubungi Admin.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan pengajuan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package com.bsf.leaveapp.ui;

import com.bsf.leaveapp.database.DatabaseHelper;
import com.bsf.leaveapp.model.Karyawan;

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

public class PengajuanCutiPanel extends JPanel {
    private String userName;
    private String userRole;
    private DashboardFrame parentFrame;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtKdDaftar;
    private JComboBox<Karyawan> cbKaryawan;
    private JTextField txtTglAwal;
    private JTextField txtTglAkhir;
    private JTextField txtMasaCuti;
    private JComboBox<String> cbJenisCuti;
    private JTextField txtKeterangan;

    private JButton btnAdd, btnEdit, btnDelete, btnClear;
    private JButton btnApprove, btnReject;

    public PengajuanCutiPanel(String userName, String userRole, DashboardFrame parentFrame) {
        this.userName    = userName;
        this.userRole    = userRole;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Pengajuan & Persetujuan Cuti");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        add(lblTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "Kode Req", "ID Karyawan", "Nama Karyawan", "Departemen",
                "Tgl Awal", "Tgl Akhir", "Masa Cuti", "Jenis Cuti", "Status"
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
                BorderFactory.createTitledBorder("Form Pengajuan Cuti"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.setPreferredSize(new Dimension(300, 0));

        JPanel fields = new JPanel(new GridLayout(14, 1, 5, 3));

        fields.add(new JLabel("Kode Pengajuan:"));
        txtKdDaftar = new JTextField();
        txtKdDaftar.setEditable(false);
        fields.add(txtKdDaftar);

        fields.add(new JLabel("Karyawan:"));
        cbKaryawan = new JComboBox<>();
        fields.add(cbKaryawan);

        fields.add(new JLabel("Tanggal Mulai (YYYY-MM-DD):"));
        txtTglAwal = new JTextField();
        fields.add(txtTglAwal);

        fields.add(new JLabel("Tanggal Selesai (YYYY-MM-DD):"));
        txtTglAkhir = new JTextField();
        fields.add(txtTglAkhir);

        fields.add(new JLabel("Durasi (Hari - Otomatis):"));
        txtMasaCuti = new JTextField("0");
        txtMasaCuti.setEditable(false);
        fields.add(txtMasaCuti);

        fields.add(new JLabel("Jenis Cuti:"));
        cbJenisCuti = new JComboBox<>(new String[]{"Cuti Sakit", "Cuti Melahirkan", "Cuti Tahunan", "Cuti Alasan Penting"});
        fields.add(cbJenisCuti);

        fields.add(new JLabel("Keterangan:"));
        txtKeterangan = new JTextField();
        fields.add(txtKeterangan);

        formPanel.add(fields, BorderLayout.NORTH);

        // Buttons by role
        if ("ADMIN".equals(userRole)) {
            JPanel actionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            btnAdd    = new JButton("Ajukan Cuti");
            btnEdit   = new JButton("Ubah");
            btnDelete = new JButton("Hapus");
            btnClear  = new JButton("Bersihkan");
            actionPanel.add(btnAdd);
            actionPanel.add(btnEdit);
            actionPanel.add(btnDelete);
            actionPanel.add(btnClear);
            formPanel.add(actionPanel, BorderLayout.SOUTH);

            btnAdd.addActionListener(e -> handleAdd());
            btnEdit.addActionListener(e -> handleEdit());
            btnDelete.addActionListener(e -> handleDelete());
            btnClear.addActionListener(e -> clearForm());
        } else {
            cbKaryawan.setEnabled(false);
            txtTglAwal.setEditable(false);
            txtTglAkhir.setEditable(false);
            cbJenisCuti.setEnabled(false);
            txtKeterangan.setEditable(false);

            JPanel hrdPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            btnApprove = new JButton("Setujui (Approve)");
            btnReject  = new JButton("Tolak (Reject)");
            hrdPanel.add(btnApprove);
            hrdPanel.add(btnReject);
            formPanel.add(hrdPanel, BorderLayout.SOUTH);

            btnApprove.addActionListener(e -> updateStatus("DISETUJUI"));
            btnReject.addActionListener(e -> updateStatus("DITOLAK"));
        }

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);

        table.getSelectionModel().addListSelectionListener(e -> handleTableSelection());

        refreshData();
    }

    public void refreshData() {
        loadKaryawanComboBox();
        tableModel.setRowCount(0);
        String sql = "SELECT p.*, k.nama, j.departemen FROM pengajuan_cuti p " +
                     "JOIN karyawan k ON p.id_karyawan = k.id_karyawan " +
                     "LEFT JOIN jabatan j ON k.kd_jabatan = j.kd_jabatan " +
                     "ORDER BY p.kd_daftar_cuti ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("kd_daftar_cuti"),
                        rs.getString("id_karyawan"),
                        rs.getString("nama"),
                        rs.getString("departemen") != null ? rs.getString("departemen") : "-",
                        rs.getString("tanggal_awal"),
                        rs.getString("tanggal_akhir"),
                        rs.getInt("masa_cuti"),
                        rs.getString("jenis_cuti"),
                        rs.getString("status_pengajuan")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        clearForm();
    }

    private void loadKaryawanComboBox() {
        cbKaryawan.removeAllItems();
        String sql = "SELECT * FROM karyawan ORDER BY nama ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                cbKaryawan.addItem(new Karyawan(
                        rs.getString("id_karyawan"), rs.getString("nama"),
                        rs.getString("jenis_kelamin"), rs.getString("alamat"),
                        rs.getString("nomor_hp"), rs.getString("status"),
                        rs.getString("kd_jabatan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String code  = (String) tableModel.getValueAt(row, 0);
            String empId = (String) tableModel.getValueAt(row, 1);
            txtKdDaftar.setText(code);

            for (int i = 0; i < cbKaryawan.getItemCount(); i++) {
                if (cbKaryawan.getItemAt(i).getIdKaryawan().equals(empId)) {
                    cbKaryawan.setSelectedIndex(i);
                    break;
                }
            }

            txtTglAwal.setText((String) tableModel.getValueAt(row, 4));
            txtTglAkhir.setText((String) tableModel.getValueAt(row, 5));
            txtMasaCuti.setText(String.valueOf(tableModel.getValueAt(row, 6)));
            cbJenisCuti.setSelectedItem(tableModel.getValueAt(row, 7));

            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement p = conn.prepareStatement("SELECT keterangan FROM pengajuan_cuti WHERE kd_daftar_cuti = ?")) {
                p.setString(1, code);
                try (ResultSet rs = p.executeQuery()) {
                    if (rs.next()) txtKeterangan.setText(rs.getString("keterangan"));
                }
            } catch (SQLException e) { e.printStackTrace(); }

            String status = (String) tableModel.getValueAt(row, 8);
            if ("ADMIN".equals(userRole)) {
                boolean isPending = "PENDING".equals(status);
                btnAdd.setEnabled(false);
                btnEdit.setEnabled(isPending);
                btnDelete.setEnabled(true);
            } else {
                boolean isPending = "PENDING".equals(status);
                btnApprove.setEnabled(isPending);
                btnReject.setEnabled(isPending);
            }
        }
    }

    private void clearForm() {
        txtKdDaftar.setText(DatabaseHelper.generateNextId("pengajuan_cuti", "kd_daftar_cuti", "REQ-"));
        if (cbKaryawan.getItemCount() > 0) cbKaryawan.setSelectedIndex(0);
        txtTglAwal.setText("");
        txtTglAkhir.setText("");
        txtMasaCuti.setText("0");
        cbJenisCuti.setSelectedIndex(0);
        txtKeterangan.setText("");
        table.clearSelection();
        if ("ADMIN".equals(userRole)) {
            btnAdd.setEnabled(true);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } else {
            btnApprove.setEnabled(false);
            btnReject.setEnabled(false);
        }
    }

    private int calculateDuration() {
        try {
            LocalDate start = LocalDate.parse(txtTglAwal.getText().trim());
            LocalDate end   = LocalDate.parse(txtTglAkhir.getText().trim());
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            if (days < 1) {
                JOptionPane.showMessageDialog(this, "Tanggal selesai tidak boleh sebelum tanggal mulai!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return -1;
            }
            txtMasaCuti.setText(String.valueOf(days));
            return (int) days;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid! Gunakan YYYY-MM-DD.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
    }

    private void handleAdd() {
        Karyawan emp = (Karyawan) cbKaryawan.getSelectedItem();
        if (emp == null) { JOptionPane.showMessageDialog(this, "Pilih karyawan!", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        int duration = calculateDuration();
        if (duration == -1) return;

        String code  = txtKdDaftar.getText();
        String start = txtTglAwal.getText().trim();
        String end   = txtTglAkhir.getText().trim();
        String jenis = (String) cbJenisCuti.getSelectedItem();
        String desc  = txtKeterangan.getText().trim();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pQ = conn.prepareStatement("SELECT kd_cuti, jumlah_cuti FROM cuti WHERE id_karyawan = ?")) {
            pQ.setString(1, emp.getIdKaryawan());
            try (ResultSet rs = pQ.executeQuery()) {
                if (rs.next()) {
                    String kdCuti = rs.getString("kd_cuti");
                    int quota     = rs.getInt("jumlah_cuti");
                    if ("Cuti Tahunan".equals(jenis) && duration > quota) {
                        JOptionPane.showMessageDialog(this, "Sisa kuota cuti hanya " + quota + " hari!", "Batas Kuota", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    try (PreparedStatement p = conn.prepareStatement(
                            "INSERT INTO pengajuan_cuti (kd_daftar_cuti,id_karyawan,kd_cuti,tanggal_awal,tanggal_akhir,masa_cuti,jenis_cuti,keterangan,status_pengajuan) VALUES (?,?,?,?,?,?,?,?,'PENDING')")) {
                        p.setString(1, code); p.setString(2, emp.getIdKaryawan()); p.setString(3, kdCuti);
                        p.setString(4, start); p.setString(5, end); p.setInt(6, duration);
                        p.setString(7, jenis); p.setString(8, desc);
                        p.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Pengajuan berhasil! Menunggu persetujuan HRD.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        parentFrame.refreshStats();
                        refreshData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Karyawan tidak memiliki catatan saldo cuti!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        String code  = txtKdDaftar.getText();
        Karyawan emp = (Karyawan) cbKaryawan.getSelectedItem();
        if (emp == null) return;
        int duration = calculateDuration();
        if (duration == -1) return;

        String start = txtTglAwal.getText().trim();
        String end   = txtTglAkhir.getText().trim();
        String jenis = (String) cbJenisCuti.getSelectedItem();
        String desc  = txtKeterangan.getText().trim();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pQ = conn.prepareStatement("SELECT jumlah_cuti FROM cuti WHERE id_karyawan = ?")) {
            pQ.setString(1, emp.getIdKaryawan());
            try (ResultSet rs = pQ.executeQuery()) {
                if (rs.next()) {
                    int quota = rs.getInt("jumlah_cuti");
                    if ("Cuti Tahunan".equals(jenis) && duration > quota) {
                        JOptionPane.showMessageDialog(this, "Sisa kuota cuti hanya " + quota + " hari!", "Batas Kuota", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    try (PreparedStatement p = conn.prepareStatement(
                            "UPDATE pengajuan_cuti SET tanggal_awal=?,tanggal_akhir=?,masa_cuti=?,jenis_cuti=?,keterangan=? WHERE kd_daftar_cuti=? AND status_pengajuan='PENDING'")) {
                        p.setString(1, start); p.setString(2, end); p.setInt(3, duration);
                        p.setString(4, jenis); p.setString(5, desc); p.setString(6, code);
                        int updated = p.executeUpdate();
                        if (updated > 0) {
                            JOptionPane.showMessageDialog(this, "Pengajuan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                            parentFrame.refreshStats(); refreshData();
                        } else {
                            JOptionPane.showMessageDialog(this, "Pengajuan tidak dapat diubah karena sudah diproses!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        String code = txtKdDaftar.getText();
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus pengajuan " + code + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false);

            String idKaryawan = null; int duration = 0; String status = null;
            try (PreparedStatement p = conn.prepareStatement("SELECT id_karyawan, masa_cuti, status_pengajuan FROM pengajuan_cuti WHERE kd_daftar_cuti = ?")) {
                p.setString(1, code);
                try (ResultSet rs = p.executeQuery()) {
                    if (rs.next()) { idKaryawan = rs.getString("id_karyawan"); duration = rs.getInt("masa_cuti"); status = rs.getString("status_pengajuan"); }
                }
            }

            if (idKaryawan != null) {
                try (PreparedStatement p = conn.prepareStatement("DELETE FROM pengajuan_cuti WHERE kd_daftar_cuti = ?")) {
                    p.setString(1, code); p.executeUpdate();
                }
                if ("DISETUJUI".equals(status)) {
                    try (PreparedStatement p = conn.prepareStatement("UPDATE cuti SET jumlah_cuti = jumlah_cuti + ? WHERE id_karyawan = ?")) {
                        p.setInt(1, duration); p.setString(2, idKaryawan); p.executeUpdate();
                    }
                }
            }
            conn.commit();
            JOptionPane.showMessageDialog(this, "Pengajuan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            parentFrame.refreshStats(); refreshData();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Gagal menghapus: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void updateStatus(String newStatus) {
        String code = txtKdDaftar.getText();
        if (code.isEmpty()) return;

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false);

            String idKaryawan = null; int duration = 0;
            try (PreparedStatement p = conn.prepareStatement("SELECT id_karyawan, masa_cuti FROM pengajuan_cuti WHERE kd_daftar_cuti = ?")) {
                p.setString(1, code);
                try (ResultSet rs = p.executeQuery()) {
                    if (rs.next()) { idKaryawan = rs.getString("id_karyawan"); duration = rs.getInt("masa_cuti"); }
                }
            }

            if (idKaryawan == null) { conn.rollback(); return; }

            if ("DISETUJUI".equals(newStatus)) {
                try (PreparedStatement p = conn.prepareStatement("SELECT jumlah_cuti FROM cuti WHERE id_karyawan = ?")) {
                    p.setString(1, idKaryawan);
                    try (ResultSet rs = p.executeQuery()) {
                        if (rs.next()) {
                            int quota = rs.getInt("jumlah_cuti");
                            if (duration > quota) {
                                JOptionPane.showMessageDialog(this, "Saldo cuti karyawan tidak mencukupi (Sisa: " + quota + " hari).", "Peringatan", JOptionPane.WARNING_MESSAGE);
                                conn.rollback(); return;
                            }
                        }
                    }
                }
                try (PreparedStatement p = conn.prepareStatement("UPDATE cuti SET jumlah_cuti = jumlah_cuti - ? WHERE id_karyawan = ?")) {
                    p.setInt(1, duration); p.setString(2, idKaryawan); p.executeUpdate();
                }
            }

            try (PreparedStatement p = conn.prepareStatement("UPDATE pengajuan_cuti SET status_pengajuan = ? WHERE kd_daftar_cuti = ?")) {
                p.setString(1, newStatus); p.setString(2, code); p.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Status diperbarui menjadi: " + newStatus, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            parentFrame.refreshStats(); refreshData();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Gagal memperbarui status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
}

package com.bsf.leaveapp.ui;

import com.bsf.leaveapp.database.DatabaseHelper;
import com.bsf.leaveapp.model.Jabatan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KaryawanPanel extends JPanel {
    private String userRole;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtIdKaryawan;
    private JTextField txtNama;
    private JComboBox<String> cbJenisKelamin;
    private JTextField txtAlamat;
    private JTextField txtNomorHp;
    private JComboBox<String> cbStatus;
    private JComboBox<Jabatan> cbJabatan;

    private JButton btnAdd, btnEdit, btnDelete, btnClear;

    public KaryawanPanel(String userRole) {
        this.userRole = userRole;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Manajemen Data Karyawan");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        add(lblTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "ID Karyawan", "Nama", "Jenis Kelamin", "Alamat", "No HP", "Status", "Jabatan", "Departemen"
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
                BorderFactory.createTitledBorder("Form Data Karyawan"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.setPreferredSize(new Dimension(290, 0));

        JPanel fields = new JPanel(new GridLayout(14, 1, 5, 3));

        fields.add(new JLabel("ID Karyawan:"));
        txtIdKaryawan = new JTextField();
        txtIdKaryawan.setEditable(false);
        fields.add(txtIdKaryawan);

        fields.add(new JLabel("Nama Lengkap:"));
        txtNama = new JTextField();
        fields.add(txtNama);

        fields.add(new JLabel("Jenis Kelamin:"));
        cbJenisKelamin = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        fields.add(cbJenisKelamin);

        fields.add(new JLabel("Alamat:"));
        txtAlamat = new JTextField();
        fields.add(txtAlamat);

        fields.add(new JLabel("No HP:"));
        txtNomorHp = new JTextField();
        fields.add(txtNomorHp);

        fields.add(new JLabel("Status Karyawan:"));
        cbStatus = new JComboBox<>(new String[]{"Tetap", "Kontrak"});
        fields.add(cbStatus);

        fields.add(new JLabel("Jabatan:"));
        cbJabatan = new JComboBox<>();
        fields.add(cbJabatan);

        formPanel.add(fields, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnAdd    = new JButton("Tambah");
        btnEdit   = new JButton("Ubah");
        btnDelete = new JButton("Hapus");
        btnClear  = new JButton("Bersihkan");
        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnClear);
        formPanel.add(actionPanel, BorderLayout.SOUTH);

        if ("HRD".equals(userRole)) {
            txtNama.setEditable(false);
            cbJenisKelamin.setEnabled(false);
            txtAlamat.setEditable(false);
            txtNomorHp.setEditable(false);
            cbStatus.setEnabled(false);
            cbJabatan.setEnabled(false);
            btnAdd.setEnabled(false);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);

        table.getSelectionModel().addListSelectionListener(e -> handleTableSelection());
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());

        refreshData();
    }

    public void refreshData() {
        loadJabatanComboBox();
        tableModel.setRowCount(0);
        String sql = "SELECT k.*, j.nama_jabatan, j.departemen FROM karyawan k " +
                     "LEFT JOIN jabatan j ON k.kd_jabatan = j.kd_jabatan ORDER BY k.id_karyawan ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("id_karyawan"),
                        rs.getString("nama"),
                        rs.getString("jenis_kelamin"),
                        rs.getString("alamat"),
                        rs.getString("nomor_hp"),
                        rs.getString("status"),
                        rs.getString("nama_jabatan") != null ? rs.getString("nama_jabatan") : "-",
                        rs.getString("departemen")   != null ? rs.getString("departemen")   : "-"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        clearForm();
    }

    private void loadJabatanComboBox() {
        cbJabatan.removeAllItems();
        String sql = "SELECT * FROM jabatan ORDER BY nama_jabatan ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                cbJabatan.addItem(new Jabatan(
                        rs.getString("kd_jabatan"),
                        rs.getString("nama_jabatan"),
                        rs.getString("departemen")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtIdKaryawan.setText((String) tableModel.getValueAt(row, 0));
            txtNama.setText((String) tableModel.getValueAt(row, 1));
            cbJenisKelamin.setSelectedItem(tableModel.getValueAt(row, 2));
            txtAlamat.setText((String) tableModel.getValueAt(row, 3));
            txtNomorHp.setText((String) tableModel.getValueAt(row, 4));
            cbStatus.setSelectedItem(tableModel.getValueAt(row, 5));
            String jabName = (String) tableModel.getValueAt(row, 6);
            for (int i = 0; i < cbJabatan.getItemCount(); i++) {
                if (cbJabatan.getItemAt(i).getNamaJabatan().equals(jabName)) {
                    cbJabatan.setSelectedIndex(i);
                    break;
                }
            }
            if (!"HRD".equals(userRole)) {
                btnAdd.setEnabled(false);
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        }
    }

    private void clearForm() {
        txtIdKaryawan.setText(DatabaseHelper.generateNextId("karyawan", "id_karyawan", "EMP-"));
        txtNama.setText("");
        cbJenisKelamin.setSelectedIndex(0);
        txtAlamat.setText("");
        txtNomorHp.setText("");
        cbStatus.setSelectedIndex(0);
        if (cbJabatan.getItemCount() > 0) cbJabatan.setSelectedIndex(0);
        table.clearSelection();
        if (!"HRD".equals(userRole)) {
            btnAdd.setEnabled(true);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void handleAdd() {
        String id     = txtIdKaryawan.getText();
        String name   = txtNama.getText().trim();
        String gender = (String) cbJenisKelamin.getSelectedItem();
        String addr   = txtAlamat.getText().trim();
        String phone  = txtNomorHp.getText().trim();
        String status = (String) cbStatus.getSelectedItem();
        Jabatan jab   = (Jabatan) cbJabatan.getSelectedItem();

        if (name.isEmpty() || jab == null) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap dan Jabatan wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement p = conn.prepareStatement(
                    "INSERT INTO karyawan (id_karyawan, nama, jenis_kelamin, alamat, nomor_hp, status, kd_jabatan) VALUES (?,?,?,?,?,?,?)")) {
                p.setString(1, id); p.setString(2, name); p.setString(3, gender);
                p.setString(4, addr); p.setString(5, phone); p.setString(6, status);
                p.setString(7, jab.getKdJabatan());
                p.executeUpdate();
            }

            String nextCutiId = DatabaseHelper.generateNextId("cuti", "kd_cuti", "CUT-");
            try (PreparedStatement p = conn.prepareStatement(
                    "INSERT INTO cuti (kd_cuti, id_karyawan, jumlah_cuti) VALUES (?, ?, 12)")) {
                p.setString(1, nextCutiId); p.setString(2, id);
                p.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Data Karyawan dan Kuota Cuti berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void handleEdit() {
        String id     = txtIdKaryawan.getText();
        String name   = txtNama.getText().trim();
        String gender = (String) cbJenisKelamin.getSelectedItem();
        String addr   = txtAlamat.getText().trim();
        String phone  = txtNomorHp.getText().trim();
        String status = (String) cbStatus.getSelectedItem();
        Jabatan jab   = (Jabatan) cbJabatan.getSelectedItem();

        if (name.isEmpty() || jab == null) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap dan Jabatan wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE karyawan SET nama=?, jenis_kelamin=?, alamat=?, nomor_hp=?, status=?, kd_jabatan=? WHERE id_karyawan=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, name); p.setString(2, gender); p.setString(3, addr);
            p.setString(4, phone); p.setString(5, status); p.setString(6, jab.getKdJabatan());
            p.setString(7, id);
            p.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Karyawan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        String id = txtIdKaryawan.getText();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus karyawan " + id + "?\nSemua data pengajuan cuti & kuota akan terhapus secara permanen.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement p = conn.prepareStatement("DELETE FROM karyawan WHERE id_karyawan = ?")) {
                p.setString(1, id);
                p.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data Karyawan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

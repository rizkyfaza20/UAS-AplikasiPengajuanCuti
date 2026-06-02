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
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Heading title
        JLabel lblTitle = new JLabel("Manajemen Data Karyawan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        add(lblTitle, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{
                "ID Karyawan", "Nama", "Jenis Kelamin", "Alamat", "No HP", "Status", "Jabatan", "Departemen"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        // Form panel layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(43, 43, 43));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setPreferredSize(new Dimension(320, 0));

        JLabel lblFormTitle = new JLabel("Form Data Karyawan");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(Color.WHITE);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblFormTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        txtIdKaryawan = new JTextField();
        txtIdKaryawan.setEditable(false);
        txtIdKaryawan.setMaximumSize(new Dimension(290, 28));
        txtIdKaryawan.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNama = new JTextField();
        txtNama.setMaximumSize(new Dimension(290, 28));
        txtNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbJenisKelamin = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        cbJenisKelamin.setMaximumSize(new Dimension(290, 28));
        cbJenisKelamin.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtAlamat = new JTextField();
        txtAlamat.setMaximumSize(new Dimension(290, 28));
        txtAlamat.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNomorHp = new JTextField();
        txtNomorHp.setMaximumSize(new Dimension(290, 28));
        txtNomorHp.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbStatus = new JComboBox<>(new String[]{"Tetap", "Kontrak"});
        cbStatus.setMaximumSize(new Dimension(290, 28));
        cbStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbJabatan = new JComboBox<>();
        cbJabatan.setMaximumSize(new Dimension(290, 28));
        cbJabatan.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(createFormLabel("ID Karyawan"));
        formPanel.add(txtIdKaryawan);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Nama Lengkap"));
        formPanel.add(txtNama);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Jenis Kelamin"));
        formPanel.add(cbJenisKelamin);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Alamat"));
        formPanel.add(txtAlamat);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("No HP"));
        formPanel.add(txtNomorHp);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Status Karyawan"));
        formPanel.add(cbStatus);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Jabatan"));
        formPanel.add(cbJabatan);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Action Buttons Setup
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        actionPanel.setBackground(new Color(43, 43, 43));
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionPanel.setMaximumSize(new Dimension(290, 70));

        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Ubah");
        btnDelete = new JButton("Hapus");
        btnClear = new JButton("Bersihkan");

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnClear);

        formPanel.add(actionPanel);

        // View-only lock for HRD role
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

        // Core Event bindings
        table.getSelectionModel().addListSelectionListener(e -> handleTableSelection());
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());

        // Initial Data Fetch
        refreshData();
    }

    private JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // Refresh comboboxes and tables with updated positions list and employee list
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
                        rs.getString("departemen") != null ? rs.getString("departemen") : "-"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        clearForm();
    }

    // Loads available positions list into JComboBox
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

    // Handle form population on table row selection
    private void handleTableSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtIdKaryawan.setText((String) tableModel.getValueAt(selectedRow, 0));
            txtNama.setText((String) tableModel.getValueAt(selectedRow, 1));
            cbJenisKelamin.setSelectedItem(tableModel.getValueAt(selectedRow, 2));
            txtAlamat.setText((String) tableModel.getValueAt(selectedRow, 3));
            txtNomorHp.setText((String) tableModel.getValueAt(selectedRow, 4));
            cbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 5));

            String jabName = (String) tableModel.getValueAt(selectedRow, 6);
            for (int i = 0; i < cbJabatan.getItemCount(); i++) {
                Jabatan j = cbJabatan.getItemAt(i);
                if (j.getNamaJabatan().equals(jabName)) {
                    cbJabatan.setSelectedItem(j);
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

    // Adds employee and initializes their leave balance records atomically
    private void handleAdd() {
        String id = txtIdKaryawan.getText();
        String name = txtNama.getText().trim();
        String gender = (String) cbJenisKelamin.getSelectedItem();
        String address = txtAlamat.getText().trim();
        String phone = txtNomorHp.getText().trim();
        String status = (String) cbStatus.getSelectedItem();
        Jabatan jab = (Jabatan) cbJabatan.getSelectedItem();

        if (name.isEmpty() || jab == null) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap dan Jabatan wajib dipilih!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sqlEmp = "INSERT INTO karyawan (id_karyawan, nama, jenis_kelamin, alamat, nomor_hp, status, kd_jabatan) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCuti = "INSERT INTO cuti (kd_cuti, id_karyawan, jumlah_cuti) VALUES (?, ?, 12)";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false); // Enable atomic transaction execution

            // 1. Insert Employee
            try (PreparedStatement pstmt = conn.prepareStatement(sqlEmp)) {
                pstmt.setString(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, gender);
                pstmt.setString(4, address);
                pstmt.setString(5, phone);
                pstmt.setString(6, status);
                pstmt.setString(7, jab.getKdJabatan());
                pstmt.executeUpdate();
            }

            // 2. Insert Default Leave Balance
            String nextCutiId = DatabaseHelper.generateNextId("cuti", "kd_cuti", "CUT-");
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCuti)) {
                pstmt.setString(1, nextCutiId);
                pstmt.setString(2, id);
                pstmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Data Karyawan dan Kuota Cuti berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    // Updates employee info
    private void handleEdit() {
        String id = txtIdKaryawan.getText();
        String name = txtNama.getText().trim();
        String gender = (String) cbJenisKelamin.getSelectedItem();
        String address = txtAlamat.getText().trim();
        String phone = txtNomorHp.getText().trim();
        String status = (String) cbStatus.getSelectedItem();
        Jabatan jab = (Jabatan) cbJabatan.getSelectedItem();

        if (name.isEmpty() || jab == null) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap dan Jabatan wajib dipilih!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE karyawan SET nama = ?, jenis_kelamin = ?, alamat = ?, nomor_hp = ?, status = ?, kd_jabatan = ? WHERE id_karyawan = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setString(3, address);
            pstmt.setString(4, phone);
            pstmt.setString(5, status);
            pstmt.setString(6, jab.getKdJabatan());
            pstmt.setString(7, id);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Karyawan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Deletes employee (leaves/quotas cascade deleted automatically)
    private void handleDelete() {
        String id = txtIdKaryawan.getText();
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus karyawan " + id + "?\nSemua data pengajuan cuti & sisa kuota karyawan ini akan terhapus secara permanen.", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM karyawan WHERE id_karyawan = ?";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data Karyawan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

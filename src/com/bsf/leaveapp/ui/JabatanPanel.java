package com.bsf.leaveapp.ui;

import com.bsf.leaveapp.database.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JabatanPanel extends JPanel {
    private String userRole;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtKdJabatan;
    private JTextField txtNamaJabatan;
    private JTextField txtDepartemen;

    private JButton btnAdd, btnEdit, btnDelete, btnClear;

    public JabatanPanel(String userRole) {
        this.userRole = userRole;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Manajemen Data Jabatan & Departemen");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        add(lblTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Kode Jabatan", "Nama Jabatan", "Departemen"}, 0) {
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
                BorderFactory.createTitledBorder("Form Data Jabatan"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.setPreferredSize(new Dimension(270, 0));

        JPanel fields = new JPanel(new GridLayout(6, 1, 5, 5));

        fields.add(new JLabel("Kode Jabatan:"));
        txtKdJabatan = new JTextField();
        txtKdJabatan.setEditable(false);
        fields.add(txtKdJabatan);

        fields.add(new JLabel("Nama Jabatan:"));
        txtNamaJabatan = new JTextField();
        fields.add(txtNamaJabatan);

        fields.add(new JLabel("Departemen:"));
        txtDepartemen = new JTextField();
        fields.add(txtDepartemen);

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
            txtNamaJabatan.setEditable(false);
            txtDepartemen.setEditable(false);
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
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM jabatan ORDER BY kd_jabatan ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("kd_jabatan"),
                        rs.getString("nama_jabatan"),
                        rs.getString("departemen")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        clearForm();
    }

    private void handleTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtKdJabatan.setText((String) tableModel.getValueAt(row, 0));
            txtNamaJabatan.setText((String) tableModel.getValueAt(row, 1));
            txtDepartemen.setText((String) tableModel.getValueAt(row, 2));
            if (!"HRD".equals(userRole)) {
                btnAdd.setEnabled(false);
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        }
    }

    private void clearForm() {
        txtKdJabatan.setText(DatabaseHelper.generateNextId("jabatan", "kd_jabatan", "JAB-"));
        txtNamaJabatan.setText("");
        txtDepartemen.setText("");
        table.clearSelection();
        if (!"HRD".equals(userRole)) {
            btnAdd.setEnabled(true);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void handleAdd() {
        String code = txtKdJabatan.getText();
        String name = txtNamaJabatan.getText().trim();
        String dept = txtDepartemen.getText().trim();
        if (name.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Jabatan dan Departemen wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "INSERT INTO jabatan (kd_jabatan, nama_jabatan, departemen) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, name);
            pstmt.setString(3, dept);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Jabatan berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        String code = txtKdJabatan.getText();
        String name = txtNamaJabatan.getText().trim();
        String dept = txtDepartemen.getText().trim();
        if (name.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Jabatan dan Departemen wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "UPDATE jabatan SET nama_jabatan = ?, departemen = ? WHERE kd_jabatan = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, dept);
            pstmt.setString(3, code);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Jabatan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        String code = txtKdJabatan.getText();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus jabatan " + code + "? Karyawan terhubung akan dialihkan ke posisi kosong.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM jabatan WHERE kd_jabatan = ?";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, code);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data Jabatan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

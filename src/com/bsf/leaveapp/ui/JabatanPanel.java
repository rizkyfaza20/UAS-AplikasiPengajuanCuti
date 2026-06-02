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
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Heading title
        JLabel lblTitle = new JLabel("Manajemen Data Jabatan & Departemen");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        add(lblTitle, BorderLayout.NORTH);

        // Main Table layout
        tableModel = new DefaultTableModel(new Object[]{"Kode Jabatan", "Nama Jabatan", "Departemen"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        // Sidebar input form layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(43, 43, 43));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setPreferredSize(new Dimension(300, 0));

        JLabel lblFormTitle = new JLabel("Form Data Jabatan");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(Color.WHITE);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblFormTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        txtKdJabatan = new JTextField();
        txtKdJabatan.setEditable(false);
        txtKdJabatan.setMaximumSize(new Dimension(270, 30));
        txtKdJabatan.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNamaJabatan = new JTextField();
        txtNamaJabatan.setMaximumSize(new Dimension(270, 30));
        txtNamaJabatan.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtDepartemen = new JTextField();
        txtDepartemen.setMaximumSize(new Dimension(270, 30));
        txtDepartemen.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(createFormLabel("Kode Jabatan"));
        formPanel.add(txtKdJabatan);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormLabel("Nama Jabatan"));
        formPanel.add(txtNamaJabatan);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormLabel("Departemen"));
        formPanel.add(txtDepartemen);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        actionPanel.setBackground(new Color(43, 43, 43));
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionPanel.setMaximumSize(new Dimension(270, 70));

        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Ubah");
        btnDelete = new JButton("Hapus");
        btnClear = new JButton("Bersihkan");

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnClear);

        formPanel.add(actionPanel);

        // HRD Role Restrictions: view-only permissions
        if ("HRD".equals(userRole)) {
            txtNamaJabatan.setEditable(false);
            txtDepartemen.setEditable(false);
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

    // Loads positions list from database into the UI table
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

    // Handles population of form fields when a table row is highlighted
    private void handleTableSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtKdJabatan.setText((String) tableModel.getValueAt(selectedRow, 0));
            txtNamaJabatan.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtDepartemen.setText((String) tableModel.getValueAt(selectedRow, 2));

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

    // Insert new position
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
            e.printStackTrace();
        }
    }

    // Modify selected position
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
            e.printStackTrace();
        }
    }

    // Delete selected position
    private void handleDelete() {
        String code = txtKdJabatan.getText();
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus jabatan " + code + "?\nKaryawan yang terhubung akan dialihkan ke posisi kosong.", "Konfirmasi", JOptionPane.YES_NO_OPTION);
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
                e.printStackTrace();
            }
        }
    }
}

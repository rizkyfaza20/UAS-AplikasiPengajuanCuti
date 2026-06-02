package com.bsf.leaveapp.ui;

import com.bsf.leaveapp.database.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CutiPanel extends JPanel {
    private String userRole;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtKdCuti;
    private JTextField txtIdKaryawan;
    private JTextField txtNamaKaryawan;
    private JSpinner spinJumlahCuti;

    private JButton btnEdit, btnClear;

    public CutiPanel(String userRole) {
        this.userRole = userRole;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Heading title
        JLabel lblTitle = new JLabel("Manajemen Saldo / Kuota Cuti Karyawan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        add(lblTitle, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{
                "Kode Cuti", "ID Karyawan", "Nama Karyawan", "Sisa Kuota Cuti (Hari)"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        // Sidebar Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(43, 43, 43));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setPreferredSize(new Dimension(300, 0));

        JLabel lblFormTitle = new JLabel("Form Penyesuaian Kuota");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(Color.WHITE);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblFormTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        txtKdCuti = new JTextField();
        txtKdCuti.setEditable(false);
        txtKdCuti.setMaximumSize(new Dimension(270, 30));
        txtKdCuti.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtIdKaryawan = new JTextField();
        txtIdKaryawan.setEditable(false);
        txtIdKaryawan.setMaximumSize(new Dimension(270, 30));
        txtIdKaryawan.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNamaKaryawan = new JTextField();
        txtNamaKaryawan.setEditable(false);
        txtNamaKaryawan.setMaximumSize(new Dimension(270, 30));
        txtNamaKaryawan.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Spinner to handle leave day increments safely
        spinJumlahCuti = new JSpinner(new SpinnerNumberModel(12, 0, 100, 1));
        spinJumlahCuti.setMaximumSize(new Dimension(270, 30));
        spinJumlahCuti.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(createFormLabel("Kode Kuota"));
        formPanel.add(txtKdCuti);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormLabel("ID Karyawan"));
        formPanel.add(txtIdKaryawan);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormLabel("Nama Karyawan"));
        formPanel.add(txtNamaKaryawan);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormLabel("Saldo Kuota (Hari)"));
        formPanel.add(spinJumlahCuti);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Action controls
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(new Color(43, 43, 43));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(270, 35));

        btnEdit = new JButton("Simpan Perubahan");
        btnClear = new JButton("Batal");

        btnPanel.add(btnEdit);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel);

        // HRD view lock restrictions
        if ("HRD".equals(userRole)) {
            spinJumlahCuti.setEnabled(false);
            btnEdit.setEnabled(false);
        }

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);

        // Core Event bindings
        table.getSelectionModel().addListSelectionListener(e -> handleTableSelection());
        btnEdit.addActionListener(e -> handleEdit());
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

    // Pulls leave quota list from database
    public void refreshData() {
        tableModel.setRowCount(0);
        String sql = "SELECT c.*, k.nama FROM cuti c " +
                     "JOIN karyawan k ON c.id_karyawan = k.id_karyawan ORDER BY c.kd_cuti ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("kd_cuti"),
                        rs.getString("id_karyawan"),
                        rs.getString("nama"),
                        rs.getInt("jumlah_cuti")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        clearForm();
    }

    // Handles form population on row click
    private void handleTableSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtKdCuti.setText((String) tableModel.getValueAt(selectedRow, 0));
            txtIdKaryawan.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtNamaKaryawan.setText((String) tableModel.getValueAt(selectedRow, 2));
            spinJumlahCuti.setValue(tableModel.getValueAt(selectedRow, 3));

            if (!"HRD".equals(userRole)) {
                btnEdit.setEnabled(true);
            }
        }
    }

    private void clearForm() {
        txtKdCuti.setText("");
        txtIdKaryawan.setText("");
        txtNamaKaryawan.setText("");
        spinJumlahCuti.setValue(12);
        table.clearSelection();

        btnEdit.setEnabled(false);
    }

    // Modifies employee's remaining leave balance manually
    private void handleEdit() {
        String code = txtKdCuti.getText();
        int newQuota = (Integer) spinJumlahCuti.getValue();

        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE cuti SET jumlah_cuti = ? WHERE kd_cuti = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQuota);
            pstmt.setString(2, code);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kuota cuti berhasil disesuaikan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui kuota: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

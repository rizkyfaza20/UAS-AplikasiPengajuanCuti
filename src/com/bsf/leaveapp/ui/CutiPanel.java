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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Manajemen Saldo / Kuota Cuti Karyawan");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        add(lblTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "Kode Cuti", "ID Karyawan", "Nama Karyawan", "Sisa Kuota (Hari)"
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
                BorderFactory.createTitledBorder("Form Penyesuaian Kuota"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.setPreferredSize(new Dimension(270, 0));

        JPanel fields = new JPanel(new GridLayout(8, 1, 5, 3));

        fields.add(new JLabel("Kode Kuota:"));
        txtKdCuti = new JTextField();
        txtKdCuti.setEditable(false);
        fields.add(txtKdCuti);

        fields.add(new JLabel("ID Karyawan:"));
        txtIdKaryawan = new JTextField();
        txtIdKaryawan.setEditable(false);
        fields.add(txtIdKaryawan);

        fields.add(new JLabel("Nama Karyawan:"));
        txtNamaKaryawan = new JTextField();
        txtNamaKaryawan.setEditable(false);
        fields.add(txtNamaKaryawan);

        fields.add(new JLabel("Saldo Kuota (Hari):"));
        spinJumlahCuti = new JSpinner(new SpinnerNumberModel(12, 0, 100, 1));
        fields.add(spinJumlahCuti);

        formPanel.add(fields, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnEdit  = new JButton("Simpan");
        btnClear = new JButton("Batal");
        btnPanel.add(btnEdit);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel, BorderLayout.SOUTH);

        if ("HRD".equals(userRole)) {
            spinJumlahCuti.setEnabled(false);
            btnEdit.setEnabled(false);
        }

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);

        table.getSelectionModel().addListSelectionListener(e -> handleTableSelection());
        btnEdit.addActionListener(e -> handleEdit());
        btnClear.addActionListener(e -> clearForm());

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        String sql = "SELECT c.*, k.nama FROM cuti c JOIN karyawan k ON c.id_karyawan = k.id_karyawan ORDER BY c.kd_cuti ASC";
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

    private void handleTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtKdCuti.setText((String) tableModel.getValueAt(row, 0));
            txtIdKaryawan.setText((String) tableModel.getValueAt(row, 1));
            txtNamaKaryawan.setText((String) tableModel.getValueAt(row, 2));
            spinJumlahCuti.setValue(tableModel.getValueAt(row, 3));
            if (!"HRD".equals(userRole)) btnEdit.setEnabled(true);
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

    private void handleEdit() {
        String code = txtKdCuti.getText();
        int quota = (Integer) spinJumlahCuti.getValue();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih baris tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "UPDATE cuti SET jumlah_cuti = ? WHERE kd_cuti = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quota);
            pstmt.setString(2, code);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kuota cuti berhasil disesuaikan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui kuota: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package com.bsf.leaveapp.ui;

import com.bsf.leaveapp.database.DatabaseHelper;
import com.bsf.leaveapp.model.Karyawan;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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
        this.userName = userName;
        this.userRole = userRole;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Heading title
        JLabel lblTitle = new JLabel("Pengajuan & Persetujuan Cuti");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        add(lblTitle, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{
                "Kode Req", "ID Karyawan", "Nama Karyawan", "Departemen", "Tgl Awal", "Tgl Akhir", "Masa Cuti", "Jenis Cuti", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        // Custom JTable status color renderer for premium visual appearance
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = (String) value;
                if ("PENDING".equals(val)) {
                    c.setForeground(new Color(241, 196, 15)); // Soft Gold
                } else if ("DISETUJUI".equals(val)) {
                    c.setForeground(new Color(46, 204, 113)); // Soft Emerald Green
                } else if ("DITOLAK".equals(val)) {
                    c.setForeground(new Color(231, 76, 60)); // Soft Coral Red
                }
                setFont(getFont().deriveFont(Font.BOLD));
                return c;
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);

        // Sidebar input layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(43, 43, 43));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setPreferredSize(new Dimension(320, 0));

        JLabel lblFormTitle = new JLabel("Form Pengajuan Cuti");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(Color.WHITE);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblFormTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        txtKdDaftar = new JTextField();
        txtKdDaftar.setEditable(false);
        txtKdDaftar.setMaximumSize(new Dimension(290, 26));
        txtKdDaftar.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbKaryawan = new JComboBox<>();
        cbKaryawan.setMaximumSize(new Dimension(290, 26));
        cbKaryawan.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtTglAwal = new JTextField();
        txtTglAwal.setMaximumSize(new Dimension(290, 26));
        txtTglAwal.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtTglAkhir = new JTextField();
        txtTglAkhir.setMaximumSize(new Dimension(290, 26));
        txtTglAkhir.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtMasaCuti = new JTextField();
        txtMasaCuti.setEditable(false);
        txtMasaCuti.setMaximumSize(new Dimension(290, 26));
        txtMasaCuti.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbJenisCuti = new JComboBox<>(new String[]{"Cuti Sakit", "Cuti Melahirkan", "Cuti Tahunan", "Cuti Alasan Penting"});
        cbJenisCuti.setMaximumSize(new Dimension(290, 26));
        cbJenisCuti.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtKeterangan = new JTextField();
        txtKeterangan.setMaximumSize(new Dimension(290, 26));
        txtKeterangan.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(createFormLabel("Kode Pengajuan"));
        formPanel.add(txtKdDaftar);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Karyawan"));
        formPanel.add(cbKaryawan);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Tanggal Mulai (YYYY-MM-DD)"));
        formPanel.add(txtTglAwal);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Tanggal Selesai (YYYY-MM-DD)"));
        formPanel.add(txtTglAkhir);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Durasi Cuti (Hari - Otomatis)"));
        formPanel.add(txtMasaCuti);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Jenis Cuti"));
        formPanel.add(cbJenisCuti);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(createFormLabel("Keterangan"));
        formPanel.add(txtKeterangan);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Action Buttons Setup based on user login role
        if ("ADMIN".equals(userRole)) {
            JPanel actionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            actionPanel.setBackground(new Color(43, 43, 43));
            actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            actionPanel.setMaximumSize(new Dimension(290, 70));

            btnAdd = new JButton("Ajukan Cuti");
            btnEdit = new JButton("Ubah");
            btnDelete = new JButton("Hapus");
            btnClear = new JButton("Bersihkan");

            actionPanel.add(btnAdd);
            actionPanel.add(btnEdit);
            actionPanel.add(btnDelete);
            actionPanel.add(btnClear);

            formPanel.add(actionPanel);

            btnAdd.addActionListener(e -> handleAdd());
            btnEdit.addActionListener(e -> handleEdit());
            btnDelete.addActionListener(e -> handleDelete());
            btnClear.addActionListener(e -> clearForm());
        } else {
            // HRD role can approve or reject leaves
            JPanel hrdPanel = new JPanel(new GridLayout(2, 1, 5, 10));
            hrdPanel.setBackground(new Color(43, 43, 43));
            hrdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            hrdPanel.setMaximumSize(new Dimension(290, 80));

            btnApprove = new JButton("Setujui Cuti (Approve)");
            btnApprove.setBackground(new Color(46, 204, 113));
            btnApprove.setForeground(Color.WHITE);

            btnReject = new JButton("Tolak Cuti (Reject)");
            btnReject.setBackground(new Color(231, 76, 60));
            btnReject.setForeground(Color.WHITE);

            hrdPanel.add(btnApprove);
            hrdPanel.add(btnReject);

            formPanel.add(hrdPanel);

            cbKaryawan.setEnabled(false);
            txtTglAwal.setEditable(false);
            txtTglAkhir.setEditable(false);
            cbJenisCuti.setEnabled(false);
            txtKeterangan.setEditable(false);

            btnApprove.addActionListener(e -> updateStatus("DISETUJUI"));
            btnReject.addActionListener(e -> updateStatus("DITOLAK"));
        }

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);

        table.getSelectionModel().addListSelectionListener(e -> handleTableSelection());

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

    // Refresh comboboxes and tables with updated positions list and request history list
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

    // Loads employees list into combobox for selection
    private void loadKaryawanComboBox() {
        cbKaryawan.removeAllItems();
        String sql = "SELECT * FROM karyawan ORDER BY nama ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                cbKaryawan.addItem(new Karyawan(
                        rs.getString("id_karyawan"),
                        rs.getString("nama"),
                        rs.getString("jenis_kelamin"),
                        rs.getString("alamat"),
                        rs.getString("nomor_hp"),
                        rs.getString("status"),
                        rs.getString("kd_jabatan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Populate sidebar when a table row is highlighted
    private void handleTableSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String code = (String) tableModel.getValueAt(selectedRow, 0);
            txtKdDaftar.setText(code);

            String empId = (String) tableModel.getValueAt(selectedRow, 1);
            for (int i = 0; i < cbKaryawan.getItemCount(); i++) {
                Karyawan k = cbKaryawan.getItemAt(i);
                if (k.getIdKaryawan().equals(empId)) {
                    cbKaryawan.setSelectedItem(k);
                    break;
                }
            }

            txtTglAwal.setText((String) tableModel.getValueAt(selectedRow, 4));
            txtTglAkhir.setText((String) tableModel.getValueAt(selectedRow, 5));
            txtMasaCuti.setText(String.valueOf(tableModel.getValueAt(selectedRow, 6)));
            cbJenisCuti.setSelectedItem(tableModel.getValueAt(selectedRow, 7));

            // Load Keterangan from database since it is not displayed in the JTable directly
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT keterangan FROM pengajuan_cuti WHERE kd_daftar_cuti = ?")) {
                pstmt.setString(1, code);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        txtKeterangan.setText(rs.getString("keterangan"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String status = (String) tableModel.getValueAt(selectedRow, 8);

            if ("ADMIN".equals(userRole)) {
                // Admin can only edit/delete if the request is still pending
                boolean isPending = "PENDING".equals(status);
                btnAdd.setEnabled(false);
                btnEdit.setEnabled(isPending);
                btnDelete.setEnabled(true);
            } else {
                // HRD approval button rules: only enable for pending leaves
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

    // Helper to calculate date differences
    private int calculateDuration() {
        try {
            LocalDate start = LocalDate.parse(txtTglAwal.getText().trim());
            LocalDate end = LocalDate.parse(txtTglAkhir.getText().trim());
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            if (days < 1) {
                JOptionPane.showMessageDialog(this, "Tanggal selesai tidak boleh sebelum tanggal mulai!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return -1;
            }
            return (int) days;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid! Gunakan format YYYY-MM-DD.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
    }

    // Submits a new leave request (placed in PENDING status)
    private void handleAdd() {
        Karyawan emp = (Karyawan) cbKaryawan.getSelectedItem();
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih karyawan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int duration = calculateDuration();
        if (duration == -1) return;

        // Query sisa quota cuti to ensure the employee is eligible before request registration
        String qCuti = "SELECT kd_cuti, jumlah_cuti FROM cuti WHERE id_karyawan = ?";
        String code = txtKdDaftar.getText();
        String tglAwal = txtTglAwal.getText().trim();
        String tglAkhir = txtTglAkhir.getText().trim();
        String jenis = (String) cbJenisCuti.getSelectedItem();
        String desc = txtKeterangan.getText().trim();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmtCuti = conn.prepareStatement(qCuti)) {

            pstmtCuti.setString(1, emp.getIdKaryawan());
            try (ResultSet rs = pstmtCuti.executeQuery()) {
                if (rs.next()) {
                    String kdCuti = rs.getString("kd_cuti");
                    int quota = rs.getInt("jumlah_cuti");

                    if ("Cuti Tahunan".equals(jenis) && duration > quota) {
                        JOptionPane.showMessageDialog(this, "Pengajuan gagal! Sisa kuota cuti tahunan karyawan ini hanya " + quota + " hari.", "Batas Kuota Dilampaui", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Proceed insertion in PENDING state
                    String sql = "INSERT INTO pengajuan_cuti (kd_daftar_cuti, id_karyawan, kd_cuti, tanggal_awal, tanggal_akhir, masa_cuti, jenis_cuti, keterangan, status_pengajuan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, code);
                        pstmt.setString(2, emp.getIdKaryawan());
                        pstmt.setString(3, kdCuti);
                        pstmt.setString(4, tglAwal);
                        pstmt.setString(5, tglAkhir);
                        pstmt.setInt(6, duration);
                        pstmt.setString(7, jenis);
                        pstmt.setString(8, desc);
                        pstmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Pengajuan cuti berhasil diajukan! Menunggu persetujuan HRD.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        parentFrame.refreshStats();
                        refreshData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Karyawan ini tidak memiliki catatan saldo cuti!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan pengajuan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Edit pending request
    private void handleEdit() {
        String code = txtKdDaftar.getText();
        Karyawan emp = (Karyawan) cbKaryawan.getSelectedItem();
        if (emp == null) return;

        int duration = calculateDuration();
        if (duration == -1) return;

        String tglAwal = txtTglAwal.getText().trim();
        String tglAkhir = txtTglAkhir.getText().trim();
        String jenis = (String) cbJenisCuti.getSelectedItem();
        String desc = txtKeterangan.getText().trim();

        // Validate quota
        String qCuti = "SELECT jumlah_cuti FROM cuti WHERE id_karyawan = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmtCuti = conn.prepareStatement(qCuti)) {
            pstmtCuti.setString(1, emp.getIdKaryawan());
            try (ResultSet rs = pstmtCuti.executeQuery()) {
                if (rs.next()) {
                    int quota = rs.getInt("jumlah_cuti");
                    if ("Cuti Tahunan".equals(jenis) && duration > quota) {
                        JOptionPane.showMessageDialog(this, "Pengajuan gagal! Sisa kuota cuti tahunan karyawan ini hanya " + quota + " hari.", "Batas Kuota Dilampaui", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String sql = "UPDATE pengajuan_cuti SET tanggal_awal = ?, tanggal_akhir = ?, masa_cuti = ?, jenis_cuti = ?, keterangan = ? WHERE kd_daftar_cuti = ? AND status_pengajuan = 'PENDING'";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, tglAwal);
                        pstmt.setString(2, tglAkhir);
                        pstmt.setInt(3, duration);
                        pstmt.setString(4, jenis);
                        pstmt.setString(5, desc);
                        pstmt.setString(6, code);

                        int updated = pstmt.executeUpdate();
                        if (updated > 0) {
                            JOptionPane.showMessageDialog(this, "Pengajuan cuti berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                            parentFrame.refreshStats();
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(this, "Pengajuan tidak dapat diubah karena sudah diproses!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui pengajuan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Delete request. If already approved, refunds leave days back to employee
    private void handleDelete() {
        String code = txtKdDaftar.getText();
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pengajuan " + code + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                conn.setAutoCommit(false);

                // Fetch details to check if we need to refund approved leave duration
                String qDetails = "SELECT id_karyawan, masa_cuti, status_pengajuan FROM pengajuan_cuti WHERE kd_daftar_cuti = ?";
                String idKaryawan = null;
                int duration = 0;
                String status = null;

                try (PreparedStatement pstmt = conn.prepareStatement(qDetails)) {
                    pstmt.setString(1, code);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            idKaryawan = rs.getString("id_karyawan");
                            duration = rs.getInt("masa_cuti");
                            status = rs.getString("status_pengajuan");
                        }
                    }
                }

                if (idKaryawan != null) {
                    // 1. Delete request record
                    try (PreparedStatement pstmtDel = conn.prepareStatement("DELETE FROM pengajuan_cuti WHERE kd_daftar_cuti = ?")) {
                        pstmtDel.setString(1, code);
                        pstmtDel.executeUpdate();
                    }

                    // 2. Refund balance back if status was 'DISETUJUI'
                    if ("DISETUJUI".equals(status)) {
                        try (PreparedStatement pstmtRefund = conn.prepareStatement("UPDATE cuti SET jumlah_cuti = jumlah_cuti + ? WHERE id_karyawan = ?")) {
                            pstmtRefund.setInt(1, duration);
                            pstmtRefund.setString(2, idKaryawan);
                            pstmtRefund.executeUpdate();
                        }
                    }
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Pengajuan cuti berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.refreshStats();
                refreshData();
            } catch (SQLException e) {
                if (conn != null) {
                    try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
                }
                JOptionPane.showMessageDialog(this, "Gagal menghapus pengajuan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                }
            }
        }
    }

    // Handles approval or rejection of leave request by HRD role
    private void updateStatus(String newStatus) {
        String code = txtKdDaftar.getText();
        if (code.isEmpty()) return;

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false);

            // Fetch details first
            String qReq = "SELECT id_karyawan, masa_cuti, status_pengajuan FROM pengajuan_cuti WHERE kd_daftar_cuti = ?";
            String idKaryawan = null;
            int duration = 0;
            String prevStatus = null;

            try (PreparedStatement pstmt = conn.prepareStatement(qReq)) {
                pstmt.setString(1, code);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        idKaryawan = rs.getString("id_karyawan");
                        duration = rs.getInt("masa_cuti");
                        prevStatus = rs.getString("status_pengajuan");
                    }
                }
            }

            if (idKaryawan == null) {
                conn.rollback();
                return;
            }

            // Perform business rules check for Approval state
            if ("DISETUJUI".equals(newStatus)) {
                // Fetch current leave quota
                try (PreparedStatement pstmtQ = conn.prepareStatement("SELECT jumlah_cuti FROM cuti WHERE id_karyawan = ?")) {
                    pstmtQ.setString(1, idKaryawan);
                    try (ResultSet rs = pstmtQ.executeQuery()) {
                        if (rs.next()) {
                            int quota = rs.getInt("jumlah_cuti");
                            if (duration > quota) {
                                JOptionPane.showMessageDialog(this, "Persetujuan ditolak! Karyawan tidak memiliki saldo cuti yang cukup (Sisa: " + quota + " hari).", "Peringatan", JOptionPane.WARNING_MESSAGE);
                                conn.rollback();
                                return;
                            }
                        }
                    }
                }

                // Deduct balance from remaining leave days
                try (PreparedStatement pstmtDec = conn.prepareStatement("UPDATE cuti SET jumlah_cuti = jumlah_cuti - ? WHERE id_karyawan = ?")) {
                    pstmtDec.setInt(1, duration);
                    pstmtDec.setString(2, idKaryawan);
                    pstmtDec.executeUpdate();
                }
            }

            // Write status updates
            try (PreparedStatement pstmtUpdate = conn.prepareStatement("UPDATE pengajuan_cuti SET status_pengajuan = ? WHERE kd_daftar_cuti = ?")) {
                pstmtUpdate.setString(1, newStatus);
                pstmtUpdate.setString(2, code);
                pstmtUpdate.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Status pengajuan diperbarui menjadi: " + newStatus, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            parentFrame.refreshStats();
            refreshData();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            JOptionPane.showMessageDialog(this, "Gagal memperbarui status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
}

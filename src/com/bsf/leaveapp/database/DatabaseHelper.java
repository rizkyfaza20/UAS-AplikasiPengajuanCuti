package com.bsf.leaveapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String DB_HOST = "127.0.0.1";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "cuti_karyawan";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "MySQLServer2456";

    private static final String DB_URL_NO_DB = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "?allowMultiQueries=true";
    private static final String DB_URL_WITH_DB = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?allowMultiQueries=true";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in classpath!");
            e.printStackTrace();
        }
    }

    // Connects to MySQL server without selecting any database (used for initialization)
    private static Connection getBaseConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL_NO_DB, DB_USER, DB_PASS);
    }

    // Connects to the specific application database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL_WITH_DB, DB_USER, DB_PASS);
    }

    // Automatically bootstraps database and tables on startup
    public static void initializeDatabase() {
        try (Connection conn = getBaseConnection(); Statement stmt = conn.createStatement()) {
            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            
            // Reconnect to compile tables
            try (Connection dbConn = getConnection(); Statement dbStmt = dbConn.createStatement()) {
                
                // Create admin table
                dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS admin (" +
                        "kd_admin VARCHAR(20) PRIMARY KEY, " +
                        "nama_admin VARCHAR(100) NOT NULL, " +
                        "username VARCHAR(50) NOT NULL UNIQUE, " +
                        "password VARCHAR(50) NOT NULL, " +
                        "hak_akses ENUM('ADMIN', 'HRD') NOT NULL" +
                        ")");

                // Create jabatan table
                dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS jabatan (" +
                        "kd_jabatan VARCHAR(20) PRIMARY KEY, " +
                        "nama_jabatan VARCHAR(100) NOT NULL, " +
                        "departemen VARCHAR(100) NOT NULL" +
                        ")");

                // Create karyawan table
                dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS karyawan (" +
                        "id_karyawan VARCHAR(20) PRIMARY KEY, " +
                        "nama VARCHAR(150) NOT NULL, " +
                        "jenis_kelamin ENUM('Laki-laki', 'Perempuan') NOT NULL, " +
                        "alamat TEXT, " +
                        "nomor_hp VARCHAR(20), " +
                        "status VARCHAR(50) NOT NULL, " +
                        "kd_jabatan VARCHAR(20), " +
                        "username VARCHAR(50) UNIQUE, " +
                        "password VARCHAR(50), " +
                        "FOREIGN KEY (kd_jabatan) REFERENCES jabatan(kd_jabatan) ON DELETE SET NULL" +
                        ")");

                // Alter table for existing database
                try {
                    dbStmt.executeUpdate("ALTER TABLE karyawan ADD COLUMN username VARCHAR(50) UNIQUE, ADD COLUMN password VARCHAR(50)");
                } catch (SQLException e) {
                    // Ignore if columns already exist
                }

                // Create cuti table
                dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS cuti (" +
                        "kd_cuti VARCHAR(20) PRIMARY KEY, " +
                        "id_karyawan VARCHAR(20) UNIQUE NOT NULL, " +
                        "jumlah_cuti INT NOT NULL DEFAULT 12, " +
                        "FOREIGN KEY (id_karyawan) REFERENCES karyawan(id_karyawan) ON DELETE CASCADE" +
                        ")");

                // Create pengajuan_cuti table
                dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS pengajuan_cuti (" +
                        "kd_daftar_cuti VARCHAR(20) PRIMARY KEY, " +
                        "id_karyawan VARCHAR(20) NOT NULL, " +
                        "kd_cuti VARCHAR(20) NOT NULL, " +
                        "tanggal_awal DATE NOT NULL, " +
                        "tanggal_akhir DATE NOT NULL, " +
                        "masa_cuti INT NOT NULL, " +
                        "jenis_cuti ENUM('Cuti Sakit', 'Cuti Melahirkan', 'Cuti Tahunan', 'Cuti Alasan Penting') NOT NULL, " +
                        "keterangan TEXT, " +
                        "status_pengajuan ENUM('PENDING', 'DISETUJUI', 'DITOLAK') DEFAULT 'PENDING', " +
                        "FOREIGN KEY (id_karyawan) REFERENCES karyawan(id_karyawan) ON DELETE CASCADE, " +
                        "FOREIGN KEY (kd_cuti) REFERENCES cuti(kd_cuti) ON DELETE CASCADE" +
                        ")");

                // Seed initial Admin and HRD accounts
                dbStmt.executeUpdate("INSERT IGNORE INTO admin (kd_admin, nama_admin, username, password, hak_akses) VALUES " +
                        "('ADM-001', 'Administrator Utama', 'admin', 'admin123', 'ADMIN'), " +
                        "('ADM-002', 'Admin Kedua', 'admin2', 'admin123', 'ADMIN'), " +
                        "('HRD-001', 'HRD Manager Utama', 'hrd', 'hrd123', 'HRD'), " +
                        "('HRD-002', 'HRD Assistant', 'hrd2', 'hrd123', 'HRD')");

                // Seed initial Jabatan
                dbStmt.executeUpdate("INSERT IGNORE INTO jabatan (kd_jabatan, nama_jabatan, departemen) VALUES " +
                        "('JAB-001', 'Software Engineer', 'IT'), " +
                        "('JAB-002', 'System Analyst', 'IT'), " +
                        "('JAB-003', 'Marketing Specialist', 'Marketing'), " +
                        "('JAB-004', 'Sales Executive', 'Sales'), " +
                        "('JAB-005', 'HR Staff', 'Human Resources')");

                // Seed initial Karyawan
                dbStmt.executeUpdate("INSERT IGNORE INTO karyawan (id_karyawan, nama, jenis_kelamin, alamat, nomor_hp, status, kd_jabatan, username, password) VALUES " +
                        "('EMP-001', 'Budi Santoso', 'Laki-laki', 'Jl. Merdeka No. 1, Jakarta', '081234567890', 'Tetap', 'JAB-001', 'budi', 'pass123'), " +
                        "('EMP-002', 'Siti Aminah', 'Perempuan', 'Jl. Sudirman No. 2, Bandung', '081234567891', 'Tetap', 'JAB-002', 'siti', 'pass123'), " +
                        "('EMP-003', 'Andi Darmawan', 'Laki-laki', 'Jl. Thamrin No. 3, Surabaya', '081234567892', 'Kontrak', 'JAB-001', 'andi', 'pass123'), " +
                        "('EMP-004', 'Rina Melati', 'Perempuan', 'Jl. Gatot Subroto No. 4, Medan', '081234567893', 'Tetap', 'JAB-003', 'rina', 'pass123'), " +
                        "('EMP-005', 'Joko Widodo', 'Laki-laki', 'Jl. Diponegoro No. 5, Semarang', '081234567894', 'Tetap', 'JAB-004', 'joko', 'pass123'), " +
                        "('EMP-006', 'Maya Sari', 'Perempuan', 'Jl. Hasanuddin No. 6, Makassar', '081234567895', 'Kontrak', 'JAB-005', 'maya', 'pass123'), " +
                        "('EMP-007', 'Doni Pratama', 'Laki-laki', 'Jl. Ahmad Yani No. 7, Yogyakarta', '081234567896', 'Tetap', 'JAB-001', 'doni', 'pass123'), " +
                        "('EMP-008', 'Lina Kusuma', 'Perempuan', 'Jl. Pahlawan No. 8, Palembang', '081234567897', 'Tetap', 'JAB-002', 'lina', 'pass123'), " +
                        "('EMP-009', 'Hendra Wijaya', 'Laki-laki', 'Jl. Gajah Mada No. 9, Denpasar', '081234567898', 'Kontrak', 'JAB-003', 'hendra', 'pass123'), " +
                        "('EMP-010', 'Fitriani', 'Perempuan', 'Jl. Imam Bonjol No. 10, Padang', '081234567899', 'Tetap', 'JAB-004', 'fitri', 'pass123')");

                // Seed initial Saldo Cuti
                dbStmt.executeUpdate("INSERT IGNORE INTO cuti (kd_cuti, id_karyawan, jumlah_cuti) VALUES " +
                        "('CUT-001', 'EMP-001', 12), ('CUT-002', 'EMP-002', 12), " +
                        "('CUT-003', 'EMP-003', 12), ('CUT-004', 'EMP-004', 12), " +
                        "('CUT-005', 'EMP-005', 12), ('CUT-006', 'EMP-006', 12), " +
                        "('CUT-007', 'EMP-007', 12), ('CUT-008', 'EMP-008', 12), " +
                        "('CUT-009', 'EMP-009', 12), ('CUT-010', 'EMP-010', 12)");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Auto-generates the next sequential ID for tables (e.g., EMP-001, EMP-002)
    public static String generateNextId(String tableName, String idColumnName, String prefix) {
        String query = "SELECT " + idColumnName + " FROM " + tableName + " WHERE " + idColumnName + " LIKE ?";
        int maxNum = 0;
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, prefix + "%");
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString(1);
                    try {
                        String numPart = id.substring(prefix.length());
                        int num = Integer.parseInt(numPart.trim());
                        if (num > maxNum) {
                            maxNum = num;
                        }
                    } catch (Exception e) {
                        // Ignore malformed IDs
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefix + String.format("%03d", maxNum + 1);
    }
}

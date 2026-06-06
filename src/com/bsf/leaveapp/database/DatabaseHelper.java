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
                        "FOREIGN KEY (kd_jabatan) REFERENCES jabatan(kd_jabatan) ON DELETE SET NULL" +
                        ")");

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

                // Seed initial accounts if not present
                dbStmt.executeUpdate("INSERT INTO admin (kd_admin, nama_admin, username, password, hak_akses) " +
                        "VALUES ('ADM-001', 'Administrator', 'admin', 'admin123', 'ADMIN'), " +
                        "('HRD-001', 'HRD Manager', 'hrd', 'hrd123', 'HRD') " +
                        "ON DUPLICATE KEY UPDATE nama_admin = nama_admin");
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

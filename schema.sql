-- SQL Schema for PT BSF Employee Leave Application (Aplikasi Pengajuan Cuti PT BSF)

CREATE DATABASE IF NOT EXISTS cuti_karyawan;
USE cuti_karyawan;

-- 1. Table: admin (System users for login)
CREATE TABLE IF NOT EXISTS admin (
    kd_admin VARCHAR(20) PRIMARY KEY,
    nama_admin VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    hak_akses ENUM('ADMIN', 'HRD') NOT NULL
);

-- 2. Table: jabatan (Departments and Positions)
CREATE TABLE IF NOT EXISTS jabatan (
    kd_jabatan VARCHAR(20) PRIMARY KEY,
    nama_jabatan VARCHAR(100) NOT NULL,
    departemen VARCHAR(100) NOT NULL
);

-- 3. Table: karyawan (Employee Details)
CREATE TABLE IF NOT EXISTS karyawan (
    id_karyawan VARCHAR(20) PRIMARY KEY,
    nama VARCHAR(150) NOT NULL,
    jenis_kelamin ENUM('Laki-laki', 'Perempuan') NOT NULL,
    alamat TEXT,
    nomor_hp VARCHAR(20),
    status VARCHAR(50) NOT NULL, -- e.g., 'Tetap', 'Kontrak'
    kd_jabatan VARCHAR(20),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50),
    FOREIGN KEY (kd_jabatan) REFERENCES jabatan(kd_jabatan) ON DELETE SET NULL
);

-- 4. Table: cuti (Employee Leave Quota/Balance)
CREATE TABLE IF NOT EXISTS cuti (
    kd_cuti VARCHAR(20) PRIMARY KEY,
    id_karyawan VARCHAR(20) UNIQUE NOT NULL,
    jumlah_cuti INT NOT NULL DEFAULT 12, -- Default annual leave balance is 12 days
    FOREIGN KEY (id_karyawan) REFERENCES karyawan(id_karyawan) ON DELETE CASCADE
);

-- 5. Table: pengajuan_cuti (Leave Application Requests)
CREATE TABLE IF NOT EXISTS pengajuan_cuti (
    kd_daftar_cuti VARCHAR(20) PRIMARY KEY,
    id_karyawan VARCHAR(20) NOT NULL,
    kd_cuti VARCHAR(20) NOT NULL,
    tanggal_awal DATE NOT NULL,
    tanggal_akhir DATE NOT NULL,
    masa_cuti INT NOT NULL, -- Calculated leave duration in days
    jenis_cuti ENUM('Cuti Sakit', 'Cuti Melahirkan', 'Cuti Tahunan', 'Cuti Alasan Penting') NOT NULL,
    keterangan TEXT,
    status_pengajuan ENUM('PENDING', 'DISETUJUI', 'DITOLAK') DEFAULT 'PENDING',
    FOREIGN KEY (id_karyawan) REFERENCES karyawan(id_karyawan) ON DELETE CASCADE,
    FOREIGN KEY (kd_cuti) REFERENCES cuti(kd_cuti) ON DELETE CASCADE
);

-- Seed initial Admin and HRD accounts if they don't exist
INSERT INTO admin (kd_admin, nama_admin, username, password, hak_akses)
VALUES 
('ADM-001', 'Administrator', 'admin', 'admin123', 'ADMIN'),
('HRD-001', 'HRD Manager', 'hrd', 'hrd123', 'HRD')
ON DUPLICATE KEY UPDATE nama_admin=nama_admin;

-- Seed Data for PT BSF Employee Leave Application
-- Run this script after schema.sql to populate initial sample data.

USE cuti_karyawan;

-- 1. Seed Admin and HRD
INSERT IGNORE INTO admin (kd_admin, nama_admin, username, password, hak_akses) VALUES
('ADM-001', 'Administrator Utama', 'admin', 'admin123', 'ADMIN'),
('ADM-002', 'Admin Kedua', 'admin2', 'admin123', 'ADMIN'),
('HRD-001', 'HRD Manager Utama', 'hrd', 'hrd123', 'HRD'),
('HRD-002', 'HRD Assistant', 'hrd2', 'hrd123', 'HRD');

-- 2. Seed Jabatan (Departments)
INSERT IGNORE INTO jabatan (kd_jabatan, nama_jabatan, departemen) VALUES
('JAB-001', 'Software Engineer', 'IT'),
('JAB-002', 'System Analyst', 'IT'),
('JAB-003', 'Marketing Specialist', 'Marketing'),
('JAB-004', 'Sales Executive', 'Sales'),
('JAB-005', 'HR Staff', 'Human Resources');

-- 3. Seed Karyawan (Employees)
-- We provide 10 sample employees with their login credentials
INSERT IGNORE INTO karyawan (id_karyawan, nama, jenis_kelamin, alamat, nomor_hp, status, kd_jabatan, username, password) VALUES
('EMP-001', 'Budi Santoso', 'Laki-laki', 'Jl. Merdeka No. 1, Jakarta', '081234567890', 'Tetap', 'JAB-001', 'budi', 'pass123'),
('EMP-002', 'Siti Aminah', 'Perempuan', 'Jl. Sudirman No. 2, Bandung', '081234567891', 'Tetap', 'JAB-002', 'siti', 'pass123'),
('EMP-003', 'Andi Darmawan', 'Laki-laki', 'Jl. Thamrin No. 3, Surabaya', '081234567892', 'Kontrak', 'JAB-001', 'andi', 'pass123'),
('EMP-004', 'Rina Melati', 'Perempuan', 'Jl. Gatot Subroto No. 4, Medan', '081234567893', 'Tetap', 'JAB-003', 'rina', 'pass123'),
('EMP-005', 'Joko Widodo', 'Laki-laki', 'Jl. Diponegoro No. 5, Semarang', '081234567894', 'Tetap', 'JAB-004', 'joko', 'pass123'),
('EMP-006', 'Maya Sari', 'Perempuan', 'Jl. Hasanuddin No. 6, Makassar', '081234567895', 'Kontrak', 'JAB-005', 'maya', 'pass123'),
('EMP-007', 'Doni Pratama', 'Laki-laki', 'Jl. Ahmad Yani No. 7, Yogyakarta', '081234567896', 'Tetap', 'JAB-001', 'doni', 'pass123'),
('EMP-008', 'Lina Kusuma', 'Perempuan', 'Jl. Pahlawan No. 8, Palembang', '081234567897', 'Tetap', 'JAB-002', 'lina', 'pass123'),
('EMP-009', 'Hendra Wijaya', 'Laki-laki', 'Jl. Gajah Mada No. 9, Denpasar', '081234567898', 'Kontrak', 'JAB-003', 'hendra', 'pass123'),
('EMP-010', 'Fitriani', 'Perempuan', 'Jl. Imam Bonjol No. 10, Padang', '081234567899', 'Tetap', 'JAB-004', 'fitri', 'pass123');

-- 4. Seed Saldo Cuti (Leave Balances) for Karyawan
INSERT IGNORE INTO cuti (kd_cuti, id_karyawan, jumlah_cuti) VALUES
('CUT-001', 'EMP-001', 12),
('CUT-002', 'EMP-002', 12),
('CUT-003', 'EMP-003', 12),
('CUT-004', 'EMP-004', 12),
('CUT-005', 'EMP-005', 12),
('CUT-006', 'EMP-006', 12),
('CUT-007', 'EMP-007', 12),
('CUT-008', 'EMP-008', 12),
('CUT-009', 'EMP-009', 12),
('CUT-010', 'EMP-010', 12);

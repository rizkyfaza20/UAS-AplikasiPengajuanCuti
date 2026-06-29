# Aplikasi Pengajuan Cuti PT BSF
> **Tugas Akhir / Ujian Akhir Semester (UAS) - Pemrograman Berorientasi Objek (OOP)**

Aplikasi desktop manajemen pengajuan cuti karyawan pada PT BSF berbasis **Java Swing (GUI)** dan **MySQL Database**. Aplikasi ini dirancang dengan menerapkan pilar-pilar Object-Oriented Programming (OOP) dan menggunakan library **FlatLaf** untuk tampilan antarmuka (UI) gelap yang modern.

---

## Fitur Utama

1. **Penerapan Konsep OOP:** Menggunakan Enkapsulasi (Model POJO), Pewarisan (Inheritance dari JFrame/JPanel), Polimorfisme (Custom render & Override toString), dan Abstraksi (DatabaseHelper).
2. **Koneksi Database Dinamis (JDBC MySQL):** Pengecekan otomatis saat aplikasi berjalan. Jika database `cuti_karyawan` dan tabel belum ada, aplikasi akan membuat skema database secara otomatis.
3. **Pembagian Hak Akses (Role-based Authorization):**
   *   **ADMIN:** Memiliki akses penuh terhadap data master (CRUD Data Karyawan, Data Jabatan, Data Cuti/Kuota, dan mengajukan cuti baru).
   *   **HRD:** Memiliki akses untuk memproses (Setuju / Tolak) pengajuan cuti yang masuk serta melihat dan mencetak seluruh laporan.
4. **ID Auto-Sequence Generator:** Kode Jabatan (`JAB-xxx`), ID Karyawan (`EMP-xxx`), Kode Cuti (`CUT-xxx`), dan Kode Pengajuan (`REQ-xxx`) dibuat secara otomatis berdasarkan urutan terakhir di database.
5. **Perhitungan Hari Otomatis:** Durasi hari cuti (`masa_cuti`) dihitung secara otomatis berdasarkan tanggal mulai dan tanggal selesai pengajuan.
6. **Sinkronisasi Saldo Cuti:** Pengajuan cuti tahunan divalidasi terhadap sisa saldo cuti. Saat disetujui (`DISETUJUI`), saldo cuti karyawan akan berkurang otomatis. Jika pengajuan ditolak/dihapus, saldo cuti akan dikembalikan.
7. **Laporan & Cetak Dokumen:** Menampilkan pratinjau laporan berbasis tabel ASCII. Dilengkapi dengan tombol **Cetak Laporan** (menghubungkan ke printer fisik/PDF OS) dan **Ekspor ke File** (`.txt`).

---

## Prasyarat System

*   **Java Development Kit (JDK):** Versi 17 atau yang lebih baru (Aplikasi dikembangkan menggunakan OpenJDK 25).
*   **Database Server:** MySQL Server (bisa menggunakan XAMPP, Wampserver, Laragon, dsb.) berjalan di port `3306` (username: `root`, password kosong/empty).

---

## Struktur Direktori

```text
AplikasiPengajuanCuti/
├── README.md             <- Dokumen panduan ini
├── schema.sql            <- File skema database MySQL
├── run.sh                <- Script bash untuk kompilasi dan menjalankan program
├── lib/                  <- Folder library eksternal (MySQL Connector & FlatLaf)
│   ├── mysql-connector-j-8.3.0.jar
│   └── flatlaf-3.4.1.jar
└── src/                  <- Source code program Java
    └── com/bsf/leaveapp/
        ├── Main.java     <- Class Utama (Bootstrapper)
        ├── database/
        │   └── DatabaseHelper.java
        ├── model/        <- Data Class / POJO (OOP Encapsulation)
        │   ├── Admin.java
        │   ├── Cuti.java
        │   ├── Jabatan.java
        │   ├── Karyawan.java
        │   └── PengajuanCuti.java
        └── ui/           <- Komponen GUI Swing
            ├── DashboardFrame.java
            ├── LoginFrame.java
            ├── CutiPanel.java
            ├── JabatanPanel.java
            ├── KaryawanPanel.java
            ├── LaporanPanel.java
            └── PengajuanCutiPanel.java
```

### A. Menggunakan Terminal (Command Line)

Aplikasi ini sudah dilengkapi dengan script otomasi `run.sh` untuk pengguna sistem operasi berbasis Linux/MacOS.

1. Pastikan MySQL Server Anda aktif.
2. Buka terminal pada direktori proyek.
3. Jalankan perintah berikut:
   ```bash
   ./run.sh
   ```
   *Script ini secara otomatis akan mengunduh library jar jika belum ada, membersihkan file class lama, melakukan kompilasi ulang seluruh source code, dan menjalankan aplikasi.*

### B. Menggunakan NetBeans IDE

Proyek ini telah dikonfigurasi dengan file proyek NetBeans (`nbproject/` dan `build.xml`).

1. Buka NetBeans IDE Anda.
2. Pilih menu **File** -> **Open Project**.
3. Navigasikan ke folder `AplikasiPengajuanCuti` (NetBeans akan mendeteksi folder ini dengan ikon cangkir kopi khas proyek Java SE).
4. Klik **Open Project**.
5. Semua file sumber (`src/`) dan dependensi library di folder `lib/` (FlatLaf dan MySQL Connector) akan dimuat secara otomatis. Anda bisa langsung menekan tombol **Run Project (F6)**.

### C. Migrasi Database Manual

Meskipun aplikasi ini memiliki fitur *auto-migrate* dan *auto-seed* yang akan membuat tabel dan memasukkan data sampel secara otomatis saat pertama kali dijalankan, Anda juga dapat menjalankan migrasi secara manual jika diperlukan (misalnya untuk me-reset database):

1. Buka terminal atau command prompt.
2. Masuk ke MySQL console menggunakan perintah:
   ```bash
   mysql -u root -p
   ```
3. Jalankan file `schema.sql` untuk membuat struktur database:
   ```sql
   source /path/to/AplikasiPengajuanCuti/schema.sql;
   ```
4. Jalankan file `seed.sql` untuk memasukkan data sampel:
   ```sql
   source /path/to/AplikasiPengajuanCuti/seed.sql;
   ```
   *(Pastikan untuk mengganti `/path/to/AplikasiPengajuanCuti/` dengan path absolut menuju folder proyek ini di komputer Anda).*

---

## Data Akun Pengguna Bawaan (Default Accounts)

Database akan secara otomatis terisi dengan data sampel berikut saat pertama kali dijalankan:

*   **Akun ADMIN:**
    *   `admin` / `admin123` (Administrator Utama)
    *   `admin2` / `admin123` (Admin Kedua)
*   **Akun HRD:**
    *   `hrd` / `hrd123` (HRD Manager Utama)
    *   `hrd2` / `hrd123` (HRD Assistant)
*   **Akun KARYAWAN:**
    *   Sebanyak 10 data Karyawan akan dibuat secara otomatis, lengkap dengan jabatan, data diri, dan kuota cuti.
    *   Anda dapat login menggunakan role KARYAWAN dengan username berupa nama depan (misal: `budi`, `siti`, `joko`) dan password default `pass123`.

*Catatan: Anda juga bisa memasukkan data secara manual menggunakan file `seed.sql` yang telah disertakan.*

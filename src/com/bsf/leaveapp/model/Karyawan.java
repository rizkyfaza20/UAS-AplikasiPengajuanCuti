package com.bsf.leaveapp.model;

public class Karyawan {
    private String idKaryawan;
    private String nama;
    private String jenisKelamin; // 'Laki-laki' or 'Perempuan'
    private String alamat;
    private String nomorHp;
    private String status; // e.g. 'Tetap', 'Kontrak'
    private String kdJabatan;

    public Karyawan(String idKaryawan, String nama, String jenisKelamin, String alamat, String nomorHp, String status, String kdJabatan) {
        this.idKaryawan = idKaryawan;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.nomorHp = nomorHp;
        this.status = status;
        this.kdJabatan = kdJabatan;
    }

    public String getIdKaryawan() { return idKaryawan; }
    public void setIdKaryawan(String idKaryawan) { this.idKaryawan = idKaryawan; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getNomorHp() { return nomorHp; }
    public void setNomorHp(String nomorHp) { this.nomorHp = nomorHp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getKdJabatan() { return kdJabatan; }
    public void setKdJabatan(String kdJabatan) { this.kdJabatan = kdJabatan; }

    @Override
    public String toString() {
        return nama + " (" + idKaryawan + ")";
    }
}

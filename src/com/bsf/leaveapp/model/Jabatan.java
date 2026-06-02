package com.bsf.leaveapp.model;

public class Jabatan {
    private String kdJabatan;
    private String namaJabatan;
    private String departemen;

    public Jabatan(String kdJabatan, String namaJabatan, String departemen) {
        this.kdJabatan = kdJabatan;
        this.namaJabatan = namaJabatan;
        this.departemen = departemen;
    }

    public String getKdJabatan() { return kdJabatan; }
    public void setKdJabatan(String kdJabatan) { this.kdJabatan = kdJabatan; }

    public String getNamaJabatan() { return namaJabatan; }
    public void setNamaJabatan(String namaJabatan) { this.namaJabatan = namaJabatan; }

    public String getDepartemen() { return departemen; }
    public void setDepartemen(String departemen) { this.departemen = departemen; }

    @Override
    public String toString() {
        return namaJabatan + " (" + departemen + ")";
    }
}

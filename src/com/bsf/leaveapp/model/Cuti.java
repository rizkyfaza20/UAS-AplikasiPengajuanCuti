package com.bsf.leaveapp.model;

public class Cuti {
    private String kdCuti;
    private String idKaryawan;
    private int jumlahCuti;

    public Cuti(String kdCuti, String idKaryawan, int jumlahCuti) {
        this.kdCuti = kdCuti;
        this.idKaryawan = idKaryawan;
        this.jumlahCuti = jumlahCuti;
    }

    public String getKdCuti() { return kdCuti; }
    public void setKdCuti(String kdCuti) { this.kdCuti = kdCuti; }

    public String getIdKaryawan() { return idKaryawan; }
    public void setIdKaryawan(String idKaryawan) { this.idKaryawan = idKaryawan; }

    public int getJumlahCuti() { return jumlahCuti; }
    public void setJumlahCuti(int jumlahCuti) { this.jumlahCuti = jumlahCuti; }
}

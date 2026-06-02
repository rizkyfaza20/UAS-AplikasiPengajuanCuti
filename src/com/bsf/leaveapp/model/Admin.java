package com.bsf.leaveapp.model;

public class Admin {
    private String kdAdmin;
    private String namaAdmin;
    private String username;
    private String password;
    private String hakAkses; // 'ADMIN' or 'HRD'

    public Admin(String kdAdmin, String namaAdmin, String username, String password, String hakAkses) {
        this.kdAdmin = kdAdmin;
        this.namaAdmin = namaAdmin;
        this.username = username;
        this.password = password;
        this.hakAkses = hakAkses;
    }

    public String getKdAdmin() { return kdAdmin; }
    public void setKdAdmin(String kdAdmin) { this.kdAdmin = kdAdmin; }

    public String getNamaAdmin() { return namaAdmin; }
    public void setNamaAdmin(String namaAdmin) { this.namaAdmin = namaAdmin; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getHakAkses() { return hakAkses; }
    public void setHakAkses(String hakAkses) { this.hakAkses = hakAkses; }
}

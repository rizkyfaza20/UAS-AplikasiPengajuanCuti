package com.bsf.leaveapp.model;

import java.sql.Date;

public class PengajuanCuti {
    private String kdDaftarCuti;
    private String idKaryawan;
    private String kdCuti;
    private Date tanggalAwal;
    private Date tanggalAkhir;
    private int masaCuti;
    private String jenisCuti; // e.g. 'Cuti Sakit', 'Cuti Melahirkan', 'Cuti Tahunan', 'Cuti Alasan Penting'
    private String keterangan;
    private String statusPengajuan; // 'PENDING', 'DISETUJUI', 'DITOLAK'

    public PengajuanCuti(String kdDaftarCuti, String idKaryawan, String kdCuti, Date tanggalAwal, Date tanggalAkhir, int masaCuti, String jenisCuti, String keterangan, String statusPengajuan) {
        this.kdDaftarCuti = kdDaftarCuti;
        this.idKaryawan = idKaryawan;
        this.kdCuti = kdCuti;
        this.tanggalAwal = tanggalAwal;
        this.tanggalAkhir = tanggalAkhir;
        this.masaCuti = masaCuti;
        this.jenisCuti = jenisCuti;
        this.keterangan = keterangan;
        this.statusPengajuan = statusPengajuan;
    }

    public String getKdDaftarCuti() { return kdDaftarCuti; }
    public void setKdDaftarCuti(String kdDaftarCuti) { this.kdDaftarCuti = kdDaftarCuti; }

    public String getIdKaryawan() { return idKaryawan; }
    public void setIdKaryawan(String idKaryawan) { this.idKaryawan = idKaryawan; }

    public String getKdCuti() { return kdCuti; }
    public void setKdCuti(String kdCuti) { this.kdCuti = kdCuti; }

    public Date getTanggalAwal() { return tanggalAwal; }
    public void setTanggalAwal(Date tanggalAwal) { this.tanggalAwal = tanggalAwal; }

    public Date getTanggalAkhir() { return tanggalAwal; }
    public void setTanggalAkhir(Date tanggalAkhir) { this.tanggalAkhir = tanggalAkhir; }

    public int getMasaCuti() { return masaCuti; }
    public void setMasaCuti(int masaCuti) { this.masaCuti = masaCuti; }

    public String getJenisCuti() { return jenisCuti; }
    public void setJenisCuti(String jenisCuti) { this.jenisCuti = jenisCuti; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public String getStatusPengajuan() { return statusPengajuan; }
    public void setStatusPengajuan(String statusPengajuan) { this.statusPengajuan = statusPengajuan; }
}

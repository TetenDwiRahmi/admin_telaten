package com.mobile.admintelaten.Model;

public class Pembayaran {
    private int id_bank;
    private String nama_bank;
    private String no_rek;
    private String atas_nama;

    public Pembayaran(int id_bank, String nama_bank, String no_rek, String atas_nama) {
        this.id_bank = id_bank;
        this.nama_bank = nama_bank;
        this.no_rek = no_rek;
        this.atas_nama = atas_nama;
    }

    public int getId_bank() {
        return id_bank;
    }

    public String getNama_bank() {
        return nama_bank;
    }

    public String getNo_rek() {
        return no_rek;
    }

    public String getAtas_nama() {
        return atas_nama;
    }
}

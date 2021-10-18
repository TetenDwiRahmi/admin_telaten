package com.mobile.admintelaten.Model;

public class BarangDipesan {
    private String nama_item;
    private String variasi;
    private int harga_item;
    private int banyak_item;
    private  String foto_item;
    private int tot_peritem;

    public BarangDipesan (String nama_item, String variasi, int harga_item, int banyak_item, String foto_item, int tot_peritem) {
        this.nama_item = nama_item;
        this.variasi = variasi;
        this.harga_item = harga_item;
        this.banyak_item = banyak_item;
        this.foto_item = foto_item;
        this.tot_peritem = tot_peritem;
    }

    public String getNama_item() {
        return nama_item;
    }

    public String getVariasi() {
        return variasi;
    }

    public int getHarga_item() {
        return harga_item;
    }

    public int getBanyak_item() {
        return banyak_item;
    }

    public String getFoto_item() {
        return foto_item;
    }

    public int getTot_peritem() {
        return tot_peritem;
    }
}

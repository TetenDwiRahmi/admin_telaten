package com.mobile.admintelaten.Model;

public class Kategori {
    private int id_kategori;
    private String nama_kategori;

    public Kategori(int id_kategori, String nama_kategori) {
        this.id_kategori = id_kategori;
        this.nama_kategori = nama_kategori;
    }

    public int getId_kategori() {
        return id_kategori;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }

}

package com.mobile.admintelaten.Model;

public class Variasi {
    private  int id_variasi;
    private int id_item;
    private String variasi;
    private double stock;
    private int min_pembelian;
    private double harga;

    public Variasi(int id_variasi, int id_item, String variasi, double stock, int min_pembelian, double harga) {
        this.id_variasi = id_variasi;
        this.id_item = id_item;
        this.variasi = variasi;
        this.stock = stock;
        this.min_pembelian = min_pembelian;
        this.harga = harga;
    }

    public int getId_variasi() {
        return id_variasi;
    }

    public int getId_item() {
        return id_item;
    }

    public String getVariasi() {
        return variasi;
    }

    public double getStock() {
        return stock;
    }

    public int getMin_pembelian() {
        return min_pembelian;
    }

    public double getHarga() {
        return harga;
    }
}

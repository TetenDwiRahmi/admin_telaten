package com.mobile.admintelaten.Model;

public class Pesanan {
    private String id_order;
    private int id_customer;
    private String tgl_order;
    private String status_order;
    private String nama_customer;
    private int total_bayar;
    private int total_item;
    private String metode_pengiriman;

    public Pesanan(String id_order, int id_customer, String tgl_order, String status_order, String nama_customer, int total_bayar, int total_item, String metode_pengiriman) {
        this.id_order = id_order;
        this.id_customer = id_customer;
        this.tgl_order = tgl_order;
        this.status_order = status_order;
        this.nama_customer = nama_customer;
        this.total_bayar = total_bayar;
        this.total_item = total_item;
        this.metode_pengiriman = metode_pengiriman;
    }

    public String getId_order() {
        return id_order;
    }

    public int getId_customer() {
        return id_customer;
    }

    public String getTgl_order() {
        return tgl_order;
    }

    public String getStatus_order() {
        return status_order;
    }

    public String getNama_customer() {
        return nama_customer;
    }

    public int getTotal_bayar() {
        return total_bayar;
    }

    public int getTotal_item() {
        return total_item;
    }

    public String getMetode_pengiriman() {
        return metode_pengiriman;
    }
}

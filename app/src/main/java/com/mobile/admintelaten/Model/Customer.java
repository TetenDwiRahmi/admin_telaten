package com.mobile.admintelaten.Model;

public class Customer {
    private int id_customer;
    private String username;
    private String email;
    private String nohp;
    private String foto_customer;
    private String alamat;

    public Customer(int id_customer, String username, String email, String nohp, String foto_customer, String alamat) {
        this.id_customer = id_customer;
        this.username = username;
        this.email = email;
        this.nohp = nohp;
        this.foto_customer = foto_customer;
        this.alamat = alamat;
    }

    public int getId_customer() {
        return id_customer;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getNohp() {
        return nohp;
    }

    public String getFoto_customer() {
        return foto_customer;
    }

    public String getAlamat() {
        return alamat;
    }
}

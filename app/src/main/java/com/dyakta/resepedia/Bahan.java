package com.dyakta.resepedia;

public class Bahan {

    String id;
    String nama;
    String satuan;
    String tipe;

    public Bahan() {
    }

    public Bahan(String id, String nama, String satuan, String tipe) {
        this.id = id;
        this.nama = nama;
        this.satuan = satuan;
        this.tipe = tipe;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getSatuan() {
        return satuan;
    }

    public String getTipe() {
        return tipe;
    }
}

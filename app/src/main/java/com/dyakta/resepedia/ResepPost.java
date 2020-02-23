package com.dyakta.resepedia;


import java.util.Date;

public class ResepPost extends ResepPostId{

    public String judul,deskripsi,jenis_resep,bahan,langkah;
    public Double porsi,quantitas;


    public ResepPost() {
    }

    public ResepPost(String judul, String deskripsi, String jenis_resep, String bahan, String langkah, Double porsi, Double quantitas) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.jenis_resep = jenis_resep;
        this.bahan = bahan;
        this.langkah = langkah;
        this.porsi = porsi;
        this.quantitas = quantitas;
    }

    public Double getPorsi() {
        return porsi;
    }

    public void setPorsi(Double porsi) {
        this.porsi = porsi;
    }

    public Double getQuantitas() {
        return quantitas;
    }

    public void setQuantitas(Double quantitas) {
        this.quantitas = quantitas;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }


    public String getJenis_resep() {
        return jenis_resep;
    }

    public void setJenis_resep(String jenis_resep) {
        this.jenis_resep = jenis_resep;
    }

    public String getBahan() {
        return bahan;
    }

    public void setBahan(String bahan) {
        this.bahan = bahan;
    }


    public String getLangkah() {
        return langkah;
    }

    public void setLangkah(String langkah) {
        this.langkah = langkah;
    }
}


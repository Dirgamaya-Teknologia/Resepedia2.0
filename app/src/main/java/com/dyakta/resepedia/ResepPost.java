package com.dyakta.resepedia;


import java.util.Date;

public class ResepPost extends ResepPostId{

    public String judul,desc,jenis_resep,bahan,langkah,image_url,thumb,user_id;
    public Double porsi,quantitas;


    public ResepPost() {
    }

    public ResepPost(String judul, String deskripsi, String jenis_resep, String bahan, String langkah, String image_url, String thumb, String user_id, Double porsi, Double quantitas) {
        this.judul = judul;
        this.desc = deskripsi;
        this.jenis_resep = jenis_resep;
        this.bahan = bahan;
        this.langkah = langkah;
        this.image_url = image_url;
        this.thumb = thumb;
        this.user_id = user_id;
        this.porsi = porsi;
        this.quantitas = quantitas;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
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

    public String getDesc() {
        return desc;
    }

    public void setDeskripsi(String deskripsi) {
        this.desc = deskripsi;
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


package com.dyakta.resepedia;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ResepPost extends ResepPostId implements Parcelable {

    private String id, judul,desc,jenis_resep,bahan,langkah,image_url,thumb,user_id;
    private Double porsi,quantitas;


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

    public ResepPost(String id, String judul, String desc, String jenis_resep, String bahan, String langkah, String image_url, String thumb, String user_id, Double porsi, Double quantitas) {
        this.id = id;
        this.judul = judul;
        this.desc = desc;
        this.jenis_resep = jenis_resep;
        this.bahan = bahan;
        this.langkah = langkah;
        this.image_url = image_url;
        this.thumb = thumb;
        this.user_id = user_id;
        this.porsi = porsi;
        this.quantitas = quantitas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.judul);
        dest.writeString(this.desc);
        dest.writeString(this.jenis_resep);
        dest.writeString(this.bahan);
        dest.writeString(this.langkah);
        dest.writeString(this.image_url);
        dest.writeString(this.thumb);
        dest.writeString(this.user_id);
        dest.writeValue(this.porsi);
        dest.writeValue(this.quantitas);
    }

    protected ResepPost(Parcel in) {
        this.id = in.readString();
        this.judul = in.readString();
        this.desc = in.readString();
        this.jenis_resep = in.readString();
        this.bahan = in.readString();
        this.langkah = in.readString();
        this.image_url = in.readString();
        this.thumb = in.readString();
        this.user_id = in.readString();
        this.porsi = (Double) in.readValue(Double.class.getClassLoader());
        this.quantitas = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<ResepPost> CREATOR = new Parcelable.Creator<ResepPost>() {
        @Override
        public ResepPost createFromParcel(Parcel source) {
            return new ResepPost(source);
        }

        @Override
        public ResepPost[] newArray(int size) {
            return new ResepPost[size];
        }
    };
}


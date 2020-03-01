package com.dyakta.resepedia;

import android.os.Parcel;
import android.os.Parcelable;

public class Bahan extends BahanPostId implements Parcelable {

    private String id;
    private String nama;
    private String satuan;
    private String tipe;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nama);
        dest.writeString(this.satuan);
        dest.writeString(this.tipe);
    }

    protected Bahan(Parcel in) {
        this.id = in.readString();
        this.nama = in.readString();
        this.satuan = in.readString();
        this.tipe = in.readString();
    }

    public static final Parcelable.Creator<Bahan> CREATOR = new Parcelable.Creator<Bahan>() {
        @Override
        public Bahan createFromParcel(Parcel source) {
            return new Bahan(source);
        }

        @Override
        public Bahan[] newArray(int size) {
            return new Bahan[size];
        }
    };
}

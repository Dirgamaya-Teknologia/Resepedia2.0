package com.dyakta.resepedia;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;


public class BahanPostId {

    @Exclude
    public String BahanPostId;

    public <T extends BahanPostId>T withId(@NonNull final String id){
        this.BahanPostId = id;
        return (T) this;
    }


}

package com.dyakta.resepedia;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;


public class ResepPostId {

    @Exclude
    public String ResepPostId;

    public <T extends ResepPostId>T withId(@NonNull final String id){
        this.ResepPostId = id;
        return (T) this;
    }


}

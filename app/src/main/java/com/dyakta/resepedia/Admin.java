package com.dyakta.resepedia;

public class Admin {
    public String image, name, email;

    public Admin() {
    }

    public Admin(String image, String name, String email) {
        this.image = image;
        this.name = name;
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

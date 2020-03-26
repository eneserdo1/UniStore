package com.eneserdogan.unistore.Models;

public class Users {
    private String name;
    private String email;
    private String password;
    private String imageUrl;
    //private String locationInfo;

    public Users() {
    }

    public Users(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Users(String name, String email, String password, String imageUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

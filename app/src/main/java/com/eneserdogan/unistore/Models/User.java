package com.eneserdogan.unistore.Models;

public class User {
    private String ID;
    private String email;
    private String name;
    private String university;
    private Picture profilePicture;

    public User(String ID, String email, String name, String university, Picture profilePicture) {
        this.ID = ID;
        this.email = email;
        this.name = name;
        this.university = university;
        this.profilePicture = profilePicture;
    }

    public String getID() {
        return ID;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public Picture getPicture() {
        return profilePicture;
    }

    public void setPicture(Picture picture) {
        this.profilePicture = picture;
    }
}

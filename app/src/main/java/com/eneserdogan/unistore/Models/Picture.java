package com.eneserdogan.unistore.Models;

public class Picture {
    private String namePicture;
    private String urlPicture;

    public Picture(String namePicture, String urlPicture) {
        this.namePicture = namePicture;
        this.urlPicture = urlPicture;
    }

    public String getNamePicture() {
        return namePicture;
    }

    public void setNamePicture(String namePicture) {
        this.namePicture = namePicture;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }
}

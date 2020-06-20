package com.eneserdogan.unistore.Models;
import java.io.Serializable;

public class Advertisement implements Serializable {
    private String mail;
    private String title;
    private String description;
    private String category;
    private String price;
    private String Id;
    private String uuid;
    //Location

    public Advertisement(String Id,String title, String description, String category, String price,String mail,String uuid) {
        this.Id=Id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.mail=mail;
        this.uuid=uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId(){return this.Id;}

    public void setId(String Id){this.Id=Id;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMail(){
        return mail;
    }

    public void setMail(String mail){
        this.mail = mail;
    }
}


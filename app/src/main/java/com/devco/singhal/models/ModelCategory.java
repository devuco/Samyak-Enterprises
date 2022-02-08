package com.devco.singhal.models;

public class ModelCategory {
    String cname, image;

    public ModelCategory() {
    }

    public ModelCategory(String cname, String image) {
        this.cname = cname;
        this.image = image;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

package com.example.pahwa;

public class itemdetails {

    String image,name,brand,moreinfo;

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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMoreinfo() {
        return moreinfo;
    }

    public void setMoreinfo(String moreinfo) {
        this.moreinfo = moreinfo;
    }

    public itemdetails() {
    }

    public itemdetails(String image, String name, String brand, String moreinfo) {
        this.image = image;
        this.name = name;
        this.brand = brand;
        this.moreinfo = moreinfo;
    }
}

package com.example.pahwa;

public class profilesetupdetails {
    String name;
    String shopname;
    String shoplocation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShoplocation() {
        return shoplocation;
    }

    public void setShoplocation(String shoplocation) {
        this.shoplocation = shoplocation;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public profilesetupdetails() {
    }

    public profilesetupdetails(String name, String shopname, String shoplocation, String phone1, String phone2, String profilepic) {
        this.name = name;
        this.shopname = shopname;
        this.shoplocation = shoplocation;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.profilepic = profilepic;
    }

    String phone1;
    String phone2;
    String profilepic,balance;

    public profilesetupdetails(String name, String shopname, String shoplocation, String phone1, String phone2, String profilepic, String balance) {
        this.name = name;
        this.shopname = shopname;
        this.shoplocation = shoplocation;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.profilepic = profilepic;
        this.balance = balance;
    }
}

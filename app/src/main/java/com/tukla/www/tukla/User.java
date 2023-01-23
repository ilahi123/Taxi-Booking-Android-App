package com.tukla.www.tukla;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class User {

    private String userID;
    private String fullname;
    private String address;
    private String phone;
    private String idImgUrl;
    private Boolean isDriver;
    private Boolean isVerified;
    private String updatedAt;
    private Boolean isAdmin;

    public User(){
    }

    public User(
            String userID,
            String fullname,
            String address,
            String phone,
            Boolean isDriver,
            Boolean isVerified,
            String updatedAt,
            Boolean isAdmin) {

        this.userID = userID;
        this.fullname = fullname;
        this.address = address;
        this.phone = phone;
        this.isDriver = isDriver;
        this.isVerified = isVerified;
        this.updatedAt = updatedAt;
        this.isAdmin = isAdmin;
    }

    public String getUserID() {
        return userID;
    }

    public String getFullname() {
        return fullname;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return  phone;
    }

    public Boolean getIsDriver() {
        return isDriver;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIdImgUrl(String url) {
        this.idImgUrl = url;
    }

    public String getIdImgUrl() {
        return idImgUrl;
    }

}

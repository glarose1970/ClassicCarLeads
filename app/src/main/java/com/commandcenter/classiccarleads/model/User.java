package com.commandcenter.classiccarleads.model;

/**
 * Created by Command Center on 11/22/2017.
 */

public class User {

    private String userID;
    private String email;
    private String zipcode;

    public User() {
    }

    public User(String userID, String email, String zipcode) {
        this.userID = userID;
        this.email = email;
        this.zipcode = zipcode;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}

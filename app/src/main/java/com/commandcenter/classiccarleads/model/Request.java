package com.commandcenter.classiccarleads.model;

/**
 * Created by Command Center on 12/17/2017.
 */

public class Request {

    private Dealer dealerInfo;
    private String title;
    private String email;
    private String phone;
    private String name;
    private String comment;

    public Request() {
    }

    public Request(Dealer dealerInfo, String title, String email, String phone, String name, String comment) {
        this.dealerInfo = dealerInfo;
        this.title = title;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Dealer getDealerInfo() {
        return dealerInfo;
    }

    public void setDealerInfo(Dealer dealerInfo) {
        this.dealerInfo = dealerInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

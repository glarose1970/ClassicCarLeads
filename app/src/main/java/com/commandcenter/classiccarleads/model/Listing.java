package com.commandcenter.classiccarleads.model;

/**
 * Created by Command Center on 11/22/2017.
 */

public class Listing {

    //==========PRIVATE FIELDS==========//
    private Dealer dealerInfo;
    private String listingID;
    private String img_url;
    private String title;
    private String make;
    private String model;
    private String year;
    private String price;
    private String desc;
    private String location;
    //==========END PRIVATE FIELDS==========//

    //==========CONSTRUCTOR==========//
    public Listing() {
    }

    public Listing(String listingID, String img_url, String title, String price, String desc) {
        this.listingID = listingID;
        this.img_url = img_url;
        this.title = title;
        this.price = price;
        this.desc = desc;
    }

    public Listing(Dealer dealerInfo, String listingID, String img_url, String title, String make, String model, String year, String price, String desc, String location) {
        this.dealerInfo = dealerInfo;
        this.listingID = listingID;
        this.img_url = img_url;
        this.title = title;
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.desc = desc;
        this.location = location;
    }
    //==========END CONSTRUCTOR==========//

    //==========GETTER/SETTER==========//
    public Dealer getDealerInfo() {
        return dealerInfo;
    }

    public void setDealerInfo(Dealer dealerInfo) {
        this.dealerInfo = dealerInfo;
    }

    public String getListingID() {
        return listingID;
    }

    public void setListingID(String listingID) {
        this.listingID = listingID;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    //==========END GETTER/SETTER==========//
}

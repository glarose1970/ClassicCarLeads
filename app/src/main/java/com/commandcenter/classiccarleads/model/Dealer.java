package com.commandcenter.classiccarleads.model;

/**
 * Created by Command Center on 11/22/2017.
 */

public class Dealer {

    //==========PRIVATE FIELDS==========//
    private String dealer_name;
    private String dealer_url;
    private String address;
    //==========END PRIVATE FIELDS==========//

    //==========CONSTRUCTOR==========//
    public Dealer() {
    }

    public Dealer(String dealer_name, String dealer_url, String address) {
        this.dealer_name = dealer_name;
        this.dealer_url = dealer_url;
        this.address = address;
    }
    //==========END CONSTRUCTOR==========//

    //==========GETTERS/SETTERS==========//
    public String getDealer_name() {
        return dealer_name;
    }

    public void setDealer_name(String dealer_name) {
        this.dealer_name = dealer_name;
    }

    public String getDealer_url() {
        return dealer_url;
    }

    public void setDealer_url(String dealer_url) {
        this.dealer_url = dealer_url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    //==========END GETTERS/SETTERS==========//
}

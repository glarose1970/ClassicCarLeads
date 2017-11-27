package com.commandcenter.classiccarleads.utils;

/**
 * Created by Command Center on 11/26/2017.
 */

public class VariableHolder {

    private String zipcode;


    public VariableHolder() {
    }

    public VariableHolder(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}

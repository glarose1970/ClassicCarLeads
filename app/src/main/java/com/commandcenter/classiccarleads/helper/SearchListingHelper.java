package com.commandcenter.classiccarleads.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by Command Center on 11/22/2017.
 */

public class SearchListingHelper {

    //==========CLASS FIELDS==========//
    private String Make;
    private String Model;
    private String Loacation;
    private String Year;
    //==========END CLASS FIELDS==========//

    //==========FIREBASE==========//
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference mDataRef;
    //==========END FIREBASE==========//

    //==========CONSTRUCTOR==========//
    public SearchListingHelper() {

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance();
        mDataRef = mData.getReference().child("users");
    }
    //==========END CONSTRUCTOR==========//

    //==========SEARCH METHOD==========//
    public void Search(String make, String model, String location, String year) {

        String base_url = "https://classiccars.com/listings/find/" + year + "/" + make + "/" + model + "?auction=false&dealer=true&private=false&state=" + location ;
        try {
            Document mainDoc = Jsoup.connect(base_url).get();
            String totalListing = mainDoc.select("//*[@class='search-result-info]").text();
            String test = "";
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String doSearchResult(String make, String model, String location, String year) {

        String result = "";
        String base_url = "https://classiccars.com/listings/find/" + year + "/" + make + "/" + model + "?auction=false&dealer=true&private=false&state=" + location ;
        try {
            Document mainDoc = Jsoup.connect(base_url).get();
            String totalListing = mainDoc.getElementsByClass("search-result-info").text();
            result = totalListing;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //==========END SEARCH METHOD==========//

    //==========GETTER/SETTER==========//

    public String getMake() {
        return Make;
    }

    public void setMake(String make) {
        Make = make;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getLoacation() {
        return Loacation;
    }

    public void setLoacation(String loacation) {
        Loacation = loacation;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    //==========END GETTER/SETTER==========//
}

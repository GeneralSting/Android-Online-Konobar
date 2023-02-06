package com.example.onlinekonobar.Models;

import java.util.ArrayList;
import java.util.Map;

public class Cafe {
    String cafeLocation;
    String cafeName;
    Integer cafeTables;
    String cafeOwnerGmail;
    String getCafeOwnerLastname;
    String cafeOwnerName;
    String cafeOwnerOib;
    String cafeOwnerPhoneNumber;
    Map<String, Drink> cafeDrinks;
    Map<String, CafeCategory> cafeDrinksCategories;

    public Cafe() {
        // Default constructor required for calls to DataSnapshot.getValue(Cafe.class)
    }

    public Cafe(String cafeLocation, String cafeName, Integer cafeTables, String cafeOwnerGmail, String getCafeOwnerLastname, String cafeOwnerName, String cafeOwnerOib, String cafeOwnerPhoneNumber, Map<String, Drink> cafeDrinks) {
        this.cafeLocation = cafeLocation;
        this.cafeName = cafeName;
        this.cafeTables = cafeTables;
        this.cafeOwnerGmail = cafeOwnerGmail;
        this.getCafeOwnerLastname = getCafeOwnerLastname;
        this.cafeOwnerName = cafeOwnerName;
        this.cafeOwnerOib = cafeOwnerOib;
        this.cafeOwnerPhoneNumber = cafeOwnerPhoneNumber;
        this.cafeDrinks = cafeDrinks;
    }

    public String getCafeLocation() {
        return cafeLocation;
    }

    public void setCafeLocation(String cafeLocation) {
        this.cafeLocation = cafeLocation;
    }

    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    public String getCafeOwnerGmail() {
        return cafeOwnerGmail;
    }

    public void setCafeOwnerGmail(String cafeOwnerGmail) {
        this.cafeOwnerGmail = cafeOwnerGmail;
    }

    public String getGetCafeOwnerLastname() {
        return getCafeOwnerLastname;
    }

    public void setGetCafeOwnerLastname(String getCafeOwnerLastname) {
        this.getCafeOwnerLastname = getCafeOwnerLastname;
    }

    public String getCafeOwnerName() {
        return cafeOwnerName;
    }

    public void setCafeOwnerName(String cafeOwnerName) {
        this.cafeOwnerName = cafeOwnerName;
    }

    public String getCafeOwnerOib() {
        return cafeOwnerOib;
    }

    public void setCafeOwnerOib(String cafeOwnerOib) {
        this.cafeOwnerOib = cafeOwnerOib;
    }

    public String getCafeOwnerPhoneNumber() {
        return cafeOwnerPhoneNumber;
    }

    public void setCafeOwnerPhoneNumber(String cafeOwnerPhoneNumber) {
        this.cafeOwnerPhoneNumber = cafeOwnerPhoneNumber;
    }

    public Map<String, Drink> getCafeDrinks() {
        return cafeDrinks;
    }

    public void setCafeDrinks(Map<String, Drink> cafeDrinks) {
        this.cafeDrinks = cafeDrinks;
    }

    public Integer getCafeTables() {
        return cafeTables;
    }

    public void setCafeTables(Integer cafeTables) {
        this.cafeTables = cafeTables;
    }

    public Map<String, CafeCategory> getCafeDrinksCategories() {
        return cafeDrinksCategories;
    }

    public void setCafeDrinksCategories(Map<String, CafeCategory> cafeDrinksCategories) {
        this.cafeDrinksCategories = cafeDrinksCategories;
    }
}

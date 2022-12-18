package com.example.onlinekonobar.Models;

import java.util.ArrayList;

public class Cafe {
    String cafeLocation;
    String cafeName;
    String cafeOwnerGmail;
    String getCafeOwnerLastname;
    String cafeOwnerName;
    String cafeOwnerOib;
    String cafeOwnerPhoneNumber;

    public Cafe() {
        // Default constructor required for calls to DataSnapshot.getValue(Cafe.class)
    }

    public Cafe(String cafeLocation, String cafeName, String cafeOwnerGmail, String getCafeOwnerLastname, String cafeOwnerName, String cafeOwnerOib, String cafeOwnerPhoneNumber, ArrayList<Employee> cafeEmployees) {
        this.cafeLocation = cafeLocation;
        this.cafeName = cafeName;
        this.cafeOwnerGmail = cafeOwnerGmail;
        this.getCafeOwnerLastname = getCafeOwnerLastname;
        this.cafeOwnerName = cafeOwnerName;
        this.cafeOwnerOib = cafeOwnerOib;
        this.cafeOwnerPhoneNumber = cafeOwnerPhoneNumber;
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
}

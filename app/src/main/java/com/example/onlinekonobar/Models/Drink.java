package com.example.onlinekonobar.Models;

public class Drink {
    String cafeDrinkId;
    String cafeDrinkName;
    String cafeDrinkDescription;
    Float cafeDrinkPrice;
    String cafeDrinkImage;

    public Drink(){}

    public Drink(String cafeDrinkId, String cafeDrinkName, String cafeDrinkDescription, Float cafeDrinkPrice, String cafeDrinkImage) {
        this.cafeDrinkId = cafeDrinkId;
        this.cafeDrinkName = cafeDrinkName;
        this.cafeDrinkDescription = cafeDrinkDescription;
        this.cafeDrinkPrice = cafeDrinkPrice;
        this.cafeDrinkImage = cafeDrinkImage;
    }

    public String getCafeDrinkImage() {
        return cafeDrinkImage;
    }

    public void setCafeDrinkImage(String cafeDrinkImage) {
        this.cafeDrinkImage = cafeDrinkImage;
    }

    public String getCafeDrinkName() {
        return cafeDrinkName;
    }

    public void setCafeDrinkName(String cafeDrinkName) {
        this.cafeDrinkName = cafeDrinkName;
    }

    public String getCafeDrinkDescription() {
        return cafeDrinkDescription;
    }

    public void setCafeDrinkDescription(String cafeDrinkDescription) {
        this.cafeDrinkDescription = cafeDrinkDescription;
    }

    public Float getCafeDrinkPrice() {
        return cafeDrinkPrice;
    }

    public void setCafeDrinkPrice(Float cafeDrinkPrice) {
        this.cafeDrinkPrice = cafeDrinkPrice;
    }

    public String getCafeDrinkId() {
        return cafeDrinkId;
    }

    public void setCafeDrinkId(String cafeDrinkId) {
        this.cafeDrinkId = cafeDrinkId;
    }
}

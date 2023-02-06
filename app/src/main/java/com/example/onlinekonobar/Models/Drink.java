package com.example.onlinekonobar.Models;

public class Drink {
    String cafeDrinkId;
    String cafeDrinkName;
    String cafeDrinkDescription;
    Float cafeDrinkPrice;
    String cafeDrinkPriceString;
    String cafeDrinkImage;

    String cafeDrinkCategoryId;

    public Drink(){}

    public Drink(String cafeDrinkId, String cafeDrinkName, String cafeDrinkDescription, Float cafeDrinkPrice, String cafeDrinkImage) {
        this.cafeDrinkId = cafeDrinkId;
        this.cafeDrinkName = cafeDrinkName;
        this.cafeDrinkDescription = cafeDrinkDescription;
        this.cafeDrinkPrice = cafeDrinkPrice;
        this.cafeDrinkImage = cafeDrinkImage;
    }

    public Drink(String cafeDrinkName, String cafeDrinkDescription, Float cafeDrinkPrice, String cafeDrinkImage) {
        this.cafeDrinkName = cafeDrinkName;
        this.cafeDrinkDescription = cafeDrinkDescription;
        this.cafeDrinkPrice = cafeDrinkPrice;
        this.cafeDrinkImage = cafeDrinkImage;
    }

    public Drink(String cafeDrinkId, String cafeDrinkName, String cafeDrinkDescription, String cafeDrinkPrice, String cafeDrinkImage) {
        this.cafeDrinkId = cafeDrinkId;
        this.cafeDrinkName = cafeDrinkName;
        this.cafeDrinkDescription = cafeDrinkDescription;
        this.cafeDrinkPriceString = cafeDrinkPrice;
        this.cafeDrinkImage = cafeDrinkImage;
    }

    public Drink(String cafeDrinkCategoryId, String cafeDrinkName, String cafeDrinkDescription, String cafeDrinkImage, Float cafeDrinkPrice) {
        this.cafeDrinkCategoryId = cafeDrinkCategoryId;
        this.cafeDrinkName = cafeDrinkName;
        this.cafeDrinkDescription = cafeDrinkDescription;
        this.cafeDrinkImage = cafeDrinkImage;
        this.cafeDrinkPrice = cafeDrinkPrice;
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

    public String getCafeDrinkCategoryId() {
        return cafeDrinkCategoryId;
    }

    public void setCafeDrinkCategoryId(String cafeDrinkCategoryId) {
        this.cafeDrinkCategoryId = cafeDrinkCategoryId;
    }

    public String getCafeDrinkPriceString() {
        return cafeDrinkPriceString;
    }

    public void setCafeDrinkPriceString(String cafeDrinkPriceString) {
        this.cafeDrinkPriceString = cafeDrinkPriceString;
    }
}

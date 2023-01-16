package com.example.onlinekonobar.Models;

public class Drink {
    String cafeDrinkCategory;
    String cafeDrinkName;
    String cafeDrinkDescription;
    Float cafeDrinkPrice;

    public Drink(){}

    public Drink(String cafeDrinkCategory, String cafeDrinkName, String cafeDrinkDescription, Float cafeDrinkPrice) {
        this.cafeDrinkCategory = cafeDrinkCategory;
        this.cafeDrinkName = cafeDrinkName;
        this.cafeDrinkDescription = cafeDrinkDescription;
        this.cafeDrinkPrice = cafeDrinkPrice;
    }

    public String getCafeDrinkCategory() {
        return cafeDrinkCategory;
    }

    public void setCafeDrinkCategory(String cafeDrinkCategory) {
        this.cafeDrinkCategory = cafeDrinkCategory;
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

}

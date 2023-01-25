package com.example.onlinekonobar.Models;

public class DrinkBill {
    String drinkId;
    String drinkName;
    Float drinkPrice;
    Float drinkTotalPrice;
    int drinkAmount;
    String drinkImage;
    String drinkPriceString;
    String drinkTotalPriceString;

    public DrinkBill() {

    }

    public DrinkBill(String drinkId, String drinkName, Float drinkPrice, Float drinkTotalPrice, int drinkAmount) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.drinkPrice = drinkPrice;
        this.drinkTotalPrice = drinkTotalPrice;
        this.drinkAmount = drinkAmount;
    }

    public DrinkBill(String drinkId, String drinkName, Float drinkPrice, Float drinkTotalPrice, int drinkAmount, String drinkImage) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.drinkPrice = drinkPrice;
        this.drinkTotalPrice = drinkTotalPrice;
        this.drinkAmount = drinkAmount;
        this.drinkImage = drinkImage;
    }

    public DrinkBill(String drinkId, String drinkName, String drinkPriceString, String drinkTotalPriceString, int drinkAmount, String drinkImage) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.drinkPriceString = drinkPriceString;
        this.drinkTotalPriceString = drinkTotalPriceString;
        this.drinkAmount = drinkAmount;
        this.drinkImage = drinkImage;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public Float getDrinkPrice() {
        return drinkPrice;
    }

    public void setDrinkPrice(Float drinkPrice) {
        this.drinkPrice = drinkPrice;
    }

    public Float getDrinkTotalPrice() {
        return drinkTotalPrice;
    }

    public void setDrinkTotalPrice(Float drinkTotalPrice) {
        this.drinkTotalPrice = drinkTotalPrice;
    }

    public int getDrinkAmount() {
        return drinkAmount;
    }

    public void setDrinkAmount(int drinkAmount) {
        this.drinkAmount = drinkAmount;
    }

    public String getDrinkImage() {
        return drinkImage;
    }

    public void setDrinkImage(String drinkImage) {
        this.drinkImage = drinkImage;
    }

    public String getDrinkPriceString() {
        return drinkPriceString;
    }

    public void setDrinkPriceString(String drinkPriceString) {
        this.drinkPriceString = drinkPriceString;
    }

    public String getDrinkTotalPriceString() {
        return drinkTotalPriceString;
    }

    public void setDrinkTotalPriceString(String drinkTotalPriceString) {
        this.drinkTotalPriceString = drinkTotalPriceString;
    }
}

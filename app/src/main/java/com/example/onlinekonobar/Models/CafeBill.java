package com.example.onlinekonobar.Models;

import java.util.Date;
import java.util.HashMap;

public class CafeBill {
    String cafeBillDate;
    //for easier saving in database
    String cafeBillTotalPrice;
    int cafeBillProductsAmount;
    String cafeBillEmployee;
    Integer cafeBillTableNumber;
    HashMap<String, DrinkBill> cafeBillDrinks;

    public CafeBill(String cafeBillDate, String cafeBillTotalPrice, int cafeBillProductsAmount, String cafeBillEmployee,
                    Integer cafeBillTableNumber, HashMap<String, DrinkBill> cafeBillDrinks) {
        this.cafeBillDate = cafeBillDate;
        this.cafeBillTotalPrice = cafeBillTotalPrice;
        this.cafeBillProductsAmount = cafeBillProductsAmount;
        this.cafeBillEmployee = cafeBillEmployee;
        this.cafeBillTableNumber = cafeBillTableNumber;
        this.cafeBillDrinks = cafeBillDrinks;
    }

    public String getCafeBillDate() {
        return cafeBillDate;
    }

    public void setCafeBillDate(String cafeBillDate) {
        this.cafeBillDate = cafeBillDate;
    }

    public String getCafeBillTotalPrice() {
        return cafeBillTotalPrice;
    }

    public void setCafeBillTotalPrice(String cafeBillTotalPrice) {
        this.cafeBillTotalPrice = cafeBillTotalPrice;
    }

    public int getCafeBillProductsAmount() {
        return cafeBillProductsAmount;
    }

    public void setCafeBillProductsAmount(int cafeBillProductsAmount) {
        this.cafeBillProductsAmount = cafeBillProductsAmount;
    }

    public String getCafeBillEmployee() {
        return cafeBillEmployee;
    }

    public void setCafeBillEmployee(String cafeBillEmployee) {
        this.cafeBillEmployee = cafeBillEmployee;
    }

    public Integer getCafeBillTableNumber() {
        return cafeBillTableNumber;
    }

    public void setCafeBillTableNumber(Integer cafeBillTableNumber) {
        this.cafeBillTableNumber = cafeBillTableNumber;
    }

    public HashMap<String, DrinkBill> getCafeBillDrinks() {
        return cafeBillDrinks;
    }

    public void setCafeBillDrinks(HashMap<String, DrinkBill> cafeBillDrinks) {
        this.cafeBillDrinks = cafeBillDrinks;
    }
}

package com.example.onlinekonobar.Interfaces;

import com.example.onlinekonobar.Models.DrinkBill;

import java.util.HashMap;

public interface CallBackCart {
    void updateCartDrinks(HashMap<String, DrinkBill> cartDrinks);
}

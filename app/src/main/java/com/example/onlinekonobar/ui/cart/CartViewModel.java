package com.example.onlinekonobar.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlinekonobar.Models.DrinkBill;

import java.util.HashMap;

public class CartViewModel extends ViewModel {

    //for drinks in cart
    private final MutableLiveData<HashMap<String, DrinkBill>> drinksInCart = new MutableLiveData<HashMap<String, DrinkBill>>();

    public LiveData<HashMap<String, DrinkBill>> getDrinksInCart() {
        return drinksInCart;
    }

    public void setDrinksInCart(HashMap<String, DrinkBill> value) {
        drinksInCart.setValue(value);
    }

    //for collecting cafe ID inside cartFragment
    private final MutableLiveData<String> cafeId = new MutableLiveData<String>();

    public LiveData<String> getCafeId() {
        return cafeId;
    }

    public void setCafeId(String recivedCafeId) {
        cafeId.setValue(recivedCafeId);
    }

    //for collecting employee ID
    private final MutableLiveData<String> employeeId = new MutableLiveData<String>();

    public LiveData<String> getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String recivedEmployeeId) {
        employeeId.setValue(recivedEmployeeId);
    }

}
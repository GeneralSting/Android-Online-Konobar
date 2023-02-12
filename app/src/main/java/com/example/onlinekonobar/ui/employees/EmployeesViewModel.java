package com.example.onlinekonobar.ui.employees;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EmployeesViewModel extends ViewModel {

    private final MutableLiveData<String> cafeId = new MutableLiveData<String>();

    public LiveData<String> getCafeId() {
        return cafeId;
    }

    public void setCafeId(String value) {
        cafeId.setValue(value);
    }
}
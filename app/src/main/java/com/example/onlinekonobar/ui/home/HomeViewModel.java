package com.example.onlinekonobar.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> ownerNumber = new MutableLiveData<String>();

    public LiveData<String> getOwnerNumber() {
        return ownerNumber;
    }

    public void setOwnerNumber(String value) {
        ownerNumber.setValue(value);
    }
}
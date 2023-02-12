package com.example.onlinekonobar.ui.logout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogoutViewModel extends ViewModel {

    private final MutableLiveData<Boolean> logout = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLogout() {
        return logout;
    }

    public void setLogout(Boolean value) {
        logout.setValue(value);
    }
}
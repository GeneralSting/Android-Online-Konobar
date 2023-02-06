package com.example.onlinekonobar.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<Integer> rvSettingsDisplayed = new MutableLiveData<Integer>();

    public LiveData<Integer> getRvSettingsDisplayed() {
        return rvSettingsDisplayed;
    }

    public void setRvSettingsDisplayed(Integer value) {
        rvSettingsDisplayed.setValue(value);
    }


    private final MutableLiveData<Boolean> settingsChangeDisplayed = new MutableLiveData<Boolean>();

    public LiveData<Boolean> getSettingsChangeDisplayed() {
        return settingsChangeDisplayed;
    }

    public void setSettingsChangeDisplayed(Boolean value) {
        settingsChangeDisplayed.setValue(value);
    }

}
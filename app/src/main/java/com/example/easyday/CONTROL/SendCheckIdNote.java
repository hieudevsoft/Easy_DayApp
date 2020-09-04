package com.example.easyday.CONTROL;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendCheckIdNote extends ViewModel {
    MutableLiveData<Boolean> checkId = new MutableLiveData<>();

    public void setCheckId(MutableLiveData<Boolean> checkId) {
        this.checkId = checkId;
    }

    public MutableLiveData<Boolean> getCheckId() {
        return checkId;
    }
}

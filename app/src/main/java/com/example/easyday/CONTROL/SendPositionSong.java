package com.example.easyday.CONTROL;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendPositionSong extends ViewModel {
    MutableLiveData<Integer> position = new MediatorLiveData<>();

    public MutableLiveData<Integer> getPosition() {
        return position;
    }
}

package com.example.easyday.CONTROL;

import android.graphics.Bitmap;
import android.net.Uri;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendTheme extends ViewModel {
    public MutableLiveData<String> uri = new MutableLiveData<>();

    public MutableLiveData<String> getUri() {
        return uri;
    }
}

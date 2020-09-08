package com.example.easyday.FRAGMENT.music;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.easyday.R;


public class SplashMusicFragment extends Fragment {
    View view;
    public SplashMusicFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_splash_music, container, false);
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    NavDirections action = SplashMusicFragmentDirections.ActionToHomeMusic();
                    try {
                        Navigation.findNavController(getView()).navigate(action);
                    }catch (Exception e)
                    {

                    }

                }
            },2000);
        }catch (Exception e)
        {

        }
        return view;
    }

}
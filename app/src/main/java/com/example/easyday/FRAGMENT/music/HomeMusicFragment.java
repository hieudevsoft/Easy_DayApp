package com.example.easyday.FRAGMENT.music;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.example.easyday.ADAPTER.ViewPagerAdapterMusic;
import com.example.easyday.FRAGMENT.music.fragmentsviewpager2.ListMusicFragment;
import com.example.easyday.FRAGMENT.music.fragmentsviewpager2.PlayMusicFragment;
import com.example.easyday.R;

import java.util.ArrayList;
import java.util.List;


public class HomeMusicFragment extends Fragment {
     public static ViewPager viewPager;
     View view;
     ViewPagerAdapterMusic adapter;
     List<Fragment> listFragment;
    public HomeMusicFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_music, container, false);
        mapping(view);
        return view;
    }

     @Override
     public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
         initComponents();
         super.onViewCreated(view, savedInstanceState);
     }

     private void mapping(View view)
     {
        viewPager = view.findViewById(R.id.viewPagerMakeListMusic);
     }
     private void initComponents()
     {
         listFragment = new ArrayList<>();
         listFragment.add(new ListMusicFragment());
         listFragment.add(new PlayMusicFragment());
         adapter = new ViewPagerAdapterMusic(getActivity().getSupportFragmentManager(), listFragment);
         viewPager.setAdapter(adapter);
         viewPager.setPageTransformer(true, new AccordionTransformer());
     }
 }
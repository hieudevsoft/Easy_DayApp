package com.example.easyday.ACTIVITY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.FRAGMENT.note.MainNoteFragment;
import com.example.easyday.FRAGMENT.note.SetNoteFragment;
import com.example.easyday.R;

public class ToDoListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_to_do_list);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        MainNoteFragment.check=false;
        if (count == 0) {
            finish();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

}
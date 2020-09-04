package com.example.easyday.ACTIVITY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.easyday.ACTIVITY.Welcome.SignInActivity;
import com.example.easyday.R;
import com.example.easyday.CONTROL.TOOL;

public class SplashActivity extends AppCompatActivity {
    RelativeLayout relativeLayout_Splash;
    ImageView imgView_Splash;
    TextView tv_SplashAppName, tv_Bottom_Splash;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.splash_layout);
        mapping();
        TOOL.setAnimation(relativeLayout_Splash, this, R.anim.anim_splash_alpha);
        TOOL.setAnimation(imgView_Splash, this,R.anim.anim_spash_top_to_bottom );
        TOOL.setAnimation(tv_Bottom_Splash, this,R.anim.anim_splash_bottom_to_top );
        TOOL.setAnimation(tv_SplashAppName, this,R.anim.anim_left_to_right );
        TOOL.setTimeHideView(progressBar, 5000);
        move();
    }

    private void mapping()
    {
        relativeLayout_Splash = findViewById(R.id.rlt_splash);
        imgView_Splash = findViewById(R.id.img_ic_splash);
        tv_Bottom_Splash = findViewById(R.id.tv_bottom_splash);
        tv_SplashAppName = findViewById(R.id.tv_splash);
        progressBar = findViewById(R.id.progressbar);
    }

    private void move()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplication(), SignInActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this, (View)tv_SplashAppName,"app_name_transition");
                startActivity(intent,options.toBundle() );

            }
        }, 5500);
    }
}
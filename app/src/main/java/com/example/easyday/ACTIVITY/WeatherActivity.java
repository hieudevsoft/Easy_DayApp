package com.example.easyday.ACTIVITY;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyday.CLASS.ParserJSonWeather;
import com.example.easyday.CLASS.Weather;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;

public class WeatherActivity extends AppCompatActivity {
    TextView tv_Location, tv_TimeNow, tv_Status, tv_Temp, tv_TempMin, tv_TempMax, tv_SunRise, tv_SunSet, tv_Wind, tv_Pressure, tv_Humidity;
    RelativeLayout main;
    EditText edt_city;
    Weather weather;
    Button submit_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_weather);
        mapping();
        final ParserJSonWeather parserJSonWeather = ParserJSonWeather.getInstance();
        parserJSonWeather.setContext(this);
        weather = new Weather();
        submit_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parserJSonWeather.processParser(main, ParserJSonWeather.editStatusWeather(edt_city.getText().toString()).trim(), weather);
                updateView();
            }
        });

    }

    private void mapping() {
        tv_Location = findViewById(R.id.address);
        tv_TimeNow = findViewById(R.id.updated_at);
        tv_Status = findViewById(R.id.status);
        tv_Temp = findViewById(R.id.temp);
        tv_TempMin = findViewById(R.id.temp_min);
        tv_TempMax = findViewById(R.id.temp_max);
        tv_SunRise = findViewById(R.id.sunrise);
        tv_SunSet = findViewById(R.id.sunset);
        tv_Wind = findViewById(R.id.wind);
        tv_Pressure = findViewById(R.id.pressure);
        tv_Humidity = findViewById(R.id.humidity);
        main = findViewById(R.id.main_layout_weather);
        edt_city = findViewById(R.id.edt_location);
        submit_city = findViewById(R.id.bt_submit_city);
    }

    private void updateView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_Humidity.setText(weather.getHumidity());
                tv_Location.setText(weather.getLocation());
                tv_Pressure.setText(weather.getPressure());
                tv_Status.setText(weather.getStatus());
                tv_SunRise.setText(weather.getSunrise());
                tv_SunSet.setText(weather.getSunset());
                tv_Temp.setText(weather.getTemp());
                tv_TempMax.setText(weather.getMaxTemp());
                tv_TempMin.setText(weather.getMinTemp());
                tv_Wind.setText(weather.getWind());
                tv_TimeNow.setText(weather.getTime());
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);
            }
        }, 1700);
        findViewById(R.id.layout_enterCity).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_exit_screen));
    }



    @Override
    public void onBackPressed() {
        findViewById(R.id.mainContainer).setVisibility(View.GONE);
        super.onBackPressed();
    }
}
package com.example.easyday.ACTIVITY;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;
import com.example.easyday.SERVICE.AlarmService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {
    Button bt_submit_alram, bt_cancel_alarm, bt_turn_off;
    TextView tv_time;
    EditText edt_setDesTime;
    int timeHour, timeMinutes;
    Boolean checkPickTime = false;
    SharedPreferences sharePre;
    Calendar c;
    MediaPlayer doneTime;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_alarm);
        mapping();
        initCalendar();
        tv_time.setText(getSharedPreferences("alarmRemember", MODE_PRIVATE).getString("timeText", ""));
        edt_setDesTime.setText(getSharedPreferences("alarmRemember", MODE_PRIVATE).getString("desText", ""));
        checkPickTime = getSharedPreferences("alarmRemember", MODE_PRIVATE).getBoolean("checkTime", false);
        bt_submit_alram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPickTime && tv_time.getText().toString().length() != 0) {
                    doneTime = MediaPlayer.create(getApplicationContext(), R.raw.pop);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doneTime.release();
                            doneTime = null;
                        }
                    }, 1000);
                    doneTime.start();
                    sharePre = getSharedPreferences("alarmRemember", MODE_PRIVATE);
                    sharePre.edit().
                            putString("timeText", tv_time.getText().toString()).putBoolean("checkTime", checkPickTime)
                            .putString("desText", edt_setDesTime.getText().toString().trim())
                            .apply();
                    openAlarm(c);
                    finish();
                } else TOOL.setToast(getApplicationContext(), "Please set time alarm!!");
            }


        });
        bt_cancel_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });
        bt_turn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    intent = new Intent(getApplicationContext(),AlarmService.class);
                    intent.putExtra("checkMusic", false);
                    startService(intent);

            }
        });
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogAlarm();
            }
        });
    }

    private void openDialogAlarm() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(AlarmActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeHour = hourOfDay;
                timeMinutes = minute;
                checkPickTime = true;
                String time = hourOfDay + ":" + minute;
                SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                Date date = null;
                getSharedPreferences("alarmRemember", MODE_PRIVATE)
                        .edit()
                        .putInt("hour", hourOfDay)
                        .putInt("minute", minute)
                        .apply();
                try {
                    c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);
                    c.set(Calendar.SECOND, 0);
                } catch (Exception e) {
                    initCalendar();
                }

                try {
                    date = f24Hours.parse(time);
                    tv_time.setText(new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, 12, 0, false);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.updateTime(timeHour, timeMinutes);
        timePickerDialog.show();
    }

    private void openAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        intent = new Intent(this, AlarmService.class);
        intent.putExtra("contentText", edt_setDesTime.getText().toString().trim());
        intent.putExtra("music", R.raw.alarm);
        intent.putExtra("checkMusic", true);
        try {
            if (c.before(Calendar.getInstance())) c.add(Calendar.DATE, 1);
            PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        } catch (Exception e) {

        }
    }

    private void cancelAlarm() {
        edt_setDesTime.setText("");
        tv_time.setText("");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private void mapping() {
        bt_cancel_alarm = findViewById(R.id.button_cancel_alarm);
        bt_submit_alram = findViewById(R.id.button_submit_alarm);
        tv_time = findViewById(R.id.tv_Time);
        bt_turn_off = findViewById(R.id.bt_turnoff_bell);
        edt_setDesTime = findViewById(R.id.edt_Des_Time);
    }

    private void initCalendar() {
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, getSharedPreferences("alarmRemember", MODE_PRIVATE).getInt("hour", 0));
        c.set(Calendar.MINUTE, getSharedPreferences("alarmRemember", MODE_PRIVATE).getInt("minute", 0));
        c.set(Calendar.SECOND, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(intent!=null)
        stopService(intent);
    }
}
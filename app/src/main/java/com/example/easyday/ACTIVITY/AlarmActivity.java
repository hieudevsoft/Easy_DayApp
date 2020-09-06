package com.example.easyday.ACTIVITY;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;

import net.steamcrafted.lineartimepicker.dialog.LinearTimePickerDialog;

public class AlarmActivity extends AppCompatActivity {
    Button bt_submit_alram,bt_cancel_alarm;
    TextView tv_time;
    EditText edt_setDesTime;
    LinearTimePickerDialog.Builder dialog;
    Boolean checkPickTime = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_alarm);
        mapping();
        openDialogAlarm();
        if(checkPickTime){

        }
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogAlarm();
            }
        });
    }

    private void openDialogAlarm()
    {
      dialog = LinearTimePickerDialog.Builder.with(this);
        dialog.setShowTutorial(true);
        dialog.setButtonCallback(new LinearTimePickerDialog.ButtonCallback() {
            @Override
            public void onPositive(DialogInterface dialog, int hour, int minutes) {
                tv_time.setText(hour + " : " + minutes);
                checkPickTime=true;
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                if(tv_time.getText().toString().length()==0)
                {
                checkPickTime=false;
                TOOL.setToast(getApplicationContext(),"You don't pick the time !!!");
                }
            }
        });
      LinearTimePickerDialog dialogTimePicker= dialog.build();
     dialogTimePicker.show();
    }
    private void mapping()
    {
        bt_cancel_alarm = findViewById(R.id.button_cancel_alarm);
        bt_submit_alram = findViewById(R.id.button_submit_alarm);
        tv_time = findViewById(R.id.tv_Time);
        edt_setDesTime = findViewById(R.id.edt_Des_Time);
    }
}
package com.example.easyday.ACTIVITY.Welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easyday.ENTITY.Account;
import com.example.easyday.R;
import com.example.easyday.CONTROL.TOOL;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText edt_email, edt_password,edt_confirm_password;
    Button bt_login_signup, bt_signup;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_signup_welcome);
        mAuth = FirebaseAuth.getInstance();
        mapping();
        showSignIn();
        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

    }

    private void mapping() {
        edt_email = findViewById(R.id.tv_signup_email);
        edt_password = findViewById(R.id.tv_signup_password);
        edt_confirm_password = findViewById(R.id.tv_signup_ConfirmPassword);
        bt_login_signup = findViewById(R.id.bt_Login_SignUp);
        bt_signup = findViewById(R.id.bt_SignUp);
    }

    private void showSignIn()
    {
        bt_login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter_screen, R.anim.anim_exit_screen);

            }
        });
    }

    private boolean validateEmail(String email)
    {
        String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void createUser()
    {
        if(TOOL.emptyEditText(edt_email)||TOOL.emptyEditText(edt_password)||TOOL.emptyEditText(edt_confirm_password))
        {
            Toast.makeText(this, "Register Failure~", Toast.LENGTH_LONG).show();
        }
        else if(!validateEmail(edt_email.getText().toString().trim())){
            edt_email.setError("Email is not validation");
            edt_email.requestFocus();
        } else if(edt_password.getText().toString().length()<6){
            edt_password.setError("Minimum password is 6 characters");
            edt_password.requestFocus();
        } else if(!edt_password.getText().toString().trim().equals(edt_confirm_password.getText().toString().trim()))
        {
            edt_confirm_password.setError("Password is not matcher");
            edt_confirm_password.requestFocus();
        }
        else {
            mAuth.createUserWithEmailAndPassword(edt_email.getText().toString().trim(), edt_password.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                toast = new Toast(getApplicationContext());
                                toast.setView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_register_successful, null));
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Register Successful~", Toast.LENGTH_LONG).show();
                                    }
                                }, 2500);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(mAuth.getCurrentUser().getUid()).setValue(new Account("Name",edt_email.getText().toString().trim(),mAuth.getCurrentUser().getPhoneNumber()+"","Male","https://i7.pngguru.com/preview/1016/429/148/computer-icons-download-avatar-avatar.jpg",false));
                            }
                            else Toast.makeText(getApplicationContext(), "Account already exists!!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
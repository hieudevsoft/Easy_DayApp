package com.example.easyday.ACTIVITY.Welcome;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyday.ACTIVITY.HomeScreen;
import com.example.easyday.ENTITY.Account;
import com.example.easyday.R;
import com.example.easyday.CONTROL.TOOL;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

public class SignInActivity extends AppCompatActivity {
    EditText edt_email, edt_password;
    Button bt_forgot_password, bt_signup_login, bt_signin;
    FirebaseAuth mAuth;
    CallbackManager mCallbackManager;
    Boolean wifiConnected = false,dataMobile= false;
    LoginButton loginButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        overridePendingTransition(R.anim.anim_enter_screen, R.anim.anim_exit_screen);
        setContentView(R.layout.welcome_activity);
        mapping();
        showSignUp();
        try {

            if (checkNetWorking()) {
                mAuth = FirebaseAuth.getInstance();
                FacebookSdk.sdkInitialize(getApplicationContext());

                loginWithFacebook();
                bt_signin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn(edt_email.getText().toString().trim(), edt_password.getText().toString().trim());
                    }
                });
                bt_forgot_password.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog;
                        dialog = new Dialog(SignInActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.reset_pass_dialog);
                        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.5);
                        dialog.getWindow().setLayout(width, height);
                        Button buttonReset = dialog.findViewById(R.id.btSendResetPassword);
                        final EditText edt_resetPassEmail = dialog.findViewById(R.id.edt_resetPass);
                        edt_resetPassEmail.setText(edt_email.getText().toString().trim());
                        buttonReset.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (validateEmail(edt_resetPassEmail.getText().toString().trim())) {
                                    mAuth.sendPasswordResetEmail(edt_resetPassEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Please access your email to reset your password", Toast.LENGTH_LONG).show();
                                            } else
                                                Toast.makeText(getApplicationContext(), "Send Failure~", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    edt_resetPassEmail.setError("Email is not validation");
                                    edt_resetPassEmail.requestFocus();
                                }
                            }
                        });
                        dialog.show();
                    }
                });
            } else TOOL.setToast(this, "Please access network");
        }catch (Exception e)
        {
            TOOL.setToast(this, "Access");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void signIn(String email, String password) {
        if(edt_email.getText().toString().trim().isEmpty())
        {
            edt_email.setError("Please fill out");
            edt_email.requestFocus();
        }
        else if(edt_password.getText().toString().isEmpty())
        {
            edt_password.setError("Please enter the password");
            edt_password.requestFocus();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                updateUI(mAuth.getCurrentUser());
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(), "Account or Password not correct!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                edt_email.setError("");
                                edt_password.setError("");
                            }
                        }
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkNetWorking())
        updateUI(mAuth.getCurrentUser());
    }

    private void mapping() {
        edt_email = findViewById(R.id.tv_login_email);
        edt_password = findViewById(R.id.tv_login_password);
        bt_forgot_password = findViewById(R.id.bt_forgotPass);
        bt_signup_login = findViewById(R.id.bt_SignUp_Login);
        bt_signin = findViewById(R.id.bt_SignIn);
        progressBar = findViewById(R.id.progress_loading);
    }

    private void showSignUp()
    {
        bt_signup_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter_screen, R.anim.anim_exit_screen);
            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
    private void updateUI(FirebaseUser user)
    {
        if(user!=null)
        {
            startActivity(new Intent(this, HomeScreen.class));
        }
    }
    private boolean validateEmail(String email)
    {
        String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void loginWithFacebook()
    {
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.getEmail()==null)
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(mAuth.getCurrentUser().getUid()).setValue(new Account(user.getDisplayName(),user.getUid()+"@easyday.com",user.getPhoneNumber()+"","Male",user.getPhotoUrl().toString(),false));
                            else
                            {
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(mAuth.getCurrentUser().getUid()).setValue(new Account(user.getDisplayName(),user.getEmail(),user.getPhoneNumber()+"","Male",user.getPhotoUrl().toString(),false));

                                Log.d("TAG","Vao day");
                            }
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    public boolean checkNetWorking()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            wifiConnected = networkInfo.getType() == TYPE_WIFI;
            dataMobile = networkInfo.getType() == TYPE_MOBILE;
            return wifiConnected || dataMobile;
        }
        return false;
    }
}
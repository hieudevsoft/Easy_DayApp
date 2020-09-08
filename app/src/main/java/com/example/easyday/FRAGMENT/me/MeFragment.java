package com.example.easyday.FRAGMENT.me;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.ENTITY.Account;
import com.example.easyday.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class MeFragment extends Fragment {
    Button bt_Signout,bt_Submit;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageView imgAvatar;
    TextView tvEmailHeader;
    TextView tvNameHeader;
    EditText edt_email, edt_phone,edt_password,edt_name;
    Spinner gender;
    View view;
    String urlPhoto;
    ImageButton imgbt_theme,imgbt_home;
    Toast toast;
    final int PICK_IMAGE_AVATAR = 1;
    private DatabaseReference mDatabase;
    OnClickListener onClickListener;
    public interface OnClickListener{
        void onClickImageButton(int number);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);
        mapping();
        return view;
    }

    @Override
    public void onResume() {


        bt_Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                getActivity().finish();
            }
        });
        updateProfile();
        imgbt_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickImageButton(0);
            }
        });
        imgbt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickImageButton(1);
            }
        });
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_AVATAR && resultCode == RESULT_OK  )
        {
            if(data==null) Toast.makeText(getContext(), "Error!", Toast.LENGTH_LONG).show();
            else
            {
                try {
                    urlPhoto = String.valueOf(data.getData());
                    Glide.with(requireActivity()).load(urlPhoto).centerCrop().into(imgAvatar);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Image not valid!", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), PICK_IMAGE_AVATAR);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        loadSingleDataFromDataBase();
        super.onViewCreated(view, savedInstanceState);

    }

    private void mapping()
    {
        bt_Signout = view.findViewById(R.id.bt_signout);
        tvEmailHeader = view.findViewById(R.id.tv_email_me_header);
        tvNameHeader = view.findViewById(R.id.tv_name_me_header);
        edt_email = view.findViewById(R.id.edt_email_me);
        edt_name = view.findViewById(R.id.edt_name_me);
        edt_password = view.findViewById(R.id.edt_password_me);
        edt_phone = view.findViewById(R.id.edt_phone_number);
        gender = view.findViewById(R.id.spinner_me_gender);
        imgAvatar = view.findViewById(R.id.imgAvatarHeader);
        bt_Submit = view.findViewById(R.id.bt_submit_me);
        imgbt_home = view.findViewById(R.id.imagebt_home_me);
        imgbt_theme = view.findViewById(R.id.imagebt_themes_me);
    }
    private void updateProfileUpToDatabase(String name, String email,String phone,String gender,String urlPhoto,boolean mUpdate)
    {
        Account account = new Account(name,email,phone,gender,urlPhoto,mUpdate);
        mDatabase.child(mUser.getUid()).setValue(account);
    }
    private void loadSingleDataFromDataBase()
    {
        edt_password.setEnabled(false);
        edt_email.setEnabled(false);
        Query checkUser = mDatabase.orderByChild("mEmail").equalTo(mUser.getEmail());
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String mName = snapshot.child(mUser.getUid()).child("mName").getValue(String.class);
                    String mEmail = snapshot.child(mUser.getUid()).child("mEmail").getValue(String.class);
                    String mPhoneNumber = snapshot.child(mUser.getUid()).child("mPhoneNumber").getValue(String.class);
                    if(mPhoneNumber.equals("null")) mPhoneNumber="";
                    String mGender = snapshot.child(mUser.getUid()).child("mGender").getValue(String.class);
                    String mUrlPhoto = snapshot.child(mUser.getUid()).child("mUrlPhoto").getValue(String.class);

                    tvNameHeader.setText(mName);
                    edt_name.setText(mName );
                    tvEmailHeader.setText(mEmail);
                    edt_phone.setText(mPhoneNumber, null);
                    edt_password.setText("Password was protected");
                    edt_email.setText(mEmail );
                    assert mGender != null;
                    switch (mGender)
                    {
                        case "Male":gender.setSelection(0);
                                    break;
                        case "Female":gender.setSelection(1);
                                    break;
                        default: gender.setSelection(2);
                                    break;
                    }
                    if(mUrlPhoto!=null)
                    {
                        if(mUrlPhoto.contains("facebook.com")||mUrlPhoto.contains("https"))
                            Glide.with(getContext()).load(mUrlPhoto).centerCrop().into(imgAvatar);
                        else
                            imgAvatar.setImageBitmap(TOOL.convertStringToBitmap(mUrlPhoto));
                    }

                    else Glide.with(getContext()).load("https://i7.pngguru.com/preview/1016/429/148/computer-icons-download-avatar-avatar.jpg").centerCrop().into(imgAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query checkUserWhithoutEmail = mDatabase.orderByChild("mEmail").equalTo(mUser.getUid()+"@easyday.com");
        checkUserWhithoutEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String mName = snapshot.child(mUser.getUid()).child("mName").getValue(String.class);
                    String mEmail = "Email@easyday.com";
                    String mPhoneNumber = snapshot.child(mUser.getUid()).child("mPhoneNumber").getValue(String.class);
                    if(mPhoneNumber.equals("null")) mPhoneNumber="";
                    String mGender = snapshot.child(mUser.getUid()).child("mGender").getValue(String.class);
                    String mUrlPhoto = snapshot.child(mUser.getUid()).child("mUrlPhoto").getValue(String.class);

                    tvNameHeader.setText(mName);
                    edt_name.setText(mName );
                    tvEmailHeader.setText(mEmail);
                    edt_phone.setText(mPhoneNumber, null);
                    edt_password.setText("Password was protected");
                    edt_email.setText(mEmail);
                    assert mGender != null;
                    switch (mGender)
                    {
                        case "Male":gender.setSelection(0);
                            break;
                        case "Female":gender.setSelection(1);
                            break;
                        default: gender.setSelection(2);
                            break;
                    }
                    if(mUrlPhoto!=null)
                    {
                        if(mUrlPhoto.contains("facebook.com")||mUrlPhoto.contains("https"))
                            Glide.with(getContext()).load(mUrlPhoto).fitCenter().into(imgAvatar);
                        else
                            imgAvatar.setImageBitmap(TOOL.convertStringToBitmap(mUrlPhoto));
                    }
                    else Glide.with(getContext()).load("https://i7.pngguru.com/preview/1016/429/148/computer-icons-download-avatar-avatar.jpg").fitCenter().into(imgAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateProfile()
    {
        bt_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name.getText().toString().trim();
                String email = edt_email.getText().toString().trim();
                String phone = edt_phone.getText().toString().trim();
                tvEmailHeader.setText(email);
                tvNameHeader.setText(name);
                String sex = (String) gender.getSelectedItem();
                urlPhoto = TOOL.convertBitMapToString(TOOL.getBitmapFromImageView(imgAvatar));
                updateProfileUpToDatabase(name, email, phone, sex, urlPhoto,true);
                toast = new Toast(getApplicationContext());
                toast.setView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_register_successful, null));
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Update Successful~", Toast.LENGTH_LONG).show();
                    }
                }, 2500);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity = (Activity) context;
        onClickListener = (OnClickListener) activity;
    }
}

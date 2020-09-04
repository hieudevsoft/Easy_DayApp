package com.example.easyday.ENTITY;

public class Account {
    private String mName;
    private String mEmail;
    private String mPhoneNumber;
    private String mGender;
    private String mUrlPhoto;
    private boolean mUpdate;
    public Account()
    {

    }

    public boolean ismUpdate() {
        return mUpdate;
    }

    public void setmUpdate(boolean mUpdate) {
        this.mUpdate = mUpdate;
    }

    public Account(String mName, String mEmail, String mPhoneNumber, String mGender, String mUrlPhoto, boolean mUpdate) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.mPhoneNumber = mPhoneNumber;
        this.mGender = mGender;
        this.mUrlPhoto = mUrlPhoto;
        this.mUpdate = false;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public String getmUrlPhoto() {
        return mUrlPhoto;
    }

    public void setmUrlPhoto(String mUrlPhoto) {
        this.mUrlPhoto = mUrlPhoto;
    }
}

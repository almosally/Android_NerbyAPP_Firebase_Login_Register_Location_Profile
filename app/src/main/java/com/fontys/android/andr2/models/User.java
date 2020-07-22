package com.fontys.android.andr2.models;

import android.graphics.Bitmap;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {
    private String displayName;
    private String email;
    private String phoneNumber;
    private String userImage;
    private String status;
    private int range;
    private Bitmap picture;
    private UserSetting userSetting;

    public User() {
    }

    public User(String displayName, String email, String phoneNumber, String userImage, String status, int range, UserSetting userSetting) {
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userImage = userImage;
        this.status = status;
        this.range = range;
        this.userSetting = userSetting;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public UserSetting getUserSetting() {
        return userSetting;
    }

    public void setUserSetting(UserSetting userSetting) {
        this.userSetting = userSetting;
    }
}

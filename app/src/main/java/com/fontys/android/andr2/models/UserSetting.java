package com.fontys.android.andr2.models;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserSetting implements Serializable {
    private boolean backGroundService;
    private boolean fingerprint;
    private boolean accountDeleted;

    public UserSetting() {
    }

    public UserSetting(boolean backGroundService, boolean fingerprint, boolean accountDeleted) {
        this.backGroundService = backGroundService;
        this.fingerprint = fingerprint;
        this.accountDeleted = accountDeleted;
    }

    public boolean isBackGroundService() {
        return backGroundService;
    }

    public void setBackGroundService(boolean backGroundService) {
        this.backGroundService = backGroundService;
    }

    public boolean isFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(boolean fingerprint) {
        this.fingerprint = fingerprint;
    }

    public boolean isAccountDeleted() {
        return accountDeleted;
    }

    public void setAccountDeleted(boolean accountDeleted) {
        this.accountDeleted = accountDeleted;
    }
}

package com.fontys.android.andr2.helper;

import android.util.Log;

public class LoginEntryValidate {
    /* validation  */
    public static boolean validate(String verEmail, String verPassword) {
        boolean valid = true;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (verEmail.isEmpty() || !verEmail.matches(emailPattern)) {
            Log.d("LoginEntryValidate","Email entry error");
            // userEmail.setError("enter a valid email address");
            valid = false;
        } else if (verPassword.isEmpty() || verPassword.length() < 4 || verPassword.length() > 10) {
            //   userPassword.setError("Password Incorrect");
            Log.d("LoginEntryValidate","Password entry error");
            valid = false;
        } else {
            //   userPassword.setError(null);
        }
        return valid;
    }
}

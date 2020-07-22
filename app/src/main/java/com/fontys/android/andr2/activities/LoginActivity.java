package com.fontys.android.andr2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fontys.android.andr2.R;
import com.fontys.android.andr2.helper.LoginEntryValidate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    EditText userEmail, userPassword;
    Button userSignIn;
    TextView userSignUp;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.regEmail);
        userPassword = findViewById(R.id.regPassword);
        userSignIn = findViewById(R.id.signUpBtn);
        userSignUp = findViewById(R.id.regSingin);

        userSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginEntryValidate.validate(userEmail.getText().toString(), userPassword.getText().toString()) == false) {
                    Toast.makeText(getBaseContext(), "Can not Login", Toast.LENGTH_LONG).show();
                } else {
                    String email1 = userEmail.getText().toString();
                    String pwd = userPassword.getText().toString();
                    login(email1, pwd);
                }
            }
        });
        userSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // if (firebaseAuth.getCurrentUser() != null) {
        //    finish();
        //   Log.i(TAG, "onStart: currentUser: " + firebaseAuth.getCurrentUser());
        //   startActivity(new Intent(this, ProfileActivity.class));
        // }
    }


    public void login(String email, String pwd1) {
        firebaseAuth.signInWithEmailAndPassword(email, pwd1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}



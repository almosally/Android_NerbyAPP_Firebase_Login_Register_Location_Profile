package com.fontys.android.andr2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fontys.android.andr2.R;
import com.fontys.android.andr2.models.User;
import com.fontys.android.andr2.models.UserLocation;
import com.fontys.android.andr2.models.UserSetting;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Login And Register pages
 * Suggestion: Create two layout (use the login and register layout)
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    // Declare an instance of FirebaseAuth
    // private FirebaseAuth mAuth;
    EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    Button btnSignUp;
    TextView tvSignIn;
    private FirebaseAuth firebaseAuth;
    private UserSetting setting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize the FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.regName);
        editTextPhone = findViewById(R.id.regMobile);
        editTextEmail = findViewById(R.id.regEmail);
        editTextPassword = findViewById(R.id.regPassword);
        btnSignUp = findViewById(R.id.signUpBtn);
        tvSignIn = findViewById(R.id.regSingin);

        //register
        findViewById(R.id.signUpBtn).setOnClickListener(this);
        //login
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            Log.i(TAG, "onStart: currentUser: " + firebaseAuth.getCurrentUser());
            startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
        }
    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String image = "";
        final String status = "";

        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.input_error_name));
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.input_error_email));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.input_error_email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.input_error_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.input_error_password_length));
            editTextPassword.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError(getString(R.string.input_error_phone));
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            editTextPhone.setError(getString(R.string.input_error_phone_invalid));
            editTextPhone.requestFocus();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // boolean backGroundService, boolean fingerprint, boolean accountDeleted
                            UserSetting userSetting = new UserSetting(true, false, false);
                            User user = new User(
                                    name, email, phone, image, "Offline", 10, userSetting
                            );
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                        Toast.makeText(RegisterActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                                        startActivity(intent);

                                    } else {
                                        //display a failure message
                                        Toast.makeText(RegisterActivity.this, "Unable to Register! Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            LatLng latLng = new LatLng(0, 0);
                            UserLocation userLocation = new UserLocation(
                                    latLng.latitude, latLng.longitude, "Offline", System.currentTimeMillis()
                            );
                            FirebaseDatabase.getInstance().getReference("Location")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    } else {
                                        //display a failure message
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:
                registerUser();
                break;
        }
    }
}
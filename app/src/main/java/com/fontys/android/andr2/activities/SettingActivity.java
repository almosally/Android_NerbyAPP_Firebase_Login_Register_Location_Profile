package com.fontys.android.andr2.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fontys.android.andr2.R;
import com.fontys.android.andr2.models.User;
import com.fontys.android.andr2.models.UserLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";
    private DatabaseReference mRootRef, databaseReference, userLastLocation;

    private String UID;
    private User currentUserInfo;
    private UserLocation currentUserLocation;

    private Button deleteAccount;
    private Switch switchBackgroundServices;
    private Switch switchFingerprint;
    private boolean currentSwitchState, currentFingerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Intent intent = getIntent();
        currentUserInfo = (User) intent.getSerializableExtra("currentUserInfo");
        currentUserLocation = (UserLocation) intent.getSerializableExtra("currentUserLocation");

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        databaseReference = mRootRef.child("Users").child(UID);
        userLastLocation = mRootRef.child("Location").child(UID);

        switchBackgroundServices = findViewById(R.id.switchToBackgroundService);
        switchFingerprint = findViewById(R.id.switchFingerprint);
        deleteAccount = findViewById(R.id.bttn_delete_account);

        currentSwitchState = currentUserInfo.getUserSetting().isBackGroundService();
        currentFingerprint = currentUserInfo.getUserSetting().isFingerprint();

        switchBackgroundServices.setChecked(currentSwitchState);
        switchFingerprint.setChecked(currentFingerprint);

        switchBackgroundServices.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchBackgroundServices.isChecked()) {
                    setBackgroundService(true);
                } else {
                    setBackgroundService(false);
                }
            }
        });
        switchFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchFingerprint.isChecked()) {
                    setFingerprint(true);
                } else {
                    setFingerprint(false);
                }
            }
        });


        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirm();
            }
        });
    }

    private void deleteConfirm() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Are you sure you want to leave us?")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        deleteUserAccount();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void restartApp() {
        Intent mStartActivity = new Intent(this, ProfileActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private void setBackgroundService(final boolean serviceStatus) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("The application will restart after confirming a services type change!")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        currentUserInfo.getUserSetting().setBackGroundService(serviceStatus);
                        databaseReference.child("userSetting").child("backGroundService").setValue(serviceStatus);

                        SettingActivity.this.finish();
                        restartApp();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
        switchBackgroundServices.setChecked(currentSwitchState);
    }

    private void setFingerprint(final boolean fingerprintStatus) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You can open the application using your registered fingerprint!")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        currentUserInfo.getUserSetting().setFingerprint(fingerprintStatus);
                        databaseReference.child("userSetting").child("fingerprint").setValue(fingerprintStatus);
                        Log.d(TAG, "onClick: FingerPrintStatus" + fingerprintStatus);

                        if (fingerprintStatus) {
                            Intent intent = new Intent(SettingActivity.this, LockActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

        switchFingerprint.setChecked(currentFingerprint);
    }


    private void deleteUserAccount() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userUID = user.getUid();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(currentUserInfo.getEmail(), password);

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    databaseReference.child("userSetting").child("accountDeleted").setValue(true);
                                                    Log.d(TAG, "User account deleted.");
                                                    deleteDB(userUID);
                                                    signOut();
                                                }
                                            }
                                        });

                            }
                        });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void deleteDB(String UID) {
        Query deleteQuery = databaseReference.orderByValue().equalTo(UID);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        child.getRef().removeValue();
                        Log.d(TAG, "onDataChange: Removed " + child.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: FAILED " + databaseError.getMessage());
                throw databaseError.toException();
            }
        });

        Query deleteQuery2 = userLastLocation.orderByValue().equalTo(UID);
        deleteQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        child.getRef().removeValue();
                        Log.d(TAG, "onDataChange: Removed " + child.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: FAILED " + databaseError.getMessage());
                throw databaseError.toException();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile: {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            case R.id.action_map: {
                Intent intent = new Intent(this, LocationComponent.class);
                intent.putExtra("currentUserInfo", currentUserInfo);
                intent.putExtra("currentUserLocation", currentUserLocation);
                startActivity(intent);
                return true;
            }
            case R.id.action_contact: {
                showCustomDialog();
                return true;
            }
            case R.id.action_setting: {
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("currentUserInfo", currentUserInfo);
                intent.putExtra("currentUserLocation", currentUserLocation);
                startActivity(intent);
                return true;
            }
            case R.id.action_sign_out: {
                signOut();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

    private void signOut() {
        long timeNow = System.currentTimeMillis();
        databaseReference.child("status").setValue("Offline");
        userLastLocation.child("status").setValue("Offline");
        userLastLocation.child("logout").setValue(timeNow);

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showCustomDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

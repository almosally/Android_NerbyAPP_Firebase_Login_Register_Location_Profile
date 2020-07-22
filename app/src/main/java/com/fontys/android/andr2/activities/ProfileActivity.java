package com.fontys.android.andr2.activities;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fontys.android.andr2.R;
import com.fontys.android.andr2.fragment.EditProfileFragment;
import com.fontys.android.andr2.models.User;
import com.fontys.android.andr2.models.UserLocation;
import com.fontys.android.andr2.models.UserSetting;
import com.fontys.android.andr2.services.BackgroundLocationService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class ProfileActivity extends AppCompatActivity {
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final String TAG = "ProfileActivity";
    private boolean isFragmentOpen = false;
    private User currentUserInfo;
    private UserLocation currentUserLocation;
    private TextView userName, userEmail, userPhoneNumber, userLocation;
    private String UID;
    private ImageView userImage, backImage;
    private Button editProfile;
    private Button userLocation_btn;
    private Uri mImageUrix;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mRootRef, databaseReference, userLastLocation;
    private String profileImage, username, email, phoneNumber, lastLocation;
    private StorageReference mStorageRef;
    private boolean mLocationPermissionGranted = false;
    private UserSetting setting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        setting();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhoneNumber = findViewById(R.id.userPhoneNumber);
        userLocation = findViewById(R.id.userLocation);
        userLocation_btn = findViewById(R.id.usr_location_btn);
      //  editProfile = findViewById(R.id.edit_profile_btn);
        backImage = findViewById(R.id.imageView11);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        UID = firebaseUser.getUid();

        Log.d(TAG, "onCreate: Current User UID: " + UID);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        databaseReference = mRootRef.child("Users").child(UID);
        userLastLocation = mRootRef.child("Location").child(UID);

        databaseReference.child("status").setValue("Online");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getUserInfo();
            getUserLocation();

            getLocationPermission();
            bttnLocationListener();
        //    edit_profile_button();
            Picasso.get().load("https://picsum.photos/200?random").fit().error(R.drawable.ic_person_profile).into(backImage);
        } else {
            new Intent(getApplicationContext(), LoginActivity.class);
        }
    }

    private void setting() {
        try {


            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Users")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .child("userSetting").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: dataSnapShot is : " + dataSnapshot.getValue());
                    setting = dataSnapshot.getValue(UserSetting.class);
                    Log.d(TAG, "onDataChange: setting is : " + setting);

                    if (setting.isFingerprint()) {
                        Log.d(TAG, "onDataChange: Fingerprint is ON");
                        startActivity(new Intent(ProfileActivity.this, LockActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception ex) {
            Log.e(TAG, "setting: ", ex);
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

            if (currentUserInfo != null) {
                startLocationService(currentUserInfo.getUserSetting().isBackGroundService());
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void getUserInfo() {
        // StorageReference fileRef = mStorageRef.child("User: " + username).child(UID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int imageId = getResources().getIdentifier("" + R.drawable.ic_person_profile, null, null);
                Log.d(TAG, "onDataChange: User: " + dataSnapshot);
                User info = dataSnapshot.getValue(User.class);
                currentUserInfo = info;

                try {
                    if (!info.getUserImage().isEmpty()) {
                        Picasso.get().load(info.getUserImage()).transform(new CropCircleTransformation()).fit().centerCrop().error(R.drawable.ic_person_profile).into(userImage);
                    } else {
                        userImage.setImageResource(imageId);
                        //  Picasso.get().load(R.drawable.ic_person_profile).transform(new CropCircleTransformation()).fit().centerCrop().into(userImage);
                    }

                    email = info.getEmail();
                    username = info.getDisplayName();
                    phoneNumber = info.getPhoneNumber();
                    userName.setText(username);
                    userEmail.setText(email);
                    userPhoneNumber.setText(phoneNumber);

                    if (currentUserInfo != null) {
                        Log.d(TAG, "onDataChange: Background Service Status is: " + currentUserInfo.getUserSetting().isBackGroundService());
                        startLocationService(currentUserInfo.getUserSetting().isBackGroundService());
                    }
                } catch (Exception ex) {
                    new Intent(getApplicationContext(), LoginActivity.class);
                    Log.d(TAG, "onDataChange: ", ex);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserLocation() {
        userLastLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Location: " + dataSnapshot);
                if (dataSnapshot.getValue() != null) {
                    UserLocation locationInfo = dataSnapshot.getValue(UserLocation.class);
                    lastLocation = "Lat: " + locationInfo.getLatitude() +
                            "\nLng: " + locationInfo.getLongitude();
                    userLocation.setText(lastLocation);

                    currentUserLocation = locationInfo;
                } else {
                    userLocation.setText("N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean showMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        popup.show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_profile: {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                editProfileFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, editProfileFragment).commit();
                isFragmentOpen = true;
                return true;
            }
            case R.id.action_map: {
                Intent intent = new Intent(this, LocationComponent.class);
                intent.putExtra("currentUserInfo", currentUserInfo);
                intent.putExtra("currentUserLocation", currentUserLocation);
                startActivity(intent);
                this.finish();
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
                this.finish();
                return true;
            }
//            case R.id.action_lock: {
//                Intent intent = new Intent(ProfileActivity.this, LockActivity.class);
//                startActivity(intent);
//                return true;
//            }
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

    private void bttnLocationListener() {
        userLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LocationComponent.class);
                intent.putExtra("currentUserInfo", currentUserInfo);
                intent.putExtra("currentUserLocation", currentUserLocation);
                startActivity(intent);
            }
        });
    }
/*
    private void edit_profile_button() {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
                // EditProfileFragment editProfileFragment = new EditProfileFragment();
                //editProfileFragment.setArguments(getIntent().getExtras());
                // getSupportFragmentManager().beginTransaction().add(android.R.id.content, editProfileFragment).commit();
            }
        });
    }
    */

    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            Log.i(TAG, "onStart: currentUser: " + firebaseAuth.getCurrentUser());
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            online();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        online();
    }

    @Override
    protected void onResume() {
        super.onResume();
        online();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        offline();
    }

    private String getExtention(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void getUserImage() {
        final StorageReference fileRef = mStorageRef.child("Images").child(UID);
        //final StorageReference fileRef = mStorageRef.child("Images").child(UID).child(UID + "." + getExtention(mImageUrix));
        //  File localFile = File.createTempFile("images", "jpg");
        fileRef.getFile(mImageUrix)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
    }

    private void startLocationService(boolean start) {
        if (!isLocationServiceRunning()) {
            if (start) {
                Log.d(TAG, "startLocationService: " + "1");
                Intent serviceIntent = new Intent(this, BackgroundLocationService.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                    Log.d(TAG, "startLocationService: " + "2");
                    this.startForegroundService(serviceIntent);
                } else {

                    Log.d(TAG, "startLocationService: " + "3");
                    startService(serviceIntent);
                }
            } else {

                Log.d(TAG, "startLocationService: " + "4");
                Intent serviceIntent = new Intent(this, BackgroundLocationService.class);
                serviceIntent.setAction("STOP");
                stopService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.fontys.android.andr2.services.BackgroundLocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void online() {
        getLocationPermission();
        databaseReference.child("status").setValue("Online");
        userLastLocation.child("status").setValue("Online");
    }

    private void offline() {
        databaseReference.child("status").setValue("Offline");
        userLastLocation.child("status").setValue("Offline");
        long timeNow = System.currentTimeMillis();
        if (timeNow != 0)
            userLastLocation.child("logout").setValue(timeNow);

        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (isFragmentOpen) {
            isFragmentOpen = false;
            Intent firebaseUserIntent = new Intent(this, ProfileActivity.class);
            startActivity(firebaseUserIntent);
        }
    }

    @Override
    protected void onPostResume() {
        if (currentUserInfo != null) {
            startLocationService(currentUserInfo.getUserSetting().isBackGroundService());
        }
        super.onPostResume();
    }
}

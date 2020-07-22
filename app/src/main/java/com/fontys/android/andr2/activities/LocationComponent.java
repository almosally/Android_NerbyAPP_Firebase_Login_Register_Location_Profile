package com.fontys.android.andr2.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.fontys.android.andr2.MyApplication;
import com.fontys.android.andr2.R;
import com.fontys.android.andr2.adapter.CustomSnippetAdapter;
import com.fontys.android.andr2.helper.NotificationHelper;
import com.fontys.android.andr2.models.User;
import com.fontys.android.andr2.models.UserLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.fontys.android.andr2.helper.BitmapCustomMarker.createCustomMarker;
import static com.fontys.android.andr2.models.UserLocation.CalculateUsersDistance;

public class LocationComponent extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private static final String TAG = "LocationComponent";
    private static final int REQUEST_CODE = 101;
    private static int DEFAULT_RANGE = 10;
    private NotificationHelper notificationHelper;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbUserLocation, dbUserInfo;
    private UserLocation currentUserLocation;
    private User currentUserInfo;
    private HashMap<String, User> userInfoHashMap = new HashMap<>();
    private HashMap<String, UserLocation> userLocationHashMap = new HashMap<>();
    private HashMap<String, UserLocation> notifiedUsers = new HashMap<>();
    private LocationManager locationManager;
    private GoogleMap google_Map;
    private Marker currentLocationMarker;
    private Circle circle;
    private LatLng startPoint = new LatLng(0, 0);
    private Long time = null;
    private boolean isBackgroundServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_component);
        Log.d(TAG, "onCreate: Called");

        Intent intent = getIntent();
        currentUserInfo = (User) intent.getSerializableExtra("currentUserInfo");
        currentUserLocation = (UserLocation) intent.getSerializableExtra("currentUserLocation");

        isBackgroundServiceRunning = currentUserInfo.getUserSetting().isBackGroundService();

        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().add(R.id.frameLayout, mapFragment).commit();
        mapFragment.getMapAsync(this);

        Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } else {
            if (vibrator != null) {
                vibrator.vibrate(1000);
            }
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        dbUserInfo = mRootRef.child("Users");
        dbUserLocation = mRootRef.child("Location");

        notificationHelper = new NotificationHelper(this);

        fetchLocation();
        getUsersToHashMap();
    }

    private void fetchLocation() {
        Log.d(TAG, "fetchLocation: Called");
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called");
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }

    private void getUsersToHashMap() {
        Log.d(TAG, "getUsersToHashMap: Called");
        dbUserInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User userInformation = data.getValue(User.class);
                    userInfoHashMap.put(data.getKey(), userInformation);
                    if (dataSnapshot.getKey().equals(firebaseUser.getUid())) {
                        if (currentUserInfo == null) {
                            currentUserInfo = userInformation;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Called");
        google_Map = googleMap;
        userLocationsListener();
        if (currentUserLocation != null)
            addCurrentUserMarker(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: Called");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        startPoint = latLng;
        time = location.getTime();

        if (!isBackgroundServiceRunning) {
            this.currentUserLocation = new UserLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    "Online",
                    time);
            dbUserLocation.child(firebaseUser.getUid()).setValue(currentUserLocation);
            addCurrentUserMarker(latLng);
        }
    }

    private void addCurrentUserMarker(LatLng latLng) {
        Log.d(TAG, "addCurrentUserMarker: Called");
        getUsersToHashMap();
        if (google_Map != null) {
            if (currentLocationMarker == null && circle == null) {
                try {
                    if (currentUserInfo != null && currentUserInfo.getRange() > 0) {
                        DEFAULT_RANGE = currentUserInfo.getRange();
                    }
                    MarkerOptions currentUserMarkerOptions = new MarkerOptions();
                    currentUserMarkerOptions.position(latLng);
                    currentUserMarkerOptions.title("I am Here");
                    if (currentUserInfo != null)
                        currentUserMarkerOptions.snippet(currentUserInfo.getEmail());
                    currentUserMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(LocationComponent.this, R.drawable.default_image, "Online")));

                    drawCircle(latLng);
                    currentLocationMarker = google_Map.addMarker(currentUserMarkerOptions);
                    pointToPosition(latLng);
                } catch (Exception ex) {
                    Log.e(TAG, "addCurrentUserMarker: ", ex);
                }

            } else {
                updateCircle(latLng);
                currentLocationMarker.setPosition(latLng);
                currentUserLocation.setMarker(currentLocationMarker);
            }
        }
    }

    private void drawCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(DEFAULT_RANGE)
                .fillColor(Color.parseColor("#500084d3"))
                .strokeColor(Color.parseColor("#500084d3"))
                .strokeWidth(4);
        circle = google_Map.addCircle(circleOptions);
    }

    private void updateCircle(LatLng latLng) {
        circle.setCenter(latLng);
    }

    private void pointToPosition(LatLng position) {
        Log.d(TAG, "pointToPosition: Called");
        //Build camera position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(18).build();
        //Zoom in and animate the camera.
        google_Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void dataChanged(HashMap<String, UserLocation> stringUserLocationHashMap) {
        Log.d(TAG, "dataChanged: Called");
        for (Map.Entry<String, UserLocation> entry : stringUserLocationHashMap.entrySet()) {
            String uid = entry.getKey();
            UserLocation value = entry.getValue();
            if (value.getStatus().equals("Online")) {
                ShowNotificationToUser(entry);
            }
        }
    }

    private void userLocationsListener() {
        Log.d(TAG, "userLocationsListener: Called");
        dbUserLocation.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey());
                if (firebaseUser != null) {
                    UserLocation uLocation = dataSnapshot.getValue(UserLocation.class);
                    if (!Objects.equals(dataSnapshot.getKey(), firebaseUser.getUid())) {
                        if (uLocation != null)
                            updateUsersLocation("Added", dataSnapshot, uLocation);
                    } else {
                        currentUserLocation = uLocation;
                        addCurrentUserMarker(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()));
                    }
                    dataChanged(userLocationHashMap);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: " + dataSnapshot.getKey());
                if (firebaseUser != null) {
                    UserLocation uLocation = dataSnapshot.getValue(UserLocation.class);
                    if (!Objects.equals(dataSnapshot.getKey(), firebaseUser.getUid())) {
                        if (uLocation != null) {
                            updateUsersLocation("Changed", dataSnapshot, uLocation);
                        }
                    } else {
                        currentUserLocation = uLocation;
                    }
                    dataChanged(userLocationHashMap);
                    addCurrentUserMarker(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: " + dataSnapshot.getKey());
                if (firebaseUser != null) {
                    UserLocation uLocation = dataSnapshot.getValue(UserLocation.class);
                    if (!Objects.equals(dataSnapshot.getKey(), firebaseUser.getUid())) {
                        if (uLocation != null) {
                            updateUsersLocation("Removed", dataSnapshot, uLocation);
                        }
                    }
                    dataChanged(userLocationHashMap);
                    addCurrentUserMarker(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved: " + s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUsersLocation(String toDo, @NonNull DataSnapshot dataSnapshot, UserLocation uLocation) {
        try {
            LatLng latLng = new LatLng(uLocation.getLatitude(), uLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(Objects.requireNonNull(userInfoHashMap.get(dataSnapshot.getKey())).getDisplayName());
            markerOptions.snippet(Objects.requireNonNull(userInfoHashMap.get(dataSnapshot.getKey())).getEmail());

            markerOptions.icon(BitmapDescriptorFactory
                    .fromBitmap(createCustomMarker(LocationComponent.this, R.drawable.default_image, uLocation.getStatus())));

            google_Map.setInfoWindowAdapter(new CustomSnippetAdapter(this, dataSnapshot.getKey()));
            google_Map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.setTag(userInfoHashMap);
                    return false;
                }
            });

            //-------------------
            if (toDo.equals("Added")) {
                userLocationHashMap.put(dataSnapshot.getKey(), uLocation);
                Marker marker = google_Map.addMarker(markerOptions);
                uLocation.setMarker(marker);
            }
            //-------------------
            else if (toDo.equals("Changed")) {
                UserLocation uList = userLocationHashMap.get(dataSnapshot.getKey());

                assert uList != null;
                Marker marker = uList.getMarker();
                marker.remove();

                uList.setLatitude(uLocation.getLatitude());
                uList.setLongitude(uLocation.getLongitude());
                uList.setMarker(google_Map.addMarker(markerOptions));
            }
            //-------------------
            else {
                UserLocation uList = userLocationHashMap.get(dataSnapshot.getKey());
                assert uList != null;
                Marker marker = uList.getMarker();
                marker.remove();
                userLocationHashMap.remove(dataSnapshot.getValue());
            }
        } catch (Exception ex) {
            Log.e(TAG, "updateUsersLocation: ", ex);
        }
    }

    public void ShowNotificationToUser(Map.Entry<String, UserLocation> entry) {
        Log.d(TAG, "ShowNotificationToUser: Called");
        LatLng latLngNoti = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());

        if (CalculateUsersDistance(startPoint, latLngNoti) <= DEFAULT_RANGE && CalculateUsersDistance(startPoint, latLngNoti) > 0) {
            if (!notifiedUsers.containsKey(entry.getKey())) {
                notifiedUsers.put(entry.getKey(), entry.getValue());
                Log.d(TAG, "ShowNotificationToUser: notifiedUsers: " + entry.getKey() + " ADDED!");
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(500);
                }
                notificationHelper.sendHighPriorityNotification(
                        Objects.requireNonNull(userInfoHashMap.get(entry.getKey())).getDisplayName() + " near by " + DEFAULT_RANGE + " meters " +
                                "..",
                        this);
            }
        } else {
            notifiedUsers.remove(entry.getKey(), entry.getValue());
            //Log.d(TAG, "ShowNotificationToUser: notifiedUsers: " + entry.getKey() + " REMOVED!");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: " + provider);
        fetchLocation();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: Called");
        super.onStart();
        online();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: Called");
        super.onResume();
        MyApplication.activityResumed();
        online();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Called");
        super.onDestroy();
        offline();

        MyApplication.activityDestroyed();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: Called");
        super.onBackPressed();
    }

    private void online() {
        notifiedUsers.clear();
        dbUserInfo.child(firebaseUser.getUid()).child("status").setValue("Online");
        dbUserLocation.child(firebaseUser.getUid()).child("status").setValue("Online");
    }

    private void offline() {
        dbUserInfo.child(firebaseUser.getUid()).child("status").setValue("Offline");
        dbUserLocation.child(firebaseUser.getUid()).child("status").setValue("Offline");
        locationManager.removeUpdates(this);
        long timeNow = System.currentTimeMillis();
        if (time != null)
            dbUserLocation.child(firebaseUser.getUid()).child("logout").setValue(timeNow);

        LocationComponent.this.finish();
    }
}

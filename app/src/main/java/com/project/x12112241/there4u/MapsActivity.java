package com.project.x12112241.there4u;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker, mfriendsMarker, mfriendsMarker2;

    public static final int REQUEST_LOCATION_CODE = 99;
    private String mCurrent_user_id;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mLocationDatabase, mFriendsDatabase, mUsersDatabase, mRootRef, mHFriendDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    Marker marker;
    Double Latitude, Longitude;
    Double location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPerssion();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client != null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mAuth = FirebaseAuth.getInstance();


        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");








        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    @Override
    public void onLocationChanged(final Location location) {
        lastLocation = location;
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrentUser.getUid();

        mLocationDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        if (current_uid == null) {
            Intent loginIntent = new Intent(MapsActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();


        } else {

            mLocationDatabase.child(current_uid).child("online").setValue(true);

        }

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        if (mfriendsMarker != null) {
            mfriendsMarker.remove();
        }


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        //markerOptions.title(mLocationDatabase.child(current_uid).child("name").getKey().toString());

        mLocationDatabase.child(current_uid).child("position").setValue(latLng).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


            }
        });

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 11)));


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    MarkerOptions markerOptions = new MarkerOptions();

                    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String current_uid = mCurrentUser.getUid();

                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();
                    markerOptions.title(dataSnapshot.child("name").getValue().toString());

                    LatLng LatLng2 = new LatLng(Double.parseDouble(dataSnapshot.child("position").child("latitude").getValue().toString()), Double.parseDouble(dataSnapshot.child("position").child("longitude").getValue().toString()));

                    mfriendsMarker = mMap.addMarker(new MarkerOptions().position(LatLng2).title(dataSnapshot.child("name").getValue().toString()));
                    PicassoMarker marker2 = new PicassoMarker(mfriendsMarker);
                    Picasso.with(MapsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).resize(300, 300).transform(new CircleTransform()).into(marker2);

                    PicassoMarker marker = new PicassoMarker(currentLocationMarker);
                    //Picasso.with(MapsActivity.this).load(image).resize(300, 300).transform(new CircleTransform()).into(marker);
                    Picasso.with(MapsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).resize(300, 300).transform(new CircleTransform()).into(marker);


                    // Picasso.with(SettingsActivity.this).load(image).into(mDisplayImage);
                } else {

                    MarkerOptions markerOptions = new MarkerOptions();


                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ////-----------hardcode friend-----------/////////

        mHFriendDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mHFriendDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    MarkerOptions markerOptions = new MarkerOptions();

                    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String current_uid = mCurrentUser.getUid();

                    String name = dataSnapshot.child("RpLflfW010fsaW1V2rdMSfYxpQ03").child("name").getValue().toString();
                    String image = dataSnapshot.child("RpLflfW010fsaW1V2rdMSfYxpQ03").child("thumb_image").getValue().toString();
                    markerOptions.title(dataSnapshot.child("RpLflfW010fsaW1V2rdMSfYxpQ03").child("name").getValue().toString());

                    LatLng LatLng2 = new LatLng(Double.parseDouble(dataSnapshot.child("RpLflfW010fsaW1V2rdMSfYxpQ03").child("position").child("latitude").getValue().toString()), Double.parseDouble(dataSnapshot.child("RpLflfW010fsaW1V2rdMSfYxpQ03").child("position").child("longitude").getValue().toString()));

                    mfriendsMarker = mMap.addMarker(new MarkerOptions().position(LatLng2).title(dataSnapshot.child("RpLflfW010fsaW1V2rdMSfYxpQ03").child("name").getValue().toString()));
                    PicassoMarker marker2 = new PicassoMarker(mfriendsMarker);
                    Picasso.with(MapsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).resize(300, 300).transform(new CircleTransform()).into(marker2);

                    PicassoMarker marker = new PicassoMarker(currentLocationMarker);
                    //Picasso.with(MapsActivity.this).load(image).resize(300, 300).transform(new CircleTransform()).into(marker);
                    Picasso.with(MapsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).resize(300, 300).transform(new CircleTransform()).into(marker);

                    MarkerOptions markerOptions2 = new MarkerOptions();

                    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String current_uid2 = mCurrentUser.getUid();


                    markerOptions2.title(dataSnapshot.child("c4WO0XZFivayha4uJQvhmtwIhQ22").child("name").getValue().toString());
                    String name2 = dataSnapshot.child("c4WO0XZFivayha4uJQvhmtwIhQ22").child("name").getValue().toString();
                    String image2 = dataSnapshot.child("c4WO0XZFivayha4uJQvhmtwIhQ22").child("thumb_image").getValue().toString();

                    LatLng LatLng3 = new LatLng(Double.parseDouble(dataSnapshot.child("c4WO0XZFivayha4uJQvhmtwIhQ22").child("position").child("latitude").getValue().toString()), Double.parseDouble(dataSnapshot.child("c4WO0XZFivayha4uJQvhmtwIhQ22").child("position").child("longitude").getValue().toString()));

                    mfriendsMarker2 = mMap.addMarker(new MarkerOptions().position(LatLng3).title(dataSnapshot.child("c4WO0XZFivayha4uJQvhmtwIhQ22").child("name").getValue().toString()));
                    PicassoMarker marker3 = new PicassoMarker(mfriendsMarker2);
                    Picasso.with(MapsActivity.this).load(image2).networkPolicy(NetworkPolicy.OFFLINE).resize(300, 300).transform(new CircleTransform()).into(marker3);


                    // Picasso.with(SettingsActivity.this).load(image).into(mDisplayImage);
                } else {

                    MarkerOptions markerOptions = new MarkerOptions();


                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ////-----------hardcode f-----------/////////
        ////-----------Show friend-----------/////////
//        mRootRef = FirebaseDatabase.getInstance().getReference();
//        mCurrent_user_id = mAuth.getCurrentUser().getUid();
//
//        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
//        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
//
//        mRootRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//
//                    Friends friend = dataSnapshot.getValue(Friends.class);
//                    String Friendinfo = dataSnapshot.child("Friends").child(mCurrent_user_id).child(friend).getValue().toString();
//
//                    MarkerOptions markerOptions = new MarkerOptions();
//
//                    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
//                    String current_uid = mCurrentUser.getUid();
//
//                    String friendname = dataSnapshot.child(Friendinfo).child("name").getValue().toString();
//                    String image = dataSnapshot.child(Friendinfo).child("thumb_image").getValue().toString();
//                    markerOptions.title(dataSnapshot.child(Friendinfo).child("name").getValue().toString());
//
//                    LatLng FriendLatLng = new LatLng(Double.parseDouble(dataSnapshot.child(Friendinfo).child("position").child("latitude").getValue().toString()), Double.parseDouble(dataSnapshot.child(Friendinfo).child("position").child("longitude").getValue().toString()));
//
//                    mfriendsMarker = mMap.addMarker(new MarkerOptions().position(FriendLatLng).title(dataSnapshot.child(Friendinfo).child("name").getValue().toString()));
//                    PicassoMarker friendMarker = new PicassoMarker(mfriendsMarker);
//                    Picasso.with(MapsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).resize(300, 300).transform(new CircleTransform()).into(friendMarker);
//
//
//                } else {
//
//                    MarkerOptions markerOptions = new MarkerOptions();
//
//
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//

        /////----------Friend ----------------------//////////


        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    public boolean checkLocationPerssion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
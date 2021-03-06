package com.project.x12112241.there4u;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.storage.StorageReference;

import mehdi.sakout.fancybuttons.FancyButton;

public class HomeActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private TextView mNameView;
    private FancyButton MapView, ShareBtn, ConnectBtn, userInfo, button;

    private DynamicLink dynamicLink;
    private TextView txtResult;
    private FirebaseAnalytics analytics;
    private final String TAG = getClass().getName();
    private static final int REQUEST_INVITE = 0;
    public boolean toasted = false;

    private String userID;


    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference mUsersDatabase;
    private String mCurrentUserId;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();


        } else {

            mUsersDatabase.child(mCurrentUserId).child("online").setValue(true);

        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");


//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    toastMessage("Successfully signed in with: " + user.getEmail());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                    toastMessage("Successfully signed out.");
//                }
//                // ...
//            }
//        };

//        txtResult = (TextView) findViewById(R.id.txtDynamicLinkResult);
//        FirebaseDynamicLinks.getInstance()
//                .getDynamicLink(getIntent())
//                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
//                    @Override
//                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                        if (pendingDynamicLinkData != null) {
//                            analytics = FirebaseAnalytics.getInstance(HomeActivity.this);
//
//                            Uri deeplink = pendingDynamicLinkData.getLink();
//                            txtResult.append("\nonSuccess called " + deeplink.toString());
//
//                            FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
//                            if (invite != null) {
//                                String invitationId = invite.getInvitationId();
//                                if (!TextUtils.isEmpty(invitationId))
//                                    txtResult.append("\ninvitation Id " + invitationId);
//                            }
//
//                        }
//                    }
//
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        txtResult.append("\nonFailure");
//                    }
//                });


//        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("name");
//        mNameView = (TextView) findViewById(R.id.name_view);


        MapView = (FancyButton) findViewById(R.id.map_button);
        MapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent maps = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(maps);
            }
        });

        ShareBtn = (FancyButton) findViewById(R.id.share_btn);
        ShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(HomeActivity.this, AppInviteActivity.class);
                startActivity(back);
            }
        });

        userInfo = (FancyButton) findViewById(R.id.user_Info);
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(HomeActivity.this, UserInfoActivity.class);
                startActivity(back);
            }
        });

        button = (FancyButton) findViewById(R.id.logout_Btn);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    //toastMessage("Successfully signed in with: " + user.getEmail());
                }
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }

            }
        };


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });


        ConnectBtn = (FancyButton) findViewById(R.id.connect_btn);
        //ConnectBtn.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //  public void onClick(View v) {
        //      shareDynamicLink(v);


        //  }
        //  });
        ConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ConnectionsActivity.class));
//                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
//                        .setMessage(getString(R.string.invitation_message))
//                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
//                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
//                        .setCallToActionText(getString(R.string.invitation_cta))
//                        .build();
//                startActivityForResult(intent, REQUEST_INVITE);
            }
        });
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                showData(dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
//
//    }

//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot == null) {
//                    //  mNameView.setText("Name :" + " ");
//                    Toast.makeText(HomeActivity.this, "Name is empty", Toast.LENGTH_LONG).show();
//                }
//                UserInformation uInfo = new UserInformation();
//                //   String name = dataSnapshot.getValue().toString();
//                // if (name != null) {
//                // mNameView.setText("Name : " + name);
//                // }
//
//
//            }
//
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    }

//    private void showData(DataSnapshot dataSnapshot) {
//        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//            UserInformation uInfo = new UserInformation();
//            uInfo.setName(ds.child(userID).getValue(UserInformation.class).getName()); //set the name
//            String name = ds.getValue().toString();
//            //display all the information
//            Log.d(TAG, "showData: name: " + uInfo.getName());
//            mNameView.setText("Name :" + name);
//            ArrayList<String> array = new ArrayList<>();
//            array.add(uInfo.getName());
//
//
//        }
//    }


    public void shareDynamicLink(View view) {

        Intent intent = new Intent();
        String msg = "get my app : " + buildDynamicLink();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.setType("text/plain");
        txtResult.setText(getString(R.string.invitation_deep_link));
        startActivity(intent);
    }


    private String buildDynamicLink()//{String link, String description, String titleSocial, String source)
    {

        // String path = FirebaseDynamicLinks.getInstance().createDynamicLink()
        //         .setLink(Uri.parse("https://github.com/x12112241/There4U"))
        //        .setDynamicLinkDomain("quw5p.app.goo.gl/Scq6")
        //        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
        //        .buildDynamicLink().getUri().toString();

        return "https://quw5p.app.goo.gl/Scq6 " + " " +  /*link*/ " https://github.com/x12112241/There4U " + "apn = " + "com.project.x12112241.there4u" + "&st " + "Share+This+App" + "&sd= " + "The+caring+app. " + "&utm_source= " + "AndroidApp ";
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

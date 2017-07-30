package com.project.x12112241.there4u;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private TextView mNameView;
    private Button MapView;
    private Button Backbtn;
    private Button InviteBtn;
    private Button userInfo;
    private DynamicLink dynamicLink;
    private TextView txtResult;
    private FirebaseAnalytics analytics;
    private final String TAG = getClass().getName();
    private static final int REQUEST_INVITE = 0;
    private Button button;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        txtResult = (TextView) findViewById(R.id.txtDynamicLinkResult);
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if (pendingDynamicLinkData != null) {
                            analytics = FirebaseAnalytics.getInstance(HomeActivity.this);

                            Uri deeplink = pendingDynamicLinkData.getLink();
                            txtResult.append("\nonSuccess called " + deeplink.toString());

                            FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
                            if (invite != null) {
                                String invitationId = invite.getInvitationId();
                                if (!TextUtils.isEmpty(invitationId))
                                    txtResult.append("\ninvitation Id " + invitationId);
                            }

                        }
                    }

                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        txtResult.append("\nonFailure");
                    }
                });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Name");
        mNameView = (TextView) findViewById(R.id.name_view);


        MapView = (Button) findViewById(R.id.map_button);
        MapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent maps = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(maps);
            }
        });

        Backbtn = (Button) findViewById(R.id.return_btn);
        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(HomeActivity.this, AppInviteActivity.class);
                startActivity(back);
            }
        });

        userInfo = (Button) findViewById(R.id.user_Info);
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(HomeActivity.this, UserInfoActivity.class);
                startActivity(back);
            }
        });

        button = (Button) findViewById(R.id.logout_Btn);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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


        InviteBtn = (Button) findViewById(R.id.invite_button);
        //InviteBtn.setOnClickListener(new View.OnClickListener() {
          //  @Override
          //  public void onClick(View v) {
          //      shareDynamicLink(v);



          //  }
      //  });
        InviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
            }
      });



        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.getValue().toString();

                // mNameView.setText("Name : " + name);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

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


}
package com.project.x12112241.there4u;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import mehdi.sakout.fancybuttons.FancyButton;

public class UserInfoActivity extends AppCompatActivity {


    private static final String TAG = "AddToDatabase";

    private FancyButton btnSubmit, mProfileBtn;
    private EditText mName, mEmail, mPhone, mCompany, mStatus;
    private String mImage = " ";
    private String mThumb_Image;
    private String userID;
    private ImageView mImageView, iMg, bkrnd_Image;

    private final static int RC_SIGN_IN = 1;

    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    private StorageReference mStorageRef;

    //add Firebase Database stuff
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        mProgressDialog = new ProgressDialog(this);
        btnSubmit = (FancyButton) findViewById(R.id.btnSubmit);
        mProfileBtn = (FancyButton) findViewById(R.id.profile_button);


        mName = (EditText) findViewById(R.id.etName);
        mEmail = (EditText) findViewById(R.id.etEmail);
        mPhone = (EditText) findViewById(R.id.etPhone);
        mCompany = (EditText) findViewById(R.id.etCompany);
        mStatus = (EditText) findViewById(R.id.etStatus);


        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUserDatabase = mDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef = mStorageRef.child("images");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    mName.setText(name);
                    if (dataSnapshot.child("status").exists()) {
                        String status = dataSnapshot.child("status").getValue().toString();
                    mStatus.setText(status);
                    } else {
                        mStatus.setText("");
                    }
                    String email = dataSnapshot.child("email").getValue().toString();
                    mEmail.setText(email);
                    if (dataSnapshot.child("phone").exists()) {
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        mPhone.setText(phone);
                    } else {
                        mPhone.setText("");
                    }
                    if (dataSnapshot.child("company").exists()) {
                        String company = dataSnapshot.child("company").getValue().toString();
                        mCompany.setText(company);
                    } else {
                        mCompany.setText("");
                    }


                } else {

                    mName.setText("");
                    mStatus.setText("");
                    mEmail.setText("");
                    mPhone.setText("");
                    mCompany.setText("");

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        //Picasso.with(UserInfoActivity.this).load(image).into(mDisplayImage)


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                }
            }
        };
// Read from the database
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Submit pressed.");
                String name = mName.getText().toString();
                String email = mEmail.getText().toString();
                String phone = mPhone.getText().toString();
                String company = mCompany.getText().toString();
//                String image = mImage;
//                String thumb_image = mThumb_Image;
                String status = mStatus.getText().toString();


                mUserDatabase.child("name").setValue(name);
                mUserDatabase.child("email").setValue(email);
                mUserDatabase.child("phone").setValue(phone);
                mUserDatabase.child("company").setValue(company);
                mUserDatabase.child("status").setValue(status);

                toastMessage("Profile updated");

//                HashMap<String, String> userMap = new HashMap<>();
//                userMap.put("name", name);
//                userMap.put("status", status);
//                userMap.put("phone", phone);
//                userMap.put("email", email);
//                userMap.put("company", company);
//                userMap.put("image", "https://cdn.discordapp.com/attachments/293759137123270656/342988441706823681/there4ulogo.png");
//                userMap.put("thumb_image", "https://cdn.discordapp.com/attachments/293759137123270656/342988441706823681/there4ulogo.png");


                //handle the exception if the EditText fields are null
//                if (!name.equals("") && !email.equals("") && !phone.equals("") && !company.equals("") && !status.equals("")) {
//                    UserInformation userInformation = new UserInformation(name, email, phone, company, status);
//                    mUserDatabase.setValue(userInformation);
//                    toastMessage("New Information has been saved.");
//                    mName.setText("");
//                    mEmail.setText("");
//                    mPhone.setText("");
//                    mCompany.setText("");
//                    mStatus.setText("");

                //mUserDatabase.setValue(userMap);


                // }


//                if (name == "") {
//                    toastMessage("please input Name");
//                }
//                if (email.equals("")) {
//                    toastMessage("please input Email");
//                }
//                if (phone.equals("")) {
//                    toastMessage("please input Phone Number");
//                }
//                if (company.equals("")) {
//                    toastMessage("please input Company");if (status.equals("")) {
//                        toastMessage("please input Status");
//
//                    }
//                }
//                if (TextUtils.isEmpty(name)){toastMessage("Cmon Bruh");
//                }
//                if (!name.equals("") && !email.equals("") && !phone.equals("") && !company.equals("") && !status.equals("")) {
//
//                    toastMessage("Profile updated");
//                }

            }
        });
        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(UserInfoActivity.this, SettingsActivity.class);
                startActivity(profile);
            }
        });

        //  mUploadImage = (FancyButton) findViewById(R.id.image_upload);
        mImageView = (ImageView) findViewById(R.id.imageView);
        //  iMg = (ImageView) findViewById(R.id.iMage_View);
        bkrnd_Image = (ImageView) findViewById(R.id.image_backround);

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


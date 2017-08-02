package com.project.x12112241.there4u;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
    private EditText mName, mEmail, mPhoneNum, mCompany, mStatus;
    private String mImage = " ";
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
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mProgressDialog = new ProgressDialog(this);
        btnSubmit = (FancyButton) findViewById(R.id.btnSubmit);
        mProfileBtn = (FancyButton) findViewById(R.id.profile_button);
        mName = (EditText) findViewById(R.id.etName);
        mEmail = (EditText) findViewById(R.id.etEmail);
        mPhoneNum = (EditText) findViewById(R.id.etPhone);
        mCompany = (EditText) findViewById(R.id.etCompany);
        mStatus = (EditText) findViewById(R.id.etStatus);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef = mStorageRef.child("images");


        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
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
        myRef.addValueEventListener(new ValueEventListener() {
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
                String phoneNum = mPhoneNum.getText().toString();
                String company = mCompany.getText().toString();
                String image_url = mImage;
                String status = mStatus.getText().toString();

                myRef.child("users").child(userID);

                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("name", name);
                userMap.put("status", status);
                userMap.put("phone", phoneNum);
                userMap.put("email", email);
                userMap.put("company", company);
                userMap.put("image", "default.jpg");
                userMap.put("thumb_image", "default");


//                Log.d(TAG, "onClick: Attempting to submit to database: \n" +
//                        "name: " + name + "\n" +
//                        "email: " + email + "\n" +
//                        "phone number: " + phoneNum + "\n" +
//                        "Company " + company + "\n" +
//                        "Image Url " + image_url + "\n"
//                );

                //handle the exception if the EditText fields are null
                if (!name.equals("") && !email.equals("") && !phoneNum.equals("") && !company.equals("") && !status.equals("")) {
                    UserInformation userInformation = new UserInformation(name, email, phoneNum, company, status);
                    myRef.child("users").child(userID).setValue(userInformation);
                    toastMessage("New Information has been saved.");
                    mName.setText("");
                    mEmail.setText("");
                    mPhoneNum.setText("");
                    mCompany.setText("");
                    mStatus.setText("");

                    myRef.child("users").child(userID).setValue(userMap);


                } else {
                    toastMessage("Fill out all the fields");
                }
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
//        mUploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent image = new Intent(Intent.ACTION_PICK);
//                image.setType("image/*");
//                startActivityForResult(image, GALLERY_INTENT);
//
//                // startActivityForResult(image, GALLERY_INTENT);
//
//
//            }
//        });
//        mStorageRef.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot == null) {
//
//                    Toast.makeText(UserInfoActivity.this, "Profile pic set.", Toast.LENGTH_LONG).show();
//                }
//                UserInformation uInfo = new UserInformation();
        //   String name = dataSnapshot.getValue().toString();
        // if (name != null) {
        // mNameView.setText("Name : " + name);
        // }


//            }
//
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        StorageReference imagepath = storageReference.child("Images");
//        imagepath.putFile(selectedImage).addOnSuccessListener(new
//                                                                      OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                                                          @Override
//                                                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//
//                                                                              Uri getimage = taskSnapshot.getDownloadUrl();
//
//                                                                              Glide.with(QuizActivity.this)
//                                                                                      .using(new FirebaseImageLoader())
//                                                                                      .load(getimage)
//                                                                                      .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                                                                                      .into(mImageQuestion);
//                                                                          }
//                                                                      });

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Uri uri = null;
//        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
//            mProgressDialog.setMessage("Uploading....");
//            mProgressDialog.show();
//            uri = data.getData();
//            //Picasso.with(UserInfoActivity.this).load(uri).fit().centerCrop().into(bkrnd_Image);
//            Picasso.with(UserInfoActivity.this).load(uri).fit().centerCrop().into(iMg);
//
//            StorageReference filePath = mStorageRef.child("images").child("users").child(userID).child(uri.getLastPathSegment());
//
//            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                    Toast.makeText(UserInfoActivity.this, "Upload Done", Toast.LENGTH_LONG).show();
//                    mProgressDialog.dismiss();
//
//                    Uri downloadUri = taskSnapshot.getDownloadUrl();
//
//                    System.out.println("Here is the print of url : " + downloadUri);
//
//                    Picasso.with(UserInfoActivity.this).load(downloadUri)
//                            .error(R.drawable.t4ulogo).resize(50, 50)
//                            .placeholder(R.drawable.t4ulogo).resize(120, 120).centerCrop().into(iMg);
//
//
//                    Picasso.with(UserInfoActivity.this).load(downloadUri).fit().centerCrop().into(mImageView);
//
//                }
//            });
//            FirebaseUser user = mAuth.getCurrentUser();
//            String current_uid = user.getUid();
//            myRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
//            myRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                    String image = dataSnapshot.child("image").getValue().toString();
//
//
//                    Picasso.with(UserInfoActivity.this).load(image).into(bkrnd_Image);
//
//                    Toast.makeText(UserInfoActivity.this, "Image should be there", Toast.LENGTH_LONG).show();
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }
//    }

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


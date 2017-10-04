package com.project.x12112241.there4u;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import mehdi.sakout.fancybuttons.FancyButton;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android Layout

    private CircleImageView mDisplayImage;
    private TextView mName, mStatus, mEmail, mPhone, mCompany;

    private FancyButton mImageBtn, mUpdateInfoBtn;

    private static final int GALLERY_PICK = 1;

    //Storage Firebase

    private StorageReference mStorageRef;

    private ProgressDialog mProgessDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_Displayname);
        mEmail = (TextView) findViewById(R.id.settings_Email);
        mPhone = (TextView) findViewById(R.id.settings_Phone);
        mCompany = (TextView) findViewById(R.id.settings_Company);
        mStatus = (TextView) findViewById(R.id.settings_status);
        mImageBtn = (FancyButton) findViewById(R.id.change_Img_btn);
        mUpdateInfoBtn = (FancyButton) findViewById(R.id.status_Btn);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        mUserDatabase.keepSynced(true);

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


                    if (dataSnapshot.child("thumb_image").exists()) {
                        String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                        Picasso.with(SettingsActivity.this).load(thumb_image).into(mDisplayImage);
                        final String image = dataSnapshot.child("image").getValue().toString();


                        Picasso.with(SettingsActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).into(mDisplayImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(SettingsActivity.this).load(image).into(mDisplayImage);

                            }
                        });

                    } else {
                        Picasso.with(SettingsActivity.this).load("https://cdn.discordapp.com/attachments/293759137123270656/342988441706823681/there4ulogo.png").into(mDisplayImage);
                    }





                    mName.setText(name);
                    mEmail.setText(email);


                } else if (dataSnapshot.child("thumb_image").exists()) {
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                    Picasso.with(SettingsActivity.this).load(thumb_image).into(mDisplayImage);
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

        mUpdateInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(home);
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {

                mProgessDialog = new ProgressDialog(SettingsActivity.this);
                mProgessDialog.setTitle("Uploading Image....");
                mProgessDialog.setMessage("Image will process now.");
                mProgessDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_uid = mCurrentUser.getUid();
                Bitmap thumb_file = null;


                try {
                    thumb_file = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_file.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();



                StorageReference filepath = mStorageRef.child("images").child("users").child(current_uid).child("profile_image.jpg");
                final StorageReference thumb_filepath = mStorageRef.child("images").child("users").child(current_uid).child("profile_image_thumb.jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {


                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadURL = thumb_task.getResult().getDownloadUrl().toString();
                                    if (thumb_task.isSuccessful()) {
                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("image", download_url);
                                        update_hashMap.put("thumb_image", thumb_downloadURL);
                                        mProgessDialog.dismiss();

                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {


                                                    mProgessDialog.dismiss();
                                                }

                                            }
                                        });

                                    } else {

                                        Toast.makeText(SettingsActivity.this, "Error in upload thumbnail", Toast.LENGTH_LONG).show();
                                        mProgessDialog.dismiss();
                                    }

                                }
                            });
                        }
                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

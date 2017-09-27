package com.project.x12112241.there4u;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileActivity extends AppCompatActivity {


    private CircleImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendCount;
    private FancyButton mProfileSendReqBtn;

    private DatabaseReference mUsersDatabase, mFriendReqDataBase, mFriendDatabase;

    private FirebaseUser mCurrent_user;

    private String mfriend_State;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

//        mDisplayID = (TextView) findViewById(R.id.profile_displayName);
//        mDisplayID.setText(user_id);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        mFriendReqDataBase = FirebaseDatabase.getInstance().getReference().child("friend_request");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();


        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (FancyButton) findViewById(R.id.profile_send_req_btn);

        mfriend_State = "not_friends";

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).into(mProfileImage);

                // Friend List/ Request Feature///

                mFriendReqDataBase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("recieved")) {

                                mfriend_State = "req_recieved";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                            } else if (req_type.equals("sent")) {
                                mfriend_State = "req_recieved";
                                mProfileSendReqBtn.setText("Cancle Friend Request");
                            }
                        } else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {

                                        mfriend_State = "friends";
                                        mProfileSendReqBtn.setText("Remove Friend");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);


                // NOT FRIENDS STATE//
                if (mfriend_State.equals("not_friends")) {

                    mFriendReqDataBase.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mFriendReqDataBase.child(user_id).child(mCurrent_user.getUid()).child("request_type").setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mProfileSendReqBtn.setEnabled(true);
                                        mfriend_State = "req_sent";
                                        mProfileSendReqBtn.setText("Cancel Friend Request");


                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed To Send Request.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
                // Cancel Request STATE//

                if (mfriend_State.equals("req_sent")) {
                    mFriendReqDataBase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDataBase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendReqBtn.setEnabled(true);
                                    mfriend_State = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                }
                            });
                        }
                    });
                }
                // Request recieved States///
                if (mfriend_State.equals("req_recieved")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendReqDataBase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendReqDataBase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mfriend_State = "friends";
                                                    mProfileSendReqBtn.setText("Remove Friend");

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    });


                }

            }
        });

    }
}

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
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileActivity extends AppCompatActivity {


    private CircleImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendCount;
    private FancyButton mProfileSendReqBtn, mDeclineReqBtn, mGroupAddBtn;

    private DatabaseReference mUsersDatabase, mFriendReqDataBase, mFriendDatabase, mNotificationDatabase, mRootRef, mGroupDatabase;

    private FirebaseUser mCurrent_user;

    private String mfriend_State, mgroup_State;


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
        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Group");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();


        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        //mProfileFriendCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (FancyButton) findViewById(R.id.profile_send_req_btn);
        mDeclineReqBtn = (FancyButton) findViewById(R.id.profile_decline_req_btn);
        mGroupAddBtn = (FancyButton) findViewById(R.id.profile_add_group_btn);

        mfriend_State = "not_friends";
        mgroup_State = "not_grouped";

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).into(mProfileImage);

                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                mDeclineReqBtn.setEnabled(false);

                // Friend List/ Request Feature///

                mFriendReqDataBase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mfriend_State = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mDeclineReqBtn.setVisibility(View.VISIBLE);
                                mDeclineReqBtn.setEnabled(true);

                            } else if (req_type.equals("sent")) {
                                mfriend_State = "req_received";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);
                            }
                        } else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {

                                        mfriend_State = "Friends";
                                        mProfileSendReqBtn.setText("Remove Friend");

                                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mDeclineReqBtn.setEnabled(false);
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
//------------------add group database
        mGroupDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user_id)) {

                    String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                    if (req_type.equals("group")) {

                        mgroup_State = "group";
                        mGroupAddBtn.setText("Remove From Group");

                    } else if (req_type.equals("not_grouped")) {
                        mgroup_State = "not_grouped";
                        mGroupAddBtn.setText("Add to Group");
                    }


                } else {
                    mGroupDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(user_id)) {

                                mgroup_State = "not_grouped";
                                mGroupAddBtn.setText("Add to Group");

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
//----------------------------------------

        //---add group button

        mGroupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGroupAddBtn.setEnabled(false);


                // NOT Grouped STATE//
                if (mgroup_State.equals("not_grouped")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());


                    Map groupMap = new HashMap();
                    groupMap.put("Group/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                    groupMap.put("Group/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate);
                    groupMap.put("Group/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "group");
                    groupMap.put("Group/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "group");

                    mRootRef.updateChildren(groupMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            mGroupAddBtn.setEnabled(true);
                            mgroup_State = "group";
                            mGroupAddBtn.setText("Remove from Group");


                            if (databaseError != null) {


                                Toast.makeText(ProfileActivity.this, " There was an error sending the request", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
                //grouped State

                if (mgroup_State.equals("group")) {


                    Map ungroupMap = new HashMap();
                    ungroupMap.put("Group/" + mCurrent_user.getUid() + "/" + user_id, null);
                    ungroupMap.put("Group/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(ungroupMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                mGroupAddBtn.setEnabled(true);
                                mgroup_State = "not_grouped";
                                mGroupAddBtn.setText("Add to Group");


                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }

        });


        //---------------
        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);


                // NOT FRIENDS STATE//
                if (mfriend_State.equals("not_friends")) {

                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<String, String>();
                    notificationData.put("from", mCurrent_user.getUid());
                    notificationData.put("type", "request");


                    Map requestMap = new HashMap();
                    requestMap.put("friend_request/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("friend_request/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);
                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            mProfileSendReqBtn.setEnabled(true);
                            mfriend_State = "req_sent";
                            mProfileSendReqBtn.setText("Cancel Friend Request");

                            mDeclineReqBtn.setVisibility(View.INVISIBLE);
                            mDeclineReqBtn.setEnabled(false);

                            if (databaseError != null) {


                                Toast.makeText(ProfileActivity.this, " There was an error sending the request", Toast.LENGTH_SHORT);
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

                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mDeclineReqBtn.setEnabled(false);

                                }
                            });
                        }
                    });
                }
                // Request received States///
                if (mfriend_State.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());


                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate);

                    friendsMap.put("friend_request/" + mCurrent_user.getUid() + "/" + user_id, null);
                    friendsMap.put("friend_request/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                mProfileSendReqBtn.setEnabled(true);
                                mfriend_State = "Friends";
                                mProfileSendReqBtn.setText("Remove Friend");

                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);


                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }

                    });


                }


                //----------- Unfriend----------------//


                if (mfriend_State.equals("Friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                mProfileSendReqBtn.setEnabled(true);
                                mfriend_State = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);


                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }


            }
        });
        mDeclineReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDeclineReqBtn.setEnabled(false);
                mDeclineReqBtn.setVisibility(View.INVISIBLE);

                if (mfriend_State.equals("req_received")) {
                    mFriendReqDataBase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDataBase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendReqBtn.setEnabled(true);
                                    mfriend_State = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mDeclineReqBtn.setEnabled(false);

                                }
                            });
                        }
                    });
                }

            }
        });
    }
}


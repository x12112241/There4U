

package com.project.x12112241.there4u;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private RecyclerView mGroupsList;

    private DatabaseReference mGroupsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_groups, container, false);

        mGroupsList = (RecyclerView) mMainView.findViewById(R.id.group_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mGroupsDatabase = FirebaseDatabase.getInstance().getReference().child("Group").child(mCurrent_user_id);
        mGroupsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);


        //mGroupsList.setHasFixedSize(true);
        mGroupsList.setLayoutManager(new LinearLayoutManager(getContext()));


        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Groups, GroupsViewHolder> groupsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Groups, GroupsViewHolder>(Groups.class, R.layout.user_profile_layout, GroupsViewHolder.class, mGroupsDatabase) {
            @Override
            protected void populateViewHolder(final GroupsViewHolder groupsviewHolder, Groups groups, int i) {

                groupsviewHolder.setDate(groups.getDate());
                final String list_user_id = getRef(i).getKey();


                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            groupsviewHolder.setUserOnline(userOnline);
                        }


                        groupsviewHolder.setName(userName);
                        groupsviewHolder.setUserImage(userThumb, getContext());

                        groupsviewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                CharSequence options[] = new CharSequence[]{"View Profile", "Send Message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (i == 0) ;
                                        {

                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);
                                        }

                                        if (i == 1) {

                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);


                                        }

                                    }
                                });


                                builder.show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mGroupsList.setAdapter(groupsRecyclerViewAdapter);

    }

    public static class GroupsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public GroupsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_profile_status);
            userStatusView.setText(date);

        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_profile_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.profile_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.new_user).into(userImageView);


        }

        public void setUserOnline(String online_status) {

            CircleImageView userOnlineView = (CircleImageView) mView.findViewById(R.id.online_image);

            if (online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }


}

package com.project.x12112241.there4u;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class ConnectionsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private DatabaseReference mUsersDatabase;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String mCurrentUserId;

    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connections);

        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mToolbar = (Toolbar) findViewById(R.id.connections_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("There 4 U Chat");

        //tabs
        mViewPager = (ViewPager) findViewById(R.id.tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.social_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent loginIntent = new Intent(ConnectionsActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();


        } else {

            mUsersDatabase.child(mCurrentUserId).child("online").setValue(true);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            mUsersDatabase.child(mCurrentUserId).child("online").setValue(ServerValue.TIMESTAMP);
            mUsersDatabase.child(mCurrentUserId).child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout_Btn) {
            FirebaseAuth.getInstance().signOut();
        }
        if (item.getItemId() == R.id.settings_Btn) {

            Intent settingsIntent = new Intent(ConnectionsActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        if (item.getItemId() == R.id.allusers_Btn) {

            Intent usersIntent = new Intent(ConnectionsActivity.this, UsersActivity.class);
            startActivity(usersIntent);
        }


        return true;
    }
}

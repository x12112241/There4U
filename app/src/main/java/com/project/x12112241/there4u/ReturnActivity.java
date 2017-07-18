package com.project.x12112241.there4u;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReturnActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private TextView mNameView;
    private Button MapView;
    private Button Backbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Name");
        mNameView = (TextView) findViewById(R.id.name_view);


        MapView = (Button) findViewById(R.id.map_button);
        MapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent maps = new Intent(ReturnActivity.this,MapsActivity.class);
                startActivity(maps);
            }
        });

        Backbtn = (Button) findViewById(R.id.back_btn);
        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(ReturnActivity.this,MainActivity.class);
                startActivity(back);
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.getValue().toString();

                mNameView.setText("Name : " + name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

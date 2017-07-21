package com.project.x12112241.there4u;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button mFirebaseBtn;
    private Button NextActivity;
    private Button mUploadImage;

    private static final int GALLERY_INTENT = 2;

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private EditText mNameField;
    private EditText mEmailField;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseBtn = (Button) findViewById(R.id.firebase_btn);



        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef = mStorageRef.child("images");
        StorageReference mountainsRef = storageRef.child("mountains.jpg");
        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

        //storageRef = mStorageRef.child("images");


        StorageReference nullRef = storageRef.getRoot().getParent();









        mNameField = (EditText) findViewById(R.id.name_field);
        mEmailField = (EditText) findViewById(R.id.email_field);

       // NextActivity.setOnClickListener(new View.OnClickListener() {


            NextActivity = (Button) findViewById(R.id.nextActivity);
            NextActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent next = new Intent(MainActivity.this,ReturnActivity.class);
                    startActivity(next);
                }
            });
        mUploadImage = (Button) findViewById(R.id.image_upload);

        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent image = new Intent(Intent.ACTION_PICK);
                image.setType("image/*");
                startActivityForResult(image, GALLERY_INTENT);

               // startActivityForResult(image, GALLERY_INTENT);


            }
        });






                //onNewIntent(getApplicationContext(),ReturnActivity.class););
        mFirebaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //1 - Create Child in root object
                //2 - Assign some value to the child object

                //mDatabase.child("Name").setValue("Ben Callaghan");

                String name = mNameField.getText().toString().trim();
                String email = mEmailField.getText().toString().trim();

                HashMap<String, String> dataMap = new HashMap<String, String>();
                dataMap.put("Name", name);
                dataMap.put("Email", email);

                mDatabase.push().setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "Info Stored..", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(MainActivity.this, "Error...", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            uri = data.getData();

            StorageReference filePath = mStorageRef.child("Photos").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(MainActivity.this, "Upload Done" , Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

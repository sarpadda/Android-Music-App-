package com.manpreetsingh.firetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    //passing user name to the other activity;
    public static final String USER_LOGGED_IN ="userLogged";
    public static  final String USER_ID ="userid";

    public Intent artistIntent;
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;

    private DatabaseReference database;
    private EditText editTextname, editTextAddress ;
    private Button saveButton;
    private Button addTrackButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //instantiating database
        database = FirebaseDatabase.getInstance().getReference("USERS");

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //passing userLoggedIn to the other activity


        Bundle bundle_profile = new Bundle();
        bundle_profile.putString("userLogged",user.getEmail());
        bundle_profile.putString("userid",user.getUid());

        artistIntent = new Intent(this, ArtistActivity.class);
        artistIntent.putExtras(bundle_profile);


        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        editTextname = (EditText) findViewById(R.id.editTextName);

        //saving this name for passing to another activity


        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        saveButton = (Button) findViewById(R.id.saveInfoButton);
        addTrackButton = (Button) findViewById(R.id.addTrackButton);


        //displaying logged in user name
        textViewUserEmail.setText("Welcome "+user.getEmail());


        //adding listener to button
        buttonLogout.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        addTrackButton.setOnClickListener(this);




    }

    private  void saveUserInfo()
    {
        String name = editTextname.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();


        UserInformation userinformation = new UserInformation(name,address);
        FirebaseUser fireuser = firebaseAuth.getCurrentUser();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address))
        {
            Toast.makeText(this,"Enter Information ",Toast.LENGTH_LONG).show();
        }
        else {

            //saving to the database
            database.child(fireuser.getUid()).setValue(userinformation);


            Toast.makeText(this,"Information Saved",Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        if (view == saveButton)
        {
            saveUserInfo();
        }

        if (view  == addTrackButton)
        {
            // go to artist activity

            finish();
            //passing the value of userLoggedIn to the artist activity

            startActivity(artistIntent);

        }
    }
}
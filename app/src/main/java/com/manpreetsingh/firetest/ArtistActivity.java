package com.manpreetsingh.firetest;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ArtistActivity extends AppCompatActivity   {

    //we will use these constants later to pass the artist name and id to another activity
    public static final String ARTIST_NAME = "artistname" ;
    public static final String ARTIST_ID = "artistid";

   public static final String USER_ID ="userid";



    //view objects
    TextView myMessage ;
    EditText editTextName;
    Spinner spinnerGenre;
    Button buttonAddArtist;
    Button profileButton;
    Button updateArtistButton ;
    public String userID;

    //a list to store all the artist from firebase database
    List<Artist> artists;

    //our database reference object
    DatabaseReference databaseArtists;
    DatabaseReference updateArtistDatabase;



    //listview...VERY IMPORTANT STEP ****************************************************************************************
    ListView listViewArtists;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);


        //Getting User_ID from the profile activity
        Intent intent = getIntent();

         userID = intent.getStringExtra(ProfileActivity.USER_ID);
        String user_email = intent.getStringExtra(ProfileActivity.USER_LOGGED_IN);

        //getting the reference of artists node by connecting it with the parent user_id
        databaseArtists = FirebaseDatabase.getInstance().getReference("artistsOfUser").child(userID);


        //getting views
        myMessage = (TextView) findViewById(R.id.textView);
        editTextName = (EditText) findViewById(R.id.editTextName);
        spinnerGenre = (Spinner) findViewById(R.id.spinnerGenres);
        listViewArtists = (ListView) findViewById(R.id.listViewArtists);

        buttonAddArtist = (Button) findViewById(R.id.buttonAddArtist);

        profileButton = (Button)  findViewById(R.id.profileButton);


        profileButton.setText(user_email.toLowerCase());


        //list to store artists************************************************************************************************
        artists = new ArrayList<>();


        //adding an onclicklistener to button
        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addArtist()
                //the method is defined below
                //this method is actually performing the write operation
                addArtist();
            }
        });
        //User-profile back button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });

        //attaching listener to listview
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Artist artist = artists.get(i);

                //creating an intent
                Intent intent = new Intent(getApplicationContext(), TrackActivity.class);

                //putting artist name and id to intent
                intent.putExtra(ARTIST_ID, artist.getArtistId());
                intent.putExtra(ARTIST_NAME, artist.getArtistName());

                //starting the activity with intent
                startActivity(intent);
            }
        });

        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Artist artist = artists.get(position);

                //call the update dialog box ****************
                showUpdateDeleteDialog(artist.getArtistId(),artist.getArtistName());
                return true;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                artists.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist

                        Artist artist = postSnapshot.getValue(Artist.class);
                        //adding artist to the list
                        artists.add(artist);

                }

                //creating adapter using custom adapter "ArtistList"
                ArtistList artistAdapter = new ArtistList(ArtistActivity.this, artists);
                //attaching adapter to the listview
                listViewArtists.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    * This method is saving a new artist to the
    * Firebase Realtime Database
    * */
    private void addArtist() {
        //getting the values to save
        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();

        //checking if the value is provided
        if (!TextUtils.isEmpty(name)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Artist
            String id = databaseArtists.push().getKey();

            //creating an Artist Object
            Artist artist = new Artist(id, name, genre);

            //Saving the Artist
            databaseArtists.child(id).setValue(artist);

            //setting edittext to blank again
            editTextName.setText("");

            //displaying a success toast
            Toast.makeText(this, "Artist added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }

    private void showUpdateDeleteDialog(final String artistId,  String artistName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Spinner spinnerGenre = (Spinner) dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteArtist);

        dialogBuilder.setTitle("Please Update or Delete the Artist - " + artistName);
        final AlertDialog showDialogBox = dialogBuilder.create();
        showDialogBox.show();



        // update button listener
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update the artist;
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenre.getSelectedItem().toString();

                if (TextUtils.isEmpty(name))
                {
                    editTextName.setError("Artist Name is Required");

                }

                updateArtist(artistId, name, genre);
                showDialogBox.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArtist(artistId);
                showDialogBox.dismiss();
            }
        });

    }//end of showupdateDeleteDialog


    public boolean updateArtist(String id, String ArtistName, String genre)
    {
        //DatabaseReference db = FirebaseDatabase.getInstance().getReference("artistsOfUser").child()
        updateArtistDatabase = FirebaseDatabase.getInstance().getReference("artistsOfUser").child(userID);

        Artist artist = new Artist(id, ArtistName,genre);
        updateArtistDatabase.child(id).setValue(artist);
        Toast.makeText(this,"Artist Info Updated",Toast.LENGTH_LONG).show();
        return  true;
    }

    public void deleteArtist(String id)
    {
        DatabaseReference databaseArtist = FirebaseDatabase.getInstance().getReference("artistsOfUser").child(userID).child(id);
        DatabaseReference databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        databaseArtist.removeValue();
        databaseTracks.removeValue();

        Toast.makeText(this, "Artist is deleted",Toast.LENGTH_LONG);
    }
}


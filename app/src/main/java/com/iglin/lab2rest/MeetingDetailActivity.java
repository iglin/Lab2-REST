package com.iglin.lab2rest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iglin.lab2rest.model.ContactsDialog;
import com.iglin.lab2rest.model.Meeting;
import com.iglin.lab2rest.model.Participant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An activity representing a single Meeting detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MeetingListActivity}.
 */
public class MeetingDetailActivity extends AppCompatActivity {

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private static final int REQ_CODE_FOR_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptMeeting();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(MeetingDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(MeetingDetailFragment.ARG_ITEM_ID));
            MeetingDetailFragment fragment = new MeetingDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.meeting_detail_container, fragment)
                    .commit();
        }
    }

    private void acceptMeeting() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String userId = firebaseAuth.getCurrentUser().getUid();

        final Participant participant = new Participant();
        participant.setId(userId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                participant.setName(dataSnapshot.child("users").child(userId).child("name").getValue(String.class));
                participant.setPosition(dataSnapshot.child("users").child(userId).child("position").getValue(String.class));

                String meetingId = getIntent().getStringExtra(MeetingDetailFragment.ARG_ITEM_ID);

                System.out.println(meetingId + " " + userId + participant.getName());

                Meeting meeting = dataSnapshot.child("meetings").child(meetingId).getValue(Meeting.class);
                meeting.addParticipant(participant);

                dataSnapshot.child("meetings").child(meetingId).getRef().setValue(meeting);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showErrorToast("Error! Unable to update meeting.");
            }
        });
    }

    private void deleteMeeting() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String meetingId = getIntent().getStringExtra(MeetingDetailFragment.ARG_ITEM_ID);

                dataSnapshot.child("meetings").child(meetingId).getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showErrorToast("Error! Unable to delete meeting.");
            }
        });
    }

    private void showErrorToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
      /*  if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
          //  navigateUpTo(new Intent(this, MeetingListActivity.class));
            onBackPressed();
            return true;
        }*/
        switch (id) {
            case  R.id.home:
                onBackPressed();
                return true;
            case R.id.item_go:
                acceptMeeting();
                break;
            case R.id.item_invite:
           //     Intent intent = new Intent(this, ContactsActivity.class);
           //     startActivity(intent);

           //     Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
              //  pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_URI); // Show user only contacts w/ phone numbers
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(pickContactIntent, REQ_CODE_FOR_CONTACT);

                break;
            case R.id.item_delete:
                deleteMeeting();
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQ_CODE_FOR_CONTACT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                Uri contactUri = data.getData();

                String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();

                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
                    String name = cursor.getString(column);
                    Log.i(getClass().getName(), "Picked " + name);
                    // Do something with the contact here (bigger example below)
                }
            }
        }
    }
}

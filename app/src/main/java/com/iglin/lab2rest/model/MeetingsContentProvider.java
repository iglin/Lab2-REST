package com.iglin.lab2rest.model;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.R.id.candidatesArea;
import static android.R.id.list;

/**
 * Created by user on 18.01.2017.
 */

public class MeetingsContentProvider {

    private DatabaseReference database;

    private List<Meeting> meetings = new ArrayList<>();

    public MeetingsContentProvider() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public List<Meeting> getMeetingsList() {
        final List<Meeting> result = new ArrayList<>();
          database.child("meetings").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Meeting meeting = snapshot.getValue(Meeting.class);
                            meeting.setId(snapshot.getKey());
                            meetings.add(meeting);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                }
        );
        return result;
    }

    public Meeting getMeeting(String id) {
        String ref = database.child("meetings").child(id).getRef().toString();
        System.out.println(ref);

        Meeting result = null;
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL("https://lab2-rest.firebaseio.com/meetings/" + id + ".json");
            urlConnection = (HttpsURLConnection) url.openConnection();
            try (InputStream in = new BufferedInputStream(urlConnection.getInputStream())) {
                ObjectMapper mapper = new ObjectMapper();
                result = mapper.readValue(in, Meeting.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }

    public void addMeeting(Meeting meeting) {
        database.child("meetings").push().setValue(meeting);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Meeting> {
        @Override
        protected Meeting doInBackground(Void... params) {
            try {
                final String url = "http://rest-service.guides.spring.io/greeting";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                return restTemplate.getForObject(url, Meeting.class);
            } catch (Exception e) {
                System.out.println(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Meeting greeting) {
      //      TextView greetingIdText = (TextView) findViewById(R.id.id_value);
        //    TextView greetingContentText = (TextView) findViewById(R.id.content_value);
        //    greetingIdText.setText(greeting.getId());
       //     greetingContentText.setText(greeting.getContent());
        }

    }
}

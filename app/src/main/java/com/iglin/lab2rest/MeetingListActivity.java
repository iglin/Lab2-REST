package com.iglin.lab2rest;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.iglin.lab2rest.model.DateTimeFormatter;
import com.iglin.lab2rest.model.Meeting;
import com.iglin.lab2rest.model.MeetingsContentProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An activity representing a list of Meetings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MeetingDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MeetingListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private RetainedFragment dataFragment;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listener;
    private Query query;

    private MeetingsSearchDialog dialog;
    private boolean searchEnabled;
    private boolean onlyFutureMeetings;
    private String searchText;

    public String getSearchText() {
        return searchText;
    }

    public boolean isOnlyFutureMeetings() {
        return onlyFutureMeetings;
    }

    private final MeetingsContentProvider meetingsContentProvider = new MeetingsContentProvider();

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            loadLogInView();
        }

        setContentView(R.layout.activity_meeting_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FragmentManager fm = getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();
            // load the data from the web
            dataFragment.setSearchEnabled(false);
            dataFragment.setOnlyFutureMeetings(false);
            dataFragment.setSearchText(null);
        }
        searchEnabled = dataFragment.isSearchEnabled();
        onlyFutureMeetings = dataFragment.isOnlyFutureMeetings();
        searchText = dataFragment.getSearchText();

        dialog = new MeetingsSearchDialog(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

     //   View recyclerView = findViewById(R.id.meeting_list);
      //  assert recyclerView != null;
       // setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.meeting_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        updateMeetingsListeners(searchEnabled ,onlyFutureMeetings, searchText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        dataFragment.setSearchEnabled(searchEnabled);
        dataFragment.setOnlyFutureMeetings(onlyFutureMeetings);
        dataFragment.setSearchText(searchText);
    }

    public void updateMeetingsListeners(boolean searchEnabled, boolean onlyFutureMeetings, String searchText) {
        this.searchEnabled = searchEnabled;
        this.onlyFutureMeetings = onlyFutureMeetings;
        this.searchText = searchText;

        if (query != null && listener != null) {
            query.removeEventListener(listener);
        }

        if (searchEnabled) {
            if ((searchText != null && searchText.length() > 0)) {
                if (onlyFutureMeetings) query =  database.child("meetings")
                        .orderByChild("description").startAt(searchText)
                        .orderByChild("endTime").startAt((new Date()).getTime());
                else query = database.child("meetings").orderByChild("description").startAt(searchText);
            } else if (onlyFutureMeetings) {
                query =  database.child("meetings").orderByChild("endTime").startAt((new Date()).getTime());
            } else {
                query =  database.child("meetings");
            }
        } else {
            query = database.child("meetings");
        }
        System.out.println(query.toString());

        System.out.println(searchEnabled + " " + searchText);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Meeting> result = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Meeting meeting = snapshot.getValue(Meeting.class);
                    meeting.setId(snapshot.getKey());
                    result.add(meeting);
                }

                View recyclerView = findViewById(R.id.meeting_list);
                assert recyclerView != null;

                setupRecyclerView((RecyclerView) recyclerView, result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };

        query.addValueEventListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.item_new:
                Intent intent = new Intent(this, NewMeetingActivity.class);
                startActivity(intent);
                break;
            case R.id.item_search:
                dialog.show();
                break;
            case R.id.item_settings:
                break;
            case R.id.item_logout:
                firebaseAuth.signOut();
                loadLogInView();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Meeting> list) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(list));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Meeting> mValues;

        public SimpleItemRecyclerViewAdapter(List<Meeting> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.meeting_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getName());
            try {
                holder.mContentView.setText(mValues.get(position).getPriority() + "\n"
                        + DateTimeFormatter.getShortFormat(mValues.get(position).getStartTimeAsDate()) + "\n"
                        + DateTimeFormatter.getShortFormat(mValues.get(position).getEndTimeAsDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MeetingDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        MeetingDetailFragment fragment = new MeetingDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.meeting_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MeetingDetailActivity.class);
                        intent.putExtra(MeetingDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mIdView;
            final TextView mContentView;
            Meeting mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}

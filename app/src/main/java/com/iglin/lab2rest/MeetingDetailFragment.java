package com.iglin.lab2rest;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iglin.lab2rest.model.Meeting;

import java.text.ParseException;

/**
 * A fragment representing a single Meeting detail screen.
 * This fragment is either contained in a {@link MeetingListActivity}
 * in two-pane mode (on tablets) or a {@link MeetingDetailActivity}
 * on handsets.
 */
public class MeetingDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    /**
     * The dummy content this fragment is presenting.
     */
    private Meeting mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MeetingDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String id = getArguments().getString(ARG_ITEM_ID);

            final Activity activity = this.getActivity();
            final CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);


            if (id != null)
                database.child("meetings").child(id).addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mItem = dataSnapshot.getValue(Meeting.class);
                                    mItem.setId(dataSnapshot.getKey());
                                    if (appBarLayout != null && mItem != null) {
                                        appBarLayout.setTitle(mItem.getName());
                                        try {
                                            TextView textView = (TextView) activity.findViewById(R.id.meeting_detail);
                                            if (textView != null)
                                                textView.setText(mItem.representDetails());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    getActivity().onBackPressed();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) { }
                        }
                );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.meeting_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            try {
                ((TextView) rootView.findViewById(R.id.meeting_detail)).setText(mItem.representDetails());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return rootView;
    }
}

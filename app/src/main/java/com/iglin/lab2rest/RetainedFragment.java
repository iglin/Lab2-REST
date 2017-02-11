package com.iglin.lab2rest;

import android.app.Fragment;
import android.os.Bundle;

public class RetainedFragment extends Fragment {

    // data object we want to retain
    private boolean searchEnabled;
    private boolean onlyFutureMeetings;
    private String searchText;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public boolean isSearchEnabled() {
        return searchEnabled;
    }

    public void setSearchEnabled(boolean searchEnabled) {
        this.searchEnabled = searchEnabled;
    }

    public boolean isOnlyFutureMeetings() {
        return onlyFutureMeetings;
    }

    public void setOnlyFutureMeetings(boolean onlyFutureMeetings) {
        this.onlyFutureMeetings = onlyFutureMeetings;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
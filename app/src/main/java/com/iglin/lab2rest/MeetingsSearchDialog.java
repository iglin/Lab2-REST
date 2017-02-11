package com.iglin.lab2rest;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by user on 11.02.2017.
 */

public class MeetingsSearchDialog extends Dialog implements
        android.view.View.OnClickListener {

    private MeetingListActivity activity;

    MeetingsSearchDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = (MeetingListActivity) a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_dialog);

        ((CheckBox)findViewById(R.id.checkBox)).setChecked(activity.isOnlyFutureMeetings());
        if (activity.getSearchText() != null) ((EditText) findViewById(R.id.editTextSearch)).setText(activity.getSearchText());

        findViewById(R.id.buttonFind).setOnClickListener(this);
        findViewById(R.id.buttonCancel).setOnClickListener(this);
        findViewById(R.id.buttonClear).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFind:
                EditText editText = (EditText) findViewById(R.id.editTextSearch);
                if (editText.getText() != null && editText.getText().length() > 0) {
                    activity.updateMeetingsListeners(true, ((CheckBox)findViewById(R.id.checkBox)).isChecked(), editText.getText().toString());
                } else {
                    activity.updateMeetingsListeners(true, ((CheckBox)findViewById(R.id.checkBox)).isChecked(), null);
                }
                break;
            case R.id.buttonCancel:
                break;
            case R.id.buttonClear:
                ((CheckBox)findViewById(R.id.checkBox)).setChecked(false);
                ((EditText) findViewById(R.id.editTextSearch)).setText("");
                activity.updateMeetingsListeners(false, false, null);
                break;
            default:
                break;
        }
        dismiss();
    }
}
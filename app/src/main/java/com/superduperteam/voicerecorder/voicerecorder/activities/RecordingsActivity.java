package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Adapter;
import android.widget.ListView;

import com.superduperteam.voicerecorder.voicerecorder.R;

public class RecordingsActivity extends AppCompatActivity {

    private RecyclerView recordingsRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);

        recordingsRecyclerView = findViewById(R.id.recordingsListView);
    }
}

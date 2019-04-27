package com.superduperteam.voicerecorder.voicerecorder.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

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

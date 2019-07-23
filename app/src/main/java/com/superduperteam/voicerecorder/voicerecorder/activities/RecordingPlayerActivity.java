package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.content.Context;

import androidx.appcompat.app.ActionBar;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class RecordingPlayerActivity extends BaseActivity {
    private Recording recording;
    private static final String LOG_TAG = "AudioRecordTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_recording_player);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_recording_player, null, false);
        mDrawer.addView(contentView, 0);
        Intent intent = getIntent();
        String pathToFileToPlay = intent.getStringExtra("fileToPlayPath");
        File file = new File(pathToFileToPlay);

        try {
            this.recording = new Recording(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            startPlaying();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPlaying() throws IOException {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(recording.getFile().getPath());
            player.prepare();
            player.start();
            System.out.print("duration: ");
            System.out.println(player.getDuration());
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        MetaDataRead cmd = new MetaDataRead();
        String text = cmd.read(recording.getFile().getPath());
    }

// This is for the buttons in the top: search and sort
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_recordings, menu);
//        MenuItem mMenuItem = menu.findItem(R.id.app_bar_search);
//
//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
////        mMenuItem.setVisible(false);
////        mMenuItem.setTitle("Deleteeeee");
////        mMenuItem.setEnabled(false);
//        return super.onCreateOptionsMenu(menu);
//    }

//    private MenuItem mMenuItem;

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        invalidateOptionsMenu();
//        menu.findItem(R.id.app_bar_search).setVisible(false);
//        menu.findItem(R.id.app_bar_search).setVisible(true);
//        return super.onPrepareOptionsMenu(menu);
//    }

}

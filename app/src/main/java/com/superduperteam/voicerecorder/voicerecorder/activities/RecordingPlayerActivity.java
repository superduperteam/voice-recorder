package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.content.Context;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuView;

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
import android.widget.ImageButton;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class RecordingPlayerActivity extends BaseActivity {
    private Recording recording;
    private static final String LOG_TAG = "AudioRecordTest";
    ImageButton playButton;
    MediaPlayer player = new MediaPlayer();

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
            this.recording = new Recording(file.getName(),file, Recording.fetchBookmarks(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            startPlaying();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playButton = findViewById(R.id.recordingPlayerPlay);
        playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);

        player.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.reset();
            playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
        });
    }

    private void startPlaying() throws IOException {
        try {
            player.setDataSource(recording.getFile().getPath());
            player.prepare();
            player.start();
            System.out.print("duration: ");
            System.out.println(player.getDuration());
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }


        System.out.println(recording.getBookmarksList());
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

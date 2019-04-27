package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private Chronometer chronometer;
    //private boolean isStart;
    private boolean isRecording;

    //voice recorder
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    private MediaRecorder recorder = null;

    private MediaPlayer player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    boolean mStartRecording = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.screen1, null, false);
        mDrawer.addView(contentView, 0);
//        super.replaceContentLayout(R.layout.activity_main, super.C);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
//
//        drawerLayout = findViewById(R.id.drawer_layout);

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        // set item as selected to persist highlight
//                        int id = menuItem.getItemId();
//                        Intent intent;
//
//                        switch (id) {
//                            case R.id.nav_recorder:
//                                //Do some thing here
//                                // add navigation drawer item onclick method here
//                                break;
//                            case R.id.nav_recordings:
//                                intent = new Intent(MainActivity.this, RecordingsActivity.class);
//                                startActivity(intent);
//                                break;
//                            case R.id.nav_settings:
//                                intent = new Intent(MainActivity.this, RecordingPlayerActivity.class);
//                                startActivity(intent);
//                                break;
//                        }
//
//                        // close drawer when item is tapped
//                        drawerLayout.closeDrawers();
//
//                        // Add code here to update the UI based on the item selected
//                        // For example, swap UI fragments here
//
//                        return true;
//                    }
//                });

//        drawerLayout.addDrawerListener(
//                new DrawerLayout.DrawerListener() {
//                    @Override
//                    public void onDrawerSlide(View drawerView, float slideOffset) {
//                        // Respond when the drawer's position changes
//                    }
//
//                    @Override
//                    public void onDrawerOpened(View drawerView) {
//                        // Respond when the drawer is opened
//                    }
//
//                    @Override
//                    public void onDrawerClosed(View drawerView) {
//                        // Respond when the drawer is closed
//                    }
//
//                    @Override
//                    public void onDrawerStateChanged(int newState) {
//                        // Respond when the drawer motion state changes
//                    }
//                }
//        );

        chronometer = findViewById(R.id.chronometer);

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                chronometer = chronometerChanged;
            }
        });
    }

    @Override
    protected int getLayoutToInflate() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private long lastPause;
    private boolean isFirstStart = true;

    public void onRecordClick(View view) {
        ImageButton button = findViewById(R.id.record_button);
        button.setBackgroundResource(R.drawable.ic_pressed_record_button);

        if (!isRecording) {
            startStopWatch();
        } else {
            stopStopWatch();
        }

        isRecording = !isRecording;
    }

    private void stopStopWatch() {
        lastPause = SystemClock.elapsedRealtime();
        chronometer.stop();
    }

    private void startStopWatch() {
        if (isFirstStart) {
            isFirstStart = false;
            chronometer.setBase(SystemClock.elapsedRealtime());
        } else {
            chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
        }
        chronometer.start();
    }
}

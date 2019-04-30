package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.content.Context;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import java.util.Objects;


public class RecordingPlayerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_recording_player);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_recording_player, null, false);
        mDrawer.addView(contentView, 0);
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

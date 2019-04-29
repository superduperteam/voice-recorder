package com.superduperteam.voicerecorder.voicerecorder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

public class RecordingsActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
//        setContentView(R.layout.activity_recording_player);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_recordings, null, false);
        mDrawer.addView(contentView, 0);




//        recyclerView = (RecyclerView) findViewById(R.id.recordingsLRecyclerView);

    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        getMenuInflater().inflate(R.layout.recordingsMoreOptions);
//    }


    // This is for the buttons in the top: search and sort
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recordings, menu);
        MenuItem mMenuItem = menu.findItem(R.id.app_bar_search);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
//        mMenuItem.setVisible(false);
//        mMenuItem.setTitle("Deleteeeee");
//        mMenuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }


    // This is for the actual list of recordings - if user click the vertical 3 dots
    public void onRecordingClick(View view) {
        PopupMenu popup = new PopupMenu(this, view); // view=button
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.recording_clicked_menu);
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.recording_clicked_delete:
                Toast.makeText(this, "delete", Toast.LENGTH_LONG).show();
                return true;
            case R.id.recording_clicked_edit:
                Toast.makeText(this, "edit", Toast.LENGTH_LONG).show();
                return true;
            case R.id.recording_clicked_share:
                Toast.makeText(this, "share", Toast.LENGTH_LONG).show();
                return true;
                default:
                    return false;
        }
    }
}

package com.superduperteam.voicerecorder.voicerecorder.Activities.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.superduperteam.voicerecorder.voicerecorder.R;
import com.superduperteam.voicerecorder.voicerecorder.Activities.MainActivity.MainActivity;
import com.superduperteam.voicerecorder.voicerecorder.Activities.RecordingsActivity.RecordingsActivity;
import com.superduperteam.voicerecorder.voicerecorder.Activities.SettingsActivity.SettingsActivity;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);

        mDrawer = findViewById(R.id.drawer_layout);

        //This is about creating custom listview for navigate drawer
        //Implementation for NavigateDrawer HERE !
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    int id = menuItem.getItemId();
                    Intent intent;

                    switch (id) {
                        case R.id.nav_recorder:
                            intent = new Intent(BaseActivity.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_recordings:
                            intent = new Intent(BaseActivity.this, RecordingsActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_settings:
                            intent = new Intent(BaseActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            break;
                    }

                    // close drawer when item is tapped
                    mDrawer.closeDrawers();

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    return true;
                });

        mDrawer.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    mDrawer.openDrawer(GravityCompat.START);
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }
}

package com.superduperteam.voicerecorder.voicerecorder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DrawerLayout fullView = (DrawerLayout)
                getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout)
                fullView.findViewById(R.id.activity_base);

        int layoutToInflate = getLayoutToInflate();
        getLayoutInflater().inflate(layoutToInflate, activityContainer, true);
        super.setContentView(fullView);
    }

    protected abstract int getLayoutToInflate();
}

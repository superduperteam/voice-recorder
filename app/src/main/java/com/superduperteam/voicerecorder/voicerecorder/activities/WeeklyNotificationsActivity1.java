package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.alamkanak.weekview.MonthLoader.MonthChangeListener;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekView.EventClickListener;
import com.alamkanak.weekview.WeekView.EventLongPressListener;
import com.alamkanak.weekview.WeekViewEvent;
import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeeklyNotificationsActivity1 extends BaseActivity{
    private RecyclerView recyclerView;
    private NotificationsRecyclerViewAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_recording_player);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_weekly_notifications_alt, null, false);
        mDrawer.addView(contentView, 0);

//        createNotificationChannel();
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        System.out.println(notificationManager.getNotificationChannels());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "2";
            String description = "not1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("6", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

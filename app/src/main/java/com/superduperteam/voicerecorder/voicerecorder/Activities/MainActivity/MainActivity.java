package com.superduperteam.voicerecorder.voicerecorder.Activities.MainActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.superduperteam.voicerecorder.voicerecorder.Activities.BaseActivity.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;
import com.superduperteam.voicerecorder.voicerecorder.Visualizer.VisualizerView;
import com.superduperteam.voicerecorder.voicerecorder.Model.Bookmark;
import com.superduperteam.voicerecorder.voicerecorder.Utills.MetaDataInsert;
import com.superduperteam.voicerecorder.voicerecorder.Utills.MetaDataRead;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity {
    private NotificationPanel nPanel;
    private ActionReceiver actionReceiver;
    private Chronometer chronometer;
    private volatile boolean isRecording = false; // is actively recording something?
    private String lastRecordingPath = null;
    private String outputFormat = ".m4a";
    private List<Bookmark> bookmarksList;
    private long pauseOffset = 0;
    private EditText bookmarkNameEditText;
    private EditText recordingNameEditText;
    private View addBookmarkView;
    private View renameRecordingView;
    private PopupWindow addBookmarkPopupWindow;
    private PopupWindow nameRecordingPopupWindow;
    private static final String STOP_RECORDING_ACTION = "stop";
    private static final String PAUSE_OR_RESUME_RECORDING_ACTION = "pauseOrResume";
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String CHANNEL_ID = "my_channel_01";
    private MediaRecorder recorder = null;


    private Handler handler; // Handler for updating the visualizer
    VisualizerView visualizerView;
    public static final int REPEAT_INTERVAL = 40;


    // Requesting permission to RECORD_AUDIO
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    //Todo: need to to make sure it checks all the checks (permissions and etc').
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawer.addView(contentView, 1);
        // Record to the external cache directory for visibility
        bookmarkNameEditText = findViewById(R.id.add_bookmark_edit_text);
        ImageButton bookmarkImageButton = findViewById(R.id.bookmark_image_button);
        bookmarkImageButton.setOnLongClickListener(view -> {
            onButtonShowPopupWindowClick(getWindow().getDecorView());
            return true;
        });

        isExternalStorageWritable();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        chronometerInit();
        visualizerView = findViewById(R.id.visualizer);
        handler = new Handler();
        actionReceiver = new ActionReceiver();
        registerReceiver(actionReceiver, new IntentFilter(STOP_RECORDING_ACTION));
        registerReceiver(actionReceiver, new IntentFilter(PAUSE_OR_RESUME_RECORDING_ACTION));
    }

    // updates the visualizer every 50 milliseconds
    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {
                // get the current amplitude
                int x = recorder.getMaxAmplitude();
                visualizerView.addAmplitude(x); // update the VisualizeView
                visualizerView.invalidate(); // refresh the VisualizerView

                // update in 40 milliseconds
                handler.postDelayed(this, REPEAT_INTERVAL);
            }
        }
    };

    //Todo: problem: if we allow different audio formats: user can have recording1.3gp and recording1.mp3 for example.
    private String getCurrentFilePath() {
        isExternalStorageWritable();

        int fileNameNumber = 1;
        String filePath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath();
        filePath += "/Recording";

        while(fileExists(filePath +" "+ fileNameNumber + outputFormat)){// the name is already taken.. Need a different name.
            fileNameNumber++;
        }

        Log.i(LOG_TAG,  "filePath chosen for recording: " + filePath);

        return filePath +" "+ fileNameNumber + outputFormat;
    }

    private boolean fileExists(String filePath) {
      //  File file = context.getFileStreamPath(filename);
        File file = new File(filePath);
        //Todo: problem: Race condition ?
        return file.exists();
    }


    private void chronometerInit() {
        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime()); // new
        chronometer.setOnChronometerTickListener(chronometerChanged -> {
            chronometer = chronometerChanged;
            TextView status = findViewById(R.id.notification_recording_time);
            if(status != null){
                status.setText(chronometerChanged.getText());
            }

            if(nPanel != null){
                if(nPanel.getRemoteView() != null){
                    nPanel.getNotificationManager().notify(2, nPanel.getNotificationBuilder().build());
                }
            }
        });

    }

    private boolean isRunning = false;

    public void onRecordClick(View view) {
        if (!isRecording) {
            findViewById(R.id.record_button).setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);

            if(nPanel != null){
                nPanel.getRemoteView().setInt(R.id.notification_pause_resume_button, "setBackgroundResource",R.drawable.ic_pause_circle_filled_black_24dp);
                nPanel.getRemoteView().setTextViewText(R.id.notification_recording_time, "Recording");
                nPanel.getNotificationManager().notify(2, nPanel.getNotificationBuilder().build());
            }

            startStopWatch();
            startRecording(isRunning);
            if(nPanel == null){
                nPanel = new NotificationPanel(this);
            }

            isRunning = true;
        } else {
            findViewById(R.id.record_button).setBackgroundResource(R.drawable.ic_record_button);

            if(nPanel != null){
                nPanel.getRemoteView().setInt(R.id.notification_pause_resume_button,"setBackgroundResource" ,R.drawable.ic_record_button);
                nPanel.getRemoteView().setTextViewText(R.id.notification_recording_time, "Paused");
                nPanel.getNotificationManager().notify(2, nPanel.getNotificationBuilder().build());
            }

            pauseStopWatch();
            pauseRecording();
        }

        isRecording = !isRecording;
    }

    @Override
    protected void onPause() {
        if(recorder != null) {
            try {
                onStopClick(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            if(actionReceiver!=null)
                unregisterReceiver(actionReceiver);
            if(nPanel != null){
                nPanel.notificationCancel();
            }

        }catch(Exception e){}
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(recorder != null) {
            try {
                onStopClick(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            if(actionReceiver!=null)
                unregisterReceiver(actionReceiver);
            if(nPanel != null){
                nPanel.notificationCancel();
            }

        }catch(Exception e){}

        super.onDestroy();
    }

    public void onStopClick(View view) throws IOException {
        if(isRunning){
            resetStopWatch();
            findViewById(R.id.record_button).setBackgroundResource(R.drawable.ic_record_button);
            stopRecording();

            if(view != null){
                onRecordingNaming(getWindow().getDecorView());
            }

            if(nPanel != null){
                nPanel.notificationCancel();
            }

            nPanel = null;
            isRunning = false;
            isRecording = false;
        }
    }

    private void startStopWatch() {
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
    }

    private void pauseStopWatch() {
       if (isRunning) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        }
    }

    private void resetStopWatch() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        pauseOffset = 0;
    }

    private void stopRecording() throws IOException {
        recorder.stop();
        recorder.release();
        recorder = null;

        handler.removeCallbacks(updateVisualizer);
        visualizerView.clear();
        visualizerView.invalidate();
        addBookmarksToMetadata();
    }

    private void pauseRecording() {
        recorder.pause();
    }

    private void recorderInit() {
        bookmarksList = new ArrayList<>();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        String outputPath = getCurrentFilePath();
        recorder.setOutputFile(outputPath);
        lastRecordingPath = outputPath;
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
        handler.post(updateVisualizer);
    }

    private void startRecording(boolean isRunning) {
        if(!isRunning){
            recorderInit();
        }
        else{
            recorder.resume();
            handler.post(updateVisualizer);
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        Log.e(LOG_TAG, "external storage is NOT available for read and write");
        return false;
    }

    public void onBookmarkClick(View view) {
        if(isRunning){
            long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            bookmarksList.add(new Bookmark(elapsedTime,""));
        }
    }

    private void addBookmarksToMetadata() throws IOException {
        MetaDataInsert cmd = new MetaDataInsert();
        StringBuilder sb = new StringBuilder();
        sb.append("Bookmarks: ").append(System.lineSeparator());

        for(Bookmark bookmark : bookmarksList){
            sb.append(bookmark.toString()).append(System.lineSeparator());
        }

        cmd.writeMetadata(lastRecordingPath, sb.toString());
    }



    public void onRecordingNaming(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        renameRecordingView = inflater.inflate(R.layout.recording_naming_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        nameRecordingPopupWindow = new PopupWindow(renameRecordingView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        nameRecordingPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        renameRecordingView.setOnTouchListener((v, event) -> {
            nameRecordingPopupWindow.dismiss();
            return true;
        });

        recordingNameEditText = view.findViewById(R.id.add_recording_edit_text);
    }

    public void onNameRecordingClick(View view) {
        recordingNameEditText = renameRecordingView.findViewById(R.id.add_recording_edit_text);
        String recordingName = recordingNameEditText.getText().toString();
//        System.out.println(recordingName + "1234");

        if(!recordingName.equals("")){
            File sdcard = Environment.getExternalStorageDirectory();
            File from = new File(lastRecordingPath);
            File to = new File(lastRecordingPath.substring(0, lastRecordingPath.lastIndexOf("/")+1) + recordingName + outputFormat);

            System.out.println(from.renameTo(to));
            nameRecordingPopupWindow.dismiss();
        }
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        addBookmarkView = inflater.inflate(R.layout.add_bookmark_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        addBookmarkPopupWindow = new PopupWindow(addBookmarkView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        addBookmarkPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        addBookmarkView.setOnTouchListener((v, event) -> {
            addBookmarkPopupWindow.dismiss();
            return true;
        });

        bookmarkNameEditText = view.findViewById(R.id.add_bookmark_edit_text);
    }

    public void onAddBookmarkClick(View view) {
        bookmarkNameEditText = addBookmarkView.findViewById(R.id.add_bookmark_edit_text);
        String bookmarkName = bookmarkNameEditText.getText().toString();
        long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        bookmarksList.add(new Bookmark(elapsedTime, bookmarkName));
        addBookmarkPopupWindow.dismiss();
    }

    // ********************************* Inner Class from here *********************************
    public static class NotificationPanel {

        private Context parent;
        private RemoteViews remoteView;

        private NotificationManager nManager;
        private NotificationCompat.Builder nBuilder;

        NotificationCompat.Builder getNotificationBuilder() {
            return nBuilder;
        }

        RemoteViews getRemoteView() {
            return remoteView;
        }
        NotificationManager getNotificationManager() {
            return nManager;
        }

        NotificationPanel(Context parent) {
            // TODO Auto-generated constructor stub
            this.parent = parent;
            nManager = (NotificationManager) parent.getSystemService(NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(parent, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(parent, 0, notificationIntent, 0);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = parent.getString(R.string.app_name);
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setSound(null, null);
                assert nManager != null;
                nManager.createNotificationChannel(mChannel);
            }

            nBuilder = new NotificationCompat.Builder(parent)
                    .setContentTitle("Parking Meter")
                    .setSmallIcon(R.drawable.ic_record_button)
                    .setChannelId(CHANNEL_ID)
                    .setTicker(null)
                    .setSound(null)
                    .setContentIntent(intent)
                    .setOngoing(true);


            remoteView = new RemoteViews(parent.getPackageName(), R.layout.recording_controls_in_notification_area);

            //set the button listeners
            setListeners(remoteView);
            nBuilder.setContent(remoteView);

            nManager.notify(2, nBuilder.build());
        }

        void setListeners(RemoteViews view){

            Intent intentStop = new Intent(STOP_RECORDING_ACTION);
            Intent intentPauseOrResume = new Intent(PAUSE_OR_RESUME_RECORDING_ACTION);
            PendingIntent pendingSwitchIntentStop = PendingIntent.getBroadcast(parent, 0, intentStop, 0);
            PendingIntent pendingSwitchIntentPauseOrResume = PendingIntent.getBroadcast(parent, 0, intentPauseOrResume, 0);

            view.setOnClickPendingIntent(R.id.notification_stop_recording_button, pendingSwitchIntentStop);
            view.setOnClickPendingIntent(R.id.notification_pause_resume_button, pendingSwitchIntentPauseOrResume);
        }

        void notificationCancel() {
            nManager.cancel(2);
        }
    }

    public class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(STOP_RECORDING_ACTION)){
                try {
                    onStopClick(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(intent.getAction().equals(PAUSE_OR_RESUME_RECORDING_ACTION)){
                onRecordClick(null);
            }

            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
    }
}

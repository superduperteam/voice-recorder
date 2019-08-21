package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import android.widget.Toast;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;
import com.superduperteam.voicerecorder.voicerecorder.VisualizerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: 8/20/2019 saar: probably need to implement onClose (user might close the app while recording)
public class MainActivity extends BaseActivity {

    NotificationPanel nPanel;


    ActionReceiver actionReceiver;
   // private DrawerLayout drawerLayout;
    private Chronometer chronometer;
    private volatile boolean isRecording = false; // is actively recording something?
    private String lastRecordingPath = null;
    private String outputFormat = ".m4a"; // TODO: 7/18/2019  might wantto change to ".m4a"
    private List<Bookmark> bookmarksList;
    private ImageButton bookmarkImageButton;
    private EditText bookmarkNameEditText;
    private View addBookmarkView;
    private PopupWindow addBookmarkPopupWindow;
    private String STOP_RECORDING_ACTION = "stop";
    //voice recorder
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private MediaRecorder recorder = null;
//    private RecordingSampler recordingSampler;


    private Handler handler; // Handler for updating the visualizer
    com.superduperteam.voicerecorder.voicerecorder.VisualizerView visualizerView;
    public static final int REPEAT_INTERVAL = 40;


    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

   // boolean mStartRecording = true;

    //Todo: need to to make sure it checks all the checks (permissions and etc').
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawer.addView(contentView, 1);

        // Record to the external cache directory for visibility
//        fileName = getExternalCacheDir().getAbsolutePath();
//        fileName += "/audiorecordtest.3gp";
        bookmarkNameEditText = findViewById(R.id.add_bookmark_edit_text);
        bookmarkImageButton = findViewById(R.id.bookmark_image_button);
        bookmarkImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onButtonShowPopupWindowClick(getWindow().getDecorView());

                return true;
            }
        });

        isExternalStorageWritable();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        chronometerInit();


        visualizerView = (VisualizerView) findViewById(R.id.visualizer);
        handler = new Handler();

//        RecordingSampler recordingSampler = new RecordingSampler();
////        recordingSampler.setVolumeListener(this);  // for custom implements
//        recordingSampler.setSamplingInterval(100); // voice sampling interval
//        recordingSampler.link(VisualizerView);     // link to visualizer
//
//        recordingSampler.startRecording();


        actionReceiver = new ActionReceiver();
        registerReceiver(actionReceiver, new IntentFilter(STOP_RECORDING_ACTION));

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

//        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(Chronometer cArg) {
//                long time = SystemClock.elapsedRealtime() - cArg.getBase();
//                int h   = (int)(time /3600000);
//                int m = (int)(time - h*3600000)/60000;
//                int s= (int)(time - h*3600000- m*60000)/1000 ;
//                String hh = h < 10 ? "0"+h: h+"";
//                String mm = m < 10 ? "0"+m: m+"";
//                String ss = s < 10 ? "0"+s: s+"";
//                cArg.setText(hh+":"+mm+":"+ss);
//            }
//        });

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                chronometer = chronometerChanged;
            }
        });

    }

    private long lastPause;

    //private boolean isFirstStart = true;
    // Saar: changed some things (added stop option so a recording could be stopped and saved).
    // using isRunning instead of isFirstStart. isRunning as whether a recorder is even running or not.
    private boolean isRunning = false;

    public void onRecordClick(View view) {

        if (!isRecording) {
            //startStopWatch(isFirstStart);
            findViewById(R.id.record_button).setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);

            startStopWatch();
            startRecording(isRunning);
            nPanel = new NotificationPanel(this);
//            notificationTest();

            isRunning = true;
        } else {
            findViewById(R.id.record_button).setBackgroundResource(R.drawable.ic_record_button);
            pauseStopWatch();
            pauseRecording();
        }

        isRecording = !isRecording;
    }

    @Override
    public void onDestroy() {

        try{
            if(actionReceiver!=null)
                unregisterReceiver(actionReceiver);

        }catch(Exception e){}

        super.onDestroy();
    }

    public void onStopClick(View view) throws IOException {
        if(isRunning){
            resetStopWatch();
            findViewById(R.id.record_button).setBackgroundResource(R.drawable.ic_record_button);
            stopRecording();
            if(nPanel != null){
                nPanel.notificationCancel();
            }
            isRunning = false;
            isRecording = false;
            //stopStopWatch();
            //    stopRecording();
        }
    }
//    private void stopStopWatch() {
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        chronometer.stop();
//        lastPause = 0;
//    }
//    private void pauseStopWatch() {
//        lastPause = SystemClock.elapsedRealtime();
//        chronometer.stop();
//    }
//    private void startStopWatch(boolean isFirstStart) {
//        if (isFirstStart) {
//            chronometer.setBase(SystemClock.elapsedRealtime());
//        } else {
//            chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
//        }
//        chronometer.start();
//    }

private long pauseOffset = 0;

    private void startStopWatch() {
        //if (!isRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
       // }
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
        startPlaying();
    }

    private void pauseRecording() {
        recorder.pause();
    }

    private void startPlaying() throws IOException {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(lastRecordingPath);
            player.prepare();
            player.start();
            handler.post(updateVisualizer);
            System.out.print("duration: ");
            System.out.println(player.getDuration());
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        MetaDataRead cmd = new MetaDataRead();
        String text = cmd.read(lastRecordingPath);
        System.out.println(text +"!@#");
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

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
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
        addBookmarkView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                addBookmarkPopupWindow.dismiss();
                return true;
            }
        });

        bookmarkNameEditText = view.findViewById(R.id.add_bookmark_edit_text);
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
        private NotificationManager nManager;
        private NotificationCompat.Builder nBuilder;
        private RemoteViews remoteView;

        public NotificationPanel(Context parent) {
            // TODO Auto-generated constructor stub
            this.parent = parent;
            String CHANNEL_ID = "my_channel_01";
            nManager = (NotificationManager) parent.getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = parent.getString(R.string.app_name);
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setSound(null, null);
                assert nManager != null;
                nManager.createNotificationChannel(mChannel);
            }

//            Notification.Builder notiBuilder = new Notification.Builder(parent)
//                    .setSmallIcon(R.drawable.ic_record_button)
//                    .setOngoing(true);

            nBuilder = new NotificationCompat.Builder(parent)
                    .setContentTitle("Parking Meter")
                    .setSmallIcon(R.drawable.ic_record_button)
                    .setChannelId(CHANNEL_ID)
                    .setTicker(null)
                    .setSound(null)
                    .setOngoing(true);


            remoteView = new RemoteViews(parent.getPackageName(), R.layout.recording_controls_in_notification_area);

            //set the button listeners
            setListeners(remoteView);
            nBuilder.setContent(remoteView);
//            notiBuilder.setCustomContentView(remoteView);

            nManager.notify(2, nBuilder.build());
        }

        public void setListeners(RemoteViews view){

            Intent intent = new Intent("stop");
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(parent, 0, intent, 0);

            view.setOnClickPendingIntent(R.id.btn2, pendingSwitchIntent);

//
//
//
//            //listener 1
//            Intent volume = new Intent("volume");
//            volume.putExtra("DO", "volume");
//            PendingIntent btn1 = PendingIntent.getActivity(parent, 0, volume, 0);
//            view.setOnClickPendingIntent(R.id.btn1, btn1);
//
//            //listener 2
//            Intent stop = new Intent("stop");
//            stop.putExtra("DO", "stop");
//            PendingIntent btn2 = PendingIntent.getActivity(parent, 1, stop, 0);
//            view.setOnClickPendingIntent(R.id.btn2, btn2);
        }

        public void notificationCancel() {
            nManager.cancel(2);
        }
    }












    public class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

//            Toast.makeText(context,"recieved", Toast.LENGTH_SHORT).show();

            try {
                onStopClick(null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
    }





    // ********************************* Inner Class from here *********************************
//    public static class NotificationReturnSlot extends IntentService {
//        /**
//         * Creates an IntentService.  Invoked by your subclass's constructor.
//         *
//         * @param name Used to name the worker thread, important only for debugging.
//         */
//        public NotificationReturnSlot(String name) {
//            super(name);
//        }
//
//    //    @Override
//    //    protected void onCreate(Bundle savedInstanceState) {
//    //        // TODO Auto-generated method stub
//    //        super.onCreate(savedInstanceState);
//    //        String action = (String) getIntent().getExtras().get("DO");
//    //        if (action.equals("volume")) {
//    //            Log.i("NotificationReturnSlot", "volume");
//    //            //Your code
//    //        } else if (action.equals("stopNotification")) {
//    //            //Your code
//    //
//    //            Log.i("NotificationReturnSlot", "stopNotification");
//    //        }
//    //        finish();
//    //    }
//
//        @Override
//        protected void onHandleIntent(@Nullable Intent intent) {
//            String action = (String) intent.getExtras().get("DO");
//            if (action.equals("volume")) {
//                Log.i("NotificationReturnSlot", "volume");
//                //Your code
//            } else if (action.equals("stopNotification")) {
//                //Your code
//
//                Log.i("NotificationReturnSlot", "stopNotification");
//            }
//        }
//    }
}

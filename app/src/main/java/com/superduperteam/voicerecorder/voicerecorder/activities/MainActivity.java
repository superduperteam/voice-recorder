package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.Manifest;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;

import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.media.MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION;

public class MainActivity extends BaseActivity {

   // private DrawerLayout drawerLayout;
    private Chronometer chronometer;
    //private boolean isStart;
    private boolean isRecording = false; // is actively recording something?
    private String lastRecordingPath = null;
    private String outputFormat = ".m4a"; // TODO: 7/18/2019  might wantto change to ".m4a"
    private int recordingNum = 0;
    private List<Bookmark> bookmarksList;

    //voice recorder
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String defaultFileName = "Recording";

    private MediaRecorder recorder = null;

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

        isExternalStorageWritable();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        chronometerInit();
    }

//    private String getCurrentFileName() {
//        isExternalStorageWritable();
//        fileName = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath();
//        fileName += "/recording" + recordingNum + outputFormat;
//        Log.i(LOG_TAG,  "filePath for recording: " + fileName);
//
//        return fileName;

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
            findViewById(R.id.record_button).setBackgroundResource(R.drawable.ic_pressed_record_button);

            startStopWatch();
            startRecording(isRunning);
            isRunning = true;
        } else {
            findViewById(R.id.record_button).setBackgroundResource(R.drawable.ic_record_button);
            pauseStopWatch();
            pauseRecording();
        }

        isRecording = !isRecording;
    }

    public void onStopClick(View view) throws IOException {
        if(isRunning){
            resetStopWatch();
            findViewById(R.id.record_button).setBackgroundResource(R.drawable.ic_record_button);
            stopRecording();
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
        recordingNum++;

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
    }

    private void startRecording(boolean isRunning) {
        if(!isRunning){
            recorderInit();
        }
        else{
            recorder.resume();
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
            bookmarksList.add(new Bookmark(elapsedTime,"test"));
        }
    }

    private void addBookmarksToMetadata() throws IOException {
        MetaDataInsert cmd = new MetaDataInsert();
        cmd.writeRandomMetadata(lastRecordingPath, bookmarksList.toString());
    }
}

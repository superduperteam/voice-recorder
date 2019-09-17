package com.superduperteam.voicerecorder.voicerecorder.Activities.RecordingPlayerActivity;

import android.content.Context;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.superduperteam.voicerecorder.voicerecorder.Activities.BaseActivity.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;
import com.superduperteam.voicerecorder.voicerecorder.Visualizer.VisualizerView;
import com.superduperteam.voicerecorder.voicerecorder.Interfaces.BookmarkClickedListener;
import com.superduperteam.voicerecorder.voicerecorder.Adapters.BookmarksRecyclerViewAdapter;
import com.superduperteam.voicerecorder.voicerecorder.Model.Recording;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RecordingPlayerActivity extends BaseActivity implements BookmarkClickedListener {
    private Recording recording;
    private float speed = 1f;
    private static final String LOG_TAG = "AudioRecordTest";
    private ImageButton playButton;
    volatile SeekBar seekBar;
    private CircleLineVisualizer mVisualizer;
    volatile MediaPlayer mediaPlayer = new MediaPlayer();
    boolean wasPlaying = false;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_recording_player, null, false);
        mDrawer.addView(contentView, 0);
        Intent intent = getIntent();
        String pathToFileToPlay = intent.getStringExtra("fileToPlayPath");
        String timestampStartStr = intent.getStringExtra("startTimestamp");
        int timestampStart = 0;

        if(timestampStartStr != null){
            timestampStart = Integer.valueOf(timestampStartStr);
        }

        File file = new File(pathToFileToPlay);

        try {
            this.recording = new Recording(file.getName(),file, Recording.fetchBookmarks(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.bookmarksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        BookmarksRecyclerViewAdapter adapter = new BookmarksRecyclerViewAdapter(this, new ArrayList<>(recording.getBookmarksList()), recyclerView, this);
        recyclerView.setAdapter(adapter);
        final TextView seekBarHint = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekbar);
        seekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        seekBar.setProgress(timestampStart);
        playButton = findViewById(R.id.recordingPlayerPlay);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                    seekBarHint.setVisibility(View.VISIBLE);

                seekBarHint.setText(convertMilliSecondsToRecordingTime((int) Math.ceil(progress)));

                    double percent = progress / (double) seekBar.getMax();
                    int offset = seekBar.getThumbOffset();
                    int seekWidth = seekBar.getWidth();
                    int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                    int labelWidth = seekBarHint.getWidth();
                    seekBarHint.setX(offset + seekBar.getX() + val - Math.round(percent * offset) - Math.round(percent * labelWidth / 2));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        //get reference to visualizer
        mVisualizer = findViewById(R.id.circle_line_visualizer);
        playRecording(timestampStart);
    }


    private String convertMilliSecondsToRecordingTime(long milliseconds) {
        String minutes = Long.toString(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        String seconds = Long.toString(TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        if(TimeUnit.MILLISECONDS.toMinutes(milliseconds) < 10) {
            minutes = "0" + minutes;
        }

        if(TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)) < 10) {
            seconds = "0" + seconds;
        }
        System.out.print("Minutes and seconds: ");
        System.out.println(minutes);
        System.out.println(seconds);
        return String.format("%s:%s", minutes, seconds);
    }

    public void onPlayButtonClick(View v){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
            mHandler.removeCallbacks(mUpdateSeekbar);
        }
        else{
            mediaPlayer.start();
            playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
            mHandler.postDelayed(mUpdateSeekbar, 0);
        }
    }

    public void playRecording(int timestampStart) {
        try {
            if (!wasPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                mediaPlayer.setDataSource(recording.getFile().getPath());
                mediaPlayer.setOnCompletionListener(MediaPlayer::start);
                mediaPlayer.prepare();
                TextView durationTextView = findViewById(R.id.recording_duration_in_recordings_player);
                mediaPlayer.start();
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(recording.getFile().getPath());
                String recordingDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                durationTextView.setText(convertMilliSecondsToRecordingTime(Long.valueOf(recordingDuration)));

                //TODO: init MediaPlayer and play the audio

                //get the AudioSessionId from your MediaPlayer and pass it to the visualizer
                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1){
                    mVisualizer.setAudioSessionId(audioSessionId);
                }

                mediaPlayer.seekTo(timestampStart);
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());
                mHandler.postDelayed(mUpdateSeekbar, 0);
            }

            wasPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            if(seekBar != null && mediaPlayer != null){
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                mHandler.postDelayed(this, 50);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        mHandler.removeCallbacks(mUpdateSeekbar);

        if (mVisualizer != null)
            mVisualizer.release();
    }

    @Override
    public void OnClick(int time) {
        if (mediaPlayer != null) {
            seekBar.setProgress(time);
            mediaPlayer.seekTo(time);
        }
    }

    public void onSpeedUpClick(View view) {
        speed = Math.min(speed + 0.2f,2f);
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
        Toast.makeText(getApplicationContext(),"Speed: "+ String.valueOf((float)Math.round(speed * 100000d) / 100000d).substring(0,String.valueOf(speed).indexOf(".")+2), Toast.LENGTH_SHORT).show();
    }

    public void onSpeedDownClick(View view) {
        speed = Math.max(speed - 0.2f,0.2f);
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
        Toast.makeText(getApplicationContext(), "Speed: "+ String.valueOf((float)Math.round(speed * 100000d) / 100000d).substring(0,String.valueOf(speed).indexOf(".")+2), Toast.LENGTH_SHORT).show();
    }
}

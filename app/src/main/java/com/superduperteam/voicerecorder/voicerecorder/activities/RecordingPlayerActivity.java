package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.content.Context;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecordingPlayerActivity extends BaseActivity implements BookmarkClickedListener {
    private Recording recording;
    private static final String LOG_TAG = "AudioRecordTest";
    private ImageButton playButton;
    volatile SeekBar seekBar;
    FloatingActionButton fab;
    private BookmarksRecyclerViewAdapter adapter;
    volatile MediaPlayer mediaPlayer = new MediaPlayer();
    boolean wasPlaying = false;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_recording_player);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);








        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_recording_player, null, false);
        mDrawer.addView(contentView, 0);
        Intent intent = getIntent();
        String pathToFileToPlay = intent.getStringExtra("fileToPlayPath");
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
        adapter = new BookmarksRecyclerViewAdapter(this, new ArrayList<>(recording.getBookmarksList()), recyclerView, this);
        recyclerView.setAdapter(adapter);






        final TextView seekBarHint = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekbar);
        seekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        playButton = findViewById(R.id.recordingPlayerPlay);
//        playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);


//        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
//            mediaPlayer.reset();
//            playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
//        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                    seekBarHint.setVisibility(View.VISIBLE);
                    int x = (int) Math.ceil(progress / 1000f);

                    if (x < 10)
                        seekBarHint.setText("0:0" + x);
                    else
                        seekBarHint.setText("0:" + x);

                    double percent = progress / (double) seekBar.getMax();
                    int offset = seekBar.getThumbOffset();
                    int seekWidth = seekBar.getWidth();
                    int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                    int labelWidth = seekBarHint.getWidth();
                    seekBarHint.setX(offset + seekBar.getX() + val - Math.round(percent * offset) - Math.round(percent * labelWidth / 2));
//                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
////                    clearMediaPlayer();
////                    fab.setImageDrawable(ContextCompat.getDrawable(RecordingPlayerActivity.this, android.R.drawable.ic_media_play));
//                    RecordingPlayerActivity.this.seekBar.setProgress(0);
//                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        playRecording();
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

    public void playRecording() {
        try {
//            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                pauseMediaPlayer();
//                seekBar.setProgress(0);
//                wasPlaying = true;
//                playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
//            }
            if (!wasPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                mediaPlayer.setDataSource(recording.getFile().getPath());
                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    mediaPlayer.start();
                });
                mediaPlayer.prepare();
//                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.start();
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
    }

    @Override
    public void OnClick(int time) {
        if (mediaPlayer != null) {
            seekBar.setProgress(time);
            mediaPlayer.seekTo(time);
        }
    }

//    private void pauseMediaPlayer() {
//        mediaPlayer.pause();
//    }


//    class seekBarUpdater implements Runnable{
//        @Override
//        public void run() {
//                int currentPosition = mediaPlayer.getCurrentPosition();
//                int total = mediaPlayer.getDuration();
//
//                while (mediaPlayer != null && currentPosition < total) {
//                    try {
//                        currentPosition = mediaPlayer.getCurrentPosition();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                synchronized (seekBar){
//                    seekBar.setProgress(currentPosition);
//                }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//        }
//    }

//    private Handler mHandler = new Handler();
////Make sure you update Seekbar on UI thread
//    RecordingPlayerActivity.this.runOnUiThread(new Runnable() {
//
//        @Override
//        public void run() {
//            if(mMediaPlayer != null){
//                int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
//                mSeekBar.setProgress(mCurrentPosition);
//            }
//            mHandler.postDelayed(this, 1000);
//        }
//    });



//    private void startPlaying() throws IOException {
//        try {
//            player.setDataSource(recording.getFile().getPath());
//            player.prepare();
//            player.start();
//            System.out.print("duration: ");
//            System.out.println(player.getDuration());
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//
//
//        System.out.println(recording.getBookmarksList());
//    }

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

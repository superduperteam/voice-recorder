package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


// Saar: took this from:
// https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private static final String LOG_TAG = "AudioRecordTest";
    private List<Line> mRecordings;
    Context context;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private MediaMetadataRetriever mmr;
    private MediaPlayer player = new MediaPlayer();
    private int recordingToResumePosition = -1; //-1 means not recording was paused and should be resumed
    private boolean shouldSetDataSource = true;
    private ImageButton lastPlayed;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Line> recordings) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mRecordings = recordings;
        mmr = new MediaMetadataRetriever(); //used to get duration of recording. much lighter than MediaPlayer
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recording_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String recordingName = mRecordings.get(position).getName();
        String recordingDuration;
        long recordingDurationMilliseconds;
        String recordingDate;

        mmr.setDataSource(mRecordings.get(position).getFile().getPath());
        recordingDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        recordingDurationMilliseconds = Long.parseLong(recordingDuration);
        recordingDuration = convertMilliSecondsToRecordingTime(recordingDurationMilliseconds);
        recordingDate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        recordingDate = extractDateFromMetaDataDate(recordingDate);

        holder.recordingDate.setText(recordingDate);
        holder.recordingDurationTextView.setText(recordingDuration);
        holder.recordingNameTextView.setText(recordingName);
    }

    private final static int YEAR_START_INDEX = 0;
    private final static int YEAR_END_INDEX = 4;
    private final static int MONTH_START_INDEX = 4;
    private final static int MONTH_END_INDEX = 6;
    private final static int DAY_START_INDEX = 6;
    private final static int DAY_END_INDEX = 8;
    //meta data date structure: 20190430T192392329
    //we want what is before T (what comes after T is timestamp)
    private String extractDateFromMetaDataDate(String metaDataDate) {
        String unFormattedDate = metaDataDate.split("T")[0];
        String day = unFormattedDate.substring(DAY_START_INDEX, DAY_END_INDEX);
        String month = unFormattedDate.substring(MONTH_START_INDEX, MONTH_END_INDEX);
        String year = unFormattedDate.substring(YEAR_START_INDEX, YEAR_END_INDEX);

        String date = new StringBuilder()
                .append(day).append("/")
                .append(month).append("/")
                .append(year).toString();

        return date;
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
        return String.format("%s:%s",
               minutes, seconds
        );
    }
    // total number of rows
    @Override
    public int getItemCount() {
        return mRecordings.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView recordingNameTextView;
        TextView recordingDurationTextView;
        TextView recordingDate;
        ImageButton playButton;

        ViewHolder(View itemView) {
            super(itemView);
            recordingNameTextView = itemView.findViewById(R.id.recordingTitle);
            recordingDurationTextView = itemView.findViewById(R.id.recordingDuration);
            recordingDate = itemView.findViewById(R.id.recordingDate);
            playButton = itemView.findViewById(R.id.recordingsPlayImagePlace);

            // Saar: moved this into: playButton.setOnClickListener(..} below, to make pause symbol return to play on finish.
//            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    mediaPlayer.reset();
//                    recordingToResumePosition = -1;
//                    shouldSetDataSource = true;
//                    playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
//                }
//            });

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    File fileToPlay = getItem(position).getFile();

                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.reset();
                            recordingToResumePosition = -1;
                            shouldSetDataSource = true;
                            playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
                        }
                    });

                    if(player.isPlaying() && recordingToResumePosition != position) {
                        player.reset();
                        recordingToResumePosition = -1;
                        shouldSetDataSource = true;
                        playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
                        lastPlayed.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
                    }
                    else if(player.isPlaying() && recordingToResumePosition == position) {
                        player.pause();
                        System.out.println("paused");
                        shouldSetDataSource = false;
                        playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
                    }
                    else {
                        try {
                            System.out.println("going to start..");
                            recordingToResumePosition = position;
                            if(shouldSetDataSource) {
                                player.setDataSource(fileToPlay.getAbsolutePath());
                                player.prepare();
                            }
                            player.start();
                            lastPlayed = playButton;
                            playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                            System.out.print("duration: ");
                            System.out.println(player.getDuration());
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "prepare() failed");
                        }
                    }
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
           // if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            int position = getAdapterPosition();
            File fileToPlay = getItem(position).getFile();

            Intent intent = new Intent(context, RecordingPlayerActivity.class);
            intent.putExtra("fileToPlayPath", fileToPlay.getAbsolutePath());
            context.startActivity(intent);
        }
    }

    public void updateList(List<Line> newList){
        mRecordings = new ArrayList<>();
        mRecordings.addAll(newList);
        notifyDataSetChanged();

    }

    // convenience method for getting data at click position
    Line getItem(int id) {
        return mRecordings.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

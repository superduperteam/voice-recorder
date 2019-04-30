package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.R;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;


// Saar: took this from:
// https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<File> mRecordings;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private MediaMetadataRetriever mmr;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<File> recordings) {
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

        mmr.setDataSource(mRecordings.get(position).getPath());
        recordingDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        recordingDurationMilliseconds = Long.parseLong(recordingDuration);
        recordingDuration = convertMilliSecondsToRecordingTime(recordingDurationMilliseconds);

        holder.recordingDurationTextView.setText(recordingDuration);
        holder.recordingNameTextView.setText(recordingName);
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

        ViewHolder(View itemView) {
            super(itemView);
            recordingNameTextView = itemView.findViewById(R.id.recordingTitle);
            recordingDurationTextView = itemView.findViewById(R.id.recordingDuration);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    File getItem(int id) {
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

package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


// Saar: took this from:
// https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
//RecyclerView.Adapter<BookmarksRecyclerViewAdapter.ViewHolder>
//RecordingViewHolder
public class BookmarksRecyclerViewAdapter extends RecyclerView.Adapter<RecordingsAdapter.BookmarkViewHolder> {
    private static final String LOG_TAG = "AudioRecordTest";
    private final RecyclerView rv;
    private List<Bookmark> bookmarks;
    Context context;
    private LayoutInflater mInflater;
//    private ItemClickListener mClickListener;
    private MediaMetadataRetriever mmr;
    private MediaPlayer player = new MediaPlayer();
    private int recordingToResumePosition = -1; //-1 means not recording was paused and should be resumed
    private boolean shouldSetDataSource = true;
    private ImageButton lastPlayed;
    private View.OnClickListener mOnClickListener;
    private BookmarkClickedListener bookmarkClickedListener;

    // data is passed into the constructor
    BookmarksRecyclerViewAdapter(Context context, List<Bookmark> bookmarks, RecyclerView recyclerView, BookmarkClickedListener listener) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.bookmarks = bookmarks;
        this.rv = recyclerView;
        this.mOnClickListener = new MyOnClickListener();
        this.bookmarkClickedListener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public RecordingsAdapter.BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.bookmark_row, parent, false);
        RecordingsAdapter.BookmarkViewHolder bookmarkViewHolder = new RecordingsAdapter.BookmarkViewHolder(view);
        view.setOnClickListener(mOnClickListener);
        return bookmarkViewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecordingsAdapter.BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);

        holder.setBookmarkText(bookmark.getTitle());
        holder.setTimeText(convertMilliSecondsToRecordingTime(bookmark.getTimestamp()));

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
        return bookmarks.size();
    }




//    // stores and recycles views as they are scrolled off screen
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        TextView recordingNameTextView;
//        TextView recordingDurationTextView;
//        TextView recordingDate;
//        ImageButton playButton;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            recordingNameTextView = itemView.findViewById(R.id.recordingTitle);
//            recordingDurationTextView = itemView.findViewById(R.id.recordingDuration);
//            recordingDate = itemView.findViewById(R.id.recordingDate);
//            playButton = itemView.findViewById(R.id.recordingsPlayImagePlace);
//
//            // Saar: moved this into: playButton.setOnClickListener(..} below, to make pause symbol return to play on finish.
////            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
////                @Override
////                public void onCompletion(MediaPlayer mediaPlayer) {
////                    mediaPlayer.reset();
////                    recordingToResumePosition = -1;
////                    shouldSetDataSource = true;
////                    playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
////                }
////            });
//
//            playButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition();
//                    Bookmark bookmark = (Bookmark)(getItem(position));
//
//                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            mediaPlayer.reset();
//                            recordingToResumePosition = -1;
//                            shouldSetDataSource = true;
//                            playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
//                        }
//                    });
//
//                    if(player.isPlaying() && recordingToResumePosition != position) {
//                        player.reset();
//                        recordingToResumePosition = -1;
//                        shouldSetDataSource = true;
//                        playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
//                        lastPlayed.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
//                    }
//                    else if(player.isPlaying() && recordingToResumePosition == position) {
//                        player.pause();
//                        System.out.println("paused");
//                        shouldSetDataSource = false;
//                        playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
//                    }
//                    else {
//                        try {
//                            System.out.println("going to start..");
//                            recordingToResumePosition = position;
//                            if(shouldSetDataSource) {
//                                player.setDataSource(fileToPlay.getAbsolutePath());
//                                player.prepare();
//                            }
//                            player.start();
//                            lastPlayed = playButton;
//                            playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
//                            System.out.print("duration: ");
//                            System.out.println(player.getDuration());
//                        } catch (IOException e) {
//                            Log.e(LOG_TAG, "prepare() failed");
//                        }
//                    }
//                }
//            });
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//           // if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
//            int position = getAdapterPosition();
//            File fileToPlay = getItem(position).getFile();
//
//            Intent intent = new Intent(context, RecordingPlayerActivity.class);
//            intent.putExtra("fileToPlayPath", fileToPlay.getAbsolutePath());
//            context.startActivity(intent);
//        }
//    }

    public void updateList(List<Bookmark> newList){
        bookmarks = new ArrayList<>();
        bookmarks.addAll(newList);
        notifyDataSetChanged();
    }

    // convenience method for getting data at click position
    Bookmark getItem(int id) {
        return bookmarks.get(id);
    }

    // allows clicks events to be caught
//    void setClickListener(ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = rv.getChildLayoutPosition(v);
            Bookmark bookmarkClicked = bookmarks.get(itemPosition);
//            String time = convertMilliSecondsToRecordingTime(bookmarkClicked.getTimestamp());
            Integer timestamp = Integer.getInteger(bookmarkClicked.getTimestamp().toString());

            bookmarkClickedListener.OnClick(bookmarkClicked.getTimestamp().intValue());
        }
    }
}

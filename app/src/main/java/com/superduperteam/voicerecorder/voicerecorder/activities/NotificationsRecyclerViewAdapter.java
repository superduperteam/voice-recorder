package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.goncalves.pugnotification.notification.PugNotification;

// TODO: 9/3/2019 saar: need to add notifications from channels and present them with the adapter
public class NotificationsRecyclerViewAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewAdapter.NotificationViewHolder> {
    private static final String LOG_TAG = "AudioRecordTest";
    private final RecyclerView rv;
    private List<NotificationChannel> notifications;
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
    NotificationsRecyclerViewAdapter(Context context, List<NotificationChannel> notifications, RecyclerView recyclerView, BookmarkClickedListener listener) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.notifications = notifications;
        this.rv = recyclerView;
        this.mOnClickListener = new MyOnClickListener();
        this.bookmarkClickedListener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.bookmark_row, parent, false);
        NotificationViewHolder bookmarkViewHolder = new NotificationViewHolder(view);
        view.setOnClickListener(mOnClickListener);
        return bookmarkViewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        NotificationChannel notification = notifications.get(position);
//        holder.setDayText(notification.getDescription());
//        holder.setBookmarkText(notification.getName());
//        holder.setTimeText(convertMilliSecondsToRecordingTime(bookmark.getTimestamp()));


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return notifications.size();
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

    public void updateList(List<NotificationChannel> newList){
        notifications = new ArrayList<>();
        notifications.addAll(newList);
        notifyDataSetChanged();
    }

    // convenience method for getting data at click position
    NotificationChannel getItem(int id) {
        return notifications.get(id);
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
            NotificationChannel notificationClicked = notifications.get(itemPosition);
//            String time = convertMilliSecondsToRecordingTime(bookmarkClicked.getTimestamp());

//            bookmarkClickedListener.OnClick(notificationClicked.getTimestamp().intValue());
        }
    }

    static class NotificationViewHolder extends ChildViewHolder {
        private final TextView dayTextView;
        private final TextView timeViewTime;
        private TextView textViewTime;
        private OnChildClickListener listener;


        public NotificationViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.notification_day);
            timeViewTime = itemView.findViewById(R.id.notification_time_in_day);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int groupPosition; // TODO: 9/1/2019 saar: need to find a way to get the his parent group
//                        listener.onChildClick();
                    }
                }
            });
        }

//        public Bookmark getBookmark() {
//            return bookmark;
//        }
//
//        public void setBookmark(Bookmark bookmark) {
//            this.bookmark = bookmark;
//        }


        public void setListener(OnChildClickListener listener) {
            this.listener = listener;
        }

        public void setDayText(Spannable name) {
            dayTextView.setText(name);
        }

        public void setTimeText(String time) {
            this.textViewTime.setText(time);
        }
    }
}

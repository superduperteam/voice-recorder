package com.superduperteam.voicerecorder.voicerecorder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.Model.Bookmark;
import com.superduperteam.voicerecorder.voicerecorder.Interfaces.OnChildClickListener;
import com.superduperteam.voicerecorder.voicerecorder.Model.Recording;
import com.superduperteam.voicerecorder.voicerecorder.R;
import com.superduperteam.voicerecorder.voicerecorder.Activities.RecordingPlayerActivity.RecordingPlayerActivity;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecordingsAdapter extends CustomExpandableRecyclerViewAdapter<RecordingsAdapter.RecordingViewHolder, RecordingsAdapter.BookmarkViewHolder> implements OnChildClickListener {
    private Context context;
    private RecyclerView rv;
    private static final String LOG_TAG = "AudioRecordTest";
    private MediaMetadataRetriever mmr;
    private MediaPlayer player = new MediaPlayer();
    private int recordingToResumePosition = -1; //-1 means not recording was paused and should be resumed
    private boolean shouldSetDataSource = true;
    private ImageButton lastPlayed;

    public RecordingsAdapter(Context context, List<? extends ExpandableGroup> recordingGroups, RecyclerView rv) {
    super(recordingGroups);
    this.context = context;
    this.rv = rv;
    mmr = new MediaMetadataRetriever(); //used to get duration of recording. much lighter than MediaPlayer
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    switch (viewType) {
      case ExpandableListPosition.GROUP:
        RecordingViewHolder recordingViewHolder = onCreateGroupViewHolder(parent, viewType);
        recordingViewHolder.setOnGroupClickListener(this);
        return recordingViewHolder;
      case ExpandableListPosition.CHILD:
          return onCreateChildViewHolder(parent, viewType);
      default:
        throw new IllegalArgumentException("viewType is not valid");
    }
  }

  @Override
  public RecordingViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.recording_row, parent, false);
    return new RecordingViewHolder(view);
  }

  @Override
  public BookmarkViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.bookmark_row, parent, false);
      BookmarkViewHolder bookmarkViewHolder = new BookmarkViewHolder(view);

      bookmarkViewHolder.setListener(this);
      return bookmarkViewHolder;
  }

  @Override
  public void onBindChildViewHolder(BookmarkViewHolder holder, int flatPosition,
                                    ExpandableGroup group, int childIndex) {

        final Bookmark bookmark = ((Recording) group).getItems().get(childIndex);
        holder.setBookmarkText(bookmark.getTitle());
        holder.setTimeText(convertMilliSecondsToRecordingTime(bookmark.getTimestamp()));
        holder.setBookmark(bookmark);
  }

  @Override
  public void onBindGroupViewHolder(RecordingViewHolder holder, int flatPosition, ExpandableGroup group) {

        if(getItem(flatPosition) != null){
            String recordingName = getItem(flatPosition).getTitle();
            String recordingDuration;
            long recordingDurationMilliseconds;
            String recordingDate;

            mmr.setDataSource(getItem(flatPosition).getFile().getPath());
            recordingDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            recordingDurationMilliseconds = Long.parseLong(recordingDuration);
            recordingDuration = convertMilliSecondsToRecordingTime(recordingDurationMilliseconds);
            recordingDate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            recordingDate = extractDateFromMetaDataDate(recordingDate);

            holder.recordingDate.setText(recordingDate);
            holder.recordingDurationTextView.setText(recordingDuration);
            holder.recordingNameTextView.setText(recordingName);
            holder.recordingBookmarksCount.setText(String.valueOf(group.getItemCount()));

            holder.setRecordingTitle(group);

        }
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

        return day + "/" +
                month + "/" +
                year;
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

    public void updateList(List<Recording> groups) {
        ((List<Recording>)getGroups()).clear();
        ((List<Recording>)getGroups()).addAll(groups);
        notifyGroupDataChanged();
        notifyDataSetChanged();
    }

    private void notifyGroupDataChanged() {
        expandableList.expandedGroupIndexes = new boolean[getGroups().size()];
        for (int i = 0; i < getGroups().size(); i++) {
            expandableList.expandedGroupIndexes[i] = false;
        }
    }

  @Override
  public boolean onGroupClick(int flatPos) {

    return true;
  }

  private Recording getItem(int position){
        if(expandableList.groups.size() > position){
            return (Recording)(expandableList.groups.get(position));
        }
        return null;
  }

    private void createIntentForRecordingPlayer(int startTimestamp, Recording recording) {

        if(recording != null){
            File fileToPlay = recording.getFile();
            Intent intent = new Intent(context, RecordingPlayerActivity.class);
            intent.putExtra("fileToPlayPath", fileToPlay.getAbsolutePath());
            intent.putExtra("startTimestamp", String.valueOf(startTimestamp));
            context.startActivity(intent);
        }
    }

    @Override
    public void onChildClick(Bookmark bookmark) {
        if(bookmark != null) {

            for (ExpandableGroup group : getGroups()) {
                if (group instanceof Recording) {
                    for (Object currBookmark : group.getItems()) {
                        if (currBookmark == bookmark) {
                            File file = ((Recording) group).getFile();
                            createIntentForRecordingPlayer(bookmark.getTimestamp().intValue(), (Recording) group);
                        }
                    }
                }
            }
        }
    }

    public class RecordingViewHolder extends GroupViewHolder {
      TextView recordingDate;
      TextView recordingNameTextView;
      TextView recordingDurationTextView;
      TextView recordingBookmarksCount;
      ImageButton playButton;

      RecordingViewHolder(View itemView) {
          super(itemView);
          recordingNameTextView = itemView.findViewById(R.id.recordingTitle);
          recordingDurationTextView = itemView.findViewById(R.id.recordingDuration);
          recordingDate = itemView.findViewById(R.id.recordingDate);
          recordingBookmarksCount = itemView.findViewById(R.id.recordingBookmarksCount2);
          playButton = itemView.findViewById(R.id.recordingsPlayImagePlace);
          ImageButton bookmarksExpandButton = itemView.findViewById(R.id.recordingBookmarksPlace);
          bookmarksExpandButton.setOnClickListener(this);
          bookmarksExpandButton.setOnClickListener(v -> {
            int position = getAdapterPosition();
            toggleGroup(position);
          });

          playButton.setOnClickListener(view -> {
              int position = getAdapterPosition();
              File fileToPlay = getItem(position).getFile();

              player.setOnCompletionListener(mediaPlayer -> {
                  mediaPlayer.reset();
                  recordingToResumePosition = -1;
                  shouldSetDataSource = true;
                  playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);
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
          });
          itemView.setOnClickListener(this);
      }

      @Override
      public void onClick(View v) {
          player.reset();
          recordingToResumePosition = -1;
          shouldSetDataSource = true;
          playButton.setBackgroundResource(R.drawable.ic_play_arrow_triangle_alt1);

          Recording recording = getItem(getAdapterPosition());
          createIntentForRecordingPlayer(0, recording);
      }


        @Override
      public void setOnGroupClickListener(OnGroupClickListener listener) {
        }

      void setRecordingTitle(ExpandableGroup recording) {
              recordingNameTextView.setText(recording.getTitle());
      }
  }


    static class BookmarkViewHolder extends ChildViewHolder {
        private TextView childTextView;
        private TextView textViewTime;
        private OnChildClickListener listener;

        private Bookmark bookmark;

        BookmarkViewHolder(View itemView) {
            super(itemView);
            childTextView = itemView.findViewById(R.id.bookmark_name);
            textViewTime = itemView.findViewById(R.id.bookmark_time);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChildClick(bookmark);
                }
            });
        }

        void setBookmark(Bookmark bookmark) {
            this.bookmark = bookmark;
        }

        void setListener(OnChildClickListener listener) {
            this.listener = listener;
        }

        void setBookmarkText(Spannable name) {
            childTextView.setText(name);
        }

        void setTimeText(String time) {
            this.textViewTime.setText(time);
        }
    }
}

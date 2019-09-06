package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.R;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.io.File;
import java.io.IOException;
import java.security.acl.Group;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RecordingsAdapter extends CustomExpandableRecyclerViewAdapter<RecordingsAdapter.RecordingViewHolder, RecordingsAdapter.BookmarkViewHolder> implements OnChildClickListener {
    private View.OnClickListener bookmarkClickListener;
    private Context context;
    private RecyclerView rv;
    private static final String LOG_TAG = "AudioRecordTest";
    private ExpandableList recordings;
    private LayoutInflater mInflater;
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
        BookmarkViewHolder bookmarkViewHolder = onCreateChildViewHolder(parent, viewType);
        return bookmarkViewHolder;
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
//      holder.itemView.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
////              Bookmark bookmark = ((Recording) group).getItems().get(childPosition);
////              createIntentForRecordingPlayer(bookmark.getTimestamp().intValue(), groupPosition);
//          }
//      });
  }

  @Override
  public void onBindGroupViewHolder(RecordingViewHolder holder, int flatPosition, ExpandableGroup group) {

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

    public void updateList(List<Recording> groups) {
        ((List<Recording>)getGroups()).clear();
        ((List<Recording>)getGroups()).addAll(groups);
        notifyGroupDataChanged();
        notifyDataSetChanged();

//        expandAll();
 //       expandRecordingGroupsThatShouldBeExpanded();
//        notifyGroupDataChanged();
    }


    private void expandRecordingGroupsThatShouldBeExpanded() {
//        togglGroupsThatShouldBeExpanded1();

//        for(ExpandableGroup recordingGroup : getGroups()){
//            if(((Recording) recordingGroup).isShouldExpand()){
//                if(!isGroupExpanded(recordingGroup)){
//                    boolean isExpanded = toggleGroup(recordingGroup);
//                    System.out.println(isExpanded);
//                }
//            }
//        }
//        for(int i=0; i<getGroups().size(); i++){
//            Recording currRecording = (Recording)getGroups().get(i);
//            if(currRecording.isShouldExpand()){
//                if(!isGroupExpanded(getGroups().get(i))){
////                    boolean isExpanded = toggleGroup(getGroups().get(i));
////                    System.out.println(isExpanded);
//
//                }
//
//            }
//        }
//        for(int i=0; i<getGroups().size();i++){
//
//        }
    }

    public void notifyGroupDataChanged() {
        expandableList.expandedGroupIndexes = new boolean[getGroups().size()];
        for (int i = 0; i < getGroups().size(); i++) {
            expandableList.expandedGroupIndexes[i] = false;
        }
    }

    public void togglGroupsThatShouldBeExpanded1() {
        for (int i = 0; i < getGroups().size(); i++) {
            toggleGroup(getFlattenedGroupPosition(i));
        }
    }

    public int getFlattenedGroupPosition(int groupIndex) {
        int runningTotal = 0;
        for (int i = 0; i < groupIndex; i++) {
            runningTotal += numberOfVisibleItemsInGroup(i);
        }
        return runningTotal;
    }

    private int numberOfVisibleItemsInGroup(int group) {
        boolean[] clone = expandableList.expandedGroupIndexes.clone();
        if (clone[group]) {
            return expandableList.groups.get(group).getItemCount() + 1;
        } else {
            return 1;
        }
    }

//    public void expandAll() {
//        for (int i = 0; i < getItemCount(); i++) {
//            if (!isGroupExpanded(i)){
//                toggleGroup(i);
//            }
//        }
//    }

//    public void updateList(List<Recording> newList){
//        this.expandableList = new ExpandableList(newList);
//        notifyDataSetChanged();
//    }

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

//    @Override
//    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//        ExpandableGroup group = expandableList.groups.get(groupPosition);
//        Bookmark bookmark = ((Recording) group).getItems().get(childPosition);
//        createIntentForRecordingPlayer(bookmark.getTimestamp().intValue(), groupPosition);
//        return true;
//    }


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


//        ExpandableGroup group = getFlattenedGroupPosition();
//
//        Bookmark bookmark = ((Recording) group).getItems().get(childPosition);
//        createIntentForRecordingPlayer(bookmark.getTimestamp().intValue(), groupPosition);
//        createIntentForRecordingPlayer();

//        getItem()
    }


//    private int getChildItemIndex(int index){
//
//    }

    ////////////////// INNER CLASS /////////////////
    public class RecordingViewHolder extends GroupViewHolder {
      TextView recordingDate;
      TextView recordingNameTextView;
      TextView recordingDurationTextView;
      TextView recordingBookmarksCount;
      ImageButton playButton;
      private ImageButton bookmarksExpandButton;
      private OnGroupClickListener listener;
     // private ImageView icon;

      public RecordingViewHolder(View itemView) {
          super(itemView);
          recordingNameTextView = itemView.findViewById(R.id.recordingTitle);
          recordingDurationTextView = itemView.findViewById(R.id.recordingDuration);
          recordingDate = itemView.findViewById(R.id.recordingDate);
          recordingBookmarksCount = itemView.findViewById(R.id.recordingBookmarksCount2);
          playButton = itemView.findViewById(R.id.recordingsPlayImagePlace);
          bookmarksExpandButton = itemView.findViewById(R.id.recordingBookmarksPlace);
          bookmarksExpandButton.setOnClickListener(this);
          bookmarksExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              int position = getAdapterPosition();
              toggleGroup(position);
            }
          });

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
      public void onClick(View v) {
//          if (listener != null) {
//              if (listener.onGroupClick(getAdapterPosition())) {
//                  collapse();
//              } else {
//                  expand();
//              }

          Recording recording = getItem(getAdapterPosition());
          createIntentForRecordingPlayer(0, recording);
      }


        @Override
      public void setOnGroupClickListener(OnGroupClickListener listener) {
          this.listener = listener;
      }

      public void setRecordingTitle(ExpandableGroup recording) {
              recordingNameTextView.setText(recording.getTitle());
      }

  //    @Override
  //    public void expand() {
  //        animateExpand();
  //    }
  //
  //    @Override
  //    public void collapse() {
  //        animateCollapse();
  //    }
  //
  //    private void animateExpand() {
  //        RotateAnimation rotate =
  //                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
  //        rotate.setDuration(300);
  //        rotate.setFillAfter(true);
  //        arrow.setAnimation(rotate);
  //    }
  //
  //    private void animateCollapse() {
  //        RotateAnimation rotate =
  //                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
  //        rotate.setDuration(300);
  //        rotate.setFillAfter(true);
  //        arrow.setAnimation(rotate);
  //    }
  }

//    public class MyOnClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            int itemPosition = rv.getChildLayoutPosition(v);
//
//            expandableList.getExpandableGroup(0).getItems()
//            Bookmark bookmarkClicked = bookmarks.get(itemPosition);
////            String time = convertMilliSecondsToRecordingTime(bookmarkClicked.getTimestamp());
//            Integer timestamp = Integer.getInteger(bookmarkClicked.getTimestamp().toString());
//
//            bookmarkClickedListener.OnClick(bookmarkClicked.getTimestamp().intValue());
//        }
//    }

    static class BookmarkViewHolder extends ChildViewHolder{
        private TextView childTextView;
        private TextView textViewTime;
        private OnChildClickListener listener;

        private Bookmark bookmark;

        public BookmarkViewHolder(View itemView) {
            super(itemView);
            childTextView = itemView.findViewById(R.id.bookmark_name);
            textViewTime = itemView.findViewById(R.id.bookmark_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int groupPosition; // TODO: 9/1/2019 saar: need to find a way to get the his parent group
                        listener.onChildClick(bookmark);
                    }
                }
            });
        }

        public Bookmark getBookmark() {
            return bookmark;
        }

        public void setBookmark(Bookmark bookmark) {
            this.bookmark = bookmark;
        }


        public void setListener(OnChildClickListener listener) {
            this.listener = listener;
        }

        public void setBookmarkText(Spannable name) {
            childTextView.setText(name);
        }

        public void setTimeText(String time) {
            this.textViewTime.setText(time);
        }
    }
}

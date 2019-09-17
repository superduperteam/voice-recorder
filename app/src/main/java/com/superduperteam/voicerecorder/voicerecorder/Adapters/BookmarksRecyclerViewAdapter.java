package com.superduperteam.voicerecorder.voicerecorder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.Model.Bookmark;
import com.superduperteam.voicerecorder.voicerecorder.Interfaces.BookmarkClickedListener;
import com.superduperteam.voicerecorder.voicerecorder.R;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class BookmarksRecyclerViewAdapter extends RecyclerView.Adapter<RecordingsAdapter.BookmarkViewHolder> {
    private static final String LOG_TAG = "AudioRecordTest";
    private final RecyclerView rv;
    private List<Bookmark> bookmarks;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private BookmarkClickedListener bookmarkClickedListener;

    // data is passed into the constructor
    public BookmarksRecyclerViewAdapter(Context context, List<Bookmark> bookmarks, RecyclerView recyclerView, BookmarkClickedListener listener) {
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

    public void updateList(List<Bookmark> newList){
        bookmarks = new ArrayList<>();
        bookmarks.addAll(newList);
        notifyDataSetChanged();
    }

    public class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = rv.getChildLayoutPosition(v);
            Bookmark bookmarkClicked = bookmarks.get(itemPosition);
            bookmarkClickedListener.OnClick(bookmarkClicked.getTimestamp().intValue());
        }
    }
}

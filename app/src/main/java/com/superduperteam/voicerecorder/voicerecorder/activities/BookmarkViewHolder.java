package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.text.Spannable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.superduperteam.voicerecorder.voicerecorder.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

class BookmarkViewHolder extends ChildViewHolder {
    private TextView childTextView;
    private TextView textViewTime;

    public BookmarkViewHolder(View itemView) {
        super(itemView);
        childTextView = itemView.findViewById(R.id.bookmark_name);
        textViewTime = itemView.findViewById(R.id.bookmark_time);
    }

    public void setBookmarkText(Spannable name) {
        childTextView.setText(name);
    }

    public void setTimeText(String time) {
        this.textViewTime.setText(time);
    }
}

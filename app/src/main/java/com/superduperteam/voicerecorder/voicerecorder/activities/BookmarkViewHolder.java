package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.view.View;
import android.widget.TextView;

import com.superduperteam.voicerecorder.voicerecorder.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

class BookmarkViewHolder extends ChildViewHolder {

    private TextView childTextView;

    public BookmarkViewHolder(View itemView) {
        super(itemView);
        childTextView = itemView.findViewById(R.id.bookmark_name);
    }

    public void setBookmarkText(String name) {
        childTextView.setText(name);
    }
}

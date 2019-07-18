package com.superduperteam.voicerecorder.voicerecorder.activities;

import androidx.annotation.NonNull;

public class Bookmark {
    Long timestamp;
    String text;

    public Bookmark(Long timestamp, String text) {
        this.timestamp = timestamp;
        this.text = text;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "timestamp=" + timestamp +
                ", text='" + text + '\'' +
                '}';
    }
}

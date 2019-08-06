package com.superduperteam.voicerecorder.voicerecorder.activities;

import androidx.annotation.NonNull;

import java.io.File;

public class Bookmark implements Line {
    private long timestamp;
    private String text;
    private Recording recording;
    private static final String timestampDelimiter = "timestamp: ";
    private static final String textStrDelimiter = "text: ";
    private static final String timestampToTextSeparatorStr = " - ";

    public Bookmark(Long timestamp, String text) {
        this.timestamp = timestamp;
        this.text = text;
    }

    public Bookmark(Long timestamp, String text, Recording recording) {
        this.timestamp = timestamp;
        this.text = text;
        this.recording = recording;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return timestampDelimiter + timestamp + timestampToTextSeparatorStr + textStrDelimiter + text;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    public static Bookmark parseBookmark(String line){
        int timeStampDelimiterIndex = line.indexOf(timestampDelimiter);
        int timestampToTextSeparatorStrIndex = line.indexOf(timestampToTextSeparatorStr);
        int textDelimiterIndex = line.indexOf(textStrDelimiter);
        long timestamp;
        String text;

        // TODO: 7/23/2019 check if the parsing is OK
        if(timeStampDelimiterIndex != -1 && textDelimiterIndex != -1){
            timestamp = Long.parseLong(line.substring(timeStampDelimiterIndex + timestampDelimiter.length(), timestampToTextSeparatorStrIndex));
            text = line.substring(textDelimiterIndex + textStrDelimiter.length());
            return new Bookmark(timestamp, text);
        }

        return null;
    }

    @Override
    public String getName() {
        return text;
    }

    @Override
    public File getFile() {
        if(recording != null){
            return recording.getFile();
        }

        return null;
    }
}

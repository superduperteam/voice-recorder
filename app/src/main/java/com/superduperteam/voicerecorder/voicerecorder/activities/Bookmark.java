package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Bookmark implements Parcelable {
    private long timestamp;
    private Spannable text;
    private StyleSpan styleSpan;
    private Recording recording;
    private File recordingFile;
    private static final String timestampDelimiter = "timestamp: ";
    private static final String textStrDelimiter = "text: ";
    private static final String timestampToTextSeparatorStr = " - ";

    public Bookmark(Long timestamp, String text) {
        this.timestamp = timestamp;
        this.text = new SpannableString(text);
    }

    public Bookmark(Long timestamp, String text, File recordingFile) {
        this.timestamp = timestamp;
        this.text = new SpannableString(text);
        this.recordingFile = recordingFile;
    }

    public Bookmark(Long timestamp, String text, Recording recording) {
        this.timestamp = timestamp;
        this.text = new SpannableString(text);
        this.recording = recording;
    }

    protected Bookmark(Parcel in) {
        text = new SpannableString(in.readString());
    }


    public Bookmark(String JSONString) {
        JSONObject myJson = null;
        try {
            myJson = new JSONObject(JSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        long timestamp = myJson.optLong("timestamp");
        String text = myJson.optString("text");
        Spannable spannableText = new SpannableString(text);
        this.timestamp = timestamp;
        this.text = spannableText;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return timestampDelimiter + timestamp + timestampToTextSeparatorStr + textStrDelimiter + text;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    public static Bookmark parseBookmark(String line, File fromFile){
        int timeStampDelimiterIndex = line.indexOf(timestampDelimiter);
        int timestampToTextSeparatorStrIndex = line.indexOf(timestampToTextSeparatorStr);
        int textDelimiterIndex = line.indexOf(textStrDelimiter);
        long timestamp;
        String text;

        // TODO: 7/23/2019 check if the parsing is OK
        if(timeStampDelimiterIndex != -1 && textDelimiterIndex != -1){
            timestamp = Long.parseLong(line.substring(timeStampDelimiterIndex + timestampDelimiter.length(), timestampToTextSeparatorStrIndex));
            text = line.substring(textDelimiterIndex + textStrDelimiter.length());
            return new Bookmark(timestamp, text, fromFile);
        }

        return null; // TODO: 8/11/2019 maybe better to throw exception here
    }

    public Spannable getTitle() {
        return text;
    }

//    public File getFile() {
//        if(recording != null){
//            return recording.getFile();
//        }
//
//        return null;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text.toString());
    }

    public static final Creator<Bookmark> CREATOR = new Creator<Bookmark>() {
        @Override
        public Bookmark createFromParcel(Parcel in) {
            return new Bookmark(in);
        }

        @Override
        public Bookmark[] newArray(int size) {
            return new Bookmark[size];
        }
    };

    public void removeTheHighlightFromText(){
        if(styleSpan != null){
            text.removeSpan(styleSpan);
        }
    }

    public void highlightText(int start, int end) {
        removeTheHighlightFromText();

        styleSpan  = new StyleSpan(Typeface.BOLD);
        text.setSpan(styleSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    public String toJSON() {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("timestamp", timestamp);
            jsonObject.put("text", text);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}

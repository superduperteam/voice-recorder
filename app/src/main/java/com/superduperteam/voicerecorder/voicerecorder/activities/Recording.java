package com.superduperteam.voicerecorder.voicerecorder.activities;

import com.alamkanak.weekview.WeekView;
import com.superduperteam.voicerecorder.voicerecorder.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Recording extends ExpandableGroup<Bookmark> {
    private File file;
    private boolean shouldExpand = false;

    public Recording(String title, File recordingFile, List<Bookmark> items) throws IOException {
        super(title,items);
        this.file = recordingFile;
        fetchBookmarks(recordingFile);
    }
    public List<Bookmark> getBookmarksList() {
        return super.getItems();
    }

//    private void fetchBookmarks() throws IOException {
//        bookmarksList = new ArrayList<>();
//        MetaDataRead cmd = new MetaDataRead();
//        String bookmarksRawData, currLine;
//
//        try {
//            bookmarksRawData = cmd.read(file.getPath());
//        } catch (NullPointerException e) {
//            System.out.println(e);
//            return;
//        }
//
//        Scanner scr = new Scanner(bookmarksRawData);
//
//        while(scr.hasNextLine()){
//            currLine = scr.nextLine();
//            Bookmark currBookmark = Bookmark.parseBookmark(currLine);
//
//            if(currBookmark != null){
//                currBookmark.setRecording(this);
//                bookmarksList.add(currBookmark);
//            }
//        }
//    }

    // Saar: This method is static because we need bookmarks list to create a Recording (because it
    // extends this NonSenseGroup class which has no default constructor that doesn't expect a children list).
    public static List<Bookmark> fetchBookmarks(File file) throws IOException {
        List<Bookmark> bookmarksList = new ArrayList<>();
        MetaDataRead cmd = new MetaDataRead();
        String bookmarksRawData, currLine;

        bookmarksRawData = cmd.read(file.getPath());

        Scanner scr = new Scanner(bookmarksRawData);

        while(scr.hasNextLine()){
            currLine = scr.nextLine();
            Bookmark currBookmark = Bookmark.parseBookmark(currLine, file);

            if(currBookmark != null){
               // currBookmark.setRecording(file);
                bookmarksList.add(currBookmark);
            }
        }

        return bookmarksList;
    }

    public void addBookmarks(List<Bookmark> bookmarksToAdd) {
        super.getItems().addAll(bookmarksToAdd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recording recording = (Recording) o;
        return file.equals(recording.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    public File getFile() {
        return file;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
    }

    public boolean isShouldExpand() {
        return shouldExpand;
    }
}

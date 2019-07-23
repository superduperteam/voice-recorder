package com.superduperteam.voicerecorder.voicerecorder.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Recording implements Line {
    private File file;
    private List<Bookmark> bookmarksList = null;

    public Recording(File recordingFile) throws IOException {
        this.file = recordingFile;
        fetchBookmarks();
    }

    public List<Bookmark> getBookmarksList() {
        return bookmarksList;
    }

    private void fetchBookmarks() throws IOException {
        bookmarksList = new ArrayList<>();
        MetaDataRead cmd = new MetaDataRead();
        String bookmarksRawData = cmd.read(file.getPath()), currLine;
        Scanner scr = new Scanner(bookmarksRawData);

        while(scr.hasNextLine()){
            currLine = scr.nextLine();
            Bookmark currBookmark = Bookmark.parseBookmark(currLine);

            if(currBookmark != null){
                currBookmark.setRecording(this);
                bookmarksList.add(currBookmark);
            }
        }
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public File getFile() {
        return file;
    }
}

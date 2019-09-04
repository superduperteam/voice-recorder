package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class RecordingsActivity extends BaseActivity implements SearchView.OnQueryTextListener, PopupMenu.OnMenuItemClickListener{
//public class RecordingsActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener, MyNewRecyclerViewAdapter.ItemClickListener, SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private List selectedItems;
    private RecordingsAdapter adapter;
    private File recordingsFolder;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private EditText searchBookmarkEditText;
    private List<Recording> recordings;
    MenuItem searchRecordingsMenuItem;
    private RadioButton checkedRadio;
    private File bookmarksToAddFile;
    private List<Bookmark> bookmarksToAddToExistingRecording = new ArrayList<>();
    private ConstraintLayout parentOfMenuItemClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    setContentView(R.layout.activity_recordings);
//        setContentView(R.layout.activity_recording_player);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mediaMetadataRetriever = new MediaMetadataRetriever();
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_recordings, null, false);
        mDrawer.addView(contentView, 1);


        onSharedIntent();

        String folderPath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath();
        recordingsFolder = new File(folderPath);
        recordings = new ArrayList<>();
        List<File> recordingsFiles = new ArrayList<>(Arrays.asList(recordingsFolder.listFiles()));
        for(File recordingFile : recordingsFiles ){
            try {
                if (!recordingFile.isDirectory()) {
                Recording recording = new Recording(recordingFile.getName().substring(0,recordingFile.getName().indexOf(".")), recordingFile, Recording.fetchBookmarks(recordingFile));
                recordings.add(recording);
//                if(recording.getBookmarksList() != null){
//                    recordings.addAll(recording.getBookmarksList());
                }
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recordingsLRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        adapter = new RecordingsAdapter(this, new ArrayList<>(recordings), recyclerView);
//        adapter = new MyNewRecyclerViewAdapter(this, recordings);
//        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration); // TODO: 8/12/2019 Optional - decide if its better with/without
     //   searchBookmarkEditText = findViewById(R.id.search_by_bookmark);
     //   searchBookmarkEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
//                final String textToSearch = charSequence.toString();
//
//                recordings = new ArrayList<>(Arrays.asList(recordingsFolder.listFiles()));
//                recordings.removeIf(recording -> {
//                    boolean remove = false;
//                    mediaMetadataRetriever.setDataSource(recording.getPath());
//                    String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                    if (title == null) {
//                        remove = true;
//                    } else {
//                        remove = !(title.contains(textToSearch));
//                    }
//
//                    if (textToSearch.equals("")) {
//                        remove = false;
//                    }
//
//                    return remove;
//                });
//
//                adapter = new BookmarksRecyclerViewAdapter(getApplicationContext(), recordings);
//
//                recyclerView.swapAdapter(adapter, true);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
    }

    private List<Bookmark> JsonArrayToBookmarksList(JSONArray jsonArray) {
        ArrayList<Bookmark> list = new ArrayList<>();

        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                try {
                    Bookmark bookmark;
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    long timestamp = Long.parseLong(jsonObject.get("timestamp").toString());
                    String text = jsonObject.get("text").toString();

                    bookmark = new Bookmark(timestamp, text);
                    bookmarksToAddToExistingRecording.add(bookmark);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    private void onSharedIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        String jsonString = "";

        if (action != null) {
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("application/json".equals(type)) {
                    JSONObject obj = null;
                    boolean toggleRadioButtonsToVisible = true;

//                    toggleRadioButtonsVisibility(toggleRadioButtonsToVisible, recyclerView);

                    try {

                        Uri bookmarksFileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                        bookmarksToAddFile = new File(bookmarksFileUri.getPath());
                        try {
                            InputStream stream = getContentResolver().openInputStream(bookmarksFileUri);

                            Toast.makeText(this, "Success " + intent.getAction() + " " + bookmarksFileUri.toString(), Toast.LENGTH_LONG).show();

                            byte[] buffer = new byte[4 * 1024];
                            int read;

                            while ((read = stream.read(buffer)) != -1) {
                                String tmp = new String(buffer);
                                jsonString += tmp;
                            }

                        } catch (IOException e) {
                            Toast.makeText(this, "Error " + intent.getAction() + " " + bookmarksFileUri.toString() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                        obj = new JSONObject(jsonString);
                        String jsonArrayAsString = obj.getString("bookmarksList");
                        JSONArray bookmarkAsJson = new JSONArray(jsonArrayAsString);
                        List<Bookmark> jsonsList = JsonArrayToBookmarksList(bookmarkAsJson);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.startsWith("audio/")) {
                    Uri redordingUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

                    File recordingFile = new File(redordingUri.getPath());
                    Recording recording = null;


                    try {
                        InputStream stream = getContentResolver().openInputStream(redordingUri);
                        Toast.makeText(this, "Success " + intent.getAction() + " " + redordingUri.toString(), Toast.LENGTH_LONG).show();
                        String folderPath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath();

                        File file = new File(folderPath + "/ReceivedRecording.m4a");
                        try (OutputStream output = new FileOutputStream(file)) {
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;

                            while ((read = stream.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }

                            output.flush();
                        }
                        Recording rec = new Recording("/ReceivedRecording.m4a", file, null);
                        recordings.add(rec);
                    } catch (IOException e) {
                        Toast.makeText(this, "Error " + intent.getAction() + " " + redordingUri.toString() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
//                recordings.add(recording);
                }
            }
        }
    }
//                    toggleRadioButtonsVisibility(toggleRadioButtonsToVisible, recyclerView);

//
//
//        // Saar: This is for the actual list of recordings - if user click on the vertical 3 dots.
//        public void onRecordingClick (View view){
//            PopupMenu popup = new PopupMenu(this, view); // view=button
//            popup.setOnMenuItemClickListener(this);
//            popup.inflate(R.menu.recording_clicked_menu);
//            popup.show();
//        }
//        @Override
//        public boolean onMenuItemClick (MenuItem item){
//            switch (item.getItemId()) {
//                case R.id.recording_clicked_delete:
//                    Toast.makeText(this, "delete from recording row", Toast.LENGTH_LONG).show();
//                    return true;
//                case R.id.recording_clicked_edit:
//                    Toast.makeText(this, "edit from recording row", Toast.LENGTH_LONG).show();
//                    return true;
//                case R.id.recording_clicked_share:
//                    Toast.makeText(this, "share from recording row", Toast.LENGTH_LONG).show();
//                    return true;
//                default:
//                    return false;
//            }
//        }
//



    public void onRadaioButtonClick(View view) {
        String recordingName = ((TextView) ((ConstraintLayout) view.getParent()).getChildAt(1)).getText().toString() + ".m4a";
        boolean toggleRadioButtonsToVisible = false;

        checkedRadio = (RadioButton) view;
        toggleOffTheRestOfTheRadioButtons(view.getParent().getParent());
        Recording recording = getRecordingFromName(recordingName);

        for (Bookmark bookmark : bookmarksToAddToExistingRecording) {
            bookmark.setRecording(recording);
        }

        recording.addBookmarks(bookmarksToAddToExistingRecording);
        writeBookmarksToMetaData(recording);
//        toggleRadioButtonsVisibility(toggleRadioButtonsToVisible, recyclerView);
    }

    private void writeBookmarksToMetaData(Recording recording) {
        MetaDataInsert cmd = new MetaDataInsert();
        StringBuilder sb = new StringBuilder();
        sb.append("Bookmarks: ").append(System.lineSeparator());

        for (Bookmark bookmark : recording.getBookmarksList()) {
            sb.append(bookmark.toString()).append(System.lineSeparator());
        }

        try {
            cmd.writeMetadata(recording.getFile().getAbsolutePath(), sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Recording getRecordingFromName(String recordingName) {
        String recordingPath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + '/' + recordingName;
        File file = new File(recordingPath);
        Recording recording = null;

        for (Recording rec : recordings) {
            if (rec.getFile().getAbsolutePath().equals(recordingPath)) {
                recording = (Recording) rec;
            }
        }

        return recording;
    }

    private void toggleRadioButtonsVisibility(boolean toggleToVisible, ViewParent parentOfRadioButtons) {
        int numberOfChildren = ((RecyclerView) parentOfRadioButtons).getChildCount();

        for (int i = 0; i < numberOfChildren; i += 1) {
            RadioButton radioButton = ((RadioButton) ((RecyclerView) parentOfRadioButtons).findViewHolderForLayoutPosition(i).itemView.getTouchables().get(4));

            radioButton.setVisibility(toggleToVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void toggleOffTheRestOfTheRadioButtons(ViewParent parentOfRadioButtons) {
        int numberOfChildren = ((RecyclerView) parentOfRadioButtons).getChildCount();

        for (int i = 0; i < numberOfChildren; i += 1) {
            RadioButton radioButton = ((RadioButton) ((RecyclerView) parentOfRadioButtons).findViewHolderForLayoutPosition(i).itemView.getTouchables().get(4));

            if (radioButton != checkedRadio) {
                ((RadioButton) ((RecyclerView) parentOfRadioButtons).findViewHolderForLayoutPosition(i).itemView.getTouchables().get(4)).setChecked(false);
            }
        }

    }

    // Saar: This is for the actual list of recordings - if user click on the vertical 3 dots.
    public void onRecordingClick(View view) {
        parentOfMenuItemClicked = (ConstraintLayout) view.getParent();
        PopupMenu popup = new PopupMenu(this, view); // view=button
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.recording_clicked_menu);
        popup.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String recordingName = ((TextView) parentOfMenuItemClicked.getChildAt(1)).getText().toString() + ".m4a";
        String fileName = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + '/' + recordingName;
        String bookmarkDirectoryName = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "/bookmarks";
        File file = new File(fileName);
        File dir = new File(bookmarkDirectoryName);
        dir.mkdir();
        String boomkarkFileName = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "/bookmarks/bookmarks.json";
        File bookmarkFile = new File(boomkarkFileName);
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.mydomain.fileprovider", file);
        Uri bookmarkUri = FileProvider.getUriForFile(getApplicationContext(), "com.mydomain.fileprovider", bookmarkFile);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        switch (item.getItemId()) {
            case R.id.recording_clicked_delete:
//                Toast.makeText(this, "delete from recording row", Toast.LENGTH_LONG).show();
                file.delete();


                for(Recording recording : recordings){
                    if(recording.getFile().equals(file) ){
                        recordings.remove(recording);
                    }
                }

                adapter.updateList(recordings);
                return true;
            case R.id.recording_clicked_edit:
                Toast.makeText(this, "edit from recording row", Toast.LENGTH_LONG).show();
                return true;
            case R.id.share_recording:
//                    Uri uri = FileProvider.getUriForFile(getApplicationContext(),"com.mydomain.fileprovider", file);
//
//                    Uri uri = Uri.parse(fileName);
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.setType("audio/*");
                startActivity(Intent.createChooser(sendIntent, "Share Sound File"));

                return true;
            case R.id.share_bookmarks:
                String bookmarks = null;
                List<Bookmark> bookmarksList = new ArrayList<>();
                MetaDataRead cmd = new MetaDataRead();
                String bookmarksRawData, currLine;

                try {
                    bookmarksRawData = cmd.read(file.getPath());
                } catch (NullPointerException e) {
                    System.out.println(e);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }

                Scanner scr = new Scanner(bookmarksRawData);

                while (scr.hasNextLine()) {
                    currLine = scr.nextLine();
                    Bookmark currBookmark = Bookmark.parseBookmark(currLine, file);

                    if (currBookmark != null) {
                        bookmarksList.add(currBookmark);
                    }
                }
                JSONObject jsonObject = new JSONObject();
                List<String> bookmarksAsJSON = new ArrayList<>();
                bookmarksList.forEach(bookmark -> {
                    bookmarksAsJSON.add(bookmark.toJSON());
                });
                try {
                    jsonObject.put("bookmarksList", bookmarksAsJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                File file1 = new File(Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "bookmarks.json");
                Path filePath = Paths.get(file1.getPath());
                FileWriter writer = null;
                try {
                    writer = new FileWriter(bookmarkFile);
                    writer.write(jsonObject.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                sendIntent.putExtra(Intent.EXTRA_STREAM, bookmarkUri);
                sendIntent.setType("application/json");
                startActivity(Intent.createChooser(sendIntent, "Share Sound File"));
                return true;

//                    Uri uri = FileProvider.getUriForFile(getApplicationContext(),"com.mydomain.fileprovider", file);

//                    Uri uri = Uri.parse(fileName);
//                    Intent sendIntent = new Intent();
//                    sendIntent.setAction(Intent.ACTION_SEND);
//                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                    sendIntent.setType("text/plain");
//                    startActivity(Intent.createChooser(sendIntent, "Share Sound File"));

//                    return true;
            default:
                return false;
        }
    }

    private String copyFiletoExternalStorage() {
        String pathSDCard = Environment.getExternalStorageDirectory() + "/Android/data/" + "Recording 4.m4a";
        try {
            String fileName = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "/Recording 4.m4a";
            File file = new File(fileName);
            InputStream in = new FileInputStream(file);
            FileOutputStream out = null;
            out = new FileOutputStream(pathSDCard);
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathSDCard;
    }

    // Saar: This is for Sort button
    public void onSortClick (MenuItem item){

       alertSortElements();
    }
    public void alertSortElements () {

        /*
         * Inflate the XML view. activity_main is in
         * res/layout/form_elements.xml
         */
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.sort_elements,
                null, false);


        final RadioGroup sortOrderRadioGroup = formElementsView.findViewById(R.id.sortOrderRadioGroup);
        final RadioGroup sortByAttributeGroup = formElementsView.findViewById(R.id.sortByRadioGroup);

        // the alert dialog
        new AlertDialog.Builder(RecordingsActivity.this).setView(formElementsView)
                .setTitle("Sort")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        List<Recording> newList = new ArrayList<>(recordings);

                        String toastString = "";

                        /*
                         * Getting the value of selected RadioButton.
                         */
                        // get selected radio button from radioGroup
                        int selectedAttributeId = sortByAttributeGroup.getCheckedRadioButtonId();
                        int SelectedOrderId = sortOrderRadioGroup.getCheckedRadioButtonId();

                        // find the radiobutton by returned id
                        RadioButton selectedAttributeRadioButton = formElementsView.findViewById(selectedAttributeId);
                        RadioButton selectedOrderRadioButton = formElementsView.findViewById(SelectedOrderId);
                        toastString += "Selected radio buttons are: " + selectedAttributeRadioButton.getText() + " & " + selectedOrderRadioButton.getText() + "!\n";
                        if(selectedAttributeRadioButton.getId() == R.id.titleRadioButton){
                            Collections.sort(newList, new Comparator<Recording>() {
                                @Override
                                public int compare(Recording o1, Recording o2) {
                                    return o1.getTitle().compareTo(o2.getTitle());
                                }
                            });
                        }
                        else{
                            Collections.sort(newList, new Comparator<Recording>() {
                                @Override
                                public int compare(Recording o1, Recording o2) {
                                    if(o1.getFile().lastModified() == o2.getFile().lastModified()){
                                        return 0;
                                    }
                                    else if(o1.getFile().lastModified() < o2.getFile().lastModified()){
                                        return -1;
                                    }
                                    else{
                                        return 1;
                                    }
                                }
                            });
                        }

                        if(selectedOrderRadioButton.getId() == R.id.descendingRadioButton){
                            Collections.reverse(newList);
                        }
                        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                        adapter.updateList(newList);
                        dialog.cancel();
                    }

                }).show();
    }

//        @Override
//        public void onItemClick (View view,int position){
//            Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
//        }
//
//        public void onPlayClick (View view,int position){
//        }


    // Saar: This is to make the buttons in the top to show : search and sort
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recordings, menu);
        searchRecordingsMenuItem = menu.findItem(R.id.recordings_search);

        ActionBar actionbar = getSupportActionBar();
        Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        SearchView searchView = (SearchView) searchRecordingsMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
//        mMenuItem.setVisible(false);
//        mMenuItem.setTitle("Deleteeeee");
//        mMenuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

//    @Override
//    public boolean onQueryTextChange(String newText) {
//        String userInput = newText.toLowerCase();
//        List<Recording> newList = new ArrayList<>();
//
//        for(Recording recording: recordings){
//            if(recording.getTitle().toLowerCase().contains(userInput)){
//                newList.add(recording);
//            }
//            else{
//                for(Bookmark bookmark : recording.getBookmarksList()){
//                    if(bookmark.getTitle().toLowerCase().contains(userInput)) {
//                        newList.add(recording);
//                        break;
//                    }
//                }
//            }
//        }
//
//        adapter.updateList(newList);
//        return true;
//    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        List<Recording> newList = new ArrayList<>();

        for(Recording recording: recordings){
            if(recording.getTitle().toLowerCase().contains(userInput)){
                recording.getBookmarksList().forEach(Bookmark::removeTheHighlightFromText);
                newList.add(recording);
            }
            else{
                // Saar: If we are here, it means the recording is not matching the userInput.
                // Now we want to check if it has matching bookmarks - if so, we want to mark these bookmarks.
                int matchingBookmarksCount = 0;

                for(Bookmark bookmark : recording.getBookmarksList()){
                    if(bookmark.getTitle().toString().toLowerCase().contains(userInput)) {
                        int start = bookmark.getTitle().toString().toLowerCase().indexOf(userInput);
                        int end = start + userInput.length();
                        bookmark.highlightText(start, end);
                        matchingBookmarksCount++;
                    }
                }

                if(matchingBookmarksCount > 0){
                    newList.add(recording);
                }
            }
        }

        adapter.updateList(newList);
        return true;
    }
}

//    @Override
//    public boolean onQueryTextChange(String newText) {
//        String userInput = newText.toLowerCase();
//        List<Recording> newList = new ArrayList<>();
//
//        for(Recording recording: recordings){
//            if(recording.getTitle().toLowerCase().contains(userInput)){
//                recording.setShouldExpand(false);
//                newList.add(recording);
//            }
//            else{
//                // Saar: If we are here, it means the recording is not matching the userInput.
//                // Now we want to check if it has matching bookmarks - if so, we want to include only these bookmarks.
//
//                List<Bookmark> matchingBookmarks = new ArrayList<>();
//
//                for(Bookmark bookmark : recording.getBookmarksList()){
//                    if(bookmark.getTitle().toLowerCase().contains(userInput)) {
//                        matchingBookmarks.add(bookmark);
//                        break;
//                    }
//                }
//
//                if(matchingBookmarks.size() != 0){
//                    try {
//                        Recording newRecording = new Recording(recording.getTitle(), recording.getFile(), matchingBookmarks);
//                        newRecording.setShouldExpand(true);
//                        newList.add(newRecording);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//        adapter.updateList(newList);
//        return true;
//    }


//    public void alertSingleChoiceItems(){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(RecordingsActivity.this);
//
//        // Set the dialog title
//        builder.setTitle("Choose One")
//
//                // specify the list array, the items to be selected by default (null for none),
//                // and the listener through which to receive call backs when items are selected
//                // again, R.array.choices were set in the resources res/values/strings.xml
//                .setSingleChoiceItems(R.array.sort_by_attribute, 0, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        Toast.makeText(getApplicationContext(), "Some actions maybe? Selected index: " + arg1, Toast.LENGTH_SHORT).show();
//
//                    }
//
//                })
//                .setSingleChoiceItems(R.array.sort_ascending_or_descending, 0, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                Toast.makeText(getApplicationContext(), "Some actions maybe? Selected index: " + arg1, Toast.LENGTH_SHORT).show();
//
//            }
//
//        })
//
//                // Set the action buttons
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // user clicked OK, so save the mSelectedItems results somewhere
//                        // or return them to the component that opened the dialog
//
//                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
//                        Toast.makeText(getApplicationContext(), "selectedPosition: " + selectedPosition, Toast.LENGTH_SHORT).show();
//
//                    }
//                })
//
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // removes the dialog from the screen
//
//                    }
//                })
//
//                .show();
//
//    }


//    public void onShareClick(MenuItem item) {
//        Toast.makeText(getApplicationContext(),"share", Toast.LENGTH_SHORT).show();
//    }
//
//    public void onDeleteClick(MenuItem item) {
//        Toast.makeText(getApplicationContext(),"delete", Toast.LENGTH_SHORT).show();
//    }
//
//    public void onEditClick(MenuItem item) {
//        Toast.makeText(getApplicationContext(),"edit", Toast.LENGTH_SHORT).show();
//    }


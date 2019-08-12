package com.superduperteam.voicerecorder.voicerecorder.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RecordingsActivity extends BaseActivity implements SearchView.OnQueryTextListener{
//public class RecordingsActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener, MyNewRecyclerViewAdapter.ItemClickListener, SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private List selectedItems;
    private GenreAdapter adapter;
    private File recordingsFolder;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private EditText searchBookmarkEditText;
    private List<Recording> recordings;
    MenuItem searchRecordingsMenuItem;

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

        String folderPath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath();
        recordingsFolder = new File(folderPath);
        recordings = new ArrayList<>();
        List<File> recordingsFiles = new ArrayList<>(Arrays.asList(recordingsFolder.listFiles()));
        for(File recordingFile : recordingsFiles ){
            try {
                Recording recording = new Recording(recordingFile.getName().substring(0,recordingFile.getName().indexOf(".")), recordingFile, Recording.fetchBookmarks(recordingFile));
                recordings.add(recording);
//                if(recording.getBookmarksList() != null){
//                    recordings.addAll(recording.getBookmarksList());
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

        adapter = new GenreAdapter(this, new ArrayList<>(recordings));
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
//                adapter = new MyRecyclerViewAdapter(getApplicationContext(), recordings);
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
                            toastString += "Selected radio buttons are: " + selectedAttributeRadioButton.getText() + " & "
                                    + selectedOrderRadioButton.getText() + "!\n";
                            Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();

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
    }

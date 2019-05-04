package com.superduperteam.voicerecorder.voicerecorder.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RecordingsActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener, MyRecyclerViewAdapter.ItemClickListener {
    private RecyclerView recyclerView;
    private List selectedItems;
    private MyRecyclerViewAdapter adapter;
    private File recordingsFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    setContentView(R.layout.activity_recordings);
//        setContentView(R.layout.activity_recording_player);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_recordings, null, false);
        mDrawer.addView(contentView, 1);

        String folderPath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath();
        recordingsFolder = new File(folderPath);
        File [] recordings = recordingsFolder.listFiles();
        List recordingsList = new ArrayList(Arrays.asList(recordings));

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recordingsLRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, recordingsList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

//        recyclerView = (RecyclerView) findViewById(R.id.recordingsLRecyclerView);

    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        getMenuInflater().inflate(R.layout.recordingsMoreOptions);
//    }


    // Saar: This is to make the buttons in the top to show : search and sort
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recordings, menu);
        MenuItem mMenuItem = menu.findItem(R.id.app_bar_search);

        ActionBar actionbar = getSupportActionBar();
        Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
//        mMenuItem.setVisible(false);
//        mMenuItem.setTitle("Deleteeeee");
//        mMenuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }


    // Saar: This is for the actual list of recordings - if user click on the vertical 3 dots.
    public void onRecordingClick(View view) {
        PopupMenu popup = new PopupMenu(this, view); // view=button
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.recording_clicked_menu);
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.recording_clicked_delete:
                Toast.makeText(this, "delete from recording row", Toast.LENGTH_LONG).show();
                return true;
            case R.id.recording_clicked_edit:
                Toast.makeText(this, "edit from recording row", Toast.LENGTH_LONG).show();
                return true;
            case R.id.recording_clicked_share:
                Toast.makeText(this, "share from recording row", Toast.LENGTH_LONG).show();
                return true;
                default:
                    return false;
        }
    }


    // Saar: This is for Sort button
    public void onSortClick(MenuItem item) {
        alertSortElements();
    }
    public void alertSortElements() {

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
                                +selectedOrderRadioButton.getText()  +"!\n";
                        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();

                        dialog.cancel();
                    }

                }).show();
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public void onPlayClick(View view, int position) {
    }


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

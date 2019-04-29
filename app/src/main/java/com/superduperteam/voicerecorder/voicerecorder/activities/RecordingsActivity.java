package com.superduperteam.voicerecorder.voicerecorder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.superduperteam.voicerecorder.voicerecorder.BaseActivity;
import com.superduperteam.voicerecorder.voicerecorder.R;

import java.util.ArrayList;
import java.util.List;

public class RecordingsActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {
    private RecyclerView recyclerView;
    private List selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    setContentView(R.layout.activity_recordings);
//        setContentView(R.layout.activity_recording_player);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_recordings, null, false);
        mDrawer.addView(contentView, 1);




//        recyclerView = (RecyclerView) findViewById(R.id.recordingsLRecyclerView);

    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        getMenuInflater().inflate(R.layout.recordingsMoreOptions);
//    }


    // This is for the buttons in the top: search and sort
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recordings, menu);
        MenuItem mMenuItem = menu.findItem(R.id.app_bar_search);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
//        mMenuItem.setVisible(false);
//        mMenuItem.setTitle("Deleteeeee");
//        mMenuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }


    // This is for the actual list of recordings - if user click on the vertical 3 dots
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


    // This is for Sort button
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


        final RadioGroup genderRadioGroup = (RadioGroup) formElementsView
                .findViewById(R.id.genderRadioGroup);

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
                        int selectedId = genderRadioGroup
                                .getCheckedRadioButtonId();

                        // find the radiobutton by returned id
                        RadioButton selectedRadioButton = (RadioButton) formElementsView
                                .findViewById(selectedId);

                        toastString += "Selected radio button is: "
                                + selectedRadioButton.getText() + "!\n";

                        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_SHORT).show();

                        dialog.cancel();
                    }

                }).show();
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

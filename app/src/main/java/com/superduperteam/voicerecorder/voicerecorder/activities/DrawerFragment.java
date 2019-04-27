//package com.superduperteam.voicerecorder.voicerecorder.activities;
//
//import android.os.Bundle;
//import android.view.Window;
//import android.widget.ListView;
//
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.FragmentActivity;
//
//import com.superduperteam.voicerecorder.voicerecorder.R;
//
//public class DrawerFragment extends FragmentActivity {
//
//    protected DrawerLayout mDrawer;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.base_layout);
//
//        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        //This is about creating custom listview for navigate drawer
//        //Implementation for NavigateDrawer HERE !
//        ArrayList<DrawerListItem> drawerListItems = new ArrayList<DrawerListItem>();
//        drawerListItems.add(new DrawerListItem(0,"AIR° DEVICES"));
//        drawerListItems.add(new DrawerListItem(1,"A/C Device [1]"));
//        drawerListItems.add(new DrawerListItem(1,"A/C Device [2]"));
//        drawerListItems.add(new DrawerListItem(1,"A/C Device [3]"));
//        drawerListItems.add(new DrawerListItem(0,"AIR° FEATURES"));
//        drawerListItems.add(new DrawerListItem(2,"SLEEP MODE"));
//        drawerListItems.add(new DrawerListItem(2,"TRACKING MODE"));
//        drawerListItems.add(new DrawerListItem(2,"SETTINGS"));
//        DrawerAdapter mDrawerAdapter = new DrawerAdapter(this, R.layout.drawer_list_header, drawerListItems);
//        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
//        mDrawerList.setAdapter(mDrawerAdapter);
//    }
//
//}

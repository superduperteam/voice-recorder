//package com.superduperteam.voicerecorder.voicerecorder.activities;
//
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.widget.DrawerLayout;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.ListView;
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
//        DrawerAdapter mDrawerAdapter = new DrawerAdapter(this, R.layout.drawer_list_header, drawerListItems);
//        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
//        mDrawerList.setAdapter(mDrawerAdapter);
//    }
//
//    protected void replaceContentLayout(int sourceId, int destinationId) {
//        View contentLayout = findViewById(destinationId);
//
//        ViewGroup parent = (ViewGroup) contentLayout.getParent();
//        int index = parent.indexOfChild(contentLayout);
//
//        parent.removeView(contentLayout);
//        contentLayout = getLayoutInflater().inflate(sourceId, parent, false);
//        parent.addView(contentLayout, index);
//    }
//
//}

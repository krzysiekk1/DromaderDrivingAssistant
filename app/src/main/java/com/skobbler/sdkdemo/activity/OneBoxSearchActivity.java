package com.skobbler.sdkdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.sdktools.onebox.fragments.OneBoxFragment;
import com.skobbler.ngx.sdktools.onebox.fragments.OneBoxManager;
import com.skobbler.ngx.search.SKSearchListener;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.fragments.MapFragment;
import com.skobbler.sdkdemo.menu.MenuDrawerItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.R.id.list;

//import static com.skobbler.ngx.sdktools.onebox.fragments.OneBoxManager.currentPosition;

/**
 * Created by Jakub Solawa on 02.10.2016.
 */

public class OneBoxSearchActivity extends MapActivity {

    //    public void runOneLineSearch(){
//
//    }
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<MenuDrawerItem> list;
    private LinkedHashMap<MapActivity.MapOption, MenuDrawerItem> menuItems;
    private ListView drawerList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SKPosition currentPosition = SKPositionerManager.getInstance().getCurrentGPSPosition(true);
        SKCoordinate currentCoordinate = currentPosition.getCoordinate();
        LayoutInflater inflater = this.getLayoutInflater();
        listView = (ListView) findViewById(R.id.list_view);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapFragment mapFragment = new MapFragment();
        fragmentTransaction.add(R.id.onebox_fragment, mapFragment, null);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer);
        OneBoxFragment oneboxFragment = new OneBoxFragment();
        fragmentTransaction.add(R.id.onebox_fragment, oneboxFragment, OneBoxManager.ONEBOX_FRAGMENT_ID);
        fragmentTransaction.addToBackStack(OneBoxManager.ONEBOX_FRAGMENT_ID);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().findFragmentByTag(OneBoxManager.ONEBOX_FRAGMENT_ID) != null)
            ((OneBoxFragment) getFragmentManager().findFragmentByTag(OneBoxManager.ONEBOX_FRAGMENT_ID)).handleBackButtonPressed();
        return;
    }

//    @Override
//    public void onReceivedSearchResults(List list) {
//        System.out.println("ZNALAZLEM");
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!((OneBoxFragment) getFragmentManager().findFragmentByTag(OneBoxManager.ONEBOX_FRAGMENT_ID)).ONEBOX_ACTIVATED) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public static MenuDrawerItem create(MapActivity.MapOption mapOption, String label, int itemType) {
        MenuDrawerItem menuDrawerItem = new MenuDrawerItem(mapOption);
        menuDrawerItem.setLabel(label);
        menuDrawerItem.setItemType(itemType);
        return menuDrawerItem;
    }

}


package com.skobbler.sdkdemo.model;

import android.content.Context;

import com.skobbler.sdkdemo.activity.MapActivity;

/**
 * Created by AlexandraP on 04.03.2015.
 */
public class MenuDrawerItem {

    public static final int ITEM_TYPE = 1;
    public static final int SECTION_TYPE = 2;
    /**
     * Title
     */
    private String label;
    /**
     * Item type: section or list item
     */
    private int itemType;

    private MapActivity.MapOption mapOption;

    public MenuDrawerItem(MapActivity.MapOption mapOption) {
        this.mapOption = mapOption;
    }

    public MapActivity.MapOption getMapOption() {
        return mapOption;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }






}

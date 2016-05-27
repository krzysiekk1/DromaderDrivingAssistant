package com.skobbler.sdkdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.model.MenuDrawerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexandraP on 04.03.2015.
 */
public class MenuDrawerAdapter extends ArrayAdapter<MenuDrawerItem> {
    /**
     * layout inflater
     */
    private LayoutInflater inflater;

    /**
     * Navigation drawer list item
     */
    private ArrayList<MenuDrawerItem> objects;

    public MenuDrawerAdapter(Context context, int textViewResourceId, ArrayList<MenuDrawerItem> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        MenuDrawerItem menuItem = this.getItem(position);

        view = getItemView(convertView, parent, menuItem);

        return view;
    }

    public View getItemView(View convertView, ViewGroup parentView, MenuDrawerItem navDrawerItem) {

        MenuDrawerItem menuItem = (MenuDrawerItem) navDrawerItem;
        MenuItemHolder navMenuItemHolder = null;
        TextView labelView;
        if (convertView == null) {
            if (menuItem.getItemType() == MenuDrawerItem.ITEM_TYPE) {
                convertView = inflater.inflate(R.layout.element_menu_drawer_item, parentView, false);
                labelView = (TextView) convertView
                        .findViewById(R.id.navmenu_item_label);
            } else {
                convertView = inflater.inflate(R.layout.element_menu_drawer_section, parentView, false);
                labelView = (TextView) convertView
                        .findViewById(R.id.navmenusection_label);
            }


            navMenuItemHolder = new MenuItemHolder();
            navMenuItemHolder.labelView = labelView;


            convertView.setTag(navMenuItemHolder);
        }

        if (navMenuItemHolder == null) {
            navMenuItemHolder = (MenuItemHolder) convertView.getTag();
        }

        navMenuItemHolder.labelView.setText(menuItem.getLabel());


        return convertView;
    }


    @Override
    public int getViewTypeCount() {
        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.getItem(position).getItemType();
    }


    private static class MenuItemHolder {
        private TextView labelView;

    }


}

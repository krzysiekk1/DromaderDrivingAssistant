package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skobbler.debugkit.R;
import com.skobbler.debugkit.activity.DebugMapActivity;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKCalloutView;
import com.skobbler.ngx.map.SKMapViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 23.06.2015.
 */
public class CalloutViewDebugSettings extends DebugSettings {
    /**
     * Title string
     */
    private String title;
    /**
     * Subtitle string
     */
    private String subtitle;
    /**
     * Latitude
     */
    private double latitude = 37.7765;
    /**
     * longitude
     */
    private double longitude = -122.4200;
    /**
     * Offset X
     */
    private float offsetX;
    /**
     * Minimum zoom level
     */
    private int minimumZoomLevel = 5;

    /**
     * Hide callout view
     */
    private boolean hideCalloutViewButton;


    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.callout_view_title), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.latitude), latitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.longitude), longitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.callout_view_vertical_offset), offsetX));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.minimum_tap_zoom), minimumZoomLevel));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.callout_view_title_text), title));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.callout_view_subtitle_text), subtitle));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_actions), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.callout_view_show_callout), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.callout_view_hide_callout), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.callout_view_show_custom_callout), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.callout_view_customization;
    }

    @Override
    void defineSpecificListeners() {
        final View showCallout = specificLayout.findViewById(R.id.callout_view_actions);
        final EditText latitudeCallout = (EditText) specificLayout.findViewById(R.id.callout_view_latitude).findViewById(R.id.property_value);
        final EditText longitudeCallout = (EditText) specificLayout.findViewById(R.id.callout_view_longitude).findViewById(R.id.property_value);
        final EditText offsetXCallout = (EditText) specificLayout.findViewById(R.id.callout_view_offsetX).findViewById(R.id.property_value);
        final EditText minimumZoomLevelCallout = (EditText) specificLayout.findViewById(R.id.callout_view_minimum_zoom_level).findViewById(R.id.property_value);
        final EditText titleCallout = (EditText) specificLayout.findViewById(R.id.callout_view_title_text).findViewById(R.id.property_value);
        final EditText subtitleCallout = (EditText) specificLayout.findViewById(R.id.callout_view_subtitle_text).findViewById(R.id.property_value);
        showCallout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitude = Double.parseDouble(latitudeCallout.getText().toString());
                longitude = Double.parseDouble(longitudeCallout.getText().toString());
                offsetX = Float.parseFloat(offsetXCallout.getText().toString());
                minimumZoomLevel = Integer.parseInt(minimumZoomLevelCallout.getText().toString());
                title = titleCallout.getText().toString();
                subtitle = subtitleCallout.getText().toString();
                showCalloutView();
            }
        });

        final View hideCallout = specificLayout.findViewById(R.id.callout_view_hide_callout);
        hideCallout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideCalloutViewButton = true;
                showCalloutView();
            }
        });

        final View showCustomCalloutView = specificLayout.findViewById(R.id.callout_view_show_custom_callout);
        showCustomCalloutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomCalloutView();
            }
        });

    }
    private void showCalloutView() {
        SKCalloutView skCalloutView = activity.getMapHolder().getCalloutView();
        if (hideCalloutViewButton) {
            skCalloutView.setVisibility(View.GONE);
        } else {
            skCalloutView.setCustomView(null);
            skCalloutView.setTitle(title);
            skCalloutView.setDescription(subtitle);
            skCalloutView.setVerticalOffset(offsetX);
            skCalloutView.setMinimumZoomLevel(minimumZoomLevel);
            skCalloutView.setOnLeftImageClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Left button pressed", Toast.LENGTH_LONG).show();
                }
            });
            skCalloutView.setOnRightImageClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Right button pressed", Toast.LENGTH_LONG).show();
                }
            });
            skCalloutView.setOnTextClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Title pressed", Toast.LENGTH_LONG).show();
                }
            });

            skCalloutView.showAtLocation(new SKCoordinate(longitude, latitude), true);
            activity.getMapView().centerMapOnPosition(new SKCoordinate(longitude, latitude));
        }
        hideCalloutViewButton = false;
    }

    private void showCustomCalloutView() {
        SKCalloutView skCalloutViewCustom = activity.getMapHolder().getCalloutView();
        if (hideCalloutViewButton) {
            skCalloutViewCustom.setVisibility(View.GONE);
        } else {
            View view = View.inflate(activity, R.layout.callout_view_custom_view, null);
            skCalloutViewCustom.setCustomView(view);
            skCalloutViewCustom.showAtLocation(new SKCoordinate(longitude, latitude), true);
            activity.getMapView().centerMapOnPosition(new SKCoordinate(longitude, latitude));
        }
        hideCalloutViewButton = false;
    }
}

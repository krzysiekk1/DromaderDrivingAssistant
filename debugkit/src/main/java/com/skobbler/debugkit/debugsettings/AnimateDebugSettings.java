package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexandraP on 02.07.2015.
 */
public class AnimateDebugSettings extends DebugSettings {

    private float zoomLevel = 17;

    private float bearing = 0;

    private int duration = 0;

    private double latitude = 52.5233;

    private double longitude = 13.4127;


    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();

        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animate_zoom_level_title), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animate_zoom_level), zoomLevel));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animate_button), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animate_bearing_title), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animate_bearing), bearing));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animate_button), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animate_location), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.latitude), latitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.longitude), longitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animate_duration), 0));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animate_button), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.animate_debug_kit;
    }

    @Override
    void defineSpecificListeners() {
        final EditText zoomLevelValue = (EditText) specificLayout.findViewById(R.id.animate_zoom_level).findViewById(R.id.property_value);
        zoomLevelValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final View animateZoomLevelButton = specificLayout.findViewById(R.id.animate_zoom_level_button);
        animateZoomLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomLevel = Float.parseFloat(zoomLevelValue.getText().toString());
                activity.getMapView().setZoom( zoomLevel);
            }
        });

        final EditText latitudeValue = (EditText) specificLayout.findViewById(R.id.animate_latitude).findViewById(R.id.property_value);
        latitudeValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final EditText longitudeValue = (EditText) specificLayout.findViewById(R.id.animate_longitude).findViewById(R.id.property_value);
        longitudeValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final EditText durationValue = (EditText) specificLayout.findViewById(R.id.animate_duration).findViewById(R.id.property_value);
        durationValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        final View animateLocationButton = specificLayout.findViewById(R.id.animate_location_button);
        animateLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitude = Double.parseDouble(latitudeValue.getText().toString());
                longitude = Double.parseDouble(longitudeValue.getText().toString());
                duration = Integer.parseInt(durationValue.getText().toString());

                activity.getMapView().centerMapOnPositionSmooth(new SKCoordinate(longitude, latitude), duration);
            }
        });

        final EditText bearingValue = (EditText) specificLayout.findViewById(R.id.animate_bearing).findViewById(R.id.property_value);
        bearingValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final View animateBearingButton = specificLayout.findViewById(R.id.animate_bearing_button);
        animateBearingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bearing = Float.parseFloat(bearingValue.getText().toString());
                activity.getMapView().rotateMapWithAngle(bearing);

            }
        });


    }
}

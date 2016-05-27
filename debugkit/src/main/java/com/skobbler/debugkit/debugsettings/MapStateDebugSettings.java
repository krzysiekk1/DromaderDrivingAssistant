package com.skobbler.debugkit.debugsettings;

import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/8/2015.
 */
public class MapStateDebugSettings extends DebugSettings {

    private static final float INITIAL_ZOOM = 17;

    private static final float INITIAL_LATITUDE = 52.523569f;

    private static final float INITIAL_LONGITUDE = 13.413181f;

    private static final int INITIAL_BEARING = 0;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.zoom), Float.toString(INITIAL_ZOOM * 10)));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.latitude), Float.toString(INITIAL_LATITUDE)));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.longitude), Float.toString(INITIAL_LONGITUDE)));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.bearing), INITIAL_BEARING));
        return pairs;
    }

    @Override
    void onOpened() {
        super.onOpened();
        update();
    }

    private void applySettings() {
        float zoom = (float) ((SeekBar) specificLayout.findViewById(R.id.map_zoom).findViewById(R.id.property_seekbar)).getProgress() / 10;

        float latitude = 0;
        try {
            latitude = Float.parseFloat(((EditText) specificLayout.findViewById(R.id.map_position_latitude).findViewById(R.id.property_value)).getText().toString());
        } catch (NumberFormatException e) {
        }

        float longitude = 0;
        try {
            longitude = Float.parseFloat(((EditText) specificLayout.findViewById(R.id.map_position_longitude).findViewById(R.id.property_value)).getText().toString());
        } catch (NumberFormatException e) {
        }

        int bearing = ((SeekBar) specificLayout.findViewById(R.id.map_bearing).findViewById(R.id.property_seekbar)).getProgress();

        activity.getMapView().setZoom(zoom);
        activity.getMapView().centerMapOnPosition(new SKCoordinate(longitude, latitude));
        activity.getMapView().rotateMapWithAngle(bearing);
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_map_state;
    }

    @Override
    void defineSpecificListeners() {
        SeekBar zoomSeekBar = (SeekBar) specificLayout.findViewById(R.id.map_zoom).findViewById(R.id.property_seekbar);
        final TextView zoomText = (TextView) specificLayout.findViewById(R.id.map_zoom).findViewById(R.id.property_value);
        zoomText.setText(Float.toString(INITIAL_ZOOM));
        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                if (fromUser) {
                    zoomText.setText(Float.toString((float) value / 10));
                    applySettings();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar bearingSeekBar = (SeekBar) specificLayout.findViewById(R.id.map_bearing).findViewById(R.id.property_seekbar);
        final TextView bearingText = (TextView) specificLayout.findViewById(R.id.map_bearing).findViewById(R.id.property_value);
        bearingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                if (fromUser) {
                    bearingText.setText(value + "");
                    applySettings();
                }
        }

        @Override
        public void onStartTrackingTouch (SeekBar seekBar){

        }

        @Override
        public void onStopTrackingTouch (SeekBar seekBar){

        }
    }

    );

    ((EditText)specificLayout.findViewById(R.id.map_position_latitude).

    findViewById(R.id.property_value)

    ).

    setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange (View view,boolean hasFocus){
            if (!hasFocus) {
                applySettings();
            }
        }
    }

    );

    ((EditText)specificLayout.findViewById(R.id.map_position_longitude).

    findViewById(R.id.property_value)

    ).

    setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange (View view,boolean hasFocus){
            if (!hasFocus) {
                applySettings();
            }
        }
    }

    );
}

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();

        SeekBar zoom = (SeekBar) specificLayout.findViewById(R.id.map_zoom).findViewById(R.id.property_seekbar);
        zoom.setMax(190);
        zoom.setProgress(170);

        SeekBar bearing = (SeekBar) specificLayout.findViewById(R.id.map_bearing).findViewById(R.id.property_seekbar);
        bearing.setMax(360);
        bearing.setProgress(0);

        ((EditText) specificLayout.findViewById(R.id.map_position_longitude).findViewById(R.id.property_value)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ((EditText) specificLayout.findViewById(R.id.map_position_latitude).findViewById(R.id.property_value)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public void update() {
        float zoom = activity.getMapView().getZoomLevel();
        float zoomRounded = (float) (Math.round(zoom * 10)) / 10;
        ((SeekBar) specificLayout.findViewById(R.id.map_zoom).findViewById(R.id.property_seekbar)).setProgress(Math.round(zoom * 10));
        ((TextView) specificLayout.findViewById(R.id.map_zoom).findViewById(R.id.property_value)).setText(zoomRounded + "");

        SKCoordinate center = activity.getMapView().getMapCenter();
        double lat = ((int) (center.getLatitude() * Math.pow(10, 6))) / Math.pow(10, 6);
        double lon = ((int) (center.getLongitude() * Math.pow(10, 6))) / Math.pow(10, 6);
        ((EditText) specificLayout.findViewById(R.id.map_position_latitude).findViewById(R.id.property_value)).setText(lat + "");
        ((EditText) specificLayout.findViewById(R.id.map_position_longitude).findViewById(R.id.property_value)).setText(lon + "");

        int bearing = Math.round(activity.getMapView().getMapBearing());
        ((SeekBar) specificLayout.findViewById(R.id.map_bearing).findViewById(R.id.property_seekbar)).setProgress(bearing);
        ((TextView) specificLayout.findViewById(R.id.map_bearing).findViewById(R.id.property_value)).setText(bearing + "");
    }
}

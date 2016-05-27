package com.skobbler.debugkit.debugsettings;

import android.util.Pair;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/5/2015.
 */
public class ZoomLimitDebugSettings extends DebugSettings {
    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.min_zoom), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.max_zoom), 180));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_zoom_limits;
    }

    @Override
    void defineSpecificListeners() {
        final TextView minValue = (TextView) specificLayout.findViewById(R.id.min_zoom).findViewById(R.id.property_value);
        minValue.setText("0");
        SeekBar minSeekBar = (SeekBar) specificLayout.findViewById(R.id.min_zoom).findViewById(R.id.property_seekbar);
        minSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                minValue.setText((float) i / 10 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                applyZoomLimits();
            }
        });

        final TextView maxValue = (TextView) specificLayout.findViewById(R.id.max_zoom).findViewById(R.id.property_value);
        maxValue.setText("18.0");
        SeekBar maxSeekBar = (SeekBar) specificLayout.findViewById(R.id.max_zoom).findViewById(R.id.property_seekbar);
        maxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                maxValue.setText((float) i / 10 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                applyZoomLimits();
            }
        });
    }

    private void applyZoomLimits() {
        float minZoom = (float) ((SeekBar) specificLayout.findViewById(R.id.min_zoom).findViewById(R.id.property_seekbar)).getProgress() / 10;
        float maxZoom = (float) ((SeekBar) specificLayout.findViewById(R.id.max_zoom).findViewById(R.id.property_seekbar)).getProgress() / 10;
        activity.getMapView().getMapSettings().setZoomLimits(minZoom, maxZoom);
    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        SeekBar minSeekBar = (SeekBar) specificLayout.findViewById(R.id.min_zoom).findViewById(R.id.property_seekbar);
        minSeekBar.setMax(180);
        minSeekBar.setProgress(0);
        SeekBar maxSeekBar = (SeekBar) specificLayout.findViewById(R.id.max_zoom).findViewById(R.id.property_seekbar);
        maxSeekBar.setMax(180);
        maxSeekBar.setProgress(180);
    }
}

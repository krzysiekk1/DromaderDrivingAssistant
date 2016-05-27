package com.skobbler.debugkit.debugsettings;

import android.util.Pair;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SK3DCameraSettings;
import com.skobbler.ngx.map.SKMapSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/9/2015.
 */
public class CameraDebugSettings extends DebugSettings {

    private SKMapSettings.SKMapDisplayMode initialMapMode;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.center), 20));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.tilt), 15));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.distance), 144));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_camera;
    }

    @Override
    void onOpened() {
        super.onOpened();
        initialMapMode = activity.getMapView().getMapSettings().getMapDisplayMode();
        activity.getMapView().getMapSettings().setMapDisplayMode(SKMapSettings.SKMapDisplayMode.MODE_3D);
        applyCameraSettings();
    }

    @Override
    void onClose() {
        super.onClose();
        activity.getMapView().getMapSettings().setMapDisplayMode(initialMapMode);
    }

    private void applyCameraSettings() {
        SeekBar centerSeekBar = (SeekBar) specificLayout.findViewById(R.id.camera_center).findViewById(R.id.property_seekbar);
        float center = (float) centerSeekBar.getProgress() / 100 + (1/10);
        SeekBar tiltSeekBar = (SeekBar) specificLayout.findViewById(R.id.camera_tilt).findViewById(R.id.property_seekbar);
        float tilt = tiltSeekBar.getProgress();
        SeekBar distanceSeekBar = (SeekBar) specificLayout.findViewById(R.id.camera_distance).findViewById(R.id.property_seekbar);
        float distance = 30 + distanceSeekBar.getProgress();

        SK3DCameraSettings cameraSettings = new SK3DCameraSettings();
        cameraSettings.setCenter(center);
        cameraSettings.setTilt(tilt);
        cameraSettings.setDistance(distance);
        activity.getMapView().getMapSettings().setCameraSettings(cameraSettings);
    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        SeekBar centerSeekBar = (SeekBar) specificLayout.findViewById(R.id.camera_center).findViewById(R.id.property_seekbar);
        centerSeekBar.setMax(80);
        SeekBar tiltSeekBar = (SeekBar) specificLayout.findViewById(R.id.camera_tilt).findViewById(R.id.property_seekbar);
        tiltSeekBar.setMax(90);
        SeekBar distanceSeekBar = (SeekBar) specificLayout.findViewById(R.id.camera_distance).findViewById(R.id.property_seekbar);
        distanceSeekBar.setMax(270);
    }

    @Override
    void defineSpecificListeners() {
        final TextView centerValue = (TextView) specificLayout.findViewById(R.id.camera_center).findViewById(R.id.property_value);
        centerValue.setText("0.3");
        SeekBar centerSeekBar = (SeekBar) specificLayout.findViewById(R.id.camera_center).findViewById(R.id.property_seekbar);
        centerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                centerValue.setText(((float) i / 100) + (1/10) + "");
                applyCameraSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final TextView tiltValue = (TextView) specificLayout.findViewById(R.id.camera_tilt).findViewById(R.id.property_value);
        tiltValue.setText("15");
        SeekBar tiltSeekBar = (SeekBar) specificLayout.findViewById(R.id.camera_tilt).findViewById(R.id.property_seekbar);
        tiltSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tiltValue.setText(i + "");
                applyCameraSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final TextView distanceValue = (TextView) specificLayout.findViewById(R.id.camera_distance).findViewById(R.id.property_value);
        distanceValue.setText("144");
        SeekBar distanceSeekBar = (SeekBar) specificLayout.findViewById(R.id.camera_distance).findViewById(R.id.property_seekbar);
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                distanceValue.setText((i + 30) + "");
                applyCameraSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}

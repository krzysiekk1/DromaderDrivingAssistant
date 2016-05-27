package com.skobbler.debugkit.debugsettings;

import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.trail.SKTrailManager;
import com.skobbler.ngx.trail.SKTrailPosition;
import com.skobbler.ngx.trail.SKTrailType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/9/2015.
 */
public class TrailDebugSettings extends DebugSettings {

    private boolean trailWasSet;

    private SKTrailType trailType = new SKTrailType();

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.enabled), true));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.dotted), false));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.red), 1000));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.green), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.blue), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.alpha), 1000));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.width), 5));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.pedestrian_trail), false));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.smoothness), 1));
        return pairs;
    }

    @Override
    void onOpened() {
        super.onOpened();
        if (!trailWasSet) {
            List<SKTrailPosition> trailPoints = new ArrayList<SKTrailPosition>();
            trailPoints.add(new SKTrailPosition(13.423231, 52.520122));
            trailPoints.add(new SKTrailPosition(13.423907, 52.520879));
            trailPoints.add(new SKTrailPosition(13.424550, 52.521662));
            trailPoints.add(new SKTrailPosition(13.422930, 52.522152));
            trailPoints.add(new SKTrailPosition(13.423499, 52.522909));
            trailPoints.add(new SKTrailPosition(13.424014, 52.523542));
            SKTrailManager.getInstance().setTrailPoints(trailPoints);
            applyTrailSettings();
            trailWasSet = true;
        }
        activity.getMapView().rotateTheMapToNorth();
        activity.getMapView().centerMapOnPosition(new SKCoordinate(13.420195, 52.522157));
        activity.getMapView().setZoom(15.7f);
    }

    private void applyTrailSettings() {
        SKTrailManager trailManager = SKTrailManager.getInstance();

        SeekBar red = (SeekBar) specificLayout.findViewById(R.id.trail_red).findViewById(R.id.property_seekbar);
        float redValue = (float) red.getProgress() / 1000;
        SeekBar green = (SeekBar) specificLayout.findViewById(R.id.trail_green).findViewById(R.id.property_seekbar);
        float greenValue = (float) green.getProgress() / 1000;
        SeekBar blue = (SeekBar) specificLayout.findViewById(R.id.trail_blue).findViewById(R.id.property_seekbar);
        float blueValue = (float) blue.getProgress() / 1000;
        SeekBar alpha = (SeekBar) specificLayout.findViewById(R.id.trail_alpha).findViewById(R.id.property_seekbar);
        float alphaValue = (float) alpha.getProgress() / 1000;
        trailType.setColor(new float[]{redValue, greenValue, blueValue, alphaValue});

        boolean dotted = ((CheckBox) specificLayout.findViewById(R.id.trail_dotted).findViewById(R.id.property_value)).isChecked();
        trailType.setDotted(dotted);
        int width = ((SeekBar) specificLayout.findViewById(R.id.trail_width).findViewById(R.id.property_seekbar)).getProgress();
        trailType.setSize(width);

        trailManager.setTrailType(trailType);

        boolean pedestrianTrail = ((CheckBox) specificLayout.findViewById(R.id.trail_pedestrian).findViewById(R.id.property_value)).isChecked();
        int smoothness = ((SeekBar) specificLayout.findViewById(R.id.trail_smooth_level).findViewById(R.id.property_seekbar)).getProgress();
        SKRouteManager.getInstance().enablePedestrianTrail(pedestrianTrail,smoothness);
        trailType.setPedestrianTrailEnabled(pedestrianTrail, smoothness);

        boolean shown = ((CheckBox) specificLayout.findViewById(R.id.trail_enabled).findViewById(R.id.property_value)).isChecked();
        trailManager.setShowTrail(shown);

        activity.getMapView().requestRender();
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_trail;
    }

    @Override
    void defineSpecificListeners() {
        final TextView redValue = (TextView) specificLayout.findViewById(R.id.trail_red).findViewById(R.id.property_value);
        redValue.setText((Float.parseFloat(redValue.getText().toString()) / 1000) + "");
        SeekBar redSeekBar = (SeekBar) specificLayout.findViewById(R.id.trail_red).findViewById(R.id.property_seekbar);
        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                redValue.setText((float) i / 1000 + "");
                applyTrailSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final TextView alphaValue = (TextView) specificLayout.findViewById(R.id.trail_alpha).findViewById(R.id.property_value);
        alphaValue.setText((Float.parseFloat(alphaValue.getText().toString()) / 1000) + "");
        SeekBar alphaSeekBar = (SeekBar) specificLayout.findViewById(R.id.trail_alpha).findViewById(R.id.property_seekbar);
        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                alphaValue.setText((float) i / 1000 + "");
                applyTrailSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final TextView blueValue = (TextView) specificLayout.findViewById(R.id.trail_blue).findViewById(R.id.property_value);
        blueValue.setText((Float.parseFloat(blueValue.getText().toString()) / 1000) + "");
        SeekBar blueSeekBar = (SeekBar) specificLayout.findViewById(R.id.trail_blue).findViewById(R.id.property_seekbar);
        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blueValue.setText((float) i / 1000 + "");
                applyTrailSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                applyTrailSettings();
            }
        });

        final TextView greenValue = (TextView) specificLayout.findViewById(R.id.trail_green).findViewById(R.id.property_value);
        greenValue.setText((Float.parseFloat(greenValue.getText().toString()) / 1000) + "");
        SeekBar greenSeekBar = (SeekBar) specificLayout.findViewById(R.id.trail_green).findViewById(R.id.property_seekbar);
        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                greenValue.setText((float) i / 1000 + "");
                applyTrailSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                applyTrailSettings();
            }
        });

        final TextView widthValue = (TextView) specificLayout.findViewById(R.id.trail_width).findViewById(R.id.property_value);
        SeekBar widthSeekBar = (SeekBar) specificLayout.findViewById(R.id.trail_width).findViewById(R.id.property_seekbar);
        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                widthValue.setText(2 + i + "");
                applyTrailSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final TextView smoothValue = (TextView) specificLayout.findViewById(R.id.trail_smooth_level).findViewById(R.id.property_value);
        SeekBar smoothSeekBar = (SeekBar) specificLayout.findViewById(R.id.trail_smooth_level).findViewById(R.id.property_seekbar);
        smoothSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                smoothValue.setText(1 + i + "");
                applyTrailSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final View enabledView = specificLayout.findViewById(R.id.trail_enabled);
        enabledView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox enabledCheckBox = (CheckBox) enabledView.findViewById(R.id.property_value);
                enabledCheckBox.setChecked(!enabledCheckBox.isChecked());
                applyTrailSettings();
            }
        });

        final View dottedView = specificLayout.findViewById(R.id.trail_dotted);
        dottedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox dotttedCheckBox = (CheckBox) dottedView.findViewById(R.id.property_value);
                dotttedCheckBox.setChecked(!dotttedCheckBox.isChecked());
                applyTrailSettings();
            }
        });

        final View pedestrianTrailView = specificLayout.findViewById(R.id.trail_pedestrian);
        pedestrianTrailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox pedestrianCheckBox = (CheckBox) pedestrianTrailView.findViewById(R.id.property_value);
                pedestrianCheckBox.setChecked(!pedestrianCheckBox.isChecked());
                applyTrailSettings();
            }
        });
    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        SeekBar widthSeekBar = (SeekBar) specificLayout.findViewById(R.id.trail_width).findViewById(R.id.property_seekbar);
        widthSeekBar.setMax(8);
        SeekBar smoothnessSeekBar = (SeekBar) specificLayout.findViewById(R.id.trail_smooth_level).findViewById(R.id.property_seekbar);
        smoothnessSeekBar.setMax(9);
    }
}

package com.skobbler.debugkit.debugsettings;

import android.util.Pair;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/30/2015.
 */
public class ColorDebugSettings extends DebugSettings {

    private float[] color;

    public ColorDebugSettings(float[] color) {
        this.color = color;
    }

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.red), color[0]));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.green), color[1]));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.blue), color[2]));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.alpha), color[3]));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_color;
    }

    @Override
    void defineSpecificListeners() {
        final TextView redValue = (TextView) specificLayout.findViewById(R.id.red_seekbar).findViewById(R.id.property_value);
        redValue.setText(color[0] + "");
        SeekBar redSeekBar = (SeekBar) specificLayout.findViewById(R.id.red_seekbar).findViewById(R.id.property_seekbar);
        redSeekBar.setProgress((int) (color[0] * 1000));
        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                redValue.setText((float) i / 1000 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                color[0] = (float) seekBar.getProgress() / 1000;
            }
        });

        final TextView greenValue = (TextView) specificLayout.findViewById(R.id.green_seekbar).findViewById(R.id.property_value);
        greenValue.setText(color[1] + "");
        SeekBar greenSeekBar = (SeekBar) specificLayout.findViewById(R.id.green_seekbar).findViewById(R.id.property_seekbar);
        greenSeekBar.setProgress((int) (color[1] * 1000));
        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                greenValue.setText((float) i / 1000 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                color[1] = (float) seekBar.getProgress() / 1000;
            }
        });

        final TextView blueValue = (TextView) specificLayout.findViewById(R.id.blue_seekbar).findViewById(R.id.property_value);
        blueValue.setText(color[2] + "");
        SeekBar blueSeekBar = (SeekBar) specificLayout.findViewById(R.id.blue_seekbar).findViewById(R.id.property_seekbar);
        blueSeekBar.setProgress((int) (color[2] * 1000));
        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blueValue.setText((float) i / 1000 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                color[2] = (float) seekBar.getProgress() / 1000;
            }
        });

        final TextView alphaValue = (TextView) specificLayout.findViewById(R.id.alpha_seekbar).findViewById(R.id.property_value);
        alphaValue.setText(color[3] + "");
        SeekBar alphaSeekBar = (SeekBar) specificLayout.findViewById(R.id.alpha_seekbar).findViewById(R.id.property_seekbar);
        alphaSeekBar.setProgress((int) (color[3] * 1000));
        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                alphaValue.setText((float) i / 1000 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                color[3] = (float) seekBar.getProgress() / 1000;
            }
        });
    }

    public float[] getColor() {
        return color;
    }
}

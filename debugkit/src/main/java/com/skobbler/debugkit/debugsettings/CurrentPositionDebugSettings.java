package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKPulseAnimationSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 5/29/2015.
 */
public class CurrentPositionDebugSettings extends DebugSettings {

    CurrentPositionDebugSettings() {
    }

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();
        SKPulseAnimationSettings initialAnimationSettings = getPulseAnimationSettings();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animation_settings), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.red), (int) (1000 * initialAnimationSettings.getColor()[0])));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.green), (int) (1000 * initialAnimationSettings.getColor()[1])));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.blue), (int) (1000 * initialAnimationSettings.getColor()[2])));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.alpha), (int) (1000 * initialAnimationSettings.getColor()[3])));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.continuous), initialAnimationSettings.isContinuous()));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.span), (int) initialAnimationSettings.getSpan()));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.duration), initialAnimationSettings.getDuration() + " ms"));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.duration_out), initialAnimationSettings.getDurationOut() + " ms"));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.operations), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.use_custom_view), false));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.go_to_current_position), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.start_animation), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.stop_animation), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_ccp_customization;
    }

    @Override
    void defineSpecificListeners() {

        final TextView redValue = (TextView) specificLayout.findViewById(R.id.red_seekbar).findViewById(R.id.property_value);
        redValue.setText((Float.parseFloat(redValue.getText().toString()) / 1000) + "");
        SeekBar redSeekBar = (SeekBar) specificLayout.findViewById(R.id.red_seekbar).findViewById(R.id.property_seekbar);
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
                activity.getMapView().setCurrentPositionIconPulseAnimation(getPulseAnimationSettings());
            }
        });

        final TextView greenValue = (TextView) specificLayout.findViewById(R.id.green_seekbar).findViewById(R.id.property_value);
        greenValue.setText((Float.parseFloat(greenValue.getText().toString()) / 1000) + "");
        SeekBar greenSeekBar = (SeekBar) specificLayout.findViewById(R.id.green_seekbar).findViewById(R.id.property_seekbar);
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
                activity.getMapView().setCurrentPositionIconPulseAnimation(getPulseAnimationSettings());
            }
        });

        final TextView blueValue = (TextView) specificLayout.findViewById(R.id.blue_seekbar).findViewById(R.id.property_value);
        blueValue.setText((Float.parseFloat(blueValue.getText().toString()) / 1000) + "");
        SeekBar blueSeekBar = (SeekBar) specificLayout.findViewById(R.id.blue_seekbar).findViewById(R.id.property_seekbar);
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
                activity.getMapView().setCurrentPositionIconPulseAnimation(getPulseAnimationSettings());
            }
        });

        final TextView alphaValue = (TextView) specificLayout.findViewById(R.id.alpha_seekbar).findViewById(R.id.property_value);
        alphaValue.setText((Float.parseFloat(alphaValue.getText().toString()) / 1000) + "");
        SeekBar alphaSeekBar = (SeekBar) specificLayout.findViewById(R.id.alpha_seekbar).findViewById(R.id.property_seekbar);
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
                activity.getMapView().setCurrentPositionIconPulseAnimation(getPulseAnimationSettings());
            }
        });

        final TextView durationValue = (TextView) specificLayout.findViewById(R.id.duration_seekbar).findViewById(R.id.property_value);
        SeekBar durationSeekBar = (SeekBar) specificLayout.findViewById(R.id.duration_seekbar).findViewById(R.id.property_seekbar);
        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                durationValue.setText(i + " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                activity.getMapView().setCurrentPositionIconPulseAnimation(getPulseAnimationSettings());
            }
        });

        final TextView durationOutValue = (TextView) specificLayout.findViewById(R.id.duration_out_seekbar).findViewById(R.id.property_value);
        SeekBar durationOutSeekBar = (SeekBar) specificLayout.findViewById(R.id.duration_out_seekbar).findViewById(R.id.property_seekbar);
        durationOutSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                durationOutValue.setText(i + " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                activity.getMapView().setCurrentPositionIconPulseAnimation(getPulseAnimationSettings());
            }
        });

        final TextView spanValue = (TextView) specificLayout.findViewById(R.id.span_seekbar).findViewById(R.id.property_value);
        SeekBar spanSeekBar = (SeekBar) specificLayout.findViewById(R.id.span_seekbar).findViewById(R.id.property_seekbar);
        spanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                spanValue.setText(i + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                activity.getMapView().setCurrentPositionIconPulseAnimation(getPulseAnimationSettings());
            }
        });

        final View continuousSettingView = specificLayout.findViewById(R.id.continuous_check);
        continuousSettingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox continuousCheckBox = (CheckBox) continuousSettingView.findViewById(R.id.property_value);
                continuousCheckBox.setChecked(!continuousCheckBox.isChecked());
                activity.getMapView().setCurrentPositionIconPulseAnimation(getPulseAnimationSettings());
            }
        });

        specificLayout.findViewById(R.id.go_to_current_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity.getCurrentPosition() != null) {
                    activity.getMapView().centerMapOnCurrentPositionSmooth(17, 1500);
                } else {
                    Toast.makeText(activity, "Current position not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        specificLayout.findViewById(R.id.stop_animation_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getMapView().stopCurrentPositionIconPulseAnimation();
            }
        });

        specificLayout.findViewById(R.id.start_animation_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getMapView().setCurrentPositionIconPulseAnimation(getPulseAnimationSettings());
            }
        });


        final View customViewOption = specificLayout.findViewById(R.id.custom_view_checkbox);
        customViewOption.setOnClickListener(new View.OnClickListener() {

            View customView;

            @Override
            public void onClick(View view) {
                CheckBox customViewCheckBox = (CheckBox) customViewOption.findViewById(R.id.property_value);
                customViewCheckBox.setChecked(!customViewCheckBox.isChecked());
                if (customViewCheckBox.isChecked()) {
                    if (customView == null) {
                        LayoutInflater inflater = (LayoutInflater) specificLayout.getContext().getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
                        customView = inflater.inflate(R.layout.layout_custom_view, null, false);
                        customView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    }
                    activity.getMapView().showCurrentPositionIconForCcp(false);
                    activity.getMapView().setCurrentPositionIconFromView(customView);
                } else {
                    activity.getMapView().showCurrentPositionIconForCcp(true);
                }
            }
        });
    }

    private SKPulseAnimationSettings getPulseAnimationSettings() {
        SKPulseAnimationSettings pulseAnimationSettings = new SKPulseAnimationSettings();

        float[] color = new float[4];
        color[0] = (float) ((SeekBar) specificLayout.findViewById(R.id.red_seekbar).findViewById(R.id.property_seekbar)).getProgress() / 1000;
        color[1] = (float) ((SeekBar) specificLayout.findViewById(R.id.green_seekbar).findViewById(R.id.property_seekbar)).getProgress() / 1000;
        color[2] = (float) ((SeekBar) specificLayout.findViewById(R.id.blue_seekbar).findViewById(R.id.property_seekbar)).getProgress() / 1000;
        color[3] = (float) ((SeekBar) specificLayout.findViewById(R.id.alpha_seekbar).findViewById(R.id.property_seekbar)).getProgress() / 1000;
        pulseAnimationSettings.setColor(color);

        boolean continuous = ((CheckBox) specificLayout.findViewById(R.id.continuous_check).findViewById(R.id.property_value)).isChecked();
        pulseAnimationSettings.setContinuous(continuous);

        int duration = ((SeekBar) specificLayout.findViewById(R.id.duration_seekbar).findViewById(R.id.property_seekbar)).getProgress();
        pulseAnimationSettings.setDuration(duration);

        int durationOut = ((SeekBar) specificLayout.findViewById(R.id.duration_out_seekbar).findViewById(R.id.property_seekbar)).getProgress();
        pulseAnimationSettings.setDurationOut(durationOut);

        int span = ((SeekBar) specificLayout.findViewById(R.id.span_seekbar).findViewById(R.id.property_seekbar)).getProgress();
        pulseAnimationSettings.setSpan(span);

        return pulseAnimationSettings;
    }

    @Override
    void applyCustomChangesToUI() {
        SeekBar durationSeekBar = (SeekBar) specificLayout.findViewById(R.id.duration_seekbar).findViewById(R.id.property_seekbar);
        durationSeekBar.setMax(10000);
        durationSeekBar.setProgress(1300);

        SeekBar durationOutSeekBar = (SeekBar) specificLayout.findViewById(R.id.duration_out_seekbar).findViewById(R.id.property_seekbar);
        durationOutSeekBar.setMax(5000);
        durationOutSeekBar.setProgress(260);

        SeekBar spanSeekBar = (SeekBar) specificLayout.findViewById(R.id.span_seekbar).findViewById(R.id.property_seekbar);
        spanSeekBar.setMax(10);
        spanSeekBar.setProgress(2);

        ((SeekBar) specificLayout.findViewById(R.id.red_seekbar).findViewById(R.id.property_seekbar)).setProgress(165);
        ((SeekBar) specificLayout.findViewById(R.id.green_seekbar).findViewById(R.id.property_seekbar)).setProgress(510);
        ((SeekBar) specificLayout.findViewById(R.id.blue_seekbar).findViewById(R.id.property_seekbar)).setProgress(675);
        ((SeekBar) specificLayout.findViewById(R.id.alpha_seekbar).findViewById(R.id.property_seekbar)).setProgress(1000);

        ((CheckBox) specificLayout.findViewById(R.id.continuous_check).findViewById(R.id.property_value)).setChecked(true);
    }
}
package com.skobbler.debugkit.debugsettings;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKCircle;
import com.skobbler.ngx.map.SKScreenPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tudor on 6/29/2015.
 */
public class CircleDebugSettings extends DebugSettings {

    public static boolean drawOnTap;

    private ColorDebugSettings fillColorSettings = new ColorDebugSettings(new float[]{0, 0, 0, 1});

    private ColorDebugSettings outlineColorSettings = new ColorDebugSettings(new float[]{0, 0, 0, 1});

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.id), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_width), 3));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_pixels_solid), 6));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_pixels_skip), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.number_of_points), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.fill_color), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_color), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.is_mask), false));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.masked_object_scale), 15));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.latitude), ""));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.longitude), ""));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.radius), 100));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.draw_on_tap), false));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.generate_id), false));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_circle;
    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        ((SeekBar) specificLayout.findViewById(R.id.border_width).findViewById(R.id.property_seekbar)).setMax(19);
        ((SeekBar) specificLayout.findViewById(R.id.border_pixels_solid).findViewById(R.id.property_seekbar)).setMax(19);
        ((SeekBar) specificLayout.findViewById(R.id.border_pixels_skip).findViewById(R.id.property_seekbar)).setMax(20);
        ((SeekBar) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_seekbar)).setMax(30);
        specificLayout.findViewById(R.id.masked_object_scale).setVisibility(View.GONE);
    }

    @Override
    void defineSpecificListeners() {
        specificLayout.findViewById(R.id.latitude).findViewById(R.id.property_value).setEnabled(false);
        specificLayout.findViewById(R.id.longitude).findViewById(R.id.property_value).setEnabled(false);
        ((EditText) specificLayout.findViewById(R.id.radius).findViewById(R.id.property_value)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ((EditText) specificLayout.findViewById(R.id.number_of_points).findViewById(R.id.property_value)).setInputType(InputType.TYPE_CLASS_NUMBER);
        ((EditText) specificLayout.findViewById(R.id.circle_id).findViewById(R.id.property_value)).setInputType(InputType.TYPE_CLASS_NUMBER);
        generateRandomId();

        int seekBarInitValue = ((SeekBar) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_seekbar)).getProgress();
        ((TextView) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_value)).setText(((float) seekBarInitValue / 10) + "");

        int outlineWidthInitValue = ((SeekBar) specificLayout.findViewById(R.id.border_width).findViewById(R.id.property_seekbar)).getProgress();
        ((TextView) specificLayout.findViewById(R.id.border_width).findViewById(R.id.property_value)).setText("" + (outlineWidthInitValue + 1));

        int outlineSolidInitValue = ((SeekBar) specificLayout.findViewById(R.id.border_pixels_solid).findViewById(R.id.property_seekbar)).getProgress();
        ((TextView) specificLayout.findViewById(R.id.border_pixels_solid).findViewById(R.id.property_value)).setText("" + (outlineSolidInitValue + 1));

        final View isMaskView = specificLayout.findViewById(R.id.is_mask);
        isMaskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) isMaskView.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                specificLayout.findViewById(R.id.masked_object_scale).setVisibility(checkBox.isChecked() ? View.VISIBLE : View.GONE);
            }
        });

        SeekBar maskScaleSeekBar = (SeekBar) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_seekbar);
        maskScaleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_value)).setText(((float) value / 10) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar outlineWidthSeekBar = (SeekBar) specificLayout.findViewById(R.id.border_width).findViewById(R.id.property_seekbar);
        outlineWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.border_width).findViewById(R.id.property_value)).setText((value + 1) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar outlineSolidSeekBar = (SeekBar) specificLayout.findViewById(R.id.border_pixels_solid).findViewById(R.id.property_seekbar);
        outlineSolidSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.border_pixels_solid).findViewById(R.id.property_value)).setText((value + 1) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar outlineSkipSeekBar = (SeekBar) specificLayout.findViewById(R.id.border_pixels_skip).findViewById(R.id.property_seekbar);
        outlineSkipSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.border_pixels_skip).findViewById(R.id.property_value)).setText("" + value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        View tapToDrawView = specificLayout.findViewById(R.id.tap_to_draw);
        final CheckBox tapToDrawCheckBox = (CheckBox) tapToDrawView.findViewById(R.id.property_value);
        tapToDrawView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tapToDrawCheckBox.setChecked(!tapToDrawCheckBox.isChecked());
                drawOnTap = tapToDrawCheckBox.isChecked();
            }
        });

        specificLayout.findViewById(R.id.fill_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillColorSettings.open(debugBaseLayout, CircleDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.outline_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outlineColorSettings.open(debugBaseLayout, CircleDebugSettings.this);
            }
        });

        final EditText pointsEditText = (EditText) specificLayout.findViewById(R.id.number_of_points).findViewById(R.id.property_value);
        final int initialColor = pointsEditText.getCurrentTextColor();
        pointsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int value = Integer.parseInt(pointsEditText.getText().toString());
                    if ((value < 36 || value > 18000) && value != 0) {
                        pointsEditText.setTextColor(activity.getResources().getColor(R.color.red));
                    } else {
                        pointsEditText.setTextColor(initialColor);
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        final EditText idEditText = (EditText) specificLayout.findViewById(R.id.circle_id).findViewById(R.id.property_value);
        idEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                resetCenter();
            }
        });

        specificLayout.findViewById(R.id.generate_new_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomId();
            }
        });
    }

    private void resetCenter() {
        ((EditText) specificLayout.findViewById(R.id.latitude).findViewById(R.id.property_value)).setText("");
        ((EditText) specificLayout.findViewById(R.id.longitude).findViewById(R.id.property_value)).setText("");
        drawOnTap = false;
        ((CheckBox) specificLayout.findViewById(R.id.tap_to_draw).findViewById(R.id.property_value)).setChecked(false);
    }

    private void generateRandomId() {
        Random random = new Random(System.currentTimeMillis());
        int id = random.nextInt(10000);
        ((EditText) specificLayout.findViewById(R.id.circle_id).findViewById(R.id.property_value)).setText("" + id);
        resetCenter();
    }

    public void drawCircleOnTap(SKScreenPoint screenPoint) {
        SKCoordinate center = activity.getMapView().pointToCoordinate(screenPoint);
        SKCircle circle = getCircleFromSettings();
        double lat = (double) ((int) (center.getLatitude() * Math.pow(10, 6))) / Math.pow(10, 6);
        double lon = (double) ((int) (center.getLongitude() * Math.pow(10, 6))) / Math.pow(10, 6);
        ((EditText) specificLayout.findViewById(R.id.latitude).findViewById(R.id.property_value)).setText("" + lat);
        ((EditText) specificLayout.findViewById(R.id.longitude).findViewById(R.id.property_value)).setText("" + lon);
        circle.setCircleCenter(center);
        ((OverlaysDebugSettings) parentSettings).getOverlayMap().put(circle.getIdentifier(), circle);
        activity.getMapView().addCircle(circle);
    }

    private SKCircle getCircleFromSettings() {
        SKCircle circle = new SKCircle();
        circle.setColor(fillColorSettings.getColor());
        circle.setOutlineColor(outlineColorSettings.getColor());

        EditText radiusEditText = (EditText) specificLayout.findViewById(R.id.radius).findViewById(R.id.property_value);
        try {
            float radiusValue = Float.parseFloat(radiusEditText.getText().toString());
            circle.setRadius(radiusValue);
        } catch (NumberFormatException e) {
        }

        EditText pointsEditText = (EditText) specificLayout.findViewById(R.id.number_of_points).findViewById(R.id.property_value);
        try {
            int pointsValue = Integer.parseInt(pointsEditText.getText().toString());
            circle.setNumberOfPoints(pointsValue);
        } catch (NumberFormatException e) {
        }

        EditText idEditText = (EditText) specificLayout.findViewById(R.id.circle_id).findViewById(R.id.property_value);
        try {
            int id = Integer.parseInt(idEditText.getText().toString());
            circle.setIdentifier(id);
        } catch (NumberFormatException e) {
            circle.setIdentifier(0);
        }

        boolean isMasked = ((CheckBox) specificLayout.findViewById(R.id.is_mask).findViewById(R.id.property_value)).isChecked();
        if (isMasked) {
            SeekBar maskScaleSeekBar = (SeekBar) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_seekbar);
            circle.setMaskedObjectScale((float) maskScaleSeekBar.getProgress() / 10);
        }

        SeekBar outlineSize = (SeekBar) specificLayout.findViewById(R.id.border_width).findViewById(R.id.property_seekbar);
        circle.setOutlineSize(outlineSize.getProgress() + 1);

        SeekBar outlineSolidSize = (SeekBar) specificLayout.findViewById(R.id.border_pixels_solid).findViewById(R.id.property_seekbar);
        circle.setOutlineDottedPixelsSolid(outlineSolidSize.getProgress() + 1);

        SeekBar outlineSolidSkip = (SeekBar) specificLayout.findViewById(R.id.border_pixels_skip).findViewById(R.id.property_seekbar);
        circle.setOutlineDottedPixelsSkip(outlineSolidSkip.getProgress());

        return circle;
    }
}

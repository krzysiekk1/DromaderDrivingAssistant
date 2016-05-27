package com.skobbler.debugkit.debugsettings;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKPolygon;
import com.skobbler.ngx.map.SKScreenPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tudor on 7/1/2015.
 */
public class PolygonDebugSettings extends DebugSettings {

    public static boolean drawOnTap;

    private ColorDebugSettings fillColorSettings = new ColorDebugSettings(new float[]{0, 0, 0, 1});

    private ColorDebugSettings outlineColorSettings = new ColorDebugSettings(new float[]{0, 0, 0, 1});

    List<SKCoordinate> nodes = new ArrayList<SKCoordinate>();

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.id), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_width), 3));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_pixels_solid), 6));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_pixels_skip), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.fill_color), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_color), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.is_mask), false));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.masked_object_scale), 15));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.polygon_points), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.draw_on_tap), false));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.generate_id), false));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_polygon;
    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        ((SeekBar) specificLayout.findViewById(R.id.outline_width).findViewById(R.id.property_seekbar)).setMax(19);
        ((SeekBar) specificLayout.findViewById(R.id.outline_pixels_solid).findViewById(R.id.property_seekbar)).setMax(19);
        ((SeekBar) specificLayout.findViewById(R.id.outline_pixels_skip).findViewById(R.id.property_seekbar)).setMax(20);
        ((SeekBar) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_seekbar)).setMax(30);
        specificLayout.findViewById(R.id.masked_object_scale).setVisibility(View.GONE);
    }

    @Override
    void defineSpecificListeners() {
        generateRandomId();

        int seekBarInitValue = ((SeekBar) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_seekbar)).getProgress();
        ((TextView) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_value)).setText(((float) seekBarInitValue / 10) + "");

        specificLayout.findViewById(R.id.generate_new_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomId();
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

        SeekBar outlineSolidSeekBar = (SeekBar) specificLayout.findViewById(R.id.outline_pixels_solid).findViewById(R.id.property_seekbar);
        outlineSolidSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.outline_pixels_solid).findViewById(R.id.property_value)).setText((value + 1) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar outlineSkipSeekBar = (SeekBar) specificLayout.findViewById(R.id.outline_pixels_skip).findViewById(R.id.property_seekbar);
        outlineSkipSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.outline_pixels_skip).findViewById(R.id.property_value)).setText("" + value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final EditText idEditText = (EditText) specificLayout.findViewById(R.id.polygon_id).findViewById(R.id.property_value);
        idEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                resetNodes();
            }
        });

        SeekBar outlineWidthSeekBar = (SeekBar) specificLayout.findViewById(R.id.outline_width).findViewById(R.id.property_seekbar);
        outlineWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.outline_width).findViewById(R.id.property_value)).setText(value + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        specificLayout.findViewById(R.id.fill_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillColorSettings.open(debugBaseLayout, PolygonDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.outline_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outlineColorSettings.open(debugBaseLayout, PolygonDebugSettings.this);
            }
        });

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
    }

    private SKPolygon getPolygonFromSettings() {
        SKPolygon polygon = new SKPolygon();
        polygon.setColor(fillColorSettings.getColor());
        polygon.setOutlineColor(outlineColorSettings.getColor());

        EditText idText = (EditText) specificLayout.findViewById(R.id.polygon_id).findViewById(R.id.property_value);
        try {
            int id = Integer.parseInt(idText.getText().toString());
            polygon.setIdentifier(id);
        } catch (NumberFormatException e) {
        }

        SeekBar outlineSizeSeekBar = (SeekBar) specificLayout.findViewById(R.id.outline_width).findViewById(R.id.property_seekbar);
        polygon.setOutlineSize(outlineSizeSeekBar.getProgress());

        SeekBar outlineSolidSize = (SeekBar) specificLayout.findViewById(R.id.outline_pixels_solid).findViewById(R.id.property_seekbar);
        polygon.setOutlineDottedPixelsSolid(outlineSolidSize.getProgress() + 1);

        SeekBar outlineSkipSize = (SeekBar) specificLayout.findViewById(R.id.outline_pixels_skip).findViewById(R.id.property_seekbar);
        polygon.setOutlineDottedPixelsSkip(outlineSkipSize.getProgress());

        boolean isMasked = ((CheckBox) specificLayout.findViewById(R.id.is_mask).findViewById(R.id.property_value)).isChecked();
        if (isMasked) {
            SeekBar maskScaleSeekBar = (SeekBar) specificLayout.findViewById(R.id.masked_object_scale).findViewById(R.id.property_seekbar);
            polygon.setMaskedObjectScale((float) maskScaleSeekBar.getProgress() / 10);
        }

        polygon.setNodes(nodes);

        return polygon;
    }

    public void addPolygonNode(SKScreenPoint screenPoint) {
        SKCoordinate position = activity.getMapView().pointToCoordinate(screenPoint);
        if (nodes.isEmpty()) {
            nodes.add(position);
            nodes.add(position);
        } else {
            nodes.add(nodes.size() - 1, position);
        }
        ((TextView) specificLayout.findViewById(R.id.polygon_points).findViewById(R.id.property_name)).
                setText(activity.getResources().getString(R.string.polygon_points) + " " + (nodes.isEmpty() ? 0 : nodes.size() - 1));
        SKPolygon polygon = getPolygonFromSettings();
        ((OverlaysDebugSettings) parentSettings).getOverlayMap().put(polygon.getIdentifier(), polygon);
        activity.getMapView().addPolygon(polygon);
    }

    private void resetNodes() {
        nodes = new ArrayList<SKCoordinate>();
        ((TextView) specificLayout.findViewById(R.id.polygon_points).findViewById(R.id.property_name)).
                setText(activity.getResources().getString(R.string.polygon_points) + " " + (nodes.isEmpty() ? 0 : nodes.size() - 1));
        drawOnTap = false;
        ((CheckBox) specificLayout.findViewById(R.id.tap_to_draw).findViewById(R.id.property_value)).setChecked(false);
    }

    private void generateRandomId() {
        Random random = new Random(System.currentTimeMillis());
        int id = random.nextInt(10000);
        ((EditText) specificLayout.findViewById(R.id.polygon_id).findViewById(R.id.property_value)).setText("" + id);
        resetNodes();
    }
}

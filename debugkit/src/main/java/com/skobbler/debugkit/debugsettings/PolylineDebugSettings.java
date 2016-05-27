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
import com.skobbler.ngx.map.SKPolyline;
import com.skobbler.ngx.map.SKScreenPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tudor on 7/1/2015.
 */
public class PolylineDebugSettings extends DebugSettings {

    public static boolean drawOnTap;

    private ColorDebugSettings lineColorSettings = new ColorDebugSettings(new float[]{0, 0, 0, 1});

    private ColorDebugSettings outlineColorSettings = new ColorDebugSettings(new float[]{0, 0, 0, 1});

    List<SKCoordinate> nodes = new ArrayList<SKCoordinate>();

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.id), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.line_width), 2));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_width), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_pixels_solid), 5));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_pixels_skip), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.line_color), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.outline_color), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.polyline_points), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.draw_on_tap), false));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.generate_id), false));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_polyline;
    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        int maxWidth = (int) (20 / activity.getResources().getDisplayMetrics().density) + 1;
        ((SeekBar) specificLayout.findViewById(R.id.line_width).findViewById(R.id.property_seekbar)).setMax(maxWidth);
        ((SeekBar) specificLayout.findViewById(R.id.outline_width).findViewById(R.id.property_seekbar)).setMax(maxWidth);
        ((SeekBar) specificLayout.findViewById(R.id.outline_pixels_solid).findViewById(R.id.property_seekbar)).setMax(19);
        ((SeekBar) specificLayout.findViewById(R.id.outline_pixels_skip).findViewById(R.id.property_seekbar)).setMax(20);
    }

    @Override
    void defineSpecificListeners() {
        generateRandomId();

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

        SeekBar lineWidthSeekBar = (SeekBar) specificLayout.findViewById(R.id.line_width).findViewById(R.id.property_seekbar);
        lineWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.line_width).findViewById(R.id.property_value)).setText(value + "");
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
                lineColorSettings.open(debugBaseLayout, PolylineDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.outline_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outlineColorSettings.open(debugBaseLayout, PolylineDebugSettings.this);
            }
        });
    }

    private SKPolyline getPolylineFromSettings() {
        SKPolyline polyline = new SKPolyline();
        polyline.setColor(lineColorSettings.getColor());
        polyline.setOutlineColor(outlineColorSettings.getColor());

        EditText idText = (EditText) specificLayout.findViewById(R.id.polygon_id).findViewById(R.id.property_value);
        try {
            int id = Integer.parseInt(idText.getText().toString());
            polyline.setIdentifier(id);
        } catch (NumberFormatException e) {
        }

        SeekBar lineSizeSeekBar = (SeekBar) specificLayout.findViewById(R.id.line_width).findViewById(R.id.property_seekbar);
        polyline.setLineSize(lineSizeSeekBar.getProgress());

        SeekBar outlineSizeSeekBar = (SeekBar) specificLayout.findViewById(R.id.outline_width).findViewById(R.id.property_seekbar);
        polyline.setOutlineSize(outlineSizeSeekBar.getProgress());

        SeekBar outlineSolidSize = (SeekBar) specificLayout.findViewById(R.id.outline_pixels_solid).findViewById(R.id.property_seekbar);
        polyline.setOutlineDottedPixelsSolid(outlineSolidSize.getProgress() + 1);

        SeekBar outlineSkipSize = (SeekBar) specificLayout.findViewById(R.id.outline_pixels_skip).findViewById(R.id.property_seekbar);
        polyline.setOutlineDottedPixelsSkip(outlineSkipSize.getProgress());

        polyline.setNodes(nodes);

        return polyline;
    }

    public void addPolylineNode(SKScreenPoint screenPoint) {
        nodes.add(activity.getMapView().pointToCoordinate(screenPoint));
        ((TextView) specificLayout.findViewById(R.id.polygon_points).findViewById(R.id.property_name)).
                setText(activity.getResources().getString(R.string.polyline_points) + " " + nodes.size());
        SKPolyline polyline = getPolylineFromSettings();
        ((OverlaysDebugSettings) parentSettings).getOverlayMap().put(polyline.getIdentifier(), polyline);
        activity.getMapView().addPolyline(polyline);
    }

    private void resetNodes() {
        nodes = new ArrayList<SKCoordinate>();
        ((TextView) specificLayout.findViewById(R.id.polygon_points).findViewById(R.id.property_name)).
                setText(activity.getResources().getString(R.string.polyline_points) + " " + nodes.size());
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

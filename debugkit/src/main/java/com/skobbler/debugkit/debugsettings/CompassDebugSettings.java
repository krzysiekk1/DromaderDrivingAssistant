package com.skobbler.debugkit.debugsettings;

import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKScreenPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/5/2015.
 */
public class CompassDebugSettings extends DebugSettings {
    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.show_compass), false));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.offset_x), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.offset_y), 0));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_compass;
    }

    @Override
    void defineSpecificListeners() {
        final View enableCompass = specificLayout.findViewById(R.id.show_compass);
        enableCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) enableCompass.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                applyCompassSettings();
            }
        });

        ((EditText) specificLayout.findViewById(R.id.offset_x).findViewById(R.id.property_value)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    applyCompassSettings();
                }
            }
        });

        ((EditText) specificLayout.findViewById(R.id.offset_y).findViewById(R.id.property_value)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    applyCompassSettings();
                }
            }
        });
    }

    @Override
    void onClose() {
        applyCompassSettings();
    }

    private void applyCompassSettings() {
        activity.getMapView().getMapSettings().setCompassShown(((CheckBox) specificLayout.findViewById(R.id.show_compass).findViewById(R.id.property_value)).isChecked());
        String xOffsetString = ((EditText) specificLayout.findViewById(R.id.offset_x).findViewById(R.id.property_value)).getText().toString();
        String yOffsetString = ((EditText) specificLayout.findViewById(R.id.offset_y).findViewById(R.id.property_value)).getText().toString();

        float xOffset = 0;
        try {
            xOffset = Float.parseFloat(xOffsetString);
        } catch (NumberFormatException e) {
        }

        float yOffset = 0;
        try {
            yOffset = Float.parseFloat(yOffsetString);
        } catch (NumberFormatException e) {
        }

        activity.getMapView().getMapSettings().setCompassPosition(new SKScreenPoint(xOffset, yOffset));
    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        ((EditText) specificLayout.findViewById(R.id.offset_x).findViewById(R.id.property_value)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ((EditText) specificLayout.findViewById(R.id.offset_y).findViewById(R.id.property_value)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }
}

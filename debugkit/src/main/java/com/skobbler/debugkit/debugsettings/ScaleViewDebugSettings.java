package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKMapScaleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 22.06.2015.
 */
public class ScaleViewDebugSettings extends DebugSettings {

    /**
     * Distance type
     */
    SKMaps.SKDistanceUnitType skDistanceUnitType= SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS;
    /**
     * ScaleView checked
     */
    private boolean scaleViewChecked = false;
    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context=specificLayout.getContext();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.scale_view_title), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.scale_view_distance_format), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.scale_view_night_style), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.scale_view_customization;
    }

    @Override
    void defineSpecificListeners() {
        specificLayout.findViewById(R.id.scale_view_distance_format).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(ScaleViewDistanceFormat.class).open(debugBaseLayout, ScaleViewDebugSettings.this);
            }
        });
        final View nightStyleScaleView = specificLayout.findViewById(R.id.scale_view_night_style);
        nightStyleScaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox scaleViewCheckBox = (CheckBox) nightStyleScaleView.findViewById(R.id.property_value);
                scaleViewCheckBox.setChecked(!scaleViewCheckBox.isChecked());
                if (scaleViewCheckBox.isChecked()) {
                    scaleViewChecked = false;
                    showScaleview();
                }
                else {
                    scaleViewChecked=true;
                    showScaleview();
                }

            }
        });

    }

    @Override
    void onChildChanged(DebugSettings changedChild) {
        super.onChildChanged(changedChild);
        if (changedChild instanceof ScaleViewDistanceFormat) {
            switch (((ScaleViewDistanceFormat) changedChild).getCurrentSelectedIndex()) {
                case 0:skDistanceUnitType= SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS;
                    ((TextView) specificLayout.findViewById(R.id.scale_view_distance_format).findViewById(R.id.property_value)).setText("Metric");
                    showScaleview();
                    break;
                case 1:skDistanceUnitType= SKMaps.SKDistanceUnitType.DISTANCE_UNIT_MILES_FEET;
                    ((TextView) specificLayout.findViewById(R.id.scale_view_distance_format).findViewById(R.id.property_value)).setText("MilesFeet");
                    showScaleview();
                    break;
                case 2:skDistanceUnitType= SKMaps.SKDistanceUnitType.DISTANCE_UNIT_MILES_YARDS;
                    ((TextView) specificLayout.findViewById(R.id.scale_view_distance_format).findViewById(R.id.property_value)).setText("MilesYards");
                    showScaleview();
                    break;
            }
        }
    }

    private void showScaleview(){
        /**
         * Scale view object
         */
        SKMapScaleView skMapScaleView=activity.getMapHolder().getScaleView();
        skMapScaleView.setDistanceUnit(skDistanceUnitType);
        if(scaleViewChecked){
            skMapScaleView.setDarkerColor(Color.BLACK);
        }
        else {
            skMapScaleView.setDarkerColor(Color.WHITE);
        }
        skMapScaleView.setFadeOutEnabled(true);
        activity.getMapHolder().setScaleViewEnabled(true);
    }

}

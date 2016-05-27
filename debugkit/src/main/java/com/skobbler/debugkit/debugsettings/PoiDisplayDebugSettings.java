package com.skobbler.debugkit.debugsettings;

import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKMapSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/10/2015.
 */
public class PoiDisplayDebugSettings extends DebugSettings {

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getString(R.string.cities), true));
        pairs.add(new Pair<String, Object>(activity.getString(R.string.general), true));
        pairs.add(new Pair<String, Object>(activity.getString(R.string.important), true));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_poi_display;
    }

    @Override
    void defineSpecificListeners() {
        final SKMapSettings mapSettings = activity.getMapView().getMapSettings();

        final View showCities = specificLayout.findViewById(R.id.show_cities);
        showCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) showCities.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                mapSettings.setCityPoisShown(checkBox.isChecked());
            }
        });

        final View showGeneral = specificLayout.findViewById(R.id.show_general_pois);
        showGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) showGeneral.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                mapSettings.setGeneratedPoisShown(checkBox.isChecked());
            }
        });

        final View showImportant = specificLayout.findViewById(R.id.show_important_pois);
        showImportant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) showImportant.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                mapSettings.setImportantPoisShown(checkBox.isChecked());
            }
        });
    }
}

package com.skobbler.debugkit.debugsettings;


import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKMapViewStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/12/2015.
 */
public class MapStyleDebugSettings extends SingleChoiceListDebugSettings {

    private static final String[] styles = new String[]{"daystyle", "nightstyle", "outdoorstyle", "grayscalestyle"};

    @Override
    List<String> defineChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add(activity.getResources().getString(R.string.map_style_day));
        choices.add(activity.getResources().getString(R.string.map_style_night));
        choices.add(activity.getResources().getString(R.string.map_style_outdoor));
        choices.add(activity.getResources().getString(R.string.map_style_grayscale));
        return choices;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    void onCurrentSelectionChanged() {
        String styleName = styles[getCurrentSelectedIndex()];
        String resourcesPath = activity.getIntent().getStringExtra("mapResourcesPath");
        activity.getMapView().getMapSettings().setMapStyle(new SKMapViewStyle(resourcesPath + styleName + "/", styleName + ".json"));
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_style;
    }
}

package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 03.07.2015.
 */
public class RoutingRestrictions extends DebugSettings {

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        Context context = specificLayout.getContext();
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.avoid_toll_roads), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.avoid_highways), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.avoid_ferries), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.avoid_bicyclewalk), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.avoid_bicyclecarry), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.routing_route_restriction_option;
    }

    @Override
    void defineSpecificListeners() {
        final View tollRoads = specificLayout.findViewById(R.id.restriction_mode_0);
        tollRoads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox tollRoadsCheckBox = (CheckBox) tollRoads.findViewById(R.id.property_value);
                tollRoadsCheckBox.setChecked(!tollRoadsCheckBox.isChecked());
            }
        });
        final View highways = specificLayout.findViewById(R.id.restriction_mode_1);
        highways.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox highwaysCheckBox = (CheckBox) highways.findViewById(R.id.property_value);
                highwaysCheckBox.setChecked(!highwaysCheckBox.isChecked());

            }
        });
        final View ferries = specificLayout.findViewById(R.id.restriction_mode_2);
        ferries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox ferriesCheckBox = (CheckBox) ferries.findViewById(R.id.property_value);
                ferriesCheckBox.setChecked(!ferriesCheckBox.isChecked());

            }
        });
        final View bicycleWalk = specificLayout.findViewById(R.id.restriction_mode_3);
        bicycleWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox bicycleWalkCheckBox = (CheckBox) bicycleWalk.findViewById(R.id.property_value);
                bicycleWalkCheckBox.setChecked(!bicycleWalkCheckBox.isChecked());

            }
        });
        final View bicycleCarry = specificLayout.findViewById(R.id.restriction_mode_4);
        bicycleCarry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox bicycleCarryCheckBox = (CheckBox) bicycleCarry.findViewById(R.id.property_value);
                bicycleCarryCheckBox.setChecked(!bicycleCarryCheckBox.isChecked());

            }
        });
    }

}

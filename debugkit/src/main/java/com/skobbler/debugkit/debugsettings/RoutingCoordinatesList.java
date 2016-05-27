package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 15.07.2015.
 */
public class RoutingCoordinatesList extends DebugSettings {
    /**
     * View for the detected poi
     */
    ViewGroup adviceListItem;

    /**
     * Coordinates list
     */
    private static List<SKCoordinate> coordinatesList;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        Context context=specificLayout.getContext();
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_coordinates_list), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.routing_coordinates_list_info;
    }

    @Override
    void defineSpecificListeners() {

    }

    @Override
    void onOpened() {
        super.onOpened();
        coordinatesList = RoutingDebugSettings.getCoordinatesList();
        if (coordinatesList != null) {
            populateListWithAdvices(coordinatesList);
        }
    }

    @Override
    void onClose() {
        super.onClose();
        if(coordinatesList!=null){
            specificLayout.removeViews(1,coordinatesList.size());
            coordinatesList.clear();
        }
    }

    /**
     * Populates the layout with the detected POIs items
     *
     * @param detectedAdvices
     */
    private void populateListWithAdvices(List<SKCoordinate> detectedAdvices) {
        Context context1=specificLayout.getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        int position = 1;
        for (int i = 0; i < detectedAdvices.size(); i++) {
            adviceListItem =
                    (ViewGroup) inflater.inflate(R.layout.routing_via_point_info, null, false);
                String text = context1.getResources().getString(R.string.longitude) + (detectedAdvices.get(i)).getLongitude() + context1.getResources().getString(R.string.latitude) + (detectedAdvices.get(i)).getLatitude();
                ((TextView) adviceListItem.findViewById(R.id.property_name)).setText(text);
                specificLayout.addView(adviceListItem, position);
                position++;
        }
    }
}

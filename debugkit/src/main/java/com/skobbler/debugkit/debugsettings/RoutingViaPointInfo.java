package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.routing.SKViaPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 15.07.2015.
 */
public class RoutingViaPointInfo extends DebugSettings {
    /**
     * View for the detected poi
     */
    ViewGroup viaPointItem;

    /**
     * Via point list
     */
    private ArrayList<SKViaPoint> viaPointList;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        Context context = specificLayout.getContext();
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_viapoint_list), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.routing_via_point_info;
    }

    @Override
    void defineSpecificListeners() {

    }

    @Override
    void onOpened() {
        super.onOpened();
        viaPointList = RoutingViaPoints.getViaPointList();
        if (viaPointList != null) {
            populateListWithDetectedPois(viaPointList);
        }
    }

    @Override
    void onClose() {
        super.onClose();
        if (viaPointList != null) {
            specificLayout.removeViews(1, viaPointList.size());
            viaPointList.clear();
        }
    }

    /**
     * Populates the layout with the detected POIs items
     *
     * @param detectedPOIs
     */
    private void populateListWithDetectedPois(ArrayList<SKViaPoint> detectedPOIs) {
        Context context1 = specificLayout.getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        int position = 1;
        for (int i = 0; i < detectedPOIs.size(); i++) {
            viaPointItem =
                    (ViewGroup) inflater.inflate(R.layout.routing_via_point_info, null, false);
            if (detectedPOIs.get(i).getUniqueId() != -1) {
                String text = context1.getResources().getString(R.string.routing_id) + detectedPOIs.get(i).getUniqueId() + context1.getResources().getString(R.string.routing_position) + detectedPOIs.get(i).getPosition();
                ((TextView) viaPointItem.findViewById(R.id.property_name)).setText(text);
                specificLayout.addView(viaPointItem, position);
                position++;
            }

        }
    }
}

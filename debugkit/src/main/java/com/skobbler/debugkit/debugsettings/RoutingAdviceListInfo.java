package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.routing.SKRouteAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 15.07.2015.
 */
public class RoutingAdviceListInfo extends DebugSettings {
    /**
     * View for the detected poi
     */
    ViewGroup adviceListItem;

    /**
     * Advice list
     */
    private static ArrayList<SKRouteAdvice> advicesList;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        Context context = specificLayout.getContext();
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_advice_list), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.routing_advice_list_info;
    }

    @Override
    void defineSpecificListeners() {

    }

    @Override
    void onOpened() {
        super.onOpened();
        advicesList = RoutingDebugSettings.getAdviceList();
        if (advicesList != null) {
            populateListWithAdvices(advicesList);
        }
    }

    @Override
    void onClose() {
        super.onClose();
        if (advicesList != null) {
            specificLayout.removeViews(1, advicesList.size());
            advicesList.clear();
        }
    }

    /**
     * Populates the layout with the detected POIs items
     *
     * @param detectedAdvices
     */
    private void populateListWithAdvices(ArrayList<SKRouteAdvice> detectedAdvices) {
        Context context1 = specificLayout.getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        int position = 1;
        for (int i = 0; i < detectedAdvices.size(); i++) {
            adviceListItem =
                    (ViewGroup) inflater.inflate(R.layout.routing_via_point_info, null, false);
            if ((detectedAdvices.get(i)).getAdviceID() != -1) {
                String text = context1.getResources().getString(R.string.routing_id) + (detectedAdvices.get(i)).getAdviceID() + context1.getResources().getString(R.string.routing_instruction) + (detectedAdvices.get(i)).getAdviceInstruction();
                ((TextView) adviceListItem.findViewById(R.id.property_name)).setText(text);
                specificLayout.addView(adviceListItem, position);
                position++;
            }

        }
    }
}

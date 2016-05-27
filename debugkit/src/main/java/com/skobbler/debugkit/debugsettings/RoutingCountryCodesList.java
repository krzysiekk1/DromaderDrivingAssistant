package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skobbler.debugkit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 15.07.2015.
 */
public class RoutingCountryCodesList extends DebugSettings {
    /**
     * View for the detected poi
     */
    ViewGroup countryCodesListItem;

    /**
     * Country codes list
     */
    private static ArrayList<String> countryCodesList ;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        Context context = specificLayout.getContext();
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.routing_country_codes_list), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.routing_country_codes_info;
    }

    @Override
    void defineSpecificListeners() {
    }

    @Override
    void onOpened() {
        super.onOpened();
        countryCodesList = RoutingDebugSettings.getCountryCodesList();
        if (countryCodesList != null) {
            populateListWithAdvices(countryCodesList);
        }
    }

    @Override
    void onClose() {
        super.onClose();
        if (countryCodesList != null) {
            specificLayout.removeViews(1,countryCodesList.size());
            countryCodesList.clear();
        }
    }

    /**
     * Populates the layout with the detected POIs items
     * @param detectedAdvices
     */
    private void populateListWithAdvices(List<String> detectedAdvices) {
        Context context1 = specificLayout.getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        int position = 1;
        for (int i = 0; i < detectedAdvices.size(); i++) {
            countryCodesListItem =
                    (ViewGroup) inflater.inflate(R.layout.routing_country_codes_info, null, false);
            String text = context1.getResources().getString(R.string.routing_country_code) + (detectedAdvices.get(i)).toString();
            ((TextView) countryCodesListItem.findViewById(R.id.property_name)).setText(text);
            specificLayout.addView(countryCodesListItem, position);
            position++;
        }
    }
}

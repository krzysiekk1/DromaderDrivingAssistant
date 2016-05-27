package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.poitracker.SKDetectedPOI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexandraP on 13.07.2015.
 */
public class DetectedPOIDebugSettings extends DebugSettings {

    /**
     * View for the detected poi
     */
    ViewGroup detectedPOIItem;

    /**
     * List of detected POIs
     */
    List<SKDetectedPOI> listOfDetectedPOIs;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();

        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.detected_pois_list), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.detected_pois_settings;
    }

    @Override
    void defineSpecificListeners() {

    }


    @Override
    void onOpened() {
        super.onOpened();
        listOfDetectedPOIs = PoiTrackerDebugSettings.getListOfDetectedPOIs();
        populateListWithDetectedPois(listOfDetectedPOIs);
    }


    @Override
    void onClose() {
        super.onClose();
        specificLayout.removeViews(1,specificLayout.getChildCount() - 1);
        listOfDetectedPOIs.clear();
    }

    /**
     * Populates the layout with the detected POIs items
     * @param detectedPOIs
     */
    private void populateListWithDetectedPois(List<SKDetectedPOI> detectedPOIs){
        LayoutInflater inflater = activity.getLayoutInflater();
        int position = 1;
        for(int i = 0; i < detectedPOIs.size(); i++){
            detectedPOIItem =
                    (ViewGroup) inflater.inflate(R.layout.element_debug_kit_item_textview, null, false);
            if(detectedPOIs.get(i).getPoiID() != -1){
                String text = "ID: " + detectedPOIs.get(i).getPoiID() + " Dist: " + detectedPOIs.get(i).getDistance() + " Ref: " + detectedPOIs.get(i).getReferenceDistance();
                ((TextView) detectedPOIItem.findViewById(R.id.property_name)).setText(text);
                specificLayout.addView(detectedPOIItem, position);
                position++;
            }

        }
    }
}

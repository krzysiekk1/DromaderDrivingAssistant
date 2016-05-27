package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKMapCustomPOI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 15.06.2015.
 */
public class CustomPoiDebugSettings extends DebugSettings {
    /**
     * Custom poi category
     */
     SKCategories.SKPOICategory skpoiMainCategory=SKCategories.SKPOICategory.SKPOI_CATEGORY_UNKNOWN;
    /**
     * Custom poi type
     */
    SKMapCustomPOI.SKPoiType skPoiType = SKMapCustomPOI.SKPoiType.SK_POI_TYPE_CATEGORY_SEARCH;
    /**
     * Remove identifier
     */
    private int removeIdentifier = 0;
    /**
     * Minimum zoom level
     */
    private int minimumZoomLevel = 5;
    /**
     * Identifier
     */
    private int identifier = 0;
    /**
     * Latitude
     */
    private double latitude = 37.7765;
    /**
     * longitude
     */
    private double longitude = -122.4200;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.custom_poi_title), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_identifier), identifier));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.latitude), latitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.longitude), longitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.custom_poi_type), skPoiType));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.custom_poi_category), skpoiMainCategory));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_actions), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_add), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_remove_identifier), removeIdentifier));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_remove), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.custom_poi_debug_kit;
    }

    @Override
    void defineSpecificListeners() {
        specificLayout.findViewById(R.id.custom_poi_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(CustomPoiType.class).open(debugBaseLayout, CustomPoiDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.custom_poi_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(CustomPoiCategory.class).open(debugBaseLayout, CustomPoiDebugSettings.this);
            }
        });
        final View addCustomPoiButton = specificLayout.findViewById(R.id.custom_poi_add);
        final EditText annotationIdentifier = (EditText) specificLayout.findViewById(R.id.custom_poi_identifier).findViewById(R.id.property_value);
        final EditText annotationLongitude = (EditText) specificLayout.findViewById(R.id.custom_poi_longitude).findViewById(R.id.property_value);
        final EditText annotationlatitude = (EditText) specificLayout.findViewById(R.id.custom_poi_latitude).findViewById(R.id.property_value);

        addCustomPoiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                longitude = Double.parseDouble(annotationLongitude.getText().toString());
                latitude = Double.parseDouble(annotationlatitude.getText().toString());
                identifier = Integer.parseInt(annotationIdentifier.getText().toString());
                prepareCustomPoi();
            }
        });
        final View deleteCustomPoi = specificLayout.findViewById(R.id.custom_poi_remove);
        final EditText removeIdentifierEditText = (EditText) specificLayout.findViewById(R.id.custom_poi_remove_identifier).findViewById(R.id.property_value);
        deleteCustomPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeIdentifier = Integer.parseInt(removeIdentifierEditText.getText().toString());
                activity.getMapView().deleteCustomPOI(removeIdentifier);
            }
        });
    }


    private void prepareCustomPoi() {
        SKMapCustomPOI customPOI = new SKMapCustomPOI();
        customPOI.setUniqueID(identifier);
        customPOI.setLocation(new SKCoordinate(longitude, latitude));
        customPOI.setPoiType(skPoiType);
        customPOI.setCategory(skpoiMainCategory);
        activity.getMapView().addCustomPOI(customPOI);
        // set map zoom level
        activity.getMapView().setZoom(15);
        // center map on a position
        activity.getMapView().centerMapOnPosition(new SKCoordinate(longitude, latitude));
    }

    @Override
    void onChildChanged(DebugSettings changedChild) {
        super.onChildChanged(changedChild);
        if (changedChild instanceof CustomPoiType) {
            switch (((CustomPoiType) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    skPoiType = SKMapCustomPOI.SKPoiType.SK_POI_TYPE_CATEGORY_SEARCH;
                    ((TextView) specificLayout.findViewById(R.id.custom_poi_type).findViewById(R.id.property_value)).setText(skPoiType.toString());
                    break;
                case 1:
                    skPoiType = SKMapCustomPOI.SKPoiType.SK_POI_TYPE_RECENTS;
                    ((TextView) specificLayout.findViewById(R.id.custom_poi_type).findViewById(R.id.property_value)).setText(skPoiType.toString());
                    break;
                case 2:
                    skPoiType = SKMapCustomPOI.SKPoiType.SK_POI_TYPE_FAVOURITES;
                    ((TextView) specificLayout.findViewById(R.id.custom_poi_type).findViewById(R.id.property_value)).setText(skPoiType.toString());
                    break;
                case 3:
                    skPoiType = SKMapCustomPOI.SKPoiType.SK_POI_TYPE_LOCAL_SEARCH;
                    ((TextView) specificLayout.findViewById(R.id.custom_poi_type).findViewById(R.id.property_value)).setText(skPoiType.toString());
                    break;
            }
        } else if (changedChild instanceof CustomPoiCategory) {
            switch (((CustomPoiCategory) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    skpoiMainCategory= SKCategories.SKPOICategory.SKPOI_CATEGORY_UNKNOWN;
                    ((TextView) specificLayout.findViewById(R.id.custom_poi_category).findViewById(R.id.property_value)).setText(skpoiMainCategory.toString());
                    break;
                case 1:
                    skpoiMainCategory= SKCategories.SKPOICategory.SKPOI_CATEGORY_AIRPORT;
                    ((TextView) specificLayout.findViewById(R.id.custom_poi_category).findViewById(R.id.property_value)).setText(skpoiMainCategory.toString());
                    break;
                case 2:
                    skpoiMainCategory= SKCategories.SKPOICategory.SKPOI_CATEGORY_AERODROME;
                    ((TextView) specificLayout.findViewById(R.id.custom_poi_category).findViewById(R.id.property_value)).setText(skpoiMainCategory.toString());
                    break;
                case 3:
                    skpoiMainCategory= SKCategories.SKPOICategory.SKPOI_CATEGORY_FERRYTERMINAL;
                    ((TextView) specificLayout.findViewById(R.id.custom_poi_category).findViewById(R.id.property_value)).setText(skpoiMainCategory.toString());
                    break;
            }
        }

    }
}

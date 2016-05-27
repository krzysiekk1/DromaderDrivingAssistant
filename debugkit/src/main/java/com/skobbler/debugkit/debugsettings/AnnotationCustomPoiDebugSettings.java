package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.View;

import com.skobbler.debugkit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by mirceab on 02.06.2015.
 */
public class AnnotationCustomPoiDebugSettings extends DebugSettings{
    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_title),null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_option), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.custompoi_title), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.custompoi_option), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.annotation_custom_poi_customization;
    }

    @Override
    void defineSpecificListeners() {
        specificLayout.findViewById(R.id.test_open_child_annotation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(AnnotationDebugSettings.class).open(debugBaseLayout, AnnotationCustomPoiDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.test_open_child_custompoi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(CustomPoiDebugSettings.class).open(debugBaseLayout, AnnotationCustomPoiDebugSettings.this);
            }
        });
    }
}

package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 04.06.2015.
 */
public class AnnotationType extends SingleChoiceListDebugSettings {


    @Override
    List<String> defineChoices() {
        List<String> choices = new ArrayList<String>();
        Context context = specificLayout.getContext();
        choices.add(context.getResources().getString(R.string.annotation_color_red));
        choices.add(context.getResources().getString(R.string.annotation_color_blue));
        choices.add(context.getResources().getString(R.string.annotation_color_green));
        choices.add(context.getResources().getString(R.string.annotation_color_purple));
        choices.add(context.getResources().getString(R.string.annotation_color_marker));
        choices.add(context.getResources().getString(R.string.annotation_color_destinationflag));
        return choices;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.annotation_type_debug_kit;
    }
}

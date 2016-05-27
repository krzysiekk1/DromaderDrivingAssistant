package com.skobbler.debugkit.debugsettings;

import android.content.Context;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKMapScaleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 22.06.2015.
 */
public class ScaleViewDistanceFormat extends SingleChoiceListDebugSettings {

    @Override
    List<String> defineChoices() {
        List<String> choices = new ArrayList<String>();
        Context context=specificLayout.getContext();
        choices.add(context.getResources().getString(R.string.scale_view_metrics));
        choices.add(context.getResources().getString(R.string.scale_view_milesfeet));
        choices.add(context.getResources().getString(R.string.scale_view_milesyards));
        return choices;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.scale_view_distance_format;
    }

}

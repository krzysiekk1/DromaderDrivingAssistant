package com.skobbler.debugkit.debugsettings;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.positioner.logging.SKPositionLoggingManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 7/9/2015.
 */
public class PositionFileTypeDebugSettings extends EnumBasedDebugSettings {

    @Override
    List<String> defineChoices() {
        List<String> choices = new ArrayList<String>();
        for (String choice : super.defineChoices()) {
            choices.add(choice.replaceAll(".*_", ""));
        }
        return choices;
    }

    @Override
    Class defineEnumClass() {
        return SKPositionLoggingManager.SPositionLoggingType.class;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_position_file_type;
    }
}
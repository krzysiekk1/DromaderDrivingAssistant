package com.skobbler.debugkit.debugsettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/4/2015.
 */
public abstract class EnumBasedDebugSettings extends SingleChoiceListDebugSettings {

    @Override
    List<String> defineChoices() {
        List<String> choices = new ArrayList<String>();
        Class enumClass = defineEnumClass();
        if (enumClass.isEnum()) {
            Object[] enumConstants = enumClass.getEnumConstants();
            for (int i = 0; i < enumConstants.length; i++) {
                choices.add(enumConstants[i].toString());
            }
        }
        return choices;
    }

    abstract Class defineEnumClass();
}

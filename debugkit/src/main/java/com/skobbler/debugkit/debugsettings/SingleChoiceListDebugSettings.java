package com.skobbler.debugkit.debugsettings;

import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.skobbler.debugkit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/4/2015.
 */
public abstract class SingleChoiceListDebugSettings extends DebugSettings {

    private int currentSelectedItemIndex;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup parentView = (ViewGroup) view.getParent();
            int index = parentView.indexOfChild(view);
            ((RadioButton) specificLayout.getChildAt(currentSelectedItemIndex).findViewById(R.id.property_value)).setChecked(false);
            boolean selectionChanged = currentSelectedItemIndex != index;
            currentSelectedItemIndex = index;
            ((RadioButton) specificLayout.getChildAt(currentSelectedItemIndex).findViewById(R.id.property_value)).setChecked(true);
            if (selectionChanged) {
                if (parentSettings != null) {
                    parentSettings.onChildChanged(SingleChoiceListDebugSettings.this);
                }
                onCurrentSelectionChanged();
            }
        }
    };

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        currentSelectedItemIndex = defineInitialSelectionIndex();
        List<String> choices = defineChoices();

        for (int i = 0; i < choices.size(); i++) {
            pairs.add(new Pair<String, Object>(choices.get(i), i == currentSelectedItemIndex));
        }

        return pairs;
    }

    abstract List<String> defineChoices();

    abstract int defineInitialSelectionIndex();

    @Override
    void defineSpecificListeners() {
        for (int i = 0; i < specificLayout.getChildCount(); i++) {
            specificLayout.getChildAt(i).setOnClickListener(clickListener);
        }
    }

    public int getCurrentSelectedIndex() {
        return currentSelectedItemIndex;
    }

    void onCurrentSelectionChanged() {
    }
}

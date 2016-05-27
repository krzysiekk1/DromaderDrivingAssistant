package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.debugkit.activity.DebugMapActivity;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tudor on 5/29/2015.
 */
public abstract class DebugSettings {

    public static DebugMapActivity currentMapActivity;

    public static DebugSettings currentSettings;

    private static Map<DebugMapActivity, Map<Class, DebugSettings>> map = new HashMap<DebugMapActivity, Map<Class, DebugSettings>>();

    View debugBaseLayout;

    LinearLayout specificLayout;

    private boolean isMinimized;

    DebugSettings parentSettings;

    private Button minimizeButton;

    private Button closeButton;

    DebugMapActivity activity;

    private int verticalScrollOffset;

    public static DebugSettings getInstanceForType(Class classObject) {
        if (currentMapActivity != null) {
            if (map.get(currentMapActivity) == null) {
                map.put(currentMapActivity, new HashMap<Class, DebugSettings>());
            }
            if (map.get(currentMapActivity).get(classObject) != null) {
                return map.get(currentMapActivity).get(classObject);
            } else {
                try {
                    DebugSettings instance = (DebugSettings) classObject.getDeclaredConstructor().newInstance();
                    map.get(currentMapActivity).put(classObject, instance);
                    return instance;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void open(View debugBaseLayout, DebugSettings parentSettings) {
        if (currentSettings == this) {
            return;
        }
        this.debugBaseLayout = debugBaseLayout;
        debugBaseLayout.setVisibility(View.VISIBLE);
        if (specificLayout == null) {
            LayoutInflater inflater = (LayoutInflater) debugBaseLayout.getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            specificLayout = (LinearLayout) inflater.inflate(defineSpecificLayout(), null);
            activity = (DebugMapActivity) specificLayout.getContext();
            applyCustomChangesToUI();
            setupDefaultValuesInSpecificUI();
            defineSpecificListeners();
        }

        closeButton = (Button) debugBaseLayout.findViewById(R.id.debug_close_button);
        minimizeButton = (Button) debugBaseLayout.findViewById(R.id.debug_minimize_button);

        defineCloseListener();
        defineMinimizeMaximizeListener();

        ScrollView scrollView = (ScrollView) debugBaseLayout.findViewById(R.id.debug_settings_scroll_view);

        if (parentSettings != null) {
            parentSettings.verticalScrollOffset = scrollView.getScrollY();
        }

        scrollView.removeAllViews();
        scrollView.addView(specificLayout);
        scrollView.scrollTo(0, 0);

        if (parentSettings != null) {
            this.parentSettings = parentSettings;
        }
        closeButton.setVisibility(this.parentSettings == null ? View.GONE : View.VISIBLE);

        if (currentSettings != null) {
            currentSettings.onClose();
        }
        onOpened();
        currentSettings = this;
    }

    private void defineCloseListener() {
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (parentSettings != null) {
                    DebugSettings parentToOpen = DebugSettings.getInstanceForType(parentSettings.getClass());
                    parentToOpen.open(debugBaseLayout, parentToOpen.parentSettings);
                    ScrollView scrollView = (ScrollView) parentToOpen.debugBaseLayout.findViewById(R.id.debug_settings_scroll_view);
                    scrollView.scrollTo(0, parentToOpen.verticalScrollOffset);
                    parentToOpen.onChildClosed(DebugSettings.this);
                    onClose();
                }
            }
        });
    }

    private void defineMinimizeMaximizeListener() {
        minimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMinimized) {
                    debugBaseLayout.findViewById(R.id.debug_settings_top_bar).setVisibility(View.VISIBLE);
                    debugBaseLayout.findViewById(R.id.debug_settings_scroll_view).setVisibility(View.VISIBLE);
                    minimizeButton.setText("<");
                    isMinimized = false;
                } else {
                    debugBaseLayout.findViewById(R.id.debug_settings_top_bar).setVisibility(View.GONE);
                    debugBaseLayout.findViewById(R.id.debug_settings_scroll_view).setVisibility(View.GONE);
                    minimizeButton.setText(">");
                    isMinimized = true;
                }
            }
        });
    }

    void setupDefaultValuesInSpecificUI() {
        List<Pair<String, Object>> keyValuePairs = defineKeyValuePairs();
        for (int i = 0; i < Math.min(specificLayout.getChildCount(), keyValuePairs.size()); i++) {
            View rowView = specificLayout.getChildAt(i);

            View nameView = rowView.findViewById(R.id.property_name);
            if (nameView != null && nameView instanceof TextView) {
                ((TextView) nameView).setText(keyValuePairs.get(i).first);
            }

            View valueView = rowView.findViewById(R.id.property_value);
            if (valueView != null && keyValuePairs.get(i).second != null) {
                if (valueView instanceof CheckBox) {
                    ((CheckBox) valueView).setChecked((Boolean) keyValuePairs.get(i).second);
                } else if (valueView instanceof RadioButton) {
                    ((RadioButton) valueView).setChecked((Boolean) keyValuePairs.get(i).second);
                } else if (valueView instanceof EditText) {
                    final String textValue = keyValuePairs.get(i).second.toString();
                    final EditText textView = (EditText) valueView;
                    textView.setText(textValue);
                    textView.setSelection(textValue.length());
                } else if (valueView instanceof TextView) {
                    ((TextView) valueView).setText(keyValuePairs.get(i).second.toString());
                }
            }

            SeekBar seekBar = (SeekBar) rowView.findViewById(R.id.property_seekbar);
            if (seekBar != null && keyValuePairs.get(i).second != null) {
                String value = keyValuePairs.get(i).second.toString();
                value = value.replaceFirst("^\\D+", "").replaceFirst("\\D.*$", "");
                seekBar.setProgress(Integer.parseInt(value));
            }
        }
    }

    abstract List<Pair<String, Object>> defineKeyValuePairs();

    abstract int defineSpecificLayout();

    abstract void defineSpecificListeners();

    void onChildClosed(DebugSettings closedChild) {

    }

    void onChildChanged(DebugSettings changedChild) {

    }

    void onClose() {

    }

    void applyCustomChangesToUI() {
    }

    void onOpened() {

    }
}

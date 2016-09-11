package com.skobbler.sdkdemo.activity;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.util.PreferenceTypes;

public class NaviSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navi_settings);

        final ListPreference listPreference = (ListPreference) findPreference(PreferenceTypes.K_ROUTE_TYPE);
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                listPreference.setValue(newValue.toString());
                preference.setSummary(listPreference.getEntry());
                return false;
            }
        });

        final CheckBoxPreference checkBoxTolls = (CheckBoxPreference) findPreference(PreferenceTypes
                .K_AVOID_TOLLS);
        checkBoxTolls.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

    }

}
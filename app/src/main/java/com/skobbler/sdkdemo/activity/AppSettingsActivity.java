package com.skobbler.sdkdemo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.util.PreferenceTypes;

public class AppSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_settings);

        final CheckBoxPreference checkBoxCarParksAlerts = (CheckBoxPreference) findPreference(PreferenceTypes
                .K_CAR_PARKS_ALERTS);
        checkBoxCarParksAlerts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

        final CheckBoxPreference checkBoxAccommodationAlerts = (CheckBoxPreference) findPreference(PreferenceTypes
                .K_ACCOMMODATION_ALERTS);
        checkBoxAccommodationAlerts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

        final CheckBoxPreference checkBoxPetrolStationsAlerts = (CheckBoxPreference) findPreference(PreferenceTypes
                .K_PETROL_STATIONS_ALERTS);
        checkBoxPetrolStationsAlerts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

        final EditTextPreference tankCapacityPreference = (EditTextPreference) findPreference(PreferenceTypes
                .K_TANK_CAPACITY);
        tankCapacityPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences store = getSharedPreferences("storage", Context.MODE_PRIVATE);
                tankCapacityPreference.setText(store.getString(PreferenceTypes.K_TANK_CAPACITY, "0"));
                preference.setSummary(tankCapacityPreference.getText());
                return false;
            }
        });

        final ListPreference fuelTypePreference = (ListPreference) findPreference(PreferenceTypes.K_FUEL_TYPE);
        fuelTypePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                fuelTypePreference.setValue(newValue.toString());
                preference.setSummary(fuelTypePreference.getEntry());
                return false;
            }
        });

        final EditTextPreference fuelConsumptionPreference = (EditTextPreference) findPreference(PreferenceTypes
                .K_FUEL_CONSUMPTION);
        final SharedPreferences store2 = getSharedPreferences("storage", Context.MODE_PRIVATE);
        fuelConsumptionPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                fuelConsumptionPreference.setText(store2.getString(PreferenceTypes.K_FUEL_CONSUMPTION, "0"));
                //fuelConsumptionPreference.setText(newValue.toString());
                preference.setSummary(fuelConsumptionPreference.getText());
                return false;
            }
        });

        final EditTextPreference fuelLevelPreference = (EditTextPreference) findPreference(PreferenceTypes
                .K_FUEL_LEVEL);
        fuelLevelPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                fuelLevelPreference.setText(newValue.toString());
                preference.setSummary(fuelLevelPreference.getText());
                return false;
            }
        });

    }

}

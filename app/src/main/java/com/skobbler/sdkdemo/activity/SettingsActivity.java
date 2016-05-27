package com.skobbler.sdkdemo.activity;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.util.PreferenceTypes;

/**
 * Created by AlexandraP on 17.11.2014.
 */
public class SettingsActivity extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        final ListPreference listPreference = (ListPreference) findPreference(PreferenceTypes.K_ROUTE_TYPE);
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Set the value as the new value
                listPreference.setValue(newValue.toString());
                // Get the entry which corresponds to the current value and set as summary
                preference.setSummary(listPreference.getEntry());
                return false;
            }
        });

        final ListPreference listDistanceFormat = (ListPreference) findPreference(PreferenceTypes.K_DISTANCE_UNIT);
        if (listDistanceFormat.getValue() == null) {
            listDistanceFormat.setValueIndex(0);
        }
        listDistanceFormat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Set the value as the new value
                listDistanceFormat.setValue(newValue.toString());
                // Get the entry which corresponds to the current value and set as summary
                preference.setSummary(listDistanceFormat.getEntry());
                if (preference.getSummary().equals("Miles/Feet") || preference.getSummary().equals("Miles/Yards")) {

                    ListPreference listSpeedWarningsInTown = (ListPreference) findPreference
                            ("pref_speed_warnings_in_town");
                    listSpeedWarningsInTown.setEntries(new String[]{"5mi/h", "10mi/h", "15mi/h", "20mi/h", "25mi/h"});
                    listSpeedWarningsInTown.setEntryValues(new String[]{"0", "1", "2", "3", "4"});
                    ListPreference listSpeedWarningsOutTown = (ListPreference) findPreference
                            ("pref_speed_warnings_out_town");
                    listSpeedWarningsOutTown.setEntries(new String[]{"5mi/h", "10mi/h", "15mi/h", "20mi/h", "25mi/h"});
                    listSpeedWarningsOutTown.setEntryValues(new String[]{"0", "1", "2", "3", "4"});
                } else if (preference.getSummary().equals("Kilometers/Meters")) {
                    ListPreference listSpeedWarningsInTown = (ListPreference) findPreference
                            ("pref_speed_warnings_in_town");
                    listSpeedWarningsInTown.setEntries(new String[]{"5km/h", "10km/h", "15km/h", "20km/h", "25km/h"});
                    listSpeedWarningsInTown.setEntryValues(new String[]{"0", "1", "2", "3", "4"});
                    ListPreference listSpeedWarningsOutTown = (ListPreference) findPreference
                            ("pref_speed_warnings_out_town");
                    listSpeedWarningsOutTown.setEntries(new String[]{"5km/h", "10km/h", "15km/h", "20km/h", "25km/h"});
                    listSpeedWarningsOutTown.setEntryValues(new String[]{"0", "1", "2", "3", "4"});
                }
                return false;
            }
        });

        final ListPreference listNavigationType = (ListPreference) findPreference(PreferenceTypes.K_NAVIGATION_TYPE);
        if (listNavigationType.getValue() == null) {
            listNavigationType.setValueIndex(1);
        }
        listNavigationType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Set the value as the new value
                listNavigationType.setValue(newValue.toString());
                // Get the entry which corresponds to the current value and set as summary
                preference.setSummary(listNavigationType.getEntry());
                return false;
            }
        });

        final ListPreference listSpeedWarningsInTown = (ListPreference) findPreference(PreferenceTypes
                .K_IN_TOWN_SPEED_WARNING);
        if (listDistanceFormat.getSummary().equals("Kilometers/Meters")) {
            listSpeedWarningsInTown.setEntries(new String[]{"5km/h", "10km/h", "15km/h", "20km/h", "25km/h"});
            listSpeedWarningsInTown.setEntryValues(new String[]{"0", "1", "2", "3", "4"});
        } else {
            listSpeedWarningsInTown.setEntries(new String[]{"5mi/h", "10mi/h", "15mi/h", "20mi/h", "25mi/h"});
            listSpeedWarningsInTown.setEntryValues(new String[]{"0", "1", "2", "3", "4"});
        }
        if (listSpeedWarningsInTown.getValue() == null) {
            listSpeedWarningsInTown.setValueIndex(3);
        }
        listSpeedWarningsInTown.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Set the value as the new value
                listSpeedWarningsInTown.setValue(newValue.toString());
                // Get the entry which corresponds to the current value and set as summary
                preference.setSummary(listSpeedWarningsInTown.getEntry());
                return false;
            }
        });

        final ListPreference listSpeedWarningsOutTown = (ListPreference) findPreference(PreferenceTypes
                .K_OUT_TOWN_SPEED_WARNING);
        if (listDistanceFormat.getSummary().equals("Kilometers/Meters")) {
            listSpeedWarningsOutTown.setEntries(new String[]{"5km/h", "10km/h", "15km/h", "20km/h", "25km/h"});
            listSpeedWarningsOutTown.setEntryValues(new String[]{"0", "1", "2", "3", "4"});
        } else {
            listSpeedWarningsOutTown.setEntries(new String[]{"5mi/h", "10mi/h", "15mi/h", "20mi/h", "25mi/h"});
            listSpeedWarningsOutTown.setEntryValues(new String[]{"0", "1", "2", "3", "4"});
        }
        if (listSpeedWarningsOutTown.getValue() == null) {
            listSpeedWarningsOutTown.setValueIndex(3);
        }
        listSpeedWarningsOutTown.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Set the value as the new value
                listSpeedWarningsOutTown.setValue(newValue.toString());
                // Get the entry which corresponds to the current value and set as summary
                preference.setSummary(listSpeedWarningsOutTown.getEntry());
                return false;
            }
        });

        final CheckBoxPreference checkBoxDayNight = (CheckBoxPreference) findPreference(PreferenceTypes
                .K_AUTO_DAY_NIGHT);
        checkBoxDayNight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

        final CheckBoxPreference checkBoxTollRoads = (CheckBoxPreference) findPreference(PreferenceTypes
                .K_AVOID_TOLL_ROADS);
        checkBoxTollRoads.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

        final CheckBoxPreference checkBoxFerries = (CheckBoxPreference) findPreference(PreferenceTypes.K_AVOID_FERRIES);
        checkBoxFerries.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

        final CheckBoxPreference checkBoxHighways = (CheckBoxPreference) findPreference(PreferenceTypes
                .K_AVOID_HIGHWAYS);
        checkBoxHighways.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

        final CheckBoxPreference checkBoxFreeDrive = (CheckBoxPreference) findPreference(PreferenceTypes.K_FREE_DRIVE);
        checkBoxFreeDrive.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

    }

}

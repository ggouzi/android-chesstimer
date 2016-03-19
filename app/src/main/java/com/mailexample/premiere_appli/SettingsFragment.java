package com.mailexample.premiere_appli;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.Map;

/**
 * Activity for loading preferences
 *
 * This activity is called by Settings.java
 *
 * @author Gaetan GOUZI
 * @version 1.0
 * @since 1.0
 * 03/2016
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        Map<String,?> keys = prefs.getAll();


        final int DEFAULT_TIME_INDEX = Integer.parseInt(getResources().getString(R.string.defaultTimeIndex));
        final int DEFAULT_INCREMENT_INDEX = Integer.parseInt(getResources().getString(R.string.defaultIncrementIndex));

        for(Map.Entry<String,?> entry : keys.entrySet()){
            if (entry.getKey().equals(getResources().getString(R.string.time_key))) {
                Preference pref = findPreference(entry.getKey());
                ListPreference listPref = (ListPreference) pref;
                listPref.setValueIndex(DEFAULT_TIME_INDEX);
                pref.setSummary(listPref.getEntry());
            }
            else if (entry.getKey().equals(getResources().getString(R.string.increment_key))) {
                Preference pref = findPreference(entry.getKey());
                ListPreference listPref = (ListPreference) pref;
                listPref.setValueIndex(DEFAULT_INCREMENT_INDEX);
                pref.setSummary(listPref.getEntry());
            }
            else {
                updateSummary(entry.getKey());
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(key);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public void updateSummary(String key){
        Preference pref = findPreference(key);

        if (pref instanceof EditTextPreference) {
            EditTextPreference editPref = (EditTextPreference) pref;
            pref.setSummary(editPref.getText());
        }

        else if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }
}
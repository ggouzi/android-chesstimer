package com.mailexample.premiere_appli;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activity for loading settings
 *
 * @author Gaetan GOUZI
 * @version 1.0
 * @since 1.0
 * 03/2016
 */

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
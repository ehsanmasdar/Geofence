package com.asdar.geofence;

import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.PreferenceActivity;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener;

import android.os.Bundle;

/**
 * Created by Ehsan on 8/17/13.
 */
public class SettingsActivity extends PreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        
    }


}

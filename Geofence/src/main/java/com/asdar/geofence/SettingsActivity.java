package com.asdar.geofence;

import android.os.Bundle;
import android.preference.PreferenceActivity;


/**
 * Created by Ehsan on 8/17/13.
 */
public class SettingsActivity extends PreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        
    }


}

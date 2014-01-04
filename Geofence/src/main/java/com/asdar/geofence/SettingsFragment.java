package com.asdar.geofence;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;


/**
 * Created by Ehsan on 8/17/13.
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(GeofenceUtils.SHARED_PREFERENCES);
        addPreferencesFromResource(R.xml.preferences);
    }
    
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		Preference modified = findPreference(key);
		if (key.equals("pref_dropbox_link") && prefs.getBoolean("pref_dropbox_link", false)){
			Resources res = getResources();
			modified.setTitle(res.getString(R.string.DroboxLinked));
			modified.setSummary(res.getString(R.string.DroboxLinkedSum));
		}
		else if ( key.equals("pref_dropbox_link") && !prefs.getBoolean("pref_dropbox_link", false)){
			Resources res = getResources();
			modified.setTitle(res.getString(R.string.DropboxLink));
			modified.setSummary(res.getString(R.string.DroboxLinkSum));
		}
        else if (key.equals("pref_locpriority")){
            Intent stop = new Intent();
            stop.setAction("com.asdar.geofence.locationstop");
            getActivity().sendBroadcast(stop);
            Intent start = new Intent();
            start.setAction("com.asdar.geofence.locationstart");
            getActivity().sendBroadcast(start);
        }
	}
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
}

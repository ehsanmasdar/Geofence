package com.asdar.geofence;

import android.content.res.Resources;

import android.os.Bundle;

import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.PreferenceFragment;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * Created by Ehsan on 8/17/13.
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}

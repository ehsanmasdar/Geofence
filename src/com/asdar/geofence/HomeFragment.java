package com.asdar.geofence;

import java.util.ArrayList;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class HomeFragment extends ListFragment {
	private EventListAdapter eventlistadapter;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    SharedPreferences mPrefs = (SharedPreferences) getActivity().getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
		ArrayList<SimpleGeofence> currentSimpleGeofences = new ArrayList<SimpleGeofence>();
		GeofenceStore geofencestorage = new GeofenceStore(getActivity().getApplicationContext());
		for (Integer i = 0; i < mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
				-1); i++) {
			
			currentSimpleGeofences.add(geofencestorage.getGeofence(
					i.toString(), getActivity().getApplicationContext()));
		}
		 eventlistadapter = new EventListAdapter(getActivity().getBaseContext(),
				R.layout.activity_main_listrow, currentSimpleGeofences);
	    setListAdapter(eventlistadapter);
	  }

	  @Override
	  public void onListItemClick(ListView l, View v, int position, long id) {
		  if(getActivity() instanceof MainActivity){
			  ((MainActivity) getActivity()).swapFragment(position);
		  }
	  }
	  public void onResume(){
		  super.onResume();
		  regenerateList();
	  }
		public void regenerateList() {
			SharedPreferences mPrefs = (SharedPreferences) getActivity().getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
	                Context.MODE_PRIVATE);
			GeofenceStore ge = new GeofenceStore(getActivity());
			ArrayList<SimpleGeofence> localcurrentSimpleGeofences = new ArrayList<SimpleGeofence>();
			for (Integer i = 0; i < mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
					-1); i++) {
				localcurrentSimpleGeofences.add(ge.getGeofence(i.toString(),
						getActivity().getApplicationContext()));
			}

			GeofenceUtils.update(eventlistadapter, localcurrentSimpleGeofences);
		eventlistadapter.notifyDataSetChanged();
	}
}

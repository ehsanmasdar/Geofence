package com.asdar.geofence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
public class MapFragment extends Fragment {

	private SupportMapFragment fragment;
	private GoogleMap map;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_map, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SharedPreferences mPrefs = this.getActivity().getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
	
		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (map == null) {
			map = fragment.getMap();
			SharedPreferences mPrefs = this.getActivity().getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
			ArrayList<SimpleGeofence> local = GeofenceUtils.getSimpleGeofences(mPrefs, getActivity());
			for (SimpleGeofence g : local){
				map.addMarker(new MarkerOptions().position(new LatLng(g.getLatitude(),g.getLongitude())).title(g.getName()).snippet(g.getAddress()));
				
			}
			 map.setMyLocationEnabled(true);
			 Activity a = (Activity) getActivity();
			 if (a instanceof MainActivity){
				 if(((MainActivity) a).getCurrentloc() != null){
					 double lat = ((MainActivity) a).getCurrentloc().getLatitude();
					 double lon = ((MainActivity) a).getCurrentloc().getLongitude();
					 LatLng centeroncurrent = new LatLng(lat,lon);
				     map.moveCamera(CameraUpdateFactory.newLatLngZoom(centeroncurrent, 14));
				 }
				 else{
					 LatLng firstgeofence = new LatLng(local.get(0).getLatitude(), local.get(0).getLongitude());
				     map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstgeofence, 14));
				 }
			 }
			 
		}
	}
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    try {
	        SupportMapFragment fragment = (SupportMapFragment) getActivity()
	                                          .getSupportFragmentManager().findFragmentById(
	                                              R.id.map);
	        if (fragment != null) 
	        	getFragmentManager().beginTransaction().remove(fragment).commit();

	    } catch (IllegalStateException e) {
	        //handle this situation because you are necessary will get 
	        //an exception here :-(
	    }
	}
}
package com.asdar.geofence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private CardListView list;
    private CardAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
         list = (CardListView)getActivity().findViewById(R.id.cardListView);
         adapter = new CardAdapter(getActivity()).setAccentColorRes(android.R.color.holo_green_light);
        regenerateList();
        list.setAdapter(adapter);
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
				localcurrentSimpleGeofences.add(ge.getGeofence(i,
						getActivity().getApplicationContext()));
			}
            adapter.clear();
            for (SimpleGeofence g : localcurrentSimpleGeofences){
                adapter.add(new CardHeader(g.getAddress()));
                List<Action> a = GeofenceUtils.generateActionArray(g.getId(),mPrefs,getActivity());
                for (Action action :a ){
                    adapter.add(new Card(action.getDescription(), action.listText()));
                }
            }
	}
}

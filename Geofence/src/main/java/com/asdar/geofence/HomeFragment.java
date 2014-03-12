package com.asdar.geofence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fima.cardsui.views.CardUI;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private CardUI cardView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
         cardView = (CardUI) getActivity().findViewById(R.id.cardsview);
         regenerateList();
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
            cardView.clearCards();
            for (SimpleGeofence g : localcurrentSimpleGeofences){
                String title = g.getName();
                String description = "";
                List<Action> a = GeofenceUtils.generateActionArray(g.getId(),mPrefs,getActivity(),GeofenceUtils.KEY_ACTIONLIST_ENTER);
                ArrayList<Integer> drawableicons = new ArrayList<Integer>();
                for (Action action :a ){
                    description += (action.listText()  + "\n");
                    drawableicons.add(action.getIcon());
                }
                cardView.addCard(new MyPlayCard(title,description,"#FF8C00","#bebebe",false,false,drawableicons,getActivity()));
            }
            cardView.refresh();
	}
}

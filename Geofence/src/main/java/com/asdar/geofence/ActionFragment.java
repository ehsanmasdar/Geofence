package com.asdar.geofence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ActionFragment extends ListFragment {
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    Integer id = getArguments().getInt("id");
	    SharedPreferences mPrefs = (SharedPreferences) getActivity().getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
    	List<Action> actionlist = GeofenceUtils.generateActionArray(id, mPrefs,getActivity().getApplicationContext(),GeofenceUtils.KEY_ACTIONLIST_ENTER);
    	
    	DrawerDisplayAdapter adapter = new DrawerDisplayAdapter(getActivity().getApplicationContext(),
    			R.layout.activity_main_listrow, (ArrayList)actionlist);
	    setListAdapter(adapter);
	  }

	  @Override
	  public void onListItemClick(ListView l, View v, int position, long id) {
	    // do something with the data

	  }
}

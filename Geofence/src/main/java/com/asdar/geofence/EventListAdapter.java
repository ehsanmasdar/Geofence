package com.asdar.geofence;


import java.util.ArrayList;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * Created by Ehsan on 8/14/13.
 */
public class EventListAdapter extends ArrayAdapter<SimpleGeofence> {

    private ArrayList<SimpleGeofence> items;
    private LayoutInflater vi;
    private Context c;
    public EventListAdapter(Context context, int textViewResourceId, ArrayList<SimpleGeofence> items) {
        super(context, textViewResourceId, items);
        vi = LayoutInflater.from(context);
        c = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = vi.inflate(R.layout.activity_main_listrow, null);
        }
        SimpleGeofence g = items.get(position);
        if (g != null) {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView at = (TextView) v.findViewById(R.id.actiontext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText(g.getName());
            }
            if (bt != null) {
                bt.setText("At " + g.getAddress());
            }
            if (at != null){
            	SharedPreferences mPrefs = (SharedPreferences) c.getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
                        Context.MODE_PRIVATE);
            	List<Action> actionlist = GeofenceUtils.generateActionArray(g.getId(), mPrefs, c);
            	String s = "";
            	if(actionlist.size() != 0){
                	for (int i = 0; i<actionlist.size();i++){
                		s = s+ actionlist.get(i).getDescription() + ", ";
                	}
                    at.setText(s.substring(0,s.length()-2));
            	}
            }
        }
        return v;
    }


}

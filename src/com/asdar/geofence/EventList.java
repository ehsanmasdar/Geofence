package com.asdar.geofence;


import org.holoeverywhere.LayoutInflater;

import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * Created by Ehsan on 8/14/13.
 */
public class EventList extends ArrayAdapter<SimpleGeofence> {

    private ArrayList<SimpleGeofence> items;
    private LayoutInflater vi;

    public EventList(Context context, int textViewResourceId, ArrayList<SimpleGeofence> items) {
        super(context, textViewResourceId, items);
        vi = LayoutInflater.from(context);
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
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText("Name: " + g.getName());
            }
            if (bt != null) {
                bt.setText("Location: " + g.getAddress());
            }
        }
        return v;
    }


}

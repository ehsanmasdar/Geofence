package com.asdar.geofence;

import android.view.View;

import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;



/**
 * Created by Ehsan on 8/14/13.
 */
public class DrawerDisplayAdapter extends ArrayAdapter<Action> {

    private ArrayList<Action> items;
    private LayoutInflater vi;
    private Context c;
    public DrawerDisplayAdapter(Context context, int textViewResourceId, ArrayList<Action> items) {
        super(context, textViewResourceId, items);
        c = context;
        vi = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = vi.inflate(R.layout.activity_main_listrow, null);
        }
        Action g = items.get(position);
        if (g != null) {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView at = (TextView) v.findViewById(R.id.actiontext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText(g.getDescription());
            }
            if (at != null){
                    at.setText(g.listText());
            	}
            }
        return v;
    }
}

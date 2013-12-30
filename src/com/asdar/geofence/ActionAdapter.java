package com.asdar.geofence;

/**
 * Created by Ehsan on 8/23/13.
 */

import android.view.View;

import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.ArrayAdapter;
import java.util.ArrayList;


import android.content.Context;



/**
 * Created by Ehsan on 8/14/13.
 */
public class ActionAdapter extends ArrayAdapter<Action> {

    private ArrayList<Action> items;
    private LayoutInflater vi;

    public ActionAdapter(Context context, int textViewResourceId, ArrayList<Action> items) {
        super(context, textViewResourceId, items);
        vi = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Action g = items.get(position);
        v = g.addView(getContext(), position);
        return v;
    }
}

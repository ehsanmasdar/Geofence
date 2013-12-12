package com.asdar.geofence;

/**
 * Created by Ehsan on 8/23/13.
 */

import android.view.View;

import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;


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

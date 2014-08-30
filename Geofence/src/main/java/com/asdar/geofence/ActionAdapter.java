package com.asdar.geofence;

/**
 * Created by Ehsan on 8/23/13.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;



/**
 * Created by Ehsan on 8/14/13.
 */
public class ActionAdapter extends ArrayAdapter<Action> {

    private final boolean exit;
    private ArrayList<Action> items;
    private LayoutInflater vi;

    public ActionAdapter(Context context, int textViewResourceId, ArrayList<Action> items,boolean e) {
        super(context, textViewResourceId, items);
        vi = LayoutInflater.from(context);
        this.items = items;
        this.exit = e;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Action g = items.get(position);
        v = g.addView(getContext(), position);
        Log.d("com.asdar.geofence", "Type: "  + exit);
        if (exit){
            ((TextView)v.findViewById(R.id.Type)).setText("On Exit");
        }
        else {
            ((TextView)v.findViewById(R.id.Type)).setText("On Enter");
        }
        return v;
    }
}

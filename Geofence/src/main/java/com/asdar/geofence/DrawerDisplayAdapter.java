package com.asdar.geofence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

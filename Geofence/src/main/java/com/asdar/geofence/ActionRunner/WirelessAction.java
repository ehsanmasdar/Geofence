package com.asdar.geofence.ActionRunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.asdar.geofence.Action;
import com.asdar.geofence.AddGeofences;
import com.asdar.geofence.GeofenceStore;
import com.asdar.geofence.GeofenceUtils;
import com.asdar.geofence.R;

/**
 * Created by s2094505 on 2/3/14.
 */
public class WirelessAction implements Action {
    //TRUE if you WANT it to be enabled, false if you want it to be disabled
    private boolean enabled;
    public WirelessAction() {
        enabled = true;
    }
    public WirelessAction(boolean e) {
        enabled = e;
    }

    @Override
    public void execute(Context context) {
        WifiManager w = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (w.getWifiState() == w.WIFI_STATE_DISABLED && enabled){
            w.setWifiEnabled(enabled);
        }
        else if (w.getWifiState() == w.WIFI_STATE_ENABLED && !enabled){
            w.setWifiEnabled(enabled);
        }
    }

    @Override
    public void commit(Context context, int id) {
        SharedPreferences mPrefs = (SharedPreferences) context.getSharedPreferences(
                GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        // Write the Geofence values to SharedPreferences
        editor.putBoolean(GeofenceStore.getGeofenceFieldKey(id,
                GeofenceUtils.KEY_WIRELESS), enabled);
        editor.commit();
    }

    @Override
    public Dialog editDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set Options");
        builder.setNegativeButton("Enable",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enabled = true;
                        AddGeofences.refreshAddAdapter();
                    }
                });
        builder.setPositiveButton("Disable",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enabled = false;
                        AddGeofences.refreshAddAdapter();
                    }
                });
        builder.setMessage("Would you like Wifi to be Enabled or Disabled when this location is reached?");
        builder.setCancelable(false);
        return builder.create();
    }

    @Override
    public View addView(Context context, int position) {
        View v = new View(context);

      LayoutInflater vi = LayoutInflater.from(context);
        v = vi.inflate(R.layout.activity_add_actionlist, null);
        if (v != null) {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText((++position) + ". " + "Wifi Toggle");
            }
            if (bt != null) {
                if (enabled) {
                    bt.setText("Wifi Will be Enabled");
                } else {
                    bt.setText("Wifi Will be Disabled");
                }
                bt.setTypeface(Typeface.DEFAULT, 2);
            }
        }
        return v;
    }

    @Override
    public Action generateSavedState(Context context, int id) {
        SharedPreferences mPrefs = (SharedPreferences) context.getSharedPreferences(
                GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean e = false;
        e = mPrefs.getBoolean(GeofenceStore.getGeofenceFieldKey(id,
                GeofenceUtils.KEY_WIRELESS), false);

        return new WirelessAction(e);
    }

    @Override
    public String notificationText() {
        String s = "";
        if (enabled){
            s = "enabled";
        }
        else {
            s = "disabled";
        }
        return "Wifi" + s;
    }

    @Override
    public String getDescription() {

        return "Toggle Wifi";
    }

    @Override
    public String listText() {
        String temp = "";
        if (enabled){
            temp = "enabled";
        }
        else {
            temp = "disabled";
        }
        return "Wifi will be changed to " + temp;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_action_network_wifi;
    }
}

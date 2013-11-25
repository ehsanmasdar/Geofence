package com.asdar.geofence;


import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.Toast;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class TestDialog extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle("Set Options");
    	builder.setNegativeButton("Mute", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("com.asdar.geofence", "true");
			}
		});
    	builder.setPositiveButton("Unmute", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("com.asdar.geofence", "false");
			}
		});
    	builder.setMessage("Would you like the Audio to be Muted or Unmuted when this location is reached?");
    	builder.setCancelable(false);
   	 	return builder.create();
    }
}

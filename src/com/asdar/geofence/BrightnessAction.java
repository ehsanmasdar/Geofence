package com.asdar.geofence;

import android.content.Context;



import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.provider.Settings.System;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.TextView;

import android.content.SharedPreferences.Editor;

/**
 * Created by Ehsan on 8/23/13.
 */
public class BrightnessAction extends Activity implements Action {
	private float brightness;
	private LayoutInflater vi;
	private SharedPreferences mPrefs;
	private String APP_KEY;
	private String APP_SECRET;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public BrightnessAction() {
		brightness = -1;
	}

	public BrightnessAction(float b) {
		brightness = b ;
	}

	@Override
	public void execute(Context context) {
		/*
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = brightness;
		getWindow().setAttributes(lp);
		*/
        System.putInt(context.getContentResolver(), System.SCREEN_BRIGHTNESS_MODE, System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		System.putInt(context.getContentResolver(), System.SCREEN_BRIGHTNESS,(int) (brightness/100)*255);
	}

	@Override
	public void commit(Context context, String id) {
		mPrefs = (SharedPreferences) context.getSharedPreferences(
				GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		// Write the Geofence values to SharedPreferences
		editor.putFloat(GeofenceStore.getGeofenceFieldKey(id,
				GeofenceUtils.KEY_BRIGHTNESS), brightness);
		editor.commit();

	}

	@Override
	public Dialog editDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Set Options");
		builder.setNegativeButton("Brightness 100",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						brightness  = 100;
						AddGeofences.refreshAddAdapter();
					}
				});
		builder.setPositiveButton("Brightness 0",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						brightness  = 0;
						AddGeofences.refreshAddAdapter();
					}
				});
		builder.setMessage("Set desired brightness");
		builder.setCancelable(false);
		return builder.create();
	}

	@Override
	public View addView(Context context, int position) {
		View v = new View(context);

		vi = LayoutInflater.from(context);
		v = vi.inflate(R.layout.activity_add_actionlist);
		if (v != null) {
			TextView tt = (TextView) v.findViewById(R.id.toptext);
			TextView bt = (TextView) v.findViewById(R.id.bottomtext);
			if (tt != null) {
				tt.setText((++position) + ". " + "Brightness");
			}
			if (bt != null) {
				bt.setText("Brightness will be changed to " + brightness);
				bt.setTypeface(Typeface.DEFAULT, 2);
			}
		}

		return v;
	}

	@Override
	public Action generateSavedState(Context context, String id)
			 {
		mPrefs = (SharedPreferences) context.getSharedPreferences(
				GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		float b = 0;

		b = mPrefs.getFloat(GeofenceStore.getGeofenceFieldKey(id,
				GeofenceUtils.KEY_BRIGHTNESS), -1);

		return new BrightnessAction(b);
	}

	@Override
	public String notificationText() {
		return "Brightness changed to " + brightness; 
	}

	public void setBrightness(float b) {
		brightness = b;
	}

	public String getDescription() {
		return "Change Brightness";
	}

	@Override
	public String listText() {
		// TODO Auto-generated method stub
		return "Brightness will changed to " + brightness;
	}
}

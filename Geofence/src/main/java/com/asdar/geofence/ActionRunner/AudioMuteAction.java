package com.asdar.geofence.ActionRunner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.asdar.geofence.Action;
import com.asdar.geofence.AddGeofences;
import com.asdar.geofence.GeofenceStore;
import com.asdar.geofence.GeofenceUtils;
import com.asdar.geofence.R;

/**
 * Created by Ehsan on 8/23/13.
 */
public class AudioMuteAction extends ActionBarActivity implements Action {
	private boolean AudioMute;
	private LayoutInflater vi;
	private SharedPreferences mPrefs;
	private String APP_KEY;
	private String APP_SECRET;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public AudioMuteAction() {
		AudioMute = false;
	}

	public AudioMuteAction(boolean b) {
		AudioMute = b;
	}

	@Override
	public void execute(Context context) {
		AudioManager m = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (AudioMute) {
			m.setRingerMode(m.RINGER_MODE_SILENT);
		} else {
			m.setRingerMode(m.RINGER_MODE_NORMAL);
		}
	}

	@Override
	public void commit(Context context, int id) {
		mPrefs = (SharedPreferences) context.getSharedPreferences(
				GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		APP_KEY = "a6kopt2el9go62x";
		APP_SECRET = "r5nhykcj43f0rbj";
		Editor editor = mPrefs.edit();
		// Write the Geofence values to SharedPreferences
		editor.putBoolean(GeofenceStore.getGeofenceFieldKey(id,
                GeofenceUtils.KEY_AUDIO_MUTE), AudioMute);
		editor.commit();

	}

	@Override
	public AlertDialog editDialog (Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Set Options");
		builder.setNegativeButton("Mute",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AudioMute = true;
						AddGeofences.refreshAddAdapter();
					}
				});
		builder.setPositiveButton("Unmute",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AudioMute = false;
						AddGeofences.refreshAddAdapter();
					}
				});
		builder.setMessage("Would you like the Audio to be Muted or Unmuted when this location is reached?");
		builder.setCancelable(false);
		return builder.create();
	}

	@Override
	public View addView(Context context, int position) {
		View v = new View(context);

		vi = LayoutInflater.from(context);
		v = vi.inflate(R.layout.activity_add_actionlist, null);
		if (v != null) {
			TextView tt = (TextView) v.findViewById(R.id.toptext);
			TextView bt = (TextView) v.findViewById(R.id.bottomtext);
			if (tt != null) {
				tt.setText((++position) + ". " + "Audio Mute");
			}
			if (bt != null) {
				if (AudioMute) {
					bt.setText("Audio Will be Muted");
				} else {
					bt.setText("Audio Will be Unmuted");
				}
				bt.setTypeface(Typeface.DEFAULT, 2);
			}
		}

		return v;
	}

	@Override
	public Action generateSavedState(Context context, int id) {
		mPrefs = (SharedPreferences) context.getSharedPreferences(
				GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Boolean b = false;

		b = mPrefs.getBoolean(GeofenceStore.getGeofenceFieldKey(id,
				GeofenceUtils.KEY_AUDIO_MUTE), false);

		return new AudioMuteAction(b);
	}

	@Override
	public String notificationText() {
		if (AudioMute) {
			return "Audio Muted";
		} else {
			return "Audio Unmuted";
		}
	}

	@Override
	public String listText() {
		if (AudioMute) {
			return "Audio Muted on entry";
		} else {
			return "Audio Unmuted on entry";
		}
	}

	public void setAudioMute(boolean b) {
		AudioMute = b;
	}

	public String getDescription() {
		return "Mute Audio";
	}
    public int getIcon(){
        return R.drawable.ic_action_volume_muted;
    }
}

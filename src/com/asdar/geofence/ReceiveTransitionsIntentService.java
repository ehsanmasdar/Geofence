package com.asdar.geofence;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReceiveTransitionsIntentService extends IntentService {
    /**
     * Sets an identifier for the service
     */
    private DbxAccountManager mDbxAcctMgr;
    private DbxDatastore store;
    private GeofenceStore g;
    public static final boolean INVALID_BOOLEAN_VALUE = false;
    private String APP_KEY;
    private String APP_SECRET;
    private SharedPreferences mPrefs;
    // The name of the SharedPreferences
    private static final String SHARED_PREFERENCES = "SharedPreferences";

    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
    }

    /**
     * Handles incoming intents
     *
     * @param intent The Intent sent by Location Services. This Intent is provided
     *               to Location Services (inside a PendingIntent) when you call
     *               addGeofences()
     */
    protected void onHandleIntent(Intent intent) {
        // First check for errors
        Log.d("com.asdar.geofence", "Recieved Geofence Trigger");
        mPrefs = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        APP_KEY = "a6kopt2el9go62x";
        APP_SECRET = "r5nhykcj43f0rbj";
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                APP_KEY, APP_SECRET);
        if (LocationClient.hasError(intent)) {
            // Get the error code with a static method
            int errorCode = LocationClient.getErrorCode(intent);
            // Log the error
            Log.e("ReceiveTransitionsIntentService",
                    "Location Services error: " + Integer.toString(errorCode));
            /*
             * You can also send the error code to an Activity or Fragment with
			 * a broadcast Intent
			 */
            /*
             * If there's no error, get the transition type and the IDs of the
			 * geofence or geofences that triggered the transition
			 */
        } else {
            // Get the type of transition (entry or exit)
            int transitionType = LocationClient.getGeofenceTransition(intent);
            // Test that a valid transition was reported
            if ((transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
                    || (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) || (transitionType == Geofence.GEOFENCE_TRANSITION_DWELL)) {
                List<Geofence> triggerList = LocationClient
                        .getTriggeringGeofences(intent);
                String[] triggerIds = new String[triggerList.size()];
                for (int i = 0; i < triggerIds.length; i++) {
                    // Store the Id of each geofence
                    triggerIds[i] = triggerList.get(i).getRequestId();
                }
                String notificationbuilder = "";
                for (int i = 0; i < triggerIds.length; i++) {
                    List<Action> locallist = GeofenceUtils.generateActionArray(triggerIds[i], mDbxAcctMgr, mPrefs, getApplicationContext());

                    for (Action a : locallist) {
                        notificationbuilder = (a.notificationText()) + ", ";
                        a.execute(getApplicationContext());
                    }
                }

                try {
                    if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER || transitionType == Geofence.GEOFENCE_TRANSITION_DWELL ) {
                        sendNotification(triggerIds, notificationbuilder , false);
                    }else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)  {
                    	Log.d("com.asdar.geofence", "left geofence");
                        sendNotification(triggerIds, notificationbuilder, true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ReceiveTransitionsIntentService",
                        "Geofence transition error: ");
            }
        }
        // An invalid transition was reported

    }


    private String getTransitionString(int transitionType) {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered Geofence";

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited Geofence";

            default:
                return "Unkown Action";
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String[] triggerIds, String contentext, boolean exit) throws IOException {
        if (!exit) {
            // Create an explicit content Intent that starts the main Activity
            Intent notificationIntent = new Intent(getApplicationContext(),
                    MainActivity.class);

            // Construct a task stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

            // Adds the main Activity to the task stack as the parent
            stackBuilder.addParentStack(MainActivity.class);

            // Push the content Intent onto the stack
            stackBuilder.addNextIntent(notificationIntent);

            // Get a PendingIntent containing the entire back stack
            PendingIntent notificationPendingIntent = stackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get a notification builder that's compatible with platform versions
            // >= 4
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    this);

           

            // Set the notification contents
            builder.setSmallIcon(R.drawable./* TEMP */ic_launcher)
                  .setContentTitle("At " + buildName(triggerIds[0]))
                    .setContentText(contentext)
                    .setContentIntent(notificationPendingIntent)
                    .setOngoing(true);

            // Get an instance of the Notification manager
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Issue the notification
            mNotificationManager.notify(102232, builder.build());
        } else {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(102232);
        }

    }

    public String buildName(String id) throws IOException {
        if (mDbxAcctMgr.hasLinkedAccount()) {
            store = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
            DbxTable geotable = store.getTable("Geofence");
            String name = geotable.getOrInsert(id).getString("mName");
            store.close();
            return name;

        } else {
            return mPrefs.getString(g.getGeofenceFieldKey(id, GeofenceUtils.KEY_NAME), GeofenceUtils.INVALID_STRING_VALUE);
        }
    }
}
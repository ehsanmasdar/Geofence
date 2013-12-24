package com.asdar.geofence;

import android.annotation.TargetApi;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import java.io.IOException;
import java.util.List;

public class ReceiveTransitionsIntentService extends IntentService {
    /**
     * Sets an identifier for the service
     */
   
    private GeofenceStore g;
    public static final boolean INVALID_BOOLEAN_VALUE = false;

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
        mPrefs = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
            // Get the type of transition (entry or exit)
            int transitionType = intent.getIntExtra("transitionType", 1 );
            // Test that a valid transition was reported
            if ((transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
                    || (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) || (transitionType == Geofence.GEOFENCE_TRANSITION_DWELL)) {
                int triggerID = intent.getIntExtra("id", 0);
                String notificationbuilder = "";
                    List<Action> locallist = GeofenceUtils.generateActionArray(triggerID, mPrefs, getApplicationContext());

                    for (Action a : locallist) {
                        notificationbuilder = (a.notificationText()) + ", ";
                        
                        if (notificationbuilder.length() >0){
                        	notificationbuilder = notificationbuilder.substring(0,notificationbuilder.length()-2);
                        }
                        a.execute(getApplicationContext());
                    }
                

                try {
                    if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER || transitionType == Geofence.GEOFENCE_TRANSITION_DWELL ) {
                        sendNotification(triggerID, notificationbuilder , false);
                    }else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)  {
                    	Log.d("com.asdar.geofence", "left geofence");
                        sendNotification(triggerID, notificationbuilder, true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ReceiveTransitionsIntentService",
                        "Geofence transition error: ");
            }
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
    private void sendNotification(int triggerID, String contentext, boolean exit) throws IOException {
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
                  .setContentTitle("At " + buildName(triggerID))
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

    public String buildName(int id) throws IOException {
            return mPrefs.getString(g.getGeofenceFieldKey(id, GeofenceUtils.KEY_NAME), GeofenceUtils.INVALID_STRING_VALUE);

    }
}
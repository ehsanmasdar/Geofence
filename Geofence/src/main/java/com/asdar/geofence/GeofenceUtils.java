package com.asdar.geofence;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import dalvik.system.DexFile;

public final class GeofenceUtils {
	private static ArrayAdapter arrayAdapter;
	private static ArrayList<Object> listOfObject;

	public enum REQUEST_TYPE {
		ADD, ADDONE, REMOVE
	}
	public static final int ADD_GEOFENCE_REQUEST = 10;
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static final CharSequence GEOFENCE_ID_DELIMITER = ",";
	public static final int ActionNum = 1;
	public static final String KEY_LATITUDE = "com.asdar.geofence.KEY_LATITUDE";
	public static final String KEY_LONGITUDE = "com.asdar.geofence.KEY_LONGITUDE";
	public static final String KEY_RADIUS = "com.asdar.geofence.KEY_RADIUS";
	public static final String KEY_EXPIRATION_DURATION = "com.asdar.geofence.KEY_EXPIRATION_DURATION";
	public static final String KEY_TRANSITION_TYPE = "com.asdar.geofence.KEY_TRANSITION_TYPE";
	public static final String KEY_AUDIO_MUTE = "com.asdar.geofence.KEY_AUDIO_MUTE";
	public static final String KEY_NAME = "com.asdar.geofence.KEY_NAME";
	public static final String KEY_ADDRESS = "com.asdar.geofence.KEY_ADDRESS";
	public static final String KEY_ACTIONLIST = "com.asdar.geofence.KEY_ACTIONLIST";

	public static final String KEY_DELAY = "com.asdar.geofence.KEY_DELAY";
	public static final String KEY_RESPONSIVENESS = "com.asdar.geofence.KEY_RESPONSIVENESS";

	public static final String KEY_STARTID = "com.asdar.geofence.KEY_STARTID";
	// The prefix for flattened geofence keys
	public static final String KEY_PREFIX = "com.asdar.geofence";
	/*
	 * Invalid values, used to test geofence storage when retrieving geofences
	 */
	public static final long INVALID_LONG_VALUE = -999l;
	public static final float INVALID_FLOAT_VALUE = -999.0f;
	public static final int INVALID_INT_VALUE = -999;
	public static final boolean INVALID_BOOLEAN_VALUE = false;
	public static final String INVALID_STRING_VALUE = null;
	public static final String SHARED_PREFERENCES = "SharedPreferences";
	public static final String KEY_BRIGHTNESS = "com.asdar.geofence.KEY_BRIGHTNESS";

	public static void update(ArrayAdapter arrayAdapter,
			ArrayList<?> listOfObject) {
		arrayAdapter.clear();
      if (android.os.Build.VERSION.SDK_INT >= 11) {

		arrayAdapter.addAll(listOfObject);
      }
      else {
          for (Object a : listOfObject){
              arrayAdapter.add(a);
          }
      }
	}

	public static void update(ArrayAdapter arrayAdapter, Action a, int updatepos) {
		arrayAdapter.remove(arrayAdapter.getItem(updatepos));
		arrayAdapter.add(a);
	}

	// TODO:Fix for devices below Honeycomb
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void save(ArrayList<Action> actionlist, Editor e,
			int geofenceid) {
		HashSet<String> list = new HashSet<String>();
		for (Action a : actionlist) {
			String str = a.toString();
			int c = str.indexOf('@');
			list.add(str.substring(0, c));
		}
		e.putStringSet(
				GeofenceStore.getGeofenceFieldKey(geofenceid, KEY_ACTIONLIST),
				list);
		e.commit();

	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static List<Action> generateActionArray(int id,
			SharedPreferences mPrefs, Context c) {
		ArrayList<Action> list = new ArrayList<Action>();
		Set<String> local = mPrefs.getStringSet(
				GeofenceStore.getGeofenceFieldKey(id, KEY_ACTIONLIST), null);
		for (String str : local) {
			list.add(queryString(str, id, c));
		}

		return list;
	}

	public static Action queryString(String str, int id, Context context) {
		try {
			Action a = (Action) Class.forName(str).newInstance();
			return a.generateSavedState(context, id);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Takes all loaded classes with "Action" in their name and returns a string
	 * array of their names
	 * 
	 * @param context
	 *            The application context
	 * @return A String array containing full qualified class paths of all
	 *         Action classes in the current package
	 */
	public static String[] generateOptions(Context context)
			throws ClassNotFoundException, IOException {
		ArrayList<String> arr = new ArrayList<String>();
		Set<Class<?>> set = getClasspathClasses(context, "com.asdar.geofence");
		Iterator i = set.iterator();
		while (i.hasNext()) {
			Class<?> c = (Class<?>) i.next();
			Log.d("com.asdar.geofence", "Class" + c.getName());
			if (c.getName().toLowerCase().contains("action")
					&& !c.getName().toLowerCase()
							.equals("com.asdar.geofence.action")
					&& !c.getName().toLowerCase()
							.equals("com.asdar.geofence.actionadapter")
					&& !c.getName().toLowerCase().equals("com.asdar.geofence.actionfragment")
					&& !c.getName().contains("$"))
			{
				arr.add(c.getName());
			}
		}
		Log.d("com.asdar.geofence", "Arr Size");
		return arr.toArray(new String[0]);
	}

	/**
	 * Returns all Classes in the current package
	 * 
	 * @param context
	 *            The application context
	 * @param packageName
	 *            The package name
	 * @return A Set of all the classes in the current package
	 */
	public static Set<Class<?>> getClasspathClasses(Context context,
			String packageName) throws ClassNotFoundException, IOException {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		DexFile dex = new DexFile(context.getApplicationInfo().sourceDir);
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		Enumeration<String> entries = dex.entries();
		while (entries.hasMoreElements()) {
			String entry = entries.nextElement();
			if (entry.toLowerCase().startsWith(packageName.toLowerCase()))
				classes.add(classLoader.loadClass(entry));
		}
		return classes;
	}

	/**
	 * Takes all loaded classes with "Action" in their name and returns a string
	 * array of their names
	 * 
	 * @param context
	 *            The application context
	 * @return A String array containing UI names for all Action classes in the
	 *         current package
	 */
	public static String[] generateNames(Context context)
			throws ClassNotFoundException, IOException {
		ArrayList<String> arr = new ArrayList<String>();
		Set<Class<?>> set = getClasspathClasses(context, "com.asdar.geofence");
		Iterator i = set.iterator();
		while (i.hasNext()) {
			Class<?> c = (Class<?>) i.next();
			if (c.getName().toLowerCase().contains("action")
					&& !c.getName().toLowerCase()
							.equals("com.asdar.geofence.action")
					&& !c.getName().toLowerCase()
							.equals("com.asdar.geofence.actionadapter")
					&& !c.getName().toLowerCase().equals("com.asdar.geofence.actionfragment")
					&& !c.getName().contains("$")) {
				try {
					arr.add(((Action) (c.newInstance())).getDescription());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return arr.toArray(new String[0]);
	}
	public static ArrayList<SimpleGeofence> getSimpleGeofences(SharedPreferences mPrefs,Context c){
		ArrayList<SimpleGeofence> currentSimpleGeofences = new ArrayList<SimpleGeofence>();
		GeofenceStore geofencestorage = new GeofenceStore(c);
		for (Integer i = 0; i < mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
				-1); i++) {
			currentSimpleGeofences.add(geofencestorage.getGeofence(
					i, c));
		}
		return currentSimpleGeofences;
	}
	
}

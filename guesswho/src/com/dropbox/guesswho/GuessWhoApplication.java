package com.dropbox.guesswho;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class GuessWhoApplication extends Application {

	private static final String APPLICATION_PREFERENCES_NAME = "GuessWhoApplication APPLICATION_PREFERENCES_NAME";
	private static final String NUM_HALPERS_KEY = "GuessWhoApplication NUM_HALPERS_KEY";
	private static final String HALPER_KEY_PREFIX = "GuessWhoApplication HALPER_KEY_";

	public static final String USER_ID_KEY = "GuessWhoApplication USER_ID_KEY";
	public static final String TARGET_ID_KEY = "GuessWhoApplication TARGET_ID_KEY";

	private static RequestQueue requestQueue;
	private static SharedPreferences applicationPreferences;

	@Override
	public void onCreate() {
		super.onCreate();
		requestQueue = Volley.newRequestQueue(getApplicationContext());
		applicationPreferences = getSharedPreferences(
				APPLICATION_PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	public static RequestQueue getRequestQueue() {
		return requestQueue;
	}

	public static SharedPreferences getApplicationPreferences() {
		return applicationPreferences;
	}

	public static void saveHalpers(ArrayList<String> halperUrls) {
		SharedPreferences.Editor editor = applicationPreferences.edit();
		if (applicationPreferences.contains(NUM_HALPERS_KEY)) {
			int oldNumHalpers = applicationPreferences.getInt(NUM_HALPERS_KEY,
					0);
			for (int i = 0; i < oldNumHalpers; i++) {
				editor.remove(HALPER_KEY_PREFIX + i);
			}
			editor.remove(NUM_HALPERS_KEY);
		}
		editor.putInt(NUM_HALPERS_KEY, halperUrls.size());
		for (int i = 0; i < halperUrls.size(); i++) {
			editor.putString(HALPER_KEY_PREFIX + i, halperUrls.get(i));
		}
		editor.commit();
	}

	public static ArrayList<String> getHalpers() {
		ArrayList<String> halperUrls = new ArrayList<String>();
		if (applicationPreferences.contains(NUM_HALPERS_KEY)) {
			int numHalpers = applicationPreferences.getInt(NUM_HALPERS_KEY, 0);
			for (int i = 0; i < numHalpers; i++) {
				String key = HALPER_KEY_PREFIX + i;
				if (applicationPreferences.contains(key)) {
					halperUrls.add(applicationPreferences.getString(key, ""));
				}
			}
		}
		return halperUrls;
	}
}

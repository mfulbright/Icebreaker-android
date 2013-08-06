package com.dropbox.guesswho;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Application;

public class GuessWhoApplication extends Application {

	private static RequestQueue requestQueue;
	
	@Override
	public void onCreate() {
		super.onCreate();
		requestQueue = Volley.newRequestQueue(getApplicationContext());
	}
	
	public static RequestQueue getRequestQueue() {
		return requestQueue;
	}
}

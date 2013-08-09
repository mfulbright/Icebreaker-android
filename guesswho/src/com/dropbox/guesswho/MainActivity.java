package com.dropbox.guesswho;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// this activity is only used to determine the correct application to
		// show on launch
		SharedPreferences appPrefs = GuessWhoApplication
				.getApplicationPreferences();
		if (appPrefs.contains(GuessWhoApplication.USER_ID_KEY)) { // they're
																	// logged in
			if (appPrefs.contains(GuessWhoApplication.TARGET_ID_KEY)) {
				// they have a target as well - show the target
				Intent newIntent = new Intent(this, ShowTargetActivity.class);
				startActivity(newIntent);
			} else { // they haven't gotten a target yet - kick it over to the
				// get target screen
				Intent newIntent = new Intent(this, GetTargetActivity.class);
				startActivity(newIntent);
			}
		} else {
			// they need to log in. check if they were bounced here from a
			// confirmation email
			Intent intent = getIntent();
			Uri data = intent.getData();
			if (data != null) {
				// we were launched from a confirmation email link
				log("Confirmation email redirect detected - from " + data);
				String userId = data.toString().split("://")[1];
				Intent newIntent = new Intent(this, AuthenticateActivity.class);
				newIntent.putExtra(AuthenticateActivity.USER_ID_EXTRA, userId);
				startActivity(newIntent);
			} else {
				// a totally new user! let them log in
				Intent newIntent = new Intent(this, LoginActivity.class);
				startActivity(newIntent);
			}
		}
	}
}

package com.dropbox.guesswho;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class GetTargetActivity extends BaseActivity implements
		Response.Listener<JSONObject>, Response.ErrorListener {

	private TextView numberTextView;
	private TextView clueTextView;
	private LinearLayout buttonLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_target);

		numberTextView = (TextView) findViewById(R.id.number_text);
		clueTextView = (TextView) findViewById(R.id.clue_text);
		buttonLayout = (LinearLayout) findViewById(R.id.button_layout);

		SharedPreferences appPrefs = GuessWhoApplication
				.getApplicationPreferences();
		if (appPrefs.contains(GuessWhoApplication.TARGET_ID_KEY)) {
			// a target has already been downloaded - just show it
			displayTarget();
		} else {
			// we need to download a target
			String userId = appPrefs.getString(GuessWhoApplication.USER_ID_KEY, "");
			RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
			JsonObjectRequest request = new JsonObjectRequest(Method.GET,
					"http://limitless-caverns-4433.herokuapp.com/users/" + userId + "/new_assignment", null, this, this);
			requestQueue.add(request);
			startShowLoading();
		}
	}

	@Override
	public void onResponse(JSONObject response) {
		stopShowLoading();
		log(response);
		try {
			String clue = response.getString("fact");
			String targetId = response.getString("target_id");
			JSONArray halperArray = response.getJSONArray("halpers");
			log("parsed");
			ArrayList<String> halperUrls = new ArrayList<String>();
			for (int i = 0; i < halperArray.length(); i++) {
				halperUrls.add(halperArray.getString(i));
			}
			GuessWhoApplication.getApplicationPreferences().edit()
					.putString(GuessWhoApplication.TARGET_ID_KEY, targetId)
					.putBoolean(GuessWhoApplication.ACCEPTED_TARGET_KEY, false)
					.putString(GuessWhoApplication.TARGET_CLUE_KEY, clue)
					.commit();
			GuessWhoApplication.saveHalpers(halperUrls);
			log("saved");
			displayTarget();
		} catch (JSONException e) {
			log("exception");
			log(e);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		stopShowLoading();
		log(error);
		alert("Network error", "We had a problem downloading your target - try again!");
	}
	
	private void displayTarget() {
		log("displaying");
		// todo numberTextView?
		SharedPreferences appPrefs = GuessWhoApplication.getApplicationPreferences();
		String clue = appPrefs.getString(GuessWhoApplication.TARGET_CLUE_KEY, "No clue");
		clueTextView.setText(clue);
		clueTextView.setVisibility(View.VISIBLE);
		buttonLayout.setVisibility(View.VISIBLE);
	}

	public void playButtonClicked(View v) {
		log();
		SharedPreferences appPrefs = GuessWhoApplication.getApplicationPreferences();
		appPrefs.edit().putBoolean(GuessWhoApplication.ACCEPTED_TARGET_KEY, true);
		// kick it over to the show target activity
		Intent intent = new Intent(this, ShowTargetActivity.class);
		startActivity(intent);
	}

	public void knowThemButtonClicked(View v) {
		log();
		new DialogFragment() {
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Sure you want to skip?");
				builder.setMessage("Get 1 point (and exercise) by bumping phones with the Mystery Dropboxer!");
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// todo
							}
						});
				builder.setNegativeButton("No", null);
				return builder.create();
			}
		}.show(getSupportFragmentManager(), "Useless GetTargetActivity Popup Key");
	}
}

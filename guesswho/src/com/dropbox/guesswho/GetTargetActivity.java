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
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class GetTargetActivity extends BaseActivity {

	private FunFactViewPager funFactPager;
	private LinearLayout buttonLayout;
	private ArrayList<String> funFacts;
	private ArrayList<String> halperUrls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_target);

		funFactPager = (FunFactViewPager) findViewById(R.id.fun_fact_view_pager);
		buttonLayout = (LinearLayout) findViewById(R.id.button_layout);

		attemptLoadCurrentTarget();
	}

	private void attemptLoadCurrentTarget() {
		SharedPreferences appPrefs = GuessWhoApplication
				.getApplicationPreferences();
		String userId = appPrefs.getString(GuessWhoApplication.USER_ID_KEY, "");
		RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				"http://limitless-caverns-4433.herokuapp.com/users/" + userId
						+ "/current_assignment", null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						onLoadCurrentTarget(response);

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						onLoadCurrentTargetError(error);
					}
				});
		requestQueue.add(request);
		startShowLoading();
	}

	public void onLoadCurrentTarget(JSONObject response) {
		stopShowLoading();
		log(response);
		try {
			JSONArray funFactsArray = response.getJSONArray("facts");
			funFacts = new ArrayList<String>();
			for (int i = 0; i < funFactsArray.length(); i++) {
				funFacts.add(funFactsArray.getString(i));
			}
			String targetId = response.getString("target_id");
			JSONArray halperArray = response.getJSONArray("halpers");
			halperUrls = new ArrayList<String>();
			for (int i = 0; i < halperArray.length(); i++) {
				JSONObject imageObject = halperArray.getJSONObject(i);
				halperUrls.add(imageObject.getString("image"));
			}
			GuessWhoApplication.getApplicationPreferences().edit()
					.putString(GuessWhoApplication.TARGET_ID_KEY, targetId)
					.commit();
			FunFactPagerAdapter pagerAdapter = new FunFactPagerAdapter(this,
					funFacts);
			funFactPager.setAdapter(pagerAdapter);
			funFactPager.setVisibility(View.VISIBLE);
			buttonLayout.setVisibility(View.VISIBLE);
		} catch (JSONException e) {
			log(e);
		}
	}

	public void onLoadCurrentTargetError(VolleyError error) {
		stopShowLoading();
		log(error);
		alert("Network error",
				"We had a problem downloading your target - try again!");
	}

	public void playButtonClicked(View v) {
		log();
		// kick it over to the show target activity
		Intent intent = new Intent(this, ShowTargetActivity.class);
		intent.putExtra(ShowTargetActivity.FUN_FACTS_EXTRA, funFacts);
		intent.putExtra(ShowTargetActivity.HALPER_URLS_EXTRA, halperUrls);
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
								knowCurrentTarget();
							}
						});
				builder.setNegativeButton("No", null);
				return builder.create();
			}
		}.show(getSupportFragmentManager(),
				"Useless GetTargetActivity Popup Key");
	}

	private void knowCurrentTarget() {
		SharedPreferences appPrefs = GuessWhoApplication
				.getApplicationPreferences();
		String userId = appPrefs.getString(GuessWhoApplication.USER_ID_KEY, "");
		RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				"http://limitless-caverns-4433.herokuapp.com/users/" + userId
						+ "/skip_assignment/never", null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						onKnowCurrentTarget(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						onKnowCurrentTargetError(error);
					}
				});
		requestQueue.add(request);
		startShowLoading();
	}

	private void onKnowCurrentTarget(JSONObject response) {
		stopShowLoading();
		log(response);
		// delete the target from the shared preferences and load a new one
		SharedPreferences appPrefs = GuessWhoApplication
				.getApplicationPreferences();
		appPrefs.edit().remove(GuessWhoApplication.TARGET_ID_KEY).commit();
		Toast.makeText(this, "Target skipped", Toast.LENGTH_SHORT).show();
		attemptLoadCurrentTarget();
	}

	private void onKnowCurrentTargetError(VolleyError error) {
		stopShowLoading();
		log(error);
		alert("Network error", "We couldn't connect to the network. Try again.");
	}

	public void leaderboardButtonClicked(View v) {
		Intent intent = new Intent(this, LeaderboardActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
}

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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

public class ShowTargetActivity extends BaseActivity implements
		Response.Listener<JSONObject>, Response.ErrorListener {

	public static final String CLUE_EXTRA = "ShowTargetActivity CLUE_EXTRA";
	public static final String HALPER_URLS_EXTRA = "ShowTargetActivity HALPER_URLS_EXTRA";

	private TextView clueTextView;
	private String clue;
	private ArrayList<String> halperUrls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_target);
		
		clueTextView = (TextView) findViewById(R.id.clue_text);

		Intent intent = getIntent();
		if (intent.hasExtra(CLUE_EXTRA) && intent.hasExtra(HALPER_URLS_EXTRA)) {
			// everything we need was given to us
			clue = intent.getStringExtra(CLUE_EXTRA);
			halperUrls = (ArrayList<String>) intent
					.getSerializableExtra(HALPER_URLS_EXTRA);
			displayTarget();
		} else {
			// need to load everything
			String userId = GuessWhoApplication.getApplicationPreferences()
					.getString(GuessWhoApplication.USER_ID_KEY, "");
			RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
			JsonObjectRequest request = new JsonObjectRequest(Method.GET,
					"http://limitless-caverns-4433.herokuapp.com/users/"
							+ userId + "/current_assignment", null, this, this);
			requestQueue.add(request);
			startShowLoading();
		}
	}

	@Override
	public void onResponse(JSONObject response) {
		stopShowLoading();
		log(response);
		try {
			clue = response.getString("fact");
			JSONArray halperArray = response.getJSONArray("halpers");
			halperUrls = new ArrayList<String>();
			for (int i = 0; i < halperArray.length(); i++) {
				JSONObject imageObject = halperArray.getJSONObject(i);
				halperUrls.add(imageObject.getString("image"));
			}
			displayTarget();
		} catch (JSONException e) {
			log(e);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		stopShowLoading();
		log(error);
		alert("Network error",
				"We had a problem downloading your target - try again!");

	}

	private void displayTarget() {
		clueTextView.setText(clue);
		RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
		ImageLoader imageLoader = new ImageLoader(requestQueue,
				NoImageCache.instance());
		switch (halperUrls.size()) {
		// note: intentionally no break statements here
		case 4:
			setImageUrl(imageLoader, R.id.fourth_halper_image,
					halperUrls.get(3));
		case 3:
			setImageUrl(imageLoader, R.id.third_halper_image, halperUrls.get(2));
		case 2:
			setImageUrl(imageLoader, R.id.second_halper_image,
					halperUrls.get(1));
		case 1:
			setImageUrl(imageLoader, R.id.first_halper_image, halperUrls.get(0));
		}
	}

	private void setImageUrl(ImageLoader imageLoader, int imageViewId,
			String halperUrl) {
		NetworkImageView imageView = (NetworkImageView) findViewById(imageViewId);
		imageView.setImageUrl(halperUrl, imageLoader);
	}

	public void skipButtonClicked(View v) {
		log();
		new DialogFragment() {
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Give up?!");
				builder.setMessage("Are you SURE you want to give up and skip?");
				builder.setPositiveButton("Lamesauce",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// todo
							}
						});
				builder.setNegativeButton("Keep Trying!", null);
				return builder.create();
			}
		}.show(getSupportFragmentManager(),
				"Useless ShowTargetActivity Popup Key");
	}

	public void leaderboardButtonClicked(View v) {
		Intent intent = new Intent(this, LeaderboardActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void receiveThroughBump(byte[] data) {
		SharedPreferences appPrefs = GuessWhoApplication.getApplicationPreferences();
		if(appPrefs.contains(GuessWhoApplication.TARGET_ID_KEY)) {
			String bumpedId = new String(data);
			String targetId = appPrefs.getString(GuessWhoApplication.TARGET_ID_KEY, "");
			if(bumpedId.equals(targetId)) {
				bumpCorrectTarget();
			} else {
				bumpIncorrectTarget();
			}
		}
		// otherwise, there's not really much for us to do
	}

	private void bumpCorrectTarget() {
		log();
		// todo ping the server
		new DialogFragment() {
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("YOU DID IT!!");
				builder.setMessage("+1 for finding the Mystery Dropboxer :)");
				builder.setPositiveButton("Next challenge",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// todo
							}
						});
				builder.setNegativeButton("Add my own fun fact",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										ShowTargetActivity.this,
										AddFunFactActivity.class);
								startActivity(intent);
							}
						});
				return builder.create();
			}
		}.show(getSupportFragmentManager(),
				"Useless ShowTargetActivity Popup Key 2");
	}
	
	// todo
	private void bumpIncorrectTarget() {
		log();
		alert("NICE TRY...", "Keep searching! Don't forget to ask your Halpers.");
	}
}

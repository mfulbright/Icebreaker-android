package com.dropbox.guesswho;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class AuthenticateActivity extends BaseActivity implements
		Response.Listener<JSONObject>, Response.ErrorListener {

	public static final String USER_ID_EXTRA = "BaseActivity USER_ID_EXTRA";

	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate);

		Intent intent = getIntent();
		userId = intent.getStringExtra(USER_ID_EXTRA);

		RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				"http://limitless-caverns-4433.herokuapp.com/authenticate_user/"
						+ userId, null, this, this);
		requestQueue.add(request);
		startShowLoading();
	}

	@Override
	public void onResponse(JSONObject response) {
		stopShowLoading();
		log(response);
		try {
			if (response.has("success")) {
				int alreadyAuthed;

				alreadyAuthed = response.getInt("already_authenticated");

				switch (alreadyAuthed) {
				case 0:
					// this is what we expect
					SharedPreferences appPrefs = GuessWhoApplication
							.getApplicationPreferences();
					appPrefs.edit()
							.putString(GuessWhoApplication.USER_ID_KEY, userId)
							.commit();
					Intent intent = new Intent(this, GetTargetActivity.class);
					startActivity(intent);
					break;
				case 1:
					// ...unexpected. the only case I can really think of when
					// this will happen
					// is a n00b downloads the app, doesn't auth, then an old
					// timer troll forwards
					// them an email with an auth link. in that case we should
					// let the n00b have
					// the already authed userId, so that's what we'll do here
					log("Received a user id that is already authed. Id: "
							+ userId);
					break;
				default:
					log("Unexpected value for already_authenticated: "
							+ alreadyAuthed);
					break;
				}
			} else if (response.has("error")) {
				int code = response.getInt("error");
				switch (code) {
				case -1:
					// invalid id. hmm...
					log("User attempted to register with invalid id: " + userId);
					alert("Bad email link", "We didn't recognize the user associated with that confirmation link. Try signing up again?");
					break;
				default:
					log("Unexpected error code: " + code);
					break;
				}
			} else {
				log("Json response didn't contain 'success' or 'error'");
			}
		} catch (JSONException e) {
			log(e);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		stopShowLoading();
		alert("Network error", "There was an error connecting to the network. Try opening the confirmation email again");
		log(error);
		log(error.networkResponse);
		log(error.getMessage());
	}
}

package com.dropbox.guesswho;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class LoginActivity extends BaseActivity implements
		Response.Listener<JSONObject>, Response.ErrorListener {

	private EditText emailEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		emailEditText = (EditText) findViewById(R.id.email_edit_text);
	}

	public void signUpButtonClicked(View v) {
		String email = emailEditText.getText().toString();
		// might as well make sure it's a dropbox email address
		if (!Pattern.matches("[^@]+@[^@\\.]+\\.[^@\\.]+", email)) {
			alert("Bad email",
					"Please enter a valid @dropbox.com email address");
			return;
		}
		if (!email.endsWith("@dropbox.com")) {
			alert("Dropboxers only!",
					"Please enter an @dropbox.com email address");
			return;
		}
		RequestQueue queue = GuessWhoApplication.getRequestQueue();
		JsonObjectRequest request = new JsonObjectRequest(
				Method.GET,
				"http://limitless-caverns-4433.herokuapp.com/register/" + email,
				null, this, this);
		queue.add(request);
		startShowLoading();
	}

	@Override
	public void onResponse(JSONObject response) {
		stopShowLoading();
		log(response);
		try {
			if (response.has("success")) {
				log("Success");
				log(response.get("success"));
				if (response.has("already_authenticated")
						&& response.getInt("already_authenticated") == 1) {
					alert("Re-sent email",
							"That email address has been registered before - we re-sent the confirmation email anyway");
				} else {
					alert("You're in!",
							"We sent a confirmation email to that email address. Open it and click the link inside");

				}
			} else if (response.has("error")) {
				int code = response.getInt("error");
				switch (code) {
				case -1:
					// not a dropbox email address. we already do checking for
					// this but whatever
					alert("Bad email",
							"It looks like that email isn't a Dropbox email address");
					break;
				default:
					// huh?
					log("Unrecognized error code : " + code);
					break;
				}
			} else {
				// huh?
				log("json response didn't have 'success' or 'error' keys");
			}
		} catch (JSONException e) {
			log(e);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		stopShowLoading();
		log(error);
		log(error.networkResponse);
		log(error.getMessage());
		alert("Network error",
				"We had trouble connecting to the site. Try again.");
	}
}

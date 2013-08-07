package com.dropbox.guesswho;

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
		// todo - maybe make sure it's a valid email
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
				// a confirmation email was sent - nothing to do here
				log("Success");
				log(response.get("success"));
			} else if (response.has("error")) {
				int code = response.getInt("error");
				switch (code) {
				case -1:
					// not a real dropbox email address
					alert("Bad email", "It looks like that email isn't a real Dropbox email address (at least, we didn't recognize it...)");
					break;
				case -2:
					// email address already used and authenticated
					alert("Bad email", "That email address is already being used! Try again!");
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
		alert("Network error", "We had trouble connecting to the site. Try again.");
	}
}

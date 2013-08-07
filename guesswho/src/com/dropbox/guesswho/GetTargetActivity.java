package com.dropbox.guesswho;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_target);

		numberTextView = (TextView) findViewById(R.id.number_text);
		clueTextView = (TextView) findViewById(R.id.clue_text);
		
		RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				"http://limitless-caverns-4433.herokuapp.com/todo", null, this,
				this);
		requestQueue.add(request);
		startShowLoading();
	}


	@Override
	public void onResponse(JSONObject response) {
		stopShowLoading();
		log(response);
		// todo parse response
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		stopShowLoading();
		log(error);
		// todo handle error
	}

	public void playButtonClicked(View v) {
		log();
	}

	public void knowThemButtonClicked(View v) {
		log();
	}
}

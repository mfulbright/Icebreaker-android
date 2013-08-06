package com.dropbox.guesswho;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class MainActivity extends SherlockActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void startButtonClicked(View v) {
    	RequestQueue queue = GuessWhoApplication.getRequestQueue();
//    	JsonObjectRequest request = new JsonObjectRequest(
//    			Method.GET, 
//    			"http://limitless-caverns-4433.herokuapp.com/",
//    			null,
//    			this,
//    			this
//    			);
//    	queue.add(request);
    	onResponse(null);
    }
    
    @Override
	public void onResponse(JSONObject response) {
		// parse JSONObject here
    	int dropboxerNumber = 1;
    	String clue = "My family owns Santa Barbara Honda";
    	ArrayList<String> helperUrls = new ArrayList<String>();
    	helperUrls.add("http://limitless-caverns-4433.herokuapp.com/images/1");
    	helperUrls.add("http://limitless-caverns-4433.herokuapp.com/images/2");
    	Intent playIntent = new Intent(this, PlayActivity.class);
    	playIntent.putExtra(PlayActivity.DROPBOXER_NUMBER_EXTRA, dropboxerNumber);
    	playIntent.putExtra(PlayActivity.CLUE_EXTRA, clue);
    	playIntent.putStringArrayListExtra(PlayActivity.HELPER_URLS_EXTRA, helperUrls);
    	startActivity(playIntent);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// show a popup or something
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getSupportMenuInflater().inflate(R.menu.main, menu);
	    return true;
	}
}

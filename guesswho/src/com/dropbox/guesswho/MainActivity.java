package com.dropbox.guesswho;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class MainActivity extends SherlockActivity implements Response.Listener<String>, Response.ErrorListener {

	private TextView responseText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        responseText = (TextView) findViewById(R.id.response_text);
    }
    
    public void buttonClicked(View v) {
    	RequestQueue queue = GuessWhoApplication.getRequestQueue();
    	StringRequest request = new StringRequest(
    			Method.GET, 
    			"http://limitless-caverns-4433.herokuapp.com/",
    			this,
    			this
    			);
    	queue.add(request);
    }

    @Override
	public void onResponse(String response) {
		responseText.setText(response);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		responseText.setText(error.getMessage());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getSupportMenuInflater().inflate(R.menu.main, menu);
	    return true;
	}
}

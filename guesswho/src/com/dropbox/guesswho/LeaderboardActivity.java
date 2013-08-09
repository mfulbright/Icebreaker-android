package com.dropbox.guesswho;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.NoCache;

public class LeaderboardActivity extends BaseActivity implements
		Response.Listener<JSONArray>, Response.ErrorListener {

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);

		listView = (ListView) findViewById(R.id.list);

		RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
		JsonArrayRequest request = new JsonArrayRequest(
				"http://limitless-caverns-4433.herokuapp.com/leaderboard",
				this, this);
		requestQueue.add(request);
		startShowLoading();
	}

	@Override
	public void onResponse(JSONArray response) {
		stopShowLoading();
		log(response);

		try {
			ArrayList<JSONObject> leaders = new ArrayList<JSONObject>();
			for (int i = 0; i < response.length(); i++) {
				leaders.add(response.getJSONObject(i));
			}
			LeaderboardAdapter adapter = new LeaderboardAdapter(this, leaders);
			listView.setAdapter(adapter);
		} catch (JSONException e) {
			log(e);
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		stopShowLoading();
		log(error);
		alert("Network error",
				"We had trouble getting the leaderboard. Try again.");
	}

	private static class LeaderboardAdapter extends ArrayAdapter<JSONObject> {

		private LayoutInflater inflater;
		private ImageLoader imageLoader;

		public LeaderboardAdapter(Context context, ArrayList<JSONObject> data) {
			super(context, 0, data);
			inflater = LayoutInflater.from(context);
			imageLoader = new ImageLoader(GuessWhoApplication.getRequestQueue(), NoImageCache.instance());
		}

		@Override
		public View getView(int position, View recycledView, ViewGroup parent) {
			if (recycledView == null) {
				recycledView = inflater.inflate(R.layout.list_item_leaderboard,
						null);
			}

			JSONObject leaderObject = getItem(position);
			try {
				String imageUrl = leaderObject.getString("image");
				NetworkImageView imageView = (NetworkImageView) recycledView.findViewById(R.id.leader_image);
				Log.e("Mark", imageUrl);
				Log.e("Mark2", imageLoader.toString());
				imageView.setImageUrl(imageUrl, imageLoader);
				
				String name = leaderObject.getString("name");
				TextView nameView = (TextView) recycledView
						.findViewById(R.id.leader_name);
				nameView.setText(name);

				int score = leaderObject.getInt("score");
				TextView scoreView = (TextView) recycledView
						.findViewById(R.id.leader_score);
				scoreView.setText("" + score);

			} catch (JSONException e) {
				Log.e("Adapter error", e.toString());
			}

			return recycledView;
		}
	}
}

package com.dropbox.guesswho;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class EditFunFactsActivity extends BaseActivity {

	private EditText funFactEditText;
	private ArrayList<String> funFactsList;
	private FunFactsAdapter listAdapter;
	private ListView funFactsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_fun_facts);

		funFactEditText = (EditText) findViewById(R.id.fun_fact_edit_text);
		funFactsListView = (ListView) findViewById(R.id.fun_facts_list);

		LayoutInflater inflater = LayoutInflater.from(this);
		View emptyView = inflater.inflate(R.layout.list_item_fun_fact_empty,
				null);
		funFactsListView.setEmptyView(emptyView);

		attemptLoadFunFacts();
	}
	
	private void attemptLoadFunFacts() {
		SharedPreferences appPrefs = GuessWhoApplication
				.getApplicationPreferences();
		String userId = appPrefs.getString(GuessWhoApplication.USER_ID_KEY, "");
		RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				"http://limitless-caverns-4433.herokuapp.com/users/" + userId,
				null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						onLoadFunFacts(response);

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						onLoadFunFactsError(error);
					}
				});
		requestQueue.add(request);
		startShowLoading();
	}

	private void onLoadFunFacts(JSONObject response) {
		stopShowLoading();
		try {
			JSONArray funFactsArray = response.getJSONArray("facts");
			funFactsList = new ArrayList<String>();
			for (int i = 0; i < funFactsArray.length(); i++) {
				funFactsList.add(funFactsArray.getString(i));
			}
			listAdapter = new FunFactsAdapter(this, funFactsList);
			funFactsListView.setAdapter(listAdapter);
		} catch (JSONException e) {
			log(e);
		}
	}

	private void onLoadFunFactsError(VolleyError error) {
		stopShowLoading();
		log(error);
		new DialogFragment() {
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Network error");
				builder.setMessage("We couldn't connect to the network. What do you want to do?");
				builder.setPositiveButton("Skip",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										EditFunFactsActivity.this,
										GetTargetActivity.class);
								startActivity(intent);
							}
						});
				builder.setNegativeButton("Try again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								attemptLoadFunFacts();
							}
						});
				return builder.create();
			}
		}.show(getSupportFragmentManager(),
				"Useless EditFunFactsActivity Popup Key");
	}

	private void onSaveFunFacts(JSONObject response) {
		stopShowLoading();
		log(response);
		Toast.makeText(this, "Your fun facts have been added!", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, GetTargetActivity.class);
		startActivity(intent);
	}

	private void onSaveFunFactsError(VolleyError error) {
		stopShowLoading();
		log(error);
		alert("Network error", "We couldn't connect to the network. Try again");
		// don't go anywhere, just keep them on this screen
	}

	public void addButtonClicked(View v) {
		log();
		String funFact = funFactEditText.getText().toString();
		funFactEditText.setText("");
		if (funFactsList != null && listAdapter != null) {
			funFactsList.add(funFact);
			listAdapter.notifyDataSetChanged();
		}
	}

	public void skipButtonClicked(View v) {
		log();
		Intent intent = new Intent(this, GetTargetActivity.class);
		startActivity(intent);
	}

	public void doneButtonClicked(View v) {
		log();
		startShowLoading();
		SharedPreferences appPrefs = GuessWhoApplication
				.getApplicationPreferences();
		String userId = appPrefs.getString(GuessWhoApplication.USER_ID_KEY, "");
		RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();

		// create the post data to upload
		JSONArray factsArray = new JSONArray(funFactsList);
		JSONObject postData = new JSONObject();
		try {
			postData.put("facts", factsArray);
		} catch (JSONException e) {
			log(e);
		}

		JsonObjectRequest request = new JsonObjectRequest(Method.POST,
				"http://limitless-caverns-4433.herokuapp.com/users/" + userId
						+ "/facts", postData,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						onSaveFunFacts(response);

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						onSaveFunFactsError(error);
					}
				});
		requestQueue.add(request);
	}

	public void listDeleteButtonClicked(View v) {
		log();
		int deletedPosition = (Integer) v.getTag();
		if (funFactsList != null && listAdapter != null && deletedPosition >= 0
				&& funFactsList.size() > deletedPosition) {
			funFactsList.remove(deletedPosition);
			listAdapter.notifyDataSetChanged();
		}
	}

	private static class FunFactsAdapter extends ArrayAdapter<String> {

		private LayoutInflater inflater;

		public FunFactsAdapter(Context context, ArrayList<String> data) {
			super(context, 0, data);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View recycledView, ViewGroup parent) {
			if (recycledView == null) {
				recycledView = inflater.inflate(R.layout.list_item_fun_fact,
						null);
			}
			String funFact = getItem(position);
			TextView funFactTextView = (TextView) recycledView
					.findViewById(R.id.fun_fact_text);
			funFactTextView.setText(funFact);

			// there might be a better way to do this but this is pretty easy
			Button deleteButton = (Button) recycledView
					.findViewById(R.id.delete_button);
			deleteButton.setTag(position);

			return recycledView;
		}
	}
	
	@Override
	public void onBackPressed() {
		// do nothing - we don't want them going back, they have the skip 
		// button to move forward without making any changes
	}
}
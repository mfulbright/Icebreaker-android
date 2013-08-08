package com.dropbox.guesswho;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class ShowTargetActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_target);

		SharedPreferences appPrefs = GuessWhoApplication
				.getApplicationPreferences();

		TextView clueTextView = (TextView) findViewById(R.id.clue_text);
		clueTextView.setText(appPrefs.getString(
				GuessWhoApplication.TARGET_CLUE_KEY, "No clue"));

		RequestQueue requestQueue = GuessWhoApplication.getRequestQueue();
		ImageLoader imageLoader = new ImageLoader(requestQueue, null);
		ArrayList<String> halperUrls = GuessWhoApplication.getHalpers();
		switch (halperUrls.size()) {
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
		}.show(getSupportFragmentManager(), "Useless ShowTargetActivity Popup Key");
	}

	public void leaderboardButtonClicked(View v) {
		log();
		// todo leaderboard
	}
}

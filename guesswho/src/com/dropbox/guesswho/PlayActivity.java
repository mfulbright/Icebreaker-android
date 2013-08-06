package com.dropbox.guesswho;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public class PlayActivity extends SherlockActivity {
	
	public static final String DROPBOXER_NUMBER_EXTRA = "PlayActivity DROPBOXER_NUMBER_EXTRA";
	public static final String CLUE_EXTRA = "PlayActivity CLUE_EXTRA";
	public static final String HELPER_URLS_EXTRA = "PlayActivity HELPER_URLS_EXTRA";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		Intent intent = getIntent();
		
		TextView numberText = (TextView) findViewById(R.id.number_text);
		int dropboxerNumber = intent.getIntExtra(DROPBOXER_NUMBER_EXTRA, 1);
		numberText.setText(getString(R.string.mystery_dropboxer_number, dropboxerNumber));
		
		String clue = intent.getStringExtra(CLUE_EXTRA);
		TextView clueText = (TextView) findViewById(R.id.clue_text);
		clueText.setText(clue);
	}
	
	public void playButtonClicked(View v) {
		Intent pingIntent = new Intent(this, PingActivity.class);
		startActivity(pingIntent);
	}
	
	public void knowThemButtonClicked(View v) {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

}

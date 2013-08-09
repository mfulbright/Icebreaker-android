package com.dropbox.guesswho;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddFunFactActivity extends BaseActivity {
	
	EditText funFactEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fun_fact);
		
		funFactEditText = (EditText) findViewById(R.id.fun_fact_edit_text);
	}
	
	public void goButtonClicked(View v) {
		log();
		// todo send to the website
	}
	
	public void skipButtonClicked(View v) {
		log();
		Intent intent = new Intent(this, GetTargetActivity.class);
		startActivity(intent);
	}
}
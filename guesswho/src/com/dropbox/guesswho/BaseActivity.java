package com.dropbox.guesswho;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;

public class BaseActivity extends SherlockActivity {

	private ProgressBar progressBar;

	protected void log(Object message) {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		String className = caller.getClassName();
		className = className.substring(className.lastIndexOf(".") + 1);
		String methodName = caller.getMethodName();
		int lineNumber = caller.getLineNumber();
		if(message == null) {
			message = "null";
		}
		Log.e(className + "." + methodName + " (line " + lineNumber + ")", message.toString());
	}

	protected void log() {
		log("");
	}

	protected void log(int num) {
		log("" + num);
	}

	@SuppressLint("NewApi")
	protected void startShowLoading() {
		if (progressBar == null) {
			progressBar = (ProgressBar) LayoutInflater.from(this).inflate(
					R.layout.progress_bar, null);
		}
		ViewGroup rootContainer = (ViewGroup) findViewById(android.R.id.content);
		for (int i = 0; i < rootContainer.getChildCount(); i++) {
			View child = rootContainer.getChildAt(i);
			if (Build.VERSION.SDK_INT >= 11) {
				child.setAlpha(0.3f);
				child.setEnabled(false);
			} else {
				child.setVisibility(View.GONE);
			}
		}
		rootContainer.addView(progressBar);
	}

	@SuppressLint("NewApi")
	protected void stopShowLoading() {
		ViewGroup rootContainer = (ViewGroup) findViewById(android.R.id.content);
		rootContainer.removeViewAt(rootContainer.getChildCount() - 1);
		for (int i = 0; i < rootContainer.getChildCount(); i++) {
			View child = rootContainer.getChildAt(i);
			if (Build.VERSION.SDK_INT >= 11) {
				child.setAlpha(1.0f);
				child.setEnabled(true);
			} else {
				child.setVisibility(View.VISIBLE);
			}
		}
	}
	
	protected void alert(String title, String message) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(message).show();
	}
}

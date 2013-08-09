package com.dropbox.guesswho;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.bump.api.BumpAPIIntents;
import com.bump.api.IBumpAPI;

public class BaseActivity extends SherlockFragmentActivity {

	private IBumpAPI api = null;
	private ServiceConnection connection;
	private BroadcastReceiver receiver;
	private boolean connected;

	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// disable screen rotation for every activity
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		api = null;
		connected = false;

		connection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className,
					IBinder binder) {
				log("onServiceConnected");
				api = IBumpAPI.Stub.asInterface(binder);
				// why are we throwing this in a new thread, you ask? Well we
				// get NetworkOnMainThreadException's if we don't. Why does it
				// work in the test app but not here, even when running on the
				// same phone with the exact same code? I have no fucking clue
				new Thread() {
					public void run() {
						try {
							api.configure("8990ba777c5340f98eb21033cfd9b06e",
									"Bump User");
						} catch (RemoteException e) {
							Log.w("BumpTest", e);
						}
					}
				}.start();
				log("Service connected");
			}

			@Override
			public void onServiceDisconnected(ComponentName className) {
				log("Service disconnected");
			}
		};

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				try {
					if (action.equals(BumpAPIIntents.DATA_RECEIVED)) {
						log("Received data from: "
								+ api.userIDForChannelID(intent.getLongExtra(
										"channelID", 0)));
						log("Data: "
								+ new String(intent.getByteArrayExtra("data")));
						receiveThroughBump(intent.getByteArrayExtra("data"));
					} else if (action.equals(BumpAPIIntents.MATCHED)) {
						long channelID = intent.getLongExtra(
								"proposedChannelID", 0);
						log("Matched with: "
								+ api.userIDForChannelID(channelID));
						api.confirm(channelID, true);
						log("Confirm sent");
					} else if (action.equals(BumpAPIIntents.CHANNEL_CONFIRMED)) {
						long channelID = intent.getLongExtra("channelID", 0);
						log("Channel confirmed with "
								+ api.userIDForChannelID(channelID));
						api.send(channelID, sendThroughBump());
					} else if (action.equals(BumpAPIIntents.NOT_MATCHED)) {
						log("Not matched.");
					} else if (action.equals(BumpAPIIntents.CONNECTED)) {
						log("Connected to Bump...");
						connected = true;
						api.enableBumping();
					}
				} catch (RemoteException e) {
				}
			}
		};

		bindService(new Intent(IBumpAPI.class.getName()), connection,
				Context.BIND_AUTO_CREATE);
		log("Bump boot");

		IntentFilter filter = new IntentFilter();
		filter.addAction(BumpAPIIntents.CHANNEL_CONFIRMED);
		filter.addAction(BumpAPIIntents.DATA_RECEIVED);
		filter.addAction(BumpAPIIntents.NOT_MATCHED);
		filter.addAction(BumpAPIIntents.MATCHED);
		filter.addAction(BumpAPIIntents.CONNECTED);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (connected && api != null) {
			try {
				api.enableBumping();
			} catch (RemoteException e) {
				log(e);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (api != null) {
			try {
				api.disableBumping();
			} catch (RemoteException e) {
				log(e);
			}
		}
	}

	@Override
	protected void onDestroy() {
		unbindService(connection);
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	/*
	 * This method exists so that subclasses can override what is sent when a
	 * Bump occurs on their screen. (Not that any of our activities really need
	 * it)
	 */
	protected byte[] sendThroughBump() {
		// send the user id, or an empty string if we don't have one yet
		SharedPreferences appPrefs = GuessWhoApplication
				.getApplicationPreferences();
		return appPrefs.getString(GuessWhoApplication.USER_ID_KEY, "")
				.getBytes();
	}

	/*
	 * This method exists so that subclasses can override what is sent when a
	 * Bump occurs on their screen.
	 */
	protected void receiveThroughBump(byte[] data) {
		// do nothing
		log(data);
		log(new String(data));
	}

	public static void log(Object message) {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		String className = caller.getClassName();
		className = className.substring(className.lastIndexOf(".") + 1);
		String methodName = caller.getMethodName();
		int lineNumber = caller.getLineNumber();
		if (message == null) {
			message = "null";
		}
		Log.e(className + "." + methodName + " (line " + lineNumber + ")",
				message.toString());
	}

	public static void log() {
		log("");
	}

	public static void log(int num) {
		log("" + num);
	}

	@SuppressLint("NewApi")
	protected void startShowLoading() {
		if (progressBar == null) {
			// we had the progress bar styling in xml, but it wasn't working for
			// some reason. seriously. so now it's programmatic.
			progressBar = new ProgressBar(this, null,
					android.R.attr.progressBarStyleLarge);
		}
		// this is kinda hacky, but the root container in android is a
		// FrameLayout
		FrameLayout rootContainer = (FrameLayout) findViewById(android.R.id.content);
		for (int i = 0; i < rootContainer.getChildCount(); i++) {
			View child = rootContainer.getChildAt(i);
			if (Build.VERSION.SDK_INT >= 11) {
				child.setAlpha(0.3f);
				child.setEnabled(false);
			} else {
				child.setVisibility(View.GONE);
			}
		}

		rootContainer.addView(progressBar, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
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
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.show();
	}
}

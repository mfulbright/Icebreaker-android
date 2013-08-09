package com.dropbox.guesswho;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class NoImageCache implements ImageCache {
	
	private static final NoImageCache staticCache = new NoImageCache();
	
	public static NoImageCache instance() {
		return staticCache;
	}

	@Override
	public Bitmap getBitmap(String url) {
		return null;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
	}
}

package com.dropbox.guesswho;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

public class FunFactViewPager extends ViewPager {

	public FunFactViewPager(Context context) {
		super(context);
	}
	
	public FunFactViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		if (adapter instanceof FunFactPagerAdapter) {
			((FunFactPagerAdapter) adapter).setFunFactViewPager(this);
		}
		super.setAdapter(adapter);
	}
}

package com.dropbox.guesswho;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FunFactPagerAdapter extends PagerAdapter {

	private LayoutInflater inflater;
	private ArrayList<String> funFacts;
	private FunFactViewPager viewPager;

	public FunFactPagerAdapter(Context context, ArrayList<String> facts) {
		inflater = LayoutInflater.from(context);
		funFacts = facts;
	}

	public void setFunFactViewPager(FunFactViewPager pager) {
		viewPager = pager;
	}

	@Override
	public int getCount() {
		return funFacts.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.pager_item_fun_fact, null);

		String funFact = funFacts.get(position);
		TextView funFactTextView = (TextView) layout
				.findViewById(R.id.clue_text);
		funFactTextView.setText(funFact);

		ImageView leftArrow = (ImageView) layout.findViewById(R.id.left_arrow);
		if (position == 0) {
			leftArrow.setVisibility(View.INVISIBLE);
		} else {
			leftArrow.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					leftArrowClicked();
				}
			});
		}

		ImageView rightArrow = (ImageView) layout
				.findViewById(R.id.right_arrow);
		if (position == funFacts.size() - 1) {
			rightArrow.setVisibility(View.INVISIBLE);
		} else {
			rightArrow.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					rightArrowClicked();
				}
			});
		}

		container.addView(layout, position);

		return layout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeViewAt(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	private void leftArrowClicked() {
		if (viewPager != null) {
			int newPosition = viewPager.getCurrentItem() - 1;
			if (newPosition >= 0) {
				viewPager.setCurrentItem(newPosition);
			}
		}
	}

	private void rightArrowClicked() {
		if (viewPager != null) {
			int newPosition = viewPager.getCurrentItem() + 1;
			if (newPosition < funFacts.size()) {
				viewPager.setCurrentItem(newPosition);
			}
		}
	}
}

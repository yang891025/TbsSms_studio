package com.tbs.tbssms.adapter;

import com.tbs.tbssms.constants.Constants;

import android.support.v4.view.ViewPager;
import android.view.View;

public class MyPagerAdapter extends android.support.v4.view.PagerAdapter {

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return Constants.views.size();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(Constants.views.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(Constants.views.get(position));
		return Constants.views.get(position);
	}
}

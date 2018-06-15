package com.tbs.tbssms.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPage extends ViewPager {

	private boolean enabled;
	
	public MyViewPage(Context context) {
		super(context);
		this.enabled = false;
	}

	public MyViewPage(Context context, AttributeSet attrs) {
		super(context,attrs);
		this.enabled = false;
	}

	// 触摸没有反应就可以了,解决界面左右滑动的问题,把该功能关闭
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}
	
	public void setPagingEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
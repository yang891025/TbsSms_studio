package com.tbs.tbssms.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.tbs.tbssms.R;
import com.tbs.tbssms.adapter.BookFisherPagerAdapter;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.entity.Location;

public class LauncherActivity extends Activity implements OnClickListener, OnTouchListener, OnGestureListener {

	private static final String TAG = "LauncherActivity";

	private int[] ids = { R.drawable.guider_01, R.drawable.guider_02 };
	private List<View> guides = new ArrayList<View>();
	private ViewPager pager;
	private Button open;
	private ImageView curDot;
	private int offset;// 位移量
	private int curPos = 0;// 记录当前的位置

	boolean launchState;
	Intent intent;
	List<Location> loc;
	String url;

	GestureDetector mGestureDetector;// 手势类

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		init();
	}

	private void init() {

		mGestureDetector = new GestureDetector(this);

		View view = getLayoutInflater().inflate(R.layout.guider_open_app, null);

		for (int i = 0; i < ids.length; i++) {
			ImageView iv = new ImageView(this);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
			iv.setImageResource(ids[i]);
			iv.setLayoutParams(params);
			iv.setScaleType(ScaleType.FIT_XY);
			guides.add(iv);
		}
		guides.add(view);

		open = (Button) view.findViewById(R.id.beginBtn);
		open.setOnClickListener(this);
		Log.d(TAG, "guides length :" + guides.size());

		curDot = (ImageView) findViewById(R.id.cur_dot);
		curDot.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						offset = curDot.getWidth();
						return true;
					}
				});

		BookFisherPagerAdapter adapter = new BookFisherPagerAdapter(guides);
		pager = (ViewPager) findViewById(R.id.contentPager);

		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				moveCursorTo(arg0);
				if (arg0 == 2) {
					pager.setOnTouchListener(LauncherActivity.this);
				}
				curPos = arg0;
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 移动指针到相邻的位置
	 * 
	 * @param position
	 *            指针的索引值
	 * 
	 **/
	private void moveCursorTo(int position) {
		// 使用绝对位置
		TranslateAnimation anim = new TranslateAnimation(offset * curPos,offset * position, 0, 0);
		anim.setDuration(0);
		anim.setFillAfter(true);
		curDot.startAnimation(anim);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.beginBtn:
			setSharePerference(com.tbs.tbssms.constants.Constants.SHARED_PREFERENCE_FIRST_LAUNCH);
			intent = new Intent();
			intent.setClass(LauncherActivity.this, MainTab.class);
			startActivity(intent);
			break;
		}
	}

	// 触摸事件
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	// 手势事件
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {

		if (e1.getX() - e2.getX() > 80 && Math.abs(velocityX) > 80) {
			// 跳转下一个
			setSharePerference(com.tbs.tbssms.constants.Constants.SHARED_PREFERENCE_FIRST_LAUNCH);
			intent = new Intent();
			intent.setClass(LauncherActivity.this, MainTab.class);
			startActivity(intent);
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public void setSharePerference(String perferenceName) {
		SharedPreferences setting = getSharedPreferences(perferenceName,MODE_PRIVATE);
		SharedPreferences.Editor editor = setting.edit();
		editor.putBoolean("launchState", true);
		editor.commit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}

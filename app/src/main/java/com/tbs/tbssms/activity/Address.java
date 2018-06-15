package com.tbs.tbssms.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tbs.tbssms.R;
import com.tbs.tbssms.adapter.AddressMultAdapter;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.entity.AddressEntity;
import com.tbs.tbssms.util.SideBarChat;
import com.tbs.tbssms.util.Util;

public class Address extends Activity {

	protected static final String TAG = "Address";

	private BroadcastReceiver MyBroadCastReceiver;// 广播
	private Intent intent;// intent意图

	// address content
	private ListView addressLV;
	private SideBarChat indexBar;

	private WindowManager mWindowManager;
	private TextView mDialogText;

	LayoutInflater inflater;
	private View toastView;

	AddressMultAdapter multAdapter;// 通讯录适配器
	private EditText searchET;
	private TextView textview;

	private int searchState = 0;
	private AddressEntity[] addressEntities;

	private ArrayList<Integer> positionList;
	boolean searchBtnState = true;

	private AddressMultAdapter searchAdapter;
	private List<AddressEntity> searchAddressList;

	private Button searchBtn;
	private ImageButton clearnBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_chat);
		AppManager.getInstance().addActivity(this);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		init();
		address();
	}

	private void init() {
		inflater = LayoutInflater.from(Address.this);
		toastView = inflater.inflate(R.layout.address_dialog, null);

		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
					finish();
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);
	}

	// 设置标题栏右侧按钮的作用
	public void btnmainright(View v) {
		if (Constants.isCheckedMap.size() > 0) {
			positionList = new ArrayList<Integer>();
			Iterator iterator = Constants.isCheckedMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				boolean tempValue = (Boolean) entry.getValue();
				int tempPosition = (Integer) entry.getKey();
				if (tempValue) {
					// 写代码
					positionList.add(tempPosition);
				}
			}
			if (positionList.size() > 1) {
				intent = new Intent();
				intent.putExtra("size", positionList.size());
				intent.putIntegerArrayListExtra("positionList", positionList);
				intent.putExtra("searchState", searchState);
				intent.setClass(Address.this, ChatMsg.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_in, R.anim.push_out);
				Constants.isCheckedMap.clear();
				positionList.clear();
				finish();
			} else {
				if (positionList.size() > 0) {
					if (searchState == 0) {
						intent = new Intent();
						intent.putExtra("name",
								Constants.arrays[positionList.get(0)].getName());
						intent.putExtra("phone", Constants.arrays[positionList
								.get(0)].getPhone());
						intent.putExtra("searchState", searchState);
						intent.setClass(Address.this, ChatMsg.class);
						startActivity(intent);
						overridePendingTransition(R.anim.push_in,
								R.anim.push_out);
						Constants.isCheckedMap.clear();
						positionList.clear();
						finish();
					}
					if (searchState == 1) {
						intent = new Intent();
						intent.putExtra("name",
								Constants.searchArrays[positionList.get(0)]
										.getName());
						intent.putExtra("phone",
								Constants.searchArrays[positionList.get(0)]
										.getPhone());
						intent.putExtra("searchState", searchState);
						intent.setClass(Address.this, ChatMsg.class);
						startActivity(intent);
						overridePendingTransition(R.anim.push_in,
								R.anim.push_out);
						Constants.isCheckedMap.clear();
						positionList.clear();
						finish();
					}
				}
			}
		}
	}

	// 设置标题栏左侧按钮的作用
	public void btnmainleft(View v) {
		Constants.isCheckedMap.clear();
		finish();
		overridePendingTransition(R.anim.push_fast, R.anim.push_up);
	}

	private void address() {// 通讯录初始化方法

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(toastView, lp);

		mDialogText = (TextView) toastView.findViewById(R.id.textview);// 找到listview
		searchET = (EditText) findViewById(R.id.search_bar_et);
		clearnBtn = (ImageButton) findViewById(R.id.search_clear_bt);
		searchBtn = (Button) findViewById(R.id.search_more_btn);
		textview = (TextView) findViewById(R.id.textview);
		addressLV = (ListView) findViewById(R.id.listview);// 找到listview
		indexBar = (SideBarChat) findViewById(R.id.sideBar);// 找到侧边字母滑动栏

		searchBtn.setVisibility(View.VISIBLE);

		multAdapter = new AddressMultAdapter(Address.this, Constants.arrays);// 穿件适配器

		addressLV.setAdapter(multAdapter);// 填充适配器

		// Util.setListViewFastScoll(addressLV, this,
		// R.drawable.fast_scroll_bg);

		ViewTreeObserver vto2 = indexBar.getViewTreeObserver();// 获得控件高度
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				indexBar.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
				int sidebarHeight = indexBar.getHeight() / indexBar.l.length;
				indexBar.m_nItemHeight = sidebarHeight;// 设置indexbar的字母间隔高度
			}
		});

		indexBar.setListView(addressLV);// 设置listview
		indexBar.setContext(this);
		indexBar.setActivity(this);
		indexBar.setToastView(Constants.toastView);
		indexBar.setTextView(mDialogText);// 设置textview

		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchState == 0) {
					if (searchBtnState) {
						for (int i = 0; i < Constants.arrays.length; i++) {
							Constants.isCheckedMap.put(i, true);
						}
						multAdapter.notifyDataSetChanged();
						searchBtnState = false;
						searchBtn.setText("取消");
					} else {
						for (int i = 0; i < Constants.arrays.length; i++) {
							Constants.isCheckedMap.put(i, false);
						}
						multAdapter.notifyDataSetChanged();
						searchBtnState = true;
						searchBtn.setText("全选");
					}
				}
				if (searchState == 1) {
					if (searchBtnState) {
						for (int i = 0; i < searchAddressList.size(); i++) {
							Constants.isCheckedMap.put(i, true);
						}
						searchAdapter.notifyDataSetChanged();
						searchBtnState = false;
						searchBtn.setText("取消");
					} else {
						for (int i = 0; i < searchAddressList.size(); i++) {
							Constants.isCheckedMap.put(i, false);
						}
						searchAdapter.notifyDataSetChanged();
						searchBtnState = true;
						searchBtn.setText("全选");
					}
				}
			}
		});

		searchET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.i("username", "onTextChanged...");
				if (TextUtils.isEmpty(s)) {
					clearnBtn.setVisibility(View.INVISIBLE);
					addressLV.setAdapter(Constants.adapter);
					searchState = 0;// 短信搜索
				} else {
					clearnBtn.setVisibility(View.VISIBLE);
					searchAddressList = Util.getAddressCursor(Address.this,
							searchET.getText().toString());
					if (searchAddressList.size() >= 0) {
						textview.setVisibility(View.GONE);
						addressLV.setVisibility(View.VISIBLE);
						Constants.searchArrays = Util
								.getArray(searchAddressList);
						searchAdapter = new AddressMultAdapter(Address.this,
								Constants.searchArrays);
						addressLV.setAdapter(searchAdapter);
					} else {
						textview.setVisibility(View.VISIBLE);
						addressLV.setVisibility(View.INVISIBLE);
					}
				}
				// 清空全选
				Constants.isCheckedMap.clear();
				searchBtnState = true;
				searchBtn.setText("全选");
				searchAdapter.notifyDataSetChanged();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.i("username", "beforeTextChanged...");
				searchState = 1;// 短信搜索
			}

			public void afterTextChanged(Editable s) {
				Log.i("username", "afterTextChanged...");
			}
		});

		searchET.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					clearnBtn.setVisibility(View.INVISIBLE);
					// searchState = 0;//短信搜索
				} else {
					if (((EditText) v).getText().length() > 0) {
						clearnBtn.setVisibility(View.VISIBLE);
						// searchState = 1;//短信搜索
					}
				}
			}
		});

		clearnBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				searchET.setText("");
				textview.setVisibility(View.GONE);
				addressLV.setVisibility(View.VISIBLE);
				addressLV.setAdapter(multAdapter);
				searchState = 0;// 短信搜索
				Constants.isCheckedMap.clear();
				if (Constants.isCheckedMap.size() > 0) {
					searchBtnState = false;
					searchBtn.setText("取消");
				} else {
					searchBtnState = true;
					searchBtn.setText("全选");
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 获取
			Constants.isCheckedMap.clear();
			onBackPressed();
			overridePendingTransition(R.anim.push_fast, R.anim.push_up);
		} else if (keyCode == KeyEvent.KEYCODE_DEL) {// 点击删除按钮
			Log.d(TAG, "KEYCODE_DEL IS PRESS");
			// 在通讯录中的对话框里点击del键
			if (searchET.getText().toString().length() > 0) {
				List<AddressEntity> searchAddressList = Util.getAddressCursor(
						Address.this, searchET.getText().toString());
				if (searchAddressList.size() > 0) {
					textview.setVisibility(View.GONE);
					addressLV.setVisibility(View.VISIBLE);
					AddressMultAdapter searchAdapter = new AddressMultAdapter(
							Address.this, Util.getArray(searchAddressList));
					addressLV.setAdapter(searchAdapter);
				} else {
					textview.setVisibility(View.VISIBLE);
					addressLV.setVisibility(View.INVISIBLE);
				}
			} else {
				textview.setVisibility(View.GONE);
				addressLV.setVisibility(View.VISIBLE);
				addressLV.setAdapter(Constants.adapter);
			}
			// 清空全选内容
			Constants.isCheckedMap.clear();
			searchBtnState = true;
			searchBtn.setText("全选");
			searchAdapter.notifyDataSetChanged();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
		unregisterReceiver(MyBroadCastReceiver);
	}
}

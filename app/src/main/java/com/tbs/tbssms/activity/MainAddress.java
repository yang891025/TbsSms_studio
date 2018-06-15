package com.tbs.tbssms.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.tbs.tbssms.R;
import com.tbs.tbssms.adapter.AddressAdapter;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.database.ADDRESS_BOX;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.entity.AddressEntity;
import com.tbs.tbssms.util.PinyinComparator;
import com.tbs.tbssms.util.SideBar;
import com.tbs.tbssms.util.Util;

public class MainAddress extends Activity {

	protected static final String TAG = "MainAddress";
	private final Context mContext = this;

	private BroadcastReceiver MyBroadCastReceiver;// 广播

	// address content
	private ListView addressLV;
	private SideBar indexBar;
	private Button addAddressBtn;// 创建新通讯录
	private AddressEntity[] addressEntities;
	private EditText addSearchET;
	private TextView addTextview;

	private WindowManager mWindowManager;
	private TextView mDialogText;

	private int searchState = 0;
	private Intent intent;// intent意图

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_address);
		AppManager.getInstance().addActivity(this);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		init();
		address();
	}

	private void init() {
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);// 退出activity的广播
		filter.addAction(Constants.REFRESH_ADDRESS);// 刷新地址的广播
		filter.addAction(Constants.REFRESH_UPDATE_ADDRESS);// 刷新地址的广播
		filter.addAction(Constants.REFRESH_SMS);// 刷新短信的广播
		filter.addAction(Constants.HTTP_SERVICE_STATE);// http服务的广播
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
					finish();
				} else if (Constants.REFRESH_ADDRESS.equals(intent.getAction())) {
					Log.d(TAG, "REFRESH_ADDRESS...");
					MyAsyncTask task = new MyAsyncTask(mContext);
					task.execute();
				} else if (Constants.REFRESH_UPDATE_ADDRESS.equals(intent
						.getAction())) {
					Constants.adapter.notifyDataSetChanged();
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);
	}

	private void address() {// 通讯录初始化方法

		if (Constants.toastView == null) {
			LayoutInflater inflater = LayoutInflater.from(this);
			Constants.toastView = inflater.inflate(R.layout.address_dialog,
					null);
		}

		mDialogText = (TextView) Constants.toastView
				.findViewById(R.id.textview);// 找到listview
		addSearchET = (EditText) findViewById(R.id.search_bar_et);
		final ImageButton clearnBtn = (ImageButton) findViewById(R.id.search_clear_bt);// 清空输入框
		final Button searchBtn = (Button) findViewById(R.id.search_more_btn);// 搜索按钮
		addTextview = (TextView) findViewById(R.id.textview);

		addressLV = (ListView) findViewById(R.id.listview);// 找到listview
		indexBar = (SideBar) findViewById(R.id.sideBar);// 找到侧边字母滑动栏

		addAddressBtn = (Button) findViewById(R.id.right_btn);

		addressLV.setAdapter(Constants.adapter);

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

		registerForContextMenu(addressLV);

		addressLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (searchState == 2) {
					intent = new Intent();
					intent.putExtra("name", addressEntities[arg2].getName());
					intent.putExtra("phone", addressEntities[arg2].getPhone());
					intent.putExtra("size", 1);
					intent.setClass(MainAddress.this, AddressDetail.class);
					startActivity(intent);
					overridePendingTransition(R.anim.push_in, R.anim.push_out);
				} else {
					intent = new Intent();
					intent.putExtra("name", Constants.arrays[arg2].getName());
					intent.putExtra("phone", Constants.arrays[arg2].getPhone());
					intent.putExtra("size", 1);
					intent.setClass(MainAddress.this, AddressDetail.class);
					startActivity(intent);
					overridePendingTransition(R.anim.push_in, R.anim.push_out);
				}

			}
		});

		// listview长点击监听
		addressLV.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				addressLV.showContextMenu();// show menu
				addressLV.setTag(arg2);// save current position
				return true;
			}
		});

		addAddressBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 设置标题栏右侧按钮的作用
				Intent intent = new Intent(MainAddress.this, AddAddress.class);
				startActivity(intent);
				getParent().overridePendingTransition(R.anim.push_in,
						R.anim.push_out);
			}
		});

		addSearchET.addTextChangedListener(new TextWatcher() {

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.i("username", "beforeTextChanged...");
				searchState = 2;// 短信搜索
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(s)) {
					clearnBtn.setVisibility(View.INVISIBLE);
					addressLV.setAdapter(Constants.adapter);
					searchState = 0;// 短信搜索
				} else {
					clearnBtn.setVisibility(View.VISIBLE);
					List<AddressEntity> searchAddressList = Util
							.getAddressCursor(MainAddress.this, addSearchET
									.getText().toString());
					addressEntities = Util.getArray(searchAddressList);
					if (searchAddressList.size() > 0) {
						addTextview.setVisibility(View.GONE);
						addressLV.setVisibility(View.VISIBLE);
						AddressAdapter searchAdapter = new AddressAdapter(
								MainAddress.this, addressEntities);
						addressLV.setAdapter(searchAdapter);
					} else {
						addTextview.setVisibility(View.VISIBLE);
						addressLV.setVisibility(View.INVISIBLE);
					}
				}
				Log.i("username", "onTextChanged...");
			}

			public void afterTextChanged(Editable s) {
				Log.i("username", "afterTextChanged...");
			}
		});

		addSearchET.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					clearnBtn.setVisibility(View.INVISIBLE);
				} else {
					if (((EditText) v).getText().length() > 0) {
						clearnBtn.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		clearnBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				addSearchET.setText("");
				addTextview.setVisibility(View.GONE);
				addressLV.setVisibility(View.VISIBLE);
				addressLV.setAdapter(Constants.adapter);

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(Constants.toastView, lp);
	}

	// 查询本地数据库
	public void getAdressCursor() {
		// List<AddressEntity> list = new ArrayList<AddressEntity>();
		Constants.list.clear();
		Cursor cursor = new DataBaseUtil(this).getDataBase().query(
				ADDRESS_BOX.TABLE_NAME, null, null, null, null, null, "_id"); // 获得_id属性
		while (cursor.moveToNext()) {
			AddressEntity entity = new AddressEntity();// 实例化实体
			entity.setRaw_contact_id(cursor.getInt(cursor.getColumnIndex("_id")));// 存储id
			entity.setName(cursor.getString(cursor.getColumnIndex("name")));
			entity.setPhone(cursor.getString(cursor
					.getColumnIndex("mobel_number")));
			entity.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			Constants.list.add(entity);
		}
		cursor.close();
	}

	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		if (nameChar[0] > 128) {
			try {
				pinyinName += PinyinHelper.toHanyuPinyinStringArray(
						nameChar[0], defaultFormat)[0].charAt(0);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else {
			pinyinName += nameChar[0];
		}
		return pinyinName.toUpperCase();
	}
	// build context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("操作");
		menu.add(0, R.id.address_update, 0, "编辑");
		menu.add(0, R.id.address_delete, 0, "删除");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.address_delete:
			if (item.getItemId() == R.id.address_delete) {
				int position = (Integer) addressLV.getTag();
				String whereClause = "mobel_number = ?";
				String[] whereArgs = { ""
						+ Constants.arrays[position].getPhone() };
				new DataBaseUtil(this).getDataBase().delete(
						ADDRESS_BOX.TABLE_NAME, whereClause, whereArgs);
				MyAsyncTask task = new MyAsyncTask(mContext);
				task.execute();
			}
			return true;

		case R.id.address_update:
			int position = (Integer) addressLV.getTag();
			intent = new Intent();
			intent.setClass(this, UpdateAddress.class);
			intent.putExtra("name", Constants.arrays[position].getName());
			intent.putExtra("mobel_number",
					Constants.arrays[position].getPhone());
			intent.putExtra("email", Constants.arrays[position].getEmail());
			intent.putExtra("raw_contact_id",
					Constants.arrays[position].getRaw_contact_id());
			intent.putExtra("position", position);
			startActivity(intent);
			return true;

		default:
			// 默认返回值
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWindowManager.removeView(Constants.toastView);
	}

	// 销毁时生命周期
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
		unregisterReceiver(MyBroadCastReceiver);
	}

	// 键盘按钮监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DEL) {// 点击删除按钮
			Log.d(TAG, "KEYCODE_DEL IS PRESS");
			if (addSearchET.getText().toString().length() > 0) {
				List<AddressEntity> searchAddressList = Util.getAddressCursor(
						MainAddress.this, addSearchET.getText().toString());
				if (searchAddressList.size() > 0) {
					addTextview.setVisibility(View.GONE);
					addressLV.setVisibility(View.VISIBLE);
					AddressAdapter searchAdapter = new AddressAdapter(
							MainAddress.this, Util.getArray(searchAddressList));
					addressLV.setAdapter(searchAdapter);
				} else {
					addTextview.setVisibility(View.VISIBLE);
					addressLV.setVisibility(View.INVISIBLE);
				}
			} else {
				addTextview.setVisibility(View.GONE);
				addressLV.setVisibility(View.VISIBLE);
				addressLV.setAdapter(Constants.adapter);
			}
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 获取
			Intent intent = new Intent();
			intent.setClass(MainAddress.this, Exit.class);
			startActivity(intent);
		} else if (keyCode == KeyEvent.KEYCODE_MENU) { // 获取 Menu键
			Intent intent = new Intent();
			intent.setClass(MainAddress.this, Exit.class);
			startActivity(intent);
		}
		return true;
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {

		private static final String TAG = "MyAsyncTask";
		Context context;

		public MyAsyncTask(Context c) {
			this.context = c;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "onPreExecute");
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@SuppressWarnings("unchecked")
		@Override
		protected Integer doInBackground(Void... params) {
			Log.d(TAG, "doInBackground");

			getAdressCursor();
			Constants.arrays = (AddressEntity[]) Constants.list
					.toArray(new AddressEntity[Constants.list.size()]);

			Arrays.sort(Constants.arrays, new PinyinComparator());// 排序(实现了中英文混排)

			Constants.pinyinFirst.clear();
			for (int j = 0; j < Constants.arrays.length; j++) {
				String name = converterToFirstSpell(Constants.arrays[j]
						.getName());
				Constants.pinyinFirst.add(name);
			}

			Constants.adapter = new AddressAdapter(context, Constants.arrays);
			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(Integer integer) {
			Log.d(TAG, "onPostExecute");
			addressLV.setAdapter(Constants.adapter);
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.d(TAG, "onProgressUpdate");
		}
	}
}

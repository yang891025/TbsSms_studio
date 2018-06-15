package com.tbs.tbssms.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tbs.tbssms.R;
import com.tbs.tbssms.adapter.MessageAdapter;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.database.MESSAGE_BOX;
import com.tbs.tbssms.entity.MessageEntity;
import com.tbs.tbssms.util.Util;

public class MainSms extends Activity {

	protected static final String TAG = "MainSms";
	private final Context mContext = this;
	private BroadcastReceiver MyBroadCastReceiver;//广播
	
	private EditText msgSearchET;
	private TextView msgTextview;
	private List<MessageEntity> searchMsgList;
	private ImageButton newMsgBtn;//创建新短信按钮
	private ListView smsLV;//短信的listview
	private int searchState = 0;
	private Intent intent;//intent意图
	private MessageAdapter smsAdapter;
	private List<MessageEntity> msgList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_weixin);
		AppManager.getInstance().addActivity(this);
		init();
		message();
	}

	private void init() {
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);// 退出activity的广播
		filter.addAction(Constants.REFRESH_SMS);// 刷新短信的广播
		filter.addAction(Constants.HTTP_SERVICE_STATE);// http服务的广播
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
					finish();
				}
				if (Constants.REFRESH_SMS.equals(intent.getAction())) {
					Log.d(TAG, "REFRESH_SMS...");
					MyAsyncTask task = new MyAsyncTask(mContext);
					task.execute();
					
					//smsAdapter = new MessageAdapter(mContext,getMessageCursor());// 查询数据库中短信息分组
					//smsLV.setAdapter(smsAdapter);
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);
	}
	
	private void message(){//短信始化方法
		newMsgBtn = (ImageButton) findViewById(R.id.right_btn);
		smsLV = (ListView) findViewById(R.id.listview);
		msgSearchET = (EditText) findViewById(R.id.search_bar_et);
		final ImageButton clearnBtn = (ImageButton) findViewById(R.id.search_clear_bt);//清空输入框
		final Button searchBtn = (Button) findViewById(R.id.search_more_btn);//搜索按钮
		msgTextview = (TextView) findViewById(R.id.textview);
		
		MyAsyncTask task = new MyAsyncTask(mContext);
		task.execute();
		
		registerForContextMenu(smsLV);
		
		smsLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				if(searchState == 1){
					intent = new Intent();
					intent.setClass(mContext, ChatMsg.class);
					intent.putExtra("name", searchMsgList.get(arg2).getReceiveName());
					intent.putExtra("phone", searchMsgList.get(arg2).getReceivePhone());
					intent.putExtra("medium", msgList.get(arg2).getData1());
					startActivity(intent);
				}else{
					intent = new Intent();
					intent.setClass(mContext, ChatMsg.class);
					intent.putExtra("name", msgList.get(arg2).getReceiveName());
					intent.putExtra("phone", msgList.get(arg2).getReceivePhone());
					intent.putExtra("medium", msgList.get(arg2).getData1());
					startActivity(intent);
				}
				overridePendingTransition(R.anim.push_in, R.anim.push_out);
			}
		});
		
		// listview长点击监听
		smsLV.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				smsLV.showContextMenu();// show menu
				smsLV.setTag(R.id.position,arg2);// save current position
				if(searchState == 1){
					smsLV.setTag(R.id.phone,searchMsgList.get(arg2).getReceivePhone());
				}else{
					smsLV.setTag(R.id.phone, msgList.get(arg2).getReceivePhone());
				}
				return true;
			}
		});
		
		newMsgBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 短信界面设置标题栏右侧按钮的作用
				Intent intent = new Intent(MainSms.this, Address.class);
				startActivity(intent);
				getParent().overridePendingTransition(R.anim.push_down, R.anim.push_slow);
			}
		});
		
		msgSearchET.addTextChangedListener(new TextWatcher() {
			
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				Log.i("username", "beforeTextChanged...");
				searchState = 1;//短信搜索
			}

			public void onTextChanged(CharSequence s, int start, int before,int count) {
				if (TextUtils.isEmpty(s)) {
					clearnBtn.setVisibility(View.INVISIBLE);
					smsLV.setAdapter(smsAdapter);
					searchState = 0;//短信搜索
				} else {
					clearnBtn.setVisibility(View.VISIBLE);
					searchMsgList = Util.getMessageCursor(MainSms.this, msgSearchET.getText().toString());
					if(searchMsgList.size()>0){
						msgTextview.setVisibility(View.GONE);
						smsLV.setVisibility(View.VISIBLE);
						MessageAdapter smsAdapter = new MessageAdapter(MainSms.this, searchMsgList);//查询数据库中短信息分组
						smsLV.setAdapter(smsAdapter);
					}else{
						msgTextview.setVisibility(View.VISIBLE);
						smsLV.setVisibility(View.INVISIBLE);
					}
				}
				Log.i("username", "onTextChanged...");
			}

			public void afterTextChanged(Editable s) {
				Log.i("username", "afterTextChanged...");
			}
		});

		msgSearchET.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					clearnBtn.setVisibility(View.INVISIBLE);
//					searchState = 0;//短信搜索
				} else {
					if (((EditText) v).getText().length() > 0) {
						clearnBtn.setVisibility(View.VISIBLE);
//						searchState = 1;//短信搜索
					}
				}
			}
		});

		clearnBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				msgSearchET.setText("");
				msgTextview.setVisibility(View.GONE);
				smsLV.setVisibility(View.VISIBLE);
				smsLV.setAdapter(smsAdapter);
				searchState = 0;//短信搜索
			}
		});
	}
	
	
	// build context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("操作");
		menu.add(0, R.id.add_address, 0, "添加到通讯录");
		menu.add(0, R.id.sms_delete, 0, "删除消息");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_address:
			String phone = (String) smsLV.getTag(R.id.phone);
			intent = new Intent(mContext, AddAddress.class);
			intent.putExtra("phone", phone);
			startActivity(intent);
			return false;
			
		case R.id.sms_delete:
			int position = (Integer) smsLV.getTag(R.id.position);
			int index = msgList.get(position).getId();
			String whereClause = "receivePhone = ? and receiveName = ?";
			String[] whereArgs = { msgList.get(position).getReceivePhone(), msgList.get(position).getReceiveName() };
			int resoult = new DataBaseUtil(this).getDataBase().delete(MESSAGE_BOX.TABLE_NAME, whereClause, whereArgs);
			if (resoult > 0) {
				intent = new Intent(Constants.REFRESH_SMS);
				sendBroadcast(intent);
			}
			return false;

		case 1:
			return true;

		case 2:
			return false;

		default:
			// 默认返回值
			return super.onContextItemSelected(item);
		}
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
		@Override
		protected Integer doInBackground(Void... params) {
			Log.d(TAG, "doInBackground");
			msgList = getMessageCursor();
			smsAdapter = new MessageAdapter(mContext, msgList);//查询数据库中短信息分组
			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(Integer integer) {
			smsLV.setAdapter(smsAdapter);
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.d(TAG, "onProgressUpdate");
		}
	}
	
	public List<MessageEntity> getMessageCursor(){
		List<MessageEntity> msgList = new ArrayList<MessageEntity>();
		String sql = "select * from MessageBox  where _id in(select max(_id) from MessageBox group by receivePhone )";
		Cursor cursor = new DataBaseUtil(this).getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			MessageEntity entity = new MessageEntity();//实例化实体
			entity.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			entity.setMessage(cursor.getString(cursor.getColumnIndex("message")));
			entity.setMsgState(cursor.getString(cursor.getColumnIndex("msgState")));
			entity.setReceiveName(cursor.getString(cursor.getColumnIndex("receiveName")));
			entity.setReceivePhone(cursor.getString(cursor.getColumnIndex("receivePhone")));
			entity.setRetysTimes(cursor.getString(cursor.getColumnIndex("retysTimes")));
			entity.setSendState(cursor.getString(cursor.getColumnIndex("sendState")));
			entity.setSendUser(cursor.getString(cursor.getColumnIndex("sendUser")));
			entity.setTime(cursor.getString(cursor.getColumnIndex("time")));
			msgList.add(entity);
		}
		cursor.close();
		return msgList;
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
			if (msgSearchET.getText().toString().length() > 0) {
				List<MessageEntity> searchMsgList = Util.getMessageCursor(MainSms.this, msgSearchET.getText().toString());
				if (searchMsgList.size() > 0) {
					msgTextview.setVisibility(View.GONE);
					smsLV.setVisibility(View.VISIBLE);
					MessageAdapter smsAdapter = new MessageAdapter(MainSms.this, searchMsgList);// 查询数据库中短信息分组
					smsLV.setAdapter(smsAdapter);
				} else {
					msgTextview.setVisibility(View.VISIBLE);
					smsLV.setVisibility(View.INVISIBLE);
				}
			} else {
				msgTextview.setVisibility(View.GONE);
				smsLV.setVisibility(View.VISIBLE);
				smsLV.setAdapter(smsAdapter);
			}
		}
		//点击back按钮返回到桌面
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.setClass(MainSms.this, Exit.class);
			startActivity(intent);
		} else if (keyCode == KeyEvent.KEYCODE_MENU) { // 获取 Menu键
			Intent intent = new Intent();
			intent.setClass(MainSms.this, Exit.class);
			startActivity(intent);
		}
		return true;
	}
}

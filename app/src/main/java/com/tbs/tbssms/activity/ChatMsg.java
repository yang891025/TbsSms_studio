package com.tbs.tbssms.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.tbs.tbssms.R;
import com.tbs.tbssms.adapter.ChatMsgViewAdapter;
import com.tbs.tbssms.callback.HttpClientCallBack;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.database.MESSAGE_BOX;
import com.tbs.tbssms.database.SEND_BUFFER;
import com.tbs.tbssms.entity.MessageEntity;
import com.tbs.tbssms.receiver.ChatSmsSuccessReceiver;
import com.tbs.tbssms.util.HttpConnectionUtil;
import com.tbs.tbssms.util.HttpConnectionUtil.HttpMethod;
import com.tbs.tbssms.util.Util;

public class ChatMsg extends Activity implements OnClickListener {

	private static final String TAG = "ChatMsg";
	private BroadcastReceiver MyBroadCastReceiver;
	private Button backBtn;
	private Button sendBtn;
	private EditText msgEdit;
	private TextView title;
	private ListView listview;
	private Intent intent;
	private String name;
	private String phone;
	private String date;
	private String medium;
	private int size;
	private ArrayList<Integer> positionList;
	// private Button mRightBtn;
	private String msg;
	private ChatMsgViewAdapter adapter;
	private NotificationManager mNotificationManager;
	private HttpConnectionUtil connection;
	private PopupWindow window;// popwindow
	private boolean isOpenPop = false;// popwindow state
	private int searchState = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
		
		// get intent value
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			name = intent.getStringExtra("name");
			phone = intent.getStringExtra("phone");
			size = intent.getIntExtra("size", 0);
			medium = intent.getStringExtra("medium");
			positionList = intent.getIntegerArrayListExtra("positionList");
			searchState = intent.getIntExtra("searchState",0);
		}
		
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);//获取系统服务
		mNotificationManager.cancel(8888);
		
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);
		filter.addAction(Constants.REFRESH_SMS_DETAIL);
		filter.addAction(Constants.SENT_SMS_ACTION);
		filter.addAction(Constants.DELIVERED_SMS_ACTION);
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
					finish();
				}
				if (Constants.REFRESH_SMS_DETAIL.equals(intent.getAction())) {
					Log.d(TAG, "REFRESH_SMS_DETAIL");
					//接收新短信后刷新界面
					adapter.notifyDataSetChanged();//刷新适配器
					msgEdit.setText("");//情况对话框
					listview.setSelection(listview.getCount() - 1);
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);
		
		if(size > 1){
			Constants.msgDetailList = new ArrayList<MessageEntity>();
			adapter = new ChatMsgViewAdapter(this,this,Constants.msgDetailList);//实例化适配器
		}else{
			Constants.msgDetailList = getMessageCursor(name,phone);//查询数据
			adapter = new ChatMsgViewAdapter(this,this,Constants.msgDetailList);//实例化适配器
		}
		
		backBtn = (Button) findViewById(R.id.backBtn);
		sendBtn = (Button) findViewById(R.id.sendBtn);
		msgEdit = (EditText) findViewById(R.id.contentEdit);
		title = (TextView) findViewById(R.id.title);
		listview = (ListView) findViewById(R.id.listview);
		backBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);
		
		if(size > 1){
			StringBuffer sb = new StringBuffer();
			if(searchState == 0){
				for (int i = 0; i < positionList.size(); i++) {
					sb.append(Constants.arrays[positionList.get(i)].getName());
					if(i < positionList.size()-1){
						sb.append(",");
					}
				}
				title.setText(sb.toString());
			}
			if(searchState == 1){
				for (int i = 0; i < positionList.size(); i++) {
					sb.append(Constants.searchArrays[positionList.get(i)].getName());
					if(i < positionList.size()-1){
						sb.append(",");
					}
				}
				title.setText(sb.toString());
			}
		}else{
			title.setText(name);
		}
		
		File webappFile = new File(Constants.SAVE_INFORMATIONPATH);
		if ((webappFile).exists() == true) {
			SharedPreferences setting = getSharedPreferences(Constants.SAVE_INFORMATION,MODE_PRIVATE);//实例化sharepreferences
			msgEdit.setText(setting.getString(phone, ""));//设置对话框中内容
		}
		listview.setAdapter(adapter);//设置设配器
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
		unregisterReceiver(MyBroadCastReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			if(size <= 1){
				saveMsg();
			}
			finish();
			overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
			break;

		case R.id.sendBtn:
			Util.closeInput(msgEdit, this);
			sendMsg(v);
			break;

		default:
			break;
		}
	}
	
	public void sendMsg(View v){//发送短信
		msg = msgEdit.getText().toString();
		
		MyAsyncTask task = new MyAsyncTask(getApplicationContext());
		task.execute();
		//弹出poputwindow
		//changPopState(v);
	}
	
	public void saveMsg(){//保存短信
		msg = msgEdit.getText().toString();
		if(phone.trim() != null && !phone.trim().equals("")){
			// 获得编辑器
			SharedPreferences.Editor editor = getSharedPreferences(Constants.SAVE_INFORMATION, MODE_PRIVATE).edit();
			// 将EditText文本内容添加到编辑器
			editor.putString(phone, msg);
			// 提交编辑器内容
			editor.commit();
		}
	}
	
	//查询数据
	public List<MessageEntity> getMessageCursor(String name , String phone){
		List<MessageEntity> msgDetailList = new ArrayList<MessageEntity>();
		String selection = "receivePhone = ? and receiveName = ? and msgState <> ?";
		String[] selectionArgs = {phone,name,"2"};
		String orderBy = "_id collate NOCASE asc";
		Cursor cursor = new DataBaseUtil(this).getDataBase().query(MESSAGE_BOX.TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
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
			msgDetailList.add(entity);
		}
		cursor.close();
		return msgDetailList;
	}
	
	
	private int getPopHeight(View view) {
		int[] location = new int[2];// get view location
		view.getLocationOnScreen(location);
		return view.getHeight();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			if(size <= 1){
				saveMsg();
			}
			finish();
			overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
		}
		return true;
	}
	
	
	// popurwindow
	public void changPopState(View v) {

		isOpenPop = !isOpenPop;
		if (isOpenPop) {
			popAwindow(v);
		} else {
			if (window != null) {
				window.dismiss();
			}
		}
	}

	private void popAwindow(View parent) {
		// 按钮的布局
		Button sharetencent,sharexinlang,sharepengyou,shareweixin,sharemessage;
		// 布局填充器实例化
		View view = getLayoutInflater().inflate(R.layout.share_window_layout, null);
		// find view
		sharemessage = (Button) view.findViewById(R.id.share_message);
		
		sharemessage.setOnClickListener(new OnClickListener() {//发送短信
			
			@Override
			public void onClick(View v) {
				MyAsyncTask task = new MyAsyncTask(getApplicationContext());
				task.execute();
				window.dismiss();
			}
		});

		if (window == null) {
			window = new PopupWindow(view,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
		}
		window.setAnimationStyle(0);
		window.setFocusable(true);
		window.setOutsideTouchable(false);
		window.setBackgroundDrawable(new BitmapDrawable());
		window.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				isOpenPop = false;
			}
		});
		window.update();
		window.showAtLocation(parent, Gravity.BOTTOM | Gravity.RIGHT, sendBtn.getWidth()/2,getPopHeight(sendBtn));
	}
	
	
	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {

		private static final String TAG = "MyAsyncTask";
		private Context context;
		private boolean sendState = false;
		private long resoultId;//返回的id

		public MyAsyncTask(Context c) {
			this.context = c;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "onPreExecute");
			sendBtn.setEnabled(false);
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 /**
         * 参数说明
         * destinationAddress:收信人的手机号码
         * scAddress:发信人的手机号码 
         * text:发送信息的内容 
         * sentIntent:发送是否成功的回执，用于监听短信是否发送成功。
         * DeliveryIntent:接收是否成功的回执，用于监听短信对方是否接收成功。
         */
		@Override
		protected Integer doInBackground(Void... params) {
			Log.d(TAG, "doInBackground");
			if(size>1){
				for (int i = 0; i < positionList.size(); i++) {
					if(searchState == 0){
						phone = Constants.arrays[positionList.get(i)].getPhone();
						name = Constants.arrays[positionList.get(i)].getName();
					}else if(searchState == 1){
						phone = Constants.searchArrays[positionList.get(i)].getPhone();
						name = Constants.searchArrays[positionList.get(i)].getName();
					}
					if(phone.trim() != null && !phone.trim().equals("") && msg != null && !msg.equals("")){
						date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//获取去当前系统时间
						//将内容保存到数据库
						ContentValues values= new ContentValues();
						values.put("receivePhone", phone.trim());
						values.put("receiveName", name.trim());
						values.put("message", msg);
						values.put("time", date);
						values.put("sendState", "1");
						values.put("msgState", "0");
						values.put("sendUser", "我");
						values.put("data1", "短信");
						
						//执行插入语句
						resoultId = new DataBaseUtil(ChatMsg.this).getDataBase().insert(MESSAGE_BOX.TABLE_NAME, null, values);
						
						MessageEntity entity = new MessageEntity();// 实例化实体，传入参数
						entity.setId((int) resoultId);
						entity.setMessage(msg);
						entity.setMsgState("0");
						entity.setReceiveName(name.trim());
						entity.setReceivePhone(phone.trim());
						entity.setSendState("1");
						entity.setSendUser("我");
						entity.setTime(date);
						entity.setData1("短信");
						
						Constants.msgDetailList.add(entity);// 相机和中增加内容
						Constants.saveMsgPosition.put(""+resoultId, Constants.msgDetailList.size()-1);
						
						if(resoultId != -1){
							SmsManager smsManager = SmsManager.getDefault();//获得sms管理者
							
							String action = Constants.SENT_SMS_ACTION+resoultId+","+0;
							String action2 = Constants.DELIVERED_SMS_ACTION+resoultId+","+0;
							
							ChatSmsSuccessReceiver receiver = new ChatSmsSuccessReceiver();
							IntentFilter filter = new IntentFilter();  
			                filter.addAction(action);
			                filter.addAction(action2);
			                filter.setPriority(1000);
			                context.registerReceiver(receiver, filter);
							
							// create the sentIntent parameter
							Intent sentIntent = new Intent(action);
							PendingIntent sentPI = PendingIntent.getBroadcast(ChatMsg.this, 0, sentIntent,0);

							// create the deilverIntent parameter
							Intent deliverIntent = new Intent(action2);
							PendingIntent deliverPI = PendingIntent.getBroadcast(ChatMsg.this, 0,deliverIntent, 0);
							
							if (msg.length() > 70) {//判断短信长度是否大于70个字，如果大于70分多条短信发送
								List<String> contents = smsManager.divideMessage(msg);//截取短信息
								for (String sms : contents) {//循环发送短信
									smsManager.sendTextMessage(phone.trim(), null, sms, sentPI, deliverPI);//发送短信
								}
							} else {
								smsManager.sendTextMessage(phone.trim(), null, msg, sentPI, deliverPI);//发送一条短信
							}
							sendState = true;
						}else{
							sendState = false;
						}
					}
				}
			}else{
				if(phone.trim() != null && !phone.trim().equals("") && msg != null && !msg.equals("")){
					date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//获取去当前系统时间
					
					//将内容保存到数据库
					ContentValues values= new ContentValues();
					values.put("receivePhone", phone.trim());
					values.put("receiveName", name.trim());
					values.put("message", msg);
					values.put("time", date);
					values.put("sendState", "1");
					values.put("msgState", "0");
					values.put("sendUser", "我");
					values.put("data1", "短信");
					//执行插入语句
					resoultId = new DataBaseUtil(ChatMsg.this).getDataBase().insert(MESSAGE_BOX.TABLE_NAME, null, values);
					
					MessageEntity entity = new MessageEntity();// 实例化实体，传入参数
					entity.setId((int) resoultId);
					entity.setMessage(msg);
					entity.setMsgState("0");
					entity.setReceiveName(name.trim());
					entity.setReceivePhone(phone.trim());
					entity.setSendState("1");
					entity.setSendUser("我");
					entity.setTime(date);
					entity.setData1("短信");
					
					Constants.msgDetailList.add(entity);// 相机和中增加内容
					Constants.saveMsgPosition.put(""+resoultId, Constants.msgDetailList.size()-1);
					
					if(resoultId > 0){
						SmsManager smsManager = SmsManager.getDefault();//获得sms管理者
						
						String action = Constants.SENT_SMS_ACTION+resoultId+","+0;
						String action2 = Constants.DELIVERED_SMS_ACTION+resoultId+","+0;
						
						ChatSmsSuccessReceiver receiver = new ChatSmsSuccessReceiver();
						IntentFilter filter = new IntentFilter();  
		                filter.addAction(action);
		                filter.addAction(action2);
		                filter.setPriority(1000);
		                context.registerReceiver(receiver, filter);
						
						// create the sentIntent parameter
						Intent sentIntent = new Intent(action);
						PendingIntent sentPI = PendingIntent.getBroadcast(ChatMsg.this, 0, sentIntent,0);

						// create the deilverIntent parameter
						Intent deliverIntent = new Intent(action2);
						PendingIntent deliverPI = PendingIntent.getBroadcast(ChatMsg.this, 0,deliverIntent, 0);
						
						if (msg.length() > 70) {//判断短信长度是否大于70个字，如果大于70分多条短信发送
							List<String> contents = smsManager.divideMessage(msg);//截取短信息
							for (String sms : contents) {//循环发送短信
								smsManager.sendTextMessage(phone.trim(), null, sms, sentPI, deliverPI);//发送短信
							}
						} else {
							smsManager.sendTextMessage(phone.trim(), null, msg, sentPI, deliverPI);//发送一条短信
						}
						sendState = true;
					}else{
						sendState = false;
					}
					if (!sendState) {
						saveMsg();// 保存当前对话框内内容
					}
				}
			}
			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(Integer integer) {
			Log.d(TAG, "onPostExecute");
			
			//if (sendState) {
				//Toast.makeText(ChatMsg.this, "正在发送", Toast.LENGTH_LONG).show();// 提示信息
			//} else {
				//Toast.makeText(ChatMsg.this, "发送失败", Toast.LENGTH_LONG).show();// 提示信息
			//}
			
			sendBtn.setEnabled(true);
			
			Intent smsIntent = new Intent(Constants.REFRESH_SMS);
			sendBroadcast(smsIntent);
			
			Intent smsDetailIntent = new Intent(Constants.REFRESH_SMS_DETAIL);
			context.sendBroadcast(smsDetailIntent);
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.d(TAG, "onProgressUpdate");
		}
	}
}

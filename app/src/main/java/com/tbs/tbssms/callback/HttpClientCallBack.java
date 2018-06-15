package com.tbs.tbssms.callback;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract.Data;
import android.telephony.SmsManager;
import android.util.Log;

import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.dao.Dao;
import com.tbs.tbssms.database.ADDRESS_BOX;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.database.MESSAGE_BOX;
import com.tbs.tbssms.database.SEND_BUFFER;
import com.tbs.tbssms.entity.AddressEntity;
import com.tbs.tbssms.myinterface.HttpCallback;
import com.tbs.tbssms.myinterface.HttpConnectionCallback;
import com.tbs.tbssms.receiver.ChatSmsSuccessReceiver;
import com.tbs.tbssms.xmlparser.TxtImport;

public class HttpClientCallBack extends HttpCallback {

	private static final String TAG = "HttpClientCallBack";

	private boolean state = false;
	private Context mContext = null;
	private String json = null;
	private String sessionId = null;
	private boolean result = false;
	private String type = null;
	private String notifyResult = "";

	@Override
	public void execute(String response) {

	}

	@Override
	public void execute(String response, String sessionId) {

	}
	
	@Override
	public void execute(String response, String sessionId, Context context) {
		
	}

	@Override
	public void execute(String response, String sessionId, Context context, String type) {
		Log.d(TAG, "response:"+response);
		this.mContext = context;
		this.json = response;
		this.sessionId = sessionId;
		this.type = type;
		
		if (response != null && response.equals("login_success")) {
			Log.d(TAG, "login_success");
			Constants.LoginState = true;
			Constants.SessionId = sessionId;
		}
		if (response != null && response.equals("login_fail")) {
			Log.d(TAG, "login_fail");
			Constants.LoginState = false;
		}
		if (response != null && response.equals("no_login")) {
			Log.d(TAG, "no_login");
			Constants.LoginState = false;
		}
		if (response != null && response.length() > 13) {
			Log.d(TAG, "Save resoult");
			execute();
		}
		if (response != null && response.equals("success")) {
			Log.d(TAG, "success");
		}
		if (response != null && response.equals("wrong")) {
			Log.d(TAG, "wrong");
		}
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
		BufferedReader bf = new BufferedReader(new StringReader(json));
		TxtImport importTest = new TxtImport(mContext);
		importTest.setScanParams("=:", "\r\n", 3, "\r\n");// 设置分隔符号等
		importTest.changeTable(SEND_BUFFER.TABLE_NAME);// 设定导入导入的数据库表
		importTest.setColumnAlias("receivePhone", "#收信人电话");// 根据需要设定表中各字段的别名
		importTest.setColumnAlias("receiveName", "#收信人名称");
		importTest.setColumnAlias("sendName", "#发信人名称");
		importTest.setColumnAlias("message", "#信息内容");
		importTest.setColumnAlias("autoId", "#反馈ID");
		importTest.setColumnAlias("retysTimes", "#重试次数");
		importTest.setColumnAlias("data1", "#发送媒介");
		int count = importTest.importFromTxt(bf);// 导入数据库的数量
		Log.d(TAG, "count : " + count);
		if (count > 0) {
			Cursor cursor = new DataBaseUtil(mContext).getDataBase().query(SEND_BUFFER.TABLE_NAME, null, null, null, null, null, "_id");
			if (cursor != null) {
				while (cursor.moveToNext()) {
					String phone = cursor.getString(cursor.getColumnIndex("receivePhone"));
					String name = cursor.getString(cursor.getColumnIndex("receiveName"));
					String msg = cursor.getString(cursor.getColumnIndex("message"));
					String sendName = cursor.getString(cursor.getColumnIndex("sendName"));
					String medium = cursor.getString(cursor.getColumnIndex("data1"));
					String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());// 获取去当前系统时间
					int returnBufferId = cursor.getInt(cursor.getColumnIndex("_id"));
					
					if(type!= null && type.equals(name)){
						AddressEntity address = new AddressEntity();
						address.setName(name);
						address.setPhone(phone);
						Dao.getInstance(mContext).UpdateAddressInfo(address);
					}
					
					// 将内容保存到数据库
					ContentValues values = new ContentValues();
					values.put("receivePhone", phone.trim());
					values.put("receiveName", name.trim());
					values.put("message", msg);
					values.put("time", date);
					values.put("sendState", "1");
					values.put("msgState", "0");
					values.put("sendUser", sendName.trim());
					values.put("data1", medium);
					// 执行插入语句
					long resoultId = new DataBaseUtil(mContext).getDataBase().insert(MESSAGE_BOX.TABLE_NAME, null, values);
					if (resoultId > 0) {
						if (medium != null && medium.equals("1")) {
							SmsManager smsManager = SmsManager.getDefault();// 获得sms管理者
							String action = Constants.SENT_SMS_ACTION + resoultId + "," + returnBufferId;
							String action2 = Constants.DELIVERED_SMS_ACTION + resoultId + "," + returnBufferId;
							
							ChatSmsSuccessReceiver receiver = new ChatSmsSuccessReceiver();
							IntentFilter filter = new IntentFilter();
							filter.addAction(action);
							filter.addAction(action2);
							filter.setPriority(Integer.MAX_VALUE);
							mContext.registerReceiver(receiver, filter);

							// create the sentIntent parameter
							Intent sentIntent = new Intent(action);
							PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0, sentIntent, 0);

							// create the deilverIntent parameter
							Intent deliverIntent = new Intent(action2);
							PendingIntent deliverPI = PendingIntent.getBroadcast(mContext, 0, deliverIntent, 0);

							if (msg.length() > 70) {// 判断短信长度是否大于70个字，如果大于70分多条短信发送
								List<String> contents = smsManager.divideMessage(msg);// 截取短信息
								for (String sms : contents) {// 循环发送短信
									smsManager.sendTextMessage(phone.trim(), null, sms, sentPI, deliverPI);// 发送短信
								}
							} else {
								smsManager.sendTextMessage(phone.trim(), null, msg, sentPI, deliverPI);// 发送一条短信
							}
						}
						// 发送刷新短信细览界面广播
						Intent smsDetailIntent = new Intent(Constants.REFRESH_SMS_DETAIL);
						mContext.sendBroadcast(smsDetailIntent);

						// Constants.resoultId = resoultId;//插入数据后返回的ID

						// 发送刷新短信概览界面广播
						Intent smsIntent = new Intent(Constants.REFRESH_SMS);
						mContext.sendBroadcast(smsIntent);
					}
				}
			}
			cursor.close();
		}
		return null;
	}

	// 运行在ui线程中，在doInBackground()执行完毕后执行
	@Override
	protected void onPostExecute(Integer integer) {
		Log.d(TAG, "onPostExecute");
	}

	// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
	@Override
	protected void onProgressUpdate(Integer... values) {
		Log.d(TAG, "onProgressUpdate");
	}

}

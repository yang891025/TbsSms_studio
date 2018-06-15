package com.tbs.tbssms.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.callback.HttpClientCallBack;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.dao.Dao;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.database.MESSAGE_BOX;
import com.tbs.tbssms.database.MESSAGE_STATISTICS_BOX;
import com.tbs.tbssms.database.SEND_BUFFER;
import com.tbs.tbssms.entity.MessageEntity;
import com.tbs.tbssms.util.HttpConnectionUtil;
import com.tbs.tbssms.util.HttpConnectionUtil.HttpMethod;

public class ChatSmsSuccessReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSSuccessReceiver";
	private Intent intent;
	private String autoId = null;// 返回给服务器的id
	private String resoultId = "";// 添加到短信数据表中
	private String returnBufferId = "";// 删除缓冲表中数据
	private String resoultId2 = "";// 添加到短信数据表中
	private String returnBufferId2 = "";// 删除缓冲表中数据

	private ContentValues values;
	private int resoult;
	private int delResoult;
	private IniFile m_iniFileIO = null;
	private Dao dbUtil = null;
	private HttpConnectionUtil connection;

	private String username;
	private String password;
	private String url;
	private Intent smsDetailIntent;

	public ChatSmsSuccessReceiver() {
		connection = HttpConnectionUtil.newInstance();
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		m_iniFileIO = new IniFile();
		dbUtil = Dao.getInstance(context);
		// 读取ini
		username = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.LOGININFO, Constants.USERNAME, "", (byte) 0);
		password = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.LOGININFO, Constants.PASSWORD, "", (byte) 0);
		String ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.LOGININFO, Constants.LOGINURL, "", (byte) 0);
		String port = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.LOGININFO, Constants.LOGINPORT, "", (byte) 0);
		url = ip+":"+port;
		if(url!=null && !url.equals("")){
			if(url.indexOf("http://")>-1){
				
			}else{
				url = "http://"+url;
			}
		}
		
		if (intent.getAction().startsWith(Constants.SENT_SMS_ACTION)) {
			Log.d(TAG, "SENT_SMS_ACTION");
			
			String[] resoultValue = intent.getAction().substring(Constants.SENT_SMS_ACTION.length(), intent.getAction().length()).split(",");
			resoultId = resoultValue[0];
			returnBufferId = resoultValue[1];
			
			autoId = dbUtil.detailSendBufferId(SEND_BUFFER.TABLE_NAME, "_id = ?", new String[] { "" + returnBufferId });
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "resState");
			params.put("client", "SMS");
			
			switch (getResultCode()) {
			case Activity.RESULT_OK:// 发送短信成功
				Log.d(TAG, "SENT_SMS_ACTION RESULT_OK");
				values = new ContentValues();
				values.put("sendState", "2");
				resoult = dbUtil.updateInfo(MESSAGE_BOX.TABLE_NAME, values, "_id = ?", new String[] { "" + resoultId });
				if(Constants.saveMsgPosition.size()>0){
					Log.d(TAG, "sms position :" + Constants.saveMsgPosition.get(""+resoultId));
					int successPosition = Constants.saveMsgPosition.get(""+resoultId);
					Constants.msgDetailList.get(successPosition).setSendState("2");
				}
				if (Constants.HttpServerState) {
					params.put("success", "" + autoId);
					connection.asyncConnect(url + "/Message", params, HttpMethod.POST, new HttpClientCallBack(), context);
				}
				delResoult = dbUtil.deletInfo(SEND_BUFFER.TABLE_NAME, "_id = ?", new String[] { "" + returnBufferId });
				// 这里处理短信记录内容
				values.clear();
				String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());// 获取去当前系统时间
				int sendCount = dbUtil.detailSendCount(MESSAGE_STATISTICS_BOX.TABLE_NAME, "time = ?", new String[] { date });
				if (sendCount > 0) {
					values.put("sendCount", sendCount);
					dbUtil.updateInfo(MESSAGE_STATISTICS_BOX.TABLE_NAME, values, "time = ?", new String[] { date });
				} else {
					values.put("time", date);
					values.put("sendCount", 1);
					dbUtil.insertBuffer(values, MESSAGE_STATISTICS_BOX.TABLE_NAME);
				}
				Constants.saveMsgPosition.remove(resoultId);
				smsDetailIntent = new Intent(Constants.REFRESH_SMS_DETAIL);
				context.sendBroadcast(smsDetailIntent);
				//Toast.makeText(context, "短信发送成功", Toast.LENGTH_LONG).show();
				break;

			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:// 发送短信失败
				Log.d(TAG, "SENT_SMS_ACTION RESULT_ERROR_GENERIC_FAILURE");
				
				// 短信失败后的操作
				values = new ContentValues();
				values.put("sendState", "3");
				resoult = new DataBaseUtil(context).getDataBase().update(MESSAGE_BOX.TABLE_NAME, values, "_id = ?", new String[] { "" + resoultId });
				if(Constants.saveMsgPosition.size()>0){
					int successPosition = Constants.saveMsgPosition.get(""+resoultId);
					Constants.msgDetailList.get(successPosition).setSendState("3");
				}
				if (resoult > 0) {
					if (Constants.HttpServerState) {
						params.put("fail", "" + autoId);
						connection.asyncConnect(url + "/Message", params, HttpMethod.POST, new HttpClientCallBack(), context);
					}
					// 发送失败不清楚数据库。可以直接进入下轮短信发送再次发送,需要在数据库增加重发次数，在此添加重发功能
					dbUtil.deletInfo(SEND_BUFFER.TABLE_NAME, "_id = ?", new String[] { "" + returnBufferId });
				}
				Constants.saveMsgPosition.remove(resoultId);
				
				smsDetailIntent = new Intent(Constants.REFRESH_SMS_DETAIL);
				context.sendBroadcast(smsDetailIntent);
				
				//Toast.makeText(context, "短信发送失败", Toast.LENGTH_LONG).show();
				break;

			case SmsManager.RESULT_ERROR_RADIO_OFF:
				break;

			case SmsManager.RESULT_ERROR_NULL_PDU:
				break;
			}
		} else if (intent.getAction().startsWith(Constants.DELIVERED_SMS_ACTION)) {// 短信送达后的广播
			//Log.d(TAG, "已经送达了！！！！！！！！！！");

			//String[] resoultValue = intent.getAction().substring(Constants.DELIVERED_SMS_ACTION.length(), intent.getAction().length()).split(",");
			//resoultId2 = resoultValue[0];
			//returnBufferId2 = resoultValue[1];
			//Log.d(TAG, "DELIVERED_SMS_ACTION");
			
			//Log.d(TAG, "resoultId :" + resoultId);
			//Log.d(TAG, "returnBufferId :" + returnBufferId);
			
			switch (getResultCode()) {
			case Activity.RESULT_OK:// 短信已送达
				// 短信送达后的操作
				//values = new ContentValues();
				//values.put("sendState", "4");
				//resoult = new DataBaseUtil(context).getDataBase().update(MESSAGE_BOX.TABLE_NAME, values, "_id = ?", new String[] { "" + resoultId2 });
				//if(Constants.saveMsgPosition.size()>0){
					//int successPosition = Constants.saveMsgPosition.get(resoultId);
					//Constants.msgDetailList.get(successPosition).setSendState("4");
				//}
				//if (Constants.HttpServerState) {
					//Map<String, String> params = new HashMap<String, String>();
					//params.put("method", "resState");
					//params.put("client", "SMS");
					//params.put("arrive", "" + autoId);
					//connection.asyncConnect(url + "/Message", params, HttpMethod.POST, new HttpClientCallBack(), context);
			//	}
				//Constants.saveMsgPosition.remove(resoultId2);
				//smsDetailIntent = new Intent(Constants.REFRESH_SMS_DETAIL);
				//context.sendBroadcast(smsDetailIntent);
				//Toast.makeText(context, "短信已送达", Toast.LENGTH_LONG).show();
				break;

			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:// 短信未送达
				// 短信未送达后的操作
				break;

			case SmsManager.RESULT_ERROR_RADIO_OFF:
				break;

			case SmsManager.RESULT_ERROR_NULL_PDU:
				break;
			}
			context.unregisterReceiver(this);
		}
	}
}
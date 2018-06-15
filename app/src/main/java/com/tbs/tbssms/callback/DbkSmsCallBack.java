/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * MyDiskCallBack.java
 * @version 2014-11-3
 * @time 下午12:01:09
 * @function 
 */
package com.tbs.tbssms.callback;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.myinterface.HttpCallback;
import com.tbs.tbssms.util.WenZi;

public class DbkSmsCallBack extends HttpCallback {

	private static final String TAG = "SearchDbkSmsCallBack";
	
	private Context mContext = null;
	private String json = null;
	private String sessionId = null;
	private boolean result = false;
	private String type = null;
	//private String notifyResult = "";
	//private IniFile m_iniFileIO = null;
	//private WenZi wenziUtil = null;

	private String jsonResult = "";

	@Override
	public void execute(String response) {
		super.execute(response);
		Log.d(TAG, "response:"+response);
		this.json = response;
		execute();
	}

	@Override
	public void execute(String response, String sessionId) {
		super.execute(response, sessionId);
		Log.d(TAG, "response:"+response);
		this.json = response;
		this.sessionId = sessionId;
		execute();
	}
	
	

	@Override
	public void execute(String response, String sessionId, Context context) {
		super.execute(response, sessionId, context);
		Log.d(TAG, "response:"+response);
		this.mContext = context;
		this.json = response;
		this.sessionId = sessionId;
		execute();
	}

	@Override
	public void execute(String response, String sessionId, Context context, String type) {
		super.execute(response, sessionId, context, type);
		Log.d(TAG, "response:"+response);
		this.mContext = context;
		this.json = response;
		this.sessionId = sessionId;
		this.type = type;
		//m_iniFileIO = new IniFile();
		//wenziUtil = new WenZi();
		execute();
	}
	
	public void cancleTask(){
		if(this.isCancelled()){
			return;
		}
		this.cancel(true);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Log.d(TAG, "onPreExecute");
	}

	@Override
	protected Integer doInBackground(Void... params) {
		Log.d(TAG, "doInBackground");
		result = parseJson(mContext, json);
		return null;
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);
		Log.d(TAG, "onPostExecute");
		//String placeholder = wenziUtil.QtoB(m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSGATEWAYSMSCONTENTPLACEHOLDER, Constants.PLACEHOLDER, "", (byte) 0));
		//Log.d(TAG, "placeholder:"+placeholder);
		if(result){
			//String success = wenziUtil.QtoB(m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSGATEWAYSMSCONTENT, Constants.ZCSUCCESSSMSCONTENT, "", (byte) 0));
			//Log.d(TAG, "success:"+success);
			//success.indexOf(placeholder, 0);
			//int count = 0;
			//int start = 0;
			//while ((start = success.indexOf(placeholder,start))>-1) {
				//++start;
				//++count;
			//}
			//String[] tempStr = new String[count];
			//start = 0;
			//for (int i = 0; i < count; i++) {
				//int end = success.indexOf("?", start);
				//tempStr[i] = success.substring(start, end);
				//start = end+1;
			//}
			//notifyResult += tempStr[0] + disk.getUserCode();
			//notifyResult = "《中国大百科全书数据库》自动回复：请点击此链接<a href=\"http://m.bkzx.cn/smsearch.cbs?smword=[%=smword%]&userphone=[%=userphone%]&smcode=[%=smcode%]\">查看\"[%=smword%]\"</a>的有关内容。";
		}else{
			//notifyResult = "《中国大百科全书数据库》自动回复：检索失败";
		}
		
		Log.d(TAG, "DbkSmsCallBack searchResult:"+jsonResult);
		
		SmsManager smsManager = SmsManager.getDefault();// 获得sms管理者
		if (jsonResult.length() > 70) {// 判断短信长度是否大于70个字，如果大于70分多条短信发送
			//List<String> contents = smsManager.divideMessage(jsonResult);// 截取短信息
			//for (String sms : contents) {// 循环发送短信
				//Log.d(TAG, "length > 70 :"+sms);
				//smsManager.sendTextMessage(type.trim(), null, sms, null, null);// 发送短信
			//}
			ArrayList<String> msgs = smsManager.divideMessage(jsonResult);
			Log.d(TAG, "msgs size:"+msgs.size());
			smsManager.sendMultipartTextMessage(type.trim(), null, msgs, null, null);
		} else {
			smsManager.sendTextMessage(type.trim(), null, jsonResult, null, null);// 发送一条短信
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		Log.d(TAG, "onProgressUpdate");
	}
	
	// 解析json数据
	private boolean parseJson(Context context, String json) {
		JSONObject jsonResult = null;
		boolean actionResult = false;
		try {
			jsonResult = new JSONObject(json);// 转换为JSONObject
			String response = jsonResult.getString("msg");
			this.jsonResult = jsonResult.getString("text");
			if(response!= null && response.equals("成功")){
				actionResult = true;
			}else{
				actionResult = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			actionResult = false;
		}
		return actionResult;
	}
}

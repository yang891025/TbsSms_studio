/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * MyDiskCallBack.java
 * @version 2014-11-3
 * @time 下午12:01:09
 * @function 
 */
package com.tbs.tbssms.callback;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;

import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.myinterface.HttpCallback;

public class DetailServerCallBack extends HttpCallback {

	private static final String TAG = "SearchDbkSmsCallBack";
	
	private Context mContext = null;
	private String json = null;
	private String sessionId = null;
	private ArrayList<String> result = null;
	private String type = null;
	private LinearLayout root = null;

	private String jsonResult = "";
	
	public DetailServerCallBack(LinearLayout root){
		this.root = root;
	}

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
		result = parseJson(json);
		return null;
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);
		Log.d(TAG, "onPostExecute");
		Intent intent = new Intent(Constants.REFRESH_DETAIL_SERVER);
		intent.putExtra("result", result);
		mContext.sendBroadcast(intent );
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		Log.d(TAG, "onProgressUpdate");
	}
	
	// 解析json数据
	private ArrayList<String> parseJson(String json) {
		JSONObject jsonResult = null;
		ArrayList<String> arrayList = null;
		try {
			jsonResult = new JSONObject(json);// 转换为JSONObject
			String response = jsonResult.getString("msg");
			if(response!= null && response.equals("成功")){
				JSONArray array = jsonResult.getJSONArray("mobileList");
				if(array.length() > 0){
					arrayList = new ArrayList<String>();
					for (int i = 0; i < array.length(); i++) {
						String value = array.getString(i);
						arrayList.add(value);
					}
				}
			}else{
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrayList;
	}
}

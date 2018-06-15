package com.tbs.tbssms.myinterface;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class HttpCallback extends AsyncTask<Void, Integer, Integer> implements HttpConnectionCallback {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tbs.tbsdisk.myinterface.HttpConnectionCallback#execute(java.lang.
	 * String)
	 */
	@Override
	public void execute(String response) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tbs.tbsdisk.myinterface.HttpConnectionCallback#execute(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public void execute(String response, String sessionId) {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see com.tbs.tbssms.myinterface.HttpConnectionCallback#execute(java.lang.String, java.lang.String, android.content.Context)
	 */
	@Override
	public void execute(String response, String sessionId, Context context) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tbs.tbsdisk.myinterface.HttpConnectionCallback#execute(java.lang.
	 * String, java.lang.String, android.content.Context)
	 */
	@Override
	public void execute(String response, String sessionId, Context context, String type) {
		// TODO Auto-generated method stub

	}

	// 运行在UI线程中，在调用doInBackground()之前执行
	@Override
	protected void onPreExecute() {
	}

	// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
	@Override
	protected Integer doInBackground(Void... params) {
		return null;
	}

	// 运行在ui线程中，在doInBackground()执行完毕后执行
	@Override
	protected void onPostExecute(Integer integer) {
	}

	// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
	@Override
	protected void onProgressUpdate(Integer... values) {
	}
}

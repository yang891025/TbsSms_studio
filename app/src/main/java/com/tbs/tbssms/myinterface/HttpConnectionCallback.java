package com.tbs.tbssms.myinterface;


import android.content.Context;

public interface HttpConnectionCallback {

	/**
	 * Call back method will be execute after the http request return.
	 * @param response the response of http request.
	 * The value will be null if any error occur.
	 */
	void execute(String response);
	
	void execute(String response,String sessionId);
	
	void execute(String response,String sessionId,Context context);
	
	void execute(String response,String sessionId,Context context, String type);

}

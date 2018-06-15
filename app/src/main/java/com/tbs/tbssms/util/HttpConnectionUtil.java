package com.tbs.tbssms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.myinterface.HttpCallback;

public class HttpConnectionUtil {

	private static final String TAG = "HttpConnectionUtil";
	private static HttpConnectionUtil instance; // 一个静态的HttpConnectionUtil实例
	private static MyHandlerThread handlerThread = null;
	private static Handler mHandler = null;

	public static enum HttpMethod {
		GET, POST
	}

	private HttpConnectionUtil() {
		handlerThread = new MyHandlerThread("myHanler");
		handlerThread.start();
		// 注意：
		// 这里必须用到handler的这个构造器，因为需要把callback传进去，从而使自己的HandlerThread的handlerMessage来替换掉Handler原生的handlerThread
		mHandler = new Handler(handlerThread.getLooper(), handlerThread);
	}

	public static HttpConnectionUtil newInstance() {
		if (instance == null) {
			instance = new HttpConnectionUtil();
		}
		return instance;
	}

	public void asyncConnect(final String url, final HttpMethod method,
			final HttpCallback callback, final Context context)
			throws Exception {
		asyncConnect(url, null, method, callback, context);

	}

	public void syncConnect(final String url, final HttpMethod method,
			final HttpCallback callback, final Context context) {
		syncConnect(url, null, method, callback, context);
	}

	// 异步方法
	public void asyncConnect(final String url,
			final Map<String, String> params, final HttpMethod method,
			final HttpCallback callback, final Context context) {
		handlerThread.setCallback(callback);
		handlerThread.setMethod(method);
		handlerThread.setmContext(context);
		handlerThread.setURL(url);
		handlerThread.setParams(params);

		Message msg = new Message();
		msg.what = 0;
		mHandler.sendMessage(msg);
	}

	// 同步方法
	public void syncConnect(final String url, final Map<String, String> params,
			final HttpMethod method, final HttpCallback callback,
			final Context context) {

		String json = null;
		BufferedReader reader = null;
		String sessionId = "";

		try {
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = getRequest(url, params, method, context);
			if (request != null) {
				HttpResponse response = client.execute(request);
				// 获得sessionId
				List<Cookie> cookie = ((AbstractHttpClient) client)
						.getCookieStore().getCookies();
				for (int i = 0; i < cookie.size(); i++) {
					sessionId = cookie.get(i).getValue();
				}
				// 获得返回值
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					reader = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent()));
					StringBuilder sb = new StringBuilder();
					String s = null;
					int i = 0;
					while ((s = reader.readLine()) != null) {
						++i;
						if (i > 1) {
							sb.append("\r\n");
							sb.append(s);
						} else {
							sb.append(s);
						}
					}
					json = sb.toString();
				}
				callback.execute(json, sessionId, context);
			}
		} catch (ClientProtocolException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Toast.makeText(context, "OA短信服务设置异常,请检查后重新启动服务", Toast.LENGTH_SHORT)
					.show();
			context.sendBroadcast(new Intent(Constants.HTTPCLOSESERVICE));
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				Log.e("HttpConnectionUtil", e.getMessage(), e);
			}
		}
	}

	public void asyncConnect(final String url, final HttpMethod method,
			final HttpCallback callback, final Context context,
			final String value) throws Exception {
		asyncConnect(url, null, method, callback, context, value);

	}

	public void syncConnect(final String url, final HttpMethod method,
			final HttpCallback callback, final Context context,
			final String value) {
		syncConnect(url, null, method, callback, context, value);
	}

	// 异步方法
	public void asyncConnect(final String url,
			final Map<String, String> params, final HttpMethod method,
			final HttpCallback callback, final Context context,
			final String value) {
		handlerThread.setCallback(callback);
		handlerThread.setMethod(method);
		handlerThread.setmContext(context);
		handlerThread.setURL(url);
		handlerThread.setParams(params);
		handlerThread.setValue(value);
		Message msg = new Message();
		msg.what = 1;
		mHandler.sendMessage(msg);
	}

	// 同步方法
	public void syncConnect(final String url, final Map<String, String> params,
			final HttpMethod method, final HttpCallback callback,
			final Context context, String value) {
		String json = null;
		BufferedReader reader = null;
		String sessionId = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = getRequest(url, params, method, context);
			if (request != null) {
				HttpResponse response = client.execute(request);
				// 获得sessionId
				List<Cookie> cookie = ((AbstractHttpClient) client)
						.getCookieStore().getCookies();
				for (int i = 0; i < cookie.size(); i++) {
					sessionId = cookie.get(i).getValue();
				}
				// 获得返回值
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					reader = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent()));
					StringBuilder sb = new StringBuilder();
					String s = null;
					int i = 0;
					while ((s = reader.readLine()) != null) {
						++i;
						if (i > 1) {
							sb.append("\r\n");
							sb.append(s);
						} else {
							sb.append(s);
						}
					}
					json = sb.toString();
					System.out.println("json=" + json);
				}
				callback.execute(json, sessionId, context, value);

			}
		} catch (ClientProtocolException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				Log.e("HttpConnectionUtil", e.getMessage(), e);
			}
		}
	}

	private HttpUriRequest getRequest(String url, Map<String, String> params,
			HttpMethod method, Context context) {
		if (url != null && url.indexOf("http://") < 0) {
			url = "http://" + url;
		}
		Log.d(TAG, "request url:" + url);
		if (method.equals(HttpMethod.POST)) {// post提交
			List<NameValuePair> listParams = new ArrayList<NameValuePair>();
			if (params != null) {
				for (String name : params.keySet()) {
					listParams.add(new BasicNameValuePair(name, params
							.get(name)));
				}
			}
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						listParams, "utf-8");// post 提交 需要设置编码格式
				HttpPost request = new HttpPost(url);
				request.setHeader("Cookie", "JSESSIONID=" + Constants.SessionId);
				request.setEntity(entity);
				return request;
			} catch (UnsupportedEncodingException e) {
				// Should not come here, ignore me.
				throw new java.lang.RuntimeException(e.getMessage(), e);
			}
		} else {// get提交
			if (url.indexOf("?") < 0) {
				url += "?";
			}
			if (params != null) {
				int i = 0;
				for (String name : params.keySet()) {
					if (i == 0) {
						url += name + "=" + URLEncoder.encode(params.get(name));
					} else {
						url += "&" + name + "="
								+ URLEncoder.encode(params.get(name));
					}
					i++;
				}
			}
			HttpGet request = new HttpGet(url);
			request.setHeader("Cookie", "JSESSIONID=" + Constants.SessionId);
			return request;
		}
	}

	public void canThread() {
		if (handlerThread != null) {
			handlerThread.stop();
		}
	}

	private class MyHandlerThread extends HandlerThread implements Callback {

		String URL = null;
		Map<String, String> params = null;
		HttpMethod method = null;
		HttpCallback callback = null;
		Context mContext = null;
		String value = null;

		public MyHandlerThread(String name) {
			super(name);
		}

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 异步请求 5中参数
				syncConnect(URL, params, method, callback, mContext);
				break;

			case 1:// 异步请求 5中参数
				syncConnect(URL, params, method, callback, mContext, value);
				break;

			default:
				break;
			}
			return true;
		}

		public String getURL() {
			return URL;
		}

		public void setURL(String uRL) {
			URL = uRL;
		}

		public Map<String, String> getParams() {
			return params;
		}

		public void setParams(Map<String, String> params) {
			this.params = params;
		}

		public HttpMethod getMethod() {
			return method;
		}

		public void setMethod(HttpMethod method) {
			this.method = method;
		}

		public HttpCallback getCallback() {
			return callback;
		}

		public void setCallback(HttpCallback callback) {
			this.callback = callback;
		}

		public Context getmContext() {
			return mContext;
		}

		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}

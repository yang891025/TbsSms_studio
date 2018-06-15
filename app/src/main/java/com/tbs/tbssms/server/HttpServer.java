package com.tbs.tbssms.server;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.callback.HttpClientCallBack;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.util.HttpConnectionUtil;
import com.tbs.tbssms.util.HttpConnectionUtil.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HttpServer extends Service {
	
	private static final String TAG = "HttpServer";
	
	private ServiceHandler mServiceHandler;
	private final Timer timer = new Timer();
	private TimerTask task;
	private HttpConnectionUtil connection;
	private IniFile m_iniFileIO = null;
	
	String username;
	String password;
	String url;

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (!Constants.LoginState) {
				connection.asyncConnect(url + "/Login", login(), HttpMethod.POST, new HttpClientCallBack(), getApplicationContext());
			} else {
				connection.asyncConnect(url + "/Message", getSendMsgContant(), HttpMethod.POST, new HttpClientCallBack(), getApplicationContext());
			}
		}
	}
	
	public Map<String, String> login(){
		Map<String, String> params = new HashMap<String, String>();
        params.put("method", "login");  
        params.put("username", username);  
        params.put("pwd", password);  
        params.put("client", "SMS");  
		return params;  
	}
	
	public Map<String, String> getSendMsgContant(){
		Map<String, String> params = new HashMap<String, String>();
        params.put("method", "reqSMS");  
        params.put("client", "SMS");  
		return params;  
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		
		connection = HttpConnectionUtil.newInstance();
		m_iniFileIO = new IniFile();

		username = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.LOGININFO, Constants.USERNAME, "", (byte) 0);
		password = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.LOGININFO, Constants.PASSWORD, "", (byte) 0);
		String ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.LOGININFO, Constants.LOGINURL, "", (byte) 0);
		String port = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.LOGININFO, Constants.LOGINPORT, "", (byte) 0);
		
		boolean ipCanUser = false;
		boolean portCanUser = false;
		if(ip != null && !ip.equals("")){
			ipCanUser = true;
		}
		if(port != null && !port.equals("")){
			portCanUser = true;
		}
		if(!ipCanUser){
			Toast.makeText(getApplicationContext(), "服务器地址不可用，请检查后重新启动服务", Toast.LENGTH_SHORT).show();
		}
		if(!portCanUser){
			Toast.makeText(getApplicationContext(), "服务器端口不可用，请检查后重新启动服务", Toast.LENGTH_SHORT).show();
		}
		if(ipCanUser && portCanUser){
			url = ip+":"+port;
			if(url!=null && !url.equals("")){
				if(url.indexOf("http://")>-1){
					
				}else{
					url = "http://"+url;
				}
			}
			// 发送广播
			Intent intent = new Intent(Constants.HTTP_SERVICE_STATE);
			sendBroadcast(intent);
			// Start up the thread running the service. Note that we create a
			// separate thread because the service normally runs in the process's
			// main thread, which we don't want to block. We also make it
			// background priority so CPU-intensive work will not disrupt our UI.
			HandlerThread thread = new HandlerThread("ServiceStartArguments");
			thread.start();
			// Get the HandlerThread's Looper and use it for our Handler
			mServiceHandler = new ServiceHandler(thread.getLooper());
		}else{
			sendBroadcast(new Intent(Constants.HTTPCLOSESERVICE));
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		// 初始化計時器任務
		task = new TimerTask() {
			@Override
			public void run() {
				Message msg = mServiceHandler.obtainMessage();
				msg.arg1 = startId;
				mServiceHandler.sendMessage(msg);
			}
		};
		// 啟動定時器
		timer.schedule(task, 5000, 5000);
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// 销毁时做处理
		Intent intent = new Intent(Constants.HTTP_SERVICE_STATE);
		sendBroadcast(intent);
		timer.cancel();
	}
}

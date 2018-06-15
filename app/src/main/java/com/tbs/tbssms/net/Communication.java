package com.tbs.tbssms.net;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Communication类是用户和实际发送、接收消息线程之间的桥梁，用户向Communication发送命令，
 * Communication调用线程执行发送请求
 */
public class Communication {
	
	private static final String TAG = "Communication";
	
	private NetWorker netWorker; // 这是一个线程类
	private static Communication instance; // 一个静态的Communication实例
	private Context mcontext;// 消息摘要
	private String ip;
	private int port;

	//启动连接服务器
	public Communication() {
		netWorker = new NetWorker();//实例化线程类
		netWorker.start();// 线程开始执行
	}
	
	public Communication(Context context, String ip, int port, String telephone) {
		this.mcontext = context;
		this.port = port;
		netWorker = new NetWorker(context, port, ip, telephone);//实例化线程类
		netWorker.start();// 线程开始执行
	}

	public static Communication newInstance() {
		if (instance == null){
			Log.d(TAG, "newInstance new conmmunication");
			instance = new Communication();
		}
		return instance;
	}

	public static Communication newInstance(Context context, String ip, int port, String telephone) {
		if (instance == null){
			Log.d(TAG, "Communication newInstance");
			instance = new Communication(context, ip, port, telephone);
		}
		return instance;
	}

	public void setInstanceNull() {
		instance = null;
	}

	public NetWorker getTransportWorker() {
		return netWorker;
	}
	
	//创建sessionid
	public String newSessionID() {
		return String.valueOf(System.currentTimeMillis());
	}

	//重新连接
	public void reconnect() {
		netWorker.notify();
	}
	
	//程序退出时的清理工作
	public void stopWork() throws IOException {
		netWorker.setOnWork(false);
	}
	
	//判断socket是否还在连接着
	public boolean isConnection(){
		return netWorker.isConnection();
	}
}

package com.tbs.tbssms.common;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Looper;

import com.tbs.tbssms.util.ExceptionUtil;

/**
 * 搴旂敤绋嬪簭寮傚父绫伙細鐢ㄤ簬鎹曡幏寮傚父鍜屾彁绀洪敊璇俊鎭?
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressWarnings("serial")
public class AppException extends Exception implements UncaughtExceptionHandler {

	/** 绯荤粺榛樿鐨刄ncaughtException澶勭悊绫? */
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private AppException() {
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	/**
	 * 淇濆瓨鐢ㄦ埛鏃ュ織
	 * 
	 * @param excp
	 */
	@SuppressLint("SimpleDateFormat")
	public static void saveUserLog(String doWhat) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String errorlog = formatter.format(new Date()) + ".txt";
		String savePath = "";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			savePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Tbs-Soft/Log/TBSSms/";
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File f = new File(savePath + errorlog);
			f.createNewFile();// 不存在则创建
			fw = new FileWriter(f, true);
			pw = new PrintWriter(fw);
			pw.println(doWhat);
			pw.println("--------------------" + (new Date().toLocaleString())
					+ "---------------------");
			pw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

	}

	/**
	 * 鑾峰彇APP寮傚父宕╂簝澶勭悊瀵硅薄
	 * 
	 * @param context
	 * @return
	 */
	public static AppException getAppExceptionHandler() {
		return new AppException();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}

	}

	/**
	 * 鑷畾涔夊紓甯稿鐞?鏀堕泦閿欒淇℃伅&鍙戦?閿欒鎶ュ憡
	 * 
	 * @param ex
	 * @return true:澶勭悊浜嗚寮傚父淇℃伅;鍚﹀垯杩斿洖false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// AppManager.getInstance().killTopActivity();
		final Context context = AppManager.getInstance().getTopActivity();
		if (context == null) {
			return false;
		}
		final String crashReport = getCrashReport(context, ex);
		// 鏄剧ず寮傚父淇℃伅&鍙戦?鎶ュ憡
		new Thread() {
			public void run() {
				Looper.prepare();
				ExceptionUtil.sendAppCrashReport(context, crashReport);
				Looper.loop();
			}
		}.start();
		return true;
	}

	/**
	 * 鑾峰彇APP宕╂簝寮傚父鎶ュ憡
	 * 
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Context context, Throwable ex) {
		PackageManager pinMgr = context.getApplicationContext()
				.getPackageManager();
		PackageInfo pinfo = null;
		try {
			pinfo = pinMgr.getPackageInfo(context.getApplicationContext()
					.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "("
				+ pinfo.versionCode + ")\n");
		exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE
				+ "(" + android.os.Build.MODEL + ")\n");
		exceptionStr.append("Exception: " + ex.getMessage() + "\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\n");
		}
		ex.printStackTrace();
		saveUserLog(exceptionStr.toString());
		return exceptionStr.toString();
	}
}

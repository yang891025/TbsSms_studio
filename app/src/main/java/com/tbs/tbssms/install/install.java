package com.tbs.tbssms.install;

/**
 * 
 * @author xuetao.ren
 * 此类为系统安装的隐士意图
 */

import java.io.File;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.tbs.tbssms.constants.Constants;

public class install extends Activity {

	private static final String TAG = "install";
	
	boolean installState = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "start install apk now");
		installPackage(Constants.APK_SAVEFILE_URL);
	}
	
	
	public void installPackage(String url){
		// 实例化意图
		Intent notify_Intent = new Intent();
		notify_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notify_Intent.setAction(android.content.Intent.ACTION_VIEW);
		notify_Intent.setDataAndType(Uri.fromFile(new File(url)),"application/vnd.android.package-archive");
		startActivity(notify_Intent);
		// 取消上一个通知
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(8888);
		overridePendingTransition(0, 0);
		finish();
	}
}

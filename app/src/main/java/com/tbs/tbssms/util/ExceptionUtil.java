package com.tbs.tbssms.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.server.HttpServer;

@SuppressLint("ShowToast")
public class ExceptionUtil {
	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(null);
		builder.setTitle(R.string.app_name);
		builder.setCancelable(false);
		builder.setMessage("抱歉应用发生崩溃异常,无法继续运行，详情查看日志信息。。。");
		builder.setPositiveButton(R.string.main_exit,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent1 = new Intent();
                        intent1.setAction("com.tbs.tbssms.SmsService");//你定义的service的action
                        intent1.setPackage(cont.getPackageName());//这里你需要设置你应用的包名
						cont.stopService(intent1);// 停止短信服务
						Intent intent2 = new Intent(cont, HttpServer.class);
						cont.stopService(intent2);// 停止短信服务
						Constants.HttpServerState = false;// 将服务状态标志位启动
						Constants.MisServerState = false;
						Constants.DbkServerState = false;
						AppManager.getInstance().AppExit(cont,1);
					}
				});
		builder.show();
	}
}
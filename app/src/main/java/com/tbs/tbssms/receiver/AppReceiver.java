package com.tbs.tbssms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.activity.LaunchActivity;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.server.HttpServer;

import java.io.IOException;

public class AppReceiver extends BroadcastReceiver
{

    private static final String TAG = "AppReceiver";
    boolean launch_app = false;
    private IniFile m_iniFileIO = null;
    private Intent intent = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        m_iniFileIO = new IniFile();
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            String appAutoState = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants
                    .AUTOSTART, Constants.APP_AUTOSTART, "false", (byte) 0);
            if (appAutoState != null && appAutoState.equals("false")) {
                launch_app = false;
            } else if (appAutoState != null && appAutoState.equals("true")) {
                launch_app = true;
            }
            if (launch_app) {
                Intent startLaunch = new Intent();
                startLaunch.setClass(context, LaunchActivity.class);
                startLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startLaunch);
            }
        } else if (Constants.START_TIMER.equals(intent.getAction())) {//连接异常
//            intent = new Intent(context, SocketServer.class);
//            context.startService(intent);
            intent = new Intent();
            intent.setAction("com.tbs.tbssms.SmsService");//你定义的service的action
            intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
            context.startService(intent);
            //this.stopService(intent);
            //return intent;
            Constants.MisServerState = true;
            Toast.makeText(context, R.string.mis_server_begin, Toast.LENGTH_SHORT).show();
        } else if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {

            if (Constants.HttpServerState) {
                Intent intent2 = new Intent(context, HttpServer.class);
                context.stopService(intent2);//停止短信服务
                Constants.HttpServerState = false;//将服务状态标志位启动
            }
            if (Constants.MisServerState) {
                stopSmsServier();
//                intent = new Intent(context, SocketServer.class);
//                context.stopService(intent);
                intent = new Intent();
                intent.setAction("com.tbs.tbssms.SmsService");//你定义的service的action
                intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                context.stopService(intent);
                Constants.MisServerState = false;
            }
            if (Constants.DbkServerState) {
                Constants.DbkServerState = false;
            }
        } else if (Constants.HTTPSTARTSERVICE.equals(intent.getAction())) {
            intent = new Intent(context, HttpServer.class);
            context.startService(intent);//启动服务
            Constants.HttpServerState = true;
            Toast.makeText(context, R.string.sms_server_begin, Toast.LENGTH_SHORT).show();

            context.sendBroadcast(new Intent(Constants.REFRESH_SERVICE_BUTTON));
        } else if (Constants.HTTPCLOSESERVICE.equals(intent.getAction())) {
            intent = new Intent(context, HttpServer.class);
            context.stopService(intent);//停止短信服务
            Constants.HttpServerState = false;//将服务状态标志位启动
            Toast.makeText(context, R.string.sms_server_close, Toast.LENGTH_SHORT).show();

            context.sendBroadcast(new Intent(Constants.REFRESH_SERVICE_BUTTON));
        } else if (Constants.MISSTARTSERVICE.equals(intent.getAction())) {
            //launchSmsServier(context);
//            intent = new Intent(context, SocketServer.class);
//            context.startService(intent);
            intent = new Intent();
            intent.setAction("com.tbs.tbssms.SmsService");//你定义的service的action
            intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
            context.startService(intent);
            Constants.MisServerState = true;
            Toast.makeText(context, R.string.mis_server_begin, Toast.LENGTH_SHORT).show();
        } else if (Constants.DBKSTARTSERVICE.equals(intent.getAction())) {
            Constants.DbkServerState = true;//将服务状态标志位启动
            Toast.makeText(context, R.string.dbk_server_begin, Toast.LENGTH_SHORT).show();
        }
    }

    //	public void launchSmsServier(Context mContext) {
//		m_iniFileIO = new IniFile();
//		String ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.IMSMISSERVER,
// Constants.IMSMISSERVERIP, "", (byte) 0);
//		String port = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.IMSMISSERVER,
// Constants.IMSMISSERVERPORT, "", (byte) 0);
//		String telephoneNum = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.TELEPHONESETTING, Constants.TELEPHONENUMBER, "", (byte) 0);
//		Constants.mCommunication = new Communication(mContext, ip, Integer.valueOf(port), telephoneNum);
//	}
//
    public void stopSmsServier() {
        if (Constants.mCommunication != null && Constants.mCommunication.isConnection()) {
            try {
                Constants.mCommunication.stopWork();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

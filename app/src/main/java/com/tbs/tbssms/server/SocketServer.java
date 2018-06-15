package com.tbs.tbssms.server;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.net.Communication;

import java.util.Timer;
import java.util.TimerTask;

public class SocketServer extends Service {

	private static final String TAG = "SocketServer";

	private Timer timer;
	// private Communication con = null;
	private IniFile m_iniFileIO = null;
	private String[] node = null;
	private int index = 0;
    private final static int GRAY_SERVICE_ID = 20170721;

    @Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "Communication timer is start");
		m_iniFileIO = new IniFile();
		timer = new Timer(true);
		timer.schedule(task, 1, 10000); // 延时1000ms后执行，1000ms执行一次
	}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }
        //flags = Service.START_STICKY;
        return super.onStartCommand(intent, flags, startId);
        // return START_REDELIVER_INTENT;
    }
	@Override
	public void onDestroy() {
        Log.d(TAG, "SocketServer is onDestroy");
		timer.cancel(); // 退出计时器
        Constants.mCommunication.setInstanceNull();
        Intent intent = new Intent();
        intent.setAction("com.tbs.tbssms.SmsService");//你定义的service的action
        intent.setPackage(getPackageName());//这里你需要设置你应用的包名
        startService(intent);
        Constants.MisServerState = true;
	}

	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
            Log.d(TAG, "task is running");
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (!Constants.MisServerState) {
					Log.d(TAG, "MisServerState:"+Constants.MisServerState);
					stopSelf();
				} else if (Constants.mCommunication == null) {
					String ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.IMSMISSERVER, Constants.IMSMISSERVERIP, "", (byte) 0);
					String port = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.IMSMISSERVER, Constants.IMSMISSERVERPORT, "", (byte) 0);
					String telephoneNum = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.TELEPHONESETTING, Constants.TELEPHONENUMBER, "", (byte) 0);
					Constants.mCommunication = new Communication(getApplication(), ip, Integer.valueOf(port), telephoneNum);
				} else if (Constants.mCommunication != null && !Constants.mCommunication.getTransportWorker().isOnSocket()) {
					Constants.mCommunication.setInstanceNull();
					String ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.IMSMISSERVER, Constants.IMSMISSERVERIP, "", (byte) 0);
					String port = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.IMSMISSERVER, Constants.IMSMISSERVERPORT, "", (byte) 0);
					String telephoneNum = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.TELEPHONESETTING, Constants.TELEPHONENUMBER, "", (byte) 0);
					Constants.mCommunication = new Communication(getApplication(), ip, Integer.valueOf(port), telephoneNum);
				}else if(Constants.mCommunication != null && !Constants.mCommunication.isConnection()){
                    Constants.mCommunication.setInstanceNull();
                    String ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.IMSMISSERVER, Constants.IMSMISSERVERIP, "", (byte) 0);
                    String port = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.IMSMISSERVER, Constants.IMSMISSERVERPORT, "", (byte) 0);
                    String telephoneNum = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.TELEPHONESETTING, Constants.TELEPHONENUMBER, "", (byte) 0);
                    Constants.mCommunication = new Communication(getApplication(), ip, Integer.valueOf(port), telephoneNum);
                }
				break;
			}
		}
	};

    public static class GrayInnerService extends Service
    {
        private final static int GRAY_SERVICE_ID = 20170721;
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            //flags = Service.START_STICKY;
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }
}

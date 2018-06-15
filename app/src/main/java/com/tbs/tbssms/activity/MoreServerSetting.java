package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.util.Util;

import java.io.IOException;

public class MoreServerSetting extends Activity implements OnClickListener {

	private static final String TAG = "MoreServerSetting";
	private final Context mContext = this;
	private BroadcastReceiver MyBroadCastReceiver = null;
	private String[] node = null;
	private IniFile m_iniFileIO = null;

	private RelativeLayout http_server_btn;
	private RelativeLayout mis_server_btn;
	private RelativeLayout chat_server_btn;
	private RelativeLayout dbk_server_btn;

	private ImageView toggleButton;
	private ImageView mis_toggleButton;
	private ImageView chat_toggleButton;
	private ImageView dbk_toggleButton;

	private CheckBox checkbox;
	private CheckBox beginCheckBox;

	private Button backBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_button_settings);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.REFRESH_SERVICE_BUTTON);
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.REFRESH_SERVICE_BUTTON.equals(intent.getAction())) {
					if (!Constants.HttpServerState) {
						toggleButton
								.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					} else {
						toggleButton
								.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
					}
					if (!Constants.MisServerState) {
						mis_toggleButton
								.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					} else {
						mis_toggleButton
								.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
					}
					if (!Constants.DbkServerState) {
						dbk_toggleButton
								.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					} else {
						dbk_toggleButton
								.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
					}
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);

		m_iniFileIO = new IniFile();

		http_server_btn = (RelativeLayout) findViewById(R.id.http_server_btn);
		mis_server_btn = (RelativeLayout) findViewById(R.id.mis_server_btn);
		chat_server_btn = (RelativeLayout) findViewById(R.id.chat_server_btn);
		dbk_server_btn = (RelativeLayout) findViewById(R.id.dbk_server_btn);

		toggleButton = (ImageView) findViewById(R.id.toggleButton);
		mis_toggleButton = (ImageView) findViewById(R.id.mis_toggleButton);
		chat_toggleButton = (ImageView) findViewById(R.id.chat_toggleButton);
		dbk_toggleButton = (ImageView) findViewById(R.id.dbk_toggleButton);

		backBtn = (Button) findViewById(R.id.backBtn);

		checkbox = (CheckBox) findViewById(R.id.custom_server);
		beginCheckBox = (CheckBox) findViewById(R.id.toggleButton2);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
			}
		});

		http_server_btn.setOnClickListener(this);
		mis_server_btn.setOnClickListener(this);
		chat_server_btn.setOnClickListener(this);
		dbk_server_btn.setOnClickListener(this);

		String appAutoState = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.AUTOSTART,
				Constants.SERVER_AUTOSTART, "false", (byte) 0);
		if (appAutoState != null && appAutoState.equals("false")) {
			checkbox.setChecked(false);
		} else if (appAutoState != null && appAutoState.equals("true")) {
			checkbox.setChecked(true);
		}

		String serverAutoState = m_iniFileIO.getIniString(
				Constants.EXTERNALPATH + Constants.INIURL, Constants.AUTOSTART,
				Constants.APP_AUTOSTART, "false", (byte) 0);
		if (serverAutoState != null && serverAutoState.equals("false")) {
			beginCheckBox.setChecked(false);
		} else if (serverAutoState != null && serverAutoState.equals("true")) {
			beginCheckBox.setChecked(true);
		}

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				m_iniFileIO.writeIniString(Constants.EXTERNALPATH
						+ Constants.INIURL, Constants.AUTOSTART,
						Constants.SERVER_AUTOSTART, "" + isChecked);
			}
		});

		beginCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				m_iniFileIO.writeIniString(Constants.EXTERNALPATH
						+ Constants.INIURL, Constants.AUTOSTART,
						Constants.APP_AUTOSTART, "" + isChecked);
			}
		});

		if (!Constants.HttpServerState) {
			toggleButton
					.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
		} else {
			toggleButton
					.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
		}

		if (!Constants.MisServerState) {
			mis_toggleButton
					.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
		} else {
			mis_toggleButton
					.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
		}

		if (!Constants.DbkServerState) {
			dbk_toggleButton
					.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
		} else {
			dbk_toggleButton
					.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
		}
	}

//	public void launchSmsServier() {
//		SIMCardInfo info = new SIMCardInfo(mContext);
//		String telephone = info.getNativePhoneNumber();
//		String ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH
//				+ Constants.INIURL, Constants.IMSMISSERVER,
//				Constants.IMSMISSERVERIP, "", (byte) 0);
//		String port = m_iniFileIO.getIniString(Constants.EXTERNALPATH
//				+ Constants.INIURL, Constants.IMSMISSERVER,
//				Constants.IMSMISSERVERPORT, "", (byte) 0);
//		String telephoneNum = m_iniFileIO.getIniString(Constants.EXTERNALPATH
//				+ Constants.INIURL, Constants.TELEPHONESETTING,
//				Constants.TELEPHONENUMBER, "", (byte) 0);
//		Constants.mCommunication = new Communication(getApplication(), ip,
//				Integer.valueOf(port), telephoneNum);
//	}
//
//	public void stopSmsServier() {
//		Log.d(TAG, "??????????socket");
//		if (Constants.mCommunication != null
//				&& Constants.mCommunication.isConnection()) {
//			try {
//				Constants.mCommunication.stopWork();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.http_server_btn:
			if (Util.checkNetState(mContext)) {
				if (!Constants.HttpServerState) {
					sendBroadcast(new Intent(Constants.HTTPSTARTSERVICE));
					toggleButton
							.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
				} else {
					sendBroadcast(new Intent(Constants.HTTPCLOSESERVICE));
					toggleButton
							.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
				}
			} else {
				Toast.makeText(getApplicationContext(), "???????????",
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.mis_server_btn:
			if (Util.checkNetState(mContext)) {
				if (!Constants.MisServerState) {
//                   Intent intent = new Intent(mContext, SocketServer.class);
//                    mContext.startService(intent);
                    Intent intent = new Intent();
                    intent.setAction("com.tbs.tbssms.SmsService");//你定义的service的action
                    intent.setPackage(mContext.getPackageName());//这里你需要设置你应用的包名
                    mContext.startService(intent);
					Constants.MisServerState = true;// ?????????????????
					mis_toggleButton
							.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
					Toast.makeText(mContext, R.string.mis_server_begin,
							Toast.LENGTH_SHORT).show();
				} else {
					stopSmsServier();
//                    Intent intent = new Intent(mContext, SocketServer.class);
//                    mContext.stopService(intent);
                   Intent intent = new Intent();
                    intent.setAction("com.tbs.tbssms.SmsService");//你定义的service的action
                    intent.setPackage(mContext.getPackageName());//这里你需要设置你应用的包名
                    mContext.stopService(intent);
					Constants.MisServerState = false;// ?????????????????
					mis_toggleButton
							.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					Toast.makeText(mContext, R.string.mis_server_close,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "???????????",
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.chat_server_btn:

			break;

		case R.id.dbk_server_btn:
			if (Util.checkNetState(mContext)) {
				if (!Constants.DbkServerState) {
					Constants.DbkServerState = true;// ?????????????????
					dbk_toggleButton
							.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
					Toast.makeText(mContext, R.string.dbk_server_begin,
							Toast.LENGTH_SHORT).show();
				} else {
					Constants.DbkServerState = false;// ?????????????????
					dbk_toggleButton
							.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					Toast.makeText(mContext, R.string.dbk_server_close,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "???????????",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
		}
		return true;
	}
    public void stopSmsServier() {
        if (Constants.mCommunication != null && Constants.mCommunication.isConnection()) {
            try {
                Constants.mCommunication.stopWork();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
		if (MyBroadCastReceiver != null) {
			unregisterReceiver(MyBroadCastReceiver);
		}
	}
}
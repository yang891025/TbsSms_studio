package com.tbs.tbssms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.chart.SalesStackedBarChart;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.database.MESSAGE_BOX;
import com.tbs.tbssms.util.Util;

public class MainSetting extends Activity implements OnClickListener {

	protected static final String TAG = "MainSetting";
	private final Context mContext = this;
	private IniFile m_iniFileIO = null;
	private String[] node = null;

	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	private BroadcastReceiver MyBroadCastReceiver;// 广播

	// private RelativeLayout httpBtn;//启动后台服务的按钮
	private RelativeLayout moreBtn;// 高级设置
	private RelativeLayout aboutBtn;
	private RelativeLayout tongjiBtn;
	private RelativeLayout moreServerBtn;

	private RelativeLayout cleanBtn;// 情况短信内容按钮
	private RelativeLayout eamilSettingBtn;

	private TextView popup_text = null;
	
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_settings);
		AppManager.getInstance().addActivity(this);
		init();
		setting();
	}

	private void init() {
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);// 退出activity的广播
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
					finish();
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);
	}

	public void setting() {

		m_iniFileIO = new IniFile();

		// httpBtn = (RelativeLayout) findViewById(R.id.http_server_btn);

		moreServerBtn = (RelativeLayout) findViewById(R.id.more_server_btn);
		moreBtn = (RelativeLayout) findViewById(R.id.more_setting);
		aboutBtn = (RelativeLayout) findViewById(R.id.about_btn);
		tongjiBtn = (RelativeLayout) findViewById(R.id.tongji);
		eamilSettingBtn = (RelativeLayout) findViewById(R.id.email_setting);

		cleanBtn = (RelativeLayout) findViewById(R.id.clean_sms_btn);
		
		popup_text = (TextView) findViewById(R.id.popup_text);

		// httpBtn.setOnClickListener(this);
		moreServerBtn.setOnClickListener(this);
		moreBtn.setOnClickListener(this);
		aboutBtn.setOnClickListener(this);
		tongjiBtn.setOnClickListener(this);
		
		popup_text.setText("退出应用");

		cleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkVersion();
			}
		});
		
		eamilSettingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, EmailSetting.class);
				startActivity(intent);
			}
		});
		
		popup_text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, Exit.class);
				startActivity(intent);
			}
		});
	}

	// 情况所有短信
	public void checkVersion() {
		AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
		builder2.setTitle(R.string.clean_sms)
				.setMessage(R.string.clean_sms_title)
				.setInverseBackgroundForced(true).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						delAllSms();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert2 = builder2.create();
		alert2.show();
	}

	public void delAllSms() {
		int resoult = new DataBaseUtil(this).getDataBase().delete(
				MESSAGE_BOX.TABLE_NAME, null, null);
		if (resoult > 0) {
			Util.checkDialog(this, "短信已清空！");// 弹出提示对话框
			Intent smsIntent = new Intent(Constants.REFRESH_SMS);
			sendBroadcast(smsIntent);
			Intent smsDetailIntent = new Intent(Constants.REFRESH_SMS_DETAIL);
			sendBroadcast(smsDetailIntent);
		} else {
			Util.checkDialog(this, "没有短信可清除！");// 弹出提示对话框
		}
	}

	// 销毁时生命周期
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
		if (MyBroadCastReceiver != null) {
			unregisterReceiver(MyBroadCastReceiver);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.more_server_btn:
			intent = new Intent();
			intent.setClass(this, MoreServerSetting.class);
			startActivity(intent);
			getParent().overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;

		case R.id.more_setting:
			intent = new Intent();
			intent.setClass(this, MoreSetting.class);
			startActivity(intent);
			getParent().overridePendingTransition(R.anim.push_in,
					R.anim.push_out);
			break;

		case R.id.about_btn:
			intent = new Intent();
			intent.setClass(this, AboutApp.class);
			startActivity(intent);
			getParent().overridePendingTransition(R.anim.push_in,
					R.anim.push_out);
			break;

		case R.id.tongji:
			intent = new SalesStackedBarChart().execute(this);
			startActivity(intent);
			break;

		default:
			break;
		}
	}


	// 键盘按钮监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 获取
			Intent intent = new Intent();
			intent.setClass(MainSetting.this, Exit.class);
			startActivity(intent);
		} else if (keyCode == KeyEvent.KEYCODE_MENU) { // 获取 Menu键
			Intent intent = new Intent();
			intent.setClass(MainSetting.this, Exit.class);
			startActivity(intent);
		}
		return true;
	}
}
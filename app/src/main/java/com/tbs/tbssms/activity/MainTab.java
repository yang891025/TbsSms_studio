package com.tbs.tbssms.activity;



import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;

import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.util.HttpConnectionUtil;

@SuppressWarnings("deprecation")
public class MainTab extends TabActivity implements OnCheckedChangeListener {

	protected static final String TAG = "MainTab";
	
	private RadioButton smsBtn;
	private RadioButton addressBtn;
	private RadioButton settingBtn;

	private Intent smsIntent;
	private Intent addressIntent;
	private Intent settingCIntent;

	private TabHost tabHost;
	private Resources res;
	
	private BroadcastReceiver MyBroadCastReceiver;
	private HttpConnectionUtil connection;
	
	String username;
	String password;
	String url;
	
	String autoId = null;//返回给服务器的id
	long resoultId = 0;//添加到短信数据表中
	int returnBufferId = 0;//删除缓冲表中数据
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);
		init();
	}

	private void init() {
		smsIntent = new Intent(this, MainSms.class);
		addressIntent = new Intent(this, MainAddress.class);
		settingCIntent = new Intent(this, MainSetting.class);

		smsBtn = (RadioButton) findViewById(R.id.main_tab_weixin);
		addressBtn = (RadioButton) findViewById(R.id.main_tab_address);
		settingBtn = (RadioButton) findViewById(R.id.main_tab_more);
		
		smsBtn.setOnCheckedChangeListener(this);
		addressBtn.setOnCheckedChangeListener(this);
		settingBtn.setOnCheckedChangeListener(this);

		res = getResources(); // Resource object to get Drawables
		tabHost = getTabHost(); // The activity TabHost

		tabHost.addTab(tabHost.newTabSpec("sms").setIndicator(null, null).setContent(smsIntent));
		tabHost.addTab(tabHost.newTabSpec("address").setIndicator(null, null).setContent(addressIntent));
		tabHost.addTab(tabHost.newTabSpec("setting").setIndicator(null, null).setContent(settingCIntent));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.main_tab_weixin:
				tabHost.setCurrentTabByTag("sms");
				addressBtn.setChecked(false);
				settingBtn.setChecked(false);
				break;
			case R.id.main_tab_address:
				tabHost.setCurrentTabByTag("address");
				smsBtn.setChecked(false);
				settingBtn.setChecked(false);
				break;
			case R.id.main_tab_more:
				tabHost.setCurrentTabByTag("setting");
				addressBtn.setChecked(false);
				smsBtn.setChecked(false);
				break;
			}
		}
	}
}
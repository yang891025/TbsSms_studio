package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;

public class MoreSetting extends Activity {

	private RelativeLayout loginSettingBtn;
	private RelativeLayout notifiSettingBtn;
	private RelativeLayout gatewaySettingBtn;
    private RelativeLayout sms_forward_setting_btn;
	private RelativeLayout telephoneSetting;// 情况短信内容按钮
	private RelativeLayout detail_setting_btn = null;
	private Button backBtn;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_more_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
		loginSettingBtn = (RelativeLayout) findViewById(R.id.login_setting);
		notifiSettingBtn = (RelativeLayout) findViewById(R.id.notifi_setting);
		gatewaySettingBtn = (RelativeLayout) findViewById(R.id.sms_net_setting_btn);
        sms_forward_setting_btn = (RelativeLayout) findViewById(R.id.sms_forward_setting_btn);
		telephoneSetting = (RelativeLayout) findViewById(R.id.telephone_setting_btn);
		detail_setting_btn = (RelativeLayout) findViewById(R.id.detail_setting_btn);

		backBtn = (Button) findViewById(R.id.backBtn);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
			}
		});

		loginSettingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MoreSetting.this, OASetting.class);
				startActivity(intent);
			}
		});

		notifiSettingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MoreSetting.this,
						SmsServerSetting.class);
				startActivity(intent);
			}
		});

		gatewaySettingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MoreSetting.this,
						SmsGatewatSetting.class);
				startActivity(intent);
			}
		});

		telephoneSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MoreSetting.this,
						TelephoneNumberSetting.class);
				startActivity(intent);
			}
		});
        sms_forward_setting_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreSetting.this,
                        SmsForwardSetting.class);
                startActivity(intent);
            }
        });
		detail_setting_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MoreSetting.this,
						DetailServerSetting.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
	}
}
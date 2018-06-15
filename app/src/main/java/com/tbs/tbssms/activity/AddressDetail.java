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
import android.widget.EditText;
import android.widget.TextView;

import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;

public class AddressDetail extends Activity implements OnClickListener {

	Button backBtn;
	Button sendBtn;
	TextView nameTilte;
	TextView nameTv;
	TextView phoneTv;
	Intent intent;
	String name;
	String phone;

	BroadcastReceiver MyBroadCastReceiver;
	// private Button mRightBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_detail_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
		// get intent value
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			name = intent.getStringExtra("name");
			phone = intent.getStringExtra("phone");
		}
		
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);
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
		
		backBtn = (Button) findViewById(R.id.backBtn);
		sendBtn = (Button) findViewById(R.id.sendBtn);
		nameTilte = (TextView) findViewById(R.id.textview);
		nameTv = (TextView) findViewById(R.id.name_value_tv);
		phoneTv = (TextView) findViewById(R.id.phone_value_tv);
		backBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);
		
		nameTilte.setText(name);
		nameTv.setText(name);
		phoneTv.setText(phone);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
		unregisterReceiver(MyBroadCastReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			//overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
			break;

		case R.id.sendBtn:
			Intent intent = new Intent(AddressDetail.this, ChatMsg.class);
			intent.putExtra("name", name);
			intent.putExtra("phone", phone);
			startActivity(intent);
			overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;

		default:
			break;
		}
	}

	public void sendMsg() {

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			//overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
		}
		return true;
	}
}

package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.dao.Dao;
import com.tbs.tbssms.database.ADDRESS_BOX;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.entity.AddressEntity;

public class AddAddress extends Activity {

	protected static final String TAG = "AddAddress";
	private final Context mcontext = this;

	private BroadcastReceiver MyBroadCastReceiver;// 广播
	private Intent intent;// intent意图
	private Button backBtn, addBtn;

	private EditText xingET;
	private EditText mingET;
	private EditText phoneET;
	private EditText emailET;

	public static Uri uri;
	public String xing;
	public String ming;
	public String phone;
	public String email;
	// public long contact_id;
	private Dao dbUtil = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_add_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
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

		// 上一层activity传过来的数据，通过getintent进行接收
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			phone = intent.getStringExtra("phone");
		}

		dbUtil = Dao.getInstance(mcontext);

		backBtn = (Button) findViewById(R.id.backBtn);
		addBtn = (Button) findViewById(R.id.addbtn);

		xingET = (EditText) findViewById(R.id.xing);
		mingET = (EditText) findViewById(R.id.ming);
		phoneET = (EditText) findViewById(R.id.phone);
		emailET = (EditText) findViewById(R.id.email);

		if (phone != null && !phone.equals("")) {
			phoneET.setText(phone);
			backBtn.setText("返回");
		}

		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				xing = xingET.getText().toString();
				ming = mingET.getText().toString();
				phone = phoneET.getText().toString();
				email = emailET.getText().toString();

				if (xing != null && !xing.equals("") || ming != null
						&& !ming.equals("")) {
					// long id = setCursor(xing + ming, phone, email);
					// Constants.contact_id = contact_id;
					// Constants.xing = xing;
					// Constants.ming = ming;
					// Constants.phone = phone;

					addAddress();

					Log.d(TAG, "name:" + xing + ming + " phone:" + phone);

					AddressEntity address = new AddressEntity();
					address.setName(xing + ming);
					address.setPhone(phone);
					address.setEmail(email);

					long id = dbUtil.insertAddressInfo(address);
					if (id != -1) {
						address.setRaw_contact_id(id);
						Intent refreshAddress = new Intent(
								Constants.REFRESH_ADDRESS);
						refreshAddress.putExtra("address", address);
						mcontext.sendBroadcast(refreshAddress);

						finish();
					} else {
						Toast.makeText(mcontext, "当前手机号已存在,不能重复添加",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	// 设置标题栏右侧按钮的作用
	public void btnmainright(View v) {
		finish();
		overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
	}

	private void addAddress() {// 添加通讯录方法
		xing = xingET.getText().toString();
		ming = mingET.getText().toString();
		phone = phoneET.getText().toString();
		email = emailET.getText().toString();
	}

	public long setCursor(String name, String phone, String email) {// 添加通讯录
		long resoult = 0;
		// 插入raw_contacts表，并获取_id属性
		// Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		// ContentResolver resolver = getContentResolver();
		ContentValues values = new ContentValues();
		// contact_id = ContentUris.parseId(resolver.insert(uri, values));
		// 插入data表
		// uri = Uri.parse("content://com.android.contacts/data");
		// add Name
		// values.put("raw_contact_id", contact_id);
		// values.put(Data.MIMETYPE, "vnd.android.cursor.item/name");
		// values.put("data2", name);
		// values.put("data1", name);
		// resolver.insert(uri, values);
		// values.clear();
		// add Phone
		// values.put("raw_contact_id", contact_id);
		// values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
		// values.put("data2", "2"); // 手机
		// values.put("data1", phone);
		// resolver.insert(uri, values);
		// values.clear();
		// add email
		// values.put("raw_contact_id", contact_id);
		// values.put(Data.MIMETYPE, "vnd.android.cursor.item/email_v2");
		// values.put("data2", "1"); // 单位
		// values.put("data1", email);
		// Uri resoultUri = resolver.insert(uri, values);
		// if(resoultUri != null){
		// values.clear();
		// values.put("raw_contact_id", contact_id);
		values.put("name", name);
		values.put("mobel_number", phone);
		values.put("email", email);
		resoult = new DataBaseUtil(this).getDataBase().insert(
				ADDRESS_BOX.TABLE_NAME, null, values);
		// }
		return resoult;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
		unregisterReceiver(MyBroadCastReceiver);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 获取
			finish();
			overridePendingTransition(R.anim.push_in2, R.anim.push_out2);
		}
		return true;
	}
}

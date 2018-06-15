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
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.database.ADDRESS_BOX;
import com.tbs.tbssms.database.DataBaseUtil;

public class UpdateAddress extends Activity {

	protected static final String TAG = "AddAddress";
	private BroadcastReceiver MyBroadCastReceiver;// 广播
	private Intent intent;// intent意图
	private Button backBtn, addBtn;

	private EditText xingET;
	private EditText mingET;
	private EditText phoneET;
	private EditText emailET;

	public static Uri uri;
	public static String name;
	public static String xing;
	public static String ming;
	public static String phone;
	public static String email;
	public static long contact_id;
	public static int position;

	private TextView title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_add_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {

		// get intent value
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			name = intent.getStringExtra("name");
			phone = intent.getStringExtra("mobel_number");
			email = intent.getStringExtra("email");
			contact_id = intent.getIntExtra("raw_contact_id", 0);
			position = intent.getIntExtra("position", 0);
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
		addBtn = (Button) findViewById(R.id.addbtn);

		xingET = (EditText) findViewById(R.id.xing);
		mingET = (EditText) findViewById(R.id.ming);
		phoneET = (EditText) findViewById(R.id.phone);
		emailET = (EditText) findViewById(R.id.email);

		title = (TextView) findViewById(R.id.textview);

		title.setText("编辑");

		xingET.setText(name);
		phoneET.setText(phone);
		emailET.setText(email);

		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addAddress();// 获得内容
				Log.d(TAG, "content :" + xing + ";" + ming + ";" + phone + ";"
						+ email);
				if (xing != null && !xing.equals("") || ming != null
						&& !ming.equals("")) {// 修改该内容
					finish();
					String result = "";
					if (testUpdate() > 0) {
						Constants.arrays[position].setName(xing + ming);
						Constants.arrays[position].setPhone(phone);
						Constants.arrays[position].setEmail(email);
						sendBroadcast(new Intent(
								Constants.REFRESH_UPDATE_ADDRESS));
						result = getResources().getString(
								R.string.update_address_success);
					} else {
						result = getResources().getString(
								R.string.update_address_error);
					}
					Toast.makeText(UpdateAddress.this, result,
							Toast.LENGTH_SHORT).show();
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

	public int testUpdate() {
		ContentValues values = new ContentValues();
		values.put("name", xing + ming);
		values.put("mobel_number", phone);
		values.put("email", email);
		int resoult = new DataBaseUtil(this).getDataBase().update(
				ADDRESS_BOX.TABLE_NAME, values, "mobel_number = ?",
				new String[] { phone });
		// if(resoult > 0){
		// Uri uri =
		// Uri.parse("content://com.android.contacts/data");//对data表的所有数据操作
		// ContentResolver resolver = getContentResolver();
		// // add Name
		// values.clear();
		// values.put("data1", xing+ming);
		// resolver.update(uri, values, "mimetype=? and raw_contact_id=?", new
		// String[]{"vnd.android.cursor.item/name",""+contact_id});
		// values.clear();
		// // add Phone
		// values.put("data1", phone);
		// resolver.update(uri, values, "mimetype=? and raw_contact_id=?", new
		// String[]{"vnd.android.cursor.item/phone_v2",""+contact_id});
		// values.clear();
		// // add email
		// values.put("data1", email);
		// resolver.update(uri, values, "mimetype=? and raw_contact_id=?", new
		// String[]{"vnd.android.cursor.item/email_v2",""+contact_id});
		// return true;
		// }else{
		// return false;
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

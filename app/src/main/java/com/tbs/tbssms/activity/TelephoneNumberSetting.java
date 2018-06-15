package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;

public class TelephoneNumberSetting extends Activity {

	private static final String TAG = "TelephoneNumberSetting";
	private final Context mContext = this;
	private IniFile m_iniFileIO = null;
	private String telephoneNum = null;

	// private EditText emailET;
	// private EditText pswET;
	// private EditText smtpET;

	// private ExpandableListView expandable;// 树形菜单控件

	private String nodeValue = "";
	private String ipValue = "";
	private String portValue = "";

	private Button backBtn;
	private Button commitBtn;

	private EditText ims_email_setting = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_telephone_num_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
		m_iniFileIO = new IniFile();
		ims_email_setting = (EditText) findViewById(R.id.news_psw_setting);

		backBtn = (Button) findViewById(R.id.backBtn);
		commitBtn = (Button) findViewById(R.id.addbtn);
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				writeValue(telephoneNum);
				Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
				finish();
			}
		});

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		telephoneNum = readValue();

		ims_email_setting.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				telephoneNum = s.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

		ims_email_setting.setText(telephoneNum);
	}

	public String readValue() {
		String telephoneNum = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.TELEPHONESETTING,
				Constants.TELEPHONENUMBER, "", (byte) 0);
		return telephoneNum;
	}

	public void writeValue(String telephoneNum) {
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.TELEPHONESETTING, Constants.TELEPHONENUMBER,
				telephoneNum);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
	}
}
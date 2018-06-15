package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;

public class OASetting extends Activity {

	private final Context mContext = this;

	private EditText userNameET;
	private EditText pswET;
	private EditText urlET;
	private EditText portET;
	private Button backBtn;
	private Button commitBtn;

	private String username;
	private String password;
	private String url;
	private String port;
	private CheckBox checkbox;

	private IniFile m_iniFileIO = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_oa_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {

		m_iniFileIO = new IniFile();

		username = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.LOGININFO, Constants.USERNAME,
				"", (byte) 0);
		password = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.LOGININFO, Constants.PASSWORD,
				"", (byte) 0);
		url = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.LOGININFO, Constants.LOGINURL,
				"", (byte) 0);
		port = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.LOGININFO, Constants.LOGINPORT,
				"", (byte) 0);

		userNameET = (EditText) findViewById(R.id.login_setting);
		pswET = (EditText) findViewById(R.id.psw_setting);
		urlET = (EditText) findViewById(R.id.url_setting);
		portET = (EditText) findViewById(R.id.port_setting);

		backBtn = (Button) findViewById(R.id.backBtn);
		commitBtn = (Button) findViewById(R.id.addbtn);
		checkbox = (CheckBox) findViewById(R.id.toggleButton);

		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveInfo(userNameET.getText().toString(), pswET.getText()
						.toString(), urlET.getText().toString(), portET
						.getText().toString());
				Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
				finish();
			}
		});

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		String autoState = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.AUTOSTART,
				Constants.OA_AUTOSTART, "", (byte) 0);
		if (autoState != null && autoState.equals("false")) {
			checkbox.setChecked(false);
		} else if (autoState != null && autoState.equals("true")) {
			checkbox.setChecked(true);
		}

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				m_iniFileIO.writeIniString(Constants.EXTERNALPATH
						+ Constants.INIURL, Constants.AUTOSTART,
						Constants.OA_AUTOSTART, "" + isChecked);
			}
		});

		userNameET.setText(username);
		pswET.setText(password);
		urlET.setText(url);
		portET.setText(port);
	}

	public void saveInfo(String userName, String password, String url,
			String port) {
		// 获得编辑器
		// SharedPreferences.Editor editor =
		// getSharedPreferences(Constants.SHARED_PREFERENCE_FIRST_LAUNCH,
		// MODE_PRIVATE).edit();
		// 将EditText文本内容添加到编辑器
		// editor.putString("username", userName);
		// editor.putString("password", password);
		// editor.putString("url", url);
		// 提交编辑器内容
		// editor.commit();
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.LOGININFO, Constants.USERNAME, userName);
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.LOGININFO, Constants.PASSWORD, password);
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.LOGININFO, Constants.LOGINURL, url);
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.LOGININFO, Constants.LOGINPORT, port);
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
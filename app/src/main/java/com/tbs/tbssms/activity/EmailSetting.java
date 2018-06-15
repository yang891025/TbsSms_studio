package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

public class EmailSetting extends Activity {

	private final Context mContext = this;

	private EditText emailET;
	private EditText pswET;
	private EditText smtpET;
	private EditText popET;

	private String email;
	private String psw;
	private String smtp;
	// private String pop;

	private Button backBtn;
	private Button commitBtn;

	private IniFile m_iniFileIO = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_notify_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
		m_iniFileIO = new IniFile();

		// File webappFile = new File(Constants.SHARED_PREFERENCE_LAUNCHPATH);
		// if ((webappFile).exists()) {
		// 实例化sharepreferences
		// SharedPreferences setting =
		// getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,MODE_PRIVATE);
		// email = setting.getString("xmppHost", "");
		// psw = setting.getString("xmppPort", "");
		// smtp = setting.getString("apiKey", "");
		// pop = setting.getString("pop", "");
		// }

		email = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.NOTIFYINFO, Constants.XMPPHOST,
				"", (byte) 0);
		psw = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.NOTIFYINFO, Constants.XMPPPORT,
				"", (byte) 0);
		smtp = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.NOTIFYINFO, Constants.XMPPAPI,
				"", (byte) 0);

		emailET = (EditText) findViewById(R.id.email_setting);
		pswET = (EditText) findViewById(R.id.psw_setting);
		smtpET = (EditText) findViewById(R.id.smtp_setting);
		popET = (EditText) findViewById(R.id.pop_setting);

		backBtn = (Button) findViewById(R.id.backBtn);
		commitBtn = (Button) findViewById(R.id.addbtn);

		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveInfo(emailET.getText().toString(), pswET.getText()
						.toString(), smtpET.getText().toString());// ,popET.getText().toString()
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

		emailET.setText(email);
		pswET.setText(psw);
		smtpET.setText(smtp);
		// popET.setText(pop);
	}

	public void saveInfo(String email, String psw, String smtp) {// ,String pop
		// 获得编辑器
		// SharedPreferences.Editor editor =
		// getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
		// MODE_PRIVATE).edit();
		// 将EditText文本内容添加到编辑器
		// editor.putString("xmppHost", email);
		// editor.putString("xmppPort", psw);
		// editor.putString("apiKey", smtp);
		// editor.putString("pop", pop);
		// 提交编辑器内容
		// editor.commit();

		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.NOTIFYINFO, Constants.XMPPHOST, email);
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.NOTIFYINFO, Constants.XMPPPORT, psw);
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.NOTIFYINFO, Constants.XMPPAPI, smtp);
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
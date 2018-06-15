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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.entity.ExpChirdrenEntity;

public class SmsServerSetting extends Activity {

	private static final String TAG = "SmsServerSetting";
	private final Context mContext = this;
	private IniFile m_iniFileIO = null;
	private ExpChirdrenEntity children = null;

	// private EditText emailET;
	// private EditText pswET;
	// private EditText smtpET;

	// private ExpandableListView expandable;// 树形菜单控件

	private String nodeValue = "";
	private String ipValue = "";
	private String portValue = "";

	private Button backBtn;
	private Button commitBtn;

	private LinearLayout tool_button_ims = null;
	private ImageView tool_arrows_ims = null;
	private LinearLayout ims_setting = null;
	private EditText ims_email_setting = null;
	private EditText ims_psw_setting = null;
	private EditText ims_smtp_setting = null;
	private LinearLayout tool_button_news = null;
	private ImageView tool_arrows_news = null;
	private LinearLayout news_setting = null;
	private EditText news_email_setting = null;
	private EditText news_psw_setting = null;
	private EditText news_smtp_setting = null;
	private CheckBox checkbox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_ims_server_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
		m_iniFileIO = new IniFile();

		// expandable = (ExpandableListView)
		// findViewById(R.id.expandableListView);
		// emailET = (EditText) findViewById(R.id.email_setting);
		// pswET = (EditText) findViewById(R.id.psw_setting);
		// smtpET = (EditText) findViewById(R.id.smtp_setting);
		tool_button_ims = (LinearLayout) findViewById(R.id.tool_button_ims);
		tool_arrows_ims = (ImageView) findViewById(R.id.tool_arrows_ims);
		ims_setting = (LinearLayout) findViewById(R.id.ims_setting);
		ims_email_setting = (EditText) findViewById(R.id.ims_email_setting);
		ims_psw_setting = (EditText) findViewById(R.id.ims_psw_setting);
		ims_smtp_setting = (EditText) findViewById(R.id.ims_smtp_setting);
		tool_button_news = (LinearLayout) findViewById(R.id.tool_button_news);
		tool_arrows_news = (ImageView) findViewById(R.id.tool_arrows_news);
		news_setting = (LinearLayout) findViewById(R.id.news_setting);
		news_email_setting = (EditText) findViewById(R.id.news_email_setting);
		news_psw_setting = (EditText) findViewById(R.id.news_psw_setting);
		news_smtp_setting = (EditText) findViewById(R.id.news_smtp_setting);
		checkbox = (CheckBox) findViewById(R.id.toggleButton);

		// 去掉默认的箭头
		// expandable.setGroupIndicator(null);

		backBtn = (Button) findViewById(R.id.backBtn);
		commitBtn = (Button) findViewById(R.id.addbtn);

		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				writeValue(children);
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

		tool_button_ims.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ims_setting.getVisibility() == View.VISIBLE) {
					tool_arrows_ims.setImageResource(R.drawable.xsj);
					ims_setting.setVisibility(View.GONE);
				} else {
					tool_arrows_ims.setImageResource(R.drawable.xsj_down);
					ims_setting.setVisibility(View.VISIBLE);
				}
			}
		});

		tool_button_news.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (news_setting.getVisibility() == View.VISIBLE) {
					tool_arrows_news.setImageResource(R.drawable.xsj);
					news_setting.setVisibility(View.GONE);
				} else {
					tool_arrows_news.setImageResource(R.drawable.xsj_down);
					news_setting.setVisibility(View.VISIBLE);
				}
			}
		});

		children = readValue();

		ims_psw_setting.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				children.setIp(s.toString());
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

		ims_smtp_setting.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				children.setPort(s.toString());
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

		String autoState = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.AUTOSTART,
				Constants.IMS_MIS_AUTOSTART, "false", (byte) 0);
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
						Constants.IMS_MIS_AUTOSTART, "" + isChecked);
			}
		});

		// adapter = new ExpandableAdapter(mContext, node, childrenList);
		// expandable.setAdapter(adapter);
		// emailET.setText(nodeValue);
		ims_psw_setting.setText(children.getIp());
		ims_smtp_setting.setText(children.getPort());
	}

	public ExpChirdrenEntity readValue() {
		ExpChirdrenEntity children = new ExpChirdrenEntity();
		String ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.IMSMISSERVER,
				Constants.IMSMISSERVERIP, "", (byte) 0);
		String port = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.IMSMISSERVER,
				Constants.IMSMISSERVERPORT, "", (byte) 0);
		children.setIp(ip);
		children.setPort(port);
		return children;
	}

	public void writeValue(ExpChirdrenEntity children) {
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.IMSMISSERVER, Constants.IMSMISSERVERIP,
				children.getIp());
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.IMSMISSERVER, Constants.IMSMISSERVERPORT,
				children.getPort());
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
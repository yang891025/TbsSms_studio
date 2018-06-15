package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.entity.DBKGatewayEntity;

public class SmsGatewatSetting extends Activity {

	private static final String TAG = "SmsGatewatSetting";
	private final Context mContext = this;
	private IniFile m_iniFileIO = null;

	private Button backBtn;
	private Button commitBtn;
	private EditText zhuceET;
	private EditText chzmET;
	private EditText xgmmET;
	private EditText dbkjsET;
	private EditText qubyeET;
	private EditText qbczET;
	private EditText cxqbzdET;
	private EditText httpUrlET;
	private EditText httpPortET;

	private CheckBox checkbox;

	private DBKGatewayEntity Gateway = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_dbk_gateway_layout);

		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
		m_iniFileIO = new IniFile();

		checkbox = (CheckBox) findViewById(R.id.toggleButton);

		zhuceET = (EditText) findViewById(R.id.sms_gateway_regist);
		chzmET = (EditText) findViewById(R.id.sms_gateway_detail_userinfo);
		xgmmET = (EditText) findViewById(R.id.sms_gateway_update_userinfo);
		dbkjsET = (EditText) findViewById(R.id.sms_gateway_search_dbk);
		qubyeET = (EditText) findViewById(R.id.sms_gateway_detail_qb);
		qbczET = (EditText) findViewById(R.id.sms_gateway_input_qb);
		cxqbzdET = (EditText) findViewById(R.id.sms_gateway_detail_qb_zd);
		httpUrlET = (EditText) findViewById(R.id.sms_gateway_http_url);
		httpPortET = (EditText) findViewById(R.id.sms_gateway_http_port);

		backBtn = (Button) findViewById(R.id.backBtn);
		commitBtn = (Button) findViewById(R.id.addbtn);
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				writeValue(Gateway);
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

		Gateway = readValue();

		String autoState = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.AUTOSTART,
				Constants.DBK_AUTOSTART, "", (byte) 0);
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
						Constants.DBK_AUTOSTART, "" + isChecked);
			}
		});

		zhuceET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Gateway.setZc(s.toString());
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

		chzmET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Gateway.setCxzm(s.toString());
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

		xgmmET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Gateway.setXgmm(s.toString());
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

		dbkjsET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Gateway.setDbkjs(s.toString());
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

		qbczET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Gateway.setQbcz(s.toString());
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

		qubyeET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Gateway.setQubye(s.toString());
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

		cxqbzdET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Gateway.setCxqbzd(s.toString());
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

		httpUrlET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Gateway.setHttpUrl(s.toString());
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

		httpPortET.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Gateway.setHttpPort(s.toString());
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

		zhuceET.setText(Gateway.getZc());
		chzmET.setText(Gateway.getCxzm());
		xgmmET.setText(Gateway.getXgmm());
		dbkjsET.setText(Gateway.getDbkjs());
		cxqbzdET.setText(Gateway.getCxqbzd());
		qubyeET.setText(Gateway.getQubye());
		qbczET.setText(Gateway.getQbcz());
		httpUrlET.setText(Gateway.getHttpUrl());
		httpPortET.setText(Gateway.getHttpPort());
	}

	public DBKGatewayEntity readValue() {
		DBKGatewayEntity Gateway = new DBKGatewayEntity();
		String zc = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.SMSNETSET,
				Constants.RegistAccount, "", (byte) 0);
		String cxzm = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.SMSNETSET,
				Constants.QueryAccountPassword, "", (byte) 0);
		String xgmm = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.SMSNETSET,
				Constants.UpdatePassword, "", (byte) 0);
		String dbkjs = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.SMSNETSET, Constants.SearchDbk,
				"", (byte) 0);
		String qubye = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.SMSNETSET,
				Constants.QueryBalance, "", (byte) 0);
		String qbcz = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.SMSNETSET, Constants.WalletTopUp,
				"", (byte) 0);
		String cxqbzd = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.SMSNETSET,
				Constants.QueryBalanceBill, "", (byte) 0);
		String httpUrl = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.SMSGATEWAYURL,
				Constants.SMSGATEWAYIP, "", (byte) 0);
		String httpPort = m_iniFileIO.getIniString(Constants.EXTERNALPATH
				+ Constants.INIURL, Constants.SMSGATEWAYURL,
				Constants.SMSGATEWAYPORT, "", (byte) 0);

		Log.d(TAG, "httpUrl:" + httpUrl);
		Log.d(TAG, "httpPort:" + httpPort);

		Gateway.setZc(zc);
		Gateway.setCxqbzd(cxqbzd);
		Gateway.setCxzm(cxzm);
		Gateway.setDbkjs(dbkjs);
		Gateway.setQbcz(qbcz);
		Gateway.setQubye(qubye);
		Gateway.setXgmm(xgmm);
		Gateway.setHttpUrl(httpUrl);
		Gateway.setHttpPort(httpPort);
		return Gateway;
	}

	public void writeValue(DBKGatewayEntity Gateway) {
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.SMSNETSET, Constants.RegistAccount, Gateway.getZc());
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.SMSNETSET, Constants.QueryAccountPassword,
				Gateway.getCxzm());
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.SMSNETSET, Constants.UpdatePassword,
				Gateway.getXgmm());
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.SMSNETSET, Constants.SearchDbk, Gateway.getDbkjs());
		m_iniFileIO
				.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
						Constants.SMSNETSET, Constants.QueryBalance,
						Gateway.getQubye());
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.SMSNETSET, Constants.WalletTopUp, Gateway.getQbcz());
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.SMSNETSET, Constants.QueryBalanceBill,
				Gateway.getCxqbzd());
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.SMSGATEWAYURL, Constants.SMSGATEWAYIP,
				Gateway.getHttpUrl());
		m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
				Constants.SMSGATEWAYURL, Constants.SMSGATEWAYPORT,
				Gateway.getHttpPort());
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
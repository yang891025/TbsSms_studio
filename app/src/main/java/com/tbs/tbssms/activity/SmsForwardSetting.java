package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;

/**
 * Created by TBS on 2017/7/19.
 */

public class SmsForwardSetting extends Activity
{

    private static final String TAG = "SmsForwardSetting";
    private final Context mContext = this;
    private IniFile m_iniFileIO = null;
    private Button backBtn;
    private Button commitBtn;
    private EditText sms_forward_mark;
    private CheckBox checkbox;
    private RelativeLayout tools_start_forward;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_sms_forward_layout);

        AppManager.getInstance().addActivity(this);
        init();
    }

    private void init() {
        m_iniFileIO = new IniFile();
        checkbox = (CheckBox) findViewById(R.id.toggleButton);
        sms_forward_mark = (EditText) findViewById(R.id.sms_forward_mark);
        tools_start_forward = (RelativeLayout) findViewById(R.id.tools_start_forward);
        backBtn = (Button) findViewById(R.id.backBtn);
        commitBtn = (Button) findViewById(R.id.addbtn);
        commitBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                m_iniFileIO.writeIniString(Constants.EXTERNALPATH + Constants.INIURL,
                        Constants.SMSNETSET, Constants.SmsForwarding,
                        sms_forward_mark.getText().toString());
                Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String autoState = m_iniFileIO.getIniString(Constants.EXTERNALPATH
                        + Constants.INIURL, Constants.AUTOSTART,
                Constants.SMS_FORWARDING, "true", (byte) 0);
        if (autoState != null && autoState.equals("false")) {
            checkbox.setChecked(false);
        } else if (autoState != null && autoState.equals("true")) {
            checkbox.setChecked(true);
        }

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                m_iniFileIO.writeIniString(Constants.EXTERNALPATH
                                + Constants.INIURL, Constants.AUTOSTART,
                        Constants.SMS_FORWARDING, "" + isChecked);
            }
        });
        sms_forward_mark.setText(m_iniFileIO.getIniString(Constants.EXTERNALPATH
                        + Constants.INIURL, Constants.SMSNETSET,
                Constants.SmsForwarding, "TBS:", (byte) 0));
        tools_start_forward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SmsForwardSetting.this, SmsForwardActivity.class);
                startActivity(intent);
            }
        });
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
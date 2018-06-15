package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tbs.tbssms.R;
import com.tbs.tbssms.util.SmsUtil;

/**
 * Created by TBS on 2017/10/17.
 */

public class SmsForwardActivity extends Activity
{
    // private MyDialog dialog;
    private LinearLayout layout;
    private EditText sms_content;
    private EditText sms_mark_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tool_sms_dialog);
        // dialog=new MyDialog(this);
        layout = (LinearLayout) findViewById(R.id.exit_layout);
        sms_content = (EditText) findViewById(R.id.sms_content);
        sms_mark_content = (EditText) findViewById(R.id.sms_mark_content);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void exitbutton(View v) {
        this.finish();
    }

    public void makebutton(View v) {
        SmsUtil smsUtil = new SmsUtil();
        String sms_content = this.sms_content.getText().toString();
        if(sms_content.isEmpty()){
            Toast.makeText(this,"短信码为空，无法生成",Toast.LENGTH_SHORT).show();
        }else {
            String reSms = smsUtil.SMSforwarding(sms_content);
            if (reSms != "") {
                if (Integer.parseInt(reSms) <= 0 || reSms.length() < 4) {
                    Toast.makeText(this,"请按照正确格式填写短信码",Toast.LENGTH_SHORT).show();
                } else {
                    int number = Integer.parseInt(reSms);
                    int number1 = number / 1000;
                    int number2 = (number - number1 * 1000) / 100;
                    int number3 = (number - number1 * 1000 - number2 * 100) / 10;
                    int number4 = number - number1 * 1000 - number2 * 100 - number3 * 10;
                    int code1 = number1 * 2 % 10;
                    int code2 = number2 * 3 % 10;
                    int code3 = number3 * 4 % 10;
                    int code4 = number4 * 5 % 10;
                    int code5 = number4 * 6 % 10;
                    int code6 = number4 * 7 % 10;
                    String code = String.valueOf(code1) + String.valueOf(code2) + String.valueOf(code3) +
                            String.valueOf(code4) + String.valueOf(code5) + String.valueOf(code6);
                    code = "【金信桥】您的TBS产品验证码是：" + code + ",如非本人操作，请忽略该短信。";
                    sms_mark_content.setText(code);
                }
            }else{
                Toast.makeText(this,"请按照正确格式填写短信码",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void copybutton(View v) {
        String sms_mark_content = this.sms_mark_content.getText().toString();
        if(sms_mark_content.isEmpty()){
            Toast.makeText(this,"短信验证码为空，不可复制",Toast.LENGTH_SHORT).show();
        }else{
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                cmb.setText(sms_mark_content);
            }else{
                // 获取剪贴板管理服务
                android.text.ClipboardManager cm = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本数据复制到剪贴板
                cm.setText(sms_mark_content);
            }
            Toast.makeText(this,"复制成功",Toast.LENGTH_SHORT).show();
        }
    }

}

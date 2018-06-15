package com.tbs.tbssms.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.activity.base.BaseActivity;
import com.tbs.tbssms.callback.DetailServerCallBack;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.util.HttpConnectionUtil;
import com.tbs.tbssms.util.HttpConnectionUtil.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailServerSetting extends BaseActivity
{

    private final Context mContext = this;

    private BroadcastReceiver MyBroadCastReceiver;// 广播

    private LinearLayout root = null;

    private Button backBtn;

    private HttpConnectionUtil connection = null;

    private String ip = null;
    private String port = null;
    private IniFile m_iniFileIO = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_detail_telephone_layout);
        AppManager.getInstance().addActivity(this);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        root = (LinearLayout) findViewById(R.id.linearLayout1);
        backBtn = (Button) findViewById(R.id.backBtn);
    }

    @Override
    protected void initView() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.REFRESH_DETAIL_SERVER);
        filter.addCategory("default");
        MyBroadCastReceiver = new BroadcastReceiver()
        {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constants.REFRESH_DETAIL_SERVER.equals(intent.getAction())) {
                    showProgressDialog(null);
                    ArrayList<String> result = (ArrayList<String>) intent.getSerializableExtra("result");
                    if (result != null && result.size() > 0) {
                        for (int i = 0; i < result.size(); i++) {
                            View view = getLayoutInflater().inflate(R.layout.settings_detail_telephone_item, null);
                            TextView tv1 = (TextView) view.findViewById(R.id.textView1);
                            TextView tv2 = (TextView) view.findViewById(R.id.textview);
                            int j = i + 1;
                            tv1.setText("第" + j + "个可用的服务手机号：");
                            tv2.setText(result.get(i));
                            root.addView(view);
                        }
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.settings_detail_telephone_item, null);
                        TextView tv1 = (TextView) view.findViewById(R.id.textView1);
                        TextView tv2 = (TextView) view.findViewById(R.id.textview);
                        tv1.setText("当前无可用服务手机号");
                        tv2.setVisibility(View.GONE);
                        root.addView(view);
                    }
                }
            }
        };
        registerReceiver(MyBroadCastReceiver, filter);

        connection = HttpConnectionUtil.newInstance();
        m_iniFileIO = new IniFile();

        backBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.IMSMISSERVER, Constants
                .IMSMISSERVERIP, "", (byte) 0);
        port = m_iniFileIO.getIniString(Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSGATEWAYURL, Constants
                .SMSGATEWAYPORT, "", (byte) 0);

        MyAsyncTask task = new MyAsyncTask(mContext);
        task.execute(ip, port);
    }

    /* (non-Javadoc)
     * @see com.tbs.tbssms.activity.base.BaseActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
        //if(connection != null){
        //connection.canThread();
        //}
        if (MyBroadCastReceiver != null) {
            unregisterReceiver(MyBroadCastReceiver);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }

    /*
     * 异步任务 后台任务前 后台任务中 后台任务结束
     *
     */
    class MyAsyncTask extends AsyncTask<String, Integer, Integer>
    {

        private Context context;

        private String ip = null;
        private String port = null;
        String httpConUrl = null;

        public MyAsyncTask(Context c) {
            this.context = c;
        }

        // 运行在UI线程中，在调用doInBackground()之前执行
        @Override
        protected void onPreExecute() {
            showProgressDialog(null);
        }

        // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
        @Override
        protected Integer doInBackground(String... params) {
            ip = params[0];
            port = params[1];

            if (ip != null && !ip.equals("")) {
                if (ip.indexOf("http://") > -1) {
                    httpConUrl = "";
                } else {
                    httpConUrl = "http://";
                }
                //if(ip.lastIndexOf(":") > -1){
                //httpConUrl += ip+port;
                //}else{
                httpConUrl += ip + ":" + port + "/EBS/UserSMSServlet";
                //}
            }

            // 创建请求体
            Map<String, String> httpParams = new HashMap<String, String>();
            httpParams.put("act", "getSMSMobileList");

            Log.d(TAG, "httpConUrl:" + httpConUrl);

            // 发送请求
            connection.asyncConnect(httpConUrl, httpParams, HttpMethod.POST, new DetailServerCallBack(root), context);
            return null;
        }
    }
}
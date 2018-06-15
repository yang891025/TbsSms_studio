package com.tbs.tbssms.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.callback.BankSmsCallBack;
import com.tbs.tbssms.callback.DbkSmsCallBack;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.myinterface.HttpCallback;
import com.tbs.tbssms.util.HttpConnectionUtil.HttpMethod;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsUtil
{

    private static final String TAG = "SmsUtil";

    private IniFile m_iniFileIO = null;
    private WenZi wenziUtil = null;
    private String RegistAccount = null;
    private String QueryAccountPassword = null;
    private String UpdatePassword = null;
    private String SearchDbk = null;
    private String QueryBalance = null;
    private String SMSforwarding = null;
    private String WalletTopUp = null;
    private String QueryBalanceBill = null;
    private HttpConnectionUtil connection = null;
    private MyHandlerThread handlerThread;
    private Handler handler;

    public SmsUtil() {
        m_iniFileIO = new IniFile();
        wenziUtil = new WenZi();
        connection = HttpConnectionUtil.newInstance();
        handlerThread = new MyHandlerThread("myHanler");
        handlerThread.start();
        // 注意：
        // 这里必须用到handler的这个构造器，因为需要把callback传进去，从而使自己的HandlerThread的handlerMessage来替换掉Handler原生的handlerThread
        handler = new Handler(handlerThread.getLooper(), handlerThread);

        RegistAccount = wenziUtil.QtoB(m_iniFileIO.getIniString(
                Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSNETSET,
                Constants.RegistAccount, "", (byte) 0));
        QueryAccountPassword = wenziUtil.QtoB(m_iniFileIO.getIniString(
                Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSNETSET,
                Constants.QueryAccountPassword, "", (byte) 0));
        UpdatePassword = wenziUtil.QtoB(m_iniFileIO.getIniString(
                Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSNETSET,
                Constants.UpdatePassword, "", (byte) 0));
        SearchDbk = wenziUtil.QtoB(m_iniFileIO.getIniString(
                Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSNETSET,
                Constants.SearchDbk, "", (byte) 0));
        QueryBalance = wenziUtil.QtoB(m_iniFileIO.getIniString(
                Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSNETSET,
                Constants.QueryBalance, "", (byte) 0));
        WalletTopUp = wenziUtil.QtoB(m_iniFileIO.getIniString(
                Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSNETSET,
                Constants.WalletTopUp, "", (byte) 0));
        SMSforwarding = wenziUtil.QtoB(m_iniFileIO.getIniString(
                Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSNETSET,
                Constants.SmsForwarding, "TBS:", (byte) 0));
        QueryBalanceBill = wenziUtil.QtoB(m_iniFileIO.getIniString(
                Constants.EXTERNALPATH + Constants.INIURL, Constants.SMSNETSET,
                Constants.QueryBalanceBill, "", (byte) 0));
    }


    public String SMSforwarding(String msg) {
        String result = "";
        String bMsg = wenziUtil.QtoB(msg);
        if (bMsg.toLowerCase().indexOf(SMSforwarding.toLowerCase(), 0) != -1) {
            result = Pattern.compile("[^0-9]").matcher(bMsg).replaceAll("");
            //result = bMsg.replace(SMSforwarding, "");
        } else {
            if (bMsg.length() == 4) {
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isNum = pattern.matcher(bMsg);
                if (isNum.matches()) {
                    result = bMsg;
                } else {
                    result = "";
                }
            } else {
                result = "";
            }
        }
        return result.trim();
    }

    public boolean queryMsg(String msg) {
        boolean result = false;
        String bMsg = wenziUtil.QtoB(msg);
        Log.d(TAG, "cutStr bMsg: " + bMsg);
        if (bMsg.indexOf(wenziUtil.QtoB(RegistAccount), 0) != -1) {
            result = true;
        } else if (bMsg.indexOf(QueryAccountPassword, 0) != -1) {
            result = true;
        } else if (bMsg.indexOf(UpdatePassword, 0) != -1) {
            result = true;
        } else if (bMsg.indexOf(SearchDbk, 0) != -1) {
            result = true;
        } else if (bMsg.indexOf(QueryBalance, 0) != -1) {
            result = true;
        } else if (bMsg.indexOf(WalletTopUp, 0) != -1) {
            result = true;
        } else if (bMsg.indexOf(QueryBalanceBill, 0) != -1) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @SuppressLint("NewApi")
    public String cutStr(Context mContext, String phone, String msg, String url) {
        String result = null;
        String bMsg = wenziUtil.QtoB(msg);
        result = bMsg.replace(RegistAccount, "");
        if (bMsg.indexOf(RegistAccount, 0) != -1) {
            result = bMsg.replace(RegistAccount, "");
            Map<String, String> params = new HashMap<String, String>();
            params.put("act", "register");
            params.put("mobile", phone);
            params.put("userCode", result);

            handlerThread.setCallback(new DbkSmsCallBack());
            handlerThread.setMethod(HttpMethod.POST);
            handlerThread.setmContext(mContext);
            handlerThread.setURL(url + Constants.DBKCONNECTIONURL);
            handlerThread.setPhone(phone);
            handlerThread.setParams(params);
            handler.sendEmptyMessage(1);
        } else if (bMsg.indexOf(QueryAccountPassword, 0) != -1) {
            result = bMsg.replace(QueryAccountPassword, "");
            Map<String, String> params = new HashMap<String, String>();
            params.put("act", "getUser");
            params.put("mobile", phone);

            handlerThread.setCallback(new DbkSmsCallBack());
            handlerThread.setMethod(HttpMethod.POST);
            handlerThread.setmContext(mContext);
            handlerThread.setURL(url + Constants.DBKCONNECTIONURL);
            handlerThread.setPhone(phone);
            handlerThread.setParams(params);
            handler.sendEmptyMessage(1);
        } else if (bMsg.indexOf(UpdatePassword, 0) != -1) {
            result = bMsg.replace(UpdatePassword, "");
            Map<String, String> params = new HashMap<String, String>();
            params.put("act", "modifyPassword");
            params.put("mobile", phone);
            params.put("newPassword", result);

            handlerThread.setCallback(new DbkSmsCallBack());
            handlerThread.setMethod(HttpMethod.POST);
            handlerThread.setmContext(mContext);
            handlerThread.setURL(url + Constants.DBKCONNECTIONURL);
            handlerThread.setPhone(phone);
            handlerThread.setParams(params);
            handler.sendEmptyMessage(1);
        } else if (bMsg.indexOf(SearchDbk, 0) != -1) {
            result = bMsg.replace(SearchDbk, "");
            Map<String, String> params = new HashMap<String, String>();
            params.put("act", "getDBKTemp");
            params.put("mobile", phone);
            params.put("smword", result);

            handlerThread.setCallback(new DbkSmsCallBack());
            handlerThread.setMethod(HttpMethod.POST);
            handlerThread.setmContext(mContext);
            handlerThread.setURL(url + Constants.DBKSEARCHURL);
            handlerThread.setPhone(phone);
            handlerThread.setParams(params);
            handler.sendEmptyMessage(1);
        }
        // else if (bMsg.indexOf(QueryBalance, 0) != -1) {
        // result = bMsg.replace(QueryBalance, "");
        // Map<String, String> params = new HashMap<String, String>();
        // params.put("act", "register");
        // params.put("mobile", phone);
        // params.put("userCode", result);
        //
        // handlerThread.setCallback(new MyRegistSmsCallBack());
        // handlerThread.setMethod(HttpMethod.POST);
        // handlerThread.setmContext(mContext);
        // handlerThread.setURL(url + Constants.DBKCONNECTIONURL);
        // handlerThread.setPhone(phone);
        // handlerThread.setParams(params);
        // handler.sendEmptyMessage(1);
        // }
        else if (bMsg.indexOf(WalletTopUp, 0) != -1) {
            result = bMsg.replace(WalletTopUp, "");
            String md5Str;
            Map<String, String> params = new HashMap<String, String>();
            try {
                md5Str = Util.getMD5(result + "0" + phone + phone
                        + "aE3Dd4ej6KLm56uOj0yt");
                params.put("md5Str", md5Str);
                params.put("act", "depositBySMS");
                params.put("smsMobile", phone);
                params.put("money", result);
                params.put("moneyType", "0");
                params.put("account", phone);
                params.put("remark1", result);
                params.put("remark2", "");

                handlerThread.setCallback(new BankSmsCallBack());
                handlerThread.setMethod(HttpMethod.POST);
                handlerThread.setmContext(mContext);
                handlerThread.setURL(url + Constants.BankTransferURL);
                handlerThread.setPhone(phone);
                handlerThread.setParams(params);
                handler.sendEmptyMessage(1);
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        // else if (bMsg.indexOf(QueryBalanceBill, 0) != -1) {
        // result = bMsg.replace(QueryBalanceBill, "");
        // Map<String, String> params = new HashMap<String, String>();
        // params.put("act", "register");
        // params.put("mobile", phone);
        // params.put("userCode", result);
        //
        // handlerThread.setCallback(new MyRegistSmsCallBack());
        // handlerThread.setMethod(HttpMethod.POST);
        // handlerThread.setmContext(mContext);
        // handlerThread.setURL(url + Constants.DBKCONNECTIONURL);
        // handlerThread.setPhone(phone);
        // handlerThread.setParams(params);
        // handler.sendEmptyMessage(1);
        // //connection.asyncConnect(url + "/EBS/UserSMSServlet", params,
        // HttpMethod.POST, new MyRegistSmsCallBack(), mContext, phone);
        // }
        else {
            return null;
        }
        return result;
    }

    private class MyHandlerThread extends HandlerThread implements Callback
    {

        String URL;
        Map<String, String> params;
        HttpMethod method;
        HttpCallback callback;
        Context mContext;
        String phone;

        public MyHandlerThread(String name) {
            super(name);
        }

        @Override
        public boolean handleMessage(Message msg) {
            connection.asyncConnect(URL, params, method, callback, mContext,
                    phone);
            return true;
        }

        public String getURL() {
            return URL;
        }

        public void setURL(String uRL) {
            URL = uRL;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        public HttpCallback getCallback() {
            return callback;
        }

        public void setCallback(HttpCallback callback) {
            this.callback = callback;
        }

        public Context getmContext() {
            return mContext;
        }

        public void setmContext(Context mContext) {
            this.mContext = mContext;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
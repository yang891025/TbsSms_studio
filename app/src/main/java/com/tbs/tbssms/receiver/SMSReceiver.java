package com.tbs.tbssms.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.telephony.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.RemoteViews;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.activity.LaunchActivity;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.dao.Dao;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.database.MESSAGE_BOX;
import com.tbs.tbssms.database.RETURN_BUFFER;
import com.tbs.tbssms.entity.AddressEntity;
import com.tbs.tbssms.entity.MessageEntity;
import com.tbs.tbssms.util.SmsUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SMSReceiver extends BroadcastReceiver
{

    private static final String TAG = "SMSReceiver";

    private NotificationManager mNotificationManager = null;
    private Notification notification = null;
    private RemoteViews remoteviews = null;

    private IniFile m_iniFileIO = null;
    private Dao dbUtil = null;

    private String sendTime = null;
    private String phone = null;
    private String name = null;
    private String msg = null;
    private String username = null;
    private String password = null;
    private String url = null;
    private String ip = null;
    private String port = null;
    private String smsUrl = null;
    private String smsPort = null;
    private SmsUtil smsUtil = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.provider.Telephony.SMS_RECEIVED"
                .equals(intent.getAction())) {

            smsUtil = new SmsUtil();
            m_iniFileIO = new IniFile();

            dbUtil = Dao.getInstance(context);

            username = m_iniFileIO.getIniString(Constants.EXTERNALPATH
                            + Constants.INIURL, Constants.LOGININFO,
                    Constants.USERNAME, "", (byte) 0);
            password = m_iniFileIO.getIniString(Constants.EXTERNALPATH
                            + Constants.INIURL, Constants.LOGININFO,
                    Constants.PASSWORD, "", (byte) 0);
            ip = m_iniFileIO.getIniString(Constants.EXTERNALPATH
                            + Constants.INIURL, Constants.LOGININFO,
                    Constants.LOGINURL, "", (byte) 0);
            port = m_iniFileIO.getIniString(Constants.EXTERNALPATH
                            + Constants.INIURL, Constants.LOGININFO,
                    Constants.LOGINPORT, "", (byte) 0);
            url = ip + ":" + port;
            if (url != null && !url.equals("")) {
                if (url.indexOf("http://") > -1) {

                } else {
                    url = "http://" + url;
                }
            }
            smsUrl = m_iniFileIO.getIniString(Constants.EXTERNALPATH
                            + Constants.INIURL, Constants.SMSGATEWAYURL,
                    Constants.SMSGATEWAYIP, "", (byte) 0);
            smsPort = m_iniFileIO.getIniString(Constants.EXTERNALPATH
                            + Constants.INIURL, Constants.SMSGATEWAYURL,
                    Constants.SMSGATEWAYPORT, "", (byte) 0);

            // 接收由SMS传过来的数据
            Bundle bundle = intent.getExtras();
            // 判断是否有数据
            if (bundle != null) {
                // 通过pdus可以获得接收到的所有短信消息
                Object[] objArray = (Object[]) bundle.get("pdus");
                /* 构建短信对象array,并依据收到的对象长度来创建array的大小 */
                SmsMessage[] messages = new SmsMessage[objArray.length];
                for (int i = 0; i < objArray.length; i++) {
                    messages[i] = SmsMessage
                            .createFromPdu((byte[]) objArray[i]);
                }

                SmsMessage sms = messages[0];
                int pduCount = messages.length;
                Log.d(TAG, "sms msg pduCount: " + pduCount);
                phone = sms.getDisplayOriginatingAddress();// 获得接收短信的电话号码
                name = getNameByPhone(phone, context);
                if (name == null || name.equals("")) {
                    name = phone;
                }
                Date date = new Date(sms.getTimestampMillis());
                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                sendTime = format.format(date);// 获取短信发送时间；
                String phoneThree = phone.trim().substring(0, 3);// 截取钱三位字符串
                if (phoneThree != null && phoneThree.equals("+86")) {// 判断是不是+86,如果是+86截取后面的字符串
                    phone = phone.substring(3, phone.length());
                }

                if (pduCount == 1) {// 一条
                    // There is only one part, so grab the body directly.
                    msg = sms.getDisplayMessageBody();// 获得短信的内容
                    Log.d(TAG, "sms msg body: " + msg);
                } else {// 多条
                    // Build up the body from the parts.
                    StringBuilder body = new StringBuilder();
                    for (int i = 0; i < pduCount; i++) {
                        sms = messages[i];
                        body.append(sms.getDisplayMessageBody());
                    }
                    msg = body.toString();
                    Log.d(TAG, "sms msg body: " + msg);
                }

				/* 将送来的短信合并自定义信息于StringBuilder当中 */
                // for (SmsMessage currentMessage : messages) {
                // phone = currentMessage.getDisplayOriginatingAddress();//
                // 获得接收短信的电话号码
                // name = getNameByPhone(phone, context);
                // if(name == null || name.equals("")){
                // name = "手机号";
                // }
                // msg = currentMessage.getDisplayMessageBody();// 获得短信的内容

                // Date date = new Date(currentMessage.getTimestampMillis());
                // SimpleDateFormat format = new
                // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // sendTime = format.format(date);// 获取短信发送时间；

                // String phoneThree = phone.trim().substring(0, 3);//截取钱三位字符串
                // if(phoneThree != null &&
                // phoneThree.equals("+86")){//判断是不是+86,如果是+86截取后面的字符串
                // phone = phone.substring(3, phone.length());
                // }
                //else {
                boolean result = smsUtil.queryMsg(msg);
                if (result) {
                    if (Constants.DbkServerState) {
                        String value = smsUtil.cutStr(context, phone, msg,
                                smsUrl + ":" + smsPort);
                    }
                    this.abortBroadcast();// 拦截系统广播
                } else {
                    AddressEntity address = new AddressEntity();
                    address.setName(name);
                    address.setPhone(phone);
                    address.setEmail("");
                    long id = dbUtil.insertAddressInfo(address);
                    if (id != -1) {
                        address.setRaw_contact_id(id);
                        Intent refreshAddress = new Intent(
                                Constants.REFRESH_ADDRESS);
                        refreshAddress.putExtra("address", address);
                        context.sendBroadcast(refreshAddress);
                    } else {
                        name = dbUtil.detailAddressName(address);
                    }

                    // 将内容保存到数据库
                    ContentValues values = new ContentValues();
                    values.put("receivePhone", phone.trim());
                    values.put("receiveName", name.trim());
                    values.put("message", msg);
                    values.put("time", sendTime);
                    values.put("sendState", "2");
                    values.put("msgState", "1");
                    values.put("sendUser", name.trim());
                    values.put("data1", "1");

                    // 执行插入语句
                    long resoultId = dbUtil.insertBuffer(values,
                            MESSAGE_BOX.TABLE_NAME);
                    this.abortBroadcast();// 拦截系统广播
                    if (resoultId > 0) {

                        if (Constants.msgDetailList != null) {
                            MessageEntity entity = new MessageEntity();// 实例化实体，传入参数
                            entity.setId((int) resoultId);
                            entity.setMessage(msg);
                            entity.setMsgState("1");
                            entity.setReceiveName(name.trim());
                            entity.setReceivePhone(phone.trim());
                            entity.setSendState("2");
                            entity.setSendUser(name.trim());
                            entity.setTime(sendTime);
                            entity.setData1("1");
                            Constants.msgDetailList.add(entity);// 相机和中增加内容
                            context.sendBroadcast(new Intent(
                                    Constants.REFRESH_SMS_DETAIL));// 发送刷新广播
                        }
                        context.sendBroadcast(new Intent(Constants.REFRESH_SMS));// 发送刷新广播
                    }
                    String autoState = m_iniFileIO.getIniString(Constants.EXTERNALPATH
                                    + Constants.INIURL, Constants.AUTOSTART,
                            Constants.SMS_FORWARDING, "true", (byte) 0);
                    if (autoState != null && autoState.equals("true")) {
                        String reSms = smsUtil.SMSforwarding(msg);
                        if (reSms != "") {
                            if(Integer.parseInt(reSms) <= 0 || reSms.length() < 4){
                                doSmsSend(context, "请按照正确格式发送信息");
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
                                doSmsSend(context, code);
                            }
                        }
                    }
                    if (Constants.HttpServerState) {
                        values.clear();// 清空contentcalues
                        values.put("receivePhone", phone.trim());
                        values.put("receiveName", name.trim());
                        values.put("message", msg);
                        values.put("time", sendTime);
                        values.put("data1", "1");

                        // 执行插入语句
                        long resoultReturnId = dbUtil.insertBuffer(values,
                                RETURN_BUFFER.TABLE_NAME);
                        dbUtil.SendRequestToOA(RETURN_BUFFER.TABLE_NAME, url);
                    }
                }
                //}
            }

            // 设置notification的界面
            mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);// 获取系统服务
            notification = new Notification(android.R.drawable.ic_dialog_email,
                    "您有新的短消息!", System.currentTimeMillis());// 设置通知icon，tiltle
            remoteviews = new RemoteViews("com.tbs.tbssms",
                    R.layout.mynotiifcation);
            remoteviews.setImageViewResource(
                    android.R.drawable.ic_dialog_email,
                    android.R.drawable.ic_dialog_email);
            notification.contentView = remoteviews;
            // PendingIntent
            // Intent mainIntent = new Intent(context, ChatMsg.class);
            // mainIntent.("name", name.trim());
            // mainIntent.putExtra("phone", phone.trim());
            Intent mainIntent = new Intent(context, LaunchActivity.class);
            PendingIntent pendingintent = PendingIntent.getActivity(context, 0,
                    mainIntent, 0);// 调用的系统的安装隐士意图 后面红色的代码
            notification.contentIntent = pendingintent;
            mNotificationManager.notify(8888, notification);// 刷新通知界面
        }

    }

    private void doSmsSend(Context context, String msg) {
        if (phone.trim() != null && !phone.trim().equals("") && msg != null && !msg.equals("")) {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//获取去当前系统时间

            //将内容保存到数据库
            ContentValues values = new ContentValues();
            values.put("receivePhone", phone.trim());
            values.put("receiveName", name.trim());
            values.put("message", msg);
            values.put("time", date);
            values.put("sendState", "1");
            values.put("msgState", "0");
            values.put("sendUser", "我");
            values.put("data1", "短信");
            //执行插入语句
            long resoultId = new DataBaseUtil(context).getDataBase().insert(MESSAGE_BOX.TABLE_NAME, null, values);
            this.abortBroadcast();// 拦截系统广播
            if (resoultId > 0) {
                if (Constants.msgDetailList != null) {
                    MessageEntity entity = new MessageEntity();// 实例化实体，传入参数
                    entity.setId((int) resoultId);
                    entity.setMessage(msg);
                    entity.setMsgState("0");
                    entity.setReceiveName(name.trim());
                    entity.setReceivePhone(phone.trim());
                    entity.setSendState("1");
                    entity.setSendUser("我");
                    entity.setTime(date);
                    entity.setData1("短信");
                    Constants.msgDetailList.add(entity);// 相机和中增加内容
                    Constants.saveMsgPosition.put("" + resoultId, Constants.msgDetailList.size() - 1);
                }
                SmsManager smsManager = SmsManager.getDefault();//获得sms管理者

                String action = Constants.SENT_SMS_ACTION + resoultId + "," + 0;
                String action2 = Constants.DELIVERED_SMS_ACTION + resoultId + "," + 0;
                // create the sentIntent parameter
                Intent sentIntent = new Intent(action);
                PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, 0);

                // create the deilverIntent parameter
                Intent deliverIntent = new Intent(action2);
                PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0, deliverIntent, 0);

                if (msg.length() > 70) {//判断短信长度是否大于70个字，如果大于70分多条短信发送
                    List<String> contents = smsManager.divideMessage(msg);//截取短信息
                    for (String sms : contents) {//循环发送短信
                        smsManager.sendTextMessage(phone.trim(), null, sms, sentPI, deliverPI);//发送短信
                    }
                } else {
                    smsManager.sendTextMessage(phone.trim(), null, msg, sentPI, deliverPI);//发送一条短信
                }
                context.sendBroadcast(new Intent(Constants.REFRESH_SMS));// 发送刷新广播
            }
        }
    }

    // 根据电话号码查询姓名（在一个电话打过来时，如果此电话在通讯录中，则显示姓名）
    public String getNameByPhone(String phone, Context context) {
        String name = "";
        Uri uri = Uri
                .parse("content://com.android.contacts/data/phones/filter/"
                        + phone);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{Data.DISPLAY_NAME},
                null, null, null); // 从raw_contact表中返回display_name
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }
}

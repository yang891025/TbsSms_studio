package com.tbs.tbssms.net;


import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.telephony.SmsManager;
import android.util.Log;

import com.tbs.tbssms.constants.Config;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.socketutil.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;


public class NetWorker extends Thread
{

    private final String TAG = "NetWorker";

    private final byte connect = 1;
    private final byte running = 2;
    private byte state = connect; // 状态（默认为连接状态）
    private boolean onWork = true;
    private boolean onSocket = false;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Context mContext;
    private String IP = null;
    private int PORT = 0;
    private String telephone = null;
    //心跳包频率
    private static final long HEART_BEAT_RATE = 30 * 1000;
   // public static final String HEART_BEAT_STRING = "00";//心跳包内容
    private static long sendTime = 0L;


    public NetWorker() {

    }

    public NetWorker(Context context, int port, String ip, String telephone) {
        this.mContext = context;
        this.IP = ip;
        this.PORT = port;
        this.telephone = telephone;
    }

    // For heart Beat
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable()
    {

        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                boolean isSuccess = sendMsg("00");//就发送一个HEART_BEAT_STRING过去 如果发送失败，就重新初始化一个socket
                if (!isSuccess) {
                    //onWork = false;
                    try {
                        setOnWork(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onSocket = false;
                    //System.out.println("socket 发送心跳包失败");
                }
            }
            //System.out.println("socket 发送心跳包" + sendTime );
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    @Override
    public void run() {
        while (onWork) {
            switch (state) {
                case connect://连接状态
                    connect(PORT, IP, telephone);
                    break;
                case running://运行状态
                    receiveMsg();
                    break;
            }
        }
        try {
            if (socket != null) {
                socket.close();
            }
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.close();
            }

            onWork = false;
            onSocket = false;
            state = connect;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接服务器端
     */
    private synchronized void connect(int port, String ip, String telephone) {
        LogUtil.record("****************** start connect *****************");
        String tempIp = null;
        try {
            if (ip != null && !ip.equals("")) {
                if (ip.indexOf("http://") > -1) {
                    tempIp = ip.replace("http://", "");
                } else {
                    tempIp = ip;
                }
            }
            //实例化socket
            socket = new Socket();
            SocketAddress socAddress = new InetSocketAddress(tempIp, port);
            socket.connect(socAddress, 9000);
            if (socket.isConnected()) {
                dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                dos.writeInt(100);

                if (telephone.indexOf("+86") > -1) {
                    telephone = telephone.replace("+86", "");
                }

                Log.d(TAG, "ip:" + ip);
                Log.d(TAG, "tempIp:" + tempIp);
                Log.d(TAG, "port:" + port);
                Log.d(TAG, "telephone" + telephone);

                dos.writeUTF(telephone);
                dos.flush();

                mHandler.postDelayed(new Thread(heartBeatRunnable), HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
                state = running;//将状态重置为运行状态
                onSocket = true;//socket状态为连接

            }
            Log.d(TAG, "connect onSocket:" + onSocket);
        } catch (Exception e) {
            e.printStackTrace();

            Log.d(TAG, "connect onSocket PORT:" + PORT + " exception:" + e.getMessage());

//			Intent START_TIMER = new Intent(Constants.START_TIMER);
//			mContext.sendBroadcast(START_TIMER);

            try {
                setOnWork(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 接收消息
     */
    public synchronized void receiveMsg() {
        try {
            int type = dis.readInt();
            Log.d(TAG, "receiveMsg type:" + type);
            switch (type) {
                case Config.SEND_REG_SMS://验证手机号
                    sendSms();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();

            Log.d(TAG, "receiveMsg onSocket PORT:" + PORT + " exception:" + e.getMessage());

//			Intent START_TIMER = new Intent(Constants.START_TIMER);
//			mContext.sendBroadcast(START_TIMER);

            try {
                setOnWork(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
       发送心跳包函数
       msg发送信息
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean sendMsg(String msg) {
        if (null == socket ) {
            return false;
        }
        try {
            if (!socket.isClosed() && !socket.isOutputShutdown()) {
                OutputStream os = socket.getOutputStream();
                os.write(msg.getBytes());
                os.flush();
                sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }catch (NetworkOnMainThreadException e1){
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 服务器向客户端发来建立好友对的命令
     */
    public void sendSms() {
        LogUtil.record("--------------------- sendSms start ----------------------");
        try {
            String phone = dis.readUTF();
            Log.d(TAG, "sendSms phone:" + phone);
            String smsBody = dis.readUTF();
            Log.d(TAG, "sendSms smsBody:" + smsBody);

            SmsManager smsManager = SmsManager.getDefault();//获得sms管理者
            Log.d(TAG, "sendSms smsManager:" + smsManager);
            String action = Constants.SENT_SMS_ACTION + phone;
            Log.d(TAG, "sendSms action1:" + action);
            String action2 = Constants.DELIVERED_SMS_ACTION + phone;
            Log.d(TAG, "sendSms action2:" + action2);

            // create the sentIntent parameter
            Intent sentIntent = new Intent(action);
            Log.d(TAG, "sendSms sentIntent:" + sentIntent);
            PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0, sentIntent, 0);

            Log.d(TAG, "sendSms sentPI:" + sentPI);
            // create the deilverIntent parameter
            Intent deliverIntent = new Intent(action2);

            Log.d(TAG, "sendSms deliverIntent:" + deliverIntent);

            PendingIntent deliverPI = PendingIntent.getBroadcast(mContext, 0, deliverIntent, 0);

            Log.d(TAG, "sendSms deliverPI:" + deliverPI);

            if (smsBody.length() > 70) {//判断短信长度是否大于70个字，如果大于70分多条短信发送
                List<String> contents = smsManager.divideMessage(smsBody);//截取短信息
                for (String sms : contents) {//循环发送短信
                    Log.d(TAG, "sendSms sms:" + sms);
                    smsManager.sendTextMessage(phone.trim(), null, sms, sentPI, deliverPI);//发送短信
                }
            } else {
                Log.d(TAG, "sendSms smsBody:" + smsBody);
                smsManager.sendTextMessage(phone.trim(), null, smsBody, sentPI, deliverPI);//发送一条短信
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.record("--------------------- sendSms finish ----------------------");
    }

    /**
     * 设置工作状态
     *
     * @param onWork
     * @throws IOException
     */
    public void setOnWork(boolean onWork) throws IOException {
        this.onWork = onWork;
        if (!socket.isInputShutdown()) {
            socket.shutdownInput();
        }
        if (!socket.isOutputShutdown()) {
            socket.shutdownOutput();
        }
    }

    /**
     * 判断是否连接中
     *
     * @return
     */
    public boolean isConnection() {
        return socket.isConnected();
    }

    public boolean isOnWork() {
        return onWork;
    }

    public boolean isOnSocket() {
        return onSocket;
    }
}

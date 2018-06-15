package com.tbs.tbssms.constants;

import android.view.View;

import com.tbs.tbssms.adapter.AddressAdapter;
import com.tbs.tbssms.entity.AddressEntity;
import com.tbs.tbssms.entity.MessageEntity;
import com.tbs.tbssms.net.Communication;
import com.tbs.tbssms.socketutil.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {

	public static final String URL = "TbsSmsMobile/config/url.xml";// 配合文件的xml
	// share_preference
	public static final String SHARED_PREFERENCE_FIRST_LAUNCH = "first_launch_preferences";
	public static final String SHARED_PREFERENCE_FIRST_LAUNCHPATH = "/data/data/com.tbs.tbssms/shared_prefs/first_launch_preferences.xml";
	public static final String SAVE_INFORMATION = "save_information";
	public static final String SAVE_INFORMATIONPATH = "/data/data/com.tbs.tbssms/shared_prefs/save_information.xml";
	public static final String RESOULT_VALUE = "resoult_value";
	public static final String EXIT_CURRENTACTIVITY = "exit_currentactivity";// 閫?嚭褰撳墠activity甯搁噺
	public static final String EXIT_ACTIVITY = "exit_activity";// 退出软件
	public static final String REFRESH_ADDRESS = "refresh_address";// 刷新通讯录
	public static final String REFRESH_UPDATE_ADDRESS = "refresh_update_address";// 刷新通讯录
	public static final String REFRESH_SMS = "refresh_sms";// 刷新短信界面
	public static final String REFRESH_SMS_DETAIL = "refresh_sms_detail";// 刷新短信细览界面
	public static final String HTTP_SERVICE_STATE = "http_service_state";// 刷新短信细览界面
	public static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";// 短信发送成功广播
	public static final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";// 短信接收成功广播
	public static final String HTTPSTARTSERVICE = "http_start_service";// 短信接收成功广播
	public static final String HTTPCLOSESERVICE = "http_close_service";// 短信接收成功广播
	public static final String MISSTARTSERVICE = "mis_start_service";// 短信接收成功广播
	public static final String MISCLOSESERVICE = "mis_close_service";// 短信接收成功广播
	// public static final String CHATSTARTSERVICE = "chat_start_service";//
	// 短信接收成功广播
	// public static final String CHATCLOSESERVICE = "chat_close_service";//
	// 短信接收成功广播
	public static final String DBKSTARTSERVICE = "dbk_start_service";// 短信接收成功广播
	public static final String DBKCLOSESERVICE = "dbk_close_service";// 短信接收成功广播
	public static final String REFRESH_DETAIL_SERVER = "refresh_detail_server";// 短信发送成功广播
	public static final String REFRESH_SERVICE_BUTTON = "refresh_service_button";// 短信接收成功广播
	// 常用的
	public static final int UPDATE_DATA = 0x00001;
	public static final int TIMEOUT = 0x00002;// 超时常量
	public static final long timeout = 20000;// 连接超时时间
	public static final int SWIPE_MIN_DISTANCE = 120;
	public static final int SWIPE_THRESHOLD_VELOCITY = 200;
	public static final String CAMPANY_EMAIL = "247304291@qq.com";// campany
																	// email
	public static final String APP_VERSION_URL = "/version/version.txt";// update
																		// app
																		// version
																		// url
	public static final String APK_FILE_URL = "/updateApk/TbsSmsServer.apk";// update
																			// app
																			// download
																			// file
																			// url
    public static final String PACKAGE_NAME = "com.tbs.tbssmsserver";// package
																		// name
	/*
	 * 启动app是准备项的变量
	 */
	public static Map<Integer, Boolean> isCheckedMap = new HashMap<Integer, Boolean>();
	public static int displayWidth = 120;
	public static int displayHeight = 120;
	// 每个页面的view数据
	public static final ArrayList<View> views = new ArrayList<View>();
	// 布局内容
	public static View sms;
	public static View address;
	public static View setting;
	// 通讯录中使用布局
	// public static View searchView;
	public static View toastView;
	public static List<AddressEntity> list = new ArrayList<AddressEntity>();// 通讯录集合
	public static AddressAdapter adapter;// 通讯录适配器
	public static AddressEntity[] arrays;// 通讯录数组
	public static AddressEntity[] searchArrays;// 通讯录数组
	// public static List<MessageEntity> msgList;//短信列表
	// public static MessageAdapter smsAdapter;//通讯录适配器
	public static List<MessageEntity> msgDetailList = new ArrayList<MessageEntity>();// 单条短信息内容
	public static ArrayList<String> pinyinFirst = new ArrayList<String>();
	public static boolean HttpServerState = false;
	public static boolean MisServerState = false;
	// public static boolean ChatServerState = false;
	public static boolean DbkServerState = false;

	public static boolean LoginState = false;
	public static String SessionId = "";// sessionId
	public static String DbkSessionId = "";// sessionId
	public static String afterPinYin = "";// 返回的id
	public static final String APP_ID = "wx365f1864ad361278";
	public static final String SYSTEM_NOTIFI = "http://www.baidu.com";// 系统通知
	public static final String HELP_RETURN = "http://www.baidu.com";

    public static final String soft_path = "/Tbs-Soft";
	// 启动定时器
	public static final String INIURL = soft_path + "/Server/TbsSmsServer/config/TbsSmsServer.ini";// .ini文件路径
	public static final String START_TIMER = "start_timer";
	public static final String EXTERNALPATH = Util.getExternalPath();
	public static final String DBKCONNECTIONURL = "/EBS/UserSMSServlet";
	public static final String DBKSEARCHURL = "/EBS/DBKServlet";
	public static final String BankTransferURL = "/TBSEBank/TransferServlet";
	// ini配置文档标签
	public static final String IMSMISSERVER = "ImsMisServer";
	public static final String IMSMISSERVERIP = "ImsMisServerIp";
	public static final String IMSMISSERVERPORT = "ImsMisServerPort";

	public static final String LOGININFO = "LoginInfo";
	public static final String USERNAME = "UserName";
	public static final String PASSWORD = "PassWord";
	public static final String LOGINURL = "LoginUrl";
	public static final String LOGINPORT = "LoginPort";

	public static final String NOTIFYINFO = "NotifyInfo";
	public static final String XMPPHOST = "XmppHost";
	public static final String XMPPPORT = "XmppPort";
	public static final String XMPPAPI = "XmppApi";

	public static final String AUTOSTART = "AutoStart";
	public static final String APP_AUTOSTART = "AppAutoStart";
	public static final String SERVER_AUTOSTART = "ServerAutoStart";

	public static final String OA_AUTOSTART = "OAAutoStart";
	public static final String IMS_MIS_AUTOSTART = "ImsMisAutoStart";
    public static final String SMS_FORWARDING = "SmsForwardAutoStart";
	public static final String DBK_AUTOSTART = "DbkAutoStart";

	public static final String SMSNETSET = "SmsNetSet";
	public static final String RegistAccount = "RegistAccount";
	public static final String QueryAccountPassword = "QueryAccountPassword";
	public static final String UpdatePassword = "UpdatePassword";
	public static final String SearchDbk = "SearchDbk";
	public static final String QueryBalance = "QueryBalance";
	public static final String WalletTopUp = "WalletTopUp";
    public static final String SmsForwarding = "SMSforwarding";
	public static final String QueryBalanceBill = "QueryBalanceBill";
	public static final String SMSGATEWAYURL = "SmsGatewayUrl";
	public static final String SMSGATEWAYIP = "SmsGatewayIp";
	public static final String SMSGATEWAYPORT = "SmsGatewayPort";

	public static final String TELEPHONENUMBER = "TelephoneNumber";
	public static final String TELEPHONESETTING = "TelephoneSetting";

	public static final String SMSGATEWAYSMSCONTENTPLACEHOLDER = "SmsGatewaySmsContentPlaceholder";
	public static final String PLACEHOLDER = "Placeholder";
	public static final String SMSGATEWAYSMSCONTENT = "SmsGatewaySmsContent";
	public static final String ZCSUCCESSSMSCONTENT = "ZCSuccessSmsContent";
	public static final String ZCUNSUCCESSSMSCONTENT = "ZCUnsuccessSmsContent";
	public static final String CXSUCCESSSMSCONTENT = "CXSuccessSmsContent";
	public static final String CXUNSUCCESSSMSCONTENT = "CXUnsuccessSmsContent";
	public static final String XGSUCCESSSMSCONTENT = "XGSuccessSmsContent";
	public static final String XGUNSUCCESSSMSCONTENT = "XGUnsuccessSmsContent";
	public static Communication mCommunication = null;
	public static HashMap<String, Integer> saveMsgPosition = new HashMap<String, Integer>();
    public static final String APK_SAVEFILE_URL = EXTERNALPATH + soft_path+"/Server/TbsSmsServer/update/TbsSmsServer.apk";

}

package com.tbs.tbssms.constants;

public interface Config {
	//消息来自于
	public static final int SEND_REG_SMS = 0;
	//消息发送给
	public static final int MESSAGE_TO = -1;
	//文本消息
	public static final int MESSAGE_TYPE_TXT = 0;
	//图片消息
	public static final int MESSAGE_TYPE_IMG = 1;
	//音频消息
	public static final int MESSAGE_TYPE_AUDIO = 2;
	//添加好友消息
	public static final int MESSAGE_TYPE_ADD_FRIEND = 3;
	//视频消息
	public static final int MESSAGE_TYPE_VADIO = 4;
	//短信消息
	public static final int MESSAGE_TYPE_SMS = 5;
	//ims消息
	public static final int MESSAGE_TYPE_IMS = 6;
}

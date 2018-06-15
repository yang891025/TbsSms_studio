package com.tbs.tbssms.entity;

import java.io.Serializable;

public class MessageEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public MessageEntity() {

	}

	public MessageEntity(Integer id, String sendUser, String receivePhone,
			String receiveName, String message, String time, String sendState,
			String retysTimes, String msgState, String data1, String data2,
			String data3, String data4, String data5) {
		super();
		this.id = id;
		this.sendUser = sendUser;
		this.receivePhone = receivePhone;
		this.receiveName = receiveName;
		this.receiveName = message;
		this.receiveName = time;
		this.receiveName = sendState;
		this.receiveName = retysTimes;
		this.receiveName = msgState;
		this.data1 = data1;
		this.data2 = data2;
		this.data3 = data3;
		this.data4 = data4;
		this.data5 = data5;
	}

	private Integer id;
	private String sendUser;
	private String receivePhone;
	private String receiveName;
	private String message;
	private String time;
	private String sendState;
	private String retysTimes;
	private String msgState;
	private String data1;
	private String data2;
	private String data3;
	private String data4;
	private String data5;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getReceivePhone() {
		return receivePhone;
	}

	public void setReceivePhone(String receivePhone) {
		this.receivePhone = receivePhone;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSendState() {
		return sendState;
	}

	public void setSendState(String sendState) {
		this.sendState = sendState;
	}

	public String getRetysTimes() {
		return retysTimes;
	}

	public void setRetysTimes(String retysTimes) {
		this.retysTimes = retysTimes;
	}

	public String getMsgState() {
		return msgState;
	}

	public void setMsgState(String msgState) {
		this.msgState = msgState;
	}

	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String getData4() {
		return data4;
	}

	public void setData4(String data4) {
		this.data4 = data4;
	}

	public String getData5() {
		return data5;
	}

	public void setData5(String data5) {
		this.data5 = data5;
	}

}

package com.tbs.tbssms.entity;

import java.io.Serializable;

public class JsonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userCode;
	private String password;

	public JsonEntity() {

	}

	public JsonEntity(String userCode, String password) {

		super();
		this.password = password;
		this.userCode = userCode;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

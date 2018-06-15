package com.tbs.tbssms.entity;

import java.io.Serializable;

public class ExpChirdrenEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String ip;
	private String port;

	public ExpChirdrenEntity() {

	}

	public ExpChirdrenEntity(String ip, String port) {

		super();
		this.ip = ip;
		this.port = port;
		
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}

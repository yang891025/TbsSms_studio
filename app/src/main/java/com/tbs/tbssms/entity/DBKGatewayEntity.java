package com.tbs.tbssms.entity;

import java.io.Serializable;

public class DBKGatewayEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	String zc = null;
	String cxzm = null;
	String xgmm = null;
	String dbkjs = null;
	String qubye = null;
	String qbcz = null;
	String cxqbzd = null;
	String httpUrl = null;
	String httpPort = null;

	public DBKGatewayEntity() {

	}

	public DBKGatewayEntity(String zc, String cxzm, String xgmm, String dbkjs, String qubye, String qbcz, String cxqbzd, String httpUrl, String httpPort) {
		super();
		this.zc = zc;
		this.cxzm = cxzm;
		this.xgmm = xgmm;
		this.dbkjs = dbkjs;
		this.qubye = qubye;
		this.qbcz = qbcz;
		this.cxqbzd = cxqbzd;
		this.httpUrl = httpUrl;
		this.httpPort = httpPort;
	}

	public String getZc() {
		return zc;
	}

	public void setZc(String zc) {
		this.zc = zc;
	}

	public String getCxzm() {
		return cxzm;
	}

	public void setCxzm(String cxzm) {
		this.cxzm = cxzm;
	}

	public String getXgmm() {
		return xgmm;
	}

	public void setXgmm(String xgmm) {
		this.xgmm = xgmm;
	}

	public String getDbkjs() {
		return dbkjs;
	}

	public void setDbkjs(String dbkjs) {
		this.dbkjs = dbkjs;
	}

	public String getQubye() {
		return qubye;
	}

	public void setQubye(String qubye) {
		this.qubye = qubye;
	}

	public String getQbcz() {
		return qbcz;
	}

	public void setQbcz(String qbcz) {
		this.qbcz = qbcz;
	}

	public String getCxqbzd() {
		return cxqbzd;
	}

	public void setCxqbzd(String cxqbzd) {
		this.cxqbzd = cxqbzd;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	public String getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(String httpPort) {
		this.httpPort = httpPort;
	}
	
}

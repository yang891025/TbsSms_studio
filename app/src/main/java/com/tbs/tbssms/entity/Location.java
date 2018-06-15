package com.tbs.tbssms.entity;

import java.io.Serializable;

public class Location implements Serializable {

	private static final long serialVersionUID = 1L;

	public Location() {

	}

	public Location(Integer id, String url) {

		super();
		this.id = id;
		this.url = url;

	}

	private Integer id;
	private String url;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "laction [id=" + id + ", url=" + url + "]";
	}

}

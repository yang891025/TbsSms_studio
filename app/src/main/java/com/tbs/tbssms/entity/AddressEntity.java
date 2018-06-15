package com.tbs.tbssms.entity;

import java.io.Serializable;

public class AddressEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long raw_contact_id;
	private Integer pic;
	private String name;
	private String phone;
	private String email;
	private String address;
	private String data1;
	private String data2;
	private String data3;
	private String data4;
	private String data5;

	public AddressEntity() {

	}

	public AddressEntity(long raw_contact_id, Integer pic, String name,
			String phone, String email, String address, String data1,
			String data2, String data3, String data4, String data5) {

		super();
		this.raw_contact_id = raw_contact_id;
		this.pic = pic;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.address = address;
		this.data1 = data1;
		this.data2 = data2;
		this.data3 = data3;
		this.data4 = data4;
		this.data5 = data5;
		
	}

	public long getRaw_contact_id() {
		return raw_contact_id;
	}

	public void setRaw_contact_id(long raw_contact_id) {
		this.raw_contact_id = raw_contact_id;
	}

	public Integer getPic() {
		return pic;
	}

	public void setPic(Integer pic) {
		this.pic = pic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

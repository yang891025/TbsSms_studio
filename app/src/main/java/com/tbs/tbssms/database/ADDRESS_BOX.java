/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author 任雪涛
 * @version 2012-12-21
 * @time 下午12:21:00
 * @function 
 */
package com.tbs.tbssms.database;

import android.provider.BaseColumns;

public interface ADDRESS_BOX extends BaseColumns {

	String TABLE_NAME = "AddressBox";// 表名
	String RAW_CONTACT_ID = "raw_contact_id";// 联系电话
	String NAME = "name";// 联系人电话
	String MOBEL_NUMBER = "mobel_number";// 联系电话
	String NUMBER = "number";// 家庭电话
	String EMAIL = "email";// 邮箱
	String ADDRESS = "address";// 地址
	String DATA1 = "data1";// 资源1
	String DATA2 = "data2";// 资源2
	String DATA3 = "data3";// 资源3
	String DATA4 = "data4";// 资源4
	String DATA5 = "data5";// 资源5

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + RAW_CONTACT_ID + " text,"
			+ NAME + " text," + MOBEL_NUMBER + " text," + NUMBER + " text,"
			+ EMAIL + " text," + ADDRESS + " text," + DATA1 + " text," + DATA2
			+ " text," + DATA3 + " text" + ")";
}

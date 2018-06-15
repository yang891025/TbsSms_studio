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

public interface RETURN_BUFFER extends BaseColumns {

	String TABLE_NAME = "ReturnBuffer";// 表名
	String RECEIVEPHONE = "receivePhone";// 收信人电话
	String RECEIVENAME = "receiveName";// 收信人名称
	String MESSAGE = "message";// 短信内容
	String TIME = "time";// 时间
	String DATA1 = "data1";// 其他资源
	String DATA2 = "data2";// 其他资源
	String DATA3 = "data3";// 其他资源
	String DATA4 = "data4";// 其他资源
	String DATA5 = "data5";// 其他资源
	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + RECEIVEPHONE + " text,"
			+ RECEIVENAME + " text," + MESSAGE + " text," + TIME + " text,"
			+ DATA1 + " text," + DATA2 + " text," + DATA3 + " text," + DATA4
			+ " text," + DATA5 + " text" + ")";// 创建数据库表结构
}

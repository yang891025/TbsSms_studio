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

public interface MESSAGE_STATISTICS_BOX extends BaseColumns {

	String TABLE_NAME = "MessageStatisticsBox";// 表名
	String SENDCOUNT = "sendCount";// 发短信人或者使用本机
	String RECEIVECOUNT = "receiveCount";// 收到短信数量
	String TIME = "time";// 收发时间
	String DATA1 = "data1";// 其他资源
	String DATA2 = "data2";// 其他资源
	String DATA3 = "data3";// 其他资源
	String DATA4 = "data4";// 其他资源
	String DATA5 = "data5";// 其他资源

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + SENDCOUNT + " text,"
			+ RECEIVECOUNT + " text," + TIME + " text," + DATA1 + " text," + DATA2
			+ " text," + DATA3 + " text," + DATA4 + " text," + DATA5 + " text"
			+ ")";
}

/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author liyimig
 * @version 2012-5-3
 * @time 上午09:58:10
 * @function 
 */
package com.tbs.tbssms.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "SqliteOpenHelper";
	private static SqliteOpenHelper dbUtil;
	public static final String DB_NAME = "TbsSms.db";

	static Context ctx;

	public SqliteOpenHelper(Context context, String name,CursorFactory factory, int version) {
		super(context, name, factory, version);
		ctx = context;
	}

	public static SqliteOpenHelper getInstance(Context context) {
		return getInstance(context, DB_NAME, 1);
	}

	public static SqliteOpenHelper getInstance(Context context, String name,
			int version) {
		if (dbUtil == null) {
			dbUtil = new SqliteOpenHelper(context, name, null, version);
		}
		return dbUtil;
	}

	public static boolean deleteDatabase(String dbName) {
		return ctx.deleteDatabase(dbName);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "create database in first time ..initial...");
		db.execSQL(MESSAGE_BOX.CREATE_TABLE);
		db.execSQL(ADDRESS_BOX.CREATE_TABLE);
		db.execSQL(SEND_BUFFER.CREATE_TABLE);
		db.execSQL(RETURN_BUFFER.CREATE_TABLE);
		db.execSQL(MESSAGE_STATISTICS_BOX.CREATE_TABLE);
		db.execSQL(MESSAGE_STATISTICS_PEOPLE_BOX.CREATE_TABLE);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		Log.d(TAG, "open database in first time ..initial...");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}

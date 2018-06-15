package com.tbs.tbssms.dao;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tbs.tbssms.callback.HttpClientCallBack;
import com.tbs.tbssms.database.ADDRESS_BOX;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.database.RETURN_BUFFER;
import com.tbs.tbssms.entity.AddressEntity;
import com.tbs.tbssms.util.HttpConnectionUtil;
import com.tbs.tbssms.util.HttpConnectionUtil.HttpMethod;

public class Dao {
	private static final String TAG = "Dao";
	
	private static Dao dao = null;
	private Context context;

	private Dao(Context context) {
		this.context = context;
	}

	public static Dao getInstance(Context context) {
		if (dao == null) {
			dao = new Dao(context);
		}
		return dao;
	}

	public SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DataBaseUtil(context).getDataBase();
		} catch (Exception e) {
		}
		return sqliteDatabase;
	}
	
	/**
	 * 插入数据到指定表格
	 */
	public synchronized long insertBuffer(ContentValues values, String tableName) {
		long result = 0;
		Cursor cursor = null;
		SQLiteDatabase database = getConnection();
		try {
			result = database.insert(tableName, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return result;
	}
	
	/**
	 *  查询所有数据
	 */
	public Cursor detailAllInfo(String tableName) {
		Cursor cursor = null;
		SQLiteDatabase database = null;
		database = getConnection();
		try {
			cursor = database.query(tableName, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return cursor;
	}
	
	/**
	 *  删除指定数据
	 */
	public int deletInfo(String tableName, String whereClause, String[] whereArgs) {
		int result = -1;
		SQLiteDatabase database = null;
		database = getConnection();
		try {
			result = database.delete(RETURN_BUFFER.TABLE_NAME, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
		return result;
	}
	
	/**
	 *  查询指定数据
	 */
	public Cursor detailInfo(String tableName, String whereClause, String[] whereArgs) {
		Cursor cursor = null;
		SQLiteDatabase database = null;
		database = getConnection();
		try {
			cursor = database.query(tableName, null, whereClause, whereArgs, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return cursor;
	}
	
	/**
	 *  查询指定数据
	 */
	public int updateInfo(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
		int result = -1;
		SQLiteDatabase database = null;
		database = getConnection();
		try {
			result = database.update(tableName, values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
		return result;
	}
	
	/**
	 * 想oa发送http请求
	 */
	public void SendRequestToOA(String tableName, String url) {
		Cursor cursor = null;
		SQLiteDatabase database = null;
		database = getConnection();
		try {
			cursor = database.query(tableName, null, null, null, null, null, null);
			if(cursor !=null){
				HttpConnectionUtil connection = HttpConnectionUtil.newInstance();
				while (cursor.moveToNext()) {
					int delId = cursor.getInt(cursor.getColumnIndex("_id"));
					String name = cursor.getString(cursor.getColumnIndex("receiveName"));
					Map<String, String> params = new HashMap<String, String>();
			        params.put("method", "sendMsgMobile"); 
			        params.put("client", "SMS");
			        params.put("sender", name);  
			        params.put("content", cursor.getString(cursor.getColumnIndex("message")));
			        params.put("acceptTime", cursor.getString(cursor.getColumnIndex("time")));
			        params.put("sendMobile", cursor.getString(cursor.getColumnIndex("receivePhone")));
			        
					connection.asyncConnect(url+"/Message",params,HttpMethod.POST, new HttpClientCallBack(),context,name);
					
					String whereClause = "_id = ?";
					String[] whereArgs = {""+delId};
					int resoult = deletInfo(RETURN_BUFFER.TABLE_NAME, whereClause, whereArgs);
					Log.d(TAG, "resoult :" + resoult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
	}
	
	/**
	 *  查询发送缓存Id
	 */
	public String detailSendBufferId(String tableName, String whereClause, String[] whereArgs) {
		String autoId = null;
		Cursor cursor = null;
		SQLiteDatabase database = null;
		database = getConnection();
		try {
			cursor = database.query(tableName, null, whereClause, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				autoId = cursor.getString(cursor.getColumnIndex("autoId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return autoId;
	}
	
	/**
	 *  查询短信数量
	 */
	public int detailSendCount(String tableName, String whereClause, String[] whereArgs) {
		int sendCount = 0;
		Cursor cursor = null;
		SQLiteDatabase database = null;
		database = getConnection();
		try {
			cursor = database.query(tableName, null, whereClause, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				sendCount = cursor.getInt(cursor.getColumnIndex("sendCount")) + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return sendCount;
	}
	
	
	/**
	 * 查询数据库数据，如果存在数据则更新数据 如果没有数据就插入数据
	 */
	public synchronized long insertAddressInfo(AddressEntity address) {
		long result = -1;
		Cursor cursor = null;
		ContentValues values = null;
		SQLiteDatabase database = null;

		database = getConnection();
		
		values = new ContentValues();
		values.put("name", address.getName());
		values.put("mobel_number", address.getPhone());
		values.put("email", address.getEmail());
		
		String whereClause = "mobel_number = ?";
		String[] whereArgs = { address.getPhone() };
		try {
			cursor = database.query(ADDRESS_BOX.TABLE_NAME, null, whereClause, whereArgs, null, null, null);
			if (cursor != null) {
				if (cursor.getCount() <= 0) {
					result = database.insert(ADDRESS_BOX.TABLE_NAME, null, values);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return result;
	}
	
	/**
	 *  查询指定数据
	 */
	public String detailAddressName(AddressEntity address) {
		String name = null;
		Cursor cursor = null;
		SQLiteDatabase database = null;
		database = getConnection();
		String whereClause = "mobel_number = ?";
		String[] whereArgs = { address.getPhone() };
		try {
			cursor = database.query(ADDRESS_BOX.TABLE_NAME, null, whereClause, whereArgs, null, null, null);
			if (cursor != null) {
				if(cursor.moveToFirst()){
					name = cursor.getString(cursor.getColumnIndex("name"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return name;
	}
	
	
	/**
	 * 查询数据库数据，如果存在数据则更新数据 如果没有数据就插入数据
	 */
	public synchronized void UpdateAddressInfo(AddressEntity address) {
		int id = 0;
		Cursor cursor = null;
		ContentValues values = null;
		SQLiteDatabase database = null;
		database = getConnection();
		values = new ContentValues();
		String whereClause = "mobel_number = ?";
		String[] whereArgs = { address.getPhone() };
		try {
			cursor = database.query(ADDRESS_BOX.TABLE_NAME, null, whereClause, whereArgs, null, null, null);
			if (cursor != null) {
				if(cursor.moveToFirst()){
					id = cursor.getInt(cursor.getColumnIndex("_id"));
					address.setRaw_contact_id(id);
				}
			}
			values.put(ADDRESS_BOX.RAW_CONTACT_ID, address.getRaw_contact_id());
			values.put(ADDRESS_BOX.NAME, address.getName());
			database.update(ADDRESS_BOX.TABLE_NAME, values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
	}
}
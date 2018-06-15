package com.tbs.tbssms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.SyncStateContract.Constants;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbssms.R;
import com.tbs.tbssms.database.ADDRESS_BOX;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.entity.AddressEntity;
import com.tbs.tbssms.entity.Location;
import com.tbs.tbssms.entity.MessageEntity;
import com.tbs.tbssms.xmlparser.DomParser;

public class Util {

	private static final String TAG = "Util";

	static AlertDialog alertDialog;
	static AlertDialog.Builder builder;
	static Activity activity;
	static List<Location> loc;
	static InputStream inputStreams;

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	// 计算px转换成dip
	public static int px2dip(float pxValue, float scale) {
		return (int) (pxValue / scale + 0.5f);
	}

	// 计算dip转换成px
	public static int dip2px(float dipValue, float scale) {
		return (int) (dipValue * scale + 0.5f);
	}

	// 计算sp转换成px
	public static int px2sp(float pxValue, float fontScale) {
		return (int) (pxValue / fontScale + 0.5f);
	}

	// 计算sp被转换成px
	public static int sp2px(float spValue, float fontScale) {
		return (int) (spValue * fontScale + 0.5f);
	}

	// 获得日期
	public static String getdate() {
		String date;
		date = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINESE)
				.format(Calendar.getInstance().getTime());
		return date;
	}

	// 获得日期
	public static String getRandom() {
		return String.valueOf(System.currentTimeMillis());
	}

	// 解析xml返回对象
	public static List<Location> getProperty(Context context) {
		try {
			inputStreams = context.getResources().getAssets()
					.open(com.tbs.tbssms.constants.Constants.URL);
			loc = DomParser.readXml2(inputStreams);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loc;
	}

	// 获得本地的bitmap
	public static Bitmap getLoacalBitmap(String url) {
		FileInputStream fis = null;
		if (null != url && !(url.equals(""))) {
			try {
				fis = new FileInputStream(url);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(url, options);

				options.inSampleSize = computeSampleSize(options, -1, 128 * 128);
				options.inJustDecodeBounds = false;
				Bitmap bmp = BitmapFactory.decodeFile(url, options);
				fis.close();
				return bmp; // 返回bitmap
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	// 删除本地文件
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 璺緞涓烘枃浠朵笖涓嶄负绌哄垯杩涜鍒犻櫎
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	// 判断当前网络状态
	public static boolean checkNetState(Context context) {
		Context c = context;
		ConnectivityManager cm = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			if (info.getState() == State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	// 关闭输入法
	public static void closeInput(EditText contentTextEdit, Context context) {
		// close input method after send
		final InputMethodManager imm = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(contentTextEdit.getWindowToken(), 0);
	}

	// 设置listview第一次滑动
	// public static void setListViewFastScoll(ListView listview, Context
	// context, int drawableResouce) {
	// try {
	// Field f = AbsListView.class.getDeclaredField("mFastScroller");
	// f.setAccessible(true);
	// Object o = f.get(listview);
	// f = f.getType().getDeclaredField("mThumbDrawable");
	// f.setAccessible(true);
	// Drawable drawable = (Drawable) f.get(o);
	// drawable = context.getResources().getDrawable(drawableResouce);
	// f.set(o, drawable);
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }

	// 软件更新Get提交
	private static JSONObject object;
	private static String result = null;
	private static float version = 0;

	public static float doGet(String url) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = httpClient.execute(request);
			result = EntityUtils.toString(response.getEntity());
			object = new JSONObject(result);
			Log.d("HttpActivity", result);
			version = object.getInt("version");
			// JSONArray array = object.getJSONArray("data");
			// for (int i = 0; i < array.length(); i++) {
			//
			// JSONObject jsonItem = array.getJSONObject(i);
			// name=(String) jsonItem.get("username");
			// ordernum=(String) jsonItem.get("ordernum");
			// phone=(String) jsonItem.get("phonenum");
			// Log.i("HttpActivity", name);
			// }
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return version;
	}

	// inputstream转byte
	private static byte[] getFileBuffer(InputStream inStream, long fileLength)
			throws IOException {

		byte[] buffer = new byte[256 * 1024];
		byte[] fileBuffer = new byte[(int) fileLength];

		int count = 0;
		int length = 0;

		while ((length = inStream.read(buffer)) != -1) {
			for (int i = 0; i < length; ++i) {
				fileBuffer[count + i] = buffer[i];
			}
			count += length;
		}
		return fileBuffer;
	}

	// 软件更新Post提交
	public static float doPost(String url) {
		try {
			URL path = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) path.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			InputStream inStream = conn.getInputStream();
			// URL newUrl = new URL(url);
			// URLConnection connect = newUrl.openConnection();
			// connect.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			// InputStream inStream = connect.getInputStream();
			byte[] data = getFileBuffer(inStream, 3);
			// byte[] data = new byte[inStream.available()];
			String json = new String(data, "UTF-8");
			Log.d(TAG, "json : " + json);
			// JSONObject object = new JSONObject(json);
			// version = object.getInt("version");
			version = Float.valueOf(json);
			Log.d(TAG, "version : " + version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * @param dateStr
	 *            传入的时间
	 * @param stype
	 *            设置的格式
	 * @return
	 */
	public String compareDate(String dateStr, String stype) {
		long time1 = StringToDate(dateStr, stype).getTime();
		long time2 = getCurrentDate(stype).getTime();
		Log.d(TAG, "time1" + time1);
		Log.d(TAG, "time2" + time2);
		long time3 = time2 - time1;
		if ((time3 = (time3 / 60000)) < 60) {
			return time3 + "分钟前";
		} else if ((time3 = (time3 / 60)) < 60) {
			return time3 + "小时前";
		} else if ((time3 = (time3 / 24)) < 24) {
			return time3 + "天前";
		} else if ((time3 = (time3 / 30)) < 30) {
			return time3 + "月前";
		} else if ((time3 = (time3 / 12)) < 12) {
			return time3 + "年前";
		} else {
			return time3 + "年前";
		}
	}

	/**
	 * 字符串转换到时间格式
	 * 
	 * @param dateStr
	 *            需要转换的字符串
	 * @param formatStr
	 *            需要格式的目标字符串 举例 yyyy-MM-dd
	 * @return Date 返回转换后的时间
	 * @throws ParseException
	 *             转换异常
	 */
	public Date StringToDate(String dateStr, String formatStr) {
		DateFormat sdf = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 得到当前日期
	 * 
	 * @return
	 */
	public Date getCurrentDate(String style) {
		String dateStr = new SimpleDateFormat(style).format(new Date());
		Date date = StringToDate(dateStr, style);
		return date;
	}

	// list转array
	public static AddressEntity[] getArray(List<AddressEntity> list) {// 转换数组
		AddressEntity[] arrays = (AddressEntity[]) list
				.toArray(new AddressEntity[list.size()]);
		return arrays;
	}

	public static List<AddressEntity> getAddressCursor(Context context,
			String contant) {
		List<AddressEntity> list = new ArrayList<AddressEntity>();// 实例化实体类
		list.clear();
		String sql = "select * from AddressBox where mobel_number like '%"
				+ contant + "%' or name like '%" + contant + "%' order by _id";
		Cursor cursor = new DataBaseUtil(context).getDataBase().rawQuery(sql,
				null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				AddressEntity entity = new AddressEntity();// 实例化实体
				entity.setRaw_contact_id(cursor.getInt(cursor
						.getColumnIndex("_id")));// 存储id
				entity.setName(cursor.getString(cursor.getColumnIndex("name")));
				entity.setPhone(cursor.getString(cursor
						.getColumnIndex("mobel_number")));
				entity.setEmail(cursor.getString(cursor.getColumnIndex("email")));
				list.add(entity);
			}
		}
		cursor.close();
		return list;
	}

	public static List<MessageEntity> getMessageCursor(Context context,
			String contant) {
		List<MessageEntity> msgList = new ArrayList<MessageEntity>();
		String sql = "select * from MessageBox  where _id in(select max(_id) from MessageBox where receivePhone like '%"
				+ contant
				+ "%' or receiveName like '%"
				+ contant
				+ "%' or message like '%"
				+ contant
				+ "%' group by receivePhone )";
		Cursor cursor = new DataBaseUtil(context).getDataBase().rawQuery(sql,
				null);
		while (cursor.moveToNext()) {
			MessageEntity entity = new MessageEntity();// 实例化实体
			entity.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			entity.setMessage(cursor.getString(cursor.getColumnIndex("message")));
			entity.setMsgState(cursor.getString(cursor
					.getColumnIndex("msgState")));
			entity.setReceiveName(cursor.getString(cursor
					.getColumnIndex("receiveName")));
			entity.setReceivePhone(cursor.getString(cursor
					.getColumnIndex("receivePhone")));
			entity.setRetysTimes(cursor.getString(cursor
					.getColumnIndex("retysTimes")));
			entity.setSendState(cursor.getString(cursor
					.getColumnIndex("sendState")));
			entity.setSendUser(cursor.getString(cursor
					.getColumnIndex("sendUser")));
			entity.setTime(cursor.getString(cursor.getColumnIndex("time")));
			msgList.add(entity);
		}
		cursor.close();
		return msgList;
	}

	// 通用的提示对话框
	public static void checkDialog(Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setInverseBackgroundForced(true)
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// 查询sdcard路径
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}

	// 查询手机内存路径
	public static String getInternalPath() {
		File sdDir = null;
		sdDir = Environment.getRootDirectory();// 获取跟目录
		return sdDir.toString();
	}

	public static String getMD5(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();// 加密
		String sb = new String();
		for (int i = 0; i < m.length; i++) {
			// sb.append(m[i]);
			int b = (0xFF & m[i]);
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				sb += "0";
			// add number to string
			sb += Integer.toHexString(b);
		}
		return sb.toUpperCase();
	}
}
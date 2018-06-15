package com.tbs.tbssms.socketutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

public class Util {

	private static final String TAG = "Util";

	static AlertDialog alertDialog;
	static AlertDialog.Builder builder;
	static Activity activity;
	static InputStream inputStreams;

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
		// 璺緞涓烘枃浠朵笖涓嶄负绌哄垯杩涜鍒犻�?
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	// 判断当前网络状�?
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

	// 关闭输入�?
	public static void closeInput(EditText contentTextEdit, Context context) {
		// close input method after send
		final InputMethodManager imm = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(contentTextEdit.getWindowToken(), 0);
	}

	public static void openInput(EditText contentTextEdit, Context context) {
		// open input method after send
		final InputMethodManager imm = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		imm.showSoftInputFromInputMethod(contentTextEdit.getWindowToken(),
				InputMethodManager.SHOW_IMPLICIT);
	}

	// 设置listview第一次滑�?
	public static void setListViewFastScoll(ListView listview, Context context,
			int drawableResouce) {
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			f.setAccessible(true);
			Object o = f.get(listview);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			Drawable drawable = (Drawable) f.get(o);
			drawable = context.getResources().getDrawable(drawableResouce);
			f.set(o, drawable);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

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
	 * 字符串转换到时间格式
	 * 
	 * @param dateStr
	 *            �?��转换的字符串
	 * @param formatStr
	 *            �?��格式的目标字符串 举例 yyyy-MM-dd
	 * @return Date 返回转换后的时间
	 * @throws ParseException
	 *             转换异常
	 */
	public static Date StringToDate(String dateStr, String formatStr) {
		DateFormat sdf = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	// 获取系统日期
	public static String getdate() {
		String date;
		date = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINESE)
				.format(Calendar.getInstance().getTime());
		return date;
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
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存�?
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目�?
		}
		return sdDir.toString();
	}

	// 查询手机内存路径
	public static String getInternalPath() {
		File sdDir = null;
		sdDir = Environment.getRootDirectory();// 获取跟目�?
		return sdDir.toString();
	}

	/**
	 * 计算float 4�?�?
	 * 
	 * @param context
	 * @param selfId
	 * @return
	 */
	public static float getFloatToInt(float start) {
		// 结束的int�?
		float end = 0;
		// 设置位数
		int scale = 0;
		// 表示四舍五入，可以�?择其他舍值方�?
		int roundingMode = 4;
		BigDecimal bd = new BigDecimal((double) start);
		bd = bd.setScale(scale, roundingMode);
		end = bd.floatValue();
		return end;
	}

	// 获得sdcard根目�?
	public static String getExternalPath() {
		try {// read property
			File externalFile = Environment.getExternalStorageDirectory();
			String externalPath = externalFile.getAbsolutePath();
			return externalPath;
		} catch (Exception e) {
			return null;
		}
	}

	// 读取property
	public static Properties getProperty(Context context, String path) {
		PropertyReader pReader = null;
		Properties pro = null;
		File externalFile = Environment.getExternalStorageDirectory();
		String externalPath = externalFile.getAbsolutePath();
		File file = new File(path);
		if (file.exists()) {
			try {// read property
				pReader = new PropertyReader(context, path);
				pro = pReader.loadConfig();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return pro;
		} else {
			return null;
		}
	}

}
package com.tbs.tbssms.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.tbs.ini.IniFile;
import com.tbs.tbssms.R;
import com.tbs.tbssms.adapter.AddressAdapter;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.database.ADDRESS_BOX;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.entity.AddressEntity;
import com.tbs.tbssms.entity.Location;
import com.tbs.tbssms.util.PinyinComparator;
import com.tbs.tbssms.util.Util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaunchActivity extends Activity {

	protected static final String TAG = "LaunchActivity";

	private BroadcastReceiver MyBroadCastReceiver;

	private final Context mContext = this;
	private String[] node = null;
	private IniFile m_iniFileIO = null;

	List<Location> loc;//
	boolean launchState;// 是否为第一次启动标记
	Intent intent;
	String url = "";
	ImageView launcher_iv;
	LayoutInflater inflater;
	boolean launch_http = false;
	boolean oaServerStare = false;
	boolean imsMisServerStare = false;
	boolean dbkServerStare = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		init();
	}

	private void init() {
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
					finish();
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);

		m_iniFileIO = new IniFile();

		inflater = LayoutInflater.from(LaunchActivity.this);
		launcher_iv = (ImageView) findViewById(R.id.launcher_imageview1);

		Display currDisplay = getWindowManager().getDefaultDisplay();// 获取屏幕当前分辨率
		Constants.displayWidth = currDisplay.getWidth();// 宽度
		Constants.displayHeight = currDisplay.getHeight();// 高度

		// 通讯录准备代码
		Constants.toastView = inflater.inflate(R.layout.address_dialog, null);

		MyAsyncTask task = new MyAsyncTask(this);// 启动异步加载功能
		task.execute();
	}

	public boolean getShareperference(String perferenceName) {
		boolean curLaunchState = false;
		SharedPreferences setting = getSharedPreferences(perferenceName,
				MODE_PRIVATE);
		curLaunchState = setting.getBoolean("launchState", false);
		return curLaunchState;
	}

	public AddressEntity[] getArray() {// 转换数组
		List<AddressEntity> list = getAdressCursor();
		Constants.arrays = (AddressEntity[]) list
				.toArray(new AddressEntity[list.size()]);
		return Constants.arrays;
	}

	// 查询本地数据库
	public List<AddressEntity> getAdressCursor() {
		Constants.list = new ArrayList<AddressEntity>();
		Cursor cursor = new DataBaseUtil(this).getDataBase().query(
				ADDRESS_BOX.TABLE_NAME, null, null, null, null, null, "_id"); // 获得_id属性
		while (cursor.moveToNext()) {
			AddressEntity entity = new AddressEntity();// 实例化实体
			entity.setRaw_contact_id(cursor.getInt(cursor.getColumnIndex("_id")));// 存储id
			entity.setName(cursor.getString(cursor.getColumnIndex("name")));
			entity.setPhone(cursor.getString(cursor
					.getColumnIndex("mobel_number")));
			entity.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			Constants.list.add(entity);
		}
		cursor.close();
		return Constants.list;
	}

	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		if (nameChar[0] > 128) {
			try {
				pinyinName += PinyinHelper.toHanyuPinyinStringArray(
						nameChar[0], defaultFormat)[0].charAt(0);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else {
			pinyinName += nameChar[0];
		}
		return pinyinName.toUpperCase();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (launchState == false) {// 等于false跳转到导航导航界面
				intent = new Intent();
				intent.setClass(LaunchActivity.this, LauncherActivity.class);
				startActivity(intent);
			} else {// 等于true跳转到软件主界面
				intent = new Intent();
				intent.setClass(LaunchActivity.this, MainTab.class);
				startActivity(intent);
			}
			finish();
		}

	};

	public void copyConfig(Context context) {
		// 检查confige文件是否存在
		File configePath = new File(Constants.EXTERNALPATH
				+ Constants.soft_path+"/Server/TbsSmsServer/config");
		if (!configePath.exists()) {
			configePath.mkdirs();
		}
		File configeFile = new File(Constants.EXTERNALPATH + Constants.INIURL);
		if (!configeFile.exists()||launchState == false) {
			try {
				InputStream is = context.getAssets().open(
						"TbsSmsMobile/config/TbsSmsServer.ini");
				OutputStream os = new FileOutputStream(Constants.EXTERNALPATH
						+ Constants.INIURL);// 输出流
				byte[] buffer = new byte[1024];// 文件写入
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				// 关闭文件流
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {

		private static final String TAG = "MyAsyncTask";
		Context context;

		public MyAsyncTask(Context c) {
			this.context = c;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "onPreExecute");
			// 获得xml的配置的链接
			loc = Util.getProperty(context);
			url = loc.get(0).getUrl();
			// 查询preference
			launchState = getShareperference(Constants.SHARED_PREFERENCE_FIRST_LAUNCH);
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@SuppressWarnings("unchecked")
		@Override
		protected Integer doInBackground(Void... params) {
			Log.d(TAG, "doInBackground");

			String databasePath = getApplicationContext().getDatabasePath(
					"data").getAbsolutePath();
			databasePath = databasePath.substring(0, databasePath.length()
					- ("data".length() + 1));

			// 检查database文件是否存在
			File databaseFile = new File(databasePath);
			if (!databaseFile.exists()) {
				databaseFile.mkdirs();
				try {
					InputStream is = getBaseContext().getAssets().open(
							"TbsSmsMobile/database/TbsSms.db");
					// 输出流
					OutputStream os = new FileOutputStream(databasePath
							+ "/TbsSms.db");
					// 文件写入
					byte[] buffer = new byte[1024];
					int length;
					while ((length = is.read(buffer)) > 0) {
						os.write(buffer, 0, length);
					}
					// 关闭文件流
					os.flush();
					os.close();
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			copyConfig(context);

			getArray();// 通讯录内容转数组

			Arrays.sort(Constants.arrays, new PinyinComparator());// 排序(实现了中英文混排)

			Constants.pinyinFirst.clear();
			for (int j = 0; j < Constants.arrays.length; j++) {
				String name = converterToFirstSpell(Constants.arrays[j]
						.getName());
				Constants.pinyinFirst.add(name);
			}

			Constants.adapter = new AddressAdapter(LaunchActivity.this,
					Constants.arrays);

			// Constants.smsAdapter = new MessageAdapter(LaunchActivity.this,
			// getMessageCursor());//查询数据库中短信息分组

			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(Integer integer) {
			Log.d(TAG, "onPostExecute");
			String serverAutoState = m_iniFileIO.getIniString(
					Constants.EXTERNALPATH + Constants.INIURL,
					Constants.AUTOSTART, Constants.SERVER_AUTOSTART, "",
					(byte) 0);
			if (serverAutoState != null && serverAutoState.equals("false")) {
				launch_http = false;
			} else if (serverAutoState != null
					&& serverAutoState.equals("true")) {
				launch_http = true;
				String oaAutoState = m_iniFileIO.getIniString(
						Constants.EXTERNALPATH + Constants.INIURL,
						Constants.AUTOSTART, Constants.OA_AUTOSTART, "",
						(byte) 0);
				Log.d(TAG, "oaAutoState:" + oaAutoState);
				if (oaAutoState != null && oaAutoState.equals("false")) {
					oaServerStare = false;
				} else if (oaAutoState != null && oaAutoState.equals("true")) {
					oaServerStare = true;
				}
				String misImsAutoState = m_iniFileIO.getIniString(
						Constants.EXTERNALPATH + Constants.INIURL,
						Constants.AUTOSTART, Constants.IMS_MIS_AUTOSTART, "",
						(byte) 0);
				Log.d(TAG, "misImsAutoState:" + misImsAutoState);
				if (misImsAutoState != null && misImsAutoState.equals("false")) {
					imsMisServerStare = false;
				} else if (misImsAutoState != null
						&& misImsAutoState.equals("true")) {
					imsMisServerStare = true;
				}
				String dbkAutoState = m_iniFileIO.getIniString(
						Constants.EXTERNALPATH + Constants.INIURL,
						Constants.AUTOSTART, Constants.DBK_AUTOSTART, "",
						(byte) 0);
				Log.d(TAG, "dbkAutoState:" + dbkAutoState);
				if (dbkAutoState != null && dbkAutoState.equals("false")) {
					dbkServerStare = false;
				} else if (dbkAutoState != null && dbkAutoState.equals("true")) {
					dbkServerStare = true;
				}
			}
			Log.d(TAG, "launch_http:" + launch_http);
			if (launch_http) {
				if (oaServerStare && Constants.HttpServerState == false) {
					sendBroadcast(new Intent(Constants.HTTPSTARTSERVICE));
				}
				if (imsMisServerStare && Constants.MisServerState == false) {
					sendBroadcast(new Intent(Constants.MISSTARTSERVICE));
				}
				if (dbkServerStare && Constants.DbkServerState == false) {
					sendBroadcast(new Intent(Constants.DBKSTARTSERVICE));
				}
			}
			handler.sendEmptyMessageDelayed(new Message().what, 50);
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.d(TAG, "onProgressUpdate");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (MyBroadCastReceiver != null) {
			unregisterReceiver(MyBroadCastReceiver);
		}
	}
}

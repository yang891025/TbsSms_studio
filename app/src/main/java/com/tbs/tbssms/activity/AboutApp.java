package com.tbs.tbssms.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.install.install;
import com.tbs.tbssms.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@SuppressLint("CutPasteId")
public class AboutApp extends Activity {

	private static final String TAG = "AboutApp";
	private Button backBtn;
	private RelativeLayout systemBtn, helpBtn, updateBtn;
	
	/**
	 * 更新软件需要用到的参数
	 */
	private Runnable mrun;
	private NotificationManager mNotificationManager;
	private Notification notification;
	private RemoteViews remoteviews;
	private int count;
	private int loadversion;
	private float version;
	String versionUrl;
	String downloadApkUrl;
	public static String url = "";
    private TextView textView1;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_chatbg_layout);
		AppManager.getInstance().addActivity(this);
		init();
	}

	private void init() {
		backBtn = (Button) findViewById(R.id.backBtn);
		systemBtn = (RelativeLayout) findViewById(R.id.more_setting);
		helpBtn = (RelativeLayout) findViewById(R.id.notifi_setting);
		updateBtn = (RelativeLayout) findViewById(R.id.update_app);
        textView1 = (TextView) findViewById(R.id.textView1);
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0);
            // mVersion = (TextView) findViewById(R.id.about_version);
            textView1.setText(getString(R.string.app_name) + "  版本:"
                    + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
		systemBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutApp.this, WebViewActivity.class);
				intent.putExtra("tempUrl", getHtml("系统通知", "", "", "当前没有系统消息。"));
				intent.putExtra("titleStr", "系统通知");
				startActivity(intent);
			}
		});

		helpBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutApp.this, WebViewActivity.class);
				intent.putExtra("tempUrl", getHtml("常见问题", "", "", "  安装软件后，打开软件，首先进入设置功能，点击高级设置，进入登录设置选项。输入需要登录的账号，密码，以及服务地址，点击完成保存内容。<br>  登录设置完成后在设置界面点击手动开关转发服务，启动后台服务即可。<br>  也可以进入高级设置中将后台服务设置为自动启动转发服务，也可以将软件设置为自动启动。"));
				intent.putExtra("titleStr", "帮助与反馈");
				startActivity(intent);
			}
		});

		updateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				upgrade();
			}
		});

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	public String getHtml(String title, String publisher, String date,String body){
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE HTML>");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta charset=\"UTF-8\"/>");
		sb.append("<meta http-equiv=\"Cache-Control\" content=\"no-cache\"/>");
		sb.append("<meta name=\"viewport\" content=\"width=device-width, minimum-scale=1.0, maximum-scale=2.0\"/>");
		sb.append("<title>新闻</title>");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/TbsSmsMobile/web/tbsmcs_news.css\"/>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<a name='topA'></a>");
		sb.append("<div class=\"newsTitle\">");
		sb.append("<h1>");
		sb.append(title);
		sb.append("</h1>");
		sb.append("<p class=\"prot\"><span class=\"from\">");
		sb.append(publisher);
		sb.append("</span>&nbsp;&nbsp;");
		sb.append(date);
		sb.append("</p>");
		sb.append("</div>");
		sb.append("<div>");
		sb.append("<div class=\"newsContent\">");
		sb.append("<div style=\"text-align:center;padding-top:10px;color:#787878;font-size:13px;\">");
		sb.append("<br/></div>");
		sb.append(body);
		sb.append("</div>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	// 升级按钮点击事件的方法 通过服务器来解析JSON 得到版本号 判断是否来通知下载
	private void upgrade() {
		PackageManager nPackageManager = getPackageManager();
		try {
			// 得到包管理器
			PackageInfo nPackageInfo = nPackageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
			loadversion = nPackageInfo.versionCode;// 得到现在app的版本号
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
			// 异常弹出dialog
			new AlertDialog.Builder(this).setTitle("更新异常").setMessage("更新异常,请稍后再试").setNegativeButton("确定", null).show();
		}
		versionUrl = url + Constants.APP_VERSION_URL;// 获取链接
		Log.d(TAG, "versionUrl : " + versionUrl);
		MyAsyncTask updateTextTask = new MyAsyncTask(this);// 实例化下载版本信息文本
		updateTextTask.execute();
	}

	// 检查更新版本
	public void checkVersion() {
		// 发现新版本，提示用户更新
		AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
		// LayoutInflater inflater = getLayoutInflater();
		builder2.setTitle("发现新版本").setMessage("当前发现新版本是否更新?")
				.setInverseBackgroundForced(true).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						sendNotification();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert2 = builder2.create();
		alert2.show();
	}

	final Handler mhandler = new Handler();

	// 此方法来发送下载通知 采用的是自定义通知栏 并且更加下载的进度来刷新进度条
	// 自定义通知的方法 在上上篇的博文中 这里不做太多的解释
	@SuppressWarnings("deprecation")
	private void sendNotification() {

		// 设置notification的界面
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);// 获取系统服务
		notification = new Notification(R.drawable.about_image_bg, "法律法规信息软件",System.currentTimeMillis());// 设置通知icon，tiltle
		remoteviews = new RemoteViews("com.tbs.tbssms", R.layout.update_notiifcation);
		remoteviews.setImageViewResource(R.id.imageview, R.drawable.about_image_bg);
		notification.contentView = remoteviews;

		// PendingIntent
		Intent intent = new Intent(this, install.class);
		// 调用的系统的安装隐士意图 后面红色的代码
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0,intent, 0);
		notification.contentIntent = pendingintent;

		mrun = new Runnable() {// 这个Runnable 用来根据下载进度来刷新进度条

			@Override
			public void run() {
				if (count < 98) {
					// 紫色的count 是异步下载计算出来设置进度的值
					remoteviews.setTextViewText(R.id.textView1, "下载完成：" + count + "%");
					mNotificationManager.notify(8888, notification);// 刷新通知界面
					mhandler.postDelayed(mrun, 300);
				} else {
					// 这里计算出来的count 不是那么准确 所以下载完成后 给一个固定值做为下载完成
					remoteviews.setTextViewText(R.id.textView1, "下载完成：100%");
					mNotificationManager.notify(8888, notification);// 刷新通知界面
					Toast.makeText(AboutApp.this, "下载已完成",Toast.LENGTH_SHORT).show();// 提示用户下载成功
				}
			}
		};
		mhandler.postDelayed(mrun, 300);
		Update_AsyncTask mUpdate_AsyncTask = new Update_AsyncTask();// 实例化异步任务
		try {
			downloadApkUrl = url + Constants.APK_FILE_URL;// 拼接下载地址
			Log.d(TAG, "downloadApkUrl : " + downloadApkUrl);
			mUpdate_AsyncTask.execute(new URL(downloadApkUrl));// 启动异步任务，开始下载
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	// 这个内部类用来异步下载新版本app 通过服务端下载这里不多说了
	class Update_AsyncTask extends AsyncTask<URL, Integer, Object> {

		@Override
		protected Object doInBackground(URL... params) {
			try {
				URLConnection con = params[0].openConnection();
				if (HttpURLConnection.HTTP_OK != ((HttpURLConnection) con)
						.getResponseCode()) {
					mhandler.removeCallbacks(mrun);
					mNotificationManager.cancel(8888);
					handler.sendMessage(new Message());
					return null;
				}
				InputStream is = con.getInputStream();
				int contentlength = con.getContentLength();// 得到下载的总长度
				File file = new File(Constants.APK_SAVEFILE_URL);
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(file);
				int current = 0;
				int x = 0;
				byte[] arr = new byte[1024];
				while ((current = is.read(arr)) != -1) {
					out.write(arr, 0, current);
					x = x + current;
					// 计算下载的百分百
					count = (int) (100 * x / contentlength);
				}
				is.close();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}
	}

	// 异步任务,执行版本检查工作
	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {

		private Context context;

		MyAsyncTask(Context context) {
			this.context = context;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {

		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected Integer doInBackground(Void... params) {
			// 服务器端通过GET 得到JSON
			version = Util.doPost(versionUrl);
			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(Integer integer) {
			if ((loadversion < version)) {
				checkVersion();// 如果有新版本发送通知开始下载
			} else {
				// 如果没有 弹出对话框告知用户
				new AlertDialog.Builder(context).setTitle("版本检查").setMessage("当前版本为最新版本").setNegativeButton("确定", null).show();
			}
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}

	// 更新失败提醒
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(AboutApp.this, "很遗憾更新失败了", Toast.LENGTH_LONG).show();
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
	}
}
package com.tbs.tbssms.activity;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbssms.R;
import com.tbs.tbssms.common.AppManager;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.util.Util;

public class WebViewActivity extends Activity implements OnClickListener {

	private static final String TAG = "UpdateEmailActivity";

	Message msg;// 信息
	InputStream inputStreams;// 输入流
	FileInputStream fileInputStreams;// 文件输入流
	long result = -1;
	boolean currentFileState = false;
	Intent intent;
	Button goBtn, returnBtn, refreshBtn;
	Button backBtn;
	WebView wv;
	ProgressBar pb;
	private Timer timer;
	String tempUrl, titleStr;
	static String message;
	OnClickListener listener;
	String url;
	Handler Handler = new Handler();
	TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		AppManager.getInstance().addActivity(this);
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		// 从intent中获得extr
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			tempUrl = intent.getStringExtra("tempUrl");
			titleStr = intent.getStringExtra("titleStr");
		}

		goBtn = (Button) findViewById(R.id.web_forward);
		returnBtn = (Button) findViewById(R.id.web_back);
		refreshBtn = (Button) findViewById(R.id.web_refresh);

		backBtn = (Button) findViewById(R.id.backBtn);

		goBtn.setOnClickListener(this);
		returnBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);

		wv = (WebView) findViewById(R.id.webview);
		pb = (ProgressBar) findViewById(R.id.progressBar);

		title = (TextView) findViewById(R.id.textview);

		title.setText(titleStr);

		// 设置webview
		WebSettings webSettings = wv.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 设置编码格式
		webSettings.setDefaultTextEncodingName("gb2312");
		// 缩放开关
		webSettings.setSupportZoom(true);
		// 设置是否可缩放
		webSettings.setBuiltInZoomControls(true);
		// 取消拖动后产生的放大镜
		webSettings.setDisplayZoomControls(false);
		// 设置双击放大缩小
		webSettings.setUseWideViewPort(true);
		// webview.setInitialScale(57);
		webSettings.setLoadWithOverviewMode(true);
		wv.setClickable(true);
		wv.setLongClickable(true);
		// 取消滚动条
		wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

		// 实现点击加载页面在本webview内载入
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				wv.loadUrl(url);
				return true;
			}

			/*
			 * 创建一个WebViewClient,重写onPageStarted和onPageFinished
			 * onPageStarted中启动一个计时器,到达设置时间后利用handle发送消息给activity执行超时后的动作.
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				timer = new Timer();
				TimerTask tt = new TimerTask() {
					@Override
					public void run() {
						/*
						 * 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
						 */
						if (pb.getProgress() < pb.getMax()) {
							wv.stopLoading();
							sendMyMessage(Constants.TIMEOUT, 0);
							timer.cancel();
							timer.purge();
						}
					}
				};
				timer.schedule(tt, Constants.timeout, 1);
			}

			/**
			 * onPageFinished指页面加载完成,完成后取消计时器
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				timer.cancel();
				timer.purge();
			}
		});
		wv.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				pb.setVisibility(0);
				refreshBtn.setVisibility(4);
				if (progress == 100) {
					pb.setVisibility(4);
					refreshBtn.setVisibility(0);
					if (wv.canGoBack()) {
						returnBtn
								.setBackgroundResource(R.drawable.webview_tab_back_btn);
						returnBtn.setOnClickListener(listener);
					} else {
						returnBtn
								.setBackgroundResource(R.drawable.webviewtab_back_disable);
						returnBtn.setOnClickListener(null);
					}
					if (wv.canGoForward()) {
						goBtn.setBackgroundResource(R.drawable.webview_tab_forward_btn);
						goBtn.setOnClickListener(listener);
					} else {
						goBtn.setBackgroundResource(R.drawable.webviewtab_forward_disable);
						goBtn.setOnClickListener(null);
					}

				}
			}
		});

		wv.setFocusable(true);
		wv.requestFocus();
		// wv.loadUrl(tempUrl);//加载页面地址
		wv.loadDataWithBaseURL("file:///android_asset", tempUrl, "text/html",
				"utf-8", null);

		listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.web_back:
					wv.goBack();
					break;

				case R.id.web_forward:
					wv.goForward();
					break;
				}
			}
		};
	}

	Handler mhandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case Constants.TIMEOUT:
				Toast.makeText(getApplicationContext(), "加载页面失败,请刷新页面并重试！",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	// 发送message方法
	public void sendMyMessage(int stateStr, int time) {
		android.os.Message msg2 = new android.os.Message();
		msg2.what = stateStr;
		mhandler.sendMessageDelayed(msg2, time);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_fast, R.anim.push_up);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.backBtn:
			finish();
			overridePendingTransition(R.anim.push_fast, R.anim.push_up);
			break;

		case R.id.web_refresh:
			// wv.reload();
			wv.loadDataWithBaseURL("file:///android_asset", tempUrl,
					"text/html", "utf-8", null);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
	}
}

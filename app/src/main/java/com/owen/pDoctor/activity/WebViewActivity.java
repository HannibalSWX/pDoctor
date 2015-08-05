package com.owen.pDoctor.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;

/**
 * ClassName：WebViewActivity
 * Description：WebView
 * Author ： zhouqiang
 * Date ：2015-1-22 上午10:27:21
 * Copyright (C) 2012-2014 owen
 */
public class WebViewActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	private LinearLayout back_btn;

	/**
	 * Webview
	 */
	private WebView webview = null;
	private ProgressDialog pd;

	private String tit, link;

	private TextView tittle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_webview);

		Intent intent = getIntent();
		tit = intent.getStringExtra("tittle");
		link = intent.getStringExtra("link");

		initView();
		setListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		tittle = (TextView) findViewById(R.id.tittle);
		webview = (WebView) findViewById(R.id.webview);
		tittle.setText(tit);
		setWebview();
		if (link != null && !"".equals(link)) {
			if (!link.contains("http")) {
				loadUrl("http://" + link);
			} else{
				loadUrl(link);
			}
		}
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
	}

	// 图标的点击事件
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.back_btn :
				/** 返回按钮 **/
				finish();
				break;

			default :
				break;
		}
	}

	/**
	 * 设置webView
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public void setWebview() {
		runOnUiThread(new Runnable() {
			@SuppressLint("NewApi")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				webview.setWebViewClient(new webViewClient());
				webview.getSettings().setJavaScriptEnabled(true);
				webview.getSettings().setCacheMode(1);
				webview.removeJavascriptInterface("searchBoxJavaBredge_");
//				webview.addJavascriptInterface(this, "javatojs");
			}
		});
	}
	/**
	 * 加载URL
	 * @param url
	 */
	private void loadUrl(final String url) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webview.loadUrl(url);
			}
		});
	}

	class webViewClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {

			super.onPageFinished(view, url);
			if (pd != null) {
				pd.dismiss();// 关闭ProgressDialog
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (mContext != null && !((Activity) mContext).isFinishing()) {
				try {
					pd = ProgressDialog.show(mContext,
							getString(R.string.more), "加载中，请稍候……");
					pd.setCancelable(true);
				} catch (Exception e) {
				}
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}

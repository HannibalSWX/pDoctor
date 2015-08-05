package com.owen.pDoctor.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.nl.base.http.HttpUtil;
import com.nl.base.task.GenericTask;
import com.nl.base.task.TaskAdapter;
import com.nl.base.task.TaskListener;
import com.nl.base.task.TaskParams;
import com.nl.base.task.TaskResult;
import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.db.MySQLiteOpenHelper;
import com.owen.pDoctor.model.AdvertisementWelcome;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ToastUtil;

/**
 * ClassName：AdvertismentActivity
 * Description：广告页面
 * Author ： zhouqiang
 * Date ：2015-1-21 下午8:18:18
 * Copyright (C) 2012-2014 owen
 */
public class AdvertismentActivity extends BaseActivity {

	private Context mContext;
	
	private DisplayMetrics metric = new DisplayMetrics();
	
	private int width, height;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private AdvertisementWelcome advertisement = new AdvertisementWelcome();

	private LinearLayout first_bg;
	
	/**
	 * Webview
	 */
	private WebView webview = null;
	
//	private CustomProgressDialog progressDialog = null;

	private String message, code;
	
	private MySQLiteOpenHelper dbOpenHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_advertisment);

		// 获取屏幕宽高度（像素）设置广告栏高度
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
				
		initAppVersion();
		createDb();
		
		initview();
		getadvertisment();
	}

	private void createDb() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = getSharedPreferences(
				Constants.PUBLIC_DBCREATE, MODE_PRIVATE);
		if (!sharedPreferences.getBoolean(Constants.DB_ISCREATED, false)) {
			Editor editor = sharedPreferences.edit();
			dbOpenHelper = new MySQLiteOpenHelper(mContext);
			editor.putBoolean(Constants.DB_ISCREATED, true);
			editor.commit();
		}
	}

	private void initview() {
		first_bg = (LinearLayout) findViewById(R.id.first_bg);
		webview = (WebView) findViewById(R.id.webview);
		first_bg.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 初始化App版本信息
	 */
	private void initAppVersion() {
		PackageManager manager = mContext.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			Constants.APP_VERSION_CODE = info.versionCode;
			Constants.APP_VERSION_NAME = info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void getadvertisment() {
		GetHeatTask task = new GetHeatTask();
		task.setListener(getheatlistener);
		TaskParams params = new TaskParams();
//		params.put("page", page);
		task.execute(params);
	}

	private TaskListener getheatlistener = new TaskAdapter() {

		@Override
		public void onPreExecute(GenericTask task) {
//			if (progressDialog == null) {
//				progressDialog = CustomProgressDialog.createDialog(mContext);
//				progressDialog.show();
//			}
		}

		@Override
		public void onProgressUpdate(GenericTask task, Object param) {

		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
//			if (progressDialog != null) {
//				progressDialog.dismiss();
//				progressDialog = null;
//			}
			if (result == TaskResult.OK) {
				// dismissProgressDialog();
				setWebview();
				if (advertisement.getAdvertUrl() != null && !"".equals(advertisement.getAdvertUrl())) {
					loadUrl(advertisement.getAdvertUrl());
				}
				if (!message.equals("")) {
					ToastUtil.showToast(mContext, message);
				}
			} else if (result == TaskResult.CANCELLED) {
				// dismissProgressDialog();
			} else if(result == TaskResult.FAILED) {
				// 跳到主页面
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}

		@Override
		public String getName() {
			return "getWarnData";
		}
	};

	// // 获取广告
	private class GetHeatTask extends GenericTask {
		private String msg = "数据加载中...";
		public String getMsg() {
			return msg;
		}
		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			TaskParams param = params[0];
//			String page = param.get("page").toString();
			try {
				Map<String, String> listMap = new HashMap<String, String>();
				listMap.put("adverType", "3");
				String url = Constants.SERVER_URL + Constants.ADS_HOME_URL;
				String result = HttpUtil.postRequest(url, listMap, false);
				JSONObject Jsonresult = new JSONObject(result);
				String code = Jsonresult.getString("code");
				JSONObject data = Jsonresult.getJSONObject("data");
				message = Jsonresult.getString("msg");
				JSONObject obj = data.getJSONObject("obj");
				
				advertisement.setAdvertUrl(obj.getString("adLink"));
				advertisement.setStatus(obj.getString("status"));
				if (code.equals("200")) {
					return TaskResult.OK;
				} else {
					return TaskResult.FAILED;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			}
		}
	}

	/**
	 * 设置webView
	 */
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	public void setWebview() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				webview.setWebViewClient(new webViewClient());
				webview.getSettings().setJavaScriptEnabled(true);
//				webview.getSettings().setCacheMode(1); // 保留缓存
				webview.getSettings().setCacheMode(webview.getSettings().LOAD_NO_CACHE); // 清除缓存
				webview.clearHistory();
				webview.clearFormData();
				webview.clearCache(true);
				
				//自适应屏幕
				webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
				webview.getSettings().setUseWideViewPort(true);
				webview.getSettings().setLoadWithOverviewMode(true);
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
				System.out.println("html url======" + url);
				webview.loadUrl(url);
			}
		});
	}

	class webViewClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {

			super.onPageFinished(view, url);
//			if (progressDialog != null){
//				progressDialog.dismiss();
//				progressDialog = null;
//			}
			first_bg.setVisibility(View.GONE);
			webview.setVisibility(View.VISIBLE);
			new Thread(init).start();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (mContext != null && !((Activity) mContext).isFinishing()) {
				try {
//				if (progressDialog == null){
//					progressDialog = CustomProgressDialog.createDialog(mContext);
//				}
//		    	progressDialog.show();
				} catch (Exception e) {
					e.printStackTrace();
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
	
	// 欢迎页面。2秒后跳转
	private Runnable init = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				// 跳到主页面
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}

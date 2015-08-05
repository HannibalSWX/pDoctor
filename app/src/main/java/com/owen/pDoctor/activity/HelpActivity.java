package com.owen.pDoctor.activity;

import java.util.HashMap;
import java.util.List;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ClassName：SettingsActivity
 * Description：个人设置页面
 * Author ： zhouqiang
 * Date ：2015-7-12 下午12:37:51
 */
public class HelpActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	public List<String> urls;
	Uri uri;
	Intent intent;
	int gallerypisition = 0;

	private String usename = null;

	private RelativeLayout rl_wenti, rl_zhuce, rl_huanzhe, rl_huanjiao, rl_my;

	private Dialog dialog;

	private LinearLayout back_btn, ll_immediately, ll_later;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;
	
	private String link, version, message, code;

	public static HelpActivity hActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		hActivity = this;
		setContentView(R.layout.activity_help);

		initAppVersion();
		initView();
		setListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		rl_wenti = (RelativeLayout) findViewById(R.id.rl_wenti);
		rl_zhuce = (RelativeLayout) findViewById(R.id.rl_zhuce);
		rl_huanzhe = (RelativeLayout) findViewById(R.id.rl_huanzhe);
		rl_huanjiao = (RelativeLayout) findViewById(R.id.rl_huanjiao);
		rl_my = (RelativeLayout) findViewById(R.id.rl_my);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		rl_wenti.setOnClickListener(this);
		rl_zhuce.setOnClickListener(this);
		rl_huanzhe.setOnClickListener(this);
		rl_huanjiao.setOnClickListener(this);
		rl_my.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
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
	
	// 图标的点击事件
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn :
				/** 返回按钮 **/
				finish();
			break;
			
			case R.id.rl_wenti :
				/** 常见问题**/
				startActivity(new Intent(HelpActivity.this,
							ProblemsActivity.class));
				break;

			case R.id.rl_zhuce :
				/** 注册与账号 **/
				startActivity(new Intent(HelpActivity.this,
						RegistActivity.class));
				break;

			case R.id.rl_huanzhe :
				/** 患者 **/
				Intent intent = new Intent(mContext, MyPatientActivity.class);
				intent.putExtra("from", "help");
				startActivity(intent);
				break;

			case R.id.rl_huanjiao :
				/** 患教 **/
				MainActivity.mActivity.setTab(2);
				if (SettingsActivity.sActivity != null) {
					SettingsActivity.sActivity.finishMe();
				}
				finish();
				break;

			case R.id.rl_my :
				/** 我的 **/
				MainActivity.mActivity.setTab(3);
				if (SettingsActivity.sActivity != null) {
					SettingsActivity.sActivity.finishMe();
				}
				finish();
				break;

			default :
				break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void finishMe() {
		// TODO Auto-generated method stub
		finish();
	}
}

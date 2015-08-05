package com.owen.pDoctor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.db.MySQLiteOpenHelper;
import com.owen.pDoctor.util.Constants;

/**
 * ClassName：WelcomeActivity
 * Description：欢迎页面，1秒后跳转
 * Author ： zhouqiang
 * Date ：2015-1-21 下午8:18:18
 * Copyright (C) 2012-2014 owen
 */
public class WelcomeActivity extends BaseActivity {

	private Context mContext;

	private TextView version_tv;

	private MySQLiteOpenHelper dbOpenHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_welcome);

		initAppVersion();
		initview();

		new Thread(init).start();
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

	private void initview() {
		version_tv = (TextView) findViewById(R.id.version_tv);
		version_tv.setText("版本：Ver " + Constants.APP_VERSION_NAME);
	}

	// 欢迎页面。1秒后跳转
	private Runnable init = new Runnable() {
		@Override
		public void run() {
			try {
				SharedPreferences sharedPreferences = getSharedPreferences(
						Constants.PUBLIC_DBCREATE, MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				if (!sharedPreferences.getBoolean(Constants.DB_ISCREATED, false)) {
					dbOpenHelper = new MySQLiteOpenHelper(mContext);
					editor.putBoolean(Constants.DB_ISCREATED, true);
					editor.commit();
				}
				Thread.sleep(1000);
				goLogin();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 判断是否需要进入登录页面
	 */
	// private void checkLogin() {
	// SharedPreferences preference = getSharedPreferences(
	// Constants.ICRM_USERINFO, MODE_PRIVATE);
	// SharedPreferences.Editor preferEditor = preference.edit();
	//
	// if (Constants.LASTLOGINTIME != null){
	// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
	// Locale.getDefault());
	// try{
	// Date lastDate = dateFormat.parse(Constants.LASTLOGINTIME);
	// Date nowDate = new Date();
	// long off = nowDate.getTime() - lastDate.getTime();
	// long hours = off / 1000 / 60 / 60;
	// if (hours > 48){
	// preferEditor.remove(Constants.PASSWORD);
	// preferEditor.commit();
	// goLogin();
	// return;
	// }
	// }catch (Exception e){
	// e.printStackTrace();
	// }
	// }
	//
	// String nameBuf = preference.getString(Constants.USERNAME, null);
	// String passBuf = preference.getString(Constants.PASSWORD, null);
	// if (nameBuf != null && passBuf != null) { // 已经记住了用户名和密码
	// nameBuf = AESUtils.decrypt(nameBuf);
	// passBuf = AESUtils.decrypt(passBuf);
	// gotoLoginingPage(nameBuf, passBuf);
	// } else { // 没有记录用户名和密码
	// goLogin();
	// }
	// }

	/**
	 * 跳到登陆验证等待页面
	 * 
	 */
	// private void gotoLoginingPage(String userName, String password) {
	// Intent intent = new Intent(this, LoginingActivity.class);
	// intent.putExtra("userName", userName);
	// intent.putExtra("password", password);
	// startActivity(intent);
	// finish();
	// }

	/**
	 * 跳到主页面
	 * 
	 */
	private void goLogin() {
		Intent intent = new Intent(this, AdvertismentActivity.class);
		startActivity(intent);
		finish();
	};
}

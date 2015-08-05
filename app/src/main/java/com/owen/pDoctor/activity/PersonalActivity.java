package com.owen.pDoctor.activity;

import java.util.HashMap;
import java.util.List;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ToastUtil;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ClassName：SettingsActivity Description：个人设置页面 Author ： zhouqiang Date
 * ：2015-7-12 下午10:37:51
 */
public class PersonalActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	public List<String> urls;
	Uri uri;
	Intent intent;
	int gallerypisition = 0;

	private String userName = null;

	private String loginName;

	private ImageView im_me, im_my_more;

	private TextView user_name, user_no;

	private RelativeLayout rl_my_info, rl_my_erweima, rl_docter_auth, rl_my_service, rl_my_huodong, rl_settings,
			rl_help, rl_feedback, rl_contact;

	private Dialog dialog;

	private LinearLayout ll_islogin, ll_immediately, ll_later;

	private SharedPreferences sp;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String uid, link, version, message, code, qrcode;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String type = intent.getStringExtra("type");
			if (type.equals("reset")) {
				sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
				uid = sp.getString("uid", "");
				userName = sp.getString("userName", "");
				if (!userName.equals("")) {
					user_name.setText(userName);
					user_no.setText("百姓号：" + userName);
				} else {
					user_name.setText("点击登录/注册");
					user_no.setText("百姓号：");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_personal_center);
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		userName = sp.getString("userName", "");
		loginName = sp.getString("loginName", "");

		initAppVersion();
		initView();
		setListener();
		
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.RESET_BRAODCAST);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private void initView() {
		// TODO Auto-generated method stub
		im_me = (ImageView) findViewById(R.id.im_me);
		ll_islogin = (LinearLayout) findViewById(R.id.ll_islogin);
		im_my_more = (ImageView) findViewById(R.id.im_my_more);
		user_name = (TextView) findViewById(R.id.user_name);
		user_no = (TextView) findViewById(R.id.user_no);
		rl_my_info = (RelativeLayout) findViewById(R.id.rl_my_info);
		rl_my_erweima = (RelativeLayout) findViewById(R.id.rl_my_erweima);
		rl_docter_auth = (RelativeLayout) findViewById(R.id.rl_docter_auth);
		rl_my_service = (RelativeLayout) findViewById(R.id.rl_my_service);
		rl_my_huodong = (RelativeLayout) findViewById(R.id.rl_my_huodong);
		rl_settings = (RelativeLayout) findViewById(R.id.rl_settings);
		rl_help = (RelativeLayout) findViewById(R.id.rl_help);
		rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);
		rl_contact = (RelativeLayout) findViewById(R.id.rl_contact);
		// update_tv.setText("当前版本V" + Constants.APP_VERSION_NAME);
		if (!userName.equals("")) {
			user_name.setText(userName);
			user_no.setText("百姓号：" + uid);
		} else if (userName.equals("") && !loginName.equals("")) {
			user_name.setText(loginName);
			user_no.setText("百姓号：" + uid);
		} else {
			user_name.setText("点击登录/注册");
			user_no.setText("百姓号：");
		}
	}

	/**
	 * setListener
	 */
	private void setListener() {
		im_my_more.setOnClickListener(this);
		im_me.setOnClickListener(this);
		ll_islogin.setOnClickListener(this);
		rl_my_info.setOnClickListener(this);
		rl_my_erweima.setOnClickListener(this);
		rl_docter_auth.setOnClickListener(this);
		rl_my_service.setOnClickListener(this);
		rl_my_huodong.setOnClickListener(this);
		rl_settings.setOnClickListener(this);
		rl_help.setOnClickListener(this);
		rl_feedback.setOnClickListener(this);
		rl_contact.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		userName = sp.getString("userName", "");
		loginName = sp.getString("loginName", "");
		if (!userName.equals("")) {
			user_name.setText(userName);
			user_no.setText("百姓号：" + uid);
		} else if (userName.equals("") && !loginName.equals("")) {
			user_name.setText(loginName);
			user_no.setText("百姓号：" + uid);
		} else {
			user_name.setText("点击登录/注册");
			user_no.setText("百姓号：");
		}
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

	// 点击事件
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.im_my_more:
			/** 更多 **/
			MainActivity.mActivity.setTab(4);
			break;

		case R.id.im_me:
			/** 上传头像 **/
			if (loginName.equals("") || loginName == null) {
				ToastUtil.showToast(mContext, "请先登录");
				return;
			}
			startActivity(new Intent(PersonalActivity.this, PersonalSetActivity.class));
			break;

		case R.id.ll_islogin:
			/** 未登录跳转登录 **/
			if (loginName.equals("") || loginName == null) {
				startActivity(new Intent(PersonalActivity.this, LoginActivity.class));
			} else {
				if (!userName.equals("")) {
					ToastUtil.showToast(mContext, userName);
				} else {
					ToastUtil.showToast(mContext, loginName);
				}
			}
			break;

		case R.id.rl_my_info:
			/** 我的信息 **/
			startActivity(new Intent(PersonalActivity.this, PersonalSettingActivity.class));
			break;

		case R.id.rl_my_erweima:
			/** 我的二维码 **/
			Intent intent = new Intent(mContext, MyQRImageActivity.class);
			intent.putExtra("from", "mycenter");
			startActivity(intent);
			break;

		case R.id.rl_docter_auth:
			/** 医生认证 **/
			startActivity(new Intent(PersonalActivity.this, AuthenticationActivity.class));
			break;

		case R.id.rl_my_service:
			/** 我的服务 **/
			startActivity(new Intent(PersonalActivity.this, PayServiceActivity.class));
			break;

		case R.id.rl_my_huodong:
			/** 我的活动 **/
			startActivity(new Intent(PersonalActivity.this, MyZoneActivity.class));
			break;

		case R.id.rl_settings:
			/** 设置 **/
			startActivity(new Intent(PersonalActivity.this, SettingsActivity.class));
			break;

		case R.id.rl_help:
			/** 帮助 **/
			startActivity(new Intent(PersonalActivity.this, HelpActivity.class));
			break;
		case R.id.rl_feedback:
			/** 用户反馈 **/
			startActivity(new Intent(PersonalActivity.this, FeedBackActivity.class));
			break;

		case R.id.rl_contact:
			/** 联系客服 **/
			startActivity(new Intent(PersonalActivity.this, AboutActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
	}
}

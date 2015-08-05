package com.owen.pDoctor.activity;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.owen.pDoctor.R;
import com.owen.pDoctor.chat.FaceConversionUtil;
import com.owen.pDoctor.util.AppConstants;
import com.owen.pDoctor.util.ToastUtil;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.Tencent;

/**
 * ClassName：MainActivity Description：MainActivity Author ： owen Date ：2015-7-10
 * 下午6:58:58 Copyright (C) 2012-2014 owen
 */
@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	private RelativeLayout home;
	private RelativeLayout suifang;
	private RelativeLayout huanjiao;
	private RelativeLayout personal;
	private RelativeLayout more;

	private ImageView home_im, suifang_im, huanjiao_im, personal_im, more_im;

	private TextView home_tv, suifang_tv, huanjiao_tv, personal_tv, more_tv;

	private ImageView home_index, suifang_index, huanjiao_index,
			personal_index, more_index;

	private TabHost tabs;
	private InputMethodManager manager = null;
	// 默认显示首页
	private int focous_on = 0;

	// 是否能够退出
	private static boolean isBack = false;
	// 上次按退出的时间
	private static long downTime;

	public static final String ACTION_REFRESH = "com.owen.medical.scantohome";

	public static String mAppid;
	private Button mNewLoginButton;
	private TextView mUserInfo;
	private ImageView mUserLogo;
	private UserInfo mInfo;
	private EditText mEtAppid = null;
	public static Tencent mTencent;
	private static Intent mPrizeIntent = null;

	private String from = "";

	public static MainActivity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activitymain);
		mActivity = this;
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		new Thread(new Runnable() {
			@Override
			public void run() {
				FaceConversionUtil.getInstace().getFileText(getApplication());
			}
		}).start();

		Intent intent = getIntent();
		if (intent.getStringExtra("from") != null) {
			from = intent.getStringExtra("from");
		}

		if (TextUtils.isEmpty(mAppid)) {
			mAppid = AppConstants.APP_ID;
			mTencent = Tencent.createInstance(mAppid, this);
		} else {
			if (mTencent == null) {
				mTencent = Tencent.createInstance(mAppid, this);
			}
		}

		// 初始化控件
		home = (RelativeLayout) findViewById(R.id.rl_home);
		suifang = (RelativeLayout) findViewById(R.id.rl_suifang);
		huanjiao = (RelativeLayout) findViewById(R.id.rl_huanjiao);
		personal = (RelativeLayout) findViewById(R.id.rl_personal);
		more = (RelativeLayout) findViewById(R.id.rl_more);
		home_im = (ImageView) findViewById(R.id.home_im);
		suifang_im = (ImageView) findViewById(R.id.suifang_im);
		huanjiao_im = (ImageView) findViewById(R.id.huanjiao_im);
		personal_im = (ImageView) findViewById(R.id.personal_im);
		more_im = (ImageView) findViewById(R.id.more_im);

		home_tv = (TextView) findViewById(R.id.home_tv);
		suifang_tv = (TextView) findViewById(R.id.suifang_tv);
		huanjiao_tv = (TextView) findViewById(R.id.huanjiao_tv);
		personal_tv = (TextView) findViewById(R.id.personal_tv);
		more_tv = (TextView) findViewById(R.id.more_tv);
		home_index = (ImageView) findViewById(R.id.home_index);
		suifang_index = (ImageView) findViewById(R.id.suifang_index);
		huanjiao_index = (ImageView) findViewById(R.id.huanjiao_index);
		personal_index = (ImageView) findViewById(R.id.personal_index);
		more_index = (ImageView) findViewById(R.id.more_index);

		tabs = (TabHost) findViewById(R.id.mTabhost);
		LocalActivityManager Lam = new LocalActivityManager(this, false);
		Lam.dispatchCreate(savedInstanceState);
		tabs.setup(Lam);
		tabs.addTab(tabs.newTabSpec("tab0").setIndicator("tab0")
				.setContent(new Intent(this, HomeActivity.class)));
		tabs.addTab(tabs.newTabSpec("tab1").setIndicator("tab1")
				.setContent(new Intent(this, MyPatientActivity.class)));
		tabs.addTab(tabs.newTabSpec("tab2").setIndicator("tab2")
				.setContent(new Intent(this, HuanjiaoActivity.class)));
		tabs.addTab(tabs.newTabSpec("tab3").setIndicator("tab3")
				.setContent(new Intent(this, PersonalActivity.class)));
		tabs.addTab(tabs.newTabSpec("tab4").setIndicator("tab4")
				.setContent(new Intent(this, MoreActivity.class)));
//		if (from.equals("regsuc")) {
//			tabs.setCurrentTab(2);
//		} else {
//			tabs.setCurrentTab(focous_on);
//		}
		// 首页
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(0);
			}
		});
		// list
		suifang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(1);
			}
		});
		// personal
		huanjiao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(2);
			}
		});
		// personal
		personal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(3);
			}
		});
		// personal
		more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(4);
			}
		});
	}

	/**
	 * 设置Tab
	 * 
	 * @param index
	 */
	public void setTab(int index) {
		hideSoftInputFromWindow();
		tabs.setCurrentTab(index);
		focous_on = index;
		clearAllImageSelected();
		switch (index) {
		case 0:
			/** 首页 **/
			home_im.setImageDrawable(getResources().getDrawable(
					R.drawable.home_selected));
			home_tv.setTextColor(getResources().getColor(R.color.green3));
			home_index.setVisibility(View.VISIBLE);
			break;

		case 1:
			/** 随访 **/
			suifang_im.setImageDrawable(getResources().getDrawable(
					R.drawable.suifang_selected));
			suifang_tv.setTextColor(getResources().getColor(R.color.green3));
			suifang_index.setVisibility(View.VISIBLE);
			break;

		case 2:
			/** 患教 **/
			huanjiao_im.setImageDrawable(getResources().getDrawable(
					R.drawable.huanjiao_selected));
			huanjiao_tv.setTextColor(getResources().getColor(R.color.green3));
			huanjiao_index.setVisibility(View.VISIBLE);
			break;

		case 3:
			/** 个人 **/
			personal_im.setImageDrawable(getResources().getDrawable(
					R.drawable.personal_selected));
			personal_tv.setTextColor(getResources().getColor(R.color.green3));
			personal_index.setVisibility(View.VISIBLE);
			break;

		case 4:
			/** 更多 **/
			more_im.setImageDrawable(getResources().getDrawable(
					R.drawable.more_selected));
			more_tv.setTextColor(getResources().getColor(R.color.green3));
			more_index.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void hideSoftInputFromWindow() {
		// 隐藏软键盘
		manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 清楚点击图标的背景点击效果
	 */
	private void clearAllImageSelected() {
		home_im.setImageDrawable(getResources().getDrawable(R.drawable.home));
		suifang_im.setImageDrawable(getResources().getDrawable(
				R.drawable.suifang));
		huanjiao_im.setImageDrawable(getResources().getDrawable(
				R.drawable.huanjiao));
		personal_im.setImageDrawable(getResources().getDrawable(
				R.drawable.personal));
		more_im.setImageDrawable(getResources().getDrawable(R.drawable.more));
		home_tv.setTextColor(getResources().getColor(R.color.botton_font));
		suifang_tv.setTextColor(getResources().getColor(R.color.botton_font));
		huanjiao_tv.setTextColor(getResources().getColor(R.color.botton_font));
		personal_tv.setTextColor(getResources().getColor(R.color.botton_font));
		more_tv.setTextColor(getResources().getColor(R.color.botton_font));
		home_index.setVisibility(View.GONE);
		suifang_index.setVisibility(View.GONE);
		huanjiao_index.setVisibility(View.GONE);
		personal_index.setVisibility(View.GONE);
		more_index.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		clearAllImageSelected();
		switch (focous_on) {
		case 0:
			home_im.setImageDrawable(getResources().getDrawable(
					R.drawable.home_selected));
			home_tv.setTextColor(getResources().getColor(R.color.green3));
			home_index.setVisibility(View.VISIBLE);
			break;
		case 1:
			suifang_im.setImageDrawable(getResources().getDrawable(
					R.drawable.suifang_selected));
			suifang_tv.setTextColor(getResources().getColor(R.color.green3));
			suifang_index.setVisibility(View.VISIBLE);
			break;
		case 2:
			huanjiao_im.setImageDrawable(getResources().getDrawable(
					R.drawable.huanjiao_selected));
			huanjiao_tv.setTextColor(getResources().getColor(R.color.green3));
			huanjiao_index.setVisibility(View.VISIBLE);
			break;
		case 3:
			personal_im.setImageDrawable(getResources().getDrawable(
					R.drawable.personal_selected));
			personal_tv.setTextColor(getResources().getColor(R.color.green3));
			personal_index.setVisibility(View.VISIBLE);
			break;
		case 4:
			more_im.setImageDrawable(getResources().getDrawable(
					R.drawable.more_selected));
			more_tv.setTextColor(getResources().getColor(R.color.green3));
			more_index.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		super.onResume();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() != KeyEvent.ACTION_UP) {
			Log.d("onKeyDown", "onKeyDown");
			if (!isBack) {
				ToastUtil.showToast(this, "请再按一次退出");
				downTime = event.getDownTime();
				isBack = true;
				return true;
			} else {
				if (event.getDownTime() - downTime <= 2000) {
					this.finish();
					System.exit(0);
				} else {
					ToastUtil.showToast(this, "请再按一次退出");
					downTime = event.getDownTime();
					return true;
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}

}

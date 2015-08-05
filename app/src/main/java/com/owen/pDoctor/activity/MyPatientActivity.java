package com.owen.pDoctor.activity;

import com.owen.pDoctor.R;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * ClassName：MainActivity Description：MainActivity Author ： owen Date ：2015-7-10
 * 下午6:58:58 Copyright (C) 2012-2014 owen
 */
@SuppressWarnings("deprecation")
public class MyPatientActivity extends Activity {

	private Context mContext;

	private TextView tv_message, tv_contacts;

	private TabHost tabs;
	private InputMethodManager manager = null;
	// 默认显示首页
	private int focous_on = 0;

	private String from = "";

	private LinearLayout back_btn, ll_search;

	private ImageView top_person;

	public static MyPatientActivity pActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		pActivity = this;
		setContentView(R.layout.activity_mypatient);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		Intent intent = getIntent();
		if (intent.getStringExtra("from") != null) {
			from = intent.getStringExtra("from");
		}

		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		top_person = (ImageView) findViewById(R.id.top_person);
		ll_search = (LinearLayout) findViewById(R.id.ll_search);
		tv_message = (TextView) findViewById(R.id.tv_message);
		tv_contacts = (TextView) findViewById(R.id.tv_contacts);

		tabs = (TabHost) findViewById(R.id.mTabhost);
		LocalActivityManager Lam = new LocalActivityManager(this, false);
		Lam.dispatchCreate(savedInstanceState);
		tabs.setup(Lam);
		tabs.addTab(tabs.newTabSpec("tab0").setIndicator("tab0").setContent(new Intent(this, MessagesActivity.class)));
		tabs.addTab(tabs.newTabSpec("tab1").setIndicator("tab1").setContent(new Intent(this, ContactsActivity.class)));
		if (from.equals("home")) {
			// tabs.setCurrentTab(1);
			back_btn.setVisibility(View.VISIBLE);
		} else {
			// tabs.setCurrentTab(focous_on);
			back_btn.setVisibility(View.INVISIBLE);
		}
		// 返回
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 消息
		tv_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(0);
			}
		});

		// 联系人
		tv_contacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(1);
			}
		});

		// 搜索
		ll_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, SearchActivity.class));
			}
		});

		// 添加联系人
		top_person.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MyQRImageActivity.class);
				intent.putExtra("from", "mypatient");
				startActivity(intent);
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
			/** 消息 **/
			tv_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_bg_green3));
			tv_message.setTextColor(getResources().getColor(R.color.baise));
			break;

		case 1:
			/** 联系人 **/
			tv_contacts.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_bg_green3));
			tv_contacts.setTextColor(getResources().getColor(R.color.baise));
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
		manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 清楚点击图标的背景点击效果
	 */
	private void clearAllImageSelected() {
		tv_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_bg_wight));
		tv_contacts.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_bg_wight));
		tv_message.setTextColor(getResources().getColor(R.color.green3));
		tv_contacts.setTextColor(getResources().getColor(R.color.green3));
	}

	@Override
	protected void onResume() {
		clearAllImageSelected();
		switch (focous_on) {
		case 0:
			tv_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_bg_green3));
			tv_message.setTextColor(getResources().getColor(R.color.baise));
			break;
		case 1:
			tv_contacts.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_bg_green3));
			tv_contacts.setTextColor(getResources().getColor(R.color.baise));
			break;
		default:
			break;
		}
		super.onResume();
	}

	public void finishMe() {
		// TODO Auto-generated method stub
		if (HelpActivity.hActivity != null && from.equals("help")) {
			HelpActivity.hActivity.finishMe();
		}
		MyPatientActivity.this.finish();
	}
}

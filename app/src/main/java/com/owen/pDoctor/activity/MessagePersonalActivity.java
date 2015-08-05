package com.owen.pDoctor.activity;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.owen.pDoctor.R;
import com.owen.pDoctor.chat.ChatActivity;
import com.owen.pDoctor.model.MessagesBean;
import com.owen.pDoctor.model.MyGroupChildBean;

/**
 * ClassName：MainActivity Description：MainActivity Author ： owen Date ：2015-7-10
 * 下午6:58:58 Copyright (C) 2012-2014 owen
 */
@SuppressWarnings("deprecation")
public class MessagePersonalActivity extends Activity {

	private Context mContext;

	private TextView tv_name, tv_profile, tv_chat, tv_service;

	private TabHost tabs;
	private InputMethodManager manager = null;
	// 默认显示首页
	private int focous_on = 1;

	private LinearLayout back_btn;
	
	private MyGroupChildBean myChildBean;
	
	private MessagesBean msgBean;
	
	private String from;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		setContentView(R.layout.activity_personal_message);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		if (from != null && from.equals("message")) {
			if ((MessagesBean)intent.getSerializableExtra("childItem") != null) {
				msgBean = (MessagesBean)intent.getSerializableExtra("childItem");
			}
		} else if ((MyGroupChildBean)intent.getSerializableExtra("childItem") != null) {
			myChildBean = (MyGroupChildBean)intent.getSerializableExtra("childItem");
		}
		
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_profile = (TextView) findViewById(R.id.tv_profile);
		tv_chat = (TextView) findViewById(R.id.tv_chat);
		tv_service = (TextView) findViewById(R.id.tv_service);
		if (from != null && from.equals("message")) {
			if (msgBean.getNickname() != null) {
				tv_name.setText(msgBean.getNickname());
			} else {
				tv_name.setText("无名氏");
			}
		} else {
			if (myChildBean.getNickname() != null) {
				tv_name.setText(myChildBean.getNickname());
			} else {
				tv_name.setText("无名氏");
			}
		}

		tabs = (TabHost) findViewById(R.id.mTabhost);
		LocalActivityManager Lam = new LocalActivityManager(this, false);
		Lam.dispatchCreate(savedInstanceState);
		tabs.setup(Lam);
		if (from != null && from.equals("message")) {
			tabs.addTab(tabs.newTabSpec("tab0").setIndicator("tab0").setContent(new Intent(this, MyPatientProfileActivity.class).putExtra("msgItem", msgBean)));
			tabs.addTab(tabs.newTabSpec("tab1").setIndicator("tab1").setContent(new Intent(this, ChatActivity.class).putExtra("msgItem", msgBean)));
			tabs.addTab(tabs.newTabSpec("tab2").setIndicator("tab2").setContent(new Intent(this, MyPatientServiceActivity.class).putExtra("msgItem", msgBean)));
		} else {
			tabs.addTab(tabs.newTabSpec("tab0").setIndicator("tab0").setContent(new Intent(this, MyPatientProfileActivity.class).putExtra("childItem", myChildBean)));
			tabs.addTab(tabs.newTabSpec("tab1").setIndicator("tab1").setContent(new Intent(this, ChatActivity.class).putExtra("childItem", myChildBean)));
			tabs.addTab(tabs.newTabSpec("tab2").setIndicator("tab2").setContent(new Intent(this, MyPatientServiceActivity.class).putExtra("childItem", myChildBean)));
		}
		tabs.setCurrentTab(focous_on);
		// 返回
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 资料
		tv_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(0);
			}
		});

		// 聊天
		tv_chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(1);
			}
		});

		// 服务
		tv_service.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTab(2);
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
			/** 资料 **/
			tv_profile.setBackgroundColor(getResources().getColor(R.color.green3));
			break;

		case 1:
			/** 聊天 **/
			tv_chat.setBackgroundColor(getResources().getColor(R.color.green3));
			break;

		case 2:
			/** 服务 **/
			tv_service.setBackgroundColor(getResources().getColor(R.color.green3));
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
		tv_profile.setBackgroundColor(getResources().getColor(R.color.green4));
		tv_chat.setBackgroundColor(getResources().getColor(R.color.green4));
		tv_service.setBackgroundColor(getResources().getColor(R.color.green4));
	}

	@Override
	protected void onResume() {
		clearAllImageSelected();
		switch (focous_on) {
		case 0:
			tv_profile.setBackgroundColor(getResources().getColor(R.color.green3));
			break;
		case 1:
			tv_chat.setBackgroundColor(getResources().getColor(R.color.green3));
			break;
		case 2:
			tv_service.setBackgroundColor(getResources().getColor(R.color.green3));
			break;
		default:
			break;
		}
		super.onResume();
	}
	
}

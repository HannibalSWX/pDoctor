package com.owen.pDoctor.activity;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Title:CustomizedSubmitActivity.java
 * @Description:应用CustomizedSubmitActivity.java类
 * @Author:owen
 * @Since:2015年7月16日
 * @Version:
 */
public class CustomizedSubmitActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private LinearLayout back_btn;

	private TextView tv_share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_customized_submit);

		initview();
		setListener();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		tv_share = (TextView) findViewById(R.id.tv_share);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		tv_share.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.tv_share:
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}

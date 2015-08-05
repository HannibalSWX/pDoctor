package com.owen.pDoctor.activity;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.FreedateAdapter;
import com.owen.pDoctor.view.MyListView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 * @Title:FreeDateActivity.java
 * @Description:应用FreeDateActivity.java类
 * @Author:owen
 * @Since:2015年7月15日
 * @Version:
 */
public class FreeDateActivity extends BaseActivity implements OnClickListener {

	private LinearLayout back_btn;

	private MyListView lv_freedate;

	private FreedateAdapter freeadapter;

	private String[] dates = { "0天", "1天", "3天", "7天", "14天", "30天", "不限" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_freedate);

		initview();
		setListener();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		lv_freedate = (MyListView) findViewById(R.id.lv_freedate);
		freeadapter = new FreedateAdapter(this, dates);
		lv_freedate.setAdapter(freeadapter);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
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

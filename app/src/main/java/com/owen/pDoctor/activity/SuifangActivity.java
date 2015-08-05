package com.owen.pDoctor.activity;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * ClassName：SuifangActivity
 * Description：随访页面
 * Author ： zhouqiang
 * Date ：2015-7-13 下午13:07:51
 */
public class SuifangActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_suifang);

		initView();
		setListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
//		back_btn = (LinearLayout) findViewById(R.id.back_btn);
	}

	/**
	 * setListener
	 */
	private void setListener() {
//		back_btn.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	// 图标的点击事件
	public void onClick(View v) {
		switch (v.getId()) {
//			case R.id.back_btn :
//				/** 返回按钮 **/
//				finish();
//			break;
			
			default :
				break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

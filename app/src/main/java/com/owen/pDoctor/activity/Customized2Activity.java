package com.owen.pDoctor.activity;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Title:Customized2Activity.java
 * @Description:应用Customized2Activity.java类
 * @Author:owen
 * @Since:2015年7月16日
 * @Version:
 */
public class Customized2Activity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private DisplayMetrics metric;

	private int width, height;

	private LinearLayout back_btn;

	private ImageView im_bg;

	private TextView tv_pre, tv_next;

	private EditText et_name, et_hospital, et_keshi, et_zhicheng, et_liuyan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_customized2);

		// 获取屏幕宽高度（像素）设置背景高度
		metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）

		initview();
		setListener();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		im_bg = (ImageView) findViewById(R.id.im_bg);
		tv_pre = (TextView) findViewById(R.id.tv_pre);
		tv_next = (TextView) findViewById(R.id.tv_next);
		et_name = (EditText) findViewById(R.id.et_name);
		et_hospital = (EditText) findViewById(R.id.et_hospital);
		et_keshi = (EditText) findViewById(R.id.et_keshi);
		et_zhicheng = (EditText) findViewById(R.id.et_zhicheng);
		et_liuyan = (EditText) findViewById(R.id.et_liuyan);

		LayoutParams params = (LayoutParams) im_bg.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = 4 * width / 9;
		im_bg.setLayoutParams(params);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		tv_pre.setOnClickListener(this);
		tv_next.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.tv_pre:
			finish();
			break;

		case R.id.tv_next:
			Intent intent = new Intent(mContext, Customized3Activity.class);
			intent.putExtra("name", et_name.getText().toString());
			intent.putExtra("hospital", et_hospital.getText().toString());
			intent.putExtra("keshi", et_keshi.getText().toString());
			intent.putExtra("zhicheng", et_zhicheng.getText().toString());
			intent.putExtra("liuyan", et_liuyan.getText().toString());
			startActivity(intent);
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

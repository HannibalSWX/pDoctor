package com.owen.pDoctor.activity;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Title:DefinedPriceActivity.java
 * @Description:应用DefinedPriceActivity.java类
 * @Author:owen
 * @Since:2015年7月15日
 * @Version:
 */
public class DefinedPriceActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private LinearLayout back_btn;

	private TextView tv_save;

	private EditText et_price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_definedprice);

		initview();
		setListener();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		tv_save = (TextView) findViewById(R.id.tv_save);
		et_price = (EditText) findViewById(R.id.et_price);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		tv_save.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.tv_save:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("price", et_price.getText().toString());
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
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

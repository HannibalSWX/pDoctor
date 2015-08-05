package com.owen.pDoctor.activity;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * @Title:MonthPriceActivity.java
 * @Description:应用MonthPriceActivity.java类
 * @Author:owen
 * @Since:2015年7月15日
 * @Version:
 */
public class MonthPriceActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private ToggleButton pay_key;

	private boolean isCharge = true;

	private SharedPreferences sp;

	private TextView tv_time_price;

	private LinearLayout back_btn;

	private ImageView im_ten, im_fifteen, im_twenty;

	private RelativeLayout rl_ten, rl_fifteen, rl_twenty, rl_defined, rl_instruction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_monthprice);

		sp = getSharedPreferences("is_charge", Context.MODE_PRIVATE);
		isCharge = sp.getBoolean("isCharge", isCharge);

		initview();
		setListener();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		pay_key = (ToggleButton) findViewById(R.id.pay_key);
		tv_time_price = (TextView) findViewById(R.id.tv_time_price);
		im_ten = (ImageView) findViewById(R.id.im_ten);
		im_fifteen = (ImageView) findViewById(R.id.im_fifteen);
		im_twenty = (ImageView) findViewById(R.id.im_twenty);

		rl_ten = (RelativeLayout) findViewById(R.id.rl_ten);
		rl_fifteen = (RelativeLayout) findViewById(R.id.rl_fifteen);
		rl_twenty = (RelativeLayout) findViewById(R.id.rl_twenty);
		rl_defined = (RelativeLayout) findViewById(R.id.rl_defined);
		rl_instruction = (RelativeLayout) findViewById(R.id.rl_instruction);

		pay_key.setBackgroundResource(isCharge ? R.drawable.checkbox_on : R.drawable.checkbox_off);
		pay_key.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isCharge) {
					isCharge = false;
				} else {
					isCharge = true;
				}
				pay_key.setBackgroundResource(isCharge ? R.drawable.checkbox_on : R.drawable.checkbox_off);
				sp = getSharedPreferences("is_charge", Context.MODE_PRIVATE);
				Editor editor2 = sp.edit();
				editor2.putBoolean("isCharge", isCharge);
				editor2.commit();
			}
		});
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		rl_ten.setOnClickListener(this);
		rl_fifteen.setOnClickListener(this);
		rl_twenty.setOnClickListener(this);
		rl_defined.setOnClickListener(this);
		rl_instruction.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.rl_ten:
			tv_time_price.setText("99元/月");
			im_ten.setVisibility(View.VISIBLE);
			im_fifteen.setVisibility(View.GONE);
			im_twenty.setVisibility(View.GONE);
			break;

		case R.id.rl_fifteen:
			tv_time_price.setText("199元/月");
			im_ten.setVisibility(View.GONE);
			im_fifteen.setVisibility(View.VISIBLE);
			im_twenty.setVisibility(View.GONE);
			break;

		case R.id.rl_twenty:
			tv_time_price.setText("299元/月");
			im_ten.setVisibility(View.GONE);
			im_fifteen.setVisibility(View.GONE);
			im_twenty.setVisibility(View.VISIBLE);
			break;

		case R.id.rl_defined:
			Intent intent = new Intent();
			intent.setClass(this, DefinedPriceActivity.class);
			// Bundle bundle=new Bundle();
			// bundle.putString("str", "");
			// intent.putExtras(bundle);
			startActivityForResult(intent, 0);
			break;

		case R.id.rl_instruction:
			startActivity(new Intent(this, PayInstructionActivity.class));
			break;

		default:
			break;
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			Bundle bundle = data.getExtras();
			String price = bundle.getString("price");
			tv_time_price.setText(price);
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

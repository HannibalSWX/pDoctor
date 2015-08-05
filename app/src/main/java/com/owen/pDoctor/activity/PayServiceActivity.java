package com.owen.pDoctor.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * ClassName：PayServiceActivity Description：收费服务页面 Author ： zq Date ：2015-7-12
 * 下午11:32:33
 */
public class PayServiceActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private ToggleButton pay_key;

	private boolean isCharge = false;

	private SharedPreferences sp;

	private TextView tv_save, tv_date, tv_phone_fee, tv_time_fee, tv_month_fee;

	private LinearLayout back_btn;

	private RelativeLayout rl_mianfei, rl_phone, rl_time, rl_month, rl_history, rl_instruction;

	private Dialog dialog;

	private String check_time = "";

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private Intent intent;

	private String userName, message, code;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String date = intent.getStringExtra("date");
			tv_date.setText(date);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_payservice);

		sp = getSharedPreferences("is_charge", Context.MODE_PRIVATE);
		isCharge = sp.getBoolean("isCharge", isCharge);

		initview();
		setListener();
		
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.CHOOSE_DATE_BRAODCAST);
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_date = (TextView) findViewById(R.id.tv_date);
		pay_key = (ToggleButton) findViewById(R.id.pay_key);
		tv_phone_fee = (TextView) findViewById(R.id.tv_phone_fee);
		tv_time_fee = (TextView) findViewById(R.id.tv_time_fee);
		tv_month_fee = (TextView) findViewById(R.id.tv_month_fee);

		rl_mianfei = (RelativeLayout) findViewById(R.id.rl_mianfei);
		rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
		rl_time = (RelativeLayout) findViewById(R.id.rl_time);
		rl_month = (RelativeLayout) findViewById(R.id.rl_month);
		rl_history = (RelativeLayout) findViewById(R.id.rl_history);
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
					showDialog();
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
		tv_save.setOnClickListener(this);
		rl_mianfei.setOnClickListener(this);
		rl_phone.setOnClickListener(this);
		rl_time.setOnClickListener(this);
		rl_month.setOnClickListener(this);
		rl_history.setOnClickListener(this);
		rl_instruction.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.tv_save:
			ToastUtil.showToast(mContext, "save what?");
			break;

		case R.id.rl_mianfei:
			Intent intent = new Intent();
			intent.setClass(this, FreeDateActivity.class);
			startActivity(intent);
			break;

		case R.id.rl_phone:
			startActivity(new Intent(this, PhonePriceActivity.class));
			break;

		case R.id.rl_time:
			startActivity(new Intent(this, TimePriceActivity.class));
			break;

		case R.id.rl_month:
			startActivity(new Intent(this, MonthPriceActivity.class));
			break;

		case R.id.rl_history:
			ToastUtil.showToast(mContext, "where is history?");
			break;

		case R.id.rl_instruction:
			startActivity(new Intent(this, PayInstructionActivity.class));
			break;
		default:
			break;
		}

	}

	// dialog
	public void showDialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(mContext, R.style.home_dialog);
		dialog.setContentView(R.layout.alert_dialog_charge);
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.CENTER_VERTICAL);
		TextView tv_renzheng = (TextView) dialogWindow.findViewById(R.id.tv_renzheng);
		ImageView im_close = (ImageView) dialogWindow.findViewById(R.id.im_close);
		im_close.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		tv_renzheng.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(mContext, AuthenticationActivity.class));
			}
		});
		// lp.x = 100; // 新位置X坐标
		// lp.y = 300; // 新位置Y坐标
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		// lp.alpha = 0.7f; // 透明度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 修改昵称
	private void change() {
		if (userName.equals("")) {
			ToastUtil.showToast(this, "请输入昵称");
			return;
		}
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("提交中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("userName", userName);
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("change nickname返回结果  : ----", result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						handler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		// overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				ToastUtil.showToast(PayServiceActivity.this, message);
				Intent mIntent = new Intent(Constants.RESET_BRAODCAST);
				mIntent.putExtra("type", "reset");
				// 发送广播
				sendBroadcast(mIntent);
				PayServiceActivity.this.finish();
			} else {
				ToastUtil.showToast(PayServiceActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			Bundle bundle = data.getExtras();
			String date = bundle.getString("date");
			tv_date.setText(date);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}

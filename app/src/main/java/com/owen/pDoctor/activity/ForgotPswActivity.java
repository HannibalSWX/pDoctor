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
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ClassName：ForgotPswActivity Description：忘记密码 Author ： zhouqiang Date
 * ：2015-1-23 下午8:31:39 Copyright (C) 2012-2014 owen
 */
@SuppressLint("HandlerLeak")
public class ForgotPswActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	private LinearLayout back_btn;

	private EditText input_phone;

	private TextView tv_getcode;

	private MyCount mc;

	/**
	 * 提交按钮
	 */
	private Button submit_btn;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String phoneno, message, code, mCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_forgot_psw1);

		initView();
		setListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		input_phone = (EditText) findViewById(R.id.input_phone);
		tv_getcode = (TextView) findViewById(R.id.tv_getcode);
		submit_btn = (Button) findViewById(R.id.submit_btn);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		tv_getcode.setOnClickListener(this);
		submit_btn.setOnClickListener(this);
	}

	// 图标的点击事件
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.back_btn:
			/** 返回按钮 **/
			finish();
			break;

		case R.id.tv_getcode:
			/** 获取验证码按钮 **/
			if (codeIsSuccess()) {
				getidentify();
				mc = new MyCount(60000, 1000);
				mc.start();
			}
			break;

		case R.id.submit_btn:
			/** 提交按钮 **/
			if (!Utils.isAccount(mCode) || mCode.equals("")) {
				// 用户输入验证码
				ToastUtil.showToast(mContext, "请输入验证码");
				return;
			}
			submit();
			break;

		default:
			break;
		}
	}

	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);

		}

		@Override
		public void onFinish() {
			tv_getcode.setClickable(true);
			tv_getcode.setBackgroundResource(R.drawable.bt_bg);
			tv_getcode.setTextColor(getResources().getColor(R.color.gray16));
			tv_getcode.setText("获取验证码");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tv_getcode.setClickable(false);
			tv_getcode.setBackgroundResource(R.drawable.btn_bg_green3);
			tv_getcode.setTextColor(getResources().getColor(R.color.baise));
			tv_getcode.setText(millisUntilFinished / 1000 + " 秒");
		}
	}

	// 获取验证码
	private void getidentify() {
		// TODO Auto-generated method stub
		if (!Utils.isMobileNO(input_phone.getText().toString())) {
			ToastUtil.showToast(this, "请输入正确的手机号");
			return;
		}

		/**
		 * 启动修改密码请求
		 */
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "login");
			reuqestMap.put("act", "validateMobile");
			reuqestMap.put("mobile", input_phone.getText().toString());
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token", EncryptionUtil.md5EncryptToString("jiankang2015")); // 注册
			reuqestMap.put("ver", getAppVersion());
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("获取验证码返回结果 :----", "" + result);
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
			ToastUtil.showToast(ForgotPswActivity.this, "网络异常,请检查网络!");
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(ForgotPswActivity.this, message);
			} else {
				ToastUtil.showToast(ForgotPswActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 提交按钮
	private void submit() {
		// TODO Auto-generated method stub
		/**
		 * 启动修改密码请求
		 */
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "login");
			reuqestMap.put("act", "forgetPwd");
			reuqestMap.put("mobile", input_phone.getText().toString());
			reuqestMap.put("yzm", tv_getcode.getText().toString());
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token", EncryptionUtil.md5EncryptToString("jiankang2015")); // 注册
			reuqestMap.put("ver", getAppVersion());
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("获取验证码提交返回结果 :----", "" + result);
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
						shandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(mContext, "网络异常,请检查网络!");
		}
	}

	Handler shandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(mContext, message);
				Intent intent = new Intent(mContext, ForgotPsw2Activity.class);
				intent.putExtra("phone", input_phone.getText().toString());
				startActivity(intent);
				ForgotPswActivity.this.finish();
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	/*
	 * 判断用户输入手机号是否规范
	 */
	private boolean codeIsSuccess() {
		// 获取用户输入的信息
		phoneno = input_phone.getText().toString();
		if (!Utils.isMobileNO(phoneno)) {
			// 用户输入用户名
			ToastUtil.showToast(mContext, "请输入正确的手机号");
			return false;
		}
		return true;
	}
}

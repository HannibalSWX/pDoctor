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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * ClassName：ForgotPswActivity Description：忘记密码 Author ： zhouqiang Date
 * ：2015-1-23 下午8:31:39 Copyright (C) 2012-2014 owen
 */
@SuppressLint("HandlerLeak")
public class ForgotPsw2Activity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	private LinearLayout back_btn;

	private EditText input_identify, confirm_identify;

	private String identify, phone, mcode, uid;

	/**
	 * 提交按钮
	 */
	private Button submit_btn;
	
	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String phoneno,recode, message, code, mCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_forgot_psw2);

		Intent intent = getIntent();
		phone = intent.getStringExtra("phone");

		initView();
		setListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		input_identify = (EditText) findViewById(R.id.input_identify);
		confirm_identify = (EditText) findViewById(R.id.confirm_identify);
		submit_btn = (Button) findViewById(R.id.submit_btn);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		submit_btn.setOnClickListener(this);
	}

	// 图标的点击事件
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.back_btn:
			/** 返回按钮 **/
			finish();
			break;

		case R.id.submit_btn:
			/** 提交按钮 **/
			if ("".equals(input_identify.getText().toString()) || input_identify.getText().toString() == null
					|| input_identify.getText().toString().length() < 6) {
				// 密码不能为空
				ToastUtil.showToast(mContext, "密码不能小于6位");
				return;
			}
			if (!input_identify.getText().toString().equals(confirm_identify.getText().toString())) {
				ToastUtil.showToast(mContext, "请输入相同密码");
				return;
			}
			submit();
			break;

		default:
			break;
		}
	}

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
			reuqestMap.put("act", "setPwd");
			reuqestMap.put("mobile", phone);
			reuqestMap.put("newpassword", input_identify.getText().toString());
			reuqestMap.put("repassword", confirm_identify.getText().toString());
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
						handler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(mContext, "网络异常,请检查网络!");
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(mContext, message);
				SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("password", input_identify.getText().toString());
				editor.commit();
				finish();
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}

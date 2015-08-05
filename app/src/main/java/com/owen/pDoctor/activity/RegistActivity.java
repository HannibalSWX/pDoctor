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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ClassName：RegistActivity Description：注册 Author ： zhouqiang Date ：2015-01-22
 * 下午6:03:43 Copyright (C) 2012-2014 owen
 */
@SuppressLint("HandlerLeak")
public class RegistActivity extends BaseActivity {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	private EditText phone_et;

	// 手机验证码
	private EditText identifyCode;

	// 密码
	private EditText password;

	private Button getBtn, regist;

	private LinearLayout back_arrow;

	private ImageView check_agreement;

	private String phoneno, pwd;

	private boolean ischeck = false;

	private TextView Sex;

	private TextView tv = null;// 根据不同选项所要变更的文本控件

	private MyCount mc;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String message, code, id, hospital_id, keshi_id, province_id, city_id, realname, sex, email, birth_date,
			head_img, description, work_time, renzhen_img, amount, free_day, img_card, img_zgz, img_other, verifyName,
			stateName, loginName, userName, qrcode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_regist);

		/**
		 * 初始化View
		 */
		initView();
	}

	private void initView() {
		phone_et = (EditText) findViewById(R.id.phone_et);
		identifyCode = (EditText) findViewById(R.id.yanzhengma_et);
		password = (EditText) findViewById(R.id.enterPassword);
		regist = (Button) findViewById(R.id.regist_sure);
		back_arrow = (LinearLayout) findViewById(R.id.back_btn);
		getBtn = (Button) findViewById(R.id.getBtn);
		check_agreement = (ImageView) findViewById(R.id.check_agreement);
		check_agreement.setBackgroundResource(R.drawable.bg_register_checked);
		getBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (codeIsSuccess()) {
					getcode();
					mc = new MyCount(60000, 1000);
					mc.start();
				}
			}
		});
		check_agreement.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 同意协议
				if (ischeck) {
					check_agreement.setBackgroundResource(R.drawable.bg_register_checked);
					ischeck = false;
				} else {
					check_agreement.setBackgroundResource(R.drawable.bg_register);
					ischeck = true;
				}
			}
		});
		regist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (registerIsSuccess()) {
					register();
				}
			}
		});
		back_arrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				finish();
			}
		});
	}

	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);

		}

		@Override
		public void onFinish() {
			getBtn.setClickable(true);
			getBtn.setBackgroundResource(R.drawable.bt_bg);
			getBtn.setTextColor(getResources().getColor(R.color.gray16));
			getBtn.setText("获取验证码");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			getBtn.setClickable(false);
			getBtn.setBackgroundResource(R.drawable.btn_bg_green3);
			getBtn.setTextColor(getResources().getColor(R.color.baise));
			getBtn.setText(millisUntilFinished / 1000 + " 秒");
		}
	}

	// 获取验证码
	private void getcode() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("获取中，请稍后...");
			}
			progressDialog.show();
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "user");
			reuqestMap.put("act", "sendMobile");
			reuqestMap.put("mobile", phoneno);
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
					Log.i("getcode ----", "getcode返回结果  :  " + result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						codehandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}

	Handler codehandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				ToastUtil.showToast(RegistActivity.this, message);
			} else {
				ToastUtil.showToast(RegistActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	/**
	 * 注册
	 */
	private void register() {
		phoneno = phone_et.getText().toString();
		pwd = password.getText().toString();
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("正在注册，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "login");
			reuqestMap.put("act", "register");
			reuqestMap.put("mobile", phoneno);
			reuqestMap.put("yzm", identifyCode.getText().toString());
			reuqestMap.put("password", password.getText().toString());
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
					Log.i("regist ----", "regist返回结果  :  " + result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							id = data.getString("id");
							
							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						registhandler.sendMessage(msg);
					} else {
						ToastUtil.showToast(RegistActivity.this, "注册失败");
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}

	Handler registhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(RegistActivity.this, message);
				// 注册成功后自动登录
				autologin();
				// 注册成功后创建二维码
				creatQrcode();
			} else {
				ToastUtil.showToast(RegistActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 注册成功后自动登录
	private void autologin() {
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("登录中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "login");
			reuqestMap.put("act", "login");
			reuqestMap.put("username", phoneno);
			reuqestMap.put("password", pwd);
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token", EncryptionUtil.md5EncryptToString("jiankang2015"));
			reuqestMap.put("ver", getAppVersion());
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("login ----", "login返回结果  :  " + result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							id = data.getString("id");
							loginName = data.getString("mobile");
							userName = data.getString("username");
							hospital_id = data.getString("hospital_id");
							keshi_id = data.getString("keshi_id");
							province_id = data.getString("province_id");
							city_id = data.getString("city_id");
							realname = data.getString("realname");
							sex = data.getString("sex");
							email = data.getString("email");
							birth_date = data.getString("birth_date");
							head_img = data.getString("head_img");
							description = data.getString("description");
							work_time = data.getString("work_time");
							renzhen_img = data.getString("renzhen_img");
							amount = data.getString("amount");
							free_day = data.getString("free_day");
							img_card = data.getString("img_card");
							img_zgz = data.getString("img_zgz");
							img_other = data.getString("img_other");
							verifyName = data.getString("verifyName");
							stateName = data.getString("stateName");
							qrcode = data.getString("qrcode");

							saveUserInfo();
							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}
					loginhandler.sendMessage(msg);
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		// overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
	}

	Handler loginhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(RegistActivity.this, message);
				Intent intent = new Intent(RegistActivity.this, MainActivity.class);
				intent.putExtra("from", "regsuc");
				startActivity(intent);
				RegistActivity.this.finish();
			} else {
				ToastUtil.showToast(RegistActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 用户登录后生成二维码
	private void creatQrcode() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "qrcode");
			reuqestMap.put("act", "creatQrcode");
			reuqestMap.put("id", id);
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token", EncryptionUtil.md5EncryptToString("jiankang2015"));
			reuqestMap.put("ver", getAppVersion());
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("checkresult ----", result);
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
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 保存登录信息
	private void saveUserInfo() {
		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("id", id);
		editor.putString("loginName", loginName); // 登录账号
		editor.putString("password", password.getText().toString());
		editor.putString("userName", userName); // 昵称
		editor.putString("hospital_id", hospital_id);
		editor.putString("keshi_id", keshi_id);
		editor.putString("province_id", province_id);
		editor.putString("city_id", city_id);
		editor.putString("realname", realname);
		editor.putString("sex", sex);
		editor.putString("email", email);
		editor.putString("birth_date", birth_date);
		editor.putString("head_img", head_img);
		editor.putString("description", description);
		editor.putString("work_time", work_time);
		editor.putString("renzhen_img", renzhen_img);
		editor.putString("amount", amount);
		editor.putString("free_day", free_day);
		editor.putString("img_card", img_card);
		editor.putString("img_zgz", img_zgz);
		editor.putString("img_other", img_other);
		editor.putString("verifyName", verifyName);
		editor.putString("stateName", stateName);
		editor.putString("qrcode", qrcode);
		editor.commit();
	}

	/*
	 * 判断用户输入手机号是否规范
	 */
	private boolean codeIsSuccess() {
		// 获取用户输入的信息
		phoneno = phone_et.getText().toString();
		if (!Utils.isMobileNO(phoneno)) {
			// 用户输入用户名
			ToastUtil.showToast(RegistActivity.this, "请输入正确的手机号");
			return false;
		}
		return true;
	}

	/*
	 * 判断用户注册输入是否规范 录入信息验证 验证是否通过
	 */
	private boolean registerIsSuccess() {
		// 获取用户输入的信息
		String phone = phone_et.getText().toString();
		String code = identifyCode.getText().toString();
		String pwd = password.getText().toString();

		if ("".equals(phone) || !Utils.isMobileNO(phone)) {
			// 用户输入手机号
			ToastUtil.showToast(RegistActivity.this, "请输入正确的手机号");
			return false;
		} else if ("".equals(code) || !Utils.isAccount(code)) {
			// 用户输入用户名
			ToastUtil.showToast(RegistActivity.this, "请输入正确的验证码");
			return false;
		} else if ("".equals(pwd) || pwd == null || pwd.length() < 6) {
			// 密码不能为空
			ToastUtil.showToast(RegistActivity.this, "密码不能小于6位");
			return false;
		} else if (ischeck) {
			// 必须同意协议
			ToastUtil.showToast(RegistActivity.this, "请同意注册协议");
			return false;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			hideKeyboard();
		}
		return super.onTouchEvent(event);
	}

	private void hideKeyboard() {
		if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}
}
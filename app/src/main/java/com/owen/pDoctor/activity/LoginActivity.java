package com.owen.pDoctor.activity;

import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.AppConstants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ClassName：LoginActivity Description：login Author ： zq Date ：2015-1-22
 * 下午9:32:33 Copyright (C) 2012-2014 owen
 */
@SuppressLint("HandlerLeak")
public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText username;

	private EditText userpasswrod;

	private TextView login_test;

	private TextView regist = null; // 注册

	private TextView login = null; // 登入

	private TextView forgot_psw = null; // 忘记密码

	private ImageView third_qq, third_weixin;

	private String password;

	SharedPreferences sp1, sp2;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private Intent intent;

	private String message, code, id, hospital_id, keshi_id, province_id, city_id, district_id, bd_id, realname, sex,
			email, birth_date, head_img, position_id, description, work_time, renzhen_img, amount, free_day, img_card,
			img_zgz, img_other, verifyName, stateName, loginName, userName, qrcode;

	public static String mAppid;

	public static Tencent mTencent;

	private UserInfo mInfo;

	private TextView mUserInfo;

	private ImageView mUserLogo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getUserInfo();
		if (!id.equals("")) {
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}

		setContentView(R.layout.activity_login);

		mAppid = AppConstants.APP_ID;
		if (mTencent == null) {
			mTencent = Tencent.createInstance(mAppid, this);
		}
		initview();
	}

	private void initview() {
		// TODO Auto-generated method stub
		username = (EditText) findViewById(R.id.username);
		userpasswrod = (EditText) findViewById(R.id.userpassword);
		forgot_psw = (TextView) findViewById(R.id.forgot_psw);
		regist = (TextView) findViewById(R.id.regist);
		login = (TextView) findViewById(R.id.login);
		login_test = (TextView) findViewById(R.id.login_test);
		third_qq = (ImageView) findViewById(R.id.third_qq);
		third_weixin = (ImageView) findViewById(R.id.third_weixin);
		forgot_psw.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		forgot_psw.setOnClickListener(this);
		regist.setOnClickListener(this);
		login.setOnClickListener(this);
		login_test.setOnClickListener(this);
		third_qq.setOnClickListener(this);
		third_weixin.setOnClickListener(this);

		username.setText(loginName);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_test:
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.forgot_psw:
			intent = new Intent(this, ForgotPswActivity.class);
			startActivity(intent);
			break;
		case R.id.regist:
			intent = new Intent(this, RegistActivity.class);
			startActivity(intent);
			break;
		case R.id.login:
			if (Utils.isMobileNO(username.getText().toString())) {
				dealLogin();
			} else {
				ToastUtil.showToast(this, "请输入正确的手机号");
			}
			break;
		case R.id.third_qq:
			if (!mTencent.isSessionValid()) {
				mTencent.login(this, "all", loginListener);
				Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
			}
			break;
		case R.id.third_weixin:
			ToastUtil.showToast(this, "微信登录需要企业申请，需要收费的");
			break;
		default:
			break;
		}

	}

	private void dealLogin() {
		loginName = username.getText().toString().toLowerCase(Locale.getDefault());
		password = userpasswrod.getText().toString();
		if (loginName.equals("")) {
			Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.equals("")) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
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
			reuqestMap.put("username", loginName);
			reuqestMap.put("password", password);
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token", EncryptionUtil.md5EncryptToString("jiankang2015"));
			reuqestMap.put("ver", getAppVersion());
			String url = AppConstants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("login ----", "login返回结果  :  " + result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							// id, hospital_id, keshi_id, province_id, city_id,
							// realname, sex, email, mobile,
							// birth_date, head_img,
							// description, work_time, renzhen_img, amount,
							// free_day, img_card, img_zgz, img_other,
							// verifyName, stateName, loginName, userName;
							id = data.getString("id");
							loginName = data.getString("mobile");
							userName = data.getString("username");
							hospital_id = data.getString("hospital_id");
							keshi_id = data.getString("keshi_id");
							province_id = data.getString("province_id");
							city_id = data.getString("city_id");
							district_id = data.getString("district_id");
							bd_id = data.getString("bd_id");
							realname = data.getString("realname");
							sex = data.getString("sex");
							email = data.getString("email");
							birth_date = data.getString("birth_date");
							head_img = data.getString("head_img");
							position_id = data.getString("position_id");
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
				ToastUtil.showToast(LoginActivity.this, message);
				intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			} else {
				ToastUtil.showToast(LoginActivity.this, message);
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
		editor.putString("password", userpasswrod.getText().toString());
		editor.putString("userName", userName); // 昵称
		editor.putString("hospital_id", hospital_id);
		editor.putString("keshi_id", keshi_id);
		editor.putString("province_id", province_id);
		editor.putString("city_id", city_id);
		editor.putString("district_id", district_id);
		editor.putString("bd_id", bd_id);
		editor.putString("realname", realname);
		editor.putString("sex", sex);
		editor.putString("email", email);
		editor.putString("birth_date", birth_date);
		editor.putString("head_img", head_img);
		editor.putString("position_id", position_id);
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

	// 自动登录获取保存的用户信息
	private void getUserInfo() {
		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		id = sp.getString("id", "");
		loginName = sp.getString("loginName", "");
		password = sp.getString("password", "");
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
			initOpenidAndToken(values);
			updateUserInfo();
		}
	};

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				ToastUtil.showToast(LoginActivity.this, "返回为空,登录失败");
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				ToastUtil.showToast(LoginActivity.this, "返回为空,登录失败");
				return;
			}
			ToastUtil.showToast(LoginActivity.this, response.toString() + "登录成功");
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			ToastUtil.showToast(LoginActivity.this, "onError: " + e.errorDetail);
			Utils.dismissDialog();
		}

		@Override
		public void onCancel() {
			ToastUtil.showToast(LoginActivity.this, "onCancel: ");
			Utils.dismissDialog();
		}
	}

	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread() {

						@Override
						public void run() {
							JSONObject json = (JSONObject) response;
							if (json.has("figureurl")) {
								Bitmap bitmap = null;
								try {
									bitmap = Utils.getbitmap(json.getString("figureurl_qq_2"));
								} catch (JSONException e) {

								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}

					}.start();
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);

		} else {
			mUserInfo.setText("");
			mUserInfo.setVisibility(android.view.View.GONE);
			mUserLogo.setVisibility(android.view.View.GONE);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						mUserInfo.setVisibility(android.view.View.VISIBLE);
						mUserInfo.setText(response.getString("nickname"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				mUserLogo.setImageBitmap(bitmap);
				mUserLogo.setVisibility(android.view.View.VISIBLE);
			}
		}

	};

	public static void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				Tencent.handleResultData(data, loginListener);
			}
		} else if (requestCode == Constants.REQUEST_APPBAR) { // app内应用吧登录
			if (resultCode == Constants.RESULT_LOGIN) {
				updateUserInfo();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// hideKeyboard();
		LoginActivity.this.finish();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
}

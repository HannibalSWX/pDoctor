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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ClassName：PersonalActivity
 * Description：个人信息页面
 * Author ： zq
 * Date ：2015-7-12 下午10:32:33
 */
public class PersonalSetActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout rl_setting, rl_change_psw;
	
	private LinearLayout back_btn;
	
	private Button quit;

	SharedPreferences sp1;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private Intent intent;

	private String uid, message, code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("uid", "");

		initview();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		rl_setting = (RelativeLayout) findViewById(R.id.rl_setting);
		rl_change_psw = (RelativeLayout) findViewById(R.id.rl_change_psw);
		quit = (Button) findViewById(R.id.quit);

		back_btn.setOnClickListener(this);
		rl_setting.setOnClickListener(this);
		rl_change_psw.setOnClickListener(this);
		quit.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn :
				finish();
				break;
			case R.id.rl_setting :
				startActivity(new Intent(PersonalSetActivity.this,
						PersonalSettingActivity.class));
				break;
			case R.id.rl_change_psw :
				startActivity(new Intent(PersonalSetActivity.this,
						ChangePswActivity.class));
				break;
			case R.id.quit :
				quit();
				break;

			default :
				break;
		}

	}

	// 退出应用
	private void quit() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("退出中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("uid", uid);
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
							message = Jsonresult.getString("msg");
							code = Jsonresult.getString("code");
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
			if (msg.what == 200 || message.equals("注销成功")) {
				// 退出应用
				clearUserInfo();
				ToastUtil.showToast(PersonalSetActivity.this, message);
				Intent mIntent = new Intent(Constants.RESET_BRAODCAST);
				mIntent.putExtra("type", "reset");
				//发送广播
				sendBroadcast(mIntent);
				PersonalSetActivity.this.finish();
//				ActivityManager.finishApplication();
//				System.exit(0);
			} else {
				ToastUtil.showToast(PersonalSetActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	private void clearUserInfo() {
		SharedPreferences sp = getSharedPreferences("userInfo",	Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("uid", "");
		editor.putString("loginName", ""); // 登录账号
		editor.putString("password", "");
		editor.putString("userName", ""); // 昵称
		editor.commit();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}

package com.owen.pDoctor.activity;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

/**
 * ClassName：ChangePswActivity
 * Description：修改密码
 * Author ： zhouqiang
 * Date ：2015-7-13 上午12:20:21
 */
public class ChangePswActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	public List<String> urls;
	Uri uri;
	Intent intent;
	int gallerypisition = 0;

	private LinearLayout back_btn;

	/**
	 * 旧密码
	 */
	private EditText old_psw;
	
	/**
	 * 新密码
	 */
	private EditText new_psw;

	/**
	 * 确认新密码
	 */
	private EditText confirm_psw;

	private String password, old_password, new_password, confirm_password;

	/**
	 * 提交按钮
	 */
	private Button submit_btn;
	
	private ZyNet zyNet = null;
	
	private HashMap<String, String> reuqestMap = null;
	
	private CustomProgressDialog progressDialog = null;

	private String uid, message, code, userName, loginName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_change_psw);

		SharedPreferences sp = getSharedPreferences("userInfo",	Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		userName = sp.getString("userName", "");
		loginName = sp.getString("loginName", "");
		
		initView();
		setListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		old_psw = (EditText) findViewById(R.id.et_oldpsw);
		new_psw = (EditText) findViewById(R.id.et_newpsw);
		confirm_psw = (EditText) findViewById(R.id.et_confirmpsw);
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
			hideKeyboard();
			finish();
			break;

		case R.id.submit_btn:
			/** 提交按钮 **/
			change();
			break;

		default:
			break;
		}
	}

	private void change() {
		// TODO Auto-generated method stub
		old_password = old_psw.getText().toString().trim();
		new_password = new_psw.getText().toString().trim();
		confirm_password = confirm_psw.getText().toString().trim();
		if ("".equals(old_password) || null == old_password) {
			ToastUtil.showToast(this, "请输入原密码");
			return;
		}
		if ("".equals(new_password) || null == new_password	|| !Utils.isAccount(new_password)) {
			ToastUtil.showToast(this, "请输入合法密码");
			return;
		}
		if (!confirm_password.equals(new_password)) {
			// 密码不一致
			ToastUtil.showToast(this, "2次密码不一致!");
			return;
		}

		/**
		 * 启动修改密码请求
		 */
		if (Utils.isNetConn(this)) {
			if (progressDialog == null){
				progressDialog = CustomProgressDialog.createDialog(this);
			}
	    	progressDialog.show();
	    	
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
//			reuqestMap.put("old_password", old_password);
			reuqestMap.put("app", "login");
			reuqestMap.put("act", "setPwd");
			reuqestMap.put("mobile", loginName);
			reuqestMap.put("newpassword", new_password);
			reuqestMap.put("repassword", confirm_password);
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
					Log.i("修改密码返回结果 ----", "" + result);
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
					}
					handler.sendMessage(msg);
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
				ToastUtil.showToast(mContext, message);
				SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("password", new_psw.getText().toString());
				editor.commit();
				ChangePswActivity.this.finish();
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			hideKeyboard();
		}
		return super.onTouchEvent(event);
	}

	private void hideKeyboard() {
		if (getCurrentFocus() != null
				&& getCurrentFocus().getWindowToken() != null) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(), 0);
		}
	}
}

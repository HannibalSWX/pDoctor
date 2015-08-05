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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Title:UploadPatientProfileActivity.java
 * @Description:应用UploadPatientProfileActivity.java类
 * @Author:owen
 * @Since:2015年7月20日
 * @Version:
 */
public class UploadPatientProfileActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private LinearLayout back_btn;

	private EditText et_tittle, et_content;

	private TextView tv_sure, tv_cancel;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String memberId, contact, message, code, uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_upload_patient_profile);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");

		Intent intent = getIntent();
		memberId = intent.getStringExtra("memberId");

		initview();
		setListener();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		et_tittle = (EditText) findViewById(R.id.et_tittle);
		et_content = (EditText) findViewById(R.id.et_content);
		tv_sure = (TextView) findViewById(R.id.tv_sure);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		tv_sure.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.tv_sure:
			// 提交名片资料信息
			if (et_tittle.getText().toString().equals("")) {
				ToastUtil.showToast(mContext, "请输入主题名称");
				return;
			}
			if (et_content.getText().toString().equals("")) {
				ToastUtil.showToast(mContext, "请输入内容");
				return;
			}
			submit();
			break;
		case R.id.tv_cancel:
			finish();
			break;
		default:
			break;
		}
	}

	// 提交患者资料
	private void submit() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("提交中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "");
			reuqestMap.put("act", "");
			reuqestMap.put("doctorId", uid);
			reuqestMap.put("memberId", memberId); // 用户编号
			reuqestMap.put("title", et_tittle.getText().toString()); // 主题名称
			reuqestMap.put("content", et_content.getText().toString()); // 内容
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
					Log.i("提交患者资料 ----", "login返回结果  :  " + result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
//							JSONObject data = Jsonresult.getJSONObject("data");
							
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
	}

	Handler loginhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(mContext, message);
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

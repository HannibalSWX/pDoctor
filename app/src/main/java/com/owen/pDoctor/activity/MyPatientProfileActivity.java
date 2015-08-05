package com.owen.pDoctor.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.model.MessagesBean;
import com.owen.pDoctor.model.MyGroupChildBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

/**
 * @Title:MyPatientProfileActivity.java
 * @Description:我的患者资料
 * @Author:owen
 * @Since:2015年7月16日
 * @Version:
 */
public class MyPatientProfileActivity extends BaseActivity implements
		OnClickListener {

	private Context mContext;

	private ImageView im_bg;

	private TextView tv_time, tv_jibing, tv_status, tv_period, tv_description,
			tv_pre, tv_next, tv_bingli;

	private LinearLayout ll_add_profile;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String name, contact, message, code, uid;
	
	private String vist_time, jibin_step, jibin_zz, jibin_gs, jibin_case, add_time, jibing_stage, jibin_name, vist_time_f;
	
	private MyGroupChildBean myChildBean;
	
	private MessagesBean msgBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_patient_profile);

		SharedPreferences sp = getSharedPreferences("userInfo",	Context.MODE_PRIVATE);
		uid = sp.getString("id", "");

		Intent intent = getIntent();
		if ((MyGroupChildBean)intent.getSerializableExtra("childItem") != null) {
			myChildBean = (MyGroupChildBean)intent.getSerializableExtra("childItem");
		} else {
			msgBean = (MessagesBean)intent.getSerializableExtra("msgItem");
		}

		initview();
		setListener();

		getPatientProfile();
	}

	private void initview() {
		// TODO Auto-generated method stub
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_jibing = (TextView) findViewById(R.id.tv_jibing);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_period = (TextView) findViewById(R.id.tv_period);
		tv_description = (TextView) findViewById(R.id.tv_description);
		tv_bingli = (TextView) findViewById(R.id.tv_bingli);
		ll_add_profile = (LinearLayout) findViewById(R.id.ll_add_profile);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		ll_add_profile.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_add_profile:
			// 添加患者诊断资料信息
			Intent intent2 = new Intent(mContext,
					UploadPatientProfileActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	// 获取患者资料
	private void getPatientProfile() {
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
			reuqestMap.put("act", "getUserBl");
			reuqestMap.put("docId", uid);
			if (msgBean != null) {
				reuqestMap.put("userId", msgBean.getMember_id()); // 用户编号
			} else {
				reuqestMap.put("userId", myChildBean.getMember_id()); // 用户编号
			}
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token",	EncryptionUtil.md5EncryptToString("jiankang2015"));
			reuqestMap.put("ver", getAppVersion());
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("获取患者资料 ----", "" + result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							JSONObject vists = data.getJSONObject("vists");
							vist_time = vists.optString("vist_time");
							jibin_step = vists.optString("jibin_step");
							jibin_zz = vists.optString("jibin_zz");
							jibin_gs = vists.optString("jibin_gs");
							jibin_case = vists.optString("jibin_case");
							jibin_name = vists.optString("jibin_name");
							vist_time_f = vists.optString("vist_time_f");
							
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
				tv_time.setText(vist_time);
				tv_jibing.setText(jibin_name);
				tv_status.setText(jibin_zz);
				tv_period.setText(jibin_step);
				tv_description.setText(jibin_gs);
				tv_bingli.setText(vist_time_f);
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

package com.owen.pDoctor.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.model.MyGroupBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

import android.content.Context;
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
 * @Title:AddpatientActivity.java
 * @Description:应用AddpatientActivity.java类
 * @Author:owen
 * @Since:2015年7月16日
 * @Version:
 */
public class AddpatientActivity extends BaseActivity implements OnClickListener {

	private LinearLayout back_btn;

	private TextView tv_save;

	private EditText et_phone;

	private String phone;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private ArrayList<MyGroupBean> myfabulist = new ArrayList<MyGroupBean>();

	private String username, uid, ids, message, code, servicephone;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addpatient);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("uid", "");
		username = sp.getString("userName", "");

		initview();
		setListener();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		tv_save = (TextView) findViewById(R.id.tv_save);
		et_phone = (EditText) findViewById(R.id.et_phone);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		tv_save.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.tv_save:
			ToastUtil.showToast(AddpatientActivity.this, "what's this?");
			save();
			finish();
			break;
		default:
			break;
		}

	}

	private void save() {
		// TODO Auto-generated method stub
		phone = et_phone.getText().toString();
	}

	// 添加患者
	private ArrayList<MyGroupBean> getGroupsList() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "group");
			reuqestMap.put("act", "getAllGroupByDocId");
			reuqestMap.put("doctorId", uid); // 医生编号
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
						Log.i("myfabulist ----", "" + result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		return myfabulist;
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {

			} else {

			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
}

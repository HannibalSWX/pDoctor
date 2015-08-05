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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Title:CustomizedCheckActivity.java
 * @Description:应用CustomizedCheckActivity.java类
 * @Author:owen
 * @Since:2015年7月16日
 * @Version:
 */
public class CustomizedCheckActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private DisplayMetrics metric;

	private int width, height;

	private LinearLayout back_btn;

	private ImageView im_bg;

	private TextView tv_name, tv_hospital, tv_keshi, tv_zhicheng, tv_shoujian_name, tv_contact, tv_address, tv_postcode,
			tv_pre, tv_next;

	private EditText et_contact, et_address, et_postcode;

	private String name, hospital, keshi, zhicheng, liuyan, shoujian_name, contact, address, postcode;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private Intent intent;

	private String message, code, uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_customized_check);

		// 获取屏幕宽高度（像素）设置背景高度
		metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）

		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		shoujian_name = intent.getStringExtra("shoujian_name");
		hospital = intent.getStringExtra("hospital");
		keshi = intent.getStringExtra("keshi");
		zhicheng = intent.getStringExtra("zhicheng");
		liuyan = intent.getStringExtra("liuyan");
		contact = intent.getStringExtra("contact");
		address = intent.getStringExtra("address");
		postcode = intent.getStringExtra("postcode");

		initview();
		setListener();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		im_bg = (ImageView) findViewById(R.id.im_bg);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_pre = (TextView) findViewById(R.id.tv_pre);
		tv_next = (TextView) findViewById(R.id.tv_next);
		tv_hospital = (TextView) findViewById(R.id.tv_hospital);
		tv_keshi = (TextView) findViewById(R.id.tv_keshi);
		tv_zhicheng = (TextView) findViewById(R.id.tv_zhicheng);
		tv_shoujian_name = (TextView) findViewById(R.id.tv_shoujian_name);
		tv_contact = (TextView) findViewById(R.id.tv_contact);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_postcode = (TextView) findViewById(R.id.tv_postcode);
		tv_name.setText(name);
		tv_hospital.setText(hospital);
		tv_keshi.setText(keshi);
		tv_zhicheng.setText(zhicheng);
		tv_shoujian_name.setText(shoujian_name);
		tv_contact.setText(contact);
		tv_address.setText(address);
		tv_postcode.setText(postcode);

		LayoutParams params = (LayoutParams) im_bg.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = 4 * width / 9;
		im_bg.setLayoutParams(params);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		tv_pre.setOnClickListener(this);
		tv_next.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.tv_pre:
			finish();
			break;

		case R.id.tv_next:
			// 提交名片信息
//			submit();
			Intent intent = new Intent(mContext, CustomizedSubmitActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void submit() {
		// TODO Auto-generated method stub
		if (name.equals("")) {
			Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (contact.equals("")) {
			Toast.makeText(this, "联系方式不能为空", Toast.LENGTH_SHORT).show();
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
			reuqestMap.put("name", name);
			reuqestMap.put("contact", contact);
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
							JSONObject obj = Jsonresult.getJSONObject("obj");
							uid = obj.getString("uid");
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
			if (msg.what == 200) {
				ToastUtil.showToast(mContext, message);
				Intent intent = new Intent(mContext, CustomizedSubmitActivity.class);
				startActivity(intent);
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

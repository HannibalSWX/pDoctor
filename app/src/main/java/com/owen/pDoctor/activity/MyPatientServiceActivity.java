package com.owen.pDoctor.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.model.MessagesBean;
import com.owen.pDoctor.model.MyGroupChildBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ImageLoader;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @Title:MyPatientServiceActivity.java
 * @Description:应用MyPatientServiceActivity.java类
 * @Author:owen
 * @Since:2015年7月20日
 * @Version:
 */
public class MyPatientServiceActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private ImageView im_me, im_open_service;

	private TextView tv_name, tv_free, tv_date;

	private LinearLayout ll_vip;

	private RelativeLayout rl_mianfei;

	private Dialog dialog;

	private String check_time = "";

	public static final int REQUSET = 101;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private Intent intent;

	private String userName, message, code, imagePath;
	
	private MyGroupChildBean myChildBean = new MyGroupChildBean();
	
	private MessagesBean msgBean = new MessagesBean();
	
	private ImageLoader mImageLoader = new ImageLoader(mContext);

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String date = intent.getStringExtra("date");
			tv_date.setText(date);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_patient_service);

		Intent intent = getIntent();
		if ((MyGroupChildBean)intent.getSerializableExtra("childItem") != null) {
			myChildBean = (MyGroupChildBean)intent.getSerializableExtra("childItem");
		} else {
			msgBean = (MessagesBean)intent.getSerializableExtra("msgItem");
		}
		
		initview();
		setListener();

		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.CHOOSE_DATE_BRAODCAST);
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private void initview() {
		// TODO Auto-generated method stub
		im_me = (ImageView) findViewById(R.id.im_me);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_free = (TextView) findViewById(R.id.tv_free);
		rl_mianfei = (RelativeLayout) findViewById(R.id.rl_mianfei);
		im_open_service = (ImageView) findViewById(R.id.im_open_service);
		
		if (myChildBean.getRealname() != null) {
			tv_name.setText(myChildBean.getRealname());
		} else if (msgBean.getNickname() != null) {
			tv_name.setText(msgBean.getNickname());
		} else {
			tv_name.setText("无名氏");
		}
		if (msgBean.getImgurl() != null) {
			imagePath = msgBean.getImgurl();
		} else {
			imagePath = myChildBean.getHeadimgurl();
		}
		// 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
		im_me.setImageResource(R.drawable.icon_username);
		if (imagePath == null || "".equals(imagePath)) {
			im_me.setImageResource(R.drawable.icon_username);
		} else {
			// 需要显示的网络图片
			mImageLoader.DisplayImage(imagePath, im_me, false);
		}
	}

	/**
	 * setListener
	 */
	private void setListener() {
		rl_mianfei.setOnClickListener(this);
		im_open_service.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_mianfei:
			Intent intent = new Intent();
			intent.setClass(this, FreeDateActivity.class);
			startActivity(intent);
			break;

		case R.id.im_open_service:
			startActivity(new Intent(this, PayServiceActivity.class));
			break;
		default:
			break;
		}

	}

	// 修改昵称
	private void change() {
		if (userName.equals("")) {
			ToastUtil.showToast(this, "请输入昵称");
			return;
		}
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("提交中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("userName", userName);
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("change nickname返回结果  : ----", result);
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
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		// overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				ToastUtil.showToast(MyPatientServiceActivity.this, message);
				Intent mIntent = new Intent(Constants.RESET_BRAODCAST);
				mIntent.putExtra("type", "reset");
				// 发送广播
				sendBroadcast(mIntent);
				MyPatientServiceActivity.this.finish();
			} else {
				ToastUtil.showToast(MyPatientServiceActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}

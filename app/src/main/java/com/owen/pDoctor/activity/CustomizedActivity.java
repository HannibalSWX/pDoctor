package com.owen.pDoctor.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.CustomizedAdapter;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * @Title:CustomizedActivity.java
 * @Description:应用CustomizedActivity.java类
 * @Author:owen
 * @Since:2015年7月16日
 * @Version:
 */
public class CustomizedActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private DisplayMetrics metric;
	
	private int width, height;
	
	private LinearLayout back_btn;

	private ImageView im_bg;

	private ListView lv_customized;

	private CustomizedAdapter customizedadapter;

	private Dialog dialog;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String code, message;

	private String[] dates = { "1", "2", "3", "4", "5", "6" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_customized);

		// 获取屏幕宽高度（像素）设置背景高度
		metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
				
		initview();
		setListener();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		im_bg = (ImageView) findViewById(R.id.im_bg);
		lv_customized = (ListView) findViewById(R.id.lv_customized);
		customizedadapter = new CustomizedAdapter(mContext, dates);
		lv_customized.setAdapter(customizedadapter);

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
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		default:
			break;
		}

	}

	// 修改昵称
	private void change() {
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("提交中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("userName", "");
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
				ToastUtil.showToast(CustomizedActivity.this, message);
				Intent mIntent = new Intent(Constants.RESET_BRAODCAST);
				mIntent.putExtra("type", "reset");
				// 发送广播
				sendBroadcast(mIntent);
				CustomizedActivity.this.finish();
			} else {
				ToastUtil.showToast(CustomizedActivity.this, message);
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
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}

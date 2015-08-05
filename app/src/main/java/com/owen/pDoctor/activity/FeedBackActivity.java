package com.owen.pDoctor.activity;

import java.util.HashMap;
import java.util.List;

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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ClassName：FeedBackActivity
 * Description：反馈
 * Author ： zhouqiang
 * Date ：2015-7-14 上午12:31:11
 */
public class FeedBackActivity extends BaseActivity implements OnClickListener {
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
	 * 输入反馈内容
	 */
	private EditText et_content;
	
	private TextView word_counter = null, tv_addpic, tv_paizhao;

	private String content;

	/**
	 * 提交按钮
	 */
	private Button submit_btn;
	
	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;
	
	private String uid, message, code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_feedback);

		SharedPreferences sp = getSharedPreferences("userInfo",	Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		
		initView();
		setListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		et_content = (EditText) findViewById(R.id.et_content);
		tv_addpic = (TextView) findViewById(R.id.tv_addpic);
		tv_paizhao = (TextView) findViewById(R.id.tv_paizhao);
		word_counter = (TextView) findViewById(R.id.word_counter);
		submit_btn = (Button) findViewById(R.id.submit_btn);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		tv_addpic.setOnClickListener(this);
		tv_paizhao.setOnClickListener(this);
		submit_btn.setOnClickListener(this);
		et_content.addTextChangedListener(textWatcher);
	}

	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			setTextCount();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			setTextCount();
		}

		@Override
		public void afterTextChanged(Editable s) {
			setTextCount();
		}
	};

	/**
	 * 设置文字改变状态
	 */
	private void setTextCount() {
		String textContent = et_content.getText().toString();
		int currentLength = textContent.length();
		if (currentLength <= 500) {
			word_counter.setTextColor(Color.BLACK);
			word_counter.setText(String.valueOf(500 - currentLength));
		} else {
			word_counter.setTextColor(Color.RED);
			word_counter.setText(String.valueOf(500 - currentLength));
		}
	}
	
	// 图标的点击事件
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.back_btn:
			/** 返回按钮 **/
			hideKeyboard();
			finish();
			break;

		case R.id.tv_addpic:
			/** 添加图片 **/
			
			break;
			
		case R.id.tv_paizhao:
			/** 拍照 **/
			
			break;
			
		case R.id.submit_btn:
			/** 提交按钮 **/
			submit();
			break;

		default:
			break;
		}
	}

	private void submit() {
		// TODO Auto-generated method stub
		content = et_content.getText().toString().trim();
		if (uid.equals("")) {
			ToastUtil.showToast(this, "请先登录");
			return;
		}
		if ("".equals(content) || null == content) {
			ToastUtil.showToast(this, "请输入反馈信息");
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
			reuqestMap.put("app", "feedback");
			reuqestMap.put("act", "addFeedback");
			reuqestMap.put("content", content);
			reuqestMap.put("type", "1"); // 用户类型 1:普通用户 2:医生
			reuqestMap.put("type_id", ""); // 用户编号
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
					Log.i("feedback返回结果 : ----", "" + result);
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
				ToastUtil.showToast(mContext, message);
				FeedBackActivity.this.finish();
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
	public void onBackPressed() {
		super.onBackPressed();
		hideKeyboard();
		FeedBackActivity.this.finish();
	}
	
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

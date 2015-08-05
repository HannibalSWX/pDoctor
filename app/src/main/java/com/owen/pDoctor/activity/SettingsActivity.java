package com.owen.pDoctor.activity;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.service.UploadService;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ClassName：SettingsActivity Description：个人设置页面 Author ： zhouqiang Date
 * ：2015-7-12 下午10:37:51
 */
public class SettingsActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	public List<String> urls;
	Uri uri;
	Intent intent;
	int gallerypisition = 0;

	private String usename = null;

	private TextView update_tv, logout, tv_exit, tv_cancel;

	private RelativeLayout rl_change_psw, rl_help, rl_feedback, rl_about_us, rl_version_update;

	private Dialog dialog;

	private LinearLayout back_btn, ll_immediately, ll_later;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String link, version, message, code;

	public static SettingsActivity sActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		sActivity = this;
		setContentView(R.layout.activity_settings);

		initAppVersion();
		initView();
		setListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		rl_change_psw = (RelativeLayout) findViewById(R.id.rl_change_psw);
		rl_help = (RelativeLayout) findViewById(R.id.rl_help);
		rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);
		rl_about_us = (RelativeLayout) findViewById(R.id.rl_about_us);
		rl_version_update = (RelativeLayout) findViewById(R.id.rl_version_update);
		update_tv = (TextView) findViewById(R.id.update_tv);
		update_tv.setText("当前版本V" + Constants.APP_VERSION_NAME);
		logout = (TextView) findViewById(R.id.logout);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		rl_change_psw.setOnClickListener(this);
		rl_help.setOnClickListener(this);
		rl_feedback.setOnClickListener(this);
		rl_about_us.setOnClickListener(this);
		rl_version_update.setOnClickListener(this);
		logout.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 初始化App版本信息
	 */
	private void initAppVersion() {
		PackageManager manager = mContext.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			Constants.APP_VERSION_CODE = info.versionCode;
			Constants.APP_VERSION_NAME = info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	// 图标的点击事件
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			/** 返回按钮 **/
			finish();
			break;

		case R.id.rl_change_psw:
			/** 修改密码 **/
			startActivity(new Intent(SettingsActivity.this, ChangePswActivity.class));
			break;

		case R.id.rl_help:
			/** 使用帮助 **/
			startActivity(new Intent(SettingsActivity.this, HelpActivity.class));
			break;

		case R.id.rl_feedback:
			/** 用户反馈 **/
			startActivity(new Intent(SettingsActivity.this, FeedBackActivity.class));
			break;

		case R.id.rl_about_us:
			/** 关于我们 **/
			startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
			break;

		case R.id.rl_version_update:
			/** 版本更新 **/
			checkversion();
			break;

		case R.id.logout:
			/** 退出登录 **/
			exitdialog();
			break;

		default:
			break;
		}
	}

	// 检查版本更新
	private void checkversion() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("version", Constants.APP_VERSION_NAME);
			reuqestMap.put("type", "2");
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("checkresult ----", result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							JSONObject obj = data.getJSONObject("obj");
							version = obj.getString("version").toString();
							link = obj.getString("lnk").toString();
							Constants.APP_DOWNLOAD_URL = link;

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
				if (Constants.APP_VERSION_NAME.equals(version)) {
					ToastUtil.showToast(mContext, "当前已是最新版本");
				} else {
					showdialog();
				}
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 版本更新提示dialog
	private void showdialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(mContext, R.style.home_dialog);
		dialog.setContentView(R.layout.alert_dialog_update);
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.CENTER_VERTICAL);
		ll_immediately = (LinearLayout) dialogWindow.findViewById(R.id.ll_immediately);
		ll_later = (LinearLayout) dialogWindow.findViewById(R.id.ll_later);
		ll_immediately.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				Intent upIntent = new Intent(mContext, UploadService.class);
				startService(upIntent);
			}
		});
		ll_later.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// lp.x = 100; // 新位置X坐标
		// lp.y = 300; // 新位置Y坐标
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		// lp.alpha = 0.7f; // 透明度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 退出登录dialog
	private void exitdialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(this, R.style.home_dialog);
		dialog.setContentView(R.layout.exit_dialog);

		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.BOTTOM);
		tv_exit = (TextView) dialogWindow.findViewById(R.id.tv_exit);
		tv_cancel = (TextView) dialogWindow.findViewById(R.id.tv_cancel);
		tv_exit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("loginName", ""); // 登录账号
				editor.putString("userName", ""); // 昵称
				editor.putString("password", "");
				editor.putString("id", "");
				editor.commit();

				dialog.dismiss();
				MainActivity.mActivity.finish();
				SettingsActivity.this.finish();
				System.exit(0);
			}
		});
		tv_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 结束这个activity
	public void finishMe() {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

package com.owen.pDoctor.activity;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.ToastUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @Title:AboutActivity.java
 * @Description:应用AboutActivity.java类
 * @Author:owen
 * @Since:2015年7月22日
 * @Version:
 */
public class AboutActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private ImageView personal_im;

	private TextView version_code, tv_kefu_phone, tv_kefu_mail, tv_my_url, tv_about_instruction;

	private LinearLayout back_btn;

	private RelativeLayout rl_kefu_phone, rl_kefu_mail;

	private Dialog dialog;

	private TextView tv_phone_no, tv_cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_about);

		initAppVersion();
		initview();
		setListener();
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

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		version_code = (TextView) findViewById(R.id.version_code);
		tv_kefu_phone = (TextView) findViewById(R.id.tv_kefu_phone);
		tv_kefu_mail = (TextView) findViewById(R.id.tv_kefu_mail);
		tv_my_url = (TextView) findViewById(R.id.tv_my_url);
		tv_about_instruction = (TextView) findViewById(R.id.tv_about_instruction);

		rl_kefu_phone = (RelativeLayout) findViewById(R.id.rl_kefu_phone);
		rl_kefu_mail = (RelativeLayout) findViewById(R.id.rl_kefu_mail);

		version_code.setText(Constants.APP_VERSION_NAME + " (2015.07.15)");
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		rl_kefu_phone.setOnClickListener(this);
		rl_kefu_mail.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.rl_kefu_phone:
			showdialog(tv_kefu_phone.getText().toString(), 1);
			break;

		case R.id.rl_kefu_mail:
			showdialog(tv_kefu_mail.getText().toString(), 2);
			break;
		default:
			break;
		}

	}

	private void showdialog(final String content, final int from) {
		// TODO Auto-generated method stub
		dialog = new Dialog(this, R.style.home_dialog);
		dialog.setContentView(R.layout.about_info_dialog);

		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.BOTTOM);
		tv_phone_no = (TextView) dialogWindow.findViewById(R.id.tv_phone_no);
		tv_cancel = (TextView) dialogWindow.findViewById(R.id.tv_cancel);
		if (from == 1) {
			StringBuilder sb = new StringBuilder(content);
			sb.insert(2, " ");
			sb.insert(6, " ");
		} else {
			tv_phone_no.setText(content);
		}
		tv_phone_no.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				if (from == 1) {
					// 用intent启动拨打电话
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + content));
					startActivity(intent);
				} else {
					ToastUtil.showToast(mContext, content);
				}
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

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}

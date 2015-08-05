package com.owen.pDoctor;

import java.util.UUID;

import com.owen.pDoctor.util.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.widget.Toast;

public class BaseActivity extends Activity {

	private ProgressDialog _dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	public void showProgressDialog(String message) {
		if (this._dialog == null) {
			this._dialog = new ProgressDialog(this);
		}
		if (!this._dialog.isShowing()) {
			this._dialog = ProgressDialog.show(this, "", message, true);
			this._dialog.setCancelable(false);
			this._dialog.setCanceledOnTouchOutside(false);
		}
	}

	public void showProgressParentDialog(String message) {
		if (this._dialog == null) {
			this._dialog = new ProgressDialog(getParent());
		}
		if (!this._dialog.isShowing()) {
			this._dialog = ProgressDialog.show(getParent(), "", message, true);
			this._dialog.setCancelable(false);
			this._dialog.setCanceledOnTouchOutside(false);
		}
	}

	public void dismissProgressDialog() {
		if ((this._dialog != null) && (this._dialog.isShowing())) {
			this._dialog.dismiss();
		}
	}

	public void showToast(String message) {
		Toast.makeText(this, message, 0).show();
		dismissProgressDialog();
	}

	/**
	 * 获取设备唯一序号
	 * 
	 * @return
	 */
	public String getUUid() {
		TelephonyManager tmManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice = tmManager.getDeviceId();
		final String androidId = android.provider.Settings.Secure.getString(getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		// UUID deviceUid = new UUID(androidId.hashCode(),
		// ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId;
		if (tmDevice != null && !tmDevice.equals("")) {
			UUID deviceUid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32));
			uniqueId = deviceUid.toString();
		} else {
			UUID deviceUid = new UUID(androidId.hashCode(), ((long) androidId.hashCode() << 32));
			uniqueId = deviceUid.toString();
		}
		return uniqueId;
	}

	/**
	 * 初始化App版本信息
	 */
	public String getAppVersion() {
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			Constants.APP_VERSION_CODE = info.versionCode;
			Constants.APP_VERSION_NAME = info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Constants.APP_VERSION_NAME;
	}
	
	@Override
	protected void onDestroy() {

		super.onDestroy();
		ActivityManager.removeActivity(this);
	}

	@Override
	public void onBackPressed() {

		this.finish();
		ActivityManager.removeActivity(this);
		super.onBackPressed();
	}

}

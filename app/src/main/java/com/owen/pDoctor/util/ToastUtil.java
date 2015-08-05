package com.owen.pDoctor.util;

import android.content.Context;
import android.widget.Toast;

public final class ToastUtil {
	/**
	 * Toast 提示信息
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void showToast(Context ctx, String msg) {
		try {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
//			Log.e("", "show toast error:"+e.getMessage());
		}
	}
	
	public static void showError(Context ctx){
		showToast(ctx, "请求失败，请稍后再试");
	}

	/**
	 * 开发时提示信息，发布时修改isTest不提示信息
	 * 
	 * @param ctx
	 * @param msg
	 * @param isTest
	 *            isTest == true : (测试，非测试)
	 */
	public static void showToastTestMode(Context ctx, String msg, boolean isTest) {
		if (isTest) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		}

	}
}

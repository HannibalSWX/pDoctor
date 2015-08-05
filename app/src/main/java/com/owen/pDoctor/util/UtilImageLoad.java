package com.owen.pDoctor.util;

import android.content.Context;
import android.os.Environment;

public class UtilImageLoad {
	private static UtilImageLoad util;
	public static int flag = 0;
	private UtilImageLoad() {

	}

	public static UtilImageLoad getInstance() {
		if (util == null) {
			util = new UtilImageLoad();
		}
		return util;
	}

	/**
	 * 判断是否有sdcard
	 * @return
	 */
	public boolean hasSDCard() {
		boolean b = false;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			b = true;
		}
		return b;
	}

	/**
	 * 得到sdcard路径
	 * @return
	 */
	public String getExtPath() {
		String path = "";
		if (hasSDCard()) {
			path = Environment.getExternalStorageDirectory().getPath();
		}
		return path;
	}

	/**
	 * 得到/data/data/gsearch.imagedownload目录
	 * @param mContext
	 * @return
	 */
	public String getPackagePath(Context mContext) {
		return mContext.getFilesDir().toString();
	}

	/**
	 * 根据url得到图片名
	 * @param url
	 * @return
	 */
	public String getImageName(String url) {
		String imageName = "";
		if (url != null) {
			imageName = url.substring(url.lastIndexOf("/") + 1);
		}
		return imageName;
	}
}

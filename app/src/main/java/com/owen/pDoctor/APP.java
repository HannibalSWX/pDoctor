package com.owen.pDoctor;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.owen.pDoctor.activity.HomeHeatDetailActivity;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CrashLogWriter;

/**
 * 项目主入口
 * 
 * @author ken
 * 
 */
public class APP extends Application {

	private static APP mInstance = null;
	
	private Map<String, SoftReference<Bitmap>> imageCaches = null;

	public static APP getInstance() {
//		if (null == mInstance) {
//			mInstance = new APP();
//		}
//		return mInstance;
		
		if (mInstance == null) {
			throw new IllegalStateException();
		}
		return mInstance;
	}

	public APP() {
		mInstance = this;
		imageCaches = new HashMap<String, SoftReference<Bitmap>>();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
//		mkAPPDir();
		
//		String logPath = Environment.getExternalStorageDirectory()
//				+ File.separator + "gsearch";
//		File file = new File(logPath);
//		if (file.exists()) {
//			file.delete();
//		}
//		 生成crashLog
//		CrashLogWriter crashHandler = CrashLogWriter.getInstance();
//		crashHandler.init(getApplicationContext());
		
		// 打开service记录所有Log并存写入文件到sd卡
//		Intent intentService = new Intent(APP.this, SaveLog2File.class);
//		startService(intentService);
	}

	private void mkAPPDir() {

		mkPicPath();
		mkVoicePath();

	}

	private void mkPicPath() {

		File file = new File(Constants.BASE_DIR_PIC_PATH);

		if (!file.exists()) {
			file.mkdirs();
		}
	}

	private void mkVoicePath() {

		File file = new File(Constants.BASE_DIR_VOICE_PATH);

		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		imageCaches.clear();
	}
	
	public Map<String, SoftReference<Bitmap>> getImageCaches() {
		return imageCaches;
	}
}

package com.owen.pDoctor.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.owen.pDoctor.R;
import com.owen.pDoctor.activity.MainActivity;
import com.owen.pDoctor.util.Constants;

/**
 * ClassName：UploadService
 * Description：
 * Author ： zhouqiang
 * Date ：2015-2-2 下午6:40:02
 * Copyright (C) 2012-2014 owen
 */
public class UploadService extends Service {

	private String app_name_title;

	// 文件存储
	private File updateDir = null;
	private File updateFile = null;

	// 通知栏
	public static NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;

	// 下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	public static HttpURLConnection httpConnection = null;
	public static boolean isDownOk = false;
	public static int downTotalSize = 0;
	private boolean sdCardIs = false;

	/**
	 * 
	 */
	@SuppressWarnings("static-access")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isDownOk = true;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
//		app_name_title = getResources().getString(R.string.app_name) + sdf.format(new Date());
		app_name_title = getResources().getString(R.string.app_name);

		// 创建文件
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			sdCardIs = true;
			updateDir = new File(Environment.getExternalStorageDirectory(), "/gsearch/apk/");
			updateFile = new File(updateDir.getPath(), app_name_title + ".apk");
		} else {
			updateFile = new File(this.getCacheDir(), app_name_title + ".apk");
		}

		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();

		// 设置下载过程中，点击通知栏，回到主界面
		updateIntent = new Intent(this, MainActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
				0); // 第三个null为信息栏点击后intent的类
		// 设置通知栏显示内容
		updateNotification.icon = android.R.drawable.stat_sys_download;
		updateNotification.tickerText = "开始下载";
		updateNotification.setLatestEventInfo(this, app_name_title, "0%",
				updatePendingIntent);
		// 发出通知
		updateNotificationManager.notify(0, updateNotification);

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable()).start();// 这个是下载的重点，是下载的过程
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 
	 * @author C
	 * 
	 */
	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {
				// 增加权限
				if (sdCardIs) {
					if (!updateDir.exists()) {
						updateDir.mkdirs();
					}
					if (!updateFile.exists()) {
						updateFile.createNewFile();
					}
				}

				long downloadSize = downloadUpdateFile(Constants.APP_DOWNLOAD_URL, updateFile);
				if (downloadSize > 0) {
					// 下载成功
					updateHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}
	}

	/**
	 * 
	 */
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DOWNLOAD_COMPLETE :
					// 点击安装PendingIntent
					Uri uri = Uri.fromFile(updateFile);
					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					installIntent.setDataAndType(uri,
							"application/vnd.android.package-archive");

					updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
					updateNotification.setLatestEventInfo(UploadService.this,
							app_name_title, "下载完成。", updatePendingIntent);
					updateNotificationManager.notify(0, updateNotification);
					updateNotificationManager.cancel(0);
//					updateNotificationManager.cancelAll();
					startActivity(installIntent);
					isDownOk = false;
					// 停止服务
					stopService(updateIntent);
					stopSelf();
				case DOWNLOAD_FAIL :
					isDownOk = false;
					// 下载失败
					updateNotification.setLatestEventInfo(UploadService.this,
							app_name_title, "下载失败,请重新下载。", null);
				default :
					stopService(updateIntent);
					stopSelf();
			}
		}
	};

	/**
	 * 
	 * @param downloadUrl
	 * @param saveFile
	 * @return
	 * @throws Exception
	 */
	public long downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			// httpConnection.setConnectTimeout(Integer.parseInt(HttpProperties
			// .getProperty("http_timeoutConnection")));
			// httpConnection.setReadTimeout(Integer.parseInt(HttpProperties
			// .getProperty("http_timeoutSocket")));
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 1 > downloadCount) {
					downloadCount += 1;
					downTotalSize = (int) totalSize * 100 / updateTotalSize;
					updateNotification.setLatestEventInfo(UploadService.this,
							"已在下载", downTotalSize + "%", updatePendingIntent);
					updateNotificationManager.notify(0, updateNotification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		stopService(updateIntent);
	}
}

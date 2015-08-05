package com.owen.pDoctor.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.AppConstants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Title:CreateQRImageTest.java
 * @Description:应用CreateQRImageTest.java类
 * @Author:owen
 * @Since:2015年7月16日
 * @Version:
 */
public class MyQRImageActivity extends BaseActivity implements OnClickListener {

	private Context mContext;

	private LinearLayout back_btn;

	// 我的二维码图片
	private ImageView im_top_right, im_myqr;

	private int QR_WIDTH = 200, QR_HEIGHT = 200;

	private TextView tv_tittle, tv_addpatient, tv_name, tv_zhicheng, tv_kehao;

	private LinearLayout ll_share_weixin, ll_share_friends, ll_share_qq, ll_share_qzone;

	private String from, name, zhicheng, kehao;

	private int shareType_qzone = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;

	private int shareType_qq = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;

	private int mExtarFlag = 0x00;

	// 发送的目标场景，WXSceneSession表示发送到会话
	private static final int WXSceneSession = 0;

	// 发送的目标场景，WXSceneTimeline表示发送朋友圈
	private static final int WXSceneTimeline = 1;
	private IWXAPI api;

	private SharedPreferences sp;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String uid, userName, loginName, message, code, qrcode;
	
	private Bitmap qrBitmap;

	private String shareImg = "http://a.picphotos.baidu.com/album/h%3D370%3Bq%3D90%3Bg%3D0/sign=f75a14dc70c6a7efa626ae21cdc1de6c/2cf5e0fe9925bc314c2bb09b58df8db1cb1370ba.jpg";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_create_qrimage);

		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		userName = sp.getString("userName", "");
		loginName = sp.getString("loginName", "");
		qrcode = sp.getString("qrcode", "");

		Intent intent = getIntent();
		from = intent.getStringExtra("from");

		initview();
		setListener();
		if (!uid.equals("")) {
			initdate();
			getcreateQR();
		} else {
			ToastUtil.showToast(mContext, "未登录，请登录...");
		}

		// 注册微信sdk
		api = WXAPIFactory.createWXAPI(mContext, AppConstants.Weixin_APP_ID, true);
		api.registerApp(AppConstants.Weixin_APP_ID);
	}

	private void initview() {
		// TODO Auto-generated method stub
		tv_tittle = (TextView) findViewById(R.id.tv_tittle);
		im_top_right = (ImageView) findViewById(R.id.im_top_right);
		im_myqr = (ImageView) findViewById(R.id.im_myqr);
		tv_addpatient = (TextView) findViewById(R.id.tv_addpatient);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_zhicheng = (TextView) findViewById(R.id.tv_zhicheng);
		tv_kehao = (TextView) findViewById(R.id.tv_kehao);
		if (from.equals("mycenter")) {
			tv_tittle.setText("我的二维码");
			im_top_right.setVisibility(View.GONE);
			tv_addpatient.setVisibility(View.GONE);
		} else {
			tv_tittle.setText("添加患者");
			im_top_right.setVisibility(View.VISIBLE);
			tv_addpatient.setVisibility(View.VISIBLE);
		}

		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		ll_share_weixin = (LinearLayout) findViewById(R.id.ll_share_weixin);
		ll_share_friends = (LinearLayout) findViewById(R.id.ll_share_friends);
		ll_share_qq = (LinearLayout) findViewById(R.id.ll_share_qq);
		ll_share_qzone = (LinearLayout) findViewById(R.id.ll_share_qzone);
	}

	private void initdate() {
		// TODO Auto-generated method stub
		if (!userName.equals("")) {
			tv_name.setText(userName);
		} else if (userName.equals("") && !loginName.equals("")) {
			tv_name.setText(loginName);
		} else {
			tv_name.setText("");
		}
		tv_zhicheng.setText("");
		tv_kehao.setText(uid);
	}

	@Override
	public void onResume() {
		super.onResume();
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		initdate();
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		im_top_right.setOnClickListener(this);
		tv_addpatient.setOnClickListener(this);
		ll_share_weixin.setOnClickListener(this);
		ll_share_friends.setOnClickListener(this);
		ll_share_qq.setOnClickListener(this);
		ll_share_qzone.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.im_top_right:
			ToastUtil.showToast(MyQRImageActivity.this, "what's this?");
			break;

		case R.id.tv_addpatient:
			Intent intent = new Intent(MyQRImageActivity.this, AddpatientActivity.class);
			startActivity(intent);
			break;

		case R.id.ll_share_weixin:
			sendWxUrl(WXSceneSession); // 分享到微信好友
			break;

		case R.id.ll_share_friends:
			sendWxUrl(WXSceneTimeline); // 分享到微信朋友圈
			break;

		case R.id.ll_share_qq:
			sharetoqq(); // 分享到QQ
			break;

		case R.id.ll_share_qzone:
			Bundle params = new Bundle();
			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType_qzone);
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "百姓医生");
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "百姓医生");
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, AppConstants.SERVER_URL);
			if (shareType_qzone == QzoneShare.SHARE_TO_QZONE_TYPE_APP) {
				// app分享不支持传目标链接
				params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");
			}
			// 支持传多个imageUrl
			ArrayList<String> imageUrls = new ArrayList<String>();
			// for (int i = 0; i < 5; i++) {
			// imageUrls.add(adsBean.get(0).getImageUrl());
			// }
			// http://imgsrc.baidu.com/forum/w%3D72/sign=3098278a8318367aad897ddf2f73bd0e/bdadc409b3de9c825191132c6981800a1bd84396.jpg
			imageUrls.add(shareImg);
			// String imageUrl = "XXX";
			// params.putString(Tencent.SHARE_TO_QQ_IMAGE_URL, imageUrl);
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
			doShareToQzone(params);
			break;
		default:
			break;
		}

	}

	// 获取二维码
	private void getcreateQR() {
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("获取中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "qrcode");
			reuqestMap.put("act", "getQrcode");
			reuqestMap.put("id", uid);
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token", EncryptionUtil.md5EncryptToString("jiankang2015"));
			reuqestMap.put("ver", getAppVersion());
			String url = AppConstants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("getQR返回结果  : ----", result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							qrcode = Jsonresult.getString("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						khandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}

	Handler khandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				createQRImage(qrcode);
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	/**
	 * 要转换的地址或字符串,可以是中文
	 * 
	 * @param url
	 */
	public void createQRImage(String url) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			qrBitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			qrBitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// 显示到一个ImageView上面
			im_myqr.setImageBitmap(qrBitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	// 分享到QQ
	protected void sharetoqq() {
		// TODO Auto-generated method stub
		final Bundle params = new Bundle();
		if (shareType_qq != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
			params.putString(QQShare.SHARE_TO_QQ_TITLE, mContext.getResources().getString(R.string.share_title));
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, AppConstants.SERVER_URL);
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "百姓医生");
		}
		if (shareType_qq == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareImg);
		} else {
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareImg);
		}
		params.putString(shareType_qq == QQShare.SHARE_TO_QQ_TYPE_IMAGE ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
				: QQShare.SHARE_TO_QQ_IMAGE_URL, shareImg);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "appName");
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType_qq);
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);
		if (shareType_qq == QQShare.SHARE_TO_QQ_TYPE_AUDIO) {
			params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, "mEditTextAudioUrl");
		}
		if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN) != 0) {
			showToast("在好友选择列表会自动打开分享到qzone的弹窗~~~");
		} else if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE) != 0) {
			showToast("在好友选择列表隐藏了qzone分享选项~~~");
		}
		doShareToQQ(params);
	}

	IUiListener qqShareListener = new IUiListener() {
		@Override
		public void onCancel() {
			if (shareType_qq != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
				ToastUtil.showToast(MyQRImageActivity.this, "cancel");
			}
		}

		@Override
		public void onComplete(Object response) {
			// TODO Auto-generated method stub
			ToastUtil.showToast(MyQRImageActivity.this, "onComplete: " + response.toString());
		}

		@Override
		public void onError(UiError e) {
			// TODO Auto-generated method stub
			ToastUtil.showToast(MyQRImageActivity.this, "onError: " + e.errorMessage);
		}
	};

	private void doShareToQQ(final Bundle params) {
		MainActivity.mTencent.shareToQQ(MyQRImageActivity.this, params, qqShareListener);
	}

	/**
	 * 此方法是写的分享链接，如果朋友想要分享【图片，文字，等其他可下载微信官方Demo，SendToWXActivity.java类中写的很清楚】
	 * 
	 * @param scene
	 *            0代表好友 1代表朋友圈
	 */
	public void sendWxUrl(int scene) {
		WXWebpageObject webpage = new WXWebpageObject();
		if (!AppConstants.SERVER_URL.equals("")) {
			webpage.webpageUrl = AppConstants.SERVER_URL;
		} else {
			webpage.webpageUrl = mContext.getResources().getString(R.string.share_url);
		}
		WXMediaMessage msg = new WXMediaMessage(webpage);
		// msg.title = mContext.getResources().getString(R.string.share_title);
		msg.title = mContext.getResources().getString(R.string.share_title);
		// msg.description =
		// mContext.getResources().getString(R.string.share_content);
		msg.description = "";
		Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
		if (qrBitmap != null) {
			msg.thumbData = getBitmapBytes(qrBitmap, false);
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		if (scene == 0) {
			// 分享到微信
			req.scene = SendMessageToWX.Req.WXSceneSession;
		} else {
			// 分享到微信朋友圈
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
		}
		api.sendReq(req);
	}

	// 需要对图片进行处理，否则微信会在log中输出thumbData检查错误
	private static byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
		int i;
		int j;
		int size;
		float sx, sy;
		if (bitmap.getHeight() > bitmap.getWidth()) {
			i = bitmap.getWidth();
			j = bitmap.getWidth();
		} else {
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
		size = bitmap.getWidth() / bitmap.getHeight();
		sx = (float) 80 / bitmap.getWidth();
		sy = (float) 80 / bitmap.getHeight();
		Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);
		while (true) {
			Matrix matrix = new Matrix();
			matrix.postScale(sx, sy);
			localCanvas.drawBitmap(bitmap, matrix, null);
			if (paramBoolean)
				bitmap.recycle();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception e) {
				// F.out(e);
				e.printStackTrace();
			}
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
	}

	// 过滤发送内容格式
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (MainActivity.mTencent != null) {
			MainActivity.mTencent.releaseResource();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_QZONE_SHARE) {
			if (resultCode == Constants.ACTIVITY_OK) {
				Tencent.handleResultData(data, qZoneShareListener);
			}
		} else if (requestCode == Constants.REQUEST_QQ_SHARE) {
			if (resultCode == Constants.ACTIVITY_OK) {
				Tencent.handleResultData(data, qqShareListener);
			}
		} else {
			String path = null;
			if (resultCode == Activity.RESULT_OK) {
				if (data != null && data.getData() != null) {
					// 根据返回的URI获取对应的SQLite信息
					Uri uri = data.getData();
					final String[] proj = { MediaStore.Images.Media.DATA };
					Cursor cursor = null;
					try {
						cursor = this.getContentResolver().query(uri, proj, null, null, null);
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						path = cursor.getString(column_index);
					} finally {
						if (cursor != null) {
							cursor.close();
							cursor = null;
						}
					}
				}
			}
			if (path != null) {
				// 这里很奇葩的方式, 将获取到的值赋值给相应的EditText, 竟然能对应上
				// EditText editText =
				// (EditText)mImageContainerLayout.findViewById(requestCode +
				// 1000);
				// editText.setText(path);
			} else {
				// showToast("请重新选择图片");
			}
		}
	}

	private static final void startPickLocaleImage(Activity activity, int requestId) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.local_img)),
				requestId);
	}

	IUiListener qZoneShareListener = new IUiListener() {

		@Override
		public void onCancel() {
			ToastUtil.showToast(MyQRImageActivity.this, "cancel");
		}

		@Override
		public void onError(UiError e) {
			// TODO Auto-generated method stub
			ToastUtil.showToast(MyQRImageActivity.this, "onError: " + e.errorMessage);
		}

		@Override
		public void onComplete(Object response) {
			// TODO Auto-generated method stub
			ToastUtil.showToast(MyQRImageActivity.this, "onComplete: " + response.toString());
		}

	};

	/**
	 * 用异步方式启动分享
	 * 
	 * @param params
	 */
	private void doShareToQzone(final Bundle params) {
		final Activity activity = MyQRImageActivity.this;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MainActivity.mTencent.shareToQzone(activity, params, qZoneShareListener);
			}
		}).start();
	}
}

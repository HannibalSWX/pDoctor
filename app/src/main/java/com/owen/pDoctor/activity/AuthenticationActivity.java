package com.owen.pDoctor.activity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.model.FileInfo;
import com.owen.pDoctor.model.MyfabuModifyBean;
import com.owen.pDoctor.network.FileUpload;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ImageSelectTools;
import com.owen.pDoctor.util.ImageTools;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Title:AuthenticationActivity.java
 * @Description:应用AuthenticationActivity.java类
 * @Author:owen
 * @Since:2015年7月17日
 * @Version:
 */
public class AuthenticationActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	public static AuthenticationActivity authActivity;

	private LinearLayout back_btn;

	private Button submit_btn;

	private ImageView im_add;

	private int i = 0; // 删除按钮tag值，从0开始

	private String picPath;

	private List<String> listPath = new ArrayList<String>();// 存放路径的list

	private List<String> urlsPath = new ArrayList<String>();// 存放返回imgUrl的list

	private CustomProgressDialog progressDialog = null;

	private Dialog dialog;

	private LinearLayout ll_cancel, ll_sure;

	private TextView tv_tittle, tv_hint, tv_hint2, tv_my_tittle, paizhao_tv, xiangce_tv, cancel_tv, tv_submit;

	private String uid;

	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	private static final int CROP = 2;
	private static final int CROP_PICTURE = 3;
	private static final int SCALE = 5;// 照片缩小比例
	final boolean crop = false;
	int REQUEST_CODE;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private String code, message, imgUrl = "";

	private LinearLayout linear_images;

	private String strImgPath = "";// 照片文件绝对路径

	private String uploadcode = null;

	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	private ByteArrayOutputStream baos;

	private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String type = intent.getStringExtra("type");
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		authActivity = this;
		setContentView(R.layout.activity_authentication);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");

		// uid不为空，修改认证
		if (uid != null && !uid.equals("")) {
			getdata();
		}

		initView();
		setListener();

		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.SEND_BRAODCAST);
		registerReceiver(exitReceiver, myIntentFilter);
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		im_add = (ImageView) findViewById(R.id.im_add);
		linear_images = (LinearLayout) findViewById(R.id.linearlayout_images);
		tv_tittle = (TextView) findViewById(R.id.tv_tittle);
		tv_hint = (TextView) findViewById(R.id.tv_hint);
		tv_hint2 = (TextView) findViewById(R.id.tv_hint2);
		tv_my_tittle = (TextView) findViewById(R.id.tv_my_tittle);
		tv_submit = (TextView) findViewById(R.id.tv_submit);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		im_add.setOnClickListener(this);
		tv_submit.setOnClickListener(this);
	}

	// 图标的点击事件
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.back_btn:
			/** 返回按钮 **/
			Utils.showDialog(mContext, "信息尚未发布，确认离开吗？", "exit");
			hideKeyboard();
			break;

		case R.id.im_add:
			addPicDialog();
			break;

		case R.id.tv_submit:
			/** 提交按钮 **/
			if (Utils.isNetConn(this)) {
				if (uid.equals("")) {
					ToastUtil.showToast(this, "请先登录");
					return;
				}
				Utils.hintDialog(mContext, "确认发送吗？", "每天最多认证5次，好好把握哦", "再拍几张", "确定发送");
				// submit(); // 上传完图片后再提交发布
			} else {
				ToastUtil.showToast(this, "网络异常,请检查网络!");
			}
			break;

		default:
			break;
		}
	}

	// 获取发布过的item数据
	private void getdata() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "doctor");
			reuqestMap.put("act", "getDocImg");
			reuqestMap.put("doctor_id", uid);
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
					if (result != null && !result.equals("")) {
						Log.i("myfabuitem ----", "" + result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						heatHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}

	Handler heatHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				im_add.setVisibility(View.GONE);

				new Thread(setbitmap).start();
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};

	// 加载图片
	private Runnable setbitmap = new Runnable() {
		@Override
		public void run() {
//			String[] cont = myBean.getImg().replaceAll("\\[([^\\]]*)\\]", "$1").toString().replaceAll("\\\\", "").trim()
//					.split("\\,");
//			for (int i = 0; i < cont.length; i++) {
//				getImageView(cont[i].replace("\"", ""), "net"); // 显示图片
//				urlsPath.add(cont[i].replace("\"", ""));
//			}
		}
	};

	// 上传图片，单张上传
	private void uploadpic(String picPath) {
		// TODO Auto-generated method stub
		Map<String, String> param = new HashMap<String, String>();
		param.put("uid", uid);
		Map<String, File> files = new HashMap<String, File>();
		File file = new File(picPath);
		FileInfo fileInfo = new FileInfo();
		fileInfo.Name = getResources().getString(R.string.d);
		fileInfo.Path = file.getAbsolutePath();
		fileInfo.Size = file.length();
		fileInfo.realyName = file.getName();
		List<FileInfo> fileInfos = new ArrayList<FileInfo>();
		fileInfos.add(fileInfo);
		files.put(fileInfo.getRealyName(), file);
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("上传图片中...");
		}
		progressDialog.show();
		fileUpload(param, files, myHandler);
	}

	private void fileUpload(final Map<String, String> param, final Map<String, File> files, final Handler handler) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				String url = Constants.SERVER_URL + Constants.UPLOAD_URL;
				String result = FileUpload.postUploadFile(url, param, files);
				Log.i("上传文件", "上传文件返回结果 : " + result);
				Message msg = new Message();
				if (null != result && result.length() > 0) {
					try {
						JSONObject Jsonresult = new JSONObject(result);
						uploadcode = Jsonresult.getString("code");
						imgUrl = Jsonresult.getString("path");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (uploadcode.equals("100")) {
						msg.what = 0;
					} else {
						msg.what = 1;
					}
				} else {
					msg.what = 1;
				}
				myHandler.sendMessage(msg);

				// 0上传成功 1上传失败（false)
			}
		}.start();
	}

	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (msg.what == 0) {
				im_add.setVisibility(View.GONE);
				ToastUtil.showToast(mContext, "上传图片成功！");
				getImageView(strImgPath, "local");// 显示图片
				urlsPath.add(Constants.SERVER_URL + imgUrl.substring(1));
			} else {
				ToastUtil.showToast(mContext, "上传图片失败！");
			}
		};
	};

	public void submit() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (urlsPath.equals("") || urlsPath.size() < 1) {
				ToastUtil.showToast(this, "请上传图片！");
				return;
			}

			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("提交中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
//			reuqestMap.put("imgUrl", (urlsPath + "").replaceAll("\\[([^\\]]*)\\]", "$1").toString());
			reuqestMap.put("app", "doctor");
			reuqestMap.put("act", "editInfo");
			reuqestMap.put("id", "Info表自增ID");
			reuqestMap.put("doctor_id", uid);
			reuqestMap.put("img_card", "工作牌照地址");
			reuqestMap.put("img_zgz", "资格证图片地址");
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
					Log.i("imgUrl返回结果 ----", "" + result);
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
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				ToastUtil.showToast(mContext, message);
				AuthenticationActivity.this.finish();
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 添加照片dialog
	private void addPicDialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(this, R.style.home_dialog);
		dialog.setContentView(R.layout.addpic_dialog);

		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.BOTTOM);
		paizhao_tv = (TextView) dialogWindow.findViewById(R.id.paizhao_tv);
		xiangce_tv = (TextView) dialogWindow.findViewById(R.id.xiangce_tv);
		cancel_tv = (TextView) dialogWindow.findViewById(R.id.cancel_tv);
		// 类型码
		paizhao_tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				paizhao_tv.setTextColor(getResources().getColor(R.color.line_orange));
				dialog.dismiss();

				Uri imageUri = null;
				String fileName = null;
				Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (crop) {
					REQUEST_CODE = CROP;
					// 删除上一次截图的临时文件
					SharedPreferences sharedPreferences = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE);
					ImageSelectTools.deletePhotoAtPathAndName(
							Environment.getExternalStorageDirectory().getAbsolutePath() + "/gsearch/",
							sharedPreferences.getString("tempName", ""));

					// 保存本次截图临时文件名字
					if (listPath.size() < 9) {
						strImgPath = Environment.getExternalStorageDirectory().toString() + "/gsearch/";// 存放照片的文件夹
						fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
						strImgPath = strImgPath + fileName;// 该照片的绝对路径
						Editor editor = sharedPreferences.edit();
						editor.putString("tempName", fileName);
						editor.commit();
					}
				} else {
					REQUEST_CODE = TAKE_PICTURE;
					// fileName = "image.jpg";
					if (listPath.size() < 9) {
						strImgPath = Environment.getExternalStorageDirectory().toString() + "/gsearch/";// 存放照片的文件夹
						fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";// 照片命名
						File out = new File(strImgPath);
						if (!out.exists()) {
							out.mkdirs();
						}
						out = new File(strImgPath, fileName);
						strImgPath = strImgPath + fileName;// 该照片的绝对路径
						imageUri = Uri.fromFile(out);
					} else {
						ToastUtil.showToast(mContext, "只能上传 9 张照片");
						return;
					}
				}
				imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/gsearch/", fileName));
				// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent, REQUEST_CODE);
			}
		});
		xiangce_tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				xiangce_tv.setTextColor(getResources().getColor(R.color.line_orange));
				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				if (crop) {
					if (listPath.size() < 9) {
						REQUEST_CODE = CROP;
					} else {
						ToastUtil.showToast(mContext, "只能上传 9 张照片");
						return;
					}
				} else {
					if (listPath.size() < 9) {
						REQUEST_CODE = CHOOSE_PICTURE;
					} else {
						ToastUtil.showToast(mContext, "只能上传 9 张照片");
						return;
					}
				}
				openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(openAlbumIntent, REQUEST_CODE);

				dialog.dismiss();
			}
		});
		cancel_tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancel_tv.setTextColor(getResources().getColor(R.color.line_orange));
				dialog.dismiss();
			}
		});
		// lp.x = 100; // 新位置X坐标
		// lp.y = Gravity.BOTTOM; // 新位置Y坐标
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		// lp.alpha = 0.7f; // 透明度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// getImageView
	private void getImageView(String path, String from) {
		int j = i++;
		final View view = getLayoutInflater().inflate(R.layout.camera_item, null);
		final ImageView imageView = (ImageView) view.findViewById(R.id.mycamera_item_image);
		final ImageView button = (ImageView) view.findViewById(R.id.mycamera_item_delete);
		if (from.equals("local")) {
			try {
				imageView.setImageBitmap(getImageBitmap(path));
				button.setTag(j);// 给按钮设置一个tag，主要为listPath的下标所用
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				URL myFileUrl = null;
				Bitmap bitmap = null;
				myFileUrl = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
				imageView.setImageBitmap(bitmap);
				button.setTag(j);// 给按钮设置一个tag，主要为listPath的下标所用

				tv_tittle.setText("我的认证");
				tv_hint.setVisibility(View.GONE);
				tv_hint2.setVisibility(View.GONE);
				tv_my_tittle.setVisibility(View.VISIBLE);
				tv_submit.setText("重新上传");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				linear_images.removeView(view);
				int k = Integer.parseInt(button.getTag().toString());
				// 这里处理是为了防止下标越界
				listPath.set(k, "NOIMAGE");// 删除一个view（图片），就将listPath中对应的下标改一下值
				listPath.remove(k);
				urlsPath.remove(k);
				// File file = new File(button.getTag().toString());
				// if (file.exists()) {
				// file.delete();
				// }
			}
		});

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				linear_images.addView(view);
			}
		});
		listPath.add(path);
	}

	// 根据路径获取图片
	private Bitmap getImageBitmap(String path) throws FileNotFoundException, IOException {
		Bitmap bmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = ImageTools.computeSampleSize(opts, -1, 150 * 150);// 得到缩略图
		opts.inJustDecodeBounds = false;
		try {
			bmp = BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError e) {
		}
		return bmp;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				// 将保存在本地的图片取出并缩小后显示在界面上
				// picPath = Environment.getExternalStorageDirectory() +
				// "/image.jpg";
				Bitmap bitmap = BitmapFactory.decodeFile(strImgPath);
				Bitmap newBitmap = ImageSelectTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE,
						bitmap.getHeight() / SCALE);
				// 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
				savePic(newBitmap);
				bitmap.recycle();

				// 将处理过的图片显示在界面上，并保存到本地
				// add_image.setImageBitmap(newBitmap);
				// ImageSelectTools.savePhotoToSDCard(newBitmap, Environment
				// .getExternalStorageDirectory().getAbsolutePath(),
				// String.valueOf(System.currentTimeMillis()));
				// uploadpic(strImgPath);
				break;

			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				try {
					// 使用ContentProvider通过URI获取原始图片
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
					if (photo != null) {
						// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						Bitmap smallBitmap = ImageSelectTools.zoomBitmap(photo, photo.getWidth() / SCALE,
								photo.getHeight() / SCALE);
						// 释放原始图片占用的内存，防止out of memory异常发生
						savePic(smallBitmap);
						photo.recycle();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// try {
				// String[] pojo = {MediaStore.Images.Media.DATA};
				// Cursor cursor = managedQuery(originalUri, pojo, null,
				// null, null);
				// if (cursor != null) {
				// ContentResolver cr = this.getContentResolver();
				// int colunm_index = cursor
				// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// cursor.moveToFirst();
				// String path = cursor.getString(colunm_index);
				// /***
				// *
				// 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，这样的话，我们判断文件的后缀名
				// * 如果是图片格式的话，那么才可以
				// */
				// if (path.endsWith("jpg") || path.endsWith("png")) {
				// strImgPath = path;
				// uploadpic(strImgPath);
				// // Bitmap bitmap =
				// // BitmapFactory.decodeStream(cr.openInputStream(uri));
				// } else {
				// alert();
				// }
				// } else {
				// alert();
				// }
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				break;

			case CROP:
				Uri uri = null;
				if (data != null) {
					uri = data.getData();
					System.out.println("Data");
				} else {
					System.out.println("File");
					String fileName = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE).getString("tempName",
							"");
					uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/gsearch/", fileName));
				}
				cropImage(uri, 480, 300, CROP_PICTURE);

				break;

			case CROP_PICTURE:
				Bitmap photo = null;
				Uri photoUri = data.getData();
				if (photoUri != null) {
					photo = BitmapFactory.decodeFile(photoUri.getPath());
					picPath = photoUri.getPath();
					strImgPath = picPath;
					uploadpic(strImgPath);
				}
				if (photo == null) {
					Bundle extra = data.getExtras();
					if (extra != null) {
						photo = (Bitmap) extra.get("data");
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
						// 保存剪裁照片生成一个新路径并上传
						savePic(photo);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	// 截取图片
	public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, requestCode);
	}

	private void alert() {
		Dialog dialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("您选择的不是有效的图片")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						picPath = null;
					}
				}).create();
		dialog.show();
	}

	// 裁剪后的图片生成一个路径
	public void savePic(Bitmap bitmap) {
		// 使用此流读取
		baos = new ByteArrayOutputStream();
		// 第二个参数影响的是图片的质量，但是图片的宽度与高度是不会受影响滴
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		// 这个函数能够设定图片的宽度与高度
		// Bitmap map = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
		// 把数据转为为字节数组
		byte[] byteArray = baos.toByteArray();
		String imgPath = "", fileName = "";
		try {
			imgPath = Environment.getExternalStorageDirectory().toString() + "/gsearch/";// 存放照片的文件夹
			fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
			fos = new FileOutputStream(imgPath + fileName);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				baos.close();
				fos.close();
				fos.close();
				System.out.println("croppic===" + imgPath + fileName);
				strImgPath = imgPath + fileName;
				uploadpic(strImgPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(exitReceiver);
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		Utils.showDialog(mContext, "尚未上传资格证，确认离开吗？", "exit");
		hideKeyboard();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			hideKeyboard();
		}
		return super.onTouchEvent(event);
	}

	private void hideKeyboard() {
		if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}
}

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.AbstractWheelTextAdapter;
import com.owen.pDoctor.adapter.MorAdapter;
import com.owen.pDoctor.adapter.NonAdapter;
import com.owen.pDoctor.model.MorBean;
import com.owen.pDoctor.model.NonBean;
import com.owen.pDoctor.model.TimeChooseBean;
import com.owen.pDoctor.network.FileUpload;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ImageSelectTools;
import com.owen.pDoctor.util.ImageTools;
import com.owen.pDoctor.util.ListComparator;
import com.owen.pDoctor.util.OnWheelChangedListener;
import com.owen.pDoctor.util.OnWheelScrollListener;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.owen.pDoctor.view.MyGridView;
import com.owen.pDoctor.view.MyListView;
import com.owen.pDoctor.view.WheelView;

/**
 * ClassName：PersonalSettingActivity Description：个人设置页面 Author ： zq Date
 * ：2015-7-12 下午10:32:33
 */
public class PersonalSettingActivity extends BaseActivity implements
		OnClickListener, View.OnTouchListener {

	private ImageView im_check_nan, im_check_nv;

	private TextView tv_sex, tv_zhicheng, tv_keshi;

	private EditText et_name, et_birthday, et_docter_instruction;

	private LinearLayout back_btn, ll_wenzhen_time, ll_zhichen, ll_man,
			ll_womon, ll_week;

	private RelativeLayout rl_keshi;

	private Dialog dialog;

	private TextView tv_save, tv_cancel, tv_sure, tv_diliver, paizhao_tv,
			xiangce_tv, cancel_tv;

	private WheelView wheel;

	private boolean scrolling = false;

	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	private static final int CROP = 2;
	private static final int CROP_PICTURE = 3;
	private static final int SCALE = 5;// 照片缩小比例
	final boolean crop = false;
	int REQUEST_CODE;

	private static final int CHOOSE = 100;// 照片缩小比例

	private List<String> listPath = new ArrayList<String>();// 存放路径的list

	private List<String> urlsPath = new ArrayList<String>();// 存放返回imgUrl的list

	private String imgUrl = "";

	private LinearLayout linear_images;

	private String strImgPath = "";// 照片文件绝对路径

	private String uploadcode = null;

	private int i = 0; // 删除按钮tag值，从0开始

	private String picPath;

	private MyGridView gv_shangwu, gv_xiawu, gv_time;

	private MyListView lv_doctors;

	private String check_time = "", realname, hospital_id, keshi_id, position_id, sex, headimgurl,
			birth_date, desc, work_time, keshi, address, zhicheng;

	private MorAdapter moradapter;

	private NonAdapter nonadapter;

	private TimeAdapter timeadapter;

	private DoctorsAdapter doctorsadapter;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private Intent intent;

	private String uid, loginName, userName, message, code;

	private ArrayList<TimeChooseBean> timelist = new ArrayList<TimeChooseBean>();

	private TimeChooseBean tbean = new TimeChooseBean();

	private MorBean morbean = new MorBean();

	private NonBean nonbean = new NonBean();

	private ArrayList<MorBean> morlist = new ArrayList<MorBean>();

	private ArrayList<NonBean> nonlist = new ArrayList<NonBean>();

	private String[] mornings = { "周一", "周二", "周三", "周四", "周五" };

	private String[] doctors = { "医师", "主管医师", "副主任医师", "主任医师" };

	private List<Integer> listId;
	private List<Integer> listmor = new ArrayList<Integer>();
	private List<Integer> listnon = new ArrayList<Integer>();
	private ListComparator lc = new ListComparator();
	
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	private ByteArrayOutputStream baos;
	
	private String uploadImg = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_setting);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		loginName = sp.getString("loginName", "");
		userName = sp.getString("userName", "");
		realname = sp.getString("realname", "");
		hospital_id = sp.getString("hospital_id", "");
		keshi_id = sp.getString("keshi_id", "");
		position_id = sp.getString("position_id", "");

		initview();
		setListener();
		getdata();
		getBaseInfo();
		getDoctorInfo();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		tv_save = (TextView) findViewById(R.id.tv_save);
		linear_images = (LinearLayout) findViewById(R.id.linearlayout_images);
		et_name = (EditText) findViewById(R.id.et_name);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		et_birthday = (EditText) findViewById(R.id.et_birthday);
		et_docter_instruction = (EditText) findViewById(R.id.et_docter_instruction);

		ll_wenzhen_time = (LinearLayout) findViewById(R.id.ll_wenzhen_time);
		rl_keshi = (RelativeLayout) findViewById(R.id.rl_keshi);
		ll_zhichen = (LinearLayout) findViewById(R.id.ll_zhichen);
		tv_zhicheng = (TextView) findViewById(R.id.tv_zhicheng);
		tv_keshi = (TextView) findViewById(R.id.tv_keshi);

		gv_shangwu = (MyGridView) findViewById(R.id.gv_shangwu);
		gv_xiawu = (MyGridView) findViewById(R.id.gv_xiawu);

		// linear_images.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_username));
		if (!realname.equals("")) {
			et_name.setText(realname);
		} else {
			et_name.setText("请输入姓名");
		}
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		linear_images.setOnClickListener(this);
		et_birthday.setOnTouchListener(this);
		tv_save.setOnClickListener(this);
		tv_sex.setOnClickListener(this);
		ll_wenzhen_time.setOnClickListener(this);
		rl_keshi.setOnClickListener(this);
		ll_zhichen.setOnClickListener(this);
	}

	private void getdata() {
		// TODO Auto-generated method stub
		listId = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			tbean.setTime("上午");
			tbean.setCheck(false);
			timelist.add(tbean);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.tv_save:
			/* 修改后保存 */
			submit();
			break;

		case R.id.linearlayout_images:
			/* 上传头像 */
			addPicDialog();
			break;

		case R.id.tv_sex:
			/* 选择性别 */
			sexdialog();
			break;

		case R.id.ll_wenzhen_time:
			/* 选择问诊时间 */
			listId.clear();
			showdialog(1);
			break;

		case R.id.rl_keshi:
			/* 选择科室 */
			Intent intent = new Intent(this, KeshiActivity.class);
			intent.putExtra("from", "choose");
			startActivityForResult(intent, CHOOSE);
			break;

		case R.id.ll_zhichen:
			/* 选择职称 */
			zhichengDialog();
			break;
		default:
			break;
		}

	}

	// 选择性别
	private void sexdialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(this, R.style.home_dialog);
		dialog.setContentView(R.layout.sex_dialog);

		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.CENTER);
		im_check_nan = (ImageView) dialogWindow.findViewById(R.id.im_check_nan);
		im_check_nv = (ImageView) dialogWindow.findViewById(R.id.im_check_nv);
		ll_man = (LinearLayout) dialogWindow.findViewById(R.id.ll_man);
		ll_womon = (LinearLayout) dialogWindow.findViewById(R.id.ll_womon);
		ll_man.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				im_check_nan
						.setBackgroundResource(R.drawable.bg_register_checked);
				sex = "1";
				tv_sex.setText("男");
				dialog.dismiss();
			}
		});
		ll_womon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				im_check_nv
						.setBackgroundResource(R.drawable.bg_register_checked);
				sex = "2";
				tv_sex.setText("女");
				dialog.dismiss();
			}
		});
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 日期选择
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					PersonalSettingActivity.this);
			View view = View.inflate(PersonalSettingActivity.this,
					R.layout.date_time_dialog, null);
			final DatePicker datePicker = (DatePicker) view
					.findViewById(R.id.date_picker);
			builder.setView(view);

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH), null);

			if (v.getId() == R.id.et_birthday) {
				final int inType = et_birthday.getInputType();
				et_birthday.setInputType(InputType.TYPE_NULL);
				et_birthday.onTouchEvent(event);
				et_birthday.setInputType(inType);
				et_birthday.setSelection(et_birthday.getText().length());

				// builder.setTitle("选取生日日期");
				builder.setPositiveButton("确  定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								StringBuffer sb = new StringBuffer();
								sb.append(String.format("%d-%02d-%02d",
										datePicker.getYear(),
										datePicker.getMonth() + 1,
										datePicker.getDayOfMonth()));
								// sb.append(" ");
								et_birthday.setText(sb);
								dialog.cancel();
							}
						});
			}
			Dialog dialog = builder.create();
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		}
		return true;
	}

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
				paizhao_tv.setTextColor(getResources().getColor(
						R.color.line_orange));
				dialog.dismiss();

				Uri imageUri = null;
				String fileName = null;
				Intent openCameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				if (crop) {
					REQUEST_CODE = CROP;
					// 删除上一次截图的临时文件
					SharedPreferences sharedPreferences = getSharedPreferences(
							"temp", Context.MODE_WORLD_WRITEABLE);
					ImageSelectTools.deletePhotoAtPathAndName(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/gsearch/", sharedPreferences.getString(
							"tempName", ""));

					// 保存本次截图临时文件名字
					if (listPath.size() < 9) {
						strImgPath = Environment.getExternalStorageDirectory()
								.toString() + "/gsearch/";// 存放照片的文件夹
						fileName = String.valueOf(System.currentTimeMillis())
								+ ".jpg";
						strImgPath = strImgPath + fileName;// 该照片的绝对路径
						Editor editor = sharedPreferences.edit();
						editor.putString("tempName", fileName);
						editor.commit();
					}
				} else {
					REQUEST_CODE = TAKE_PICTURE;
					// fileName = "image.jpg";
					if (listPath.size() < 9) {
						strImgPath = Environment.getExternalStorageDirectory()
								.toString() + "/gsearch/";// 存放照片的文件夹
						fileName = new SimpleDateFormat("yyyyMMddHHmmss")
								.format(new Date()) + ".jpg";// 照片命名
						File out = new File(strImgPath);
						if (!out.exists()) {
							out.mkdirs();
						}
						out = new File(strImgPath, fileName);
						strImgPath = strImgPath + fileName;// 该照片的绝对路径
						imageUri = Uri.fromFile(out);
					} else {
						ToastUtil.showToast(PersonalSettingActivity.this,
								"只能上传 9 张照片");
						return;
					}
				}
				imageUri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory() + "/gsearch/", fileName));
				// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent, REQUEST_CODE);
			}
		});
		xiangce_tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				xiangce_tv.setTextColor(getResources().getColor(
						R.color.line_orange));
				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				if (crop) {
					if (listPath.size() < 9) {
						REQUEST_CODE = CROP;
					} else {
						ToastUtil.showToast(PersonalSettingActivity.this,
								"只能上传 9 张照片");
						return;
					}
				} else {
					if (listPath.size() < 9) {
						REQUEST_CODE = CHOOSE_PICTURE;
					} else {
						ToastUtil.showToast(PersonalSettingActivity.this,
								"只能上传 9 张照片");
						return;
					}
				}
				openAlbumIntent
						.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
				startActivityForResult(openAlbumIntent, REQUEST_CODE);

				dialog.dismiss();
			}
		});
		cancel_tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancel_tv.setTextColor(getResources().getColor(
						R.color.line_orange));
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

	// 加载图片
	private Runnable setbitmap = new Runnable() {
		@Override
		public void run() {
			// String[] cont = myBean.getImg().replaceAll("\\[([^\\]]*)\\]",
			// "$1").toString().replaceAll("\\\\", "").trim()
			// .split("\\,");
			// for (int i = 0; i < cont.length; i++) {
			// getImageView(cont[i].replace("\"", ""), "net"); // 显示图片
			// urlsPath.add(cont[i].replace("\"", ""));
			// }
			getImageView(headimgurl, "net"); // 显示图片
		}
	};

	// 上传图片，单张上传
//	private void uploadpic(String picPath) {
//		// TODO Auto-generated method stub
//		byte[] byteArray = baos.toByteArray();
//		String img = android.util.Base64.encodeToString(byteArray,
//				Base64.DEFAULT);
//		Map<String, String> param = new HashMap<String, String>();
//		param.put("uid", uid);
//		param.put("app", "doctor");
//		param.put("act", "editDocInfo");
//		param.put("doctor_id", uid);
//		param.put("head_img", img);
//		param.put("uuid", getUUid());
//		param.put("equipment", "iphone5");
//		param.put("token", EncryptionUtil.md5EncryptToString("jiankang2015"));
//		param.put("ver", getAppVersion());
//		Map<String, File> files = new HashMap<String, File>();
//		File file = new File(picPath);
//		FileInfo fileInfo = new FileInfo();
//		fileInfo.Name = getResources().getString(R.string.d);
//		fileInfo.Path = file.getAbsolutePath();
//		fileInfo.Size = file.length();
//		fileInfo.realyName = file.getName();
//		List<FileInfo> fileInfos = new ArrayList<FileInfo>();
//		fileInfos.add(fileInfo);
//		files.put(fileInfo.getRealyName(), file);
//		if (progressDialog == null) {
//			progressDialog = CustomProgressDialog.createDialog(this);
//			progressDialog.setMessage("上传图片中...");
//		}
//		progressDialog.show();
//		fileUpload(param, files, myHandler);
//	}

	private void fileUpload(final Map<String, String> param,
			final Map<String, File> files, final Handler handler) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				String url = Constants.SERVER_URL;
				String result = FileUpload.postUploadFile(url, param, files);
				Log.i("上传文件", "上传文件返回结果 : " + result);
				Message msg = new Message();
				if (null != result && result.length() > 0) {
					try {
						JSONObject Jsonresult = new JSONObject(result);
						uploadcode = Jsonresult.getString("result");
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
			if (msg.what == 1) {
				ToastUtil.showToast(PersonalSettingActivity.this, "上传图片成功！");
				getImageView(strImgPath, "local");// 显示图片
				urlsPath.add(Constants.SERVER_URL + imgUrl.substring(1));
			} else {
				ToastUtil.showToast(PersonalSettingActivity.this, "上传图片失败！");
			}
		};
	};

	// 选择问诊时间
	private void showdialog(int from) {
		// TODO Auto-generated method stub
		listmor.clear();
		listnon.clear();
		dialog = new Dialog(this, R.style.home_dialog);
		dialog.setContentView(R.layout.my_info_dialog);

		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.BOTTOM);
		tv_cancel = (TextView) dialogWindow.findViewById(R.id.tv_cancel);
		tv_sure = (TextView) dialogWindow.findViewById(R.id.tv_sure);
		ll_week = (LinearLayout) dialogWindow.findViewById(R.id.ll_week);
		gv_time = (MyGridView) dialogWindow.findViewById(R.id.gv_time);
		lv_doctors = (MyListView) dialogWindow.findViewById(R.id.lv_doctors);
		tv_diliver = (TextView) dialogWindow.findViewById(R.id.tv_diliver);
		if (from == 1) {
			gv_time.setVisibility(View.VISIBLE);
			ll_week.setVisibility(View.VISIBLE);
			tv_diliver.setVisibility(View.VISIBLE);
			lv_doctors.setVisibility(View.GONE);
			timeadapter = new TimeAdapter();
			gv_time.setAdapter(timeadapter);
		} else {
			lv_doctors.setVisibility(View.VISIBLE);
			tv_diliver.setVisibility(View.GONE);
			gv_time.setVisibility(View.GONE);
			ll_week.setVisibility(View.GONE);
		}
		tv_sure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				moradapter = new MorAdapter(PersonalSettingActivity.this,
						listmor);
				gv_shangwu.setAdapter(moradapter);
				nonadapter = new NonAdapter(PersonalSettingActivity.this,
						listnon);
				gv_xiawu.setAdapter(nonadapter);
				dialog.dismiss();
				Log.d("====listId====", "" + listId);
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

	// 选择职称
	private void zhichengDialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(this, R.style.home_dialog);
		dialog.setContentView(R.layout.wheel_dialog);

		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.BOTTOM);
		tv_cancel = (TextView) dialogWindow.findViewById(R.id.tv_cancel);
		tv_sure = (TextView) dialogWindow.findViewById(R.id.tv_sure);
		wheel = (WheelView) dialogWindow.findViewById(R.id.wheel);
		wheel.setVisibleItems(4);
		wheel.setViewAdapter(new DoctorsAdapter(this));
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
				}
			}
		});
		wheel.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
			}
		});
		wheel.setCurrentItem(1);

		tv_sure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tv_zhicheng.setText(doctors[wheel.getCurrentItem()]);
				dialog.dismiss();
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

	/**
	 * dialog选择上下午adapter
	 * 
	 */
	public class TimeAdapter extends BaseAdapter {

		public int getCount() {
			return timelist == null ? 0 : timelist.size();
		}

		public Object getItem(int arg0) {
			return timelist == null ? 0 : timelist.get(arg0);
		}

		public long getItemId(int arg0) {
			return timelist == null ? 0 : arg0;
		}

		public View getView(int position, View view, ViewGroup arg2) {
			final Hodler hodler;
			if (view == null) {
				hodler = new Hodler();
				view = (LinearLayout) LayoutInflater.from(
						PersonalSettingActivity.this).inflate(
						R.layout.my_info_griditem, null);
				hodler.item_cb = (CheckBox) view.findViewById(R.id.item_cb);
				hodler.items_name = (TextView) view
						.findViewById(R.id.items_name);
				view.setTag(hodler);
			} else {
				hodler = (Hodler) view.getTag();
			}

			if (position < 5) {
				hodler.items_name.setText("上午");
			} else {
				hodler.items_name.setText("下午");
			}
			view.setOnClickListener(new GvclickListener(position, hodler));
			return view;
		}

		class GvclickListener implements OnClickListener {
			int pos;
			Hodler holder;

			public GvclickListener(int position, Hodler hodler) {
				// TODO Auto-generated constructor stub
				this.pos = position;
				this.holder = hodler;
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listId.contains(pos)) {
					// 已经选中过 取消选中
					listId.remove(listId.indexOf(pos));
					if (pos < 5) {
						listmor.remove(listId.indexOf(pos));
					} else {
						listnon.remove(listId.indexOf(pos));
					}
					holder.item_cb.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.choose_none));
				} else {
					// 没有选中过
					listId.add(pos);
					if (pos < 5) {
						listmor.add(pos);
					} else {
						listnon.add(pos);
					}
					holder.item_cb.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.choose_yes));
				}
				// 排序 很重要 防止删除的时候 出现不必要的异常
				Collections.sort(listId, lc);
				Collections.sort(listmor, lc);
				Collections.sort(listnon, lc);
				Log.d("====listId====", "" + listmor + listnon);
			}
		}

		public class Hodler {
			CheckBox item_cb;
			TextView items_name;
		}
	}

	/**
	 * dialog选择医师adapter
	 * 
	 */
	private class DoctorsAdapter extends AbstractWheelTextAdapter {
		// Countries names
		protected DoctorsAdapter(Context context) {
			super(context, R.layout.my_info_griditem2, NO_RESOURCE);
			setItemTextResource(R.id.items_name);
		}

		// Countries flags
		// private int flags = R.drawable.tem_unit_dialog;

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			// ImageView img = (ImageView) view.findViewById(R.id.tempImag);
			// img.setImageResource(flags);
			return view;
		}

		@Override
		public int getItemsCount() {
			return doctors.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return doctors[index] + "";
		}
	}

	// 获取基本信息
	private void getBaseInfo() {
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("加载中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "doctor");
			reuqestMap.put("act", "index");
			reuqestMap.put("mobile", loginName);
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token",
					EncryptionUtil.md5EncryptToString("jiankang2015"));
			reuqestMap.put("ver", getAppVersion());
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("获取基本信息返回结果  : ----", result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							realname = data.getString("realname");
							headimgurl = data.getString("headimgurl");
							sex = data.getString("sex");
							birth_date = data.getString("birth_date");
							desc = data.getString("desc");
							work_time = data.getString("work_time");
							keshi = data.getString("keshi");
							address = data.getString("position");

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
		// overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (!realname.equals("")) {
					et_name.setText(realname);
				} else {
					et_name.setText("请输入姓名");
				}
				if (sex.equals("1")) {
					tv_sex.setText("男");
				} else {
					tv_sex.setText("女");
				}
				et_birthday.setText(birth_date);
				tv_keshi.setText(keshi);
				et_docter_instruction.setText(desc);
				linear_images.setBackgroundColor(getResources().getColor(R.color.baise));
				new Thread(setbitmap).start();
			} else {
				ToastUtil.showToast(PersonalSettingActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 获取医生职称信息
	private void getDoctorInfo() {
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "position");
			reuqestMap.put("act", "index");
			reuqestMap.put("position_id", position_id); // 职称编号
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token",
					EncryptionUtil.md5EncryptToString("jiankang2015"));
			reuqestMap.put("ver", getAppVersion());
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("获取医生职称信息返回结果  : ----", result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							zhicheng = data.getString("name");
							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						dhandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		// overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
	}

	Handler dhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				tv_zhicheng.setText(zhicheng);
			} else {
				ToastUtil.showToast(PersonalSettingActivity.this, message);
			}
		}
	};

	// 上传图片
	public void uploadpic(String picPath) {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("上传中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			byte[] byteArray = baos.toByteArray();
			String img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
			reuqestMap.put("uid", uid);
			reuqestMap.put("app", "doctor");
			reuqestMap.put("act", "editDocInfo");
			reuqestMap.put("doctor_id", uid);
			reuqestMap.put("head_img", img);
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
					Log.i("上传图片返回结果 ----", "" + result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						pichandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}

	Handler pichandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(PersonalSettingActivity.this, message);
			} else {
				ToastUtil.showToast(PersonalSettingActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 保存资料
	public void submit() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("保存中，请稍后...");
			}
			progressDialog.show();

			if (baos != null) {
				byte[] byteArray = baos.toByteArray();
				uploadImg = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
			}
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "doctor");
			reuqestMap.put("act", "editDocInfo");
			reuqestMap.put("doctor_id", uid);
			reuqestMap.put("realname", et_name.getText().toString());
			reuqestMap.put("sex", tv_sex.getText().toString());
			reuqestMap.put("birth_date", et_birthday.getText().toString());
			reuqestMap.put("duty_time", "");
			reuqestMap.put("hospital_id", "");
			reuqestMap.put("keshi_id", "");
			reuqestMap.put("postion_id", "");
			reuqestMap.put("description", et_docter_instruction.getText().toString());
			reuqestMap.put("head_img", uploadImg); // 图片字节流
			reuqestMap.put("uuid", getUUid());
			reuqestMap.put("equipment", "iphone5");
			reuqestMap.put("token",	EncryptionUtil.md5EncryptToString("jiankang2015"));
			reuqestMap.put("ver", getAppVersion());
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("保存返回结果 ----", "" + result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						savehandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}

	Handler savehandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(PersonalSettingActivity.this, message);
			} else {
				ToastUtil.showToast(PersonalSettingActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// getImageView
	private void getImageView(String path, String from) {
		int j = i++;
		final View view = getLayoutInflater().inflate(R.layout.camera_item,
				null);
		final ImageView imageView = (ImageView) view
				.findViewById(R.id.mycamera_item_image);
		final ImageView button = (ImageView) view
				.findViewById(R.id.mycamera_item_delete);
		button.setVisibility(View.GONE);
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
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
				imageView.setImageBitmap(bitmap);
				button.setTag(j);// 给按钮设置一个tag，主要为listPath的下标所用
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
	private Bitmap getImageBitmap(String path) throws FileNotFoundException,
			IOException {
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
				Bitmap newBitmap = ImageSelectTools.zoomBitmap(bitmap,
						bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
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
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
							originalUri);
					if (photo != null) {
						// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						Bitmap smallBitmap = ImageSelectTools.zoomBitmap(photo,
								photo.getWidth() / SCALE, photo.getHeight()
										/ SCALE);
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
					String fileName = getSharedPreferences("temp",
							Context.MODE_WORLD_WRITEABLE).getString("tempName",
							"");
					uri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory() + "/gsearch/",
							fileName));
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

			case CHOOSE:
				keshi = data.getStringExtra("hospital") + " "
						+ data.getStringExtra("keshi");
				tv_keshi.setText(keshi);
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
			imgPath = Environment.getExternalStorageDirectory().toString()
					+ "/gsearch/";// 存放照片的文件夹
			fileName = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date()) + ".jpg";
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
	public void onDestroy() {
		super.onDestroy();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
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

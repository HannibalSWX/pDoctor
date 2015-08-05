package com.owen.pDoctor.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owen.pDoctor.R;
import com.owen.pDoctor.activity.AuthenticationActivity;

/**
 * @Author zq
 * @Date 2015-1-22
 * @version 1.0
 * @Desc 工具类
 */

public class Utils {

	public static ProgressDialog loadingDialog;

	private static Dialog dialog;
	
	public static SimpleDateFormat DefaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat DefaultDate = new SimpleDateFormat("MM-dd");
	public static SimpleDateFormat DefaultDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat DefaultTimeSpanFormat = new SimpleDateFormat("HH:mm:ss");
	public static SimpleDateFormat DefaultTimeSpanFormat1 = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat DefaultYearMonthFormat = new SimpleDateFormat("yyyy-MM");
	public static SimpleDateFormat DefaultTimestampFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	public static SimpleDateFormat DefaultDateMinuteFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * 格式化一个时间
	 * 
	 */
	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}

	public static String formatDateTime(Date dateTime, boolean justDate) {
		if (dateTime == null)
			return null;
		if (justDate)
			return DefaultDateFormat.format(dateTime);
		return DefaultDateTimeFormat.format(dateTime);
	}

	public static String formatDateTime(Date dateTime, boolean justDate, boolean noNULL) {
		if (dateTime == null && noNULL)
			return "";
		if (dateTime == null)
			return null;
		if (justDate)
			return DefaultDateFormat.format(dateTime);
		return DefaultDateTimeFormat.format(dateTime);
	}

	public static String formatDateMinute(Date dateTime) {
		if (dateTime == null)
			return null;
		return DefaultDateMinuteFormat.format(dateTime);
	}

	public static String formatTimestamp(Date dateTime) {
		if (dateTime == null)
			return null;
		return DefaultTimestampFormat.format(dateTime);
	}

	public static String formatTimeSpan(Date ts) {
		if (ts == null)
			return null;
		return DefaultTimeSpanFormat.format(ts);
	}

	public static String formatTimeSpan1(Date ts) {
		if (ts == null)
			return null;
		return DefaultTimeSpanFormat1.format(ts);
	}

	public static String formatDate(Date ts) {
		if (ts == null)
			return null;
		return DefaultDate.format(ts);
	}

	/*
	 * 判断suoyou网络连接是否已开true 已打开 false 未打开
	 */
	public static boolean isNetConn(Context context) {
		boolean bisConnFlag = false;
		if (context != null) {
			ConnectivityManager conManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo network = conManager.getActiveNetworkInfo();
			if (network != null) {
				bisConnFlag = network.isAvailable();
			}
		}
		return bisConnFlag;
	}

	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3、5、8，其他位置的可以为0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	// 账号判断正则表达式
	public static boolean isAccount(String account) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(account);
		return m.matches();
	}

	// 邮箱判断正则表达式
	public static boolean isEmail(String account) {
		Pattern pattern = Pattern.compile(
				"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher("a@aa.com");
		System.out.println(matcher.matches());
		return matcher.matches();
	}

	/***
	 * 将textview中的字符全角化。
	 * 即将所有的数字、字母及标点全部转为全角字符，使它们与汉字同占两个字节，这样就可以避免由于占位导致的排版混乱问题了
	 *
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * * 去除特殊字符或将所有中文标号替换为英文标号。
	 * 利用正则表达式将所有特殊字符过滤，或利用replaceAll（）将中文标号替换为英文标号。则转化之后，则可解决排版混乱问题
	 * 
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context, String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息

		loadingDialog = new ProgressDialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		loadingDialog.setCancelable(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
		return loadingDialog;
	}

	// dialog
	public static void showDialog(final Context mContext, String content, final String type) {
		// TODO Auto-generated method stub
		dialog = new Dialog(mContext, R.style.home_dialog);
		dialog.setContentView(R.layout.alert_dialog_exit);
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.CENTER_VERTICAL);
		TextView content_tv = (TextView) dialogWindow.findViewById(R.id.content_tv);
		LinearLayout ll_cancel = (LinearLayout) dialogWindow.findViewById(R.id.ll_cancel);
		LinearLayout ll_sure = (LinearLayout) dialogWindow.findViewById(R.id.ll_sure);
		content_tv.setText(content);
		ll_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ll_sure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent mIntent = new Intent(Constants.SEND_BRAODCAST);
				mIntent.putExtra("type", type);
				// 发送广播
				mContext.sendBroadcast(mIntent);

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

	// hintDialog
	public static void hintDialog(final Context mContext, String tittle, String content, String cancel,
			String sure) {
		// TODO Auto-generated method stub
		dialog = new Dialog(mContext, R.style.home_dialog);
		dialog.setContentView(R.layout.alert_dialog_hint);
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.CENTER_VERTICAL);
		TextView tittle_tv = (TextView) dialogWindow.findViewById(R.id.tittle_tv);
		TextView content_tv = (TextView) dialogWindow.findViewById(R.id.content_tv);
		TextView cancel_tv = (TextView) dialogWindow.findViewById(R.id.cancel_tv);
		TextView sure_tv = (TextView) dialogWindow.findViewById(R.id.sure_tv);
		LinearLayout ll_cancel = (LinearLayout) dialogWindow.findViewById(R.id.ll_cancel);
		LinearLayout ll_sure = (LinearLayout) dialogWindow.findViewById(R.id.ll_sure);
		tittle_tv.setText(tittle);
		content_tv.setText(content);
		cancel_tv.setText(cancel);
		sure_tv.setText(sure);
		ll_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ll_sure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AuthenticationActivity.authActivity.submit();
				dialog.dismiss();
			}
		});
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 呼叫dialog
	public static void dialdialog(final Context mContext, final String phone_no) {
		// TODO Auto-generated method stub
		dialog = new Dialog(mContext, R.style.home_dialog);
		dialog.setContentView(R.layout.alert_dialog);
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.CENTER_VERTICAL);
		TextView phone_tv = (TextView) dialogWindow.findViewById(R.id.phone_tv);
		LinearLayout ll_cancel = (LinearLayout) dialogWindow.findViewById(R.id.ll_cancel);
		LinearLayout ll_call = (LinearLayout) dialogWindow.findViewById(R.id.ll_call);
		if (Utils.isMobileNO(phone_no)) {
			phone_tv.setText(
					phone_no.substring(0, 3) + "-" + phone_no.substring(3, 7) + "-" + phone_no.substring(7, 11));
		} else {
			phone_tv.setText(phone_no);
		}
		TextPaint tp = phone_tv.getPaint();
		tp.setFakeBoldText(true);
		ll_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ll_call.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				if (Utils.isMobileNO(phone_no)) {
					// 获取电话号码
					String mobile = "" + phone_no;
					// 参数1：动作。在这指打电话
					// 参数2：提供给应用的数据。在这指电话号
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
					// 将意图传给操作系统
					// startActivity方法专门将意图传给操作系统
					mContext.startActivity(intent);
				} else {
					ToastUtil.showToast(mContext, "号码有误");
				}
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

	public static void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}
				if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmap(String imageUri) {
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
            bitmap = null;
		}
		return bitmap;
	}
	
	public static final void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
}

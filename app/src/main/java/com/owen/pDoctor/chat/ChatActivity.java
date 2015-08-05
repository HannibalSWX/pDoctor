package com.owen.pDoctor.chat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.activity.MainActivity;
import com.owen.pDoctor.activity.MyPatientActivity;
import com.owen.pDoctor.model.HistoryOfMessagesBean;
import com.owen.pDoctor.model.MessagesBean;
import com.owen.pDoctor.model.MyGroupChildBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

/**
 * @Title:ChatActivity.java
 * @Description:应用ChatActivity.java类
 * @Author:owen
 * @Since:2015年7月22日
 * @Version:
 */
public class ChatActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private Button mBtnSend;
	private EditText mEditTextContent;
	private ListView mListView;
	private RelativeLayout mBottom, ll_picchoose;
	private LinearLayout ll_photo, ll_camera, ll_suifang, ll_huanjiao;
	private TextView mBtnRcd;
	private ChatMsgViewAdapter mAdapter;

	// 语音
	private ImageView chatting_mode_btn, volume;
	private boolean btn_voice = false;
	private LinearLayout del_re;
	private int flag = 1;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding, voice_rcd_hint_tooshort;
	private View rcChat_popup;
	private Handler mHandler = new Handler();
	private boolean isShosrt = false;
	private ImageView img1, sc_img1, btn_photo, btn_face;
	private String voiceName;
	private long startVoiceT, endVoiceT;
	private SoundMeter mSensor;
	// private ImageButton btn_face;

	public static final int SHOW_ALL_PICTURE = 0x14;// 查看图片
	public static final int SHOW_PICTURE_RESULT = 0x15;// 查看图片返回
	public static final int CLOSE_INPUT = 0x01;// 关闭软键盘
	public static Handler handlerInput;// 用于软键盘+
	// private String photoName;

	private MyCount mc;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private String username, uid, ids, message, code, servicephone;
	
	private MyGroupChildBean myChildBean;
	
	private String firstId = "0";
	
	private MessagesBean msgBean;
	
	private List<HistoryOfMessagesBean> hisList = new ArrayList<HistoryOfMessagesBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.chat);
		// 设置启动不弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		username = sp.getString("userName", "");
		
		Intent intent = getIntent();
		if ((MyGroupChildBean)intent.getSerializableExtra("childItem") != null) {
			myChildBean = (MyGroupChildBean)intent.getSerializableExtra("childItem");
		} else {
			msgBean = (MessagesBean)intent.getSerializableExtra("msgItem");
		}
		
		initView();
		initHistoryData(); // 获取历史聊天记录
		mc = new MyCount(10000, 1000);
		mc.start();
	}

	public void initView() {
		btn_face = (ImageView) findViewById(R.id.btn_face);
		btn_photo = (ImageView) findViewById(R.id.btn_photo);
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		chatting_mode_btn = (ImageView) findViewById(R.id.ivPopUp);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		del_re = (LinearLayout) this.findViewById(R.id.del_re);
		volume = (ImageView) this.findViewById(R.id.volume);
		img1 = (ImageView) this.findViewById(R.id.img1);
		sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
		rcChat_popup = this.findViewById(R.id.rcChat_popup);
		ll_picchoose = (RelativeLayout) findViewById(R.id.ll_picchoose);
		ll_photo = (LinearLayout) this.findViewById(R.id.ll_photo);
		ll_camera = (LinearLayout) this.findViewById(R.id.ll_camera);
		ll_suifang = (LinearLayout) this.findViewById(R.id.ll_suifang);
		ll_huanjiao = (LinearLayout) this.findViewById(R.id.ll_huanjiao);
		ll_photo.setOnClickListener(this);
		ll_camera.setOnClickListener(this);
		ll_suifang.setOnClickListener(this);
		ll_huanjiao.setOnClickListener(this);
		voice_rcd_hint_rcding = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_tooshort);
		mSensor = new SoundMeter();
		mBtnSend.setOnClickListener(this);
		btn_photo.setOnClickListener(this);
		btn_face.setOnClickListener(this);
		mEditTextContent.setOnClickListener(this);
		// btn_face.setOnClickListener(this);
		chatting_mode_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btn_voice) {
					mBtnRcd.setVisibility(View.GONE);
					mBottom.setVisibility(View.VISIBLE);
					btn_voice = false;
					chatting_mode_btn.setImageResource(R.drawable.chatting_setmode_msg_btn);
					((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
				} else {
					mBtnRcd.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.GONE);
					chatting_mode_btn.setImageResource(R.drawable.chatting_setmode_voice_btn);
					btn_voice = true;
					((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
				}
				if (ll_picchoose.getVisibility() == View.VISIBLE) {
					ll_picchoose.setVisibility(View.GONE);
				}
			}
		});
		mBtnRcd.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		mEditTextContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String text = mEditTextContent.getText().toString();
				if (!(text.length() > 0)) {
					// Toast.makeText(MainActivity.this,mEditTextContent.getText()+"请输入聊天内容",
					// 2).show();
					btn_photo.setVisibility(View.VISIBLE);
					btn_face.setVisibility(View.VISIBLE);
					mBtnSend.setVisibility(View.GONE);
				} else {
					// Toast.makeText(MainActivity.this,"输入的聊天内容为："+mEditTextContent.getText(),
					// 2).show();
					btn_photo.setVisibility(View.GONE);
					btn_face.setVisibility(View.GONE);
					mBtnSend.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			}
		});
	}

	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);

		}

		// 倒计时结束
		@Override
		public void onFinish() {
			if (hisList != null && hisList.size() > 0) {
				hisList.clear();
			}
			initHistoryData(); // 获取历史聊天记录, 每隔10秒取一次
		}

		// 倒计时过程
		@Override
		public void onTick(long millisUntilFinished) {
			// getBtn.setText(millisUntilFinished / 1000 + " 秒");
		}
	}

	// 按下语音录制
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(this, "无内存卡，请安装..", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (btn_voice) {
			System.out.println("1");
			int[] location = new int[2];
			mBtnRcd.getLocationInWindow(location);
			int btn_rc_Y = location[1];
			int btn_rc_X = location[0];
			int[] del_location = new int[2];
			del_re.getLocationInWindow(del_location);
			int del_Y = del_location[1];
			int del_x = del_location[0];
			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				if (!Environment.getExternalStorageDirectory().exists()) {
					Toast.makeText(this, "无内存卡，请安装..", Toast.LENGTH_SHORT).show();
					return false;
				}
				System.out.println("2");
				if (event.getRawY() > btn_rc_Y && event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
					System.out.println("3");
					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
					rcChat_popup.setVisibility(View.VISIBLE);
					voice_rcd_hint_loading.setVisibility(View.VISIBLE);
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.GONE);
					mHandler.postDelayed(new Runnable() {
						public void run() {
							if (!isShosrt) {
								voice_rcd_hint_loading.setVisibility(View.GONE);
								voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
							}
						}
					}, 300);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					startVoiceT = SystemClock.currentThreadTimeMillis();
					voiceName = startVoiceT + ".amr";
					start(voiceName);
					flag = 2;
				}
				return true; // ACTION_DOWN是touch event的开始，你要在ACTION_DOWN中return
								// true才行
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {
				System.out.println("4");
				mBtnRcd.setBackgroundResource(R.drawable.btn_bg_gray2);
				if (event.getRawY() >= del_Y && event.getRawY() <= del_Y + del_re.getHeight() && event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					rcChat_popup.setVisibility(View.GONE);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					stop();
					flag = 1;
					File file = new File(
							android.os.Environment.getExternalStorageDirectory() + "/pDoctor/" + voiceName);
					if (file.exists()) {
						file.delete();
					}
				} else {
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					stop();
					endVoiceT = SystemClock.currentThreadTimeMillis();
					flag = 1;
					int time = (int) ((endVoiceT - startVoiceT) / 50);
					if (time < 1) {
						isShosrt = true;
						voice_rcd_hint_loading.setVisibility(View.GONE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								voice_rcd_hint_tooshort.setVisibility(View.GONE);
								rcChat_popup.setVisibility(View.GONE);
								isShosrt = false;
							}
						}, 500);
						return false;
					}
					HistoryOfMessagesBean entity = new HistoryOfMessagesBean();
					entity.setSend_time(getDate());
					entity.setNickname("古月哥欠");
					entity.setTo_user_type("1");
//					entity.setTime(time + "\"");
					entity.setText(voiceName);
					hisList.add(entity);
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(mListView.getCount() - 1);
					rcChat_popup.setVisibility(View.GONE);
				}
			}
			if (event.getRawY() < btn_rc_Y) {// 手势按下的位置不在语音录制按钮的范围内
				System.out.println("5");
				Animation mLitteAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc);
				Animation mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc2);
				img1.setVisibility(View.GONE);
				del_re.setVisibility(View.VISIBLE);
				del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
				if (event.getRawY() >= del_Y && event.getRawY() <= del_Y + del_re.getHeight() && event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
					sc_img1.startAnimation(mLitteAnimation);
					sc_img1.startAnimation(mBigAnimation);
				}
			} else {

				img1.setVisibility(View.VISIBLE);
				del_re.setVisibility(View.GONE);
				del_re.setBackgroundResource(0);
			}
		}
		return super.onTouchEvent(event);
	}

	private void start(String name) {
		mSensor.start(name);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	// 历史聊天消息
	private void initHistoryData() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "message");
			reuqestMap.put("act", "getNewMessage");
			reuqestMap.put("docId", uid); // 医生编号
			if (msgBean != null) {
				reuqestMap.put("userId", msgBean.getMember_id()); // 用户编号
			} else {
				reuqestMap.put("userId", myChildBean.getMember_id()); // 用户编号
			}
			reuqestMap.put("firstId", firstId); // 分页最后一个值
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
						Log.i("历史聊天消息 ----", "" + result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							for (int i = 1; i < data.length() + 1; i++) {
								HistoryOfMessagesBean hisBean = new HistoryOfMessagesBean();
								JSONObject item = data.getJSONObject(String.valueOf(i));
								hisBean.setMember_id(item.optString("message_id"));
								hisBean.setType(item.optString("type"));
								hisBean.setType_id(item.optString("type_id"));
								hisBean.setTo_user_type(item.optString("to_user_type"));
								hisBean.setSend_type(item.optString("send_type"));
								hisBean.setIs_read(item.optString("is_read"));
								hisBean.setContent(item.optString("content"));
								hisBean.setContent_type(item.optString("content_type"));
								hisBean.setAdd_time(item.optString("add_time"));
								hisBean.setSend_time(item.optString("send_time"));
								hisList.add(hisBean);
							}
							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						hisHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}

	Handler hisHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				mAdapter = new ChatMsgViewAdapter(ChatActivity.this, hisList, msgBean, myChildBean);
				mListView.setAdapter(mAdapter);
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			send(); // 发送消息
			((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
			if (ll_picchoose.getVisibility() == View.VISIBLE) {
				ll_picchoose.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_photo:
			// new PopupWindows(ChatActivity.this, btn_photo);
			// 隐藏表情选择框
			((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();

			if (ll_picchoose.getVisibility() == View.VISIBLE) {
				ll_picchoose.setVisibility(View.GONE);
			} else {
				ll_picchoose.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.btn_face:
			// 隐藏表情选择框
			if (ll_picchoose.getVisibility() == View.VISIBLE) {
				ll_picchoose.setVisibility(View.GONE);
			}
			((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView2();
			break;

		case R.id.et_sendmessage:
			// mEditTextContent.addTextChangedListener(new TextWatcher() {
			// @Override
			// public void afterTextChanged(Editable s) {
			// String text = mEditTextContent.getText().toString();
			// if(!(text.length()>0)){
			// //Toast.makeText(MainActivity.this,mEditTextContent.getText()+"请输入聊天内容",
			// 2).show();
			// btn_photo.setVisibility(View.VISIBLE);
			// mBtnSend.setVisibility(View.GONE);
			// }else{
			// //Toast.makeText(MainActivity.this,"输入的聊天内容为："+mEditTextContent.getText(),
			// 2).show();
			// btn_photo.setVisibility(View.GONE);
			// mBtnSend.setVisibility(View.VISIBLE);
			// }
			// }
			// @Override
			// public void onTextChanged(CharSequence s, int arg1, int arg2, int
			// arg3) {
			// }
			// @Override
			// public void beforeTextChanged(CharSequence s, int arg1, int arg2,
			// int arg3) {
			// }
			// });
			// 隐藏表情选择框
			((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
			if (ll_picchoose.getVisibility() == View.VISIBLE) {
				ll_picchoose.setVisibility(View.GONE);
			}
			break;

		case R.id.ll_photo:
			Intent intent = new Intent(ChatActivity.this, ScaleImageFromSdcardActivity.class);
			ChatActivity.this.startActivityForResult(intent, SHOW_ALL_PICTURE);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// 设置切换动画，从右边进入，左边退出
			break;

		case R.id.ll_camera:
			Intent intent2 = new Intent();
			Intent intent_camera = getPackageManager().getLaunchIntentForPackage("com.android.camera");
			if (intent_camera != null) {
				intent2.setPackage("com.android.camera");
			}
			intent2.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			ChatActivity.this.startActivityForResult(intent2, TAKE_PICTURE);
			break;

		case R.id.ll_suifang:
			MainActivity.mActivity.setTab(1);
			if (MyPatientActivity.pActivity != null) {
				MyPatientActivity.pActivity.finishMe();
			}
			finish();
			break;

		case R.id.ll_huanjiao:
			MainActivity.mActivity.setTab(2);
			if (MyPatientActivity.pActivity != null) {
				MyPatientActivity.pActivity.finishMe();
			}
			finish();
			break;
		}
	}

	public void send() {
		String conString = mEditTextContent.getText().toString();
		if (conString.length() > 0) {
			HistoryOfMessagesBean entity = new HistoryOfMessagesBean();
			entity.setSend_time(getDate());
			entity.setNickname("me");
			entity.setTo_user_type("1");
			entity.setText(conString);
			hisList.add(entity);

			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			mListView.setSelection(mListView.getCount() - 1);
			
			sendToNet();
		}
	}

	// 发送消息-请求网络
	private void sendToNet() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "message");
			reuqestMap.put("act", "sendMessage");
			reuqestMap.put("docId", uid); // 医生编号
			if (msgBean != null) {
				reuqestMap.put("userId", msgBean.getMember_id()); // 用户编号
			} else {
				reuqestMap.put("userId", myChildBean.getMember_id()); // 用户编号
			}
			reuqestMap.put("content", mEditTextContent.getText().toString()); // 发送内容
			reuqestMap.put("content_type", "1"); // 1文本内容 2图片内容
			reuqestMap.put("to_user_type", "1"); // 1发送给用户 2发送给医生
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
						Log.i("发送消息 ----", "" + result);
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
						sendHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}

	Handler sendHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {

			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};

	public String getDate() {
		Calendar c = Calendar.getInstance();
		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + "-" + hour + ":" + mins);
		return sbBuffer.toString();
	}

	private void updateDisplay(double signalEMA) {

		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);

			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			super(mContext);

			View view = View.inflate(mContext, R.layout.item_popubwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// photo();
					// dismiss();
					Intent intent = new Intent();
					Intent intent_camera = getPackageManager().getLaunchIntentForPackage("com.android.camera");
					if (intent_camera != null) {
						intent.setPackage("com.android.camera");
					}
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					ChatActivity.this.startActivityForResult(intent, TAKE_PICTURE);
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Intent intent = new Intent(MainActivity.this,
					// TestPicActivity.class);
					// startActivity(intent);
					// dismiss();
					/*
					 * Intent intent = new Intent(Intent.ACTION_PICK,
					 * android.provider.MediaStore.Images.Media.
					 * EXTERNAL_CONTENT_URI);//调用android的图库
					 * startActivity(intent); dismiss();
					 */
					Intent intent = new Intent(ChatActivity.this, ScaleImageFromSdcardActivity.class);
					ChatActivity.this.startActivityForResult(intent, SHOW_ALL_PICTURE);
					dismiss();
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// 设置切换动画，从右边进入，左边退出
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK && null != data) {
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Toast.makeText(ChatActivity.this, "未找到SDK", Toast.LENGTH_SHORT).show();
				return;
			}
			new android.text.format.DateFormat();
			String name = android.text.format.DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))
					+ ".jpg";
			Bundle bundle = data.getExtras();
			// 获取相机返回的数据，并转换为图片格式
			Bitmap bitmap;
			String filename = null;
			bitmap = (Bitmap) bundle.get("data");
			FileOutputStream fout = null;
			// 定义文件存储路径
			File file = new File(Environment.getExternalStorageDirectory() + "/pDoctor/");
			if (!file.exists()) {
				file.mkdirs();
			}
			filename = file.getPath() + "/" + name;
			try {
				fout = new FileOutputStream(filename);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					fout.flush();
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent intent = new Intent(ChatActivity.this, CameraActivity.class);
			intent.putExtra("camera", filename);
			ChatActivity.this.startActivityForResult(intent, SHOW_CAMERA);
		} else if (requestCode == SHOW_CAMERA && resultCode == SHOW_CAMERA_RESULT) {
			Bundle bundle = data.getExtras();
			Object camera = bundle.get("imgUrl");
			Log.d("TAG", "需要发送照相的图片到服务器" + camera.toString());
			// 将图片发送到聊天界面
			if (camera.toString().length() > 0) {
				HistoryOfMessagesBean entity = new HistoryOfMessagesBean();
				entity.setSend_time(getDate());
				entity.setNickname("me");
				entity.setTo_user_type("1");
				entity.setText("[" + camera.toString() + "]");
				
				hisList.add(entity);
				mAdapter.notifyDataSetChanged();
				mEditTextContent.setText("");
				mListView.setSelection(mListView.getCount() - 1);
			}
		} else if (requestCode == SHOW_ALL_PICTURE && resultCode == SHOW_PICTURE_RESULT) {
			List<String> bmpUrls = new ArrayList<String>();

			Bundle bundle = data.getExtras();
			Object[] selectPictures = (Object[]) bundle.get("selectPicture");
			for (int i = 0; i < selectPictures.length; i++) {
				Log.d("TAG", "selectPictures[i]" + selectPictures[i]);
				String bmpUrl = ScaleImageFromSdcardActivity.map.get(Integer.parseInt(selectPictures[i].toString()));
				bmpUrls.add(bmpUrl);
				HistoryOfMessagesBean entity = new HistoryOfMessagesBean();
				entity.setSend_time(getDate());
				entity.setNickname("me");
				entity.setTo_user_type("1");
				entity.setText("[" + bmpUrl + "]");
				mAdapter.notifyDataSetChanged();
				mEditTextContent.setText("");
				mListView.setSelection(mListView.getCount() - 1);
			}
			Toast.makeText(ChatActivity.this, "选择图片数" + selectPictures.length, Toast.LENGTH_LONG).show();
		}
	}

	private static final int TAKE_PICTURE = 0x000000;
	private static final int SHOW_CAMERA = 0x000001;
	private static final int SHOW_CAMERA_RESULT = 0x000002;
	private String path = "";

	public void photo() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory() + "/pDoctor/");
			if (!dir.exists())
				dir.mkdirs();

			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
			path = file.getPath();
			Uri imageUri = Uri.fromFile(file);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			startActivityForResult(openCameraIntent, TAKE_PICTURE);
			// ChatMsgEntity entity = new ChatMsgEntity();
			// entity.setDate(getDate());
			// entity.setName("古月哥欠");
			// entity.setMsgType(false);
			// entity.setText(path);
			// mDateArrays.add(entity);
			// mAdapter.notifyDataSetChanged();
			// mListView.setSelection(mListView.getCount() - 1);

		} else {
			Toast.makeText(ChatActivity.this, "没有储存卡", Toast.LENGTH_LONG).show();
		}
	}

	public void head_xiaohei(View v) { // 标题栏 返回按钮

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView()) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && ll_picchoose.getVisibility() == View.VISIBLE) {
			ll_picchoose.setVisibility(View.GONE);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

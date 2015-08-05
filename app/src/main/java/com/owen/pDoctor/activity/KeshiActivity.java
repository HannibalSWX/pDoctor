package com.owen.pDoctor.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.AbstractWheelTextAdapter;
import com.owen.pDoctor.model.KeshiBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.OnWheelChangedListener;
import com.owen.pDoctor.util.OnWheelScrollListener;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.owen.pDoctor.view.MyGridView;
import com.owen.pDoctor.view.MyListView;
import com.owen.pDoctor.view.WheelView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * ClassName：PersonalSettingActivity Description：个人设置页面 Author ： zq Date
 * ：2015-7-12 下午10:32:33
 */
public class KeshiActivity extends BaseActivity implements OnClickListener {

	private TextView tv_save, tv_hospital, tv_keshi;

	private LinearLayout back_btn, ll_keshi, ll_hospital, ll_week;

	private Button change;

	private Dialog dialog;

	private TextView tv_cancel, tv_sure, tv_diliver;

	private MyGridView gv_time;

	private MyListView lv_doctors;

	private String check_time = "";

	private HospitalAdapter hosadapter;

	private int from = 0;

	private WheelView wheel;

	private boolean scrolling = false;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String fromWhere, uid, loginName, userName, city_id, district_id, hospital_id, message, code, keshi;

	private ArrayList<String> timelist = new ArrayList<String>();

	private String[] hospitals = { "南京第一医院", "南京儿童医院", "江苏省人民医院", "南大附属医院" };

	private ArrayList<KeshiBean> keshilist = new ArrayList<KeshiBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keshi);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("uid", "");
		loginName = sp.getString("loginName", "");
		userName = sp.getString("userName", "");
		city_id = sp.getString("city_id", "");
		district_id = sp.getString("district_id", "");
		hospital_id = sp.getString("hospital_id", "");

		Intent intent = getIntent();
		if (intent.getStringExtra("from") != null) {
			fromWhere = intent.getStringExtra("from");
		}

		initview();
		setListener();

		getHospital();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_hospital = (TextView) findViewById(R.id.tv_hospital);
		tv_keshi = (TextView) findViewById(R.id.tv_keshi);

		ll_hospital = (LinearLayout) findViewById(R.id.ll_hospital);
		ll_keshi = (LinearLayout) findViewById(R.id.ll_keshi);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		tv_save.setOnClickListener(this);
		ll_hospital.setOnClickListener(this);
		ll_keshi.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		case R.id.tv_save:
			Intent intent = new Intent();
			intent.putExtra("hospital", tv_hospital.getText().toString());
			intent.putExtra("keshi", tv_keshi.getText().toString());
			setResult(RESULT_OK, intent);
			finish();
			break;

		case R.id.ll_hospital:
			showdialog(1);
			break;

		case R.id.ll_keshi:
			showdialog(2);
			break;

		default:
			break;
		}

	}

	private void showdialog(int i) {
		// TODO Auto-generated method stub
		from = i;
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
		wheel.setViewAdapter(new HospitalAdapter(this));
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
				if (from == 1) {
					tv_hospital.setText(hospitals[wheel.getCurrentItem()]);
					getKeshi();
				} else {
					tv_keshi.setText(keshilist.get(wheel.getCurrentItem()).getName());
				}
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
	 * dialog选择医院科室adapter
	 * 
	 */
	public class HospitalAdapter extends AbstractWheelTextAdapter {

		protected HospitalAdapter(Context context) {
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
			if (from == 1) {
				return hospitals == null ? 0 : hospitals.length;
			} else {
				return keshilist == null ? 0 : keshilist.size();
			}
		}

		@Override
		protected CharSequence getItemText(int index) {
			if (from == 1) {
				return hospitals[index] + "";
			} else {
				return keshilist.get(index) + "";
			}
		}
	}

	// 获取医院信息
	private void getHospital() {
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("获取中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "hospital");
			reuqestMap.put("act", "getHospitalByCity");
			reuqestMap.put("city_id", city_id);
			reuqestMap.put("district_id", district_id);
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
					Log.i("根据城市获取医院列表返回结果  : ----", result);
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
				keshi = "";
			} else {
				ToastUtil.showToast(KeshiActivity.this, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 获取医院科室信息
	private void getKeshi() {
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("app", "keshi");
			reuqestMap.put("act", "getKeshi");
			reuqestMap.put("hospital_id", hospital_id); // 医院编号
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
					Log.i("获取医院科室 返回结果  : ----", result);
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");

							for (int i = 0; i < data.length(); i++) {
								KeshiBean meetBean = new KeshiBean();
								JSONObject item = data.getJSONObject(String.valueOf(i));
								meetBean.setId(item.optString("id"));
								meetBean.setName(item.optString("name"));
								keshilist.add(meetBean);
							}

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

			} else {
				ToastUtil.showToast(KeshiActivity.this, message);
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}

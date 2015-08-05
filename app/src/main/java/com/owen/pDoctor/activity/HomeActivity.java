package com.owen.pDoctor.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.AdsAdapter;
import com.owen.pDoctor.model.Advertisement;
import com.owen.pDoctor.model.HomeMeetBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ImageLoader;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.owen.pDoctor.view.MyGallery;

/**
 * ClassName：HomeActivity Description：首页 Author ： zhouqiang Date ：2015-7-11
 * 下午8:31:39
 */
public class HomeActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	public List<String> urls;

	private LinearLayout ads, ads_banner_guide;

	private MyGallery ads_banner;

	Drawable point, pointfocus;

	private int index = 0;

	Uri uri;

	private ImageView top_menu, top_person;

	private LinearLayout ll_my_patient, ll_my_suifang, ll_huanjiao_center, ll_pay_service;
	
	private RelativeLayout rl_dingzhi, rl_renzheng;
	
	private TextView tv_newest, tv_shifan, tv_kuaixun;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private String picture_path;

	List<Advertisement> adsBean = new ArrayList<Advertisement>();

	private CustomProgressDialog progressDialog = null;

	private Dialog dialog;

	private Intent intent;

	private Handler mHandler;

	private AdsAdapter adsadapter;

	private Timer timer;

	private MyTimerTask task;

	private ArrayList<HomeMeetBean> adslist = new ArrayList<HomeMeetBean>();

	private String message, code;

	private ImageLoader mImageLoader = new ImageLoader(mContext);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_home);

		timer = new Timer();

		initView();
		setListener();
		getadsdata();
	}

	private void initView() {
		// TODO Auto-generated method stub
		top_menu = (ImageView) findViewById(R.id.top_menu);
		top_person = (ImageView) findViewById(R.id.top_person);
		ads = (LinearLayout) findViewById(R.id.ads);
		ads_banner = (MyGallery) findViewById(R.id.ads_banner);
		ads_banner_guide = (LinearLayout) findViewById(R.id.ads_banner_guide);
		point = getResources().getDrawable(R.drawable.ic_dot_normal);
		pointfocus = getResources().getDrawable(R.drawable.ic_dot_focused);
		ll_my_patient = (LinearLayout) findViewById(R.id.ll_my_patient);
		ll_my_suifang = (LinearLayout) findViewById(R.id.ll_my_suifang);
		ll_huanjiao_center = (LinearLayout) findViewById(R.id.ll_huanjiao_center);
		ll_pay_service = (LinearLayout) findViewById(R.id.ll_pay_service);
		rl_dingzhi = (RelativeLayout) findViewById(R.id.rl_dingzhi);
		rl_renzheng = (RelativeLayout) findViewById(R.id.rl_renzheng);
		tv_newest = (TextView) findViewById(R.id.tv_newest);
		tv_shifan = (TextView) findViewById(R.id.tv_shifan);
		tv_kuaixun = (TextView) findViewById(R.id.tv_kuaixun);

		// 获取屏幕宽高度（像素）设置广告栏高度
		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels; // 屏幕高度（像素）
		LayoutParams params = (LayoutParams) ads.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		// params.height = 5 * width / 16;
		params.height = 2 * width / 7;
		ads.setLayoutParams(params);

		ads_banner.setOnItemClickListener(new bannerItemListener());
		ads_banner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			int i;
			ImageView im;

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				ads_banner_guide.removeAllViews();
				for (i = 0; i < adsBean.size(); i++) {
					im = new ImageView(HomeActivity.this);
					im.setPadding(5, 0, 5, 0);
					if (i == arg2 % adsBean.size()) {
						im.setImageDrawable(pointfocus);
						ads_banner_guide.removeView(im);
						ads_banner_guide.addView(im);
					} else {
						im.setImageDrawable(point);
						ads_banner_guide.removeView(im);
						ads_banner_guide.addView(im);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * setListener
	 */
	private void setListener() {
		top_menu.setOnClickListener(this);
		top_person.setOnClickListener(this);
		ll_my_patient.setOnClickListener(this);
		ll_my_suifang.setOnClickListener(this);
		ll_huanjiao_center.setOnClickListener(this);
		ll_pay_service.setOnClickListener(this);
		rl_dingzhi.setOnClickListener(this);
		rl_renzheng.setOnClickListener(this);
		tv_newest.setOnClickListener(this);
		tv_shifan.setOnClickListener(this);
		tv_kuaixun.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_menu:
			/** 首页顶部菜单 **/
			Intent intent = new Intent(mContext, SearchActivity.class);
			intent.putExtra("categoryId", "");
			startActivity(intent);
			break;

		case R.id.top_person:
			/** 首页添加人员 **/
			Intent intent2 = new Intent(mContext, MyQRImageActivity.class);
			intent2.putExtra("from", "addpatient");
			startActivity(intent2);
			break;

		case R.id.ll_my_patient:
			/** 我的患者 **/
//			Intent intent3 = new Intent(mContext, MyPatientActivity.class);
//			intent3.putExtra("from", "home");
//			startActivity(intent3);
			MainActivity.mActivity.setTab(1);
			break;

		case R.id.ll_my_suifang:
			/** 我的随访 **/
			MainActivity.mActivity.setTab(1);
			break;

		case R.id.ll_huanjiao_center:
			/** 患教中心 **/
			MainActivity.mActivity.setTab(2);
			break;

		case R.id.ll_pay_service:
			/** 收费服务 **/
			startActivity(new Intent(mContext, PayServiceActivity.class));
			break;

		case R.id.rl_dingzhi:
			/** 定制 **/
			startActivity(new Intent(mContext, MyZoneActivity.class));
			break;
			
		case R.id.rl_renzheng:
			/** 认证 **/
			startActivity(new Intent(mContext, AuthenticationActivity.class));
			break;
			
		case R.id.tv_newest:
			/** 最新会议 **/
			
			break;
			
		case R.id.tv_shifan:
			/** 示范视频 **/
			
			break;
		case R.id.tv_kuaixun:
			/** 快讯 **/
			
			break;
		default:
			break;
		}
	}

	// Banner ItemClick
	class bannerItemListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (adsBean != null && adsBean.size() > 0) {
				// if (adsBean.get(position %
				// adsBean.size()).getType().equals("1")) {
				// Intent intent = new Intent(mContext,
				// HomeHeatDetailActivity.class);
				// intent.putExtra("tittle", "商品详情");
				// intent.putExtra("pid", adsBean.get(position %
				// adsBean.size()).getPid());
				// startActivity(intent);
				// }
				// if (adsBean.get(position %
				// adsBean.size()).getType().equals("2")) {
				Intent intent = new Intent(mContext, WebViewActivity.class);
				intent.putExtra("tittle", "详情");
				intent.putExtra("link", adsBean.get(position % adsBean.size()).getAdvertUrl());
				startActivity(intent);
				// }
			}
		}
	}

	/**
	 * 定时器，实现自动播放
	 */
	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			index = ads_banner.getSelectedItemPosition();
			index++;
			adshandler.sendMessage(message);
		}
	};

	private Handler adshandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				ads_banner.setSelection(index);
				// ads_banner.onScroll(null, null, 1 * (20 + 30), 0);
				// ads_banner.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 获取广告
	 * 
	 * @param order_type
	 *            userId page rows
	 * 
	 * @return adslist
	 */
	private ArrayList<HomeMeetBean> getadsdata() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "huiyi");
			reuqestMap.put("act", "getHomeList");
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
						Log.i("meetlist ----", "" + result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("status");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							for (int i = 1; i < data.length() + 1; i++) {
								Advertisement advertisement = new Advertisement();
								JSONObject item = data.getJSONObject(String.valueOf(i));
								advertisement.setId(item.optString("id"));
								advertisement.setAdvertUrl(item.optString("url"));
								advertisement.setImageUrl(item.optString("pic"));
								advertisement.setName(item.optString("name"));
								adsBean.add(advertisement);
							}
							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						adsHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		return adslist;
	}

	Handler adsHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
//				adapter = new HomeMeetlistAdapter();
//				meet_lv.setAdapter(adapter);
//				adapter.notifyDataSetChanged();
//
//				categoryAdapter = new HomeCategoryAdapter(mContext, categorylist);
//				lv_category.setAdapter(categoryAdapter);

				// 广告栏数据填充
				adsadapter = new AdsAdapter(HomeActivity.this, adsBean);
				ads_banner.setAdapter(adsadapter);
				ads_banner.setSpacing(10);
				if (task != null) {
					task.cancel();
				}
				task = new MyTimerTask();
				timer.schedule(task, 4000, 4000);
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};

	public class HomeMeetlistAdapter extends BaseAdapter {

		private boolean mBusy = false;

		public void setFlagBusy(boolean busy) {
			this.mBusy = busy;
		}

		public ImageLoader getImageLoader() {
			return mImageLoader;
		}

		public int getCount() {
			return adslist == null ? 0 : adslist.size();
		}

		public Object getItem(int arg0) {
			return adslist == null ? 0 : adslist.get(arg0);
		}

		public long getItemId(int arg0) {
			return adslist == null ? 0 : arg0;
		}

		public View getView(int position, View view, ViewGroup arg2) {
			final Hodler hodler;
			if (view == null) {
				hodler = new Hodler();
				view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.heat_listitem, null);
				hodler.items_year = (TextView) view.findViewById(R.id.items_year);
				hodler.items_day = (TextView) view.findViewById(R.id.items_day);
				hodler.tittle = (TextView) view.findViewById(R.id.items_tittle);
				hodler.items_major = (TextView) view.findViewById(R.id.items_major);
				hodler.items_address = (TextView) view.findViewById(R.id.items_address);
				hodler.items_dianji = (TextView) view.findViewById(R.id.items_dianji);

				view.setTag(hodler);
			} else {
				hodler = (Hodler) view.getTag();
			}

			hodler.items_year.setText(adslist.get(position).getYear());
			hodler.items_day.setText(adslist.get(position).getDay());
			hodler.tittle.setText(adslist.get(position).getTitle());
			hodler.items_major.setText(adslist.get(position).getMajor());
			hodler.items_address.setText(adslist.get(position).getCity());
			// hodler.items_dianji.setText(meetlist.get(position).getTitle());
			// String imagePath = meetlist.get(position).getImg();
			//
			//// // 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
			// hodler.items_im.setImageResource(R.drawable.ic_launcher);
			// if (imagePath == null || "".equals(imagePath)) {
			// hodler.items_im.setImageResource(R.drawable.ic_launcher);
			// } else {
			// // 需要显示的网络图片
			// if (!mBusy) {
			// mImageLoader.DisplayImage(imagePath, hodler.items_im, false);
			// } else {
			// mImageLoader.DisplayImage(imagePath, hodler.items_im, true);
			// }
			// }

			return view;
		}

		public class Hodler {
			TextView items_year, items_day, tittle, items_address, items_major, items_dianji;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}

package com.owen.pDoctor.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.model.SearchResultBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ImageLoader;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.owen.pDoctor.view.XListView;
import com.owen.pDoctor.view.XListView.IXListViewListener;

/**
 * ClassName：SearchActivity
 * Description：搜索结果页
 * Author ： zhouqiang
 * Date ：2015-1-24 下午2:58:58
 * Copyright (C) 2012-2014 owen
 */
public class SearchResultActivity extends BaseActivity implements IXListViewListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	public List<String> urls;

	Uri uri;

	private LinearLayout back_btn;
	
	private AutoCompleteTextView search_et;

	private ImageView search_im, close_im;
	
	private TextView hint_tv, hint_total;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;


	private CustomProgressDialog progressDialog = null;

	private Intent intent;

	private XListView search_result_list;

	private SearchResultAdapter search_adapter;

	private ArrayList<SearchResultBean> resultList = new ArrayList<SearchResultBean>();
	
	private Handler mHandler;
	
	private String keywords, categoryid, typeId, message;
	
	private int page = 0, totalNum = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_search_result);
		
		Intent intent = getIntent();
		keywords = intent.getStringExtra("search_tv");
		categoryid = intent.getStringExtra("categoryId");
		typeId = intent.getStringExtra("typeId");
		
		initView();
		getsearchresult(keywords, 0);
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		search_et = (AutoCompleteTextView) findViewById(R.id.search_et);
		search_im = (ImageView) findViewById(R.id.search_im);
		close_im = (ImageView) findViewById(R.id.close_im);
		hint_tv = (TextView) findViewById(R.id.hint_tv);
		hint_total = (TextView) findViewById(R.id.hint_total);
		search_result_list = (XListView) findViewById(R.id.search_result_list);
		
		search_result_list.setPullLoadEnable(true);
		search_result_list.setSelection(search_result_list.getCount() - 1);
		search_result_list.requestLayout();
		// mListView.setPullLoadEnable(false);
		// mListView.setPullRefreshEnable(false);
		search_result_list.setXListViewListener(this);
		mHandler = new Handler();
		
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				finish();
			}
		});
		search_et.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		search_im.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				finish();
			}
		});
		close_im.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				search_et.setText("");
			}
		});
		search_result_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (null != resultList && resultList.size() > 0) {
					Intent intent = new Intent(mContext, HomeHeatDetailActivity.class);
					intent.putExtra("tittle", resultList.get(position - 1).getTitle());
					intent.putExtra("pid", resultList.get(position - 1).getPid());
					startActivity(intent);
				 }
			}
		});

		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
	}

	// 下拉刷新
		@Override
		public void onRefresh() {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// start = ++refreshCnt;
					// repairList.clear();
					if (resultList.size() > 0 && resultList != null) {
						resultList.clear();
						search_adapter.notifyDataSetChanged();
					}
					getsearchresult(keywords, 0);
					onLoad();
				}
			}, 2000);
		}

		// 加载更多
		@Override
		public void onLoadMore() {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					getsearchresult(keywords, page);
					onLoad();
				}
			}, 2000);
		}

		private void onLoad() {
			search_result_list.stopRefresh();
			search_result_list.stopLoadMore();
			String currentTime = Utils.getCurrentTime();
			search_result_list.setRefreshTime(currentTime);
			if (search_adapter != null) {
				search_result_list.requestLayout();
				search_adapter.notifyDataSetInvalidated();
			}
		}
		
	// 获取搜索结果
	private void getsearchresult(String keywords, int page) {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("keywords", keywords);
			reuqestMap.put("categoryId", typeId);
			reuqestMap.put("typeId", categoryid);
			reuqestMap.put("pageIndex", "" + page);
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("searchresult ----", result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							String code = Jsonresult.getString("code");
							JSONObject data = Jsonresult.getJSONObject("data");
							message = Jsonresult.getString("msg");
							msg.what = Integer.parseInt(code);
							if (!data.equals("")) {
								String items = data.getJSONArray("items").toString();
								totalNum = data.getInt("totalNum");
								JSONArray array = new JSONArray(items);
								for (int i = 0; i < array.length(); i++) {
									SearchResultBean searchBean = new SearchResultBean();
									JSONObject jsonObject = array.getJSONObject(i);
									searchBean.setTitle(jsonObject.optString("title"));
									searchBean.setImg(jsonObject.optString("img"));
									searchBean.setContent(jsonObject.optString("content"));
									searchBean.setPrice(jsonObject.optString("price"));
									searchBean.setPid(jsonObject.optString("pid"));
									resultList.add(searchBean);
								}
							}
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
			if(msg.what == 200) {
				if (resultList.size() > 0) {
					hint_tv.setVisibility(View.GONE);
					hint_total.setVisibility(View.VISIBLE);
					hint_total.setText("共有 " + totalNum + " 条搜索结果");
					search_result_list.setVisibility(View.VISIBLE);
					search_adapter = new SearchResultAdapter();
					search_result_list.setAdapter(search_adapter);
					search_adapter.notifyDataSetChanged();
				}
				if (!message.equals("")) {
					ToastUtil.showToast(mContext, message);
				}
				page++;
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
	
	OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				search_adapter.setFlagBusy(true);
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				search_adapter.setFlagBusy(false);
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				search_adapter.setFlagBusy(false);
				break;
			default:
				break;
			}
			search_adapter.notifyDataSetChanged();
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};
	
	public class SearchResultAdapter extends BaseAdapter {

		private ImageLoader mImageLoader = new ImageLoader(mContext);
		
		private boolean mBusy = false;

		public void setFlagBusy(boolean busy) {
			this.mBusy = busy;
		}
		

		public ImageLoader getImageLoader(){
			return mImageLoader;
		}
		
		public int getCount() {
			return resultList == null ? 0 : resultList.size();
		}

		public Object getItem(int arg0) {
			return resultList == null ? 0 : resultList.get(arg0);
		}

		public long getItemId(int arg0) {
			return resultList == null ? 0 : arg0;
		}

		public View getView(int position, View view, ViewGroup arg2) {
			final Hodler hodler;
			if (view == null) {
				hodler = new Hodler();
				view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.search_result_listitem, null);
				hodler.items_im = (ImageView) view.findViewById(R.id.items_im);
				hodler.items_tittle = (TextView) view.findViewById(R.id.items_tittle);
				hodler.items_address = (TextView) view.findViewById(R.id.items_address);
				hodler.items_price = (TextView) view.findViewById(R.id.items_price);
				hodler.items_time = (TextView) view.findViewById(R.id.items_time);
				hodler.items_site = (TextView) view.findViewById(R.id.items_site);
				
				view.setTag(hodler);
			} else {
				hodler = (Hodler) view.getTag();
			}
			
			hodler.items_tittle.setText(resultList.get(position).getTitle());
			hodler.items_price.setText(resultList.get(position).getPrice());
			hodler.items_address.setText(resultList.get(position).getContent());
			String imagePath = resultList.get(position).getImg().toString();
			
			// 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
			hodler.items_im.setImageResource(R.drawable.ic_launcher);
			if (imagePath == null || "".equals(imagePath)) {
				hodler.items_im.setImageResource(R.drawable.ic_launcher);
			} else {
				// 需要显示的网络图片
				if (!mBusy) {
		             mImageLoader.DisplayImage(imagePath, hodler.items_im, false);
				 } else {
		             mImageLoader.DisplayImage(imagePath, hodler.items_im, true);
				 }
			}
			return view;
		}

		public class Hodler {
			TextView items_tittle, items_address, items_price, items_time, items_site;
			ImageView items_im;
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
		if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
	        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
	                hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog != null){
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}

package com.owen.pDoctor.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.MessagesAdapter;
import com.owen.pDoctor.model.MessagesBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.owen.pDoctor.view.XListView;
import com.owen.pDoctor.view.XListView.IXListViewListener;

/**
 * @Title:MessagesActivity.java
 * @Description:应用MessagesActivity.java类
 * @Author:owen
 * @Since:2015年7月17日
 * @Version:
 */
public class MessagesActivity extends BaseActivity implements
		IXListViewListener {

	private Context mContext;

	private XListView lv_messages;

	private Handler mHandler;

	private MessagesAdapter messagesdapter;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private ArrayList<MessagesBean> messagelist = new ArrayList<MessagesBean>();

	private String uid, loginName, userName, ids, message, code, servicephone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_messages);

		SharedPreferences sp = getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		loginName = sp.getString("loginName", "");
		userName = sp.getString("userName", "");

		initview();
		getMsgList();
	}

	private void initview() {
		// TODO Auto-generated method stub
		lv_messages = (XListView) findViewById(R.id.lv_messages);
		lv_messages.setPullLoadEnable(false);
		lv_messages.setSelection(lv_messages.getCount() - 1);
		lv_messages.requestLayout();
		lv_messages.setXListViewListener(this);
		mHandler = new Handler();

		lv_messages.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (null != messagelist && messagelist.size() > 0 && position > 0) {
					Intent intent = new Intent(mContext, MessagePersonalActivity.class);
					intent.putExtra("from", "message");
					intent.putExtra("childItem", (Serializable) messagelist.get(position - 1));
					startActivity(intent);
				}
			}
		});
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// start = ++refreshCnt;
				// repairList.clear();
				if (messagelist.size() > 0 && messagelist != null) {
					messagelist.clear();
				}
				getMsgList();
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
				// onLoad();
			}
		}, 2000);
	}

	private void onLoad() {
		lv_messages.stopRefresh();
		lv_messages.stopLoadMore();
		String currentTime = Utils.getCurrentTime();
		lv_messages.setRefreshTime(currentTime);
		if (messagesdapter != null) {
			lv_messages.requestLayout();
			messagesdapter.notifyDataSetInvalidated();
		}
	}

	// 获取我的消息列表
	private ArrayList<MessagesBean> getMsgList() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "group");
			reuqestMap.put("act", "getAllMemByDocId");
			reuqestMap.put("doctorId", uid); // 医生编号
			reuqestMap.put("messageType", "0"); // 医生编号
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
					if (result != null && !result.equals("")) {
						Log.i("消息列表 ----", "" + result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");

							JSONArray array = new JSONArray(data
									.getString("messageList"));
							for (int i = 0; i < array.length(); i++) {
								MessagesBean myBean = new MessagesBean();
								JSONObject jsonObject = array.getJSONObject(i);
								myBean.setMember_id(jsonObject
										.optString("member_id"));
								myBean.setNickname(jsonObject
										.optString("nickname"));
								myBean.setImgurl(jsonObject.optString("imgurl"));
								myBean.setNoread_num(jsonObject
										.optString("noread_num"));
								myBean.setContent(jsonObject
										.optString("content"));
								messagelist.add(myBean);
							}
							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						msgHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		return messagelist;
	}

	Handler msgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				messagesdapter = new MessagesAdapter(MessagesActivity.this,
						messagelist);
				lv_messages.setAdapter(messagesdapter);
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}

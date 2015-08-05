package com.owen.pDoctor.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.GroupAdapter;
import com.owen.pDoctor.model.MyGroupBean;
import com.owen.pDoctor.model.MyGroupChildBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.owen.pDoctor.view.XExpandableListView;
import com.owen.pDoctor.view.XExpandableListView.IXExpandableListViewListener;

/**
 * @Title:ContactsActivity.java
 * @Description:应用ContactsActivity.java类
 * @Author:owen
 * @Since:2015年7月17日
 * @Version:
 */
public class ContactsActivity extends BaseActivity implements IXExpandableListViewListener {

	private Context mContext;

	private XExpandableListView lv_contacts;

	protected static final int REFRESHING = 0;
	protected static final int LOADMOREING = 1;

	private GroupAdapter groupAdapter;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private ArrayList<MyGroupBean> mygrouplist = new ArrayList<MyGroupBean>();
	
	private List<ArrayList<MyGroupChildBean>> childData = new ArrayList<ArrayList<MyGroupChildBean>>();
	
	private ArrayList<Integer> checkState = new ArrayList<Integer>();

	private String uid, loginName, userName, ids, message, code, servicephone;

	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_contacts);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");
		loginName = sp.getString("loginName", "");
		userName = sp.getString("userName", "");

		initview();
		getGroup();
	}

	private void initview() {
		// TODO Auto-generated method stub
		lv_contacts = (XExpandableListView) findViewById(R.id.lv_contacts);

		lv_contacts.setPullRefreshEnable(true); // open the pull to refresh
		lv_contacts.setPullLoadEnable(false); // open the pull to load
		lv_contacts.setExpandableListViewListener(ContactsActivity.this);

		lv_contacts.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MyPatientActivity.class);
				intent.putExtra("tittle", "");
				startActivity(intent);
			}
		});

		lv_contacts.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				hintDialog(position);
				return true;
			}
		});

		// item子view点击
		lv_contacts.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
					long id) {
				Intent intent = new Intent(mContext, MessagePersonalActivity.class);
				intent.putExtra("from", "contacts");
				intent.putExtra("childItem", (Serializable)childData.get(groupPosition).get(childPosition));
				startActivity(intent);
				return false;
			}
		});
	}

	/**
	 * it is called when the data already refreshed or loaded more
	 */
	private void stopPull() {
		lv_contacts.stopLoadMore();
		lv_contacts.stopRefresh();
	}

	/**
	 * it is called when you do not need refresh or load more
	 */
	private void closePull() {
		lv_contacts.setPullLoadEnable(false);
	}

	@Override
	public void onRefresh() {
		Message msg = Message.obtain();
		msg.what = REFRESHING;
		handler.sendMessageDelayed(msg, 2000);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESHING:
				if (uid.equals("") || uid == null) {
					ToastUtil.showToast(mContext, "请登录");
					return;
				} else {
					if (mygrouplist.size() > 0) {
						mygrouplist.clear();
						if (childData.size() > 0) {
							childData.clear();
						}
						getGroup();
					}
				}
				String currentTime = Utils.getCurrentTime();
				lv_contacts.setRefreshTime(currentTime);
				if (groupAdapter != null) {
					lv_contacts.requestLayout();
					groupAdapter.notifyDataSetInvalidated();
				}
				lv_contacts.stopRefresh();
				break;
			case LOADMOREING:
				if (uid.equals("") || uid == null) {
					ToastUtil.showToast(mContext, "请登录");
					return;
				} else {
					ToastUtil.showToast(mContext, "没有更多了");
				}
				lv_contacts.stopLoadMore();
				break;
			}
		}
	};

	@Override
	public void onLoadMore() {
		Message msg = Message.obtain();
		msg.what = LOADMOREING;
		handler.sendMessageDelayed(msg, 2000);
	}

	// 获取我的分组和组员列表
	private ArrayList<MyGroupBean> getGroup() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "group");
			reuqestMap.put("act", "getAllMemByDocId");
			reuqestMap.put("doctorId", uid);
			reuqestMap.put("messageType", "1"); // 0：最近聊天信息的患者 1：分组和分组下的患者信息
			reuqestMap.put("notId", ""); // 需要排除的分组编号
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
						Log.i("获取所有分组和分组下面的患者 ----", "" + result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							String items = data.getJSONArray("list").toString();

							JSONArray array = new JSONArray(items);
							for (int i = 0; i < array.length(); i++) {
								MyGroupBean myBean = new MyGroupBean();
								JSONObject jsonObject = array.getJSONObject(i);
								myBean.setId(jsonObject.optString("id"));
								myBean.setDoc_id(jsonObject.optString("doc_id"));
								myBean.setName(jsonObject.optString("name"));
								myBean.setSort_no(jsonObject.optString("sort_no"));
								myBean.setIs_default(jsonObject.optString("is_default"));
								myBean.setAdd_time(jsonObject.optString("add_time"));
								myBean.setUpd_time(jsonObject.optString("upd_time"));
								myBean.setMark(jsonObject.optString("mark"));
								myBean.setList(jsonObject.optString("list"));
								JSONArray childArray = new JSONArray(jsonObject.optString("list"));
								ArrayList<MyGroupChildBean> childItems = new ArrayList<MyGroupChildBean>();
								for (int j = 0; j < childArray.length(); j++) {
									MyGroupChildBean myChildBean = new MyGroupChildBean();
									JSONObject childObject = childArray.getJSONObject(j);
									myChildBean.setMember_id(childObject.optString("member_id"));
									myChildBean.setWxname(childObject.optString("wxname"));
									myChildBean.setNickname(childObject.optString("nickname"));
									myChildBean.setRealname(childObject.optString("realname"));
									myChildBean.setHeadimgurl(childObject.optString("headimgurl"));
									childItems.add(myChildBean);
								}
								childData.add(childItems);
								mygrouplist.add(myBean);
							}
							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fabuHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		return mygrouplist;
	}

	Handler fabuHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				groupAdapter = new GroupAdapter(mContext, mygrouplist, childData);
				lv_contacts.setAdapter(groupAdapter);
				groupAdapter.notifyDataSetChanged();
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};

	// hintDialog
	public void hintDialog(final int pos) {
		// TODO Auto-generated method stub
		dialog = new Dialog(getParent(), R.style.home_dialog);
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
		// tittle_tv.setText(tittle);
		content_tv.setText("管理我的分组");
		// cancel_tv.setText(cancel);
		// sure_tv.setText(sure);
		ll_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ll_sure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(mContext, GroupManagerActivity.class);
				intent.putExtra("from", "longclick");
				startActivity(intent);
				dialog.dismiss();
			}
		});
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}

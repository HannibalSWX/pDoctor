package com.owen.pDoctor.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.model.ManagerGroupBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.EncryptionUtil;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @Title:GroupManagerActivity.java
 * @Description:应用GroupManagerActivity.java类
 * @Author:owen
 * @Since:2015年7月23日
 * @Version:
 */
public class GroupManagerActivity extends BaseActivity {

	private Context mContext;

	private LinearLayout back_btn;

	private ListView lv_groups;

	private TextView tv_addgroup;

	private GroupsAdapter groupsAdpter;

	private static Dialog dialog;

	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private ArrayList<ManagerGroupBean> grouplist = new ArrayList<ManagerGroupBean>();

	private String uid, ids, message, code, servicephone;

	private int delPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_groups);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("id", "");

		initview();
		getGroupsList();
	}

	private void initview() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		tv_addgroup = (TextView) findViewById(R.id.tv_addgroup);
		lv_groups = (ListView) findViewById(R.id.lv_groups);
		groupsAdpter = new GroupsAdapter();
		lv_groups.setAdapter(groupsAdpter);

		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		tv_addgroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addDialog(-1, "add");
			}
		});

		lv_groups.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				ToastUtil.showToast(mContext, grouplist.get(position).getName());
			}
		});

		lv_groups.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				hintDialog(position);
				return true;
			}
		});
	}

	// 获取我的分组列表
	private ArrayList<ManagerGroupBean> getGroupsList() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.setMessage("加载中，请稍后...");
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "group");
			reuqestMap.put("act", "getAllGroupByDocId");
			reuqestMap.put("doctorId", uid); // 医生编号
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
						Log.i("分组列表 ----", "" + result);
						if (grouplist != null || grouplist.size() > 0) {
							grouplist.clear();
						}
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							// JSONObject data =
							// Jsonresult.getJSONObject("data");

							String items = Jsonresult.getJSONArray("data").toString();
							JSONArray array = new JSONArray(items);
							for (int i = 0; i < array.length(); i++) {
								ManagerGroupBean myBean = new ManagerGroupBean();
								JSONObject jsonObject = array.getJSONObject(i);
								myBean.setId(jsonObject.optString("id"));
								myBean.setDoc_id(jsonObject.optString("doc_id"));
								myBean.setName(jsonObject.optString("name"));
								myBean.setSort_no(jsonObject.optString("sort_no"));
								myBean.setIs_default(jsonObject.optString("is_default"));
								myBean.setAdd_time(jsonObject.optString("add_time"));
								myBean.setUpd_time(jsonObject.optString("upd_time"));
								myBean.setMark(jsonObject.optString("mark"));
								grouplist.add(myBean);
							}
							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		return grouplist;
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				groupsAdpter = new GroupsAdapter();
				lv_groups.setAdapter(groupsAdpter);
				// groupAdapter = new GroupAdapter(mContext, myfabulist);
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 修改、删除分组
	public void hintDialog(final int pos) {
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
		// tittle_tv.setText(tittle);
		content_tv.setText("修改/删除分组");
		cancel_tv.setText("修改");
		sure_tv.setText("删除");
		ll_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				addDialog(pos, "");
			}
		});
		ll_sure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				deleteGroup(pos);
				dialog.dismiss();
			}
		});
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 添加分组
	public void addDialog(final int pos, final String from) {
		// TODO Auto-generated method stub
		dialog = new Dialog(mContext, R.style.home_dialog);
		dialog.setContentView(R.layout.edit_dialog);
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.CENTER_VERTICAL);
		TextView tittle_tv = (TextView) dialogWindow.findViewById(R.id.tittle_tv);
		final TextView et_edit = (TextView) dialogWindow.findViewById(R.id.et_edit);
		TextView cancel_tv = (TextView) dialogWindow.findViewById(R.id.cancel_tv);
		TextView sure_tv = (TextView) dialogWindow.findViewById(R.id.sure_tv);
		LinearLayout ll_cancel = (LinearLayout) dialogWindow.findViewById(R.id.ll_cancel);
		LinearLayout ll_sure = (LinearLayout) dialogWindow.findViewById(R.id.ll_sure);
		cancel_tv.setText("取消");
		if (!from.equals("add")) {
			tittle_tv.setText("修改分组");
			sure_tv.setText("修改");
			et_edit.setText(grouplist.get(pos).getName());
		} else {
			tittle_tv.setText("添加分组");
			sure_tv.setText("添加");
			et_edit.setText("请输入组名");
		}
		ll_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ll_sure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!from.equals("add")) {
					editGroup(pos, et_edit.getText().toString());
				} else {
					addGroup(et_edit.getText().toString());
				}
				dialog.dismiss();
			}
		});
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 删除我的分组列表
	private ArrayList<ManagerGroupBean> deleteGroup(int pos) {
		delPos = pos;
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "group");
			reuqestMap.put("act", "drop");
			reuqestMap.put("doctorId", uid); // 医生编号
			reuqestMap.put("group_id", grouplist.get(pos).getId()); // 分组编号
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
						Log.i("删除我的分组 ----", "" + result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							// JSONObject data =
							// Jsonresult.getJSONObject("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						delHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		return grouplist;
	}

	Handler delHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(mContext, message);
				getGroupsList();
				groupsAdpter.notifyDataSetChanged();
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};

	// 修改我的分组列表
	private ArrayList<ManagerGroupBean> editGroup(int pos, String groupName) {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "group");
			reuqestMap.put("act", "edit");
			reuqestMap.put("doctorId", uid); // 医生编号
			reuqestMap.put("group_id", grouplist.get(pos).getId()); // 分组编号
			reuqestMap.put("group_name", groupName); // 修改后的分组名
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
						Log.i("修改我的分组 ----", "" + result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							// JSONObject data =
							// Jsonresult.getJSONObject("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						editHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		return grouplist;
	}

	Handler editHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(mContext, message);
				getGroupsList();
				groupsAdpter.notifyDataSetChanged();
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};

	// 添加我的分组列表
	private ArrayList<ManagerGroupBean> addGroup(String groupName) {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			// reuqestMap.put("pageIndex", page + "");
			reuqestMap.put("app", "group");
			reuqestMap.put("act", "edit");
			reuqestMap.put("doctorId", uid); // 医生编号
			reuqestMap.put("group_name", groupName); // 分组名称
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
						Log.i("添加我的分组 ----", "" + result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("result");
							message = Jsonresult.getString("msg");
							// JSONObject data =
							// Jsonresult.getJSONObject("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						addHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
		return grouplist;
	}

	Handler addHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ToastUtil.showToast(mContext, message);
				getGroupsList();
				groupsAdpter.notifyDataSetChanged();
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};

	/**
	 * 选择天数adapter
	 * 
	 */
	public class GroupsAdapter extends BaseAdapter {

		private boolean ischeck = false;

		public int getCount() {
			return grouplist == null ? 0 : grouplist.size();
		}

		public Object getItem(int arg0) {
			return grouplist == null ? 0 : grouplist.get(arg0);
		}

		public long getItemId(int arg0) {
			return grouplist == null ? 0 : arg0;
		}

		public View getView(int position, View view, ViewGroup arg2) {
			final Hodler hodler;
			if (view == null) {
				hodler = new Hodler();
				view = (LinearLayout) LayoutInflater.from(GroupManagerActivity.this).inflate(R.layout.group_item, null);
				hodler.items_name = (TextView) view.findViewById(R.id.items_name);
				view.setTag(hodler);
			} else {
				hodler = (Hodler) view.getTag();
			}

			hodler.items_name.setText(grouplist.get(position).getName());
			return view;
		}

		public class Hodler {
			TextView items_name;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}

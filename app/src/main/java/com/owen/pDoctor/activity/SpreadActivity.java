package com.owen.pDoctor.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.SpreadTypeListAdapter;
import com.owen.pDoctor.model.SpreadTypeBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;

/**
 * ClassName：SpreadActivity
 * Description：推广页面
 * Author ： zhouqiang
 * Date ：2015-1-26 上午12:25:18
 * Copyright (C) 2012-2014 owen
 */
public class SpreadActivity extends BaseActivity implements OnClickListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	private ImageView top_fabu;
	
	private TextView top_tittle;
	
	private TextView instructionTitle, instruction;
	
	private TextView phone_no;

	private LinearLayout back_btn;

	private LinearLayout phone_ll;
	
	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private Intent intent;

	private ListView spread_type_lv;
	
	private SpreadTypeListAdapter spread_list_adapter;
	
	private ArrayList<SpreadTypeBean> spreadTypeList = new ArrayList<SpreadTypeBean>();

	private String pid, tittle, declareTitle, declareContent, message, code, servicephone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_spread);

		Intent intent = getIntent();
		pid = intent.getStringExtra("pid");
		tittle = intent.getStringExtra("tittle");
		
		initView();
		setListener();
		getphone();
		getspreadtype();
	}

	private void initView() {
		// TODO Auto-generated method stub
		top_tittle = (TextView) findViewById(R.id.top_tittle);
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		top_fabu = (ImageView) findViewById(R.id.top_fabu);
		spread_type_lv = (ListView) findViewById(R.id.spread_list);
		instructionTitle = (TextView) findViewById(R.id.instructionTitle);
		instruction = (TextView) findViewById(R.id.instruction);
		phone_no = (TextView) findViewById(R.id.phone_no);
		phone_ll = (LinearLayout) findViewById(R.id.phone_ll);
	}

	/**
	 * setListener
	 */
	private void setListener() {
		back_btn.setOnClickListener(this);
		top_fabu.setOnClickListener(this);
		phone_ll.setOnClickListener(this);
	}

	// 图标的点击事件
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn :
				/** 返回按钮 **/
				finish();
				break;
			case R.id.top_fabu :
				/** 发布按钮 **/
				break;
			case R.id.phone_ll :
				Utils.dialdialog(mContext, phone_no.getText().toString());
				break;
			default :
				break;
		}
	}

	// 获取客服电弧
	protected void getphone() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(mContext)) {
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("servicephoneresult ----", result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							servicephone = data.getString("servicePhone");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						phonehandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(mContext, "网络异常,请检查网络!");
		}
	}
	
	Handler phonehandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200 && servicephone != null) {
				phone_no.setText(servicephone);
			} else {
				ToastUtil.showToast(mContext, message);
			}
		}
	};
		
	// 获取推广类型
	protected void getspreadtype() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(mContext)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(mContext);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			String url = Constants.SERVER_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("spreadtyperesult ----", result);
						if (spreadTypeList.size() > 0) {
							spreadTypeList.clear();
						}
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							String items = data.getJSONArray("items").toString();
							declareTitle = data.getString("declareTitle").toString();
							declareContent = data.getString("declareContent").toString();

							JSONArray array = new JSONArray(items);
							for (int i = 0; i < array.length(); i++) {
								SpreadTypeBean typeBean = new SpreadTypeBean();
								JSONObject jsonObject = array.getJSONObject(i);
								typeBean.setTitle(jsonObject.optString("title"));
								typeBean.setContent(jsonObject.optString("content"));
								typeBean.setType(jsonObject.optString("type"));
								typeBean.setPrId(jsonObject.optString("prId"));
								spreadTypeList.add(typeBean);
							}
							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						typehandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(mContext, "网络异常,请检查网络!");
		}
	}
	
	Handler typehandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				instructionTitle.setText(declareTitle);
				instruction.setText(declareContent);
				spread_list_adapter = new SpreadTypeListAdapter(SpreadActivity.this, spreadTypeList, pid);
				spread_type_lv.setAdapter(spread_list_adapter);
				spread_list_adapter.notifyDataSetChanged();
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }

        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
            spread_list_adapter.paysucsess("2", "1");
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
            spread_list_adapter.paysucsess("2", "0");
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
//            spread_list_adapter.paysucsess("2", "0");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        // builder.setCustomTitle();
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}

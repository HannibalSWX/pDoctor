package com.owen.pDoctor.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.owen.pDoctor.R;
import com.owen.pDoctor.alipay.PayResult;
import com.owen.pDoctor.alipay.SignUtils;
import com.owen.pDoctor.model.SpreadAmountBean;
import com.owen.pDoctor.model.SpreadTypeBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.util.Constants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ClassName：HomespreadListAdapter
 * Description：推广类型adapter
 * Author ： zhouqiang
 * Date ：2015-2-2 下午7:14:09
 * Copyright (C) 2012-2014 owen
 */
public class SpreadTypeListAdapter extends BaseAdapter {

	private Activity activity;

	private static Dialog dialog;
	
	private LinearLayout ll_zhifubao, ll_bank;
	
	private TextView tv_price;
	
	private ImageView zhifubao_im, bank_im;
	
	private String amount;
	
	private String uid, pid, dayId, pay_type = "1";
	
	// 支付宝提交订单返回
	private String oid, totalPrice, partner, rsa_private, sign, _input_charset, notify_url, out_trade_no, subject, payment_type, seller_id, total_fee, body;
	
	// 银联提交订单返回
	private String tn, respCode, orderId, txnTime;
	
	/*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private final String mMode = "00";
	
	private GridView amount_gv;
	
	private AmountAdapter amount_adapter;
	
	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	private String message, code, tit, content;

	private ArrayList<SpreadTypeBean> typeList = new ArrayList<SpreadTypeBean>();
	
	private ArrayList<SpreadAmountBean> spreadamount = new ArrayList<SpreadAmountBean>();
	
	//商户PID
	public static String PARTNER = "";
	//商户收款账号
	public static String SELLER = "";
	//商户私钥，pkcs8格式
	public static String RSA_PRIVATE = "";
	//支付宝公钥
	public static String RSA_PUBLIC = "";


	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private TextView product_subject, product_description, product_price;
	
	private Button back_btn;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();
				String resultMsg = payResult.getMemo();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
					paysucsess("1", "1");
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(activity, "支付结果确认中", Toast.LENGTH_SHORT).show();
					} else if (TextUtils.equals(resultStatus, "4000")) {
						Toast.makeText(activity, resultMsg, Toast.LENGTH_SHORT).show();
					} else if (TextUtils.equals(resultStatus, "6001")) {
						// 用户主动取消支付
						Toast.makeText(activity, resultMsg, Toast.LENGTH_SHORT).show();
					} else {
						// 其他值就可以判断为支付失败，或者系统返回的错误
						Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
						paysucsess("1", "0");
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(activity, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};
		
	public SpreadTypeListAdapter(Activity act, ArrayList<SpreadTypeBean> list, String pid) {
		this.activity = act;
		this.typeList = list;
		this.pid = pid;
		SharedPreferences sp = activity.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		uid = sp.getString("uid", "");
	}
	
	public class Hodler {
		TextView top_tv, content_tv, buy_top_tv;
	}

	public int getCount() {
		return typeList == null ? 0 : typeList.size();
	}

	public Object getItem(int arg0) {
		return typeList == null ? 0 : typeList.get(arg0);
	}

	public long getItemId(int arg0) {
		return typeList == null ? 0 : arg0;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		final Hodler hodler;
		if (view == null) {
			hodler = new Hodler();
			view = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.spread_listitem, null);
			hodler.top_tv = (TextView) view.findViewById(R.id.top_tv);
			hodler.content_tv = (TextView) view.findViewById(R.id.content_tv);
			hodler.buy_top_tv = (TextView) view.findViewById(R.id.buy_top_tv);
			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}
		
		hodler.top_tv.setText(typeList.get(position).getTitle());
		hodler.content_tv.setText(typeList.get(position).getContent());
		
		hodler.buy_top_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tit = typeList.get(position).getTitle();
				content = typeList.get(position).getContent();
				getamount(typeList.get(position).getPrId());
			}
		});
		return view;
	}

	// 获取天数对应的金额
	protected void getamount(String prId) {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(activity)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(activity);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("prId", prId);
			String url = Constants.SERVER_URL + Constants.SPREAD_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("getamountresult ----", result);
						if (spreadamount.size() > 0) {
							spreadamount.clear();
						}
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							String items = data.getJSONArray("items").toString();

							JSONArray array = new JSONArray(items);
							for (int i = 0; i < array.length(); i++) {
								JSONObject jsonObject = array.getJSONObject(i);
								SpreadAmountBean amountBean = new SpreadAmountBean();
								amountBean.setDayId(jsonObject.optString("dayId"));
								amountBean.setDays(jsonObject.optString("days"));
								amountBean.setAmount(jsonObject.optString("amount"));
								spreadamount.add(amountBean);
							}
							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						dayhandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(activity, "网络异常,请检查网络!");
		}
	}
	
	Handler dayhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				showdialog();
			} else {
				ToastUtil.showToast(activity, message);
			}
			if (progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
	
	protected void showdialog() {
		// TODO Auto-generated method stub
			// TODO Auto-generated method stub
		dialog = new Dialog(activity, R.style.home_dialog);
		dialog.setContentView(R.layout.spread_dialog);
		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL	| Gravity.CENTER_VERTICAL);
		TextView top_tv = (TextView) dialogWindow.findViewById(R.id.top_tv);
		TextView content_tv = (TextView) dialogWindow.findViewById(R.id.content_tv);
		ImageView top_close = (ImageView) dialogWindow.findViewById(R.id.top_close);
		amount_gv = (GridView) dialogWindow.findViewById(R.id.amount_gv);
		tv_price = (TextView) dialogWindow.findViewById(R.id.tv_price);
		ll_zhifubao = (LinearLayout) dialogWindow.findViewById(R.id.ll_zhifubao);
		ll_bank = (LinearLayout) dialogWindow.findViewById(R.id.ll_bank);
		zhifubao_im = (ImageView) dialogWindow.findViewById(R.id.zhifubao_im);
		bank_im = (ImageView) dialogWindow.findViewById(R.id.bank_im);
		TextView tv_fukuan = (TextView) dialogWindow.findViewById(R.id.tv_fukuan);
		top_tv.setText(tit);
		content_tv.setText(content);
		amount_adapter = new AmountAdapter(activity, spreadamount);
		amount_gv.setAdapter(amount_adapter);
		dayId = spreadamount.get(0).getDayId();
		amount_adapter.setSeclection(0);
		getprice();
		pay_type = "1";
		ll_zhifubao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zhifubao_im.setImageDrawable(activity.getResources().getDrawable(R.drawable.point_focus));
				bank_im.setImageDrawable(activity.getResources().getDrawable(R.drawable.point_nornal));
				pay_type = "1";
			}
		});
		
		ll_bank.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bank_im.setImageDrawable(activity.getResources().getDrawable(R.drawable.point_focus));
				zhifubao_im.setImageDrawable(activity.getResources().getDrawable(R.drawable.point_nornal));
				pay_type = "2";
			}
		});
		
		top_close.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		amount_gv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,	int position, long arg3) {
				if (null != spreadamount && spreadamount.size() > 0) {
					dayId = spreadamount.get(position).getDayId();
					getprice();
					amount_adapter.setSeclection(position);
					amount_adapter.notifyDataSetChanged();
				}
			}
		});
		
		// 付款
		tv_fukuan.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (dayId == null || dayId.equals("")) {
					ToastUtil.showToast(activity, "请选择购买天数");
					return;
				}
				if (pay_type != null && !pay_type.equals("")) {
					if (pay_type.equals("1")) {
						pay(pay_type);
					} else {
						bankPay(pay_type);
					}
				} else {
					ToastUtil.showToast(activity, "请选择支付类型");
				}
			}
		});
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		// lp.alpha = 0.7f; // 透明度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}
	
	// 获取价格
	private void getprice() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(activity)) {
//			if (progressDialog == null) {
//				progressDialog = CustomProgressDialog.createDialog(activity);
//			}
//			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("dayId", dayId);
			String url = Constants.SERVER_URL + Constants.TOP_PRICE_URL;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("getamountresult ----", result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							JSONObject obj = data.getJSONObject("obj");
							amount = obj.getString("amount");
							
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
			ToastUtil.showToast(activity, "网络异常,请检查网络!");
		}
	}
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				tv_price.setText(amount);
			} else {
				tv_price.setText("");
				ToastUtil.showToast(activity, message);
			}
			if (progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
	
	// 支付宝付款
	protected void pay(String type) {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(activity)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(activity);
			}
			progressDialog.show();
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("uid", uid);
			reuqestMap.put("pid", pid);
			reuqestMap.put("dayId", dayId);
			reuqestMap.put("type", type);
			String url = Constants.SERVER_URL + Constants.ORDER_SUBMIT;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("payresult ----", result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							JSONObject obj = data.getJSONObject("obj");
							oid = obj.getString("oid");
							totalPrice = obj.getString("totalPrice");
							JSONObject alipayObj = data.getJSONObject("alipayObj");
							partner = alipayObj.getString("partner");
							rsa_private = alipayObj.getString("rsa_private");
							_input_charset = alipayObj.getString("_input_charset");
							notify_url = alipayObj.getString("notify_url");
							out_trade_no = alipayObj.getString("out_trade_no");
							subject = alipayObj.getString("subject");
							payment_type = alipayObj.getString("payment_type");
							seller_id = alipayObj.getString("seller_id");
							total_fee = alipayObj.getString("total_fee");
							body = alipayObj.getString("body");
							
							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						payhandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(activity, "网络异常,请检查网络!");
		}
	}
	
	Handler payhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				if (dialog != null) {
					dialog.dismiss();
				}
				PARTNER = partner;
				RSA_PRIVATE = rsa_private;
				SELLER = seller_id;
				
				// 订单
				String orderInfo = getOrderInfo(tit, body, total_fee);

				// 对订单做RSA 签名
				String sign = sign(orderInfo);
				try {
					// 仅需对sign 做URL编码
					sign = URLEncoder.encode(sign, _input_charset);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				// 完整的符合支付宝参数规范的订单信息
				final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
						+ getSignType();

				Runnable payRunnable = new Runnable() {

					@Override
					public void run() {
						// 构造PayTask 对象
						PayTask alipay = new PayTask(activity);
						// 调用支付接口，获取支付结果
						String result = alipay.pay(payInfo);

						Message msg = new Message();
						msg.what = SDK_PAY_FLAG;
						msg.obj = result;
						mHandler.sendMessage(msg);
					}
				};

				// 必须异步调用
				Thread payThread = new Thread(payRunnable);
				payThread.start();
			} else {
				ToastUtil.showToast(activity, message);
			}
			if (progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
	
	// 银联付款
	protected void bankPay(String type) {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(activity)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(activity);
			}
			progressDialog.show();
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("uid", uid);
			reuqestMap.put("pid", pid);
			reuqestMap.put("dayId", dayId);
			reuqestMap.put("type", type);
			String url = Constants.SERVER_URL + Constants.ORDER_SUBMIT;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("payBankResult ----", result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							JSONObject obj = data.getJSONObject("obj");
							oid = obj.getString("oid");
							totalPrice = obj.getString("totalPrice");
							JSONObject unionpayObj = data.getJSONObject("unionpayObj");
							tn = unionpayObj.getString("tn");
							respCode = unionpayObj.getString("tn");
							orderId = unionpayObj.getString("orderId");
							if (orderId != null && !orderId.equals("")) {
								oid = orderId.substring(8);
							}
							txnTime = unionpayObj.getString("txnTime");
							
							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						payBankHandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(activity, "网络异常,请检查网络!");
		}
	}
	
	Handler payBankHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				if (dialog != null) {
					dialog.dismiss();
				}
				UPPayAssistEx.startPayByJAR(activity, PayActivity.class, null, null,
		                tn, mMode);
			} else {
				ToastUtil.showToast(activity, message);
			}
			if (progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
	
	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(activity);
		String version = payTask.getVersion();
		Toast.makeText(activity, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
	
	// 付款成功后回调
	public void paysucsess(String type, String status) {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(activity)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(activity);
			}
			progressDialog.show();
			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("uid", uid);
			reuqestMap.put("oid", oid);
			reuqestMap.put("type", type);
			reuqestMap.put("status", status);
			String url = Constants.SERVER_URL + Constants.ORDER_PAY;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("payresult ----", result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							
							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						paysuchandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(activity, "网络异常,请检查网络!");
		}
	}
	
	Handler paysuchandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				if (!message.equals("")) {
					ToastUtil.showToast(activity, message);
				}
			} else {
				if (!message.equals("")) {
					ToastUtil.showToast(activity, message);
				}
			}
			if (progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
}

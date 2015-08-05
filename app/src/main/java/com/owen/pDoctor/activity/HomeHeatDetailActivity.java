package com.owen.pDoctor.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.model.AdvertisementDetail;
import com.owen.pDoctor.model.HomeDetailBean;
import com.owen.pDoctor.network.INetCallBack;
import com.owen.pDoctor.network.ZyNet;
import com.owen.pDoctor.share.AccessTokenKeeper;
import com.owen.pDoctor.util.AppConstants;
import com.owen.pDoctor.util.CustomProgressDialog;
import com.owen.pDoctor.util.ImageLoader2;
import com.owen.pDoctor.util.LoadImg;
import com.owen.pDoctor.util.MMAlert;
import com.owen.pDoctor.util.ToastUtil;
import com.owen.pDoctor.util.Utils;
import com.owen.pDoctor.util.LoadImg.ImageDownloadCallBack;
import com.owen.pDoctor.view.PullToRefreshView;
import com.owen.pDoctor.view.PullToRefreshView2;
import com.owen.pDoctor.view.PullToRefreshView2.OnFooterRefreshListener;
import com.owen.pDoctor.view.PullToRefreshView2.OnHeaderRefreshListener;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * ClassName：HomeHeatDetailActivity
 * Description：热门详情
 * Author ： zhouqiang
 * Date ：2015-1-21 下午10:32:55
 * Copyright (C) 2012-2014 owen
 */
public class HomeHeatDetailActivity extends BaseActivity
		implements
			OnClickListener, OnHeaderRefreshListener, OnFooterRefreshListener {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	private TextView tittle, content, name, phone_no, phone_tv;

	private TextView fabu_time, liulan_num, price, address;

	private Button cancel_btn;

	private String tit, pid;

	private Dialog dialog;

	private LinearLayout ads, ads2, ll_xinlang, ll_pengyouquan, ll_weixin, ll_qzone,
			ll_cancel, ll_call;

	private DisplayMetrics metric = new DisplayMetrics();
	
	private int width, height;
	
	private ZyNet zyNet = null;

	private HashMap<String, String> reuqestMap = null;

	private CustomProgressDialog progressDialog = null;

	// private ArrayList<HomeDetailBean> detail = new
	// ArrayList<HomeDetailBean>();

	HomeDetailBean detailBean = new HomeDetailBean();

	List<AdvertisementDetail> adsBean = new ArrayList<AdvertisementDetail>();

	private String message, code, words;
	
	private String uid, imUrl;
	
	private PullToRefreshView2 mRefreshableView;
	
	private PopupWindow popupWindow;
	
	private LayoutInflater inflater;
	
	private int i = 0; // 图片tag值，从0开始
	
	//QZone分享， SHARE_TO_QQ_TYPE_DEFAULT 图文，SHARE_TO_QQ_TYPE_IMAGE 纯图
    private int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
    
    /** 微博分享的接口实例 */
//    private IWeiboShareAPI mWeiboShareAPI;
    
    public static final int SHARE_CLIENT = 1;
    
    public static final int SHARE_ALL_IN_ONE = 2;
    
    private int mShareType = SHARE_ALL_IN_ONE;
    
    // 发送的目标场景，WXSceneSession表示发送到会话
    private static final int WXSceneSession = 0;

    // 发送的目标场景，WXSceneTimeline表示发送朋友圈
    private static final int WXSceneTimeline = 1;
    private IWXAPI api;
    
//	private ImageLoader2 mImageLoader = new ImageLoader2(mContext);
	
//	private LoadImg loadImgMainImg;
	
	private boolean mBusy;
	
	private DisplayImageOptions options;
	
	private Bitmap wxBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_product_detail);

		// 初始化加载图片句柄
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnLoading(R.drawable.ic_launcher)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                        // 设置是否先从内存读取
                        // 设置图片的解码类型//
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300, true, false, false)) // 加载图片时会在内存中加载缓存
                .build();
        
//		loadImgMainImg = new LoadImg(this);
		
		Intent intent = getIntent();
		tit = intent.getStringExtra("tittle");
		pid = intent.getStringExtra("pid");
		
		// 获取屏幕宽高度（像素）设置广告栏高度
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
		
		getcategory();
		
		// 创建微博 SDK 接口实例
//        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, AppConstants.APP_KEY);
//        // 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
//        boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
//        int supportApiLevel = mWeiboShareAPI.getWeiboAppSupportAPI();
//		// 注册到新浪微博
//        mWeiboShareAPI.registerApp();
//        if (savedInstanceState != null) {
//            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
//        }
        
        getwords();
		initView();
		
		// 注册微信sdk
		api = WXAPIFactory.createWXAPI(mContext, AppConstants.Weixin_APP_ID, true);
		api.registerApp(AppConstants.Weixin_APP_ID);
	}

	/**
     * @see {@link Activity#onNewIntent}
     */	
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        
//        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
//        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
//        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
//        mWeiboShareAPI.handleWeiboResponse(intent, this);
//    }
    
    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     * 
     * @param baseRequest 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
//    @Override
//    public void onResponse(BaseResponse baseResp) {
//        switch (baseResp.errCode) {
//        case WBConstants.ErrorCode.ERR_OK:
//            Toast.makeText(this, "share_success", Toast.LENGTH_LONG).show();
//            break;
//        case WBConstants.ErrorCode.ERR_CANCEL:
//            Toast.makeText(this, "share_canceled", Toast.LENGTH_LONG).show();
//            break;
//        case WBConstants.ErrorCode.ERR_FAIL:
//            Toast.makeText(this, "share_failed" + "Error Message: " + baseResp.errMsg, 
//                    Toast.LENGTH_LONG).show();
//            break;
//        }
//    }
    
	private void initView() {
		// TODO Auto-generated method stub
		mRefreshableView = (PullToRefreshView2) findViewById(R.id.refresh_root);
		mRefreshableView.setOnHeaderRefreshListener(this);
		mRefreshableView.setOnFooterRefreshListener(this);
		findViewById(R.id.back_btn).setOnClickListener(this);
		findViewById(R.id.phone_ll).setOnClickListener(this);
		findViewById(R.id.jubao_tv).setOnClickListener(this);
		findViewById(R.id.top_favorite).setOnClickListener(this);
		findViewById(R.id.top_share).setOnClickListener(this);

		tittle = (TextView) findViewById(R.id.tittle);
		ads = (LinearLayout) findViewById(R.id.ads);
		ads2 = (LinearLayout) findViewById(R.id.ads2);
		content = (TextView) findViewById(R.id.content);
		fabu_time = (TextView) findViewById(R.id.fabu_time);
		liulan_num = (TextView) findViewById(R.id.liulan_num);
		price = (TextView) findViewById(R.id.price);
		address = (TextView) findViewById(R.id.address);
		name = (TextView) findViewById(R.id.name);
		phone_no = (TextView) findViewById(R.id.phone_no);

//		tittle.setText(tit);
	}
	
	//实现刷新RefreshListener 中方法
	@Override
	public void onHeaderRefresh(PullToRefreshView2 view) {
		mRefreshableView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (adsBean.size() > 0) {
					adsBean.clear();
				}
				ads.removeAllViews();
				ads2.removeAllViews();
				i = 0;
				
				getcategory();
				initView();
				
				mRefreshableView.onHeaderRefreshComplete();
				String currentTime = Utils.getCurrentTime();
				mRefreshableView.setRefreshTime(currentTime);
			}
		}, 1000);

	}
	
	@Override
	public void onFooterRefresh(PullToRefreshView2 view) {
		mRefreshableView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRefreshableView.onFooterRefreshComplete();
			}
		}, 10);
	}
	
	// 获取分享文字
	private void getwords() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			String url = AppConstants.SERVER_URL + AppConstants.SHAREWORDS;
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					Log.i("words返回结果 ----", "" + result);
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					if (result != null) {
						try {
							JSONObject Jsonresult = new JSONObject(result);
							String code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							words = data.getString("words");

						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					} else {
						ToastUtil.showToast(HomeHeatDetailActivity.this, message);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}
		
	// 商品详情
	private void getcategory() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("pid", pid);
//			if (uid != null && !uid.equals("")) {
//				reuqestMap.put("uid", uid);
//			}
			String url = AppConstants.SERVER_URL + "product_view";
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					if (result != null && !result.equals("")) {
						Log.i("product detail ----", result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");
							JSONObject obj = data.getJSONObject("obj");
							String imgs = obj.getJSONArray("imgArray").toString();
							String[] im = imgs.replace("[", "").replace("]", "").split(",");
							detailBean.setLinkUrl(obj.optString("linkUrl"));
							detailBean.setPid(obj.optString("pid"));
							detailBean.setTitle(obj.optString("title"));
							detailBean.setImg(obj.optString("img"));
							detailBean.setPrice(obj.optString("price"));
							detailBean.setNumber(obj.optString("number"));
							detailBean.setDate(obj.optString("date"));
							detailBean.setContent(obj.optString("content"));
							detailBean.setAddress(obj.optString("address"));
							detailBean.setName(obj.optString("name"));
							detailBean.setTelePhone(obj.optString("telePhone"));
							detailBean.setIscoll(obj.optString("iscoll"));
							if (obj.optString("iscoll").equals("1")) {
								detailBean.setCollectId(obj
										.optString("collectId"));
							}
							
							for(int i = 0; i < im.length;i++){
								AdvertisementDetail advertisement = new AdvertisementDetail();
								String imgUrl = im[i].replace("\"", "").replace("\\", "");
								advertisement.setImageUrl(imgUrl);
								adsBean.add(advertisement);
							}
							
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
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				tittle.setText(detailBean.getTitle());
				content.setText(detailBean.getContent());
				fabu_time.setText(detailBean.getDate());
				liulan_num.setText(detailBean.getNumber());
				price.setText(detailBean.getPrice());
				address.setText(detailBean.getAddress());
				name.setText(detailBean.getName());
				phone_no.setText(detailBean.getTelePhone());

//				SlideView slideView = new SlideView(HomeHeatDetailActivity.this, "1");
//				slideView.setAdvertisementsDetail(adsBean);
//				ads.addView(slideView.getView());
				
				//设置详情下方图片
//				new Thread(setbitmap).start();
				// 显示顶部图片
				getTopImageView(detailBean.getImg());
				// 显示底部图片
				if (adsBean.size() > 0) {
					for (int i = 0; i < adsBean.size(); i++) {
						if (!adsBean.get(i).getImageUrl().equals("")) {
							getImageView(adsBean.get(i).getImageUrl(), i);
						}
					}
				}
			} else {
				ToastUtil.showToast(mContext, message);
//				String picture_path = "http://ww4.sinaimg.cn/large/6cbb8645gw1ectlfyppjxj21kw0qvjwv.jpg";
//				AdvertisementDetail advertisement = new AdvertisementDetail();
//				advertisement.setAdvertUrl("url");
//				advertisement.setImageUrl(picture_path);
//				adsBean.add(advertisement);
//				SlideView slideView = new SlideView(HomeHeatDetailActivity.this, "1");
//				slideView.setAdvertisementsDetail(adsBean);
//				ads.addView(slideView.getView());
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};
	
	// 加载图片 从获取的第二张开始
	private Runnable setbitmap = new Runnable() {
		@Override
		public void run() {
			getTopImageView(detailBean.getImg()); // 显示顶部图片
			if (adsBean.size() > 0) {
				for (int i = 0; i < adsBean.size(); i++) {
					if (!adsBean.get(i).getImageUrl().equals("")) {
						getImageView(adsBean.get(i).getImageUrl(), i); // 显示底部图片
					}
				}
			}
		}
	};

	// getImageView
	private void getTopImageView(final String path) {
		final View view = getLayoutInflater().inflate(R.layout.home_detail_img_item, null);
		final ImageView imageView = (ImageView) view.findViewById(R.id.home_detail_item_image);
		if (path == null || "".equals(path)) {
//				imageView.setImageResource(R.drawable.ic_launcher);
		} else {
			// 需要显示的网络图片
//			if (!mBusy) {
//	             mImageLoader.DisplayImage(path, imageView, false);
//			 } else {
//	             mImageLoader.DisplayImage(path, imageView, true);
//			 }
//			Bitmap bitMain = loadImgMainImg.loadImage(imageView, path,
//					new ImageDownloadCallBack() {
//						@Override
//						public void onImageDownload(ImageView imageView,
//								Bitmap bitmap) {
//							imageView.setImageBitmap(bitmap);
//						}
//					});
//			if (bitMain != null) {
//				imageView.setImageBitmap(bitMain);
//			}
			
			ImageLoader.getInstance().loadImage(path, options, new ImageLoadingListener() {
	            @Override
	            public void onLoadingStarted(String s, View view) {

	            }

	            @Override
	            public void onLoadingFailed(String s, View view, FailReason failReason) {
//	                progressBar.setVisibility(View.GONE);
	            }

	            @Override
	            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
	                if (bitmap != null)
	                	imageView.setImageBitmap(bitmap);
	                wxBitmap = bitmap;
//	                progressBar.setVisibility(View.GONE);
	            }

	            @Override
	            public void onLoadingCancelled(String s, View view) {
//	                progressBar.setVisibility(View.GONE);
	            }
	        });
		}
		
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeHeatDetailActivity.this, ImageShowerActivity.class);
				intent.putExtra("path", path);
				startActivity(intent);
			}
		});
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LayoutParams params2 = (LayoutParams) ads.getLayoutParams();
				params2.width = LayoutParams.MATCH_PARENT;
				params2.height = 5 * width / 8;
				imageView.setLayoutParams(params2);
				ads.setLayoutParams(params2);
				ads.addView(view);
			}
		});
	}
		
	// getImageView
	private void getImageView(String path, final int pos) {
		final int j = i++;
		final View view = getLayoutInflater().inflate(R.layout.home_detail_img_item, null);
		final ImageView imageView = (ImageView) view.findViewById(R.id.home_detail_item_image);
		
//		imageView.setImageResource(R.drawable.ic_launcher);
		imageView.setTag(j);// 给图片设置一个tag，主要为listPath的下标所用
		if (path == null || "".equals(path)) {
//			imageView.setImageResource(R.drawable.ic_launcher);
		} else {
			// 需要显示的网络图片
//			if (!mBusy) {
//	             mImageLoader.DisplayImage(path, imageView, false);
//			 } else {
//	             mImageLoader.DisplayImage(path, imageView, true);
//			 }
//			Bitmap bitMain = loadImgMainImg.loadImage(imageView, path,
//					new ImageDownloadCallBack() {
//						@Override
//						public void onImageDownload(ImageView imageView,
//								Bitmap bitmap) {
//							imageView.setImageBitmap(bitmap);
//						}
//					});
//			if (bitMain != null) {
//				imageView.setImageBitmap(bitMain);
//			}
			ImageLoader.getInstance().loadImage(path, options, new ImageLoadingListener() {
	            @Override
	            public void onLoadingStarted(String s, View view) {

	            }

	            @Override
	            public void onLoadingFailed(String s, View view, FailReason failReason) {
//	                progressBar.setVisibility(View.GONE);
	            }

	            @Override
	            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
	                if (bitmap != null)
	                	imageView.setImageBitmap(bitmap);
//	                progressBar.setVisibility(View.GONE);
	            }

	            @Override
	            public void onLoadingCancelled(String s, View view) {
//	                progressBar.setVisibility(View.GONE);
	            }
	        });
		}
		
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int k = Integer.parseInt(imageView.getTag().toString());
				Intent intent = new Intent(HomeHeatDetailActivity.this, ImageShowerActivity.class);
				intent.putExtra("pos", pos);
				intent.putExtra("adsBean", (Serializable)adsBean);
				startActivity(intent);
			}
		});
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LayoutParams params = (LayoutParams) imageView.getLayoutParams();
				params.width = LayoutParams.MATCH_PARENT;
//				params.height = LayoutParams.WRAP_CONTENT;
				params.height = 5 * width / 8;
				params.setMargins(10, 0, 10, 8);
				imageView.setLayoutParams(params);
//				ads2.setLayoutParams(params);
				
//				ViewGroup.LayoutParams lp = imageView.getLayoutParams();
//				lp.width = width;
//				lp.height = 5 * width / 8;
//				imageView.setLayoutParams(lp);
			
				ads2.addView(view);
			}
		});
	}
	
	// 根据路径获取图片
	private Bitmap getImageBitmap(String path) throws FileNotFoundException,
			IOException {
		Bitmap bmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
//		opts.inSampleSize = ImageTools.computeSampleSize(opts, -1, 150 * 150);// 得到缩略图
		opts.inJustDecodeBounds = false;
		try {
			bmp = BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError e) {
		}
		return bmp;
	}
	
	OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				mBusy = true;
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				mBusy = false;
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				mBusy = false;
				break;
			default:
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};
	
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn :
				this.finish();
				break;

			case R.id.top_favorite :
				SharedPreferences sp = getSharedPreferences("userInfo",	Context.MODE_PRIVATE);
				uid = sp.getString("uid", "");
				if (uid.equals("") || uid == null) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
				} else {
					addfavorite();
				}
				break;

			case R.id.top_share :
				sharedialog();
				break;

			case R.id.jubao_tv :
				jubao();
				break;

			case R.id.phone_ll :
				Utils.dialdialog(mContext, phone_no.getText().toString());
				break;
			default :
				break;
		}
	}

	// 添加收藏
	private void addfavorite() {
		// TODO Auto-generated method stub
		if (Utils.isNetConn(this)) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(this);
			}
			progressDialog.show();

			zyNet = new ZyNet();
			reuqestMap = new HashMap<String, String>();
			reuqestMap.put("uid", uid);
			reuqestMap.put("pid", pid);
			String url = AppConstants.SERVER_URL + "collect_coll";
			zyNet.closePost();
			zyNet.startPost(url, reuqestMap, new INetCallBack() {
				@Override
				public void onComplete(String result) {
					Message msg = new Message();
					String imgUrl = "";
					if (result != null && !result.equals("")) {
						Log.i("advertisment ----", result);
						try {
							JSONObject Jsonresult = new JSONObject(result);
							String code = Jsonresult.getString("code");
							message = Jsonresult.getString("msg");
							JSONObject data = Jsonresult.getJSONObject("data");

							msg.what = Integer.parseInt(code);
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						addhandler.sendMessage(msg);
					}
				}
			});
		} else {
			ToastUtil.showToast(this, "网络异常,请检查网络!");
		}
	}

	Handler addhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				ToastUtil.showToast(mContext, message);
			} else {
				ToastUtil.showToast(mContext, message);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	};

	// 分享
	private void sharedialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(this, R.style.home_dialog);
		dialog.setContentView(R.layout.home_dialog);

		dialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.BOTTOM);
		ll_xinlang = (LinearLayout) dialogWindow.findViewById(R.id.ll_xinlang);
		ll_pengyouquan = (LinearLayout) dialogWindow
				.findViewById(R.id.ll_pengyouquan);
		ll_weixin = (LinearLayout) dialogWindow.findViewById(R.id.ll_weixin);
		ll_qzone = (LinearLayout) dialogWindow.findViewById(R.id.ll_qq);
		cancel_btn = (Button) dialogWindow.findViewById(R.id.cancel_btn);
		ll_xinlang.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				sharesina(); // 分享到新浪微博
				dialog.dismiss();
			}
		});
		ll_pengyouquan.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sendWxUrl(WXSceneTimeline); // 分享到微信朋友圈
				dialog.dismiss();
			}
		});
		ll_weixin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sendWxUrl(WXSceneSession); // 分享到微信好友
				dialog.dismiss();
			}
		});
		ll_qzone.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Bundle params = new Bundle();
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, detailBean.getTitle());
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, words);
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, detailBean.getLinkUrl());
                if (shareType == QzoneShare.SHARE_TO_QZONE_TYPE_APP) {
                    //app分享不支持传目标链接
                    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");
                }
                // 支持传多个imageUrl
                ArrayList<String> imageUrls = new ArrayList<String>();
//                for (int i = 0; i < 5; i++) {
//                    imageUrls.add(adsBean.get(0).getImageUrl());
//                }
                // http://imgsrc.baidu.com/forum/w%3D72/sign=3098278a8318367aad897ddf2f73bd0e/bdadc409b3de9c825191132c6981800a1bd84396.jpg
                imageUrls.add(detailBean.getImg());
                // String imageUrl = "XXX";
                // params.putString(Tencent.SHARE_TO_QQ_IMAGE_URL, imageUrl);
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
                doShareToQzone(params);
				dialog.dismiss();
			}
		});
		cancel_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// lp.x = 100; // 新位置X坐标
		// lp.y = Gravity.BOTTOM; // 新位置Y坐标
		lp.width = LayoutParams.MATCH_PARENT; // 宽度
		lp.height = LayoutParams.WRAP_CONTENT; // 高度
		// lp.alpha = 0.7f; // 透明度
		// 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
		// dialog.onWindowAttributesChanged(lp);
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 分享到新浪微博
//	protected void sharesina() {
//		// TODO Auto-generated method stub
//		if (mShareType == SHARE_CLIENT) {
//            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
//                int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
//                if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
////                    sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
//                	sendMultiMessage(true, false, false, false, false, false);
//                } else {
//                    sendSingleMessage(true, false, false, false, false/*, hasVoice*/);
//                }
//            } else {
//                Toast.makeText(this, "not_support_api", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else if (mShareType == SHARE_ALL_IN_ONE) {
//            sendMultiMessage(true, false, false, false, false, false);
//        }
//	}

	/**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
//    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
//            boolean hasMusic, boolean hasVideo, boolean hasVoice) {
//        
//        // 1. 初始化微博的分享消息
//        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//        if (hasText) {
//            weiboMessage.textObject = getTextObj();
//        }
//        
//        if (hasImage) {
//            weiboMessage.imageObject = getImageObj();
//        }
//        
//        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
//        if (hasWebpage) {
//            weiboMessage.mediaObject = getWebpageObj();
//        }
//        if (hasMusic) {
//            weiboMessage.mediaObject = getMusicObj();
//        }
//        if (hasVideo) {
//            weiboMessage.mediaObject = getVideoObj();
//        }
//        if (hasVoice) {
//            weiboMessage.mediaObject = getVoiceObj();
//        }
//        
//        // 2. 初始化从第三方到微博的消息请求
//        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//        // 用transaction唯一标识一个请求
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.multiMessage = weiboMessage;
//        
//        // 3. 发送请求消息到微博，唤起微博分享界面
//        if (mShareType == SHARE_CLIENT) {
//            mWeiboShareAPI.sendRequest(HomeHeatDetailActivity.this, request);
//        }
//        else if (mShareType == SHARE_ALL_IN_ONE) {
//            AuthInfo authInfo = new AuthInfo(this, AppConstants.APP_KEY, AppConstants.REDIRECT_URL, AppConstants.SCOPE);
//            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
//            String token = "";
//            if (accessToken != null) {
//                token = accessToken.getToken();
//            }
//            mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {
//                
//                @Override
//                public void onWeiboException( WeiboException arg0 ) {
//                }
//                
//                @Override
//                public void onComplete( Bundle bundle ) {
//                    // TODO Auto-generated method stub
//                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
//                    AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
//                    Toast.makeText(getApplicationContext(), "onAuthorizeComplete token = " + newToken.getToken(), 0).show();
//                }
//                
//                @Override
//                public void onCancel() {
//                	ToastUtil.showToast(HomeHeatDetailActivity.this, "cancel");
//                }
//            });
//        }
//    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     */
//    private void sendSingleMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
//            boolean hasMusic, boolean hasVideo/*, boolean hasVoice*/) {
//        
//        // 1. 初始化微博的分享消息
//        // 用户可以分享文本、图片、网页、音乐、视频中的一种
//        WeiboMessage weiboMessage = new WeiboMessage();
//        if (hasText) {
//            weiboMessage.mediaObject = getTextObj();
//        }
//        if (hasImage) {
//            weiboMessage.mediaObject = getImageObj();
//        }
//        if (hasWebpage) {
//            weiboMessage.mediaObject = getWebpageObj();
//        }
//        if (hasMusic) {
//            weiboMessage.mediaObject = getMusicObj();
//        }
//        if (hasVideo) {
//            weiboMessage.mediaObject = getVideoObj();
//        }
//        /*if (hasVoice) {
//            weiboMessage.mediaObject = getVoiceObj();
//        }*/
//        
//        // 2. 初始化从第三方到微博的消息请求
//        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
//        // 用transaction唯一标识一个请求
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.message = weiboMessage;
//        
//        // 3. 发送请求消息到微博，唤起微博分享界面
//        mWeiboShareAPI.sendRequest(HomeHeatDetailActivity.this, request);
//    }

    /**
     * 获取分享的文本模板。
     * 
     * @return 分享的文本模板
     */
    private String getSharedText() {
//        int formatId = R.string.weibosdk_demo_share_text_template;
//        String format = getString(formatId);
//        String demoUrl = getString(R.string.weibosdk_demo_app_url);
//        format = getString(R.string.weibosdk_demo_share_text_template);
        String format = words;
        return format;
    }

    /**
     * 创建文本消息对象。
     * 
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getSharedText();
        return textObject;
    }

    /**
     * 创建图片消息对象。
     * 
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) mImageView.getDrawable();
//        imageObject.setImageObject(bitmapDrawable.getBitmap());
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     * 
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
//        mediaObject.identify = Utility.generateGUID();
//        mediaObject.title = mShareWebPageView.getTitle();
//        mediaObject.description = mShareWebPageView.getShareDesc();
//        
//        // 设置 Bitmap 类型的图片到视频对象里
//        mediaObject.setThumbImage(mShareWebPageView.getThumbBitmap());
//        mediaObject.actionUrl = mShareWebPageView.getShareUrl();
//        mediaObject.defaultText = "Webpage 默认文案";
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象。
     * 
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj() {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
//        musicObject.identify = Utility.generateGUID();
//        musicObject.title = mShareMusicView.getTitle();
//        musicObject.description = mShareMusicView.getShareDesc();
//        
//        // 设置 Bitmap 类型的图片到视频对象里
//        musicObject.setThumbImage(mShareMusicView.getThumbBitmap());
//        musicObject.actionUrl = mShareMusicView.getShareUrl();
//        musicObject.dataUrl = "www.weibo.com";
//        musicObject.dataHdUrl = "www.weibo.com";
//        musicObject.duration = 10;
//        musicObject.defaultText = "Music 默认文案";
        return musicObject;
    }

    /**
     * 创建多媒体（视频）消息对象。
     * 
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getVideoObj() {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
//        videoObject.identify = Utility.generateGUID();
//        videoObject.title = mShareVideoView.getTitle();
//        videoObject.description = mShareVideoView.getShareDesc();
//        
//        // 设置 Bitmap 类型的图片到视频对象里
//        videoObject.setThumbImage(mShareVideoView.getThumbBitmap());
//        videoObject.actionUrl = mShareVideoView.getShareUrl();
//        videoObject.dataUrl = "www.weibo.com";
//        videoObject.dataHdUrl = "www.weibo.com";
//        videoObject.duration = 10;
//        videoObject.defaultText = "Vedio 默认文案";
        return videoObject;
    }

    /**
     * 创建多媒体（音频）消息对象。
     * 
     * @return 多媒体（音乐）消息对象。
     */
    private VoiceObject getVoiceObj() {
        // 创建媒体消息
        VoiceObject voiceObject = new VoiceObject();
//        voiceObject.identify = Utility.generateGUID();
//        voiceObject.title = mShareVoiceView.getTitle();
//        voiceObject.description = mShareVoiceView.getShareDesc();
//        
//        // 设置 Bitmap 类型的图片到视频对象里
//        voiceObject.setThumbImage(mShareVoiceView.getThumbBitmap());
//        voiceObject.actionUrl = mShareVoiceView.getShareUrl();
//        voiceObject.dataUrl = "www.weibo.com";
//        voiceObject.dataHdUrl = "www.weibo.com";
//        voiceObject.duration = 10;
//        voiceObject.defaultText = "Voice 默认文案";
        return voiceObject;
    }
    
    /**
     * 此方法是写的分享链接，如果朋友想要分享【图片，文字，等其他可下载微信官方Demo，SendToWXActivity.java类中写的很清楚】
     * @param scene 0代表好友   1代表朋友圈
     */
    public void sendWxUrl(int scene){
        WXWebpageObject webpage = new WXWebpageObject();
        if (!detailBean.getLinkUrl().equals("")) {
        	webpage.webpageUrl = detailBean.getLinkUrl();
        } else {
        	webpage.webpageUrl = mContext.getResources().getString(R.string.share_url);
        }
        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = mContext.getResources().getString(R.string.share_title);
        msg.title = detailBean.getTitle();
//        msg.description = mContext.getResources().getString(R.string.share_content);
        msg.description = words;
        Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        msg.thumbData = getBitmapBytes(wxBitmap, false);
        
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        if (scene == 0) {
            // 分享到微信
            req.scene = SendMessageToWX.Req.WXSceneSession; 
        } else {
            // 分享到微信朋友圈
            req.scene = SendMessageToWX.Req.WXSceneTimeline; 
        }
        api.sendReq(req);
    }
    
 // 需要对图片进行处理，否则微信会在log中输出thumbData检查错误
    private static byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
        int i;
        int j;
        int size;
        float sx, sy;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            i = bitmap.getWidth();
            j = bitmap.getWidth();
        } else {
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
        size = bitmap.getWidth() / bitmap.getHeight();
        sx = (float)80 / bitmap.getWidth();
        sy = (float)80 / bitmap.getHeight();
        Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);
        while (true) {
        	Matrix matrix = new Matrix();
        	matrix.postScale(sx, sy);
            localCanvas.drawBitmap(bitmap, matrix, null);
            if (paramBoolean)
                bitmap.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
//                F.out(e);
            	e.printStackTrace();
            }
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
    }
    
    // 过滤发送内容格式
    private String buildTransaction(final String type) {
            return (type == null) ? String.valueOf(System.currentTimeMillis())
                            : type + System.currentTimeMillis();
    }

	@Override
    protected void onDestroy() {
        super.onDestroy();
        if (MainActivity.mTencent != null) {
            MainActivity.mTencent.releaseResource();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QZONE_SHARE) {
        	if (resultCode == Constants.ACTIVITY_OK) {
        		Tencent.handleResultData(data, qZoneShareListener);
        	}
        } else {
        	String path = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.getData() != null) {
                    // 根据返回的URI获取对应的SQLite信息
                    Uri uri = data.getData();
                    final String[] proj = {
                            MediaStore.Images.Media.DATA
                    };
                    Cursor cursor = null;
                    try {
                    	cursor = this.getContentResolver().query(uri, proj, null, null, null);
                    	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    	cursor.moveToFirst();
                    	path = cursor.getString(column_index);
                    } finally {
                    	if (cursor != null) {
                    		cursor.close();
                    		cursor = null;
                    	}
                    }
                }
            }
            if (path != null) {
            	// 这里很奇葩的方式, 将获取到的值赋值给相应的EditText, 竟然能对应上
//            	EditText editText = (EditText)mImageContainerLayout.findViewById(requestCode + 1000);
//            	editText.setText(path);
            } else {
//                showToast("请重新选择图片");
            }
        }
    }

    private static final void startPickLocaleImage(Activity activity, int requestId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(
                Intent.createChooser(intent, activity.getString(R.string.local_img)), requestId);
    }
    
    IUiListener qZoneShareListener = new IUiListener() {

        @Override
        public void onCancel() {
            ToastUtil.showToast(HomeHeatDetailActivity.this, "cancel");
        }

        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
        	ToastUtil.showToast(HomeHeatDetailActivity.this, "onError: " + e.errorMessage);
        }

		@Override
		public void onComplete(Object response) {
			// TODO Auto-generated method stub
			ToastUtil.showToast(HomeHeatDetailActivity.this, "onComplete: " + response.toString());
		}

    };
    
    /**
     * 用异步方式启动分享
     * @param params
     */
    private void doShareToQzone(final Bundle params) {
        final Activity activity = HomeHeatDetailActivity.this;
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
            	MainActivity.mTencent.shareToQzone(activity, params, qZoneShareListener);
            }
        }).start();
    }
    
	// 举报
	private void jubao() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}

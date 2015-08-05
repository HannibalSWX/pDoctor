package com.owen.pDoctor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.DetailAdsAdapter;
import com.owen.pDoctor.model.AdvertisementDetail;
import com.owen.pDoctor.util.ImageLoader2;
import com.owen.pDoctor.util.LoadImg;
import com.owen.pDoctor.util.LoadImg.ImageDownloadCallBack;
import com.owen.pDoctor.view.MyGalleryView;
import com.owen.pDoctor.view.ZoomImageView;
import com.owen.pDoctor.view.ZoomableImageView;

public class ImageShowerActivity extends Activity {

	private int pos;
	
	private String path;
	
	private ZoomableImageView mImageView;
	
	private ImageLoader2 mImageLoader = new ImageLoader2(this);
	
	private ViewPager ads_banner;
	
	private DetailAdsAdapter adsadapter;
	
	List<AdvertisementDetail> adsBean = new ArrayList<AdvertisementDetail>();
	
	//屏幕的宽度
	public static int screenWidth;
	//屏幕的高度
	public static int screenHeight;
	
//	private LoadImg loadImgMainImg;
	
	public static ImageShowerActivity mActivity;
	
	private TextView index;
	
	private LinearLayout index_layout;
	
	private FrameLayout ads_fl;
	
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_popupwindow);
		
		mActivity = this;
		
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
		
		//获取屏幕的大小
        screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		
		Intent intent = getIntent();
		if (intent.getStringExtra("path") != null) {
			path = intent.getStringExtra("path");
		}
		pos = intent.getIntExtra("pos", -1);
		if ((List<AdvertisementDetail>)intent.getSerializableExtra("adsBean") != null) {
			adsBean = (List<AdvertisementDetail>)intent.getSerializableExtra("adsBean");
		}
		
		ads_fl = (FrameLayout) findViewById(R.id.ads_fl);
		index_layout = (LinearLayout) findViewById(R.id.index_layout);
		index = (TextView) findViewById(R.id.index);
		ads_banner = (ViewPager) findViewById(R.id.ads_banner);
		mImageView = (ZoomableImageView) findViewById(R.id.pop_detail_item_image);
		
		if (path != null) {
			ads_fl.setVisibility(View.GONE);
			mImageView.setVisibility(View.VISIBLE);
			
//			LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
//			params.width = LayoutParams.MATCH_PARENT;
//			params.height = LayoutParams.MATCH_PARENT;
//			mImageView.setLayoutParams(params);
//			mImageView.setScaleType(ImageView.ScaleType.MATRIX);
			// 需要显示的网络图片
//			mImageLoader.DisplayImage(path, imageView, false);
			
//			Bitmap bitMain = loadImgMainImg.loadImage(mImageView, path,
//					new ImageDownloadCallBack() {
//						@Override
//						public void onImageDownload(ImageView imageView,
//								Bitmap bitmap) {
//							mImageView.setBitmap(bitmap);
//						}
//					});
//			if (bitMain != null) {
//				mImageView.setBitmap(bitMain);
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
	                	mImageView.setImageBitmap(bitmap);
//	                progressBar.setVisibility(View.GONE);
	            }

	            @Override
	            public void onLoadingCancelled(String s, View view) {
//	                progressBar.setVisibility(View.GONE);
	            }
	        });
		} else {
			ads_fl.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.GONE);
			initView();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
//		ads_banner.setVerticalFadingEdgeEnabled(false);// 取消竖直渐变边框
//		ads_banner.setHorizontalFadingEdgeEnabled(false);// 取消水平渐变边框
//		adsadapter = new DetailAdsAdapter(ImageShowerActivity.this, adsBean, ads_banner);
//        ads_banner.setAdapter(adsadapter);
//        ads_banner.setSpacing(0);
//        ads_banner.setSelection(pos);
//        
//        // 显示页码
//        ads_banner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            int i;
//            @Override
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                // TODO Auto-generated method stub
//            	index_layout.removeAllViews();
//                for (i = 0; i < adsBean.size(); i++) {
//                    if (i == arg2 % adsBean.size()) {
//                        index_layout.removeView(index);
//                        index_layout.addView(index);
//                        index.setText((arg2 % adsBean.size() + 1)  + " / " + adsBean.size());
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//                // TODO Auto-generated method stub
//            }
//        });
		ads_banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
            	index.setText((pos + 1) + "/" + adsBean.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
		
        index.setText((pos + 1) + "/" + adsBean.size());
        DetailAdsAdapter adapter = new DetailAdsAdapter(ImageShowerActivity.this, adsBean, ads_banner);
        ads_banner.setAdapter(adapter);
        ads_banner.setCurrentItem(pos);
	}

	float beforeLenght = 0.0f; // 两触点距离
	float afterLenght = 0.0f; // 两触点距离
	boolean isScale = false;
	float currentScale = 1.0f;// 当前图片的缩放比率

	private class GalleryChangeListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			currentScale = 1.0f;
			isScale = false;
			beforeLenght = 0.0f;
			afterLenght = 0.0f;

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//
//		// Log.i("","touched---------------");
//		switch (event.getAction() & MotionEvent.ACTION_MASK) {
//		case MotionEvent.ACTION_POINTER_DOWN:// 多点缩放
//			beforeLenght = spacing(event);
//			if (beforeLenght > 5f) {
//				isScale = true;
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if (isScale) {
//				afterLenght = spacing(event);
//				if (afterLenght < 5f)
//					break;
//				float gapLenght = afterLenght - beforeLenght;
//				if (gapLenght == 0) {
//					break;
//				} else if (Math.abs(gapLenght) > 5f) {
//					// FrameLayout.LayoutParams params =
//					// (FrameLayout.LayoutParams) gallery.getLayoutParams();
//					float scaleRate = gapLenght / screenHeight;// 缩放比例
//					// Log.i("",
//					// "scaleRate："+scaleRate+" currentScale:"+currentScale);
//					// Log.i("", "缩放比例：" +
//					// scaleRate+" 当前图片的缩放比例："+currentScale);
//					// params.height=(int)(800*(scaleRate+1));
//					// params.width=(int)(480*(scaleRate+1));
//					// params.height = 400;
//					// params.width = 300;
//					// gallery.getChildAt(0).setLayoutParams(new
//					// Gallery.LayoutParams(300, 300));
//					Animation myAnimation_Scale = new ScaleAnimation(currentScale, currentScale + scaleRate, currentScale, currentScale + scaleRate, Animation.RELATIVE_TO_SELF, 0.5f,
//							Animation.RELATIVE_TO_SELF, 0.5f);
//					// Animation myAnimation_Scale = new
//					// ScaleAnimation(currentScale, 1+scaleRate, currentScale,
//					// 1+scaleRate);
//					myAnimation_Scale.setDuration(100);
//					myAnimation_Scale.setFillAfter(true);
//					myAnimation_Scale.setFillEnabled(true);
//					// gallery.getChildAt(0).startAnimation(myAnimation_Scale);
//
//					// gallery.startAnimation(myAnimation_Scale);
//					currentScale = currentScale + scaleRate;
//					// gallery.getSelectedView().setLayoutParams(new
//					// Gallery.LayoutParams((int)(480), (int)(800)));
//					// Log.i("",
//					// "===========:::"+gallery.getSelectedView().getLayoutParams().height);
//					// gallery.getSelectedView().getLayoutParams().height=(int)(800*(currentScale));
//					// gallery.getSelectedView().getLayoutParams().width=(int)(480*(currentScale));
//					ads_banner.getSelectedView().setLayoutParams(new Gallery.LayoutParams((int) (screenWidth * (currentScale)), (int) (screenHeight * (currentScale))));
//					// gallery.getSelectedView().setLayoutParams(new
//					// Gallery.LayoutParams((int)(320*(scaleRate+1)),
//					// (int)(480*(scaleRate+1))));
//					// gallery.getSelectedView().startAnimation(myAnimation_Scale);
//					// isScale = false;
//					beforeLenght = afterLenght;
//				}
//				return true;
//			}
//			break;
//		case MotionEvent.ACTION_POINTER_UP:
//			isScale = false;
//			break;
//		}
//
//		return false;
//	}

	/**
	 * 就算两点间的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
}
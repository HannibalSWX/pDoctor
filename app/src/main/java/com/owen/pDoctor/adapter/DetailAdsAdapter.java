package com.owen.pDoctor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.owen.pDoctor.R;
import com.owen.pDoctor.activity.HomeHeatDetailActivity;
import com.owen.pDoctor.model.AdvertisementDetail;
import com.owen.pDoctor.util.LoadImg;
import com.owen.pDoctor.util.LoadImg.ImageDownloadCallBack;
import com.owen.pDoctor.view.MyGalleryView;
import com.owen.pDoctor.view.MyImageView;
import com.owen.pDoctor.view.ZoomableImageView;

/**
 * Created by zq on 15-01-13.
 */
public class DetailAdsAdapter extends PagerAdapter {

	private Context mContext;

	private List<AdvertisementDetail> adsBean = new ArrayList<AdvertisementDetail>();

	private String imagePath = "";
	
	private ViewPager viewPage;
	
//	private LoadImg loadImgMainImg;
	
//	MyImageView mView;

	private DisplayImageOptions options;
	
	public DetailAdsAdapter(Context context, List<AdvertisementDetail> ads, ViewPager vp) {
		mContext = context;
		adsBean = ads;
		viewPage = vp;
//		loadImgMainImg = new LoadImg(mContext);
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
//		mView = new MyImageView(mContext);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);// 删除页卡
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		if (adsBean.size() > 0) {
			imagePath = adsBean.get(position % adsBean.size()).getImageUrl();
		}
		
		View v = LayoutInflater.from(mContext).inflate(R.layout.image_show_item, null);
		final ZoomableImageView iv = (ZoomableImageView) v.findViewById(R.id.gallery_image);
//		final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.pb_loading);
		// 加载图片
//        if (!path.contains(AppInterface.APP_SERVER_ADDR)) {
//            path = AppInterface.APP_SERVER_ADDR + path;
//        }
		ImageLoader.getInstance().loadImage(imagePath, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null)
                    iv.setImageBitmap(bitmap);
//                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
//                progressBar.setVisibility(View.GONE);
            }
        });
		container.addView(v);
		return v;
	}

	@Override
	public int getCount() {
		return adsBean.size();// 返回页卡的数量
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
//	@Override
//	public int getCount() {
//		// TODO Auto-generated method stub
//		return Integer.MAX_VALUE;
//	}
//
//	@Override
//	public Object getItem(int position) {
//		// TODO Auto-generated method stub
//		return position;
//	}
//
//	@Override
//	public long getItemId(int position) {
//		// TODO Auto-generated method stub
//		return position;
//	}
//
//	public View getView(int position, View view, ViewGroup arg2) {
//		if (adsBean.size() > 0) {
//			imagePath = adsBean.get(position % adsBean.size()).getImageUrl();
//		}
//		
//		View v = LayoutInflater.from(mContext).inflate(R.layout.image_show_item, null);
//		final ZoomableImageView iv = (ZoomableImageView) v.findViewById(R.id.gallery_image);
//		
//		// // 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
////		hodler.items_im.setImageResource(R.drawable.ic_launcher);
////		hodler.items_im.setScaleType(ImageView.ScaleType.FIT_XY);
//		
////		Bitmap bitMain = loadImgMainImg.loadImage(mView, imagePath,
////				new ImageDownloadCallBack() {
////					@Override
////					public void onImageDownload(ImageView imageView, Bitmap bitmap) {
//////						hodler.items_im.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//////						hodler.items_im.setBitmap(bitmap);
////						mView = new MyImageView(mContext, bitmap.getWidth(), bitmap.getHeight());
////						mView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
////						mView.setImageBitmap(bitmap);
////						notifyDataSetChanged();
////					}
////				});
////		if (bitMain != null) {
//////			hodler.items_im.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//////			hodler.items_im.setBitmap(bitMain);
////			mView = new MyImageView(mContext, bitMain.getWidth(), bitMain.getHeight());
////			mView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
////			mView.setImageBitmap(bitMain);
////		}
//
//		ImageLoader.getInstance().loadImage(imagePath, options, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String s, View view) {
//
//            }
//
//            @Override
//            public void onLoadingFailed(String s, View view, FailReason failReason) {
////                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                if (bitmap != null)
//                    iv.setImageBitmap(bitmap);
////                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingCancelled(String s, View view) {
////                progressBar.setVisibility(View.GONE);
//            }
//        });
//		return v;
//	}

}

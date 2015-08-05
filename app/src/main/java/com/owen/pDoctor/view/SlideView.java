package com.owen.pDoctor.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.owen.pDoctor.R;
import com.owen.pDoctor.activity.HomeHeatDetailActivity;
import com.owen.pDoctor.activity.WebViewActivity;
import com.owen.pDoctor.model.Advertisement;
import com.owen.pDoctor.model.AdvertisementDetail;

public class SlideView {

	private ViewGroup pageView;

	private Context context;

	private ViewPager viewPager;

	private ImageView[] mImageCircleViews = null;
	private ViewGroup mImageCircleView = null;
	private SlideImageLayout mSlideLayout = null;

	private ArrayList<View> mImagePageViewList = null;

	private SlideImageAdapter liSlideImageAdapter;

	private final int DELAY_TIME = 3000;

	private int MAX_VALUE = 10240;

	private boolean isContinue = true;

	private int size;

	private boolean hasData = false;
	
	private String isplay;

	private List<Advertisement> advertisements;
	
	private List<AdvertisementDetail> advertisementsdetai;
	
	public SlideView(Context context, String isplay) {
		this.context = context;
		this.isplay = isplay;
		initView();
	}

	// 设置首页广告
	public void setAdvertisements(List<Advertisement> advertisements) {
		this.advertisements = advertisements;
		if (advertisements != null && advertisements.size() > 0) {
			size = advertisements.size();
			hasData = true;
			String[] urls = new String[size];
			for (int i = 0; i < advertisements.size(); i++) {
				urls[i] = advertisements.get(i).getImageUrl();
			}
			addBannerView(urls);
		} else {
			hasData = false;
		}

	}
	
	// 设置热门详情广告
	public void setAdvertisementsDetail(List<AdvertisementDetail> advertisements) {
		this.advertisementsdetai = advertisements;
		if (advertisementsdetai != null && advertisementsdetai.size() > 0) {
			size = advertisementsdetai.size();
			hasData = true;
			String[] urls = new String[size];
			for (int i = 0; i < advertisementsdetai.size(); i++) {
				urls[i] = advertisementsdetai.get(i).getImageUrl();
			}
			addBannerView(urls);
		} else {
			hasData = false;
		}

	}

	private void initView() {
		pageView = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.slide_view, null);
		viewPager = (ViewPager) pageView.findViewById(R.id.image_slide_page);
		mImageCircleView = (ViewGroup) pageView
				.findViewById(R.id.layout_circle_images);

		viewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					isContinue = false;
					break;
				case MotionEvent.ACTION_UP:
					isContinue = true;
					break;
				default:
					isContinue = true;
					break;
				}
				return false;
			}
		});
	}

	private void addBannerView(String[] imageUrls) {
		mImageCircleViews = new ImageView[size];
		mSlideLayout = new SlideImageLayout(context);
		mSlideLayout.setCircleImageLayout(size);
		mImageCircleView.removeAllViews();
		mImagePageViewList = new ArrayList<View>();
		for (int i = 0; i < size; i++) {
			mImagePageViewList.add(mSlideLayout
					.getSlideImageLayout(imageUrls[i]));
			mImageCircleViews[i] = mSlideLayout.getCircleImageLayout(i);
			mImageCircleView.addView(mSlideLayout.getLinearLayout(
					mImageCircleViews[i], LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}

		liSlideImageAdapter = new SlideImageAdapter();
		viewPager.setAdapter(liSlideImageAdapter);
		viewPager.setOnPageChangeListener(new ImagePageChangeListener());
		if (isplay.equals("0")) {
			thread.start();
		}
	}

	public View getView() {
		return pageView;
	}

	private class SlideImageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return size;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View view, int position, Object arg2) {

		}

		@Override
		public Object instantiateItem(View view, final int position) {
			View v = mImagePageViewList.get(position
					% mImagePageViewList.size());
			try {
				((ViewPager) viewPager).addView(v, position);
				v.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
//						if (advertisements != null && advertisements.size() > 0) {
//							if (advertisements.get(position % mImagePageViewList.size()).getType().equals("1")) {
//								Intent intent = new Intent(context, HomeHeatDetailActivity.class);
//								intent.putExtra("tittle", "商品详情");
//								intent.putExtra("pid", advertisements.get(position % mImagePageViewList.size()).getPid());
//								context.startActivity(intent);
//							} else if (advertisements.get(position % mImagePageViewList.size()).getType().equals("2")) {
//								Intent intent = new Intent(context, WebViewActivity.class);
//								intent.putExtra("tittle", "商品详情");
//								intent.putExtra("link", advertisements.get(position % mImagePageViewList.size()).getAdvertUrl());
//								context.startActivity(intent);
//							} else {
//								// type = 3
//							}
//						}
					}
				});

			} catch (Exception e) {
			}
			return v;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}

	private class ImagePageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			viewPager.getParent().requestDisallowInterceptTouchEvent(true);
		}

		@Override
		public void onPageSelected(int index) {
			for (int i = 0; i < mImageCircleViews.length; i++) {
				mImageCircleViews[index % mImageCircleViews.length]
						.setBackgroundResource(R.drawable.ic_dot_focused);

				if (index % mImageCircleViews.length != i) {
					mImageCircleViews[i]
							.setBackgroundResource(R.drawable.ic_dot_normal);
				}
			}
		}
	}

	private Thread thread = new Thread() {
		public void run() {
			while (hasData) {
				if (isContinue) {
					try {
						Thread.sleep(DELAY_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message msg = Message.obtain();
					int index = (viewPager.getCurrentItem() + 1) % size;
					msg.what = index;
					handler.sendMessage(msg);
				}
			}

		};
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			viewPager.setCurrentItem(msg.what);
		}
	};

}

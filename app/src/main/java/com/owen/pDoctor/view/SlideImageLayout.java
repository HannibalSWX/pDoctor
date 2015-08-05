package com.owen.pDoctor.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owen.pDoctor.R;
import com.owen.pDoctor.util.ImageLoader;

/**
 * 生成滑动图片区域布局
 * 
 * @Description: 生成滑动图片区域布局
 * 
 * @File: SlideImageLayout.java
 * 
 * @Package com.image.indicator.layout
 * 
 * @Date 2012-6-18 上午09:04:14
 * 
 * @Version V1.0
 */
public class SlideImageLayout {
	private Context mContext = null;
	// 圆点图片集合
	private ImageView[] mImageViews = null;
	private ImageView mImageView = null;
	private ImageLoader imageLoader;

	public SlideImageLayout(Context context) {
		this.mContext = context;
		imageLoader = new ImageLoader(mContext);
	}

	/**
	 * 生成滑动图片区域布局
	 * 
	 * @param id
	 * @return
	 */
	public View getSlideImageLayout(String imageUrl) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.slide_image,
				null);
		ImageView image = (ImageView) view.findViewById(R.id.image);
		TextView title = (TextView) view.findViewById(R.id.title);
		if (imageUrl != null && !"".equals(imageUrl.trim())) {
			imageLoader.DisplayImage(imageUrl, image, false);
		} else {
			image.setImageResource(R.drawable.ic_launcher);
		}
		return view;
	}

	/**
	 * 获取LinearLayout
	 * 
	 * @param view
	 * @param width
	 * @param height
	 * @return
	 */
	public View getLinearLayout(View view, int width, int height) {
		LinearLayout linerLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams linerLayoutParames = new LinearLayout.LayoutParams(
				width, height, 1);
		// 这里最好也自定义设置，有兴趣的自己设置。
		linerLayout.setPadding(10, 0, 10, 0);
		linerLayout.addView(view, linerLayoutParames);

		return linerLayout;
	}

	/**
	 * 设置圆点个数
	 * 
	 * @param size
	 */
	public void setCircleImageLayout(int size) {
		mImageViews = new ImageView[size];
	}

	/**
	 * 生成圆点图片区域布局对象
	 * 
	 * @param index
	 * @return
	 */
	public ImageView getCircleImageLayout(int index) {
		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(10, 10));
		mImageView.setScaleType(ScaleType.FIT_XY);

		mImageViews[index] = mImageView;

		if (index == 0) {
			// 默认选中第一张图片
			mImageViews[index].setBackgroundResource(R.drawable.ic_dot_focused);
		} else {
			mImageViews[index].setBackgroundResource(R.drawable.ic_dot_normal);
		}

		return mImageViews[index];
	}

}

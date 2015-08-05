package com.owen.pDoctor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.owen.pDoctor.R;
import com.owen.pDoctor.model.Advertisement;
import com.owen.pDoctor.util.ImageLoader;

/**
 * Created by zq on 15-01-13.
 */
public class AdsAdapter extends BaseAdapter {

	private Context mContext;

	private List<Advertisement> adsBean = new ArrayList<Advertisement>();

	private ImageLoader mImageLoader;
	
	private String imagePath = "";

	public AdsAdapter(Context context, List<Advertisement> ads) {
		mContext = context;
		adsBean = ads;
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(mContext);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		final Hodler hodler;
		if (view == null) {
			hodler = new Hodler();
			view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ads_item, null);
			Gallery.LayoutParams params = new Gallery.LayoutParams(
					Gallery.LayoutParams.MATCH_PARENT,
					Gallery.LayoutParams.MATCH_PARENT);
			view.setLayoutParams(params);
			hodler.items_im = (ImageView) view.findViewById(R.id.gallery_image);

			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}

		if (adsBean.size() > 0) {
			imagePath = adsBean.get(position % adsBean.size()).getImageUrl();
		}

		// // 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
		hodler.items_im.setImageResource(R.drawable.ic_launcher);
		hodler.items_im.setScaleType(ImageView.ScaleType.FIT_XY);
		if (imagePath == null || "".equals(imagePath)) {
			hodler.items_im.setImageResource(R.drawable.ic_launcher);
		} else {
			// 需要显示的网络图片
			mImageLoader.DisplayImage(imagePath, hodler.items_im, false);
		}

		return view;
	}

	public class Hodler {
		ImageView items_im;
	}

}

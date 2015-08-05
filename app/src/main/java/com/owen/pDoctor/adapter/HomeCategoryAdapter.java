package com.owen.pDoctor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owen.pDoctor.R;
import com.owen.pDoctor.model.HomeCategoryBean;
import com.owen.pDoctor.util.ImageLoader;

/**
 * Created by zq on 15-01-13.
 */
public class HomeCategoryAdapter extends BaseAdapter {

	private Context mContext;

	private List<HomeCategoryBean> categoryBean = new ArrayList<HomeCategoryBean>();

	private ImageLoader mImageLoader;

	private String imagePath = "";

	public HomeCategoryAdapter(Context context, List<HomeCategoryBean> ads) {
		mContext = context;
		categoryBean = ads;
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(mContext);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categoryBean == null ? 0 : categoryBean.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return categoryBean == null ? 0 : categoryBean.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return categoryBean == null ? 0 : position;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		final Hodler hodler;
		if (view == null) {
			hodler = new Hodler();
			view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.home_category_item, null);
			hodler.items_im = (ImageView) view.findViewById(R.id.items_im);
			hodler.items_tittle = (TextView) view.findViewById(R.id.items_tittle);
			hodler.items_content = (TextView) view.findViewById(R.id.items_content);

			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}

		hodler.items_tittle.setText(categoryBean.get(position).getTitle());
		hodler.items_content.setText(categoryBean.get(position).getContent());
		imagePath = categoryBean.get(position).getImage();

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
		TextView items_tittle, items_content;
	}

}

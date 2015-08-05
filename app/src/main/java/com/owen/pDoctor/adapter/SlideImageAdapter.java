package com.owen.pDoctor.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.owen.pDoctor.R;
import com.owen.pDoctor.model.Advertisement;
import com.owen.pDoctor.view.MyGallery;

public class SlideImageAdapter extends BaseAdapter {

	private List<Advertisement> advertisements;

	private Context mContext;

	int size;

	public SlideImageAdapter(Context context, MyGallery myGallery) {
		this.mContext = context;
		myGallery.setOnItemClickListener(new myClickListener());
	}

	public void setAdvertisements(List<Advertisement> advertisements) {
		this.advertisements = advertisements;
		if (advertisements != null && advertisements.size() > 0) {
			size = advertisements.size();
		}

	}

	public int getCount() {
		if (size < 2) {
			return size;
		}
		return Integer.MAX_VALUE;
	}

	public Object getItem(int arg0) {
		return advertisements == null ? null : advertisements.get(arg0);
	}

	public long getItemId(int position) {
		return advertisements == null ? 0 : position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			ImageView imageView = new ImageView(mContext);
			imageView.setAdjustViewBounds(true);
//			imageView.setImageResource(R.drawable.defalut_big);
			imageView.setImageResource(R.drawable.ic_launcher);
//			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			convertView = imageView;
			viewHolder.imageView = (ImageView) convertView;
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.imageView.setImageResource(R.drawable.ic_launcher);
		return convertView;
	}

	private static class ViewHolder {
		ImageView imageView;
	}

	private class myClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//			if (advertisements.get(position % size).getContentType() == 0) {
//				// Intent advertIntent = new Intent(mContext,
//				// AdverContentActivity.class);
//				// advertIntent.putExtra("content_url",
//				// advertisements.get(position % size).getAdvertUrl());
//				// mContext.startActivity(advertIntent);
//			} else if (advertisements.get(position % size).getContentType() == 1) {
//				// Intent productListintent = new Intent(mContext,
//				// ProductListActivity.class);
//				// productListintent.putExtra("productList",
//				// (Serializable) (advertisements.get(position % size)
//				// .getProductList()));
//
//				// mContext.startActivity(productListintent);
//
//			}
		}

	}

}

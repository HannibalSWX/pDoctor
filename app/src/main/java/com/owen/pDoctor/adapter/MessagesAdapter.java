package com.owen.pDoctor.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.owen.pDoctor.R;
import com.owen.pDoctor.model.MessagesBean;
import com.owen.pDoctor.util.ImageLoader;

public class MessagesAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<MessagesBean> mData;
	
	private ImageLoader mImageLoader;
	
	private boolean mBusy = false;

	public MessagesAdapter(Context context, ArrayList<MessagesBean> data) {
		this.mContext = context;
		this.mData = data;
		
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(mContext);
		}
	}
	
	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	public Object getItem(int arg0) {
		return mData == null ? 0 : mData.get(arg0);
	}

	public long getItemId(int arg0) {
		return mData == null ? 0 : arg0;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		final Hodler hodler;
		if (view == null) {
			hodler = new Hodler();
			view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.messages_item, null);
			hodler.items_im = (ImageView) view.findViewById(R.id.items_im);
			hodler.items_num = (TextView) view.findViewById(R.id.items_num);
			hodler.items_name = (TextView) view.findViewById(R.id.items_name);
			hodler.items_last_msg = (TextView) view.findViewById(R.id.items_last_msg);
			hodler.items_time = (TextView) view.findViewById(R.id.items_time);

			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}

		hodler.items_name.setText(mData.get(position).getNickname());
		hodler.items_last_msg.setText(mData.get(position).getContent());
//		hodler.items_time.setText(childData.get(position).get(childPosition).getAdd_time());
		if (Integer.parseInt(mData.get(position).getNoread_num()) > 0) {
			hodler.items_num.setVisibility(View.VISIBLE);
			hodler.items_num.setText(mData.get(position).getNoread_num());
		} else {
			hodler.items_num.setVisibility(View.GONE);
		}
		
		String imagePath = mData.get(position).getImgurl();
		// 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
		hodler.items_im.setImageResource(R.drawable.icon_username);
		if (imagePath == null || "".equals(imagePath)) {
			hodler.items_im.setImageResource(R.drawable.icon_username);
		} else {
			// 需要显示的网络图片
			if (!mBusy) {
				mImageLoader.DisplayImage(imagePath, hodler.items_im, false);
			} else {
				mImageLoader.DisplayImage(imagePath, hodler.items_im, true);
			}
		}
		return view;
	}

	public class Hodler {
		ImageView items_im;
		TextView items_num, items_name, items_last_msg, items_time;
	}
}

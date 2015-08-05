package com.owen.pDoctor.adapter;

import java.util.List;

import com.owen.pDoctor.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NonAdapter extends BaseAdapter {

	private Context mContext;

	// private ArrayList<NonBean> mData = new ArrayList<NonBean>();

	private List<Integer> mData;

	public NonAdapter(Context context, List<Integer> timelist) {
		this.mContext = context;
		this.mData = timelist;
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

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		final Hodler hodler;
		if (arg1 == null) {
			hodler = new Hodler();
			arg1 = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.time_griditem, null);
			hodler.time = (TextView) arg1.findViewById(R.id.items_time);
			arg1.setTag(hodler);
		} else {
			hodler = (Hodler) arg1.getTag();
		}

		if (mData.get(arg0) == 5) {
			hodler.time.setText("周一");
		} else if (mData.get(arg0) == 6) {
			hodler.time.setText("周二");
		} else if (mData.get(arg0) == 7) {
			hodler.time.setText("周三");
		} else if (mData.get(arg0) == 8) {
			hodler.time.setText("周四");
		} else if (mData.get(arg0) == 9) {
			hodler.time.setText("周五");
		}
		return arg1;
	}

	public class Hodler {
		TextView time;
	}
}

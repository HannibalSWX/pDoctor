package com.owen.pDoctor.adapter;

import com.owen.pDoctor.R;
import com.owen.pDoctor.activity.Customized2Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomizedAdapter extends BaseAdapter {

	private Context mContext;

	private String[] dates;

	public CustomizedAdapter(Context context, String[] date) {
		this.mContext = context;
		this.dates = date;
	}

	public int getCount() {
		return dates == null ? 0 : dates.length;
	}

	public Object getItem(int arg0) {
		return dates == null ? 0 : dates[arg0];
	}

	public long getItemId(int arg0) {
		return dates == null ? 0 : arg0;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		final Hodler hodler;
		if (view == null) {
			hodler = new Hodler();
			view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.customized_item, null);
			hodler.items_im = (ImageView) view.findViewById(R.id.items_im);
			hodler.items_name = (TextView) view.findViewById(R.id.items_name);
			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}

		hodler.items_name.setOnClickListener(new ItemclickListener(position, hodler));
		return view;
	}

	class ItemclickListener implements OnClickListener {
		int pos;
		Hodler holder;

		public ItemclickListener(int position, Hodler hodler) {
			// TODO Auto-generated constructor stub
			this.pos = position;
			this.holder = hodler;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(mContext, Customized2Activity.class);
			Bundle bundle = new Bundle();
			bundle.putString("date", holder.items_name.getText().toString());
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		}
	}

	public class Hodler {
		ImageView items_im;
		TextView items_name;
	}
}

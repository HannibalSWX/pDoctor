package com.owen.pDoctor.adapter;

import com.owen.pDoctor.R;
import com.owen.pDoctor.activity.FreeDateActivity;
import com.owen.pDoctor.util.Constants;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 选择天数adapter
 * 
 */
public class FreedateAdapter extends BaseAdapter {

	private FreeDateActivity mContext;

	private boolean ischeck = false;

	private String[] dates;

	public FreedateAdapter(FreeDateActivity context, String[] dat) {
		this.mContext = context;
		this.dates = dat;
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
			view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.freedate_item, null);
			hodler.items_im = (ImageView) view.findViewById(R.id.items_im);
			hodler.items_name = (TextView) view.findViewById(R.id.items_name);
			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}
		hodler.items_name.setText(dates[position]);
		view.setOnClickListener(new GvclickListener(position, hodler));
		return view;
	}

	class GvclickListener implements OnClickListener {
		int pos;
		Hodler holder;

		public GvclickListener(int position, Hodler hodler) {
			// TODO Auto-generated constructor stub
			this.pos = position;
			this.holder = hodler;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (ischeck) {
				holder.items_im.setVisibility(View.GONE);
				// holder.items_im.setBackgroundResource(R.drawable.choose_yes);
				ischeck = false;
			} else {
				holder.items_im.setVisibility(View.VISIBLE);
				ischeck = true;
//				Intent intent = new Intent();
//				intent.putExtra("date", dates[pos]);
//				mContext.setResult(mContext.RESULT_OK, intent);
				
				Intent mIntent = new Intent(Constants.CHOOSE_DATE_BRAODCAST);
				mIntent.putExtra("date", dates[pos]);
				// 发送广播
				mContext.sendBroadcast(mIntent);
				
				mContext.finish();
			}
		}
	}

	public class Hodler {
		ImageView items_im;
		TextView items_name;
	}
}

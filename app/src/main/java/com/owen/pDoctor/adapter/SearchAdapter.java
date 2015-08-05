package com.owen.pDoctor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owen.pDoctor.R;

public class SearchAdapter extends BaseAdapter {

	private Context mContext;
	
//	private ArrayList<MessagesBean> mData;
	
	List<String> result = new ArrayList<String>(); // 测试数据

	public SearchAdapter(Context context, List<String> result) {
		this.mContext = context;
		this.result = result;
	}

	public int getCount() {
		return result == null ? 0 : result.size();
	}

	public Object getItem(int arg0) {
		return result == null ? 0 : result.get(arg0);
	}

	public long getItemId(int arg0) {
		return result == null ? 0 : arg0;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		final Hodler hodler;
		if (view == null) {
			hodler = new Hodler();
			view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.search_listitem, null);
			hodler.content = (TextView) view.findViewById(R.id.items_content);
			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}

		hodler.content.setText(result.get(position));
//		hodler.content.setText(mData.get(position).getEquipName());
//		hodler.tittle.setText(mData.get(position).getInspStarttime());
		return view;
	}

	public class Hodler {
		TextView content;
	}
}

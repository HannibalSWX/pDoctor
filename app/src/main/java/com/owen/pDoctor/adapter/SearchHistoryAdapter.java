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
import com.owen.pDoctor.model.SearchHistoryBean;

/**
 * ClassName：SearchAdapter
 * Description：SearchAdapter
 * Author ： zhouqiang
 * Date ：2015-1-25 上午10:08:55
 * Copyright (C) 2012-2014 owen
 */
public class SearchHistoryAdapter extends BaseAdapter {

	private Context mContext;
	
	private List<SearchHistoryBean> sh_bean = new ArrayList<SearchHistoryBean>();
	
	public SearchHistoryAdapter(Context context, List<SearchHistoryBean> sh) {
		this.mContext = context;
		this.sh_bean = sh;
	}

	public int getCount() {
		return sh_bean == null ? 0 : sh_bean.size();
	}

	public Object getItem(int arg0) {
		return sh_bean == null ? 0 : sh_bean.get(arg0);
	}

	public long getItemId(int arg0) {
		return sh_bean == null ? 0 : arg0;
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

		hodler.content.setText(sh_bean.get(position).getHistoryContent());
		return view;
	}

	public class Hodler {
		TextView content;
	}
}

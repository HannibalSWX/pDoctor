package com.owen.pDoctor.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owen.pDoctor.R;
import com.owen.pDoctor.model.SpreadAmountBean;

/**
 * ClassName：FabuAdapter
 * Description：发布adapter
 * Author ： zhouqiang
 * Date ：2015-2-3 下午5:08:04
 * Copyright (C) 2012-2014 owen
 */
public class AmountAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<SpreadAmountBean> amountlist = new ArrayList<SpreadAmountBean>();
	
	private int clickTemp = -1;

	public AmountAdapter(Context context, ArrayList<SpreadAmountBean> list) {
		this.mContext = context;
		this.amountlist = list;
	}

	public int getCount() {
		return amountlist == null ? 0 : amountlist.size();
	}

	public Object getItem(int arg0) {
		return amountlist == null ? 0 : amountlist.get(arg0);
	}

	public long getItemId(int arg0) {
		return amountlist == null ? 0 : arg0;
	}

	public void setSeclection(int position) {
		clickTemp = position;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		final Hodler hodler;
		if (view == null) {
			hodler = new Hodler();
			view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.amount_item, null);
			hodler.tv_day = (TextView) view.findViewById(R.id.tv_day);
			hodler.ll_day = (LinearLayout) view.findViewById(R.id.ll_day);
			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}

		hodler.tv_day.setText(amountlist.get(position).getDays());
		
		if (clickTemp == position) {
			hodler.tv_day.setTextColor(mContext.getResources().getColor(R.color.baise));
			hodler.ll_day.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.jubao_bg_pressed));
		} else {
			hodler.tv_day.setTextColor(mContext.getResources().getColor(R.color.red));
			hodler.ll_day.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.jubao_bg_normal));
		}
		return view;
	}

	public class Hodler {
		TextView tv_day;
		LinearLayout ll_day;
	}
}

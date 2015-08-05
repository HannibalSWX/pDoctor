package com.owen.pDoctor.adapter;

import java.util.ArrayList;
import java.util.List;

import com.owen.pDoctor.R;
import com.owen.pDoctor.model.MyGroupBean;
import com.owen.pDoctor.model.MyGroupChildBean;
import com.owen.pDoctor.util.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @Title:GroupAdapter.java
 * @Description:应用GroupAdapter.java类
 * @Author:owen
 * @Since:2015年7月17日
 * @Version:
 */
public class GroupAdapter extends BaseExpandableListAdapter {

	private Context mContext;

	private Boolean ischeck = false;

	private ArrayList<MyGroupBean> items = new ArrayList<MyGroupBean>();

	private List<ArrayList<MyGroupChildBean>> childData = new ArrayList<ArrayList<MyGroupChildBean>>();

	private final List<Boolean> selected = new ArrayList<Boolean>();

	private ImageLoader mImageLoader;

	public GroupAdapter(Context context, ArrayList<MyGroupBean> items, List<ArrayList<MyGroupChildBean>> childData) {
		this.mContext = context;
		this.items = items;
		this.childData = childData;
		
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(mContext);
		}
	}

	public List<Boolean> getSelect() {
		return selected;
	}

	private boolean mBusy = false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}
	
	public class Hodler {
		ImageView items_expand, im_pic;
		TextView items_group_name, items_number, items_service_info, tv_name, tv_lasttalk, tv_time;
	}

	public void showcheckbox() {
		// TODO Auto-generated method stub
		ischeck = true;
		notifyDataSetChanged();
	}

	public void hidecheckbox() {
		// TODO Auto-generated method stub
		ischeck = false;
		notifyDataSetChanged();
	}

	public int getCount() {
		return items == null ? 0 : items.size();
	}

	public Object getItem(int arg0) {
		return items == null ? 0 : items.get(arg0);
	}

	public long getItemId(int arg0) {
		return items == null ? 0 : arg0;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childData.get(groupPosition).get(childPosition).getNickname();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childData.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return items.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(final int position, boolean isExpanded, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Hodler hodler;
		MyGroupBean item = items.get(position);
		if (view == null) {
			hodler = new Hodler();
			view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.contacts_item, null);
			hodler.items_group_name = (TextView) view.findViewById(R.id.items_group_name);
			hodler.items_expand = (ImageView) view.findViewById(R.id.items_expand);
			hodler.items_number = (TextView) view.findViewById(R.id.items_number);

			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}

		hodler.items_group_name.setText(item.getName());
		hodler.items_number.setText("" + childData.get(position).size());

		if (!isExpanded) {
			hodler.items_expand.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_suifang_right));
		} else {
			hodler.items_expand.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_suifang_down));
		}

		return view;
	}

	@Override
	public View getChildView(int position, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Hodler hodler;
//		final MyGroupBean item2 = items.get(position);
		if (view == null) {
			hodler = new Hodler();
			view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.contacts_item_child2, null);
			hodler.im_pic = (ImageView) view.findViewById(R.id.im_pic);
			hodler.tv_name = (TextView) view.findViewById(R.id.tv_name);
			hodler.tv_lasttalk = (TextView) view.findViewById(R.id.tv_lasttalk);
			hodler.tv_time = (TextView) view.findViewById(R.id.tv_time);

			view.setTag(hodler);
		} else {
			hodler = (Hodler) view.getTag();
		}

		hodler.tv_name.setText(childData.get(position).get(childPosition).getNickname());
//		hodler.tv_time.setText(childData.get(position).get(childPosition).getAdd_time());

		String imagePath = childData.get(position).get(childPosition).getHeadimgurl();

		// 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
		hodler.im_pic.setImageResource(R.drawable.icon_username);
		if (imagePath == null || "".equals(imagePath)) {
			hodler.im_pic.setImageResource(R.drawable.icon_username);
		} else {
			// 需要显示的网络图片
			if (!mBusy) {
				mImageLoader.DisplayImage(imagePath, hodler.im_pic, false);
			} else {
				mImageLoader.DisplayImage(imagePath, hodler.im_pic, true);
			}
		}
		
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
}

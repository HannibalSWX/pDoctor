package com.owen.pDoctor.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.owen.pDoctor.R;

public class DownLoadingListview extends ListView implements OnScrollListener{

	// 点击加载更多枚举状态
	private enum DListViewLoadingMore {
		LV_LOADING, // 加载状态
		LV_LOADED, // 加载完毕
		LV_OVER; // 结束状态
	}

	private View mFootView;
	private TextView footerMoreView;
	private ProgressBar footerProgressBar;
	private OnBottomRefreshLoadingListener onBottomRefreshLoadingListener;
	
	private boolean flag;
	
	public DownLoadingListview(Context context) {
		super(context);
		init(context);
		initLoadMoreView(context);
	}

	public DownLoadingListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		initLoadMoreView(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(R.color.transparent));
		setOnScrollListener(this);
		setHorizontalFadingEdgeEnabled(false);

	}

	/***
	 * 初始化底部加载更多控件
	 */
	private void initLoadMoreView(Context context) {
		mFootView = LayoutInflater.from(context).inflate(
				R.layout.listview_footer, null);

		footerMoreView = (TextView) mFootView.findViewById(R.id.more);
		footerProgressBar = (ProgressBar) mFootView
				.findViewById(R.id.footer_progressBar);
		
		mFootView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (onBottomRefreshLoadingListener != null  && !flag) {
					updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
					onBottomRefreshLoadingListener.onLoadMore();
				}
				
			}
		});

		addFooterView(mFootView);
	}

	public void setOnBottomRefreshListner(
			OnBottomRefreshLoadingListener reListener) {
		this.onBottomRefreshLoadingListener = reListener;
	}

	// 更新Footview视图
	private void updateLoadMoreViewState(DListViewLoadingMore state) {
		switch (state) {
		// 加载中状态
		case LV_LOADING:
			footerProgressBar.setVisibility(View.VISIBLE);
			footerMoreView.setVisibility(View.VISIBLE);
			footerMoreView.setText("正在努力加载");
			flag=true;
			break;
		// 加载完毕状态
		case LV_OVER:
			footerProgressBar.setVisibility(View.GONE);
			footerMoreView.setVisibility(View.VISIBLE);
			footerMoreView.setText("加载完成");
			break;
			//上一次加载结束
		case LV_LOADED:
			footerProgressBar.setVisibility(View.GONE);
			footerMoreView.setVisibility(View.VISIBLE);
			footerMoreView.setText("更多");
			flag=false;
			break;
		default:
			break;
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			int lastPostion = this.getLastVisiblePosition(); // 从0开始的.
			int size = this.getCount();
			if ((size - 1) == lastPostion) {
				if (onBottomRefreshLoadingListener != null  && !flag) {
					updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
					onBottomRefreshLoadingListener.onLoadMore();
					
				}
			}

		}
	}	   
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (totalItemCount <= visibleItemCount) {
			if (mFootView != null) {
			    mFootView.setVisibility(View.GONE);
			}
		} else {
			if (mFootView != null) {
				mFootView.setVisibility(View.VISIBLE);
			}
		}

	}

	public void loadComplete() {
		updateLoadMoreViewState(DListViewLoadingMore.LV_OVER);    //加载完成
	}
	
	public void loadedComplete() {
		updateLoadMoreViewState(DListViewLoadingMore.LV_LOADED);  //上一次加载结束
	}
	 
	/**
	 * 不现实footView
	 */
	public void setFootViewGone(){
	    mFootView.setVisibility(View.GONE);
	}
	/**
	 * 
	 * @author tang_zhengzong
	 * listView加载更多监听时间
	 */
	public interface OnBottomRefreshLoadingListener {
		void onLoadMore();
	}

}

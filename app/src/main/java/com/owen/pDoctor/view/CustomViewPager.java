package com.owen.pDoctor.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 禁止左右滑屏的ViewPager
 * 
 * @author zq
 */
public class CustomViewPager extends ViewPager {

	/**
	 * 是否允许滑动
	 */
	private boolean enabled;

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.enabled = false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}

	public void setPagingEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
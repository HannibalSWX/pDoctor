package com.owen.pDoctor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 解决ScrollView嵌套ViewPager出现的滑动冲突问题
 */
public class MyScrollView extends ScrollView {

	// private boolean canScroll;

	private GestureDetector mGestureDetector;

	// View.OnTouchListener mGestureListener;

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(new YScrollDetector());
		// canScroll = true;
	}

	public MyScrollView(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(new YScrollDetector());
		// canScroll = true;
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mGestureDetector = new GestureDetector(new YScrollDetector());
		// canScroll = true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// if (ev.getAction() == MotionEvent.ACTION_UP)
		// canScroll = true;
		return super.onInterceptTouchEvent(ev)
				&& mGestureDetector.onTouchEvent(ev);
	}

	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// if (canScroll)
			// if (Math.abs(distanceY) >= Math.abs(distanceX))
			// canScroll = true;
			// else
			// canScroll = false;
			return (Math.abs(distanceY) >= Math.abs(distanceX));
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if ((oldh - h) > (oldw - w)) {

		}
		super.onSizeChanged(w, h, oldw, oldh);
	}
}

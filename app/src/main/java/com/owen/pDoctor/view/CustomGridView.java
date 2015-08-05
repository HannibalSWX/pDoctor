package com.owen.pDoctor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author zq
 * @date 2013-4-24
 * 
 */
public class CustomGridView extends GridView {

	public CustomGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomGridView(Context context) {
		super(context);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}

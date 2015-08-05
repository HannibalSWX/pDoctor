package com.owen.pDoctor.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.owen.pDoctor.BaseActivity;
import com.owen.pDoctor.R;
import com.owen.pDoctor.adapter.SearchHistoryAdapter;
import com.owen.pDoctor.db.DBManager;
import com.owen.pDoctor.db.MySQLiteOpenHelper;
import com.owen.pDoctor.model.Advertisement;
import com.owen.pDoctor.model.SearchHistoryBean;
import com.owen.pDoctor.util.Constants;

/**
 * ClassName：SearchActivity
 * Description：搜索页
 * Author ： zhouqiang
 * Date ：2015-1-24 下午2:37:16
 * Copyright (C) 2012-2014 owen
 */
public class SearchActivity extends BaseActivity {
	/**
	 * 应用程序上下文
	 */
	private Context mContext;

	public List<String> urls;

	Uri uri;

	private LinearLayout back_btn;
	
	private Button clear_btn;
	
	private AutoCompleteTextView search_et;

	private ImageView search_im, close_im;
	
	private TextView search_tv, hint_tv;

	private LinearLayout ll_clear;

	List<Advertisement> adsBean = new ArrayList<Advertisement>();

	private ListView search_history_lv;

	private SearchHistoryAdapter sh_adapter;
	
	private List<SearchHistoryBean> sh_bean = new ArrayList<SearchHistoryBean>();
	
	private MySQLiteOpenHelper dbOpenHelper;
	// 数据库管理对象
	private DBManager dbManager;
	
	private String categoryid, typeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_search_history);
		
		dbOpenHelper = new MySQLiteOpenHelper(mContext);
		dbManager = DBManager.getInstance(this);
		
		Intent intent = getIntent();
		categoryid = intent.getStringExtra("categoryId");
		typeId = intent.getStringExtra("typeId");
		
//		getadvertisment();
		
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (LinearLayout) findViewById(R.id.back_btn);
		search_et = (AutoCompleteTextView) findViewById(R.id.search_et);
		search_im = (ImageView) findViewById(R.id.search_im);
		close_im = (ImageView) findViewById(R.id.close_im);
		hint_tv = (TextView) findViewById(R.id.hint_tv);
		search_tv = (TextView) findViewById(R.id.search_tv);
		search_history_lv = (ListView) findViewById(R.id.search_history_list);
		ll_clear = (LinearLayout) findViewById(R.id.ll_clear);
		clear_btn = (Button) findViewById(R.id.clear_btn);
		
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				finish();
			}
		});
		search_et.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sh_bean = dbManager.queryHistory();
				if (sh_bean.size() > 0) {
					hint_tv.setVisibility(View.GONE);
					search_history_lv.setVisibility(View.VISIBLE);
					ll_clear.setVisibility(View.VISIBLE);
				}
				sh_adapter = new SearchHistoryAdapter(getApplicationContext(), sh_bean);
				search_history_lv.setAdapter(sh_adapter);
			}
		});
		search_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				getresult();
			}
		});
		clear_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				dbManager.deleteHistory("search_content = ?");
				sh_bean.clear();
				sh_adapter.notifyDataSetChanged();
			}
		});
		search_im.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				
			}
		});
		close_im.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				search_et.setText("");
			}
		});
		search_history_lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				search_et.setText(sh_bean.get(position).getHistoryContent());
				getresult();
			}
		});

		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
	}

	private void getresult() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(mContext, SearchResultActivity.class);
		intent.putExtra("search_tv", search_et.getText().toString());
		intent.putExtra("categoryId", categoryid);
		intent.putExtra("typeId", typeId);
		startActivity(intent);
		String et = search_et.getText().toString();
		// 删除重复搜索关键字
		dbManager.deleteDuplecate(et);
		sh_adapter.notifyDataSetChanged();
		
		saveHistory();
		
		search_et.setText("");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	// 保存搜索历史信息
	protected void saveHistory() {
		// TODO Auto-generated method stub
		String his_tv = search_et.getText().toString();
		ContentValues cv = new ContentValues();
		// 列名和值
		cv.put(Constants.SEARCH_CONTENT, his_tv);
		// 得到结果
		boolean flag = dbManager.insertHistory(cv);
		if (flag) {
			System.out.println("添加搜索历史成功");
		} else {
			System.out.println("添加搜索历史失败");
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			hideKeyboard();
		}
		return super.onTouchEvent(event);
	}
	
	private void hideKeyboard() {
		if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
	        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
	                hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
    }
}

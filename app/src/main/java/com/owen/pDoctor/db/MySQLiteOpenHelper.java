package com.owen.pDoctor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.owen.pDoctor.util.Constants;

/**
 * ClassName：MySQLiteOpenHelper
 * Description：MySQLiteOpenHelper
 * Author ： zhouqiang
 * Date ：2015-1-24 下午8:28:28
 * Copyright (C) 2012-2014 南瑞信通
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	// 定义一个SQLiteDatabase对象，对表进行相应的操作
	private SQLiteDatabase mDatabase;

	public MySQLiteOpenHelper(Context context) {
		super(context, Constants.DATABASE_NAME, null,
				Constants.DATABASE_VERSION);
		mDatabase = getWritableDatabase();
	}

	/*
	 * 创建表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// sql语句
		String search_history = "create table " + Constants.TABLE_SEARCHHISTORY + "("
				+ Constants.IDS + " integer primary key autoincrement,"
				+ Constants.SEARCH_CONTENT + " varchar(20))";
		db.execSQL(search_history);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(newVersion > oldVersion){
	        db.execSQL("DROP TABLE IF EXISTS[searchhistory]");
	    } else {
	        return;
	    }
		onCreate(db);
	}

}

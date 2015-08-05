package com.owen.pDoctor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "nari.db";
	private static final int DATABASE_VERSION = 1;
	public static final String CONTACT = "t_contact";
	public static final String T_ORDER = "t_order";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS t_contact(key INTEGER PRIMARY KEY AUTOINCREMENT,_id INT,"
				+ "name STRING,moblePhone NUMBLE,remark STRING,imagePath STRING)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS t_order(id integer primary key autoincrement,"
				+ "orderNumber TEXT,time TEXT,productName TEXT,price FLOAT)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS t_repair_order(" +
				"ordernum varchar(255),"+ 
				"status int," +
				"createtime varchar(255)," +
				"context varchar(4000)," +
				"pic_path varchar(500)," +
				"voice_path varchar(500)," +
				"repairTime varchar(100)," +
				"money varchar(100)," +
				"repairer varchar(50)," +
				"repairer_tel varchar(100)," +
				"material varchar(2000)," +
				"judge varchar(50)," +
				"judge_text varchar(2000)," +
				"name varchar(2000)," +
				"addr varchar(2000)" +
				")");
		
		Log.i("create table", "success");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CONTACT);
	}
}

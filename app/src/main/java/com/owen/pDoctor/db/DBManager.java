package com.owen.pDoctor.db;

import java.util.ArrayList;
import java.util.List;

import com.owen.pDoctor.model.SearchHistoryBean;
import com.owen.pDoctor.util.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * ClassName：DBManager Description：数据库操作管理类 Author ： zhouqiang Date ：2014-12-3
 * 下午5:05:05 Copyright (C) 2012-2014 owen
 */
public class DBManager {

	private MySQLiteOpenHelper dbOpenHelper;

	private static DBManager dbManager;

	private static Context mContext;

	private String updateequiptime = "";

	public static DBManager getInstance(Context context) {
		mContext = context;
		if (dbManager == null) {
			dbManager = new DBManager(context);
		}
		return dbManager;
	}

	private DBManager(Context context) {
		dbOpenHelper = new MySQLiteOpenHelper(mContext);
	}

	/**
	 * 添加我的收藏数据
	 * 
	 * @param cv
	 * @return
	 */
	public boolean insertData(ContentValues cv) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		return db.insert(Constants.TABLE_MYFAVORITE, null, cv) > 0;
	}

	/**
	 * 删除我的收藏数据
	 * 
	 * @param cv
	 * @return
	 */
	public void deleteData(String productId) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete(Constants.TABLE_MYFAVORITE, productId, null);
	}

	/**
	 * 添加搜索历史数据
	 * 
	 * @param cv
	 * @return
	 */
	public boolean insertHistory(ContentValues cv) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		return db.insert(Constants.TABLE_SEARCHHISTORY, null, cv) > 0;
	}

	/**
	 * 查询搜索历史数据 降序排序，最大查询30条数据
	 * 
	 * @return
	 */
	public List<SearchHistoryBean> queryHistory() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		List<SearchHistoryBean> infos = new ArrayList<SearchHistoryBean>();
		// 从数据库里查询数据
		Cursor cursor = db
				.query(Constants.TABLE_SEARCHHISTORY, null, null, null, null,
						null, Constants.IDS + " DESC", Integer.toString(30));
		if (cursor != null) {
			// 取出数据
			while (cursor.moveToNext()) {
				SearchHistoryBean info = new SearchHistoryBean();
				info.setIds(cursor.getInt(0));
				info.setHistoryContent(cursor.getString(1));
				infos.add(info);
			}

		}
		return infos;
	}

	/**
	 * 删除搜索历史数据
	 * 
	 * @param cv
	 * @return
	 */
	public void deleteHistory(String productId) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try {
			String sql = "delete from " + Constants.TABLE_SEARCHHISTORY;
			db.execSQL(sql);
		} catch (Exception e) {
		}
	}

	/**
	 * 删除重复搜索历史数据
	 * 
	 * @param cv
	 * @return
	 */
	public void deleteDuplecate(String keyword) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try {
//			String sql = "delete from " + Constants.TABLE_SEARCHHISTORY + " where " + Constants.SEARCH_CONTENT + " not in (select max(" + Constants.SEARCH_CONTENT + ") from " + Constants.TABLE_SEARCHHISTORY + " group by " + keyword;
			String sql = "delete from " + Constants.TABLE_SEARCHHISTORY
					+ " where " + Constants.SEARCH_CONTENT + "='" + keyword + "'";
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试插入数据库
	 */
	public void insertData() {

		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getWritableDatabase();
			db.beginTransaction();
			Long t1 = System.currentTimeMillis();
			for (int i = 0; i < 3000; i++) {
				db.execSQL("");
			}
			Long t2 = System.currentTimeMillis();
			Log.d("inserttime", (t2 - t1) + "");
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (null != db && db.inTransaction()) {
				db.endTransaction();
				// db.close();
			}
		}
		return;
	}

	/**
	 * 用户登录成功，保存用户信息
	 */
	public synchronized void insertNewUser(String username, String passwd) {

		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getWritableDatabase();
			db.beginTransaction();
			// String args[] = {username,
			// EncryptionUtil.md5EncryptToString(passwd)};
			String args[] = { username, "" };
			String sql = "insert into DLM_USERINFO(USER_NAME, PWDMD5) values(?,?)";
			db.execSQL(sql, args);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (null != db && db.inTransaction()) {
				db.endTransaction();
			}
		}
		return;
	}

	/**
	 * 查询台账更新时间
	 * 
	 * @return
	 */
	public String queryEquipmentUpdateTime() {
		String sqle = "";
		Cursor cursor = null;
		SQLiteDatabase equipmentdb = null;
		try {
			equipmentdb = dbOpenHelper.getReadableDatabase();
			equipmentdb.beginTransaction();
			Long time1 = System.currentTimeMillis();
			// 全部和带条件查询
			sqle = "SELECT [e_time] FROM EQUIPMENT_TIME WHERE [id]=1";
			cursor = equipmentdb.rawQuery(sqle, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						updateequiptime = cursor.getString(cursor
								.getColumnIndex("e_time"));
						System.out.println("查询台账更新时间============"
								+ updateequiptime);
					} while (cursor.moveToNext());
				}
			}
			equipmentdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
			}
			if (null != equipmentdb && equipmentdb.inTransaction()) {
				equipmentdb.endTransaction();
				// db.close();
			}
		}
		return updateequiptime;
	}

	/**
	 * 修改台账更新起始时间
	 */
	public void updateEquipmentUpdateTime(String updatetime) {
		SQLiteDatabase equipmentdb = null;
		String sqle = "";
		Cursor cursor = null;
		equipmentdb = dbOpenHelper.getReadableDatabase();
		equipmentdb.beginTransaction();
		try {
			sqle = "SELECT [e_time] FROM EQUIPMENT_TIME WHERE [id]=1";
			cursor = equipmentdb.rawQuery(sqle, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						String id = cursor.getString(0);
					} while (cursor.moveToNext());
				}
				ContentValues values = new ContentValues();
				values.put("e_time", updatetime);
				String updatesql = "update EQUIPMENT_TIME set e_time = '"
						+ updatetime + "' where id = 1";
				equipmentdb.execSQL(updatesql);
			}
			equipmentdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (null != equipmentdb && equipmentdb.inTransaction()) {
				equipmentdb.endTransaction();
			}
		}
		return;
	}

	/**
	 * 根据删除用户删除用户的产权单位信息
	 * 
	 * @param
	 */
	public synchronized void deleteUserProperty() {
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			db.beginTransaction();
			db.delete("DLM_USERAUTH", "USER_NAME=?",
					new String[] { Constants.TABLE_MYFAVORITE });
			db.setTransactionSuccessful();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != db && db.inTransaction()) {
				db.endTransaction();

			}
		}
		return;
	}

	// 删除条件查询基础数据，重新网络下载最新数据
	public void deleteAllTableData() {
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getWritableDatabase();

			String sql1 = "delete from " + Constants.TABLE_MYFAVORITE;
			String sql2 = "delete from " + Constants.TABLE_MYFAVORITE;
			db.execSQL(sql1);
			db.execSQL(sql2);

		} catch (Exception e) {
		}
	}

	// 关闭
	public void dbClose() {
		dbOpenHelper.close();
	}

}

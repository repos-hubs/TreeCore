package com.treecore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import com.treecore.db.entity.TDBMasterEntity;
import com.treecore.db.exception.TDBException;
import com.treecore.db.exception.TDBNotOpenException;
import com.treecore.db.sql.TSqlBuilder;
import com.treecore.db.sql.TSqlBuilderFactory;
import com.treecore.utils.TDBUtils;
import com.treecore.utils.log.TLog;
import com.treecore.utils.stl.TArrayList;
import com.treecore.utils.stl.THashMap;
import com.treecore.utils.stl.TMapArrayList;
import java.util.ArrayList;
import java.util.List;

public class TSQLiteDatabase {
	private static final String DB_NAME = "tree.db";
	private static final int DB_VERSION = 1;
	private String queryStr = "";

	private String error = "";

	private Cursor queryCursor = null;

	private Boolean isConnect = Boolean.valueOf(false);

	private SQLiteDatabase mSQLiteDatabase = null;
	private TDBHelper mDatabaseHelper = null;
	private TADBUpdateListener mTadbUpdateListener;

	public TSQLiteDatabase(Context context) {
		TADBParams params = new TADBParams();
		this.mDatabaseHelper = new TDBHelper(context, params.getDbName(), null,
				params.getDbVersion());
	}

	public TSQLiteDatabase(Context context, TADBParams params) {
		this.mDatabaseHelper = new TDBHelper(context, params.getDbName(), null,
				params.getDbVersion());
	}

	public void setOnDbUpdateListener(TADBUpdateListener dbUpdateListener) {
		this.mTadbUpdateListener = dbUpdateListener;
		if (this.mTadbUpdateListener != null)
			this.mDatabaseHelper
					.setOndbUpdateListener(this.mTadbUpdateListener);
	}

	public SQLiteDatabase openDatabase(TADBUpdateListener dbUpdateListener,
			Boolean isWrite) {
		if (isWrite.booleanValue())
			this.mSQLiteDatabase = openWritable(this.mTadbUpdateListener);
		else {
			this.mSQLiteDatabase = openReadable(this.mTadbUpdateListener);
		}
		return this.mSQLiteDatabase;
	}

	public SQLiteDatabase openWritable(TADBUpdateListener dbUpdateListener) {
		if (dbUpdateListener != null) {
			this.mTadbUpdateListener = dbUpdateListener;
		}
		if (this.mTadbUpdateListener != null)
			this.mDatabaseHelper
					.setOndbUpdateListener(this.mTadbUpdateListener);
		try {
			this.mSQLiteDatabase = this.mDatabaseHelper.getWritableDatabase();
			this.isConnect = Boolean.valueOf(true);
		} catch (Exception e) {
			this.isConnect = Boolean.valueOf(false);
		}

		return this.mSQLiteDatabase;
	}

	public Boolean testSQLiteDatabase() {
		if (this.isConnect.booleanValue()) {
			if (this.mSQLiteDatabase.isOpen()) {
				return Boolean.valueOf(true);
			}
			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(false);
	}

	public SQLiteDatabase openReadable(TADBUpdateListener dbUpdateListener) {
		if (dbUpdateListener != null) {
			this.mTadbUpdateListener = dbUpdateListener;
		}
		if (this.mTadbUpdateListener != null)
			this.mDatabaseHelper
					.setOndbUpdateListener(this.mTadbUpdateListener);
		try {
			this.mSQLiteDatabase = this.mDatabaseHelper.getReadableDatabase();
			this.isConnect = Boolean.valueOf(true);
		} catch (Exception e) {
			this.isConnect = Boolean.valueOf(false);
		}

		return this.mSQLiteDatabase;
	}

	public ArrayList<THashMap<String>> query(String sql, String[] selectionArgs) {
		TLog.i(this, sql);
		if (testSQLiteDatabase().booleanValue()) {
			if ((sql != null) && (!sql.equalsIgnoreCase(""))) {
				this.queryStr = sql;
			}
			free();
			this.queryCursor = this.mSQLiteDatabase
					.rawQuery(sql, selectionArgs);
			if (this.queryCursor != null) {
				return getQueryCursorData();
			}
			TLog.e(this, "执行" + sql + "错误");
		} else {
			TLog.e(this, "数据库未打开！");
		}
		return null;
	}

	public <T> List<T> query(Class<?> clazz, boolean distinct, String where,
			String groupBy, String having, String orderBy, String limit) {
		if (testSQLiteDatabase().booleanValue()) {
			List list = null;
			TSqlBuilder getSqlBuilder = TSqlBuilderFactory.getInstance()
					.getSqlBuilder(1);
			getSqlBuilder.setClazz(clazz);
			getSqlBuilder.setCondition(distinct, where, groupBy, having,
					orderBy, limit);
			try {
				String sqlString = getSqlBuilder.getSqlStatement();
				TLog.i(this, "执行" + sqlString);
				free();
				this.queryCursor = this.mSQLiteDatabase.rawQuery(sqlString,
						null);
				list = TDBUtils.getListEntity(clazz, this.queryCursor);
			} catch (IllegalArgumentException e) {
				TLog.e(this, e.getMessage());
				e.printStackTrace();
			} catch (TDBException e) {
				TLog.e(this, e.getMessage());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				TLog.e(this, e.getMessage());
				e.printStackTrace();
			}
			return list;
		}
		return null;
	}

	public ArrayList<THashMap<String>> query(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		if (testSQLiteDatabase().booleanValue()) {
			this.queryCursor = this.mSQLiteDatabase.query(table, columns,
					selection, selectionArgs, groupBy, having, orderBy);
			if (this.queryCursor != null) {
				return getQueryCursorData();
			}
			TLog.e(this, "查询" + table + "错误");
		} else {
			TLog.e(this, "数据库未打开！");
		}
		return null;
	}

	public ArrayList<THashMap<String>> query(String table, boolean distinct,
			String[] columns, String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy, String limit) {
		if (testSQLiteDatabase().booleanValue()) {
			free();
			this.queryCursor = this.mSQLiteDatabase.query(distinct, table,
					columns, selection, selectionArgs, groupBy, having,
					orderBy, limit);
			if (this.queryCursor != null) {
				return getQueryCursorData();
			}
			TLog.e(this, "查询" + table + "错误");
		} else {
			TLog.e(this, "数据库未打开！");
		}
		return null;
	}

	public ArrayList<THashMap<String>> query(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {
		if (testSQLiteDatabase().booleanValue()) {
			free();
			this.queryCursor = this.mSQLiteDatabase.query(table, columns,
					selection, selectionArgs, groupBy, having, orderBy, limit);
			if (this.queryCursor != null) {
				return getQueryCursorData();
			}
			TLog.e(this, "查询" + table + "错误");
		} else {
			TLog.e(this, "数据库未打开！");
		}
		return null;
	}

	public ArrayList<THashMap<String>> queryWithFactory(
			SQLiteDatabase.CursorFactory cursorFactory, boolean distinct,
			String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		if (testSQLiteDatabase().booleanValue()) {
			free();
			this.queryCursor = this.mSQLiteDatabase.queryWithFactory(
					cursorFactory, distinct, table, columns, selection,
					selectionArgs, groupBy, having, orderBy, limit);
			if (this.queryCursor != null) {
				return getQueryCursorData();
			}
			TLog.e(this, "查询" + table + "错误");
		} else {
			TLog.e(this, "数据库未打开！");
		}
		return null;
	}

	public void execute(String sql, String[] bindArgs)
			throws TDBNotOpenException {
		TLog.i(this, "准备执行SQL[" + sql + "]语句");
		if (testSQLiteDatabase().booleanValue()) {
			if ((sql != null) && (!sql.equalsIgnoreCase(""))) {
				this.queryStr = sql;
				if (bindArgs != null)
					this.mSQLiteDatabase.execSQL(sql, bindArgs);
				else {
					this.mSQLiteDatabase.execSQL(sql);
				}
			}
		} else {
			throw new TDBNotOpenException("数据库未打开！");
		}
	}

	public Boolean execute(TSqlBuilder getSqlBuilder) {
		Boolean isSuccess = Boolean.valueOf(false);
		try {
			String sqlString = getSqlBuilder.getSqlStatement();
			execute(sqlString, null);
			isSuccess = Boolean.valueOf(true);
		} catch (IllegalArgumentException e) {
			isSuccess = Boolean.valueOf(false);
			e.printStackTrace();
		} catch (TDBException e) {
			isSuccess = Boolean.valueOf(false);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			isSuccess = Boolean.valueOf(false);
			e.printStackTrace();
		} catch (TDBNotOpenException e) {
			e.printStackTrace();
			isSuccess = Boolean.valueOf(false);
		}
		return isSuccess;
	}

	public TMapArrayList<String> getQueryCursorData() {
		TMapArrayList<String> arrayList = null;
		if (this.queryCursor != null)
			try {
				arrayList = new TMapArrayList();
				this.queryCursor.moveToFirst();
				while (this.queryCursor.moveToNext())
					arrayList.add(TDBUtils.getRowData(this.queryCursor));
			} catch (Exception e) {
				e.printStackTrace();
				TLog.e(this, "当前数据集获取失败！");
			}
		else {
			TLog.e(this, "当前数据集不存在！");
		}
		return arrayList;
	}

	public ArrayList<TDBMasterEntity> getTables() {
		ArrayList tadbMasterArrayList = new ArrayList();
		String sql = "select * from sqlite_master where type='table' order by name";
		TLog.i(this, sql);
		if (testSQLiteDatabase().booleanValue()) {
			if ((sql != null) && (!sql.equalsIgnoreCase(""))) {
				this.queryStr = sql;
				free();
				this.queryCursor = this.mSQLiteDatabase
						.rawQuery(
								"select * from sqlite_master where type='table' order by name",
								null);

				if (this.queryCursor != null) {
					while (this.queryCursor.moveToNext())
						if ((this.queryCursor != null)
								&& (this.queryCursor.getColumnCount() > 0)) {
							TDBMasterEntity tadbMasterEntity = new TDBMasterEntity();
							tadbMasterEntity.setType(this.queryCursor
									.getString(0));
							tadbMasterEntity.setName(this.queryCursor
									.getString(1));
							tadbMasterEntity.setTbl_name(this.queryCursor
									.getString(2));
							tadbMasterEntity.setRootpage(this.queryCursor
									.getInt(3));
							tadbMasterEntity.setSql(this.queryCursor
									.getString(4));
							tadbMasterArrayList.add(tadbMasterEntity);
						}
				} else
					TLog.e(this, "数据库未打开！");
			}
		} else {
			TLog.e(this, "数据库未打开！");
		}
		return tadbMasterArrayList;
	}

	public boolean hasTable(Class<?> clazz) {
		String tableName = TDBUtils.getTableName(clazz);
		return hasTable(tableName);
	}

	public boolean hasTable(String tableName) {
		if ((tableName != null) && (!tableName.equalsIgnoreCase(""))) {
			if (testSQLiteDatabase().booleanValue()) {
				tableName = tableName.trim();
				String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
						+ tableName + "' ";
				if ((sql != null) && (!sql.equalsIgnoreCase(""))) {
					this.queryStr = sql;
				}
				free();
				this.queryCursor = this.mSQLiteDatabase.rawQuery(sql, null);
				if (this.queryCursor.moveToNext()) {
					int count = this.queryCursor.getInt(0);
					if (count > 0)
						return true;
				}
			} else {
				TLog.e(this, "数据库未打开！");
			}
		} else
			TLog.e(this, "判断数据表名不能为空！");

		return false;
	}

	public Boolean creatTable(Class<?> clazz) {
		Boolean isSuccess = Boolean.valueOf(false);
		if (testSQLiteDatabase().booleanValue()) {
			try {
				String sqlString = TDBUtils.creatTableSql(clazz);
				execute(sqlString, null);
				isSuccess = Boolean.valueOf(true);
			} catch (TDBException e) {
				isSuccess = Boolean.valueOf(false);
				e.printStackTrace();
				TLog.e(this, e.getMessage());
			} catch (TDBNotOpenException e) {
				isSuccess = Boolean.valueOf(false);
				e.printStackTrace();
				TLog.e(this, e.getMessage());
			}
		} else {
			TLog.e(this, "数据库未打开！");
			return Boolean.valueOf(false);
		}
		return isSuccess;
	}

	public Boolean dropTable(Class<?> clazz) {
		String tableName = TDBUtils.getTableName(clazz);
		return dropTable(tableName);
	}

	public Boolean dropTable(String tableName) {
		Boolean isSuccess = Boolean.valueOf(false);
		if ((tableName != null) && (!tableName.equalsIgnoreCase(""))) {
			if (testSQLiteDatabase().booleanValue()) {
				try {
					String sqlString = "DROP TABLE " + tableName;
					execute(sqlString, null);
					isSuccess = Boolean.valueOf(true);
				} catch (Exception e) {
					isSuccess = Boolean.valueOf(false);
					e.printStackTrace();
					TLog.e(this, e.getMessage());
				}
			} else {
				TLog.e(this, "数据库未打开！");
				return Boolean.valueOf(false);
			}
		} else
			TLog.e(this, "删除数据表名不能为空！");

		return isSuccess;
	}

	public Boolean alterTable(String tableName) {
		return Boolean.valueOf(false);
	}

	public String error() {
		if ((this.queryStr != null) && (!this.queryStr.equalsIgnoreCase(""))) {
			this.error = (this.error + "\n [ SQL语句 ] : " + this.queryStr);
		}
		TLog.e(this, this.error);
		return this.error;
	}

	public Boolean insert(Object entity) {
		return insert(entity, null);
	}

	public Boolean insert(String table, String nullColumnHack,
			ContentValues values) {
		if (testSQLiteDatabase().booleanValue()) {
			if (this.mSQLiteDatabase.insert(table, nullColumnHack, values) > 0L)
				return Boolean.valueOf(true);
			return Boolean.valueOf(false);
		}
		TLog.e(this, "数据库未打开！");
		return Boolean.valueOf(false);
	}

	public Boolean insertOrThrow(String table, String nullColumnHack,
			ContentValues values) {
		if (testSQLiteDatabase().booleanValue()) {
			if (this.mSQLiteDatabase.insertOrThrow(table, nullColumnHack,
					values) > 0L)
				return Boolean.valueOf(true);
			return Boolean.valueOf(false);
		}
		TLog.e(this, "数据库未打开！");
		return Boolean.valueOf(false);
	}

	public Boolean insert(Object entity, TArrayList updateFields) {
		TSqlBuilder getSqlBuilder = TSqlBuilderFactory.getInstance()
				.getSqlBuilder(0);
		getSqlBuilder.setEntity(entity);
		getSqlBuilder.setUpdateFields(updateFields);
		return execute(getSqlBuilder);
	}

	public Boolean delete(String table, String whereClause, String[] whereArgs) {
		if (testSQLiteDatabase().booleanValue()) {
			if (this.mSQLiteDatabase.delete(table, whereClause, whereArgs) > 0)
				return Boolean.valueOf(true);
			return Boolean.valueOf(false);
		}

		TLog.e(this, "数据库未打开！");
		return Boolean.valueOf(false);
	}

	public Boolean delete(Class<?> clazz, String where) {
		if (testSQLiteDatabase().booleanValue()) {
			TSqlBuilder getSqlBuilder = TSqlBuilderFactory.getInstance()
					.getSqlBuilder(2);
			getSqlBuilder.setClazz(clazz);
			getSqlBuilder.setCondition(false, where, null, null, null, null);
			return execute(getSqlBuilder);
		}
		return Boolean.valueOf(false);
	}

	public Boolean delete(Object entity) {
		if (testSQLiteDatabase().booleanValue()) {
			TSqlBuilder getSqlBuilder = TSqlBuilderFactory.getInstance()
					.getSqlBuilder(2);
			getSqlBuilder.setEntity(entity);
			return execute(getSqlBuilder);
		}
		return Boolean.valueOf(false);
	}

	public Boolean update(String table, ContentValues values,
			String whereClause, String[] whereArgs) {
		if (testSQLiteDatabase().booleanValue()) {
			if (this.mSQLiteDatabase.update(table, values, whereClause,
					whereArgs) > 0)
				return Boolean.valueOf(true);
			return Boolean.valueOf(false);
		}

		TLog.e(this, "数据库未打开！");
		return Boolean.valueOf(false);
	}

	public Boolean update(Object entity) {
		return update(entity, null);
	}

	public Boolean update(Object entity, String where) {
		if (testSQLiteDatabase().booleanValue()) {
			TSqlBuilder getSqlBuilder = TSqlBuilderFactory.getInstance()
					.getSqlBuilder(3);
			getSqlBuilder.setEntity(entity);
			getSqlBuilder.setCondition(false, where, null, null, null, null);
			return execute(getSqlBuilder);
		}
		return Boolean.valueOf(false);
	}

	public String getLastSql() {
		return this.queryStr;
	}

	public Cursor getQueryCursor() {
		return this.queryCursor;
	}

	public void close() {
		this.mSQLiteDatabase.close();
	}

	public void free() {
		if (this.queryCursor != null)
			try {
				this.queryCursor.close();
			} catch (Exception localException) {
			}
	}

	public static class TADBParams {
		private String dbName = "tree.db";
		private int dbVersion = 1;

		public TADBParams() {
		}

		public TADBParams(String dbName, int dbVersion) {
			this.dbName = dbName;
			this.dbVersion = dbVersion;
		}

		public String getDbName() {
			return this.dbName;
		}

		public void setDbName(String dbName) {
			this.dbName = dbName;
		}

		public int getDbVersion() {
			return this.dbVersion;
		}

		public void setDbVersion(int dbVersion) {
			this.dbVersion = dbVersion;
		}
	}

	public static abstract interface TADBUpdateListener {
		public abstract void onUpgrade(SQLiteDatabase paramSQLiteDatabase,
				int paramInt1, int paramInt2);
	}
}
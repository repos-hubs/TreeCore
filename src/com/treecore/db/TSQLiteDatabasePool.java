package com.treecore.db;

import android.content.Context;
import com.treecore.utils.log.TLog;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

public class TSQLiteDatabasePool {
	private String testTable = "Sqlite_master";
	private int initialSQLiteDatabase = 2;
	private int incrementalSQLiteDatabase = 2;
	private int maxSQLiteDatabase = 10;
	private Vector<PooledSQLiteDatabase> pSQLiteDatabases = null;
	private Context context;
	private TSQLiteDatabase.TADBParams params;
	private TSQLiteDatabase.TADBUpdateListener mDBUpdateListener;
	private Boolean isWrite = Boolean.valueOf(false);
	private static HashMap<String, TSQLiteDatabasePool> poolMap = new HashMap();

	public static synchronized TSQLiteDatabasePool getInstance(Context context,
			TSQLiteDatabase.TADBParams params, Boolean isWrite) {
		String dbName = params.getDbName().trim();
		TSQLiteDatabasePool pool = (TSQLiteDatabasePool) poolMap.get(dbName);
		if (pool == null) {
			pool = new TSQLiteDatabasePool(context, params, isWrite);
			poolMap.put(dbName.trim(), pool);
		}
		return pool;
	}

	public static TSQLiteDatabasePool getInstance(Context context) {
		TSQLiteDatabase.TADBParams params = new TSQLiteDatabase.TADBParams();
		return getInstance(context, params, Boolean.valueOf(false));
	}

	public static TSQLiteDatabasePool getInstance(Context context,
			String dbName, int dbVersion, Boolean isWrite) {
		TSQLiteDatabase.TADBParams params = new TSQLiteDatabase.TADBParams(
				dbName, dbVersion);
		return getInstance(context, params, isWrite);
	}

	public TSQLiteDatabasePool(Context context,
			TSQLiteDatabase.TADBParams params, Boolean isWrite) {
		this.context = context;
		this.params = params;
		this.isWrite = isWrite;
	}

	public void setOnDbUpdateListener(
			TSQLiteDatabase.TADBUpdateListener dbUpdateListener) {
		this.mDBUpdateListener = dbUpdateListener;
	}

	public int getInitialSQLiteDatabase() {
		return this.initialSQLiteDatabase;
	}

	public void setInitialSQLiteDatabase(int initialSQLiteDatabase) {
		this.initialSQLiteDatabase = initialSQLiteDatabase;
	}

	public int getIncrementalSQLiteDatabase() {
		return this.incrementalSQLiteDatabase;
	}

	public void setIncrementalSQLiteDatabase(int incrementalSQLiteDatabase) {
		this.incrementalSQLiteDatabase = incrementalSQLiteDatabase;
	}

	public int getMaxSQLiteDatabase() {
		return this.maxSQLiteDatabase;
	}

	public void setMaxSQLiteDatabase(int maxSQLiteDatabase) {
		this.maxSQLiteDatabase = maxSQLiteDatabase;
	}

	public void setTestTable(String testTable) {
		this.testTable = testTable;
	}

	public String getTestTable() {
		return this.testTable;
	}

	public synchronized void createPool() {
		if (this.pSQLiteDatabases != null) {
			return;
		}

		this.pSQLiteDatabases = new Vector();

		createSQLiteDatabase(this.initialSQLiteDatabase);
		TLog.i(this, " 数据库连接池创建成功！ ");
	}

	private void createSQLiteDatabase(int numSQLiteDatabase) {
		for (int x = 0; x < numSQLiteDatabase; x++) {
			if ((this.maxSQLiteDatabase > 0)
					&& (this.pSQLiteDatabases.size() >= this.maxSQLiteDatabase)) {
				break;
			}
			try {
				this.pSQLiteDatabases.addElement(new PooledSQLiteDatabase(
						newSQLiteDatabase()));
			} catch (Exception e) {
				TLog.i(this, " 创建数据库连接失败！ " + e.getMessage());
			}
			TLog.i(this, "数据库连接己创建 ......");
		}
	}

	private TSQLiteDatabase newSQLiteDatabase() {
		TSQLiteDatabase sqliteDatabase = new TSQLiteDatabase(this.context,
				this.params);
		sqliteDatabase.openDatabase(this.mDBUpdateListener, this.isWrite);
		return sqliteDatabase;
	}

	public synchronized TSQLiteDatabase getSQLiteDatabase() {
		if (this.pSQLiteDatabases == null) {
			return null;
		}

		TSQLiteDatabase sqliteDatabase = getFreeSQLiteDatabase();

		while (sqliteDatabase == null) {
			wait(250);
			sqliteDatabase = getFreeSQLiteDatabase();
		}

		return sqliteDatabase;
	}

	private TSQLiteDatabase getFreeSQLiteDatabase() {
		TSQLiteDatabase sqLiteDatabase = findFreeSQLiteDatabase();
		if (sqLiteDatabase == null) {
			createSQLiteDatabase(this.incrementalSQLiteDatabase);

			sqLiteDatabase = findFreeSQLiteDatabase();
			if (sqLiteDatabase == null) {
				return null;
			}
		}
		return sqLiteDatabase;
	}

	private TSQLiteDatabase findFreeSQLiteDatabase() {
		TSQLiteDatabase sqliteDatabase = null;
		PooledSQLiteDatabase pSQLiteDatabase = null;

		Enumeration enumerate = this.pSQLiteDatabases.elements();

		while (enumerate.hasMoreElements()) {
			pSQLiteDatabase = (PooledSQLiteDatabase) enumerate.nextElement();
			if (!pSQLiteDatabase.isBusy()) {
				sqliteDatabase = pSQLiteDatabase.getSqliteDatabase();
				pSQLiteDatabase.setBusy(true);

				if (testSQLiteDatabase(sqliteDatabase))
					break;
				sqliteDatabase = newSQLiteDatabase();
				pSQLiteDatabase.setSqliteDatabase(sqliteDatabase);

				break;
			}
		}

		return sqliteDatabase;
	}

	private boolean testSQLiteDatabase(TSQLiteDatabase sqliteDatabase) {
		if (sqliteDatabase != null) {
			return sqliteDatabase.testSQLiteDatabase().booleanValue();
		}

		return false;
	}

	public void releaseSQLiteDatabase(TSQLiteDatabase sqLiteDatabase) {
		if (this.pSQLiteDatabases == null) {
			TLog.d(this, " 连接池不存在，无法返回此连接到连接池中 !");
			return;
		}
		PooledSQLiteDatabase pSqLiteDatabase = null;

		Enumeration enumerate = this.pSQLiteDatabases.elements();

		while (enumerate.hasMoreElements()) {
			pSqLiteDatabase = (PooledSQLiteDatabase) enumerate.nextElement();

			if (sqLiteDatabase == pSqLiteDatabase.getSqliteDatabase()) {
				pSqLiteDatabase.setBusy(false);
				break;
			}
		}
	}

	public synchronized void refreshSQLiteDatabase() {
		if (this.pSQLiteDatabases == null) {
			TLog.d(this, " 连接池不存在，无法刷新 !");
			return;
		}

		PooledSQLiteDatabase pSqLiteDatabase = null;
		Enumeration enumerate = this.pSQLiteDatabases.elements();
		while (enumerate.hasMoreElements()) {
			pSqLiteDatabase = (PooledSQLiteDatabase) enumerate.nextElement();

			if (pSqLiteDatabase.isBusy()) {
				wait(5000);
			}

			closeSQLiteDatabase(pSqLiteDatabase.getSqliteDatabase());
			pSqLiteDatabase.setSqliteDatabase(newSQLiteDatabase());
			pSqLiteDatabase.setBusy(false);
		}
	}

	public synchronized void closeSQLiteDatabase() {
		if (this.pSQLiteDatabases == null) {
			TLog.d(this, "连接池不存在，无法关闭 !");
			return;
		}
		PooledSQLiteDatabase pSqLiteDatabase = null;
		Enumeration enumerate = this.pSQLiteDatabases.elements();
		while (enumerate.hasMoreElements()) {
			pSqLiteDatabase = (PooledSQLiteDatabase) enumerate.nextElement();

			if (pSqLiteDatabase.isBusy()) {
				wait(5000);
			}

			closeSQLiteDatabase(pSqLiteDatabase.getSqliteDatabase());

			this.pSQLiteDatabases.removeElement(pSqLiteDatabase);
		}

		this.pSQLiteDatabases = null;
	}

	private void closeSQLiteDatabase(TSQLiteDatabase sqlLiteDatabase) {
		sqlLiteDatabase.close();
	}

	private void wait(int mSeconds) {
		try {
			Thread.sleep(mSeconds);
		} catch (InterruptedException localInterruptedException) {
		}
	}

	class PooledSQLiteDatabase {
		TSQLiteDatabase sqliteDatabase = null;
		boolean busy = false;

		public PooledSQLiteDatabase(TSQLiteDatabase sqliteDatabase) {
			this.sqliteDatabase = sqliteDatabase;
		}

		public TSQLiteDatabase getSqliteDatabase() {
			return this.sqliteDatabase;
		}

		public void setSqliteDatabase(TSQLiteDatabase sqliteDatabase) {
			this.sqliteDatabase = sqliteDatabase;
		}

		public boolean isBusy() {
			return this.busy;
		}

		public void setBusy(boolean busy) {
			this.busy = busy;
		}
	}
}
package com.treecore.db.sql;

public class TSqlBuilderFactory {
	private static TSqlBuilderFactory instance;
	public static final int INSERT = 0;
	public static final int SELECT = 1;
	public static final int DELETE = 2;
	public static final int UPDATE = 3;

	public static TSqlBuilderFactory getInstance() {
		if (instance == null) {
			instance = new TSqlBuilderFactory();
		}
		return instance;
	}

	public synchronized TSqlBuilder getSqlBuilder(int operate) {
		TSqlBuilder sqlBuilder = null;
		switch (operate) {
		case 0:
			sqlBuilder = new TInsertSqlBuilder();
			break;
		case 1:
			sqlBuilder = new TQuerySqlBuilder();
			break;
		case 2:
			sqlBuilder = new TDeleteSqlBuilder();
			break;
		case 3:
			sqlBuilder = new TUpdateSqlBuilder();
			break;
		}

		return sqlBuilder;
	}
}
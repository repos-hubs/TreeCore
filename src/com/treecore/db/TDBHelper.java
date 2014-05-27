package com.treecore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TDBHelper extends SQLiteOpenHelper {
	private TSQLiteDatabase.TADBUpdateListener mTadbUpdateListener;

	public TDBHelper(Context context, String name,
			SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public TDBHelper(Context context, String name,
			SQLiteDatabase.CursorFactory factory, int version,
			TSQLiteDatabase.TADBUpdateListener tadbUpdateListener) {
		super(context, name, factory, version);
		this.mTadbUpdateListener = tadbUpdateListener;
	}

	public void setOndbUpdateListener(
			TSQLiteDatabase.TADBUpdateListener tadbUpdateListener) {
		this.mTadbUpdateListener = tadbUpdateListener;
	}

	public void onCreate(SQLiteDatabase db) {
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (this.mTadbUpdateListener != null)
			this.mTadbUpdateListener.onUpgrade(db, oldVersion, newVersion);
	}
}
package com.treecore.db.entity;

public class TPKProperyEntity extends TPropertyEntity {
	public TPKProperyEntity() {
	}

	public TPKProperyEntity(String name, Class<?> type, Object defaultValue,
			boolean primaryKey, boolean isAllowNull, boolean autoIncrement,
			String columnName) {
		super(name, type, defaultValue, primaryKey, isAllowNull, autoIncrement,
				columnName);
	}
}
package com.treecore.db.entity;

public class TPropertyEntity {
	protected String name;
	protected String columnName;
	protected Class<?> type;
	protected Object defaultValue;
	protected boolean isAllowNull = true;
	protected int index;
	protected boolean primaryKey = false;
	protected boolean autoIncrement = false;

	public TPropertyEntity() {
	}

	public TPropertyEntity(String name, Class<?> type, Object defaultValue,
			boolean primaryKey, boolean isAllowNull, boolean autoIncrement,
			String columnName) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.primaryKey = primaryKey;
		this.isAllowNull = isAllowNull;
		this.autoIncrement = autoIncrement;
		this.columnName = columnName;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getType() {
		return this.type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Object getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isAllowNull() {
		return this.isAllowNull;
	}

	public void setAllowNull(boolean isAllowNull) {
		this.isAllowNull = isAllowNull;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getColumnName() {
		return this.columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
}
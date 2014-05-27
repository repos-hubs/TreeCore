package com.treecore.db.sql;

import android.text.TextUtils;
import com.treecore.db.exception.TDBException;
import com.treecore.utils.TDBUtils;
import com.treecore.utils.stl.TArrayList;
import org.apache.http.NameValuePair;

public abstract class TSqlBuilder {
	protected Boolean distinct;
	protected String where;
	protected String groupBy;
	protected String having;
	protected String orderBy;
	protected String limit;
	protected Class<?> clazz = null;
	protected String tableName = null;
	protected Object entity;
	protected TArrayList updateFields;

	public TSqlBuilder(Object entity) {
		this.entity = entity;
		setClazz(entity.getClass());
	}

	public Object getEntity() {
		return this.entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
		setClazz(entity.getClass());
	}

	public void setCondition(boolean distinct, String where, String groupBy,
			String having, String orderBy, String limit) {
		this.distinct = Boolean.valueOf(distinct);
		this.where = where;
		this.groupBy = groupBy;
		this.having = having;
		this.orderBy = orderBy;
		this.limit = limit;
	}

	public TArrayList getUpdateFields() {
		return this.updateFields;
	}

	public void setUpdateFields(TArrayList updateFields) {
		this.updateFields = updateFields;
	}

	public TSqlBuilder() {
	}

	public TSqlBuilder(Class<?> clazz) {
		setTableName(clazz);
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setTableName(Class<?> clazz) {
		this.tableName = TDBUtils.getTableName(clazz);
	}

	public String getTableName() {
		return this.tableName;
	}

	public Class<?> getClazz() {
		return this.clazz;
	}

	public void setClazz(Class<?> clazz) {
		setTableName(clazz);
		this.clazz = clazz;
	}

	public String getSqlStatement() throws TDBException,
			IllegalArgumentException, IllegalAccessException {
		onPreGetStatement();
		return buildSql();
	}

	public void onPreGetStatement() throws TDBException,
			IllegalArgumentException, IllegalAccessException {
	}

	public abstract String buildSql() throws TDBException,
			IllegalArgumentException, IllegalAccessException;

	protected String buildConditionString() {
		StringBuilder query = new StringBuilder(120);
		appendClause(query, " WHERE ", this.where);
		appendClause(query, " GROUP BY ", this.groupBy);
		appendClause(query, " HAVING ", this.having);
		appendClause(query, " ORDER BY ", this.orderBy);
		appendClause(query, " LIMIT ", this.limit);
		return query.toString();
	}

	protected void appendClause(StringBuilder s, String name, String clause) {
		if (!TextUtils.isEmpty(clause)) {
			s.append(name);
			s.append(clause);
		}
	}

	public String buildWhere(TArrayList conditions) {
		StringBuilder stringBuilder = new StringBuilder(256);
		if (conditions != null) {
			stringBuilder.append(" WHERE ");
			for (int i = 0; i < conditions.size(); i++) {
				NameValuePair nameValuePair = (NameValuePair) conditions.get(i);
				stringBuilder.append(nameValuePair.getName()).append(" = ")
						.append("'" + nameValuePair.getValue() + "'");
				if (i + 1 < conditions.size()) {
					stringBuilder.append(" AND ");
				}
			}
		}
		return stringBuilder.toString();
	}
}
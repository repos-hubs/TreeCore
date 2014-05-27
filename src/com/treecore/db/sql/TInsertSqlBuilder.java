package com.treecore.db.sql;

import com.treecore.db.annotation.TPrimaryKey;
import com.treecore.db.exception.TDBException;
import com.treecore.utils.TDBUtils;
import com.treecore.utils.stl.TArrayList;
import java.lang.reflect.Field;
import org.apache.http.NameValuePair;

public class TInsertSqlBuilder extends TSqlBuilder {
	public void onPreGetStatement() throws TDBException,
			IllegalArgumentException, IllegalAccessException {
		if (getUpdateFields() == null) {
			setUpdateFields(getFieldsAndValue(this.entity));
		}
		super.onPreGetStatement();
	}

	public String buildSql() throws TDBException, IllegalArgumentException,
			IllegalAccessException {
		StringBuilder columns = new StringBuilder(256);
		StringBuilder values = new StringBuilder(256);
		columns.append("INSERT INTO ");
		columns.append(this.tableName).append(" (");
		values.append("(");
		TArrayList updateFields = getUpdateFields();
		if (updateFields != null)
			for (int i = 0; i < updateFields.size(); i++) {
				NameValuePair nameValuePair = (NameValuePair) updateFields
						.get(i);
				columns.append(nameValuePair.getName());
				values.append("'" + nameValuePair.getValue() + "'");
				if (i + 1 < updateFields.size()) {
					columns.append(", ");
					values.append(", ");
				}
			}
		else {
			throw new TDBException("插入数据有误！");
		}
		columns.append(") values ");
		values.append(")");
		columns.append(values);
		return columns.toString();
	}

	public static TArrayList getFieldsAndValue(Object entity)
			throws TDBException, IllegalArgumentException,
			IllegalAccessException {
		TArrayList arrayList = new TArrayList();
		if (entity == null) {
			throw new TDBException("没有加载实体类！");
		}
		Class clazz = entity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if ((!TDBUtils.isTransient(field))
					&& (TDBUtils.isBaseDateType(field))) {
				TPrimaryKey annotation = (TPrimaryKey) field
						.getAnnotation(TPrimaryKey.class);
				if ((annotation == null) || (!annotation.autoIncrement())) {
					String columnName = TDBUtils.getColumnByField(field);
					field.setAccessible(true);
					arrayList
							.add((columnName != null)
									&& (!columnName.equals("")) ? columnName
									: field.getName(),
									field.get(entity) == null ? null : field
											.get(entity).toString());
				}
			}

		}

		return arrayList;
	}
}
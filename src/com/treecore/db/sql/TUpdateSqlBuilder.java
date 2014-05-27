package com.treecore.db.sql;

import com.treecore.db.annotation.TPrimaryKey;
import com.treecore.db.exception.TDBException;
import com.treecore.utils.TDBUtils;
import com.treecore.utils.TStringUtils;
import com.treecore.utils.stl.TArrayList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.apache.http.NameValuePair;

public class TUpdateSqlBuilder extends TSqlBuilder {
	public void onPreGetStatement() throws TDBException,
			IllegalArgumentException, IllegalAccessException {
		if (getUpdateFields() == null) {
			setUpdateFields(getFieldsAndValue(this.entity));
		}
		super.onPreGetStatement();
	}

	public String buildSql() throws TDBException, IllegalArgumentException,
			IllegalAccessException {
		StringBuilder stringBuilder = new StringBuilder(256);
		stringBuilder.append("UPDATE ");
		stringBuilder.append(this.tableName).append(" SET ");

		TArrayList needUpdate = getUpdateFields();
		for (int i = 0; i < needUpdate.size(); i++) {
			NameValuePair nameValuePair = (NameValuePair) needUpdate.get(i);
			stringBuilder.append(nameValuePair.getName()).append(" = ")
					.append("'" + nameValuePair.getValue() + "'");
			if (i + 1 < needUpdate.size()) {
				stringBuilder.append(", ");
			}
		}
		if (!TStringUtils.isEmpty(this.where))
			stringBuilder.append(buildConditionString());
		else {
			stringBuilder.append(buildWhere(buildWhere(this.entity)));
		}
		return stringBuilder.toString();
	}

	public TArrayList buildWhere(Object entity)
			throws IllegalArgumentException, IllegalAccessException,
			TDBException {
		Class clazz = entity.getClass();
		TArrayList whereArrayList = new TArrayList();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if ((!TDBUtils.isTransient(field))
					&& (TDBUtils.isBaseDateType(field))) {
				Annotation annotation = field.getAnnotation(TPrimaryKey.class);
				if (annotation != null) {
					String columnName = TDBUtils.getColumnByField(field);
					whereArrayList
							.add((columnName != null)
									&& (!columnName.equals("")) ? columnName
									: field.getName(), field.get(entity)
									.toString());
				}
			}

		}

		if (whereArrayList.isEmpty()) {
			throw new TDBException("不能创建Where条件，语句");
		}
		return whereArrayList;
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
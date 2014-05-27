package com.treecore.db.sql;

import com.treecore.db.exception.TDBException;
import com.treecore.utils.TDBUtils;
import com.treecore.utils.stl.TArrayList;
import java.lang.reflect.Field;

public class TDeleteSqlBuilder extends TSqlBuilder {
	public String buildSql() throws TDBException, IllegalArgumentException,
			IllegalAccessException {
		StringBuilder stringBuilder = new StringBuilder(256);
		stringBuilder.append("DELETE FROM ");
		stringBuilder.append(this.tableName);
		if (this.entity == null)
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
				if (!TDBUtils.isAutoIncrement(field)) {
					String columnName = TDBUtils.getColumnByField(field);
					if ((field.get(entity) != null)
							&& (field.get(entity).toString().length() > 0)) {
						whereArrayList
								.add((columnName != null)
										&& (!columnName.equals("")) ? columnName
										: field.getName(), field.get(entity)
										.toString());
					}
				}
			}
		}

		if (whereArrayList.isEmpty()) {
			throw new TDBException("不能创建Where条件，语句");
		}
		return whereArrayList;
	}
}
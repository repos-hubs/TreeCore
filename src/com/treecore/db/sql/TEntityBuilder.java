package com.treecore.db.sql;

import android.database.Cursor;
import com.treecore.utils.TDBUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TEntityBuilder {
	public static <T> List<T> buildQueryList(Class<T> clazz, Cursor cursor) {
		List queryList = new ArrayList();
		if (cursor.moveToFirst()) {
			do
				queryList.add(buildQueryOneEntity(clazz, cursor));
			while (cursor.moveToNext());
		}
		return queryList;
	}

	public static <T> T buildQueryOneEntity(Class<?> clazz, Cursor cursor) {
		Field[] fields = clazz.getDeclaredFields();
		Object entityT = null;
		try {
			entityT = clazz.newInstance();
			for (Field field : fields) {
				field.setAccessible(true);
				if ((!TDBUtils.isTransient(field))
						&& (TDBUtils.isBaseDateType(field))) {
					String columnName = TDBUtils.getColumnByField(field);
					field.setAccessible(true);
					setValue(field, columnName, entityT, cursor);
				}
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return entityT;
	}

	private static <T> void setValue(Field field, String columnName, T entityT,
			Cursor cursor) {
		try {
			int columnIndex = cursor.getColumnIndexOrThrow((columnName != null)
					&& (!columnName.equals("")) ? columnName : field.getName());
			Class clazz = field.getType();
			if (clazz.equals(String.class)) {
				field.set(entityT, cursor.getString(columnIndex));
			} else if ((clazz.equals(Integer.class))
					|| (clazz.equals(Integer.TYPE))) {
				field.set(entityT, Integer.valueOf(cursor.getInt(columnIndex)));
			} else if ((clazz.equals(Float.class))
					|| (clazz.equals(Float.TYPE))) {
				field.set(entityT, Float.valueOf(cursor.getFloat(columnIndex)));
			} else if ((clazz.equals(Double.class))
					|| (clazz.equals(Double.TYPE))) {
				field.set(entityT,
						Double.valueOf(cursor.getDouble(columnIndex)));
			} else if ((clazz.equals(Short.class))
					|| (clazz.equals(Short.class))) {
				field.set(entityT, Short.valueOf(cursor.getShort(columnIndex)));
			} else if ((clazz.equals(Long.class)) || (clazz.equals(Long.TYPE))) {
				field.set(entityT, Long.valueOf(cursor.getLong(columnIndex)));
			} else if ((clazz.equals(Byte.class)) || (clazz.equals(Byte.TYPE))) {
				field.set(entityT, cursor.getBlob(columnIndex));
			} else if (clazz.equals(Boolean.class)) {
				Boolean testBoolean = new Boolean(cursor.getString(columnIndex));
				field.set(entityT, testBoolean);
			} else if (clazz.equals(Date.class)) {
				Date date = new Date(cursor.getString(columnIndex));
				field.set(entityT, date);
			} else if ((clazz.equals(Character.class))
					|| (clazz.equals(Character.TYPE))) {
				Character c1 = Character.valueOf(cursor.getString(columnIndex)
						.trim().toCharArray()[0]);
				field.set(entityT, c1);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
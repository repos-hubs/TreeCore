package com.treecore.utils;

import android.content.Context;
import com.treecore.activity.annotation.TField;
import com.treecore.activity.annotation.TTransparent;
import java.lang.reflect.Field;

public class TReflectUtils {
	public static boolean isTransient(Field field) {
		return field.getAnnotation(TTransparent.class) != null;
	}

	public static boolean isBaseDateType(Field field) {
		Class clazz = field.getType();
		return (clazz.equals(String.class)) || (clazz.equals(Integer.class))
				|| (clazz.equals(Byte.class)) || (clazz.equals(Long.class))
				|| (clazz.equals(Double.class)) || (clazz.equals(Float.class))
				|| (clazz.equals(Character.class))
				|| (clazz.equals(Short.class)) || (clazz.equals(Boolean.class))
				|| (clazz.equals(java.util.Date.class))
				|| (clazz.equals(java.util.Date.class))
				|| (clazz.equals(java.sql.Date.class)) || (clazz.isPrimitive());
	}

	public static String getFieldName(Field field) {
		TField column = (TField) field.getAnnotation(TField.class);
		if ((column != null) && (column.name().trim().length() != 0)) {
			return column.name();
		}
		return field.getName();
	}

	public static String getDropBoxServiceName() throws NoSuchFieldException,
			IllegalAccessException {
		Field serviceName = Context.class.getField("DROPBOX_SERVICE");
		if (serviceName != null) {
			return (String) serviceName.get(null);
		}
		return null;
	}
}
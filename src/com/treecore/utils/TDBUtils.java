package com.treecore.utils;

import android.database.Cursor;
import com.treecore.db.annotation.TColumn;
import com.treecore.db.annotation.TPrimaryKey;
import com.treecore.db.annotation.TTableName;
import com.treecore.db.annotation.TTransient;
import com.treecore.db.entity.TPKProperyEntity;
import com.treecore.db.entity.TPropertyEntity;
import com.treecore.db.entity.TTableInfoEntity;
import com.treecore.db.exception.TDBException;
import com.treecore.db.sql.TEntityBuilder;
import com.treecore.db.sql.TTableInfofactory;
import com.treecore.utils.stl.THashMap;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TDBUtils {
	public static <T> List<T> getListEntity(Class<T> clazz, Cursor cursor) {
		List queryList = TEntityBuilder.buildQueryList(clazz, cursor);
		return queryList;
	}

	public static THashMap<String> getRowData(Cursor cursor) {
		if ((cursor != null) && (cursor.getColumnCount() > 0)) {
			THashMap hashMap = new THashMap();
			int columnCount = cursor.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				hashMap.put(cursor.getColumnName(i), cursor.getString(i));
			}
			return hashMap;
		}
		return null;
	}

	public static String getTableName(Class<?> clazz) {
		TTableName table = (TTableName) clazz.getAnnotation(TTableName.class);
		if ((table == null) || (TStringUtils.isEmpty(table.name()))) {
			return clazz.getName().toLowerCase().replace('.', '_');
		}
		return table.name();
	}

	public static Field getPrimaryKeyField(Class<?> clazz) {
		Field primaryKeyField = null;
		Field[] fields = clazz.getDeclaredFields();
		if (fields != null) {
			for (Field field : fields) {
				if (field.getAnnotation(TPrimaryKey.class) != null) {
					primaryKeyField = field;
					break;
				}
			}
			if (primaryKeyField == null) {
				for (Field field : fields) {
					if ("_id".equals(field.getName())) {
						primaryKeyField = field;
						break;
					}
				}
				if (primaryKeyField == null)
					for (Field field : fields)
						if ("id".equals(field.getName())) {
							primaryKeyField = field;
							break;
						}
			}
		} else {
			throw new RuntimeException("this model[" + clazz + "] has no field");
		}
		return primaryKeyField;
	}

	public static String getPrimaryKeyFieldName(Class<?> clazz) {
		Field f = getPrimaryKeyField(clazz);
		return f == null ? "id" : f.getName();
	}

	public static List<TPropertyEntity> getPropertyList(Class<?> clazz) {
		List plist = new ArrayList();
		try {
			Field[] fields = clazz.getDeclaredFields();
			String primaryKeyFieldName = getPrimaryKeyFieldName(clazz);
			for (Field field : fields) {
				if ((!isTransient(field)) && (isBaseDateType(field))) {
					if (!field.getName().equals(primaryKeyFieldName)) {
						TPKProperyEntity property = new TPKProperyEntity();

						property.setColumnName(getColumnByField(field));
						property.setName(field.getName());
						property.setType(field.getType());
						property.setDefaultValue(getPropertyDefaultValue(field));
						plist.add(property);
					}
				}
			}
			return plist;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String creatTableSql(Class<?> clazz) throws TDBException {
		TTableInfoEntity tableInfoEntity = TTableInfofactory.getInstance()
				.getTableInfoEntity(clazz);

		TPKProperyEntity pkProperyEntity = null;
		pkProperyEntity = tableInfoEntity.getPkProperyEntity();
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("CREATE TABLE IF NOT EXISTS ");
		strSQL.append(tableInfoEntity.getTableName());
		strSQL.append(" ( ");

		if (pkProperyEntity != null) {
			Class primaryClazz = pkProperyEntity.getType();
			if ((primaryClazz == Integer.TYPE)
					|| (primaryClazz == Integer.class)) {
				if (pkProperyEntity.isAutoIncrement())
					strSQL.append("\"").append(pkProperyEntity.getColumnName())
							.append("\"    ")
							.append("INTEGER PRIMARY KEY AUTOINCREMENT,");
				else
					strSQL.append("\"").append(pkProperyEntity.getColumnName())
							.append("\"    ").append("INTEGER PRIMARY KEY,");
			} else
				strSQL.append("\"").append(pkProperyEntity.getColumnName())
						.append("\"    ").append("TEXT PRIMARY KEY,");
		} else {
			strSQL.append("\"").append("id").append("\"    ")
					.append("INTEGER PRIMARY KEY AUTOINCREMENT,");
		}

		Collection<TPropertyEntity> propertys = tableInfoEntity.getPropertieArrayList();
		for (TPropertyEntity property : propertys) {
			strSQL.append("\"").append(property.getColumnName());
			strSQL.append("\",");
		}
		strSQL.deleteCharAt(strSQL.length() - 1);
		strSQL.append(" )");
		return strSQL.toString();
	}

	public static boolean isTransient(Field field) {
		return field.getAnnotation(TTransient.class) != null;
	}

	public static boolean isPrimaryKey(Field field) {
		return field.getAnnotation(TPrimaryKey.class) != null;
	}

	public static boolean isAutoIncrement(Field field) {
		TPrimaryKey primaryKey = (TPrimaryKey) field
				.getAnnotation(TPrimaryKey.class);
		if (primaryKey != null) {
			return primaryKey.autoIncrement();
		}
		return false;
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

	public static String getColumnByField(Field field) {
		TColumn column = (TColumn) field.getAnnotation(TColumn.class);
		if ((column != null) && (column.name().trim().length() != 0)) {
			return column.name();
		}
		TPrimaryKey primaryKey = (TPrimaryKey) field
				.getAnnotation(TPrimaryKey.class);
		if ((primaryKey != null) && (primaryKey.name().trim().length() != 0)) {
			return primaryKey.name();
		}
		return field.getName();
	}

	public static String getPropertyDefaultValue(Field field) {
		TColumn column = (TColumn) field.getAnnotation(TColumn.class);
		if ((column != null) && (column.defaultValue().trim().length() != 0)) {
			return column.defaultValue();
		}
		return null;
	}
}
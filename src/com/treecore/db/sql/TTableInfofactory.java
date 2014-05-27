package com.treecore.db.sql;

import com.treecore.db.entity.TPKProperyEntity;
import com.treecore.db.entity.TTableInfoEntity;
import com.treecore.db.exception.TDBException;
import com.treecore.utils.TDBUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TTableInfofactory {
	private static final HashMap<String, TTableInfoEntity> tableInfoEntityMap = new HashMap();
	private static TTableInfofactory instance;

	public static TTableInfofactory getInstance() {
		if (instance == null) {
			instance = new TTableInfofactory();
		}
		return instance;
	}

	public TTableInfoEntity getTableInfoEntity(Class<?> clazz)
			throws TDBException {
		if (clazz == null)
			throw new TDBException("表信息获取失败，应为class为null");
		TTableInfoEntity tableInfoEntity = (TTableInfoEntity) tableInfoEntityMap
				.get(clazz.getName());
		if (tableInfoEntity == null) {
			tableInfoEntity = new TTableInfoEntity();
			tableInfoEntity.setTableName(TDBUtils.getTableName(clazz));
			tableInfoEntity.setClassName(clazz.getName());
			Field idField = TDBUtils.getPrimaryKeyField(clazz);
			if (idField != null) {
				TPKProperyEntity pkProperyEntity = new TPKProperyEntity();
				pkProperyEntity.setColumnName(TDBUtils
						.getColumnByField(idField));
				pkProperyEntity.setName(idField.getName());
				pkProperyEntity.setType(idField.getType());
				pkProperyEntity.setAutoIncrement(TDBUtils
						.isAutoIncrement(idField));
				tableInfoEntity.setPkProperyEntity(pkProperyEntity);
			} else {
				tableInfoEntity.setPkProperyEntity(null);
			}
			List propertyList = TDBUtils.getPropertyList(clazz);
			if (propertyList != null) {
				tableInfoEntity.setPropertieArrayList(propertyList);
			}

			tableInfoEntityMap.put(clazz.getName(), tableInfoEntity);
		}
		if ((tableInfoEntity == null)
				|| (tableInfoEntity.getPropertieArrayList() == null)
				|| (tableInfoEntity.getPropertieArrayList().size() == 0)) {
			throw new TDBException("不能创建+" + clazz + "的表信息");
		}
		return tableInfoEntity;
	}
}
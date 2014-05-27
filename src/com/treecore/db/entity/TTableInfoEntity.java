package com.treecore.db.entity;

import java.util.ArrayList;
import java.util.List;

public class TTableInfoEntity extends TBaseEntity {
	private static final long serialVersionUID = 488168612576359150L;
	private String tableName = "";
	private String className = "";
	private TPKProperyEntity pkProperyEntity = null;

	ArrayList<TPropertyEntity> propertieArrayList = new ArrayList();

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ArrayList<TPropertyEntity> getPropertieArrayList() {
		return this.propertieArrayList;
	}

	public void setPropertieArrayList(List<TPropertyEntity> propertyList) {
		this.propertieArrayList = ((ArrayList) propertyList);
	}

	public TPKProperyEntity getPkProperyEntity() {
		return this.pkProperyEntity;
	}

	public void setPkProperyEntity(TPKProperyEntity pkProperyEntity) {
		this.pkProperyEntity = pkProperyEntity;
	}
}
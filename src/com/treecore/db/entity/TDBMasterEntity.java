package com.treecore.db.entity;

public class TDBMasterEntity extends TBaseEntity {
	private static final long serialVersionUID = 4511697615195446516L;
	private String type;
	private String name;
	private String tbl_name;
	private String sql;
	private int rootpage;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTbl_name() {
		return this.tbl_name;
	}

	public void setTbl_name(String tbl_name) {
		this.tbl_name = tbl_name;
	}

	public String getSql() {
		return this.sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public int getRootpage() {
		return this.rootpage;
	}

	public void setRootpage(int rootpage) {
		this.rootpage = rootpage;
	}
}
package com.owen.pDoctor.model;

import java.io.Serializable;

public class MyGroupBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public String id;
	public String doc_id;
	public String name;
	public String sort_no;
	public String is_default;
	public String add_time;
	public String upd_time;
	public String mark;
	public String list;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoc_id() {
		return doc_id;
	}

	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSort_no() {
		return sort_no;
	}

	public void setSort_no(String sort_no) {
		this.sort_no = sort_no;
	}

	public String getIs_default() {
		return is_default;
	}

	public void setIs_default(String is_default) {
		this.is_default = is_default;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getUpd_time() {
		return upd_time;
	}

	public void setUpd_time(String upd_time) {
		this.upd_time = upd_time;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}
}

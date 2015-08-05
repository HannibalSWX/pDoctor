package com.owen.pDoctor.model;

import java.io.Serializable;

/**
 * 推广类型bean
 * 
 */
public class SpreadTypeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String content;

	private String type;

	private String title;

	private String prId;

	private String pid;
	
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrId() {
		return prId;
	}

	public void setPrId(String prId) {
		this.prId = prId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

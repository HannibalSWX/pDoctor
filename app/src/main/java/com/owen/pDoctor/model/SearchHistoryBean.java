package com.owen.pDoctor.model;

import java.io.Serializable;

public class SearchHistoryBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public SearchHistoryBean() {

	}

	private int ids;
	private String historyContent;

	public SearchHistoryBean(int ids, String hc) {
		super();
		this.ids = ids;
		this.historyContent = hc;
	}

	public int getIds() {
		return ids;
	}

	public void setIds(int ids) {
		this.ids = ids;
	}

	public String getHistoryContent() {
		return historyContent;
	}

	public void setHistoryContent(String historyContent) {
		this.historyContent = historyContent;
	}

	@Override
	public String toString() {
		return "ProductInfo [ids=" + ids + ", historyContent=" + historyContent
				+ "]";
	}

}

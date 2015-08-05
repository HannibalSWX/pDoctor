package com.owen.pDoctor.model;

import java.io.Serializable;

public class AdvertisementWelcome implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String advertUrl;

	private String status;

	public String getAdvertUrl() {
		return advertUrl;
	}

	public void setAdvertUrl(String advertUrl) {
		this.advertUrl = advertUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

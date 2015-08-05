package com.owen.pDoctor.model;

import java.io.Serializable;

public class AdvertisementDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String skuId;

	private String advertUrl;

	private int contentType;

	private String imageUrl;

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public String getAdvertUrl() {
		return advertUrl;
	}

	public void setAdvertUrl(String advertUrl) {
		this.advertUrl = advertUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

}

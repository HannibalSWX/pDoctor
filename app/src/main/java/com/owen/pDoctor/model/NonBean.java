package com.owen.pDoctor.model;

import java.io.Serializable;

public class NonBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean isCheck;
	private String time;

	public boolean getCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

package com.owen.pDoctor.model;

import java.io.Serializable;

/**
 * 天数对应的金额bean
 * 
 */
public class SpreadAmountBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String days;

	private String amount;

	private String dayId;

	private String tittle;

	private String content;

	public String getTittle() {
		return tittle;
	}

	public void setTittle(String tittle) {
		this.tittle = tittle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDayId() {
		return dayId;
	}

	public void setDayId(String dayId) {
		this.dayId = dayId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

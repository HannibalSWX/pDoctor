package com.owen.pDoctor.model;

public class GroupBean {
	private String cattleId;
	private String groupId;
	private String groupName;
	private String userName;
	private String offDays;
	private String comments;

	public String getOffDays() {
		return offDays;
	}

	public void setOffDays(String offDays) {
		this.offDays = offDays;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCattleId() {
		return cattleId;
	}

	public void setCattleId(String cattleId) {
		this.cattleId = cattleId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}

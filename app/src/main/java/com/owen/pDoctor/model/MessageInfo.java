package com.owen.pDoctor.model;

public class MessageInfo {
	
	private String messageTime;
	private String messageMoneyString;
	private String messageID;
	private int messageStatus;
	public int getMessageStatus() {
		return messageStatus;
	}
	public void setMessageStatus(int messageStatus) {
		this.messageStatus = messageStatus;
	}
	public String getMessageTime() {
		return messageTime;
	}
	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}
	public String getMessageMoneyString() {
		return messageMoneyString;
	}
	public void setMessageMoneyString(String messageMoneyString) {
		this.messageMoneyString = messageMoneyString;
	}
	public String getMessageID() {
		return messageID;
	}
	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

}

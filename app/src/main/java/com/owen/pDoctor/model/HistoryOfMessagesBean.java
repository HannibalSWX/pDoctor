package com.owen.pDoctor.model;

import java.io.Serializable;

/**
 * 聊天历史消息bean
 * 
 */
public class HistoryOfMessagesBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String message_id;

	private String doctor_id;

	private String member_id;

	private String type;

	private String type_id;

	private String to_user_type;

	private String send_type;

	private String is_read;

	private String content;

	private String content_type;

	private String add_time;

	private String send_time;

	private String headimgurl;

	private String nickname;

	private String text; // 文本还是图片或者声音文件

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getDoctor_id() {
		return doctor_id;
	}

	public void setDoctor_id(String doctor_id) {
		this.doctor_id = doctor_id;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType_id() {
		return type_id;
	}

	public void setType_id(String type_id) {
		this.type_id = type_id;
	}

	public String getTo_user_type() {
		return to_user_type;
	}

	public void setTo_user_type(String to_user_type) {
		this.to_user_type = to_user_type;
	}

	public String getSend_type() {
		return send_type;
	}

	public void setSend_type(String send_type) {
		this.send_type = send_type;
	}

	public String getIs_read() {
		return is_read;
	}

	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getSend_time() {
		return send_time;
	}

	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

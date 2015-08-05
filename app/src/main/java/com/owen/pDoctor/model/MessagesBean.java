package com.owen.pDoctor.model;

import java.io.Serializable;

/**
 * 最近聊天消息bean
 * 
 */
public class MessagesBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String member_id;

	private String nickname;

	private String imgurl;

	private String noread_num;

	private String content;

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getNoread_num() {
		return noread_num;
	}

	public void setNoread_num(String noread_num) {
		this.noread_num = noread_num;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

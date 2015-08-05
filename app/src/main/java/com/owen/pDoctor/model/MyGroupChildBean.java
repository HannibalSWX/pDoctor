package com.owen.pDoctor.model;

import java.io.Serializable;

public class MyGroupChildBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public String member_id;
	public String wxname;
	public String nickname;
	public String realname;
	public String headimgurl;

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getWxname() {
		return wxname;
	}

	public void setWxname(String wxname) {
		this.wxname = wxname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

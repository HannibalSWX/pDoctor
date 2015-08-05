package com.owen.pDoctor.model;


public class Contact {
	Double _id;
	String name;
	String moblePhone;
	String remark;
	String imagePath;
	
	public Contact(){
	}
	
	public void setId(Double id){
		this._id = id;
	}
	
	public Double getId(){
		return _id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setMoblePhone(String moblePhone){
		this.moblePhone = moblePhone;
	}
	
	public String getMoblePhone(){
		return moblePhone;
	}
	
	public void setRemark(String remark){
		this.remark = remark;
	}
	
	public String getRemark(){
		return remark;
	}
	
	public void setImagePath(String imagePath){
		this.imagePath = imagePath;
	}
	
	public String getImagePath(){
		return imagePath;
	}
}

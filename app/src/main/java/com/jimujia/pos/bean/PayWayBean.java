package com.jimujia.pos.bean;

import java.io.Serializable;

public class PayWayBean implements Serializable {

	private int img;//图片icon
	private String text;//名称

	public PayWayBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

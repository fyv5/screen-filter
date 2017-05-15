package com.model;

public class Circle {
	
	private int color;	//颜色
	private int radius; //半径
	private float locationX;//x坐标
	private float locationY;//y坐标
	private int mode ;    //轮播模式
	
	public Circle() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Circle(int color, int radius, float locationX, float locationY,
                  int mode) {
		super();
		this.color = color;
		this.radius = radius;
		this.locationX = locationX;
		this.locationY = locationY;
		this.mode = mode;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public float getLocationX() {
		return locationX;
	}

	public void setLocationX(float locationX) {
		this.locationX = locationX;
	}

	public float getLocationY() {
		return locationY;
	}

	public void setLocationY(float locationY) {
		this.locationY = locationY;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "Circle [color=" + color + ", radius=" + radius + ", locationX="
				+ locationX + ", locationY=" + locationY + ", mode=" + mode
				+ "]";
	}
	
}

package com.dbz.framework.math;

public class Rectangle {
    public final Vector2 lowerLeft;
    public float width, height;
    
    public Rectangle(float x, float y, float width, float height) {
        this.lowerLeft = new Vector2(x,y);
        this.width = width;
        this.height = height;
    }
    
    public void setLowerLeft(float x, float y) {
    	this.lowerLeft.set(x, y);
    }
    
    public void setWidth(float width) {
    	this.width = width;
    }
    
    public void setHeight(float height) {
    	this.height = height;
    }
}

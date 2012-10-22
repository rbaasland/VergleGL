package com.dbz.framework.math;

public class Rectangle {
	
	// --------------
	// --- Fields ---
	// -------------- 
	
	public final Vector2 lowerLeft;
    public float width, height;
    
    // -------------------
 	// --- Constructor ---
 	// -------------------  
    public Rectangle(float x, float y, float width, float height) {
        this.lowerLeft = new Vector2(x,y);
        this.width = width;
        this.height = height;
    }

}

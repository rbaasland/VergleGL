package com.dbz.framework.gl;

import javax.microedition.khronos.opengles.GL10;

import com.dbz.framework.math.Vector2;

//TODO: spend time developing something like glLineStrip

public class ThickLineBatcher {        
    final float[] verticesBuffer;
    int bufferIndex;
    final Vertices vertices;
    int numSprites;
    
    public ThickLineBatcher(GLGraphics glGraphics, int maxLines) {                
        this.verticesBuffer = new float[maxLines*24]; //4 for each vertex, 6 for x,y + color    
        this.vertices = new Vertices(glGraphics, maxLines*4, 0, true, false);
        this.bufferIndex = 0;
        this.numSprites = 0;
    }       
    
    public void beginBatch() {
        numSprites = 0;
        bufferIndex = 0;
    }
    
    public void endBatch() {
        vertices.setVertices(verticesBuffer, 0, bufferIndex);
        vertices.bind();
        vertices.draw(GL10.GL_TRIANGLE_STRIP, 0, numSprites * 4);
        vertices.unbind();
    }
    
    
    public void drawLine(float width, Vector2 v1, Vector2 v2, float red, float green, float blue, float alpha) {
    	drawLine(width, v1.x, v1.y,  v2.x, v2.y, red, green, blue, alpha);	
    }
    
    //sets up line to be drawn between x1,y1 & x2,y2.  Sets color to values passed.
    public void drawLine(float width, float x1, float y1, float x2, float y2, float red, float green, float blue, float alpha) {
        
    	//need 4 points per line
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1 + width/2;
        verticesBuffer[bufferIndex++] = red;
        verticesBuffer[bufferIndex++] = green;
        verticesBuffer[bufferIndex++] = blue;
        verticesBuffer[bufferIndex++] = alpha;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1 - width/2;
        verticesBuffer[bufferIndex++] = red;
        verticesBuffer[bufferIndex++] = green;
        verticesBuffer[bufferIndex++] = blue;
        verticesBuffer[bufferIndex++] = alpha;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2 + width/2;
        verticesBuffer[bufferIndex++] = red;
        verticesBuffer[bufferIndex++] = green;
        verticesBuffer[bufferIndex++] = blue;
        verticesBuffer[bufferIndex++] = alpha;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2 - width/2;
        verticesBuffer[bufferIndex++] = red;
        verticesBuffer[bufferIndex++] = green;
        verticesBuffer[bufferIndex++] = blue;
        verticesBuffer[bufferIndex++] = alpha;
        
        numSprites++;
    }
}
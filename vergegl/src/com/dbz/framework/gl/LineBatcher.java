package com.dbz.framework.gl;

import javax.microedition.khronos.opengles.GL10;

import com.dbz.framework.math.Vector2;

public class LineBatcher {        
    final float[] verticesBuffer;
    int bufferIndex;
    final Vertices vertices;
    int numSprites;
    float lineWidth = 1;
    GL10 localGL;
    
    public LineBatcher(GLGraphics glGraphics, int maxLines, float lineWidth) {                
        this.verticesBuffer = new float[maxLines*12]; //*2 for vertex, *2 for x,y per vertex   
        this.vertices = new Vertices(glGraphics, maxLines*2, 0, true, false);
        this.bufferIndex = 0;
        this.numSprites = 0;
        localGL = glGraphics.gl;
        this.lineWidth = lineWidth;
        
    }       
    
    public void beginBatch() {
        numSprites = 0;
        bufferIndex = 0;
    }
    
    public void endBatch() {
    	localGL.glLineWidth(lineWidth); 
        vertices.setVertices(verticesBuffer, 0, bufferIndex);
        vertices.bind();
        vertices.draw(GL10.GL_LINES, 0, numSprites * 2);
        vertices.unbind();
    }
    
    /** Accepts a GL primative that should be drawn instead of default GL_LINES
     * @param GL10 final constants
     */
    public void endBatch(int primativeType) {
    	localGL.glLineWidth(lineWidth); 
        vertices.setVertices(verticesBuffer, 0, bufferIndex);
        vertices.bind();
        vertices.draw(primativeType, 0, numSprites * 2);
        vertices.unbind();
    }
    
    
    public void drawLine(Vector2 v1, Vector2 v2, float red, float green, float blue, float alpha) {
    	drawLine(v1.x, v1.y,  v2.x, v2.y, red, green, blue, alpha);	
    }
    
    //sets up line to be drawn between x1,y1 & x2,y2.  Sets color to values passed.
    public void drawLine(float x1, float y1, float x2, float y2, float red, float green, float blue, float alpha) {
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = red;
        verticesBuffer[bufferIndex++] = green;
        verticesBuffer[bufferIndex++] = blue;
        verticesBuffer[bufferIndex++] = alpha;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = red;
        verticesBuffer[bufferIndex++] = green;
        verticesBuffer[bufferIndex++] = blue;
        verticesBuffer[bufferIndex++] = alpha;
        
        numSprites++;
    }
    
    public static final float DEFAULT_LIGHTNING_DETAIL = 20f;
  
    /** 
     * Rule of thumb... detail should be a fraction of displacement. Detail determines how many segments are drawn
     */
    public void drawLightning(Vector2 v1, Vector2 v2,  float displace, float detail) {   //TODO overload method for color specification
    	drawLightning(v1.x, v1.y,  v2.x, v2.y, displace, detail);	
    }
    
    
    /** 
     * Rule of thumb... detail should be a fraction of displacement. Detail determines how many segments are drawn
     */
    public void drawLightning(float x1, float y1, float x2, float y2, float displace, float detail)
    {
      if (displace < detail) {
    	drawLine(x1, y1, x2, y2,    0, 1, 1, 1f); //default lighning color //TODO: pass in as parameter
      }
      else {
        float mid_x = (x2+x1)/2;
        float mid_y = (y2+y1)/2;
        mid_x += (Math.random()-.5)*displace;
        mid_y += (Math.random()-.5)*displace;
        drawLightning(x1,y1,mid_x,mid_y,displace/2, detail);
        drawLightning(x2,y2,mid_x,mid_y,displace/2, detail);
      }
    }
    
    
}
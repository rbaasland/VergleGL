package com.dbz.framework.gl;

import javax.microedition.khronos.opengles.GL10;

/*
 * Not being drawn... but should work based on results on clean project.
 * Could have something to do with 2d texturing.. will have to research (or ask don)
 * 
 * GL_LINE_STRIP has gaps between line segments when width is increased.
 * 
 * //TODO: Standard lines suck - figure out how to use triangles to make lines like in the circuit game
 * 
 */

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
        
    }       
    
    public void beginBatch() {
        numSprites = 0;
        bufferIndex = 0;
    }
    
    public void endBatch() {
    	localGL.glLineWidth(lineWidth); //sucks, cap is like 10 pixels thick.
        vertices.setVertices(verticesBuffer, 0, bufferIndex);
        vertices.bind();
        vertices.draw(GL10.GL_LINES, 0, numSprites * 2);
        vertices.unbind();
    }
    
    
    //sets up line to be drawn between x1,y1 & x2,y2.  Sets color to values passed.
    //vertices array must have color data, as specified by 'hascolor' in Vertices() construct. 
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
    
}
package com.dbz.framework.gl;

import javax.microedition.khronos.opengles.GL10;

import android.util.FloatMath;

import com.dbz.framework.impl.GLGraphics;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;

public class SpriteBatcher {        
    final float[] verticesBuffer;
    int bufferIndex;
    final Vertices vertices;
    int numSprites;    
    
    public SpriteBatcher(GLGraphics glGraphics, int maxSprites) {                
        this.verticesBuffer = new float[maxSprites*4*4];        
        this.vertices = new Vertices(glGraphics, maxSprites*4, maxSprites*6, false, true);
        this.bufferIndex = 0;
        this.numSprites = 0;
                
        short[] indices = new short[maxSprites*6];
        int len = indices.length;
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
                indices[i + 0] = (short)(j + 0);
                indices[i + 1] = (short)(j + 1);
                indices[i + 2] = (short)(j + 2);
                indices[i + 3] = (short)(j + 2);
                indices[i + 4] = (short)(j + 3);
                indices[i + 5] = (short)(j + 0);
        }
        vertices.setIndices(indices, 0, indices.length);                
    }       
    
    public void beginBatch(Texture texture) {
        texture.bind();
        numSprites = 0;
        bufferIndex = 0;
    }
    
    public void endBatch() {
        vertices.setVertices(verticesBuffer, 0, bufferIndex);
        vertices.bind();
        vertices.draw(GL10.GL_TRIANGLES, 0, numSprites * 6);
        vertices.unbind();
    }
    
    // Modified from Ch. 9's SpriteBatcher to take the bottom left coordinates, instead of the center.
    // This was done to avoid having to calculate two different x, y values (one for drawing and one for logic).
    // *** We should probably look into making another overload of this method that will take a Rectangle class instead of x, y, width, height. ***
    public void drawSprite(float x, float y, float width, float height, TextureRegion region) {
        float x1 = x;
        float y1 = y;
        float x2 = x + width;
        float y2 = y + height;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v1;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v1;
        
        numSprites++;
    }
    
    // Modified drawSprite method that takes a Rectangle object instead of the normal x, y, width, height
    public void drawSprite(Rectangle rectangle, TextureRegion region) {
    	float x1 = rectangle.lowerLeft.x;
        float y1 = rectangle.lowerLeft.y;
        float x2 = rectangle.lowerLeft.x + rectangle.width;
        float y2 = rectangle.lowerLeft.y + rectangle.height;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v1;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v1;
        
        numSprites++;
    }
    
    // ***THIS SPRITE CALL STILL NEEDS TO BE UPDATED TO USE THE BOTTOM LEFT COORDINATES INSTEAD OF THE CENTER COORDINATES***
    public void drawSprite(float x, float y, float width, float height, float angle, TextureRegion region) {
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        
        float rad = angle * Vector2.TO_RADIANS;
        float cos = FloatMath.cos(rad);
        float sin = FloatMath.sin(rad);
                
        float x1 = -halfWidth * cos - (-halfHeight) * sin;
        float y1 = -halfWidth * sin + (-halfHeight) * cos;
        float x2 = halfWidth * cos - (-halfHeight) * sin;
        float y2 = halfWidth * sin + (-halfHeight) * cos;
        float x3 = halfWidth * cos - halfHeight * sin;
        float y3 = halfWidth * sin + halfHeight * cos;
        float x4 = -halfWidth * cos - halfHeight * sin;
        float y4 = -halfWidth * sin + halfHeight * cos;
        
        x1 += x;
        y1 += y;
        x2 += x;
        y2 += y;
        x3 += x;
        y3 += y;
        x4 += x;
        y4 += y;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x3;
        verticesBuffer[bufferIndex++] = y3;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v1;
        
        verticesBuffer[bufferIndex++] = x4;
        verticesBuffer[bufferIndex++] = y4;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v1;
        
        numSprites++;
    }
    
    //Added drawSprite to draw from center, when resizing a Rectangle/Texture, drawing the height/width from the lowerleft 
    //is bothersome. - JT
    public void drawSpriteCenterCoords(Rectangle rectangle, TextureRegion region) {
        float halfWidth = rectangle.width / 2;
        float halfHeight = rectangle.height / 2;
        float x1 = rectangle.lowerLeft.x - halfWidth;
        float y1 = rectangle.lowerLeft.y - halfHeight;
        float x2 = rectangle.lowerLeft.x + halfWidth;
        float y2 = rectangle.lowerLeft.y + halfHeight;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v1;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v1;
        
        numSprites++;
    }
    
}
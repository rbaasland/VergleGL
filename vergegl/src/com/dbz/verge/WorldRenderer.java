package com.dbz.verge;

import javax.microedition.khronos.opengles.GL10;

import com.dbz.framework.gl.Camera2D;
import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.impl.GLGraphics;

public class WorldRenderer {
    static final float FRUSTUM_WIDTH = 1280;
    static final float FRUSTUM_HEIGHT = 800;    
    GLGraphics glGraphics;
    World world;
    Camera2D cam;
    SpriteBatcher batcher;    
    
    public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher, World world) {
        this.glGraphics = glGraphics;
        this.world = world;
        this.cam = new Camera2D(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        this.batcher = batcher;        
    }
    
    public void render() {
        cam.setViewportAndMatrices();
        renderBackground();
        renderObjects();
    }
    
    public void renderBackground() {
        batcher.beginBatch(Assets.background);
        batcher.drawSprite(0, 0, FRUSTUM_WIDTH, FRUSTUM_HEIGHT, Assets.backgroundRegion);
        batcher.endBatch();
    }
    
    public void renderObjects() {
        GL10 gl = glGraphics.getGL();
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
//        batcher.beginBatch(Assets.items);
//        // Draw assets.
//        batcher.endBatch();
        gl.glDisable(GL10.GL_BLEND);
    }
}

package com.dbz.verge;

import javax.microedition.khronos.opengles.GL10;

import com.dbz.framework.Game;
import com.dbz.framework.gl.Camera2D;
import com.dbz.framework.gl.Screen;
import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;

public abstract class Menu extends Screen {
	
	// --------------
	// --- Fields ---
	// --------------
	
	// OpenGL Related Objects.
    public Camera2D guiCam = new Camera2D(glGraphics, 1280, 800);
    public SpriteBatcher batcher = new SpriteBatcher(glGraphics, 100);
    
    // TouchPoint Vector and Bounding Boxes.
    public Vector2 touchPoint = new Vector2();
    public Rectangle soundToggleBounds = new Rectangle(1135, 5, 140, 140);

    // -------------------
 	// --- Constructor ---
    // -------------------
    public Menu(Game game) {
        super(game);                    
    }       

    // ---------------------
 	// --- Update Method ---
 	// ---------------------
    
    public abstract void update(float deltaTime);
    
    // Test if touchPoint is inside bounds that all MenuScreens share.
    // Currently, this is only the sound toggle.
    // Later, we can use this to add help button bounds to all MenuScreens.
    public void update(Vector2 touchPoint) {
        // Sound Toggle Bounds Check.
        if(OverlapTester.pointInRectangle(soundToggleBounds, touchPoint)) {
            Assets.playSound(Assets.clickSound);
            Settings.soundEnabled = !Settings.soundEnabled;
            if(Settings.soundEnabled) 
                Assets.music.play();
            else
                Assets.music.pause();
        }
    }
 
    // -------------------
 	// --- Draw Method ---
 	// -------------------
    @Override
    public void present(float deltaTime) {
        GL10 gl = glGraphics.getGL();        
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        guiCam.setViewportAndMatrices();
        
        // Prepares matrix for binding. 
        // (Tells OpenGL to apply the texture to the triangles we render.)
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
        // Draws the background.
        drawBackground();
        
        // Tells OpenGL to apply alpha blending to all triangles rendered until disabled. (pg 341)
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);         
        
        // Draws the foreground objects.
        drawObjects();
        
        // Draws bounding boxes. (Used for testing.)
        // drawBounds();
        
        gl.glDisable(GL10.GL_BLEND);
    }
    
    // ----------------------------
 	// --- Utility Draw Methods ---
 	// ----------------------------
    
    public abstract void drawBackground();
    
    public void drawObjects() {
        // Draws Sound Toggle.
        batcher.beginBatch(Assets.soundToggle);
        batcher.drawSprite(soundToggleBounds, Settings.soundEnabled?Assets.soundOnRegion:Assets.soundOffRegion);
        batcher.endBatch();
    }

    public void drawBounds() {
    	batcher.drawSprite(soundToggleBounds, Assets.boundOverlayRegion); // SoundToggle Bounding Box
    }
    
    // --------------------------------
 	// --- Android State Management ---
 	// --------------------------------
    
    @Override
    public void pause() {
        Settings.save(game.getFileIO());
    }

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
     
}

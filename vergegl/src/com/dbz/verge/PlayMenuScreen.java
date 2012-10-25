package com.dbz.verge;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.gl.Camera2D;
import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.impl.GLScreen;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;

public class PlayMenuScreen extends GLScreen {
	
	// --------------
	// --- Fields ---
	// --------------
	
	// OpenGL Related Objects.
    private Camera2D guiCam = new Camera2D(glGraphics, 1280, 800);
    private SpriteBatcher batcher = new SpriteBatcher(glGraphics, 100);
    
    // TouchPoint Vector and Bounding Boxes.
    private Vector2 touchPoint = new Vector2();
    private Rectangle gameGridBounds = new Rectangle(350, 510, 580, 100);
    private Rectangle survivalBounds = new Rectangle(350, 350, 580, 100);
    private Rectangle timeAttackBounds = new Rectangle(350, 190, 580, 100);
    private Rectangle soundToggleBounds = new Rectangle(1120, 0, 160, 160);
    private Rectangle backArrowBounds = new Rectangle(0, 0, 160, 160);

    // -------------------
 	// --- Constructor ---
    // -------------------
    public PlayMenuScreen(Game game) {
        super(game);                    
    }       

    // ---------------------
 	// --- Update Method ---
 	// ---------------------
    @Override
    public void update(float deltaTime) {
    	// Gets all TouchEvents and stores them in a list.
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        
     	// Cycles through and tests all touch events.
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
        	// Gets a single TouchEvent from the list.
            TouchEvent event = touchEvents.get(i);  
            
            if(event.type == TouchEvent.TOUCH_UP) {
            	// Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
                touchPoint.set(event.x, event.y);
                // Sends the vector to the OpenGL Camera for handling.
                guiCam.touchToWorld(touchPoint);
                
                // Game Grid Button Bounds Check.
                if(OverlapTester.pointInRectangle(gameGridBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new GameGridMenuScreen(game));
                    return;
                }
                
                // Survival Button Bounds Check.
                if(OverlapTester.pointInRectangle(survivalBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    // game.setScreen(new HighScoresScreen(game));
                    return;
                }
                
                // Time Attack Button Bounds Check.
                if(OverlapTester.pointInRectangle(timeAttackBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    //  game.setScreen(new HelpScreen(game));
                    return;
                }
                
                // Sound Toggle Bounds Check.
                if(OverlapTester.pointInRectangle(soundToggleBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    Settings.soundEnabled = !Settings.soundEnabled;
                    if(Settings.soundEnabled) 
                        Assets.music.play();
                    else
                        Assets.music.pause();
                }
                
                // Back Arrow Bounds Check.
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
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
    
    public void drawBackground() {
        batcher.beginBatch(Assets.background);
        batcher.drawSprite(0, 0, 1280, 800, Assets.backgroundRegion);
        batcher.endBatch();
    }
    
    public void drawObjects() {
        // Draws Play Menu Buttons.
        batcher.beginBatch(Assets.playMenuButtons);
        batcher.drawSprite(0, 0, 1280, 800, Assets.playMenuButtonsRegion);
        batcher.endBatch();
        
        // Draws Sound Toggle.
        batcher.beginBatch(Assets.soundToggle);
        batcher.drawSprite(soundToggleBounds, Settings.soundEnabled?Assets.soundOnRegion:Assets.soundOffRegion);
        batcher.endBatch();
        
        // Draws Back Arrow.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
        batcher.endBatch(); 
    }
    
    public void drawBounds() {
      batcher.beginBatch(Assets.boundOverlay);     
      batcher.drawSprite(gameGridBounds, Assets.boundOverlayRegion); 	// Game Grid Button Bounding Box
      batcher.drawSprite(survivalBounds, Assets.boundOverlayRegion);  	// Survival Button Bounding Box
      batcher.drawSprite(timeAttackBounds, Assets.boundOverlayRegion); 	// Time Attack Bounding Box
      batcher.drawSprite(soundToggleBounds, Assets.boundOverlayRegion); // SoundToggle Bounding Box
      batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); 	// Back Arrow Bounding Box
      batcher.endBatch();
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

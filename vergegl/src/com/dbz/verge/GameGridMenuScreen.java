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
import com.dbz.verge.microgames.BroFistMicroGame;
import com.dbz.verge.microgames.CircuitMicroGame;
import com.dbz.verge.microgames.FireMicroGame;
import com.dbz.verge.microgames.FlyMicroGame;
import com.dbz.verge.microgames.TrafficMicroGame;

public class GameGridMenuScreen extends GLScreen {
   
	// --------------
	// --- Fields ---
	// --------------
	
	// OpenGL Related Objects.
	private Camera2D guiCam = new Camera2D(glGraphics, 1280, 800);
    private SpriteBatcher batcher = new SpriteBatcher(glGraphics, 100);;
    
    // TouchPoint Vector and Bounding Boxes.
    private Vector2 touchPoint = new Vector2(); 
    private Rectangle firstMicroGameBounds = new Rectangle(315, 435, 170, 170);
    private Rectangle secondMicroGameBounds = new Rectangle(555, 435, 170, 170);
    private Rectangle thirdMicroGameBounds = new Rectangle(795, 435, 170, 170);
    private Rectangle fourthMicroGameBounds = new Rectangle(315, 200, 170, 170);
    private Rectangle fifthMicroGameBounds = new Rectangle(555, 200, 170, 170);
    private Rectangle sixthMicroGameBounds = new Rectangle(795, 200, 170, 170);
    private Rectangle soundToggleBounds = new Rectangle(1120, 0, 160, 160);
    private Rectangle backArrowBounds = new Rectangle(0, 0, 160, 160);
    private Rectangle nextPageBounds = new Rectangle(345, 30, 75, 100);
    private Rectangle prevPageBounds = new Rectangle(860, 30, 75, 100);

    // -------------------
 	// --- Constructor ---
    // -------------------
    public GameGridMenuScreen(Game game) {
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
                
                // First MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(firstMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	game.setScreen(new BroFistMicroGame(game));
                	return;
                }
                
                // Second MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(secondMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	game.setScreen(new FlyMicroGame(game));
                	return;
                }
                
                // Third MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(thirdMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	game.setScreen(new FireMicroGame(game));
                	return;
                }
                
                // Fourth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(fourthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	game.setScreen(new TrafficMicroGame(game));
                	return;
                }
                
                // Fifth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(fifthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	game.setScreen(new CircuitMicroGame(game));
                	return;
                }
                
                // Sixth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(sixthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// Set the screen to a new MicroGameScreen.
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
                    game.setScreen(new PlayMenuScreen(game));
                    return;
                }
                
                // Next Page Bounds Check.
                if (OverlapTester.pointInRectangle(nextPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// increment page.
                	return;
                }
                
                // Previous Page Bounds Check.
                if (OverlapTester.pointInRectangle(prevPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// decrement page.
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
    	batcher.beginBatch(Assets.gameGridBackground);
        batcher.drawSprite(0, 0, 1280, 800, Assets.gameGridBackgroundRegion);
        batcher.endBatch();
    }
    
    public void drawObjects() {
        // Draws Game Grid Icons.
        batcher.beginBatch(Assets.gameGridIcons);
        batcher.drawSprite(0, 0, 1024, 800, Assets.gameGridIconsRegion);
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
        batcher.drawSprite(firstMicroGameBounds, Assets.boundOverlayRegion);	// 1st MicroGame Bounding Box
        batcher.drawSprite(secondMicroGameBounds, Assets.boundOverlayRegion); 	// 2nd MicroGame Bounding Box
        batcher.drawSprite(thirdMicroGameBounds, Assets.boundOverlayRegion); 	// 3rd MicroGame Bounding Box
        batcher.drawSprite(fourthMicroGameBounds, Assets.boundOverlayRegion); 	// 4th MicroGame Bounding Box
        batcher.drawSprite(fifthMicroGameBounds, Assets.boundOverlayRegion); 	// 5th MicroGame Bounding Box
        batcher.drawSprite(sixthMicroGameBounds, Assets.boundOverlayRegion); 	// 6th MicroGame Bounding Box
        batcher.drawSprite(soundToggleBounds, Assets.boundOverlayRegion); 	// SoundToggle Bounding Box
        batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); 	// Back Arrow Bounding Box
        batcher.drawSprite(nextPageBounds, Assets.boundOverlayRegion); 		// Next Page Bounding Box
        batcher.drawSprite(prevPageBounds, Assets.boundOverlayRegion); 		// Previous Page Bounding Box
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

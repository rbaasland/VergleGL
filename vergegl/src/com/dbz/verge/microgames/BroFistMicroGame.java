package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;

// TODO: Add Smoke, Fire, and Explosion visuals.
//		 Add BroFist, Smoke, Fire, and Explosion sounds.
public class BroFistMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------
	
	// Assets
    private static Texture broFistBackground;
    private static TextureRegion broFistBackgroundRegion;
    private static Texture broFist;
    private static TextureRegion broFistRegion;
	
	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredBroFistCount[] = { 5, 10, 15 };
	private int broFistCount = 0;
	
	// Bounds for touch detection.
	private Rectangle broFistBounds = new Rectangle(480, 280, 320, 240);
	
	// -------------------
	// --- Constructor ---
	// ------------------- 
	
    public BroFistMicroGame() {}
    
    public void load() {
    	broFistBackground = new Texture("brofistbackground.png");
        broFistBackgroundRegion = new TextureRegion(broFistBackground, 0, 0, 1280, 800);
        broFist = new Texture("brofist.png");
        broFistRegion = new TextureRegion(broFist, 0, 0, 320, 240);
    }
    
	@Override
	public void unload() {
		// TODO Auto-generated method stub
		broFist.dispose();
		broFistBackground.dispose();
		
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		broFist.reload();
		broFistBackground.reload();
		
	}
	// ---------------------
	// --- Update Method ---
	// ---------------------
    
	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based loss.
		if (lostTimeBased(deltaTime)) {
			AssetsManager.playSound(AssetsManager.gruntSound);
			return;
		}
		
		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);
	        
	        // Skip handling if the TouchEvent is TOUCH_DRAGGED.
	        if(event.type == TouchEvent.TOUCH_DRAGGED)
	            continue;
	        
	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
        	touchPoint.set(event.x, event.y);
        	// Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
	        // BroFist Bounds (TOUCH_DOWN) Check.
        	if (targetTouchDown(event, touchPoint, broFistBounds)) {
        		broFistCount++;
        		if (broFistCount == requiredBroFistCount[level-1]) {
        			AssetsManager.playSound(AssetsManager.explosionSound);
        			microGameState = MicroGameState.Won;
        		}
        		else if (broFistCount < requiredBroFistCount[level-1])
        			AssetsManager.playSound(AssetsManager.punchSound);
        		return;
        	}
	        
        	// Non-Unique, Super Class Bounds (TOUCH_UP) Check.
	        if (event.type == TouchEvent.TOUCH_UP)
	        	super.updateRunning(touchPoint);
	    }   
	}

	// -----------------------------
	// --- Utility Update Method ---
	// -----------------------------
	
	@Override
	public void reset() {
		super.reset();
		broFistCount = 0;
	}
	
	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		drawRunningBackground();
		drawRunningObjects();
		// drawRunningBounds();
		drawInstruction("Unleash your Bro-ness!");
		super.presentRunning();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	@Override
	public void drawRunningBackground() {
		// Draw background.
		batcher.beginBatch(broFistBackground);
		batcher.drawSprite(0, 0, 1280, 800, broFistBackgroundRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningObjects() {
		// Draw BroFist.
		batcher.beginBatch(broFist);
		batcher.drawSprite(broFistBounds, broFistRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(AssetsManager.boundOverlay);
	    batcher.drawSprite(broFistBounds, AssetsManager.boundOverlayRegion); // BroFist Bounding Box
	    batcher.endBatch();
	}
	
}

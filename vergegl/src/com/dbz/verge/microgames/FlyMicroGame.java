package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

public class FlyMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------

	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredFlySwatCount[] = { 1, 2, 3 };
	private int flySwatCount = 0;

	private int accelX = 10; 
	private int accelY = 0;
	
	// Bounds for touch detection.
	private Rectangle flyBounds = new Rectangle(600, 60, 80, 60);
	
	// -------------------
	// --- Constructor ---
	// -------------------   
    public FlyMicroGame(Game game) {
        super(game);
    }

	// ---------------------
	// --- Update Method ---
	// ---------------------
    
	@Override
	public void updateRunning(float deltaTime) {
		totalRunningTime += deltaTime;

		// Moves fly in a pre-defined way.
		moveFly();
		
		// Checks for time-based loss.
		if (lostTimeBased()) {
			Assets.playSound(Assets.hitSound);
			return;
		}
		
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
        	touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        // Tests if target (brofist) is touched.
        	if (targetTouched(event, touchPoint, flyBounds)) {
        		flySwatCount++;
        		if (flySwatCount == requiredFlySwatCount[level-1]) {
        			Assets.playSound(Assets.highJumpSound);
        			microGameState = MicroGameState.Won;
        		}
        		else if (flySwatCount < requiredFlySwatCount[level-1])
        			Assets.playSound(Assets.coinSound);
        		return;
        	}
	        
        	// Tests for non-unique touch events, which is currently pause only.
	        if (event.type == TouchEvent.TOUCH_UP)
	        	super.updateRunning(touchPoint);
	    }   
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	public void moveFly() {
		float x = flyBounds.lowerLeft.x;
		float y = flyBounds.lowerLeft.y;
		
		if (x == 1200) {
			accelX = -10;
			accelY = 5;
		} else if (x == 80) {
			accelX = 10;
			accelY = -5;
		} else if (x == 600) {
			if (accelY > 0)
				accelY = -5;
			else 
				accelY = 5;
		}
		x += accelX;
		y += accelY;
		
		flyBounds.lowerLeft.set(x, y);
	}

	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		drawBackground();
		drawObjects();
		// drawRunningBounds();
		drawInstruction("Swat the Fly!");
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawBackground() {
		// Draw the background.
		batcher.beginBatch(Assets.flyBackground);
		batcher.drawSprite(0, 0, 1280, 800, Assets.flyBackgroundRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawObjects() {
		// Draw the fly.
		batcher.beginBatch(Assets.fly);
		batcher.drawSprite(flyBounds, Assets.flyRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(flyBounds, Assets.boundOverlayRegion); // Fly Bounding Box
	    batcher.endBatch();
	}
	
}

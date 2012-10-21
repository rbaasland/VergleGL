package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// *** Need to create a standard for handling multiple difficulty levels. ***
public class CircuitMicroGame extends MicroGame {
    
	
	// --------------
	// --- Fields ---
	// --------------
	
	public boolean touchingFistOne = false, touchingFistTwo = false;
	// *** Need to create a standard for handling multiple difficulty levels. ***
	private int level = 1;

	private Rectangle brofistOneBounds;
	private Rectangle brofistTwoBounds;
	
	// -------------------
	// --- Constructor ---
	// -------------------   
    public CircuitMicroGame(Game game) {
        super(game);
        
        totalAllowedTime = 300.0f;
        
        // Initialize bounds for touch detection.
        brofistOneBounds = new Rectangle(150, 280, 320, 240);
        brofistTwoBounds = new Rectangle(480, 280, 320, 240);
    }

	// ---------------------
	// --- Update Method ---
	// ---------------------
    
	@Override
	public void updateRunning(float deltaTime) {
		totalRunningTime += deltaTime;
		
		// Checks for time-based loss.
		if (lostTimeBased())
			return;
		
		if (touchingFistOne && touchingFistTwo) {
			microGameState = MicroGameState.Won;
    		return;	
		}
		
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
        	touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
		    // Tests if target (brofist) is touched.
	        if (targetTouched(event, touchPoint, brofistOneBounds))
	        	touchingFistOne = true;
	        else
	        	touchingFistOne = false;
	        	
	        if (targetTouched(event, touchPoint, brofistTwoBounds))
	        	touchingFistTwo = true;
	        else
	        	touchingFistTwo = false;
	        
        	// Tests for non-unique touch events, which is currently pause only.
	        if (event.type == TouchEvent.TOUCH_UP)
	        	super.updateRunning(deltaTime, touchPoint);
	    }   
	}

	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		drawInstruction("BROFIST!" + String.valueOf(touchingFistOne) + String.valueOf(touchingFistTwo));
		
		// Draw Brofist.
		if (!touchingFistOne) {
			batcher.beginBatch(Assets.brofist);
			batcher.drawSprite(150, 280, 320, 240, Assets.brofistRegion);
			batcher.endBatch();
		}
		if (!touchingFistTwo) {
			batcher.beginBatch(Assets.brofist);
			batcher.drawSprite(480, 280, 320, 240, Assets.brofistRegion);
			batcher.endBatch();
		}
		
		// drawRunningBounds();
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(480, 280, 320, 240, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.endBatch();
	}
	
}

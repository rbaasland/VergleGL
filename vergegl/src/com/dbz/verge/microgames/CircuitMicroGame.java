package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

public class CircuitMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------

	// Boolean to track target touching.
	private boolean touchingFistOne = false; 
	private boolean touchingFistTwo = false;

	// Bounds for touch detection.
	private Rectangle broFistOneBounds = new Rectangle(150, 280, 320, 240);
	private Rectangle broFistTwoBounds = new Rectangle(480, 280, 320, 240);
	
	// -------------------
	// --- Constructor ---
	// -------------------
	
    public CircuitMicroGame(Game game) {
        super(game);
        
        // Extend allowed time for testing.
        totalMicroGameTime = 300.0f;
    }

	// ---------------------
	// --- Update Method ---
	// ---------------------
    
	@Override
	public void updateRunning(float deltaTime) {
		totalRunningTime += deltaTime;
		
		// Checks for time-based loss.
		if (lostTimeBased()) {
			Assets.playSound(Assets.hitSound);
			return;
		}
		
		// Checks for multi-touch win.
		if (touchingFistOne && touchingFistTwo) {
			Assets.playSound(Assets.highJumpSound);
			microGameState = MicroGameState.Won;
    		return;	
		}
		
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
        	touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	       
	        // Tests if target #1 is being touched.
	        if (targetTouched(event, touchPoint, broFistOneBounds))
	        	touchingFistOne = true;
	        else
	        	touchingFistOne = false;
	        
	        // Tests if target #2 is being touched.
	        if (targetTouched(event, touchPoint, broFistTwoBounds))
	        	touchingFistTwo = true;
	        else
	        	touchingFistTwo = false;
	        
	        //check if targets are multi-touched //note putting here has O(n^2) implications on touch events -- targetsMultiTouched also iters TouchEvents
	        if (targetsMultiTouched(touchEvents, broFistOneBounds, broFistTwoBounds)){
	        	touchingFistOne = true;  touchingFistTwo = true;
	        }   

        	// Tests for non-unique touch events, which is currently pause only.
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
		touchingFistOne = false;
		touchingFistTwo = false;
	}
	
	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		// Draw target #1.
		if (!touchingFistOne) {
			batcher.beginBatch(Assets.broFist);
			batcher.drawSprite(broFistOneBounds, Assets.broFistRegion);
			batcher.endBatch();
		}
		// Draw target #2.
		if (!touchingFistTwo) {
			batcher.beginBatch(Assets.broFist);
			batcher.drawSprite(broFistTwoBounds, Assets.broFistRegion);
			batcher.endBatch();
		}
		
		 drawRunningBounds();
		drawInstruction("Connect the Circuit!" + String.valueOf(touchingFistOne) + String.valueOf(touchingFistTwo));
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawBackground() {}
	
	@Override
	public void drawObjects() {}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(broFistOneBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(broFistTwoBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.endBatch();
	}
	
}

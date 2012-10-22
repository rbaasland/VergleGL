package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

public class FireMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------
	
	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredBroFistCount[] = { 1, 2, 3 };
	private int broFistOneCount = 0; 
	private int broFistTwoCount = 0;
	private int broFistThreeCount = 0;
	
	// Boolean used to track which targets have been touched.
	private boolean tappedFistOne = false;
	private boolean tappedFistTwo = false;
	private boolean tappedFistThree = false;
	
	// Bounds for touch detection.
	private Rectangle broFistOneBounds = new Rectangle(150, 280, 320, 240);
	private Rectangle broFistTwoBounds = new Rectangle(480, 280, 320, 240);
	private Rectangle broFistThreeBounds = new Rectangle(810, 280, 320, 240);
	
	// -------------------
	// --- Constructor ---
	// -------------------   
    public FireMicroGame(Game game) {
        super(game);
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
		
		// Tests for multi-area win.
		if (tappedFistOne && tappedFistTwo && tappedFistThree) {
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
	        
	        // *** Might be able to generalize the below three major condition blocks into an array
	        // whichs gets cycled through via a for loop. ***
	        
	        // Tests if target #1 is touched.
	        if (targetTouched(event, touchPoint, broFistOneBounds)) {
        		broFistOneCount++;
        		if (broFistOneCount == requiredBroFistCount[level-1])
        			tappedFistOne = true;
        		if (broFistOneCount <= requiredBroFistCount[level-1])
        			Assets.playSound(Assets.coinSound);
        		return;
        	}
        	
	        // Tests if target #2 is touched.
        	if (targetTouched(event, touchPoint, broFistTwoBounds)) {
        		broFistTwoCount++;
        		if (broFistTwoCount == requiredBroFistCount[level-1])
        			tappedFistTwo = true;
        		if (broFistTwoCount <= requiredBroFistCount[level-1])
        			Assets.playSound(Assets.coinSound);
        		return;
        	}
        	
        	// Tests if target #3 is touched.
        	if (targetTouched(event, touchPoint, broFistThreeBounds)) {
        		broFistThreeCount++;
        		if (broFistThreeCount == requiredBroFistCount[level-1])
        			tappedFistThree = true;
        		if (broFistThreeCount <= requiredBroFistCount[level-1])
        			Assets.playSound(Assets.coinSound);
        		return;
        	}
	        
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
		drawInstruction("BROFIST!");
		
		// Draw target #1.
		if (broFistOneCount < requiredBroFistCount[level-1]) {
			batcher.beginBatch(Assets.broFist);
			batcher.drawSprite(broFistOneBounds, Assets.broFistRegion);
			batcher.endBatch();
		}
		// Draw target #2.
		if (broFistTwoCount < requiredBroFistCount[level-1]) {
			batcher.beginBatch(Assets.broFist);
			batcher.drawSprite(broFistTwoBounds, Assets.broFistRegion);
			batcher.endBatch();
		}
		// Draw target #3.
		if (broFistThreeCount < requiredBroFistCount[level-1]) {
			batcher.beginBatch(Assets.broFist);
			batcher.drawSprite(broFistThreeBounds, Assets.broFistRegion);
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
	    batcher.drawSprite(broFistOneBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(broFistTwoBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(broFistThreeBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.endBatch();
	}
	
}

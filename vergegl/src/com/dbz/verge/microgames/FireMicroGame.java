package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// *** Need to create a standard for handling multiple difficulty levels. ***
public class FireMicroGame extends MicroGame {
    
	
	// --------------
	// --- Fields ---
	// --------------
	
	public boolean tappedFistOne = false,tappedFistTwo = false,tappedFistThree = false;
	// *** Need to create a standard for handling multiple difficulty levels. ***
	private int level = 1;
	private int requiredBroFistCount[] = { 1, 2, 3 };
	private int broFistOneCount;
	private int broFistTwoCount;
	private int broFistThreeCount;
	
	private Rectangle brofistOneBounds;
	private Rectangle brofistTwoBounds;
	private Rectangle brofistThreeBounds;
	
	// -------------------
	// --- Constructor ---
	// -------------------   
    public FireMicroGame(Game game) {
        super(game);
        
        broFistOneCount = 0;
        broFistTwoCount = 0;
        broFistThreeCount = 0;
        
        // Initialize bounds for touch detection.
        brofistOneBounds = new Rectangle(150, 280, 320, 240);
        brofistTwoBounds = new Rectangle(480, 280, 320, 240);
        brofistThreeBounds = new Rectangle(810, 280, 320, 240);
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
		
		if (tappedFistOne && tappedFistTwo && tappedFistThree) {
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
	        if (targetTouched(event, touchPoint, brofistOneBounds)) {
        		broFistOneCount++;
        		if (broFistOneCount == requiredBroFistCount[level-1])
        			tappedFistOne = true;
        		if (broFistOneCount <= requiredBroFistCount[level-1])
        			Assets.playSound(Assets.coinSound);
        		return;
        	}
        	
        	if (targetTouched(event, touchPoint, brofistTwoBounds)) {
        		broFistTwoCount++;
        		if (broFistTwoCount == requiredBroFistCount[level-1])
        			tappedFistTwo = true;
        		if (broFistTwoCount <= requiredBroFistCount[level-1])
        			Assets.playSound(Assets.coinSound);
        		return;
        	}
        	
        	if (targetTouched(event, touchPoint, brofistThreeBounds)) {
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
		
		// Draw Brofist.
		if (broFistOneCount < requiredBroFistCount[level-1]) {
			batcher.beginBatch(Assets.brofist);
			batcher.drawSprite(150, 280, 320, 240, Assets.brofistRegion);
			batcher.endBatch();
		}
		if (broFistTwoCount < requiredBroFistCount[level-1]) {
			batcher.beginBatch(Assets.brofist);
			batcher.drawSprite(480, 280, 320, 240, Assets.brofistRegion);
			batcher.endBatch();
		}
		if (broFistThreeCount < requiredBroFistCount[level-1]) {
			batcher.beginBatch(Assets.brofist);
			batcher.drawSprite(810, 280, 320, 240, Assets.brofistRegion);
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

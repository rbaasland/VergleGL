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
	private int requiredBroFistCount[] = { 10, 20, 30 };
	private int fireOneCount = 0; 
	private int fireTwoCount = 0;
	private int fireThreeCount = 0;
	
	// Boolean used to track which targets have been touched.
	private boolean clearedFireOne = false;
	private boolean clearedFireTwo = false;
	private boolean clearedFireThree = false;
	
	// Bounds for touch detection.
	private Rectangle fireOneBounds = new Rectangle(220, 280, 180, 260);
	private Rectangle fireTwoBounds = new Rectangle(550, 280, 180, 260);
	private Rectangle fireThreeBounds = new Rectangle(880, 280, 180, 260);
	
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
		if (clearedFireOne && clearedFireTwo && clearedFireThree) {
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
	        // which gets cycled through via a for loop. ***
	        
	        // *** NOTE: WE CAN TEST FOR TOUCH_DRAGGED EVENTS TO BE ABLE "RUB OUT" THE FIRE ***
	        
	        // Tests if target #1 is touched.
	        if (targetDragged(event, touchPoint, fireOneBounds)) {
        		fireOneCount++;
        		if (fireOneCount == requiredBroFistCount[level-1])
        			clearedFireOne = true;
        		if (fireOneCount <= requiredBroFistCount[level-1])
        			Assets.playSound(Assets.coinSound);
        		return;
        	}
        	
	        // Tests if target #2 is touched.
        	if (targetDragged(event, touchPoint, fireTwoBounds)) {
        		fireTwoCount++;
        		if (fireTwoCount == requiredBroFistCount[level-1])
        			clearedFireTwo = true;
        		if (fireTwoCount <= requiredBroFistCount[level-1])
        			Assets.playSound(Assets.coinSound);
        		return;
        	}
        	
        	// Tests if target #3 is touched.
        	if (targetDragged(event, touchPoint, fireThreeBounds)) {
        		fireThreeCount++;
        		if (fireThreeCount == requiredBroFistCount[level-1])
        			clearedFireThree = true;
        		if (fireThreeCount <= requiredBroFistCount[level-1])
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
		batcher.beginBatch(Assets.fire);
		drawBackground();
		drawObjects();
		batcher.endBatch();		
		// drawRunningBounds();
		drawInstruction("Put out the Fire!");
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawBackground() {
		batcher.drawSprite(0, 0, 1280, 800, Assets.fireBackgroundRegion);
	}
	
	@Override
	public void drawObjects() {
		// Draw target #1.
		if (fireOneCount < requiredBroFistCount[level-1]) 
			batcher.drawSprite(fireOneBounds, Assets.fireWindowRegion);
		else
			batcher.drawSprite(fireOneBounds,  Assets.clearWindowRegion);
		
		// Draw target #2.
		if (fireTwoCount < requiredBroFistCount[level-1])
			batcher.drawSprite(fireTwoBounds, Assets.fireWindowRegion);
		else
			batcher.drawSprite(fireTwoBounds,  Assets.clearWindowRegion);
		
		// Draw target #3.
		if (fireThreeCount < requiredBroFistCount[level-1]) 
			batcher.drawSprite(fireThreeBounds, Assets.fireWindowRegion);
		else
			batcher.drawSprite(fireThreeBounds, Assets.clearWindowRegion);
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(fireOneBounds, Assets.boundOverlayRegion); // fireOne Bounding Box
	    batcher.drawSprite(fireTwoBounds, Assets.boundOverlayRegion); // fireTwo Bounding Box
	    batcher.drawSprite(fireThreeBounds, Assets.boundOverlayRegion); // fireThree Bounding Box
	    batcher.endBatch();
	}
	
}

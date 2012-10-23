package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

public class BroFistMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------
	
	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredBroFistCount[] = { 5, 10, 15 };
	private int broFistCount = 0;
	
	// Bounds for touch detection.
	private Rectangle broFistBounds = new Rectangle(480, 280, 320, 240);
	
	// -------------------
	// --- Constructor ---
	// -------------------   
    public BroFistMicroGame(Game game) {
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
		
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
        	touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        // Tests if target (BroFist) is touched.
        	if (targetTouched(event, touchPoint, broFistBounds)) {
        		broFistCount++;
        		if (broFistCount == requiredBroFistCount[level-1]) {
        			Assets.playSound(Assets.highJumpSound);
        			microGameState = MicroGameState.Won;
        		}
        		else if (broFistCount < requiredBroFistCount[level-1])
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
		drawBackground();
		drawObjects();
		// drawRunningBounds();
		drawInstruction("Brofist!");
		super.presentRunning();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	@Override
	public void drawBackground() {
		// Draw background.
		batcher.beginBatch(Assets.broFistBackground);
		batcher.drawSprite(0, 0, 1280, 800, Assets.broFistBackgroundRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawObjects() {
		// Draw Brofist.
		batcher.beginBatch(Assets.broFist);
		batcher.drawSprite(broFistBounds, Assets.broFistRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(broFistBounds, Assets.boundOverlayRegion); // BroFist Bounding Box
	    batcher.endBatch();
	}
	
}

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

	private int requiredBroFistCount[] = { 5, 10, 15 };
	private int broFistCount = 0;

	private int accelX = 10; 
	private int accelY = 0;
	
	// Bounds for touch detection.
	private Rectangle broFistBounds= new Rectangle(480, 60, 320, 240);
	
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
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	public void moveFly() {
		float x = broFistBounds.lowerLeft.x;
		float y = broFistBounds.lowerLeft.y;
		
		if (x == 900) {
			accelX = -10;
			accelY = 5;
		} else if (x == 60) {
			accelX = 10;
			accelY = -5;
		} else if (x == 480) {
			if (accelY > 0)
				accelY = -5;
			else 
				accelY = 5;
		}
		x += accelX;
		y += accelY;
		
		broFistBounds.lowerLeft.set(x, y);
	}

	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		drawInstruction("BROFIST!");
		
		// Draw Brofist.
		batcher.beginBatch(Assets.broFist);
		batcher.drawSprite(broFistBounds, Assets.broFistRegion);
		batcher.endBatch();
		
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
	    batcher.drawSprite(broFistBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.endBatch();
	}
	
}

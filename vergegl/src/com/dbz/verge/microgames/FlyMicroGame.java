package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// TODO: Make fly's movement random.
//		 Generate more flies for higher difficulties.
//		 Splat texture on death
public class FlyMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------

	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredFlySwatCount[] = { 1, 2, 3 };
	private int flySwatCount = 0;

	// Speed variables for fly movement.
	private float speedX = 10; 
	private float speedY = 0;
	
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
		// Checks for time-based loss.
		if (lostTimeBased(deltaTime)) {
			Assets.playSound(Assets.hitSound);
			return;
		}
		
		// Moves fly in a pre-defined way.
		moveFly();
		
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
	        
	        // TODO: Make this TOUCH_DRAGGED to make the MicroGame more forgiving. (?)
	        // Fly Bounds (TOUCH_DOWN) Check.
        	if (targetTouchDown(event, touchPoint, flyBounds)) {
        		flySwatCount++;
        		if (flySwatCount == requiredFlySwatCount[level-1]) {
        			Assets.playSound(Assets.highJumpSound);
        			microGameState = MicroGameState.Won;
        		}
        		else if (flySwatCount < requiredFlySwatCount[level-1])
        			Assets.playSound(Assets.coinSound);
        		return;
        	}
	        
        	// Non-Unique, Super Class Bounds (TOUCH_UP) Check.
	        if (event.type == TouchEvent.TOUCH_UP)
	        	super.updateRunning(touchPoint);
	    }   
	}
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	@Override
	public void reset() {
		super.reset();
		flySwatCount = 0;
		speedX = 10;
		speedY = 0;
		flyBounds.lowerLeft.set(600, 60);
	}
	
	// TODO: Make movement randomized.
	// Moves fly in a predefined manner.
	public void moveFly() {
		float x = flyBounds.lowerLeft.x;
		float y = flyBounds.lowerLeft.y;
		
		if (x >= 1200) {
			speedX = -10.0f;
			speedY = 5.0f;
		}
		else if (y >= 720) {
			speedX = -10.0f;
			speedY = -5.0f;
		}
		else if (x <= 80) {
			speedX = 10.0f;
			speedY = -5.0f;
		}
		else if (y <= 80) {
			speedX = 10.0f;
			speedY = 5.0f;
		}
		
//		if (x >= 1200) {
//			speedX = -10.0f;
//			speedY = 5.0f;
//		} else if (x <= 80) {
//			speedX = 10.0f;
//			speedY = -5.0f;
//		} else if (x >= 580 && x <= 620) {
//			if (speedY > 0)
//				speedY = -5.0f;
//			else 
//				speedY = 5.0f;
//		}
		x += speedX * speedScalar[speed-1];
		y += speedY * speedScalar[speed-1];
		
		flyBounds.lowerLeft.set(x, y);
	}

	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		drawRunningBackground();
		drawRunningObjects();
		// drawRunningBounds();
		drawInstruction("Pet the Fly!");
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawRunningBackground() {
		// Draw the background.
		batcher.beginBatch(Assets.flyBackground);
		batcher.drawSprite(0, 0, 1280, 800, Assets.flyBackgroundRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningObjects() {
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

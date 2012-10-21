package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.impl.AccelerometerHandler;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// *** Need to create a standard for handling multiple difficulty levels. ***
public class TrafficMicroGame extends MicroGame {
    
	
	// --------------
	// --- Fields ---
	// --------------
	
	// *** Need to create a standard for handling multiple difficulty levels. ***
	private int level = 3;
	private int requiredBroFistCount[] = { 5, 10, 15 };
	
	private int carX = 480;
	private int carY = 0;
	private int carWidth = 320;
	private int carHeight = 240;
	
	private int obstacleX = 560;
	private int obstacleY = 800;
	private int obstacleWidth = 160;
	private int obstacleHeight = 160;
	
	
	// -------------------
	// --- Constructor ---
	// -------------------   
    public TrafficMicroGame(Game game) {
        super(game);
        totalAllowedTime = 10.0f;
        
       
    }

	// ---------------------
	// --- Update Method ---
	// ---------------------
    
	@Override
	public void updateRunning(float deltaTime) {
		totalRunningTime += deltaTime;
		
		obstacleY -= 5;
		
		if (obstacleY <= carHeight && obstacleY + obstacleHeight >= carY) {
			if (obstacleX <= carX + carWidth && obstacleX + obstacleWidth >= carX)
				microGameState = MicroGameState.Lost;
		}
		
		// Checks for time-based loss.
		if (lostTimeBased()) {
			microGameState = MicroGameState.Won;
			return;
		}
		
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
        	touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
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
		drawInstruction("BROFIST!" + " x: " + String.valueOf(game.getInput().getAccelX()) + " y: " + String.valueOf(game.getInput().getAccelY()) + " z: " + String.valueOf(game.getInput().getAccelZ()));
		
		carX += (int) game.getInput().getAccelY();
		
		// Draw Brofist.
		batcher.beginBatch(Assets.brofist);
		batcher.drawSprite(carX, carY, carWidth, carHeight, Assets.brofistRegion);
		batcher.endBatch();
		
		batcher.beginBatch(Assets.backArrow);
		batcher.drawSprite(obstacleX, obstacleY, obstacleWidth, obstacleHeight, Assets.backArrowRegion);
		batcher.endBatch();
		
		drawRunningBounds();
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(carX, carY, carWidth, carHeight, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(obstacleX, obstacleY, obstacleWidth, obstacleHeight, Assets.boundOverlayRegion);
	    batcher.endBatch();
	}
	
}

package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

public class DirtBikeMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------
	
	// Speed variable for dirt bike movement.
	private float speedX = 0;
	
	// Animation scalar based on speed variable.
	private float animationScalar[] = {1.0f, 1.5f, 2.0f};
	
	// Bounds for dirt bike.
	private Rectangle dirtBikeBounds = new Rectangle(0,225,256,256);
	private Rectangle gasBounds = new Rectangle(1050,20,160,160);
	boolean gasOn=false;
	// -------------------
	// --- Constructor ---
	// ------------------- 
	
    public DirtBikeMicroGame(Game game) {
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
  		if (dirtBikeBounds.lowerLeft.x > 1200) {
			Assets.playSound(Assets.highJumpSound);
			microGameState = MicroGameState.Won;
			return;
		}
		
		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);

	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
        	touchPoint.set(event.x, event.y);
        	// Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
			//if(targetTouchDragged(event, touchPoint, gasBounds)) {
	        if (targetTouchDown(event, touchPoint, gasBounds)) {
				gasOn=true;
	        }
			if(event.type == TouchEvent.TOUCH_UP)
				gasOn=false;

	     // Tests for non-unique touch events, which is currently pause only.
	    if (event.type == TouchEvent.TOUCH_UP)
	    	 super.updateRunning(touchPoint); 
	    }   
        if(gasOn==true){
        	moveDirtBike();
        }
	}
	
	// TODO: Make movement randomized.
	// Moves fly in a predefined manner.
	public void moveDirtBike() {
		float x = dirtBikeBounds.lowerLeft.x;
		speedX = 50;
		x += speedX * animationScalar[speed-1];
		
		dirtBikeBounds.lowerLeft.set(x, 225);
	}

	// -----------------------------
	// --- Utility Update Method ---
	// -----------------------------
	
	@Override
	public void reset() {
		super.reset();
	}
	
	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		drawRunningBackground();
		drawRunningObjects();
		// drawRunningBounds();
		drawInstruction("GO!");
		super.presentRunning();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	@Override
	public void drawRunningBackground() {
		// Draw background.
		batcher.beginBatch(Assets.dirtBikeBackground);
		batcher.drawSprite(0, 0, 1280, 800, Assets.dirtBikeBackgroundRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningObjects() {
		// dirt bike and gas
		batcher.beginBatch(Assets.dirtBikeBackground);
		batcher.drawSprite(dirtBikeBounds, Assets.dirtBikeRegion);
		batcher.drawSprite(gasBounds, Assets.gasPedalRegion);
		batcher.endBatch();
		
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(dirtBikeBounds, Assets.dirtBikeRegion); // dirtBike bounding box
	    batcher.drawSprite(gasBounds, Assets.gasPedalRegion);
	    batcher.endBatch();
	}
	
}

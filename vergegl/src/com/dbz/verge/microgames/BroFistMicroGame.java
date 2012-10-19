package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// *** Might not need individual instances of MicroGameScreen for each MicroGame ***
public class BroFistMicroGame extends MicroGame {
    
	private int brofistCount;
	private Rectangle brofistBounds;
	
    // *** Constructor ***   
    public BroFistMicroGame(Game game) {
        super(game);
        
        brofistCount = 0;
        
        // Initialize bounds for touch detection.
        brofistBounds = new Rectangle(480, 280, 320, 240);
    }

    // *** Update Method ***
	
	@Override
	public void updateRunning(float deltaTime) {
		totalRunningTime += deltaTime;
		
		// Checks for time-based loss.
		if (lostTimeBased())
			return;
		
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
        	touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        // Tests if target (brofist) is touched.
        	if (targetTouched(event, touchPoint, brofistBounds)) {
        		brofistCount++;
        		Assets.playSound(Assets.coinSound);
        		if (brofistCount >= 5)
        			microGameState = MicroGameState.Won;
        		return;
        	}
	        
        	// Tests for non-unique touch events, which is currently pause only.
	        if (event.type == TouchEvent.TOUCH_UP)
	        	super.updateRunning(deltaTime, touchPoint);
	    }   
	}

	// *** Draw Method ***
	
	@Override
	public void presentRunning() {
		drawInstruction("BROFIST!");
		
		// Draw Brofist.
		batcher.beginBatch(Assets.brofist);
		batcher.drawSprite(480, 280, 320, 240, Assets.brofistRegion);
		batcher.endBatch();

		// Bounding Boxes
//		batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(480, 280, 320, 240, Assets.boundOverlayRegion); // Brofist Bounding Box
//	    batcher.endBatch();
		
		super.presentRunning();
	}

}

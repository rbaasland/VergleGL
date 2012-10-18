package com.dbz.verge;

import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;

public class MicroWorld extends World {
	
	// Not entirely sure that we need this class to track states.
	public enum MicroWorldState {
		Running,
		Paused,
		Won,
		Lost
	}
	
	public static MicroWorldState microWorldState;
	
	float totalRunningTime;
	
	Rectangle brofistBounds;
	
	public MicroWorld() {
		microWorldState = MicroWorldState.Running;
		totalRunningTime = 0;
		
		// Initialize bounds for touch detection.
		brofistBounds = new Rectangle(480, 280, 320, 240);
	}
	
	@Override
	public void generateLevel() {
		// Currently unused...
		// Can later be used for logical preparation and initialization
	}

	// *** Need to make a checkWon()/checkLost() for every win/lose condition. ***
	
	// Checks for MicroGame win conditions (Single Touch).
	public boolean checkWon(Vector2 touchPoint) {
			
	    if(OverlapTester.pointInRectangle(brofistBounds, touchPoint)) {
	        Assets.playSound(Assets.coinSound); // Play winning sound here.
	        microWorldState = MicroWorldState.Won; // **Was MicroGameState**
	        return true;
	    }
	    else
	    	return false;
	}
	
	// Checks for MicroGame lost conditions (Time-based).
	public boolean checkLost(float totalRunningTime) {
		if (totalRunningTime > 5.0f) {
			Assets.playSound(Assets.hitSound);
			microWorldState = MicroWorldState.Lost;
			return true;
		}
		else
			return false;
	}
}

package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// TODO: Make cars randomly generated.
//		 Make left two lanes oncoming, make right two ongoing.
//		 Make all lanes oncoming for hardest difficulty.
//		 Remove the ability to go off screen.
public class TrafficMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------

	// Variable needed for obstacle movement.
	private int obstacleOneSpeedY = 12;
	private int obstacleTwoSpeedY = 8;
	private int obstacleThreeSpeedY = 15;
	
	// Speed variation based on speed
	private float animationScalar[] = new float[]{1.0f, 1.5f, 2.0f};
	
	// Bounds for touch detection.
	private Rectangle obstacleOneBounds = new Rectangle(250, 800, 80, 170);
	private Rectangle obstacleTwoBounds = new Rectangle(450, 800, 80, 170);
	private Rectangle obstacleThreeBounds = new Rectangle(750, 800, 80, 170);
	private Rectangle carBounds = new Rectangle(480, 0, 80, 170);
	
	
	// -------------------
	// --- Constructor ---
	// -------------------
	
    public TrafficMicroGame(Game game) {
        super(game);
    }

	// ---------------------
	// --- Update Method ---
	// ---------------------
    
	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based win.
		if (wonTimeBased(deltaTime)) {
			Assets.playSound(Assets.highJumpSound);
			return;
		}

		// Moves obstacles at the rate of obstacleSpeedY.
		moveObstacles();
		
		// Moves car at the rate of the Accelerometer's Y axis.
		moveCar();
		
		// Checks for collision-based loss. (obstacleOne)
		if (collision(carBounds, obstacleOneBounds)) {
				Assets.playSound(Assets.hitSound);
				microGameState = MicroGameState.Lost;
				return;
		}
		
		// Checks for collision-based loss. (obstacleTwo)
		if (collision(carBounds, obstacleTwoBounds)) {
			Assets.playSound(Assets.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}
		
		// Checks for collision-based loss. (obstacleThree)
		if (collision(carBounds, obstacleThreeBounds)) {
			Assets.playSound(Assets.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}
		
		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);
	        
	        // Skip handling if the TouchEvent isn't TOUCH_UP
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
        	touchPoint.set(event.x, event.y);
        	// Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
	        // Non-Unique, Super Class Bounds (TOUCH_UP) Check.
	        super.updateRunning(touchPoint);
	    }   
	}
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	@Override
	public void reset() {
		super.reset();
		obstacleOneBounds.lowerLeft.set(250, 800);
		obstacleTwoBounds.lowerLeft.set(450, 800);
		obstacleThreeBounds.lowerLeft.set(750, 800);
		carBounds.lowerLeft.set(480, 0);
	}
	
	// Moves obstacles at the rate of obstacleSpeedY.
	public void moveObstacles() {
		// Move Obstacle #1.
		float obstacleX = obstacleOneBounds.lowerLeft.x;
		float obstacleY = obstacleOneBounds.lowerLeft.y;
		
		if (obstacleY < -170)
			obstacleY = 800;
 
		obstacleY -= obstacleOneSpeedY * animationScalar[speed-1];
		obstacleOneBounds.lowerLeft.set(obstacleX, obstacleY);
		
		// Move Obstacle #2.
		obstacleX = obstacleTwoBounds.lowerLeft.x;
		obstacleY = obstacleTwoBounds.lowerLeft.y;
		
		if (obstacleY < -170)
			obstacleY = 800;
		 
		obstacleY -= obstacleTwoSpeedY * animationScalar[speed-1];
		obstacleTwoBounds.lowerLeft.set(obstacleX, obstacleY);
		
		// Move Obstacle #3.
		obstacleX = obstacleThreeBounds.lowerLeft.x;
		obstacleY = obstacleThreeBounds.lowerLeft.y;
		
		if (obstacleY < -170)
			obstacleY = 800;
		
		obstacleY -= obstacleThreeSpeedY * animationScalar[speed-1];
		obstacleThreeBounds.lowerLeft.set(obstacleX, obstacleY);
	}
	
	// Moves car at the rate of the Accelerometer's Y axis.
	public void moveCar() {
		carBounds.lowerLeft.x += (int) game.getInput().getAccelY();
	}
	
	// Checks for collision-based loss.
	public boolean collision(Rectangle car, Rectangle obstacle) {
		float obstacleX = obstacle.lowerLeft.x;
		float obstacleY = obstacle.lowerLeft.y;
		float carX = car.lowerLeft.x;
		float carY = car.lowerLeft.y;
		
		if (obstacleY <= car.height)
			if (obstacleY + obstacle.height >= carY)
				if (obstacleX <= carX + car.width)
					if (obstacleX + obstacle.width >= carX)
						return true;
		
		return false;
	}

	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		batcher.beginBatch(Assets.traffic);
		drawRunningBackground();
		drawRunningObjects();
		batcher.endBatch();
		// drawRunningBounds();
		drawInstruction("Dodge!");
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawRunningBackground() {
		batcher.drawSprite(0, 0, 1280, 800, Assets.trafficBackgroundRegion);
	}
	
	@Override
	public void drawRunningObjects() {
		batcher.drawSprite(obstacleOneBounds, Assets.trafficRedCarRegion); 		// Draws obstacle car.
		batcher.drawSprite(obstacleTwoBounds, Assets.trafficBlackCarRegion); 	// Draws obstacle car.
		batcher.drawSprite(obstacleThreeBounds, Assets.trafficBlueCarRegion); 	// Draws obstacle car.
		batcher.drawSprite(carBounds, Assets.trafficBlueCarRegion); 			// Draws player car.
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
		batcher.drawSprite(obstacleOneBounds, Assets.boundOverlayRegion); 	// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleTwoBounds, Assets.boundOverlayRegion); 	// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleThreeBounds, Assets.boundOverlayRegion); // Obstacle Car Bounding Box
	    batcher.drawSprite(carBounds, Assets.boundOverlayRegion); 			// Car Bounding Box    
	    batcher.endBatch();
	}
	
}

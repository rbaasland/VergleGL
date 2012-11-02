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
		totalRunningTime += deltaTime;

		// Moves obstacles at the rate of obstacleAccelY.
		moveObstacles();
		
		// Moves car at the rate of the Acceleromter's Y axis.
		moveCar();
		
		// Tests for collision-based loss.
		if (collision(carBounds, obstacleOneBounds)) {
				Assets.playSound(Assets.hitSound);
				microGameState = MicroGameState.Lost;
				return;
		}
		if (collision(carBounds, obstacleTwoBounds)) {
			Assets.playSound(Assets.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}
		if (collision(carBounds, obstacleThreeBounds)) {
			Assets.playSound(Assets.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}
		
		// Checks for time-based win.
		if (wonTimeBased()) {
			Assets.playSound(Assets.highJumpSound);
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
	
	public void moveObstacles() {
		// Move Obstacle #1.
		float obstacleX = obstacleOneBounds.lowerLeft.x;
		float obstacleY = obstacleOneBounds.lowerLeft.y;
		
		if (obstacleY < -170)
			obstacleY = 800;
 
		obstacleY -= obstacleOneSpeedY;
		obstacleOneBounds.lowerLeft.set(obstacleX, obstacleY);
		
		// Move Obstacle #2.
		obstacleX = obstacleTwoBounds.lowerLeft.x;
		obstacleY = obstacleTwoBounds.lowerLeft.y;
		
		if (obstacleY < -170)
			obstacleY = 800;
		 
		obstacleY -= obstacleTwoSpeedY;
		obstacleTwoBounds.lowerLeft.set(obstacleX, obstacleY);
		
		// Move Obstacle #3.
		obstacleX = obstacleThreeBounds.lowerLeft.x;
		obstacleY = obstacleThreeBounds.lowerLeft.y;
		
		if (obstacleY < -170)
			obstacleY = 800;
		
		obstacleY -= obstacleThreeSpeedY;
		obstacleThreeBounds.lowerLeft.set(obstacleX, obstacleY);
	}
	
	public void moveCar() {
		carBounds.lowerLeft.x += (int) game.getInput().getAccelY();
	}
	
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
		drawBackground();
		drawObjects();
		batcher.endBatch();
		// drawRunningBounds();
		drawInstruction("Dodge!");
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawBackground() {
		batcher.drawSprite(0, 0, 1280, 800, Assets.trafficBackgroundRegion);
	}
	
	@Override
	public void drawObjects() {
		batcher.drawSprite(obstacleOneBounds, Assets.trafficRedCarRegion); // Draws obstacle car.
		batcher.drawSprite(obstacleTwoBounds, Assets.trafficBlackCarRegion); // Draws obstacle car.
		batcher.drawSprite(obstacleThreeBounds, Assets.trafficBlueCarRegion); // Draws obstacle car.
		batcher.drawSprite(carBounds, Assets.trafficBlueCarRegion); // Draws player car.
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
		batcher.drawSprite(obstacleOneBounds, Assets.boundOverlayRegion); // Obstacle Car Bounding Box
		batcher.drawSprite(obstacleTwoBounds, Assets.boundOverlayRegion); // Obstacle Car Bounding Box
		batcher.drawSprite(obstacleThreeBounds, Assets.boundOverlayRegion); // Obstacle Car Bounding Box
	    batcher.drawSprite(carBounds, Assets.boundOverlayRegion); // Car Bounding Box    
	    batcher.endBatch();
	}
	
}

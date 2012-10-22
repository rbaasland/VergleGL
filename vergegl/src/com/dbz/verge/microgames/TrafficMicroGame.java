package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

public class TrafficMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------

	// Variable needed for obstacle movement.
	private int obstacleAccelY = 5;
	
	// Bounds for touch detection.
	private Rectangle obstacleBounds = new Rectangle(560, 800, 160, 160);
	private Rectangle carBounds = new Rectangle(480, 0, 320, 240);
	
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
		if (collision(carBounds, obstacleBounds)) {
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
	        	super.updateRunning(deltaTime, touchPoint);
	    }   
	}
	
	// -----------------------------
	// --- Utility Update Method ---
	// -----------------------------
	
	public void moveObstacles() {
		float obstacleX = obstacleBounds.lowerLeft.x;
		float obstacleY = obstacleBounds.lowerLeft.y;
		
		obstacleY -= obstacleAccelY;
		obstacleBounds.lowerLeft.set(obstacleX, obstacleY);
	}
	
	public void moveCar() {
		carBounds.lowerLeft.x += (int) game.getInput().getAccelY();
	}
	
	public boolean collision(Rectangle car, Rectangle obstacle) {
		float obstacleX = obstacleBounds.lowerLeft.x;
		float obstacleY = obstacleBounds.lowerLeft.y;
		float carX = carBounds.lowerLeft.x;
		float carY = carBounds.lowerLeft.y;
		
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
		drawInstruction("BROFIST!" + " x: " + String.valueOf(game.getInput().getAccelX()) + " y: " + String.valueOf(game.getInput().getAccelY()) + " z: " + String.valueOf(game.getInput().getAccelZ()));

		// Draw car.
		batcher.beginBatch(Assets.broFist);
		batcher.drawSprite(carBounds, Assets.broFistRegion);
		batcher.endBatch();
		
		// Draw obstacle.
		batcher.beginBatch(Assets.backArrow);
		batcher.drawSprite(obstacleBounds, Assets.backArrowRegion);
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
	    batcher.drawSprite(carBounds, Assets.boundOverlayRegion); // Car Bounding Box
	    batcher.drawSprite(obstacleBounds, Assets.boundOverlayRegion); // Obstacle Bounding Box
	    batcher.endBatch();
	}
	
}

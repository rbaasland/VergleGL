package com.dbz.verge.microgames;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.dbz.framework.Game;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// TODO: Make left two lanes oncoming, make right two ongoing.
//		 Make all lanes oncoming for hardest difficulty.

public class TrafficMicroGame extends MicroGame {

	// --------------
	// --- Fields ---
	// --------------

	// Queue for lane selection
	private Queue<Float> lanes = new LinkedList<Float>();

	// Random to shuffle lanes
	private Random rand = new Random();

	// Car start position
	private int carStartPosition = 480;

	// Lane variables
	private float laneOne = 150;
	private float laneTwo = 280;
	private float laneThree = 400;
	private float laneFour = 523;
	private float laneFive = 650;
	private float laneSix = 777;
	private float laneSeven = 892;
	private float laneEight = 1007;
	
	private float lanePosition[] = { laneOne, laneTwo, laneThree, laneFour,
			laneFive, laneSix, laneSeven, laneEight};

	// Variable needed for obstacle movement.
	private int obstacleOneSpeedY = 12;
	private int obstacleTwoSpeedY = 8;
	private int obstacleThreeSpeedY = 15;
	private int obstacleFourSpeedY = 5;

	// Number of cars based on level
	private int totalCars[] = { 2, 3, 4 };

	// Bounds for touch detection.
	private Rectangle obstacleOneBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle obstacleTwoBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle obstacleThreeBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle obstacleFourBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle carBounds = new Rectangle(carStartPosition, 0, 80, 170);

	// -------------------
	// --- Constructor ---
	// -------------------

	public TrafficMicroGame(Game game) {
		super(game);
		randomizeCarsLanes();
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

		// Checks for collision-based loss. (obstacleFour)
		if (collision(carBounds, obstacleFourBounds)) {
			Assets.playSound(Assets.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}

		// Gets all TouchEvents and stores them in a list.
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

		// Cycles through and tests all touch events.
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			// Gets a single TouchEvent from the list.
			TouchEvent event = touchEvents.get(i);

			// Skip handling if the TouchEvent isn't TOUCH_UP
			if (event.type != TouchEvent.TOUCH_UP)
				continue;

			// Sets the x and y coordinates of the TouchEvent to our touchPoint
			// vector.
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
		obstacleOneBounds.lowerLeft.set(0, -171);
		obstacleTwoBounds.lowerLeft.set(0, -171);
		obstacleThreeBounds.lowerLeft.set(0, -171);
		obstacleFourBounds.lowerLeft.set(0, -171);
		carBounds.lowerLeft.set(carStartPosition, 0);
		lanes.clear();
		randomizeCarsLanes();
	}

	// Moves obstacles at the rate of obstacleSpeedY.
	public void moveObstacles() {

		// Move Obstacle #1.
		float obstacleX = obstacleOneBounds.lowerLeft.x;
		float obstacleY = obstacleOneBounds.lowerLeft.y;

		if (obstacleY < -170) {
			obstacleY = 800;
			if (obstacleX != 0)
				lanes.add(obstacleX);
			obstacleX = lanes.remove();
		}

		obstacleY -= obstacleOneSpeedY * speedScalar[speed - 1];
		obstacleOneBounds.lowerLeft.set(obstacleX, obstacleY);

		// Move Obstacle #2.
		obstacleX = obstacleTwoBounds.lowerLeft.x;
		obstacleY = obstacleTwoBounds.lowerLeft.y;

		if (obstacleY < -170) {
			obstacleY = 800;
			if (obstacleX != 0)
				lanes.add(obstacleX);
			obstacleX = lanes.remove();
		}

		obstacleY -= obstacleTwoSpeedY * speedScalar[speed - 1];
		obstacleTwoBounds.lowerLeft.set(obstacleX, obstacleY);

		// Move Obstacle #3.
		if (totalCars[level - 1] >= 3) {

			obstacleX = obstacleThreeBounds.lowerLeft.x;
			obstacleY = obstacleThreeBounds.lowerLeft.y;

			if (obstacleY < -170) {
				obstacleY = 800;
				if (obstacleX != 0)
					lanes.add(obstacleX);
				obstacleX = lanes.remove();
			}

			obstacleY -= obstacleThreeSpeedY * speedScalar[speed - 1];
			obstacleThreeBounds.lowerLeft.set(obstacleX, obstacleY);
		}

		// Move Obstacle #4.
		if (totalCars[level - 1] == 4) {

			obstacleX = obstacleFourBounds.lowerLeft.x;
			obstacleY = obstacleFourBounds.lowerLeft.y;

			if (obstacleY < -170) {
				obstacleY = 800;
				if (obstacleX != 0)
					lanes.add(obstacleX);
				obstacleX = lanes.remove();
			}

			obstacleY -= obstacleFourSpeedY * speedScalar[speed - 1];
			obstacleFourBounds.lowerLeft.set(obstacleX, obstacleY);
		}
	}

	// Moves car at the rate of the Accelerometer's Y axis.
	public void moveCar() {
		// Bounds checking so car doesn't fly off screen
		if (carBounds.lowerLeft.x >= 150 && carBounds.lowerLeft.x <= 1025)
			carBounds.lowerLeft.x += (int) game.getInput().getAccelY();
		else
			carBounds.lowerLeft.x -= (int) game.getInput().getAccelY();
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

	// Shuffle car lanes and put in queue
	public void randomizeCarsLanes() {
		for (int i = 0; i < lanePosition.length; i++) {
			int randTemp = rand.nextInt(lanePosition.length - 1);
			float temp = lanePosition[i];
			lanePosition[i] = lanePosition[randTemp];
			lanePosition[randTemp] = temp;
		}
		for (int i = 0; i < lanePosition.length; i++)
			lanes.add(lanePosition[i]);
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
		// Draws obstacle car.
		batcher.drawSprite(obstacleOneBounds, Assets.trafficRedCarRegion);
		// Draws obstacle car.
		batcher.drawSprite(obstacleTwoBounds, Assets.trafficBlackCarRegion);
		// Draws obstacle car.
		batcher.drawSprite(obstacleThreeBounds, Assets.trafficBlueCarRegion);
		// Draws obstacle car.
		batcher.drawSprite(obstacleFourBounds, Assets.trafficBlackCarRegion);
		// Draws player car.
		batcher.drawSprite(carBounds, Assets.trafficBlueCarRegion);
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
		// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleOneBounds, Assets.boundOverlayRegion);
		// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleTwoBounds, Assets.boundOverlayRegion);
		// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleThreeBounds, Assets.boundOverlayRegion);
		// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleFourBounds, Assets.boundOverlayRegion);
		// Car Bounding Box
		batcher.drawSprite(carBounds, Assets.boundOverlayRegion);
		batcher.endBatch();
	}

}

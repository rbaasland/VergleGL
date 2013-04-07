package com.dbz.verge.microgames;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;

public class InvasionMicroGame extends MicroGame {

	// --------------
	// --- Fields ---
	// --------------

	// Assets
	public static Texture traffic;
	public static TextureRegion trafficBackgroundRegion;
	public static TextureRegion trafficBlueCarRegion;
	public static TextureRegion trafficRedCarRegion;
	public static TextureRegion trafficBlackCarRegion;
	public static TextureRegion trafficMonsterCarRegion;
	public static TextureRegion trafficCrushedBlueCarRegion;
	public static TextureRegion trafficCrushedRedCarRegion;
	public static TextureRegion trafficCrushedBlackCarRegion;

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
			laneFive, laneSix, laneSeven, laneEight };

	// Variable needed for obstacle movement.
	private int obstacleOneSpeedY = 12;
	private int obstacleTwoSpeedY = 8;
	private int obstacleThreeSpeedY = 15;
	private int obstacleFourSpeedY = 5;
	private float crushedCarSpeedY = 20 * speedScalar[speed - 1];
	private int backgroundSpeedY;

	// Crushed Car counter
	private int carsCrushed = 0;
	
	// Crushed cars needed to win
	private int crushedCarsNeeded = 10;
	
	// Number of cars based on level
	private int totalCars[] = { 2, 3, 4 };

	// Bounds for touch detection.
	private Rectangle obstacleOneBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle obstacleTwoBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle obstacleThreeBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle obstacleFourBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle crushedOneBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle crushedTwoBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle crushedThreeBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle crushedFourBounds = new Rectangle(0, -171, 80, 170);
	private Rectangle carBounds = new Rectangle(carStartPosition, 0, 80, 170);
	private Rectangle backgroundBounds = new Rectangle(0, 0, 1280, 800);
	private Rectangle backgroundBounds2 = new Rectangle(0, 800, 1280, 800);	
	private Rectangle lazerButtonBoundsOne = new Rectangle(0, 160, 1280, 480);
	private Rectangle lazerButtonBoundsTwo = new Rectangle(160, 0, 960, 800);
	
	public class Lazer {
		private int width = 25;
		private int height = 50;
		
		public Rectangle bounds;
		// Direction (Vector), based on who fired it.
		// Speed/Acceleration/etc?
		
		public Lazer(Rectangle shooterBounds) {
			float x = shooterBounds.lowerLeft.x + (shooterBounds.width / 2) - (width / 2);
			float y = shooterBounds.lowerLeft.y + (shooterBounds.height / 2);
			bounds = new Rectangle(x, y, width, height);	// Set bounds based on shooter.
		}
		
		public void update() {
			bounds.lowerLeft.add(0, 5);
			// TODO: If out of bounds, delete self or signal for deletion.
		}
		
		public void draw() {
			// Draws obstacle car.
			batcher.drawSprite(bounds, trafficBlackCarRegion);
		}
	}
	
	public ArrayList<Lazer> lazersList = new ArrayList<Lazer>();
	
	// -------------------
	// --- Constructor ---
	// -------------------

	public InvasionMicroGame() {
		randomizeCarsLanes();
		baseMicroGameTime = 10.0f;
		accelerometerEnabled = true;
		singleTouchEnabled = true;
	}

	@Override
	public void load() {
		traffic = new Texture("traffic.png");
		carBounds = new Rectangle(carStartPosition, 0, 92, 170);
		trafficBackgroundRegion = new TextureRegion(traffic, 0, 0, 1280, 800);
		trafficBlueCarRegion = new TextureRegion(traffic, 0, 800, 80, 170);
		trafficRedCarRegion = new TextureRegion(traffic, 80, 800, 80, 170);
		trafficBlackCarRegion = new TextureRegion(traffic, 160, 800, 80, 170);
		trafficMonsterCarRegion = new TextureRegion(traffic, 240, 800, 92, 170);
		trafficCrushedBlueCarRegion = new TextureRegion(traffic, 412, 800, 80, 170);
		trafficCrushedRedCarRegion = new TextureRegion(traffic, 494, 800, 80, 170);
		trafficCrushedBlackCarRegion = new TextureRegion(traffic, 332, 800, 80, 170);
	}

	@Override
	public void unload() {
		traffic.dispose();
	}

	@Override
	public void reload() {
		traffic.reload();
	}

	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based win.
		if (wonTimeBased(deltaTime)) {
			AssetsManager.playSound(AssetsManager.highJumpSound);
			return;
		}
		
		// Moves background
		moveBackground();
		
		// Moves obstacles at the rate of obstacleSpeedY.
		moveObstacles();
		
		// Moves crushed cars off screen
		moveCrushedCars();

		// Moves car at the rate of the Accelerometer's Y axis.
		moveCar();
		
		// Updates lazers.
		updateLazers();
		
		// Checks for collision-based loss. (obstacleOne)
		if (collision(carBounds, obstacleOneBounds)) {
			AssetsManager.playSound(AssetsManager.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}

		// Checks for collision-based loss. (obstacleTwo)
		if (collision(carBounds, obstacleTwoBounds)) {
			AssetsManager.playSound(AssetsManager.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}

		// Checks for collision-based loss. (obstacleThree)
		if (collision(carBounds, obstacleThreeBounds)) {
			AssetsManager.playSound(AssetsManager.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}

		// Checks for collision-based loss. (obstacleFour)
		if (collision(carBounds, obstacleFourBounds)) {
			AssetsManager.playSound(AssetsManager.hitSound);
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

			// Sets the x and y coordinates of the TouchEvent to our touchPoint
			// vector.
			touchPoint.set(event.x, event.y);
			// Sends the vector to the OpenGL Camera for handling.
			guiCam.touchToWorld(touchPoint);
			
	        // Lazer Bounds (TOUCH_DOWN) Check.
        	if (targetTouchDown(event, touchPoint, lazerButtonBoundsOne) || targetTouchDown(event, touchPoint, lazerButtonBoundsTwo)) {
        		shootLazer();
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
		obstacleOneBounds.lowerLeft.set(0, -171);
		obstacleTwoBounds.lowerLeft.set(0, -171);
		obstacleThreeBounds.lowerLeft.set(0, -171);
		obstacleFourBounds.lowerLeft.set(0, -171);
		carBounds.lowerLeft.set(carStartPosition, 0);
		lanes.clear();
		randomizeCarsLanes();
	}

	// Moves the background
	public void moveBackground() {
		float accelX = (30-game.getInput().getAccelX()); // Accelerometer max X value is 10 so background scrolls at least 20
		backgroundSpeedY = (int) (accelX * speedScalar[speed - 1]);
		if (backgroundBounds.lowerLeft.y >= -backgroundBounds.height + backgroundSpeedY )
			backgroundBounds.lowerLeft.y -= backgroundSpeedY;
		else
			backgroundBounds.lowerLeft.y = backgroundBounds2.lowerLeft.y + backgroundBounds.height - backgroundSpeedY;
		if (backgroundBounds2.lowerLeft.y >= -backgroundBounds2.height + backgroundSpeedY)
			backgroundBounds2.lowerLeft.y -= backgroundSpeedY;
		else
			backgroundBounds2.lowerLeft.y = backgroundBounds.lowerLeft.y + backgroundBounds.height - backgroundSpeedY;
		
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

		if (version == 0) {
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
		if (version == 1) {
			// Move Obstacle #3.
			if (totalCars[level - 1] <= 3) {
	
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
			if (totalCars[level - 1] == 2) {
	
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
	}
	
	// Moves crushed cars off screen
	public void moveCrushedCars() {
		// Move crushed car off screen
		if (crushedOneBounds.lowerLeft.y > -170 )
			crushedOneBounds.lowerLeft.y -= crushedCarSpeedY;
		
		// Move crushed car off screen
		if (crushedTwoBounds.lowerLeft.y > -170 )
			crushedTwoBounds.lowerLeft.y -= crushedCarSpeedY;
		
		// Move crushed car off screen
		if (crushedThreeBounds.lowerLeft.y > -170 )
			crushedThreeBounds.lowerLeft.y -= crushedCarSpeedY;
		
		// Move crushed car off screen
		if (crushedFourBounds.lowerLeft.y > -170 )
			crushedFourBounds.lowerLeft.y -= crushedCarSpeedY;
	}

	// Moves car at the rate of the Accelerometer's Y axis.
	public void moveCar() {
		// Bounds checking so car doesn't fly off screen
		if (carBounds.lowerLeft.x >= 150 && carBounds.lowerLeft.x <= 1025)
			carBounds.lowerLeft.x += (int) game.getInput().getAccelY() * 2 * speedScalar[speed - 1];
		if (carBounds.lowerLeft.x <= 150)
			carBounds.lowerLeft.x = 150;
		if (carBounds.lowerLeft.x >= 1025)
			carBounds.lowerLeft.x = 1025;
	}

	// Checks for collision.
	public boolean collision(Rectangle car, Rectangle obstacle) {
		float obstacleX = obstacle.lowerLeft.x;
		float obstacleY = obstacle.lowerLeft.y;
		float carX = car.lowerLeft.x;
		float carY = car.lowerLeft.y;

		if (obstacleY <= carY + car.height)
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

	public void updateLazers() {
		if (lazersList != null) 
			for (Lazer l : lazersList) {
				l.update();
				
				// Checks for collision-based loss. (obstacleOne)
				if (collision(l.bounds, obstacleOneBounds)) {
					AssetsManager.playSound(AssetsManager.explosionSound);
					crushedOneBounds.lowerLeft.set(obstacleOneBounds.lowerLeft);
					obstacleOneBounds.lowerLeft.y = -171;
					carsCrushed++;
					if (carsCrushed == crushedCarsNeeded)
						microGameState = MicroGameState.Won;
					return;
				}

				// Checks for collision-based loss. (obstacleTwo)
				if (collision(l.bounds, obstacleTwoBounds)) {
					AssetsManager.playSound(AssetsManager.explosionSound);
					crushedTwoBounds.lowerLeft.set(obstacleTwoBounds.lowerLeft);
					obstacleTwoBounds.lowerLeft.y = -171;
					carsCrushed++;
					if (carsCrushed == crushedCarsNeeded)
						microGameState = MicroGameState.Won;
					return;
				}

				// Checks for collision-based loss. (obstacleThree)
				if (collision(l.bounds, obstacleThreeBounds)) {
					AssetsManager.playSound(AssetsManager.explosionSound);
					crushedThreeBounds.lowerLeft.set(obstacleThreeBounds.lowerLeft);
					obstacleThreeBounds.lowerLeft.y = -171;
					carsCrushed++;
					if (carsCrushed == crushedCarsNeeded)
						microGameState = MicroGameState.Won;
					return;
				}

				// Checks for collision-based loss. (obstacleFour)
				if (collision(l.bounds, obstacleFourBounds)) {
					AssetsManager.playSound(AssetsManager.explosionSound);
					crushedFourBounds.lowerLeft.set(obstacleFourBounds.lowerLeft);
					obstacleFourBounds.lowerLeft.y = -171;
					carsCrushed++;
					if (carsCrushed == crushedCarsNeeded)
						microGameState = MicroGameState.Won;
					return;
				}
			}
	}
	
	public void shootLazer() {
		lazersList.add(new Lazer(carBounds));
		AssetsManager.playSound(AssetsManager.hitSound);
	}
	
	// -------------------
	// --- Draw Method ---
	// -------------------

	@Override
	public void presentRunning() {
		batcher.beginBatch(traffic);
		drawRunningBackground();
		drawRunningObjects();
		batcher.endBatch();
//		drawRunningBounds();
		drawInstruction("Survive!");
		super.presentRunning();
	}

	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------

	@Override
	public void drawRunningBackground() {
		batcher.drawSprite(backgroundBounds, trafficBackgroundRegion);
		batcher.drawSprite(backgroundBounds2, trafficBackgroundRegion);
	}

	@Override
	public void drawRunningObjects() {
		// Draws obstacle car.
		batcher.drawSprite(obstacleOneBounds, trafficRedCarRegion);
		// Draws obstacle car.
		batcher.drawSprite(obstacleTwoBounds, trafficBlackCarRegion);
		// Draws obstacle car.
		batcher.drawSprite(obstacleThreeBounds, trafficBlueCarRegion);
		// Draws obstacle car.
		batcher.drawSprite(obstacleFourBounds, trafficBlackCarRegion);
		// Draws crushed car.
		batcher.drawSprite(crushedOneBounds, trafficCrushedRedCarRegion);
		// Draws crushed car.
		batcher.drawSprite(crushedTwoBounds, trafficCrushedBlackCarRegion);
		// Draws crushed car.
		batcher.drawSprite(crushedThreeBounds, trafficCrushedBlueCarRegion);
		// Draws crushed car.
		batcher.drawSprite(crushedFourBounds, trafficCrushedBlackCarRegion);
		// Draws player car.
		batcher.drawSprite(carBounds, trafficMonsterCarRegion);
		drawLazers();
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(AssetsManager.boundOverlay);
		// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleOneBounds, AssetsManager.boundOverlayRegion);
		// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleTwoBounds, AssetsManager.boundOverlayRegion);
		// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleThreeBounds, AssetsManager.boundOverlayRegion);
		// Obstacle Car Bounding Box
		batcher.drawSprite(obstacleFourBounds, AssetsManager.boundOverlayRegion);
		// Car Bounding Box
		batcher.drawSprite(carBounds, AssetsManager.boundOverlayRegion);
		// Lazer Button Bounds.
		batcher.drawSprite(lazerButtonBoundsOne, AssetsManager.boundOverlayRegion);
		batcher.drawSprite(lazerButtonBoundsTwo, AssetsManager.boundOverlayRegion);
		batcher.endBatch();
	}

	public void drawLazers() {
	if (lazersList != null)
		for (Lazer l : lazersList)
			l.draw();
	}
}

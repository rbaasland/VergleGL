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
import com.dbz.verge.microgames.objects.Lazer;
import com.dbz.verge.microgames.objects.Ship;

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

	private int backgroundSpeedY;

	// Bounds for touch detection.
	private Rectangle backgroundBounds = new Rectangle(0, 0, 1280, 800);
	private Rectangle backgroundBounds2 = new Rectangle(0, 800, 1280, 800);
	
	private Rectangle leftWall = new Rectangle(-20, 0, 20, 800);		// TODO: STATIC CONSTS?
	private Rectangle rightWall = new Rectangle(1280, 0, 20, 800);
	
	private Rectangle lazerButtonBoundsOne = new Rectangle(0, 0, 1280, 640); // TODO: Refactor names
	private Rectangle lazerButtonBoundsTwo = new Rectangle(0, 0, 1120, 800);
	
	// Game Objects.
	public static final int MAX_ENEMY_SHIPS = 2;
	public static final int MAX_LAZERS = 100;
	public int totalEnemyShips = 0;
	public int totalLazers = 0;
	
	public Ship playerShip = new Ship(480, 0);
//	public ArrayList<Ship> enemyShipList = new ArrayList<Ship>();
//	public ArrayList<Lazer> lazerList = new ArrayList<Lazer>();
	public Ship enemyShips[] = new Ship[MAX_ENEMY_SHIPS];
	public Lazer lazers[] = new Lazer[MAX_LAZERS];
	
	// Game Variables.
	public int maxEnemyShips = 2;
	
	// -------------------
	// --- Constructor ---
	// -------------------

	public InvasionMicroGame() {
		randomizeCarsLanes();		// TODO: ???
		baseMicroGameTime = 100000.0f;
		accelerometerEnabled = true;
		singleTouchEnabled = true;
		
		playerShip.playerControlled = true;
		enemyShips[0] = new Ship(480, 600);
		totalEnemyShips++;
		
//		enemyShipList.add(new Ship(480, 600));

	}

	@Override
	public void load() {
		traffic = new Texture("traffic.png");
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
	public void unload() { traffic.dispose(); }

	@Override
	public void reload() { traffic.reload(); }

	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
//		if (enemyShipList.size() != maxEnemyShips)
		
//		updateBackground();
		updateShips(deltaTime);
		updateLazers();
		objectCollisionsTest();
		
		// Checks for time-based win.
		if (wonTimeBased(deltaTime)) {
			AssetsManager.playSound(AssetsManager.highJumpSound);
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
        	if (targetTouchDown(event, touchPoint, lazerButtonBoundsOne) || 
        		targetTouchDown(event, touchPoint, lazerButtonBoundsTwo)) {
        				// TODO: Handle Array Out of Bounds Exception when more than 100.
        		lazers[totalLazers] = playerShip.fireLazer();
        		totalLazers++;
        		
//        		lazerList.add(playerShip.fireLazer());
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
	public void reset() {	// TODO: Handle Resets.
		super.reset();
		lanes.clear();
		randomizeCarsLanes();
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
	
	// Moves the background
	public void updateBackground() {		// TODO: Change background handling.
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

	public void updateShips(float deltaTime) {
		if (!wallCollisionTest()) {
			int x = (int)((game.getInput().getAccelY() * speedScalar[speed - 1]) / 5);
			playerShip.addAcceleration(x, 0);
		}
		
		playerShip.update(deltaTime);
		
		for (int i = 0; i < totalEnemyShips; i++) {
			if (enemyShips[i] != null) {
				if (enemyShips[i].active)
					enemyShips[i].update(deltaTime);
				else
					enemyShips[i] = null;
			}
		}
			
		
//		for (Ship enemyShip : enemyShipList) {	// TODO: Error Here On Replay.
//			if (enemyShip.active) 
//				enemyShip.update(deltaTime);
//			else
//				enemyShipList.remove(enemyShip);	// TODO: Avoid calling Garbage Collector?
//		}											// TODO: Reuse Ships?
	}

	public void updateLazers() {
		for (int i = 0; i < totalLazers; i++) {	// TODO: Use Max Lazers?
			if (lazers[i] != null) {
				if (lazers[i].active)
					lazers[i].update();
				else
					lazers[i] = null;
			}
		}
		
//		for (Lazer lazer : lazerList) {
//			if (lazer.active)
//				lazer.update();
//			else
//				lazerList.remove(lazer);	// TODO: Avoid calling Garbage Collector?
//		}									// TODO: Reuse Lazers?
	}
	
	// TODO: A single shot will be detected as multiple collisions since the enemyShip's bounds are still there...
	public boolean objectCollisionsTest() {	// TODO: Make Rectangle Bounds for Outside of Screen.
		boolean collision = false;
		
		// Case 0 & 1: Player or Enemy hit by Lazer.
		for (int i = 0; i < totalLazers; i++) {
			if (lazers[i] != null) {
				// Case 0: Player hit by Lazer.
				if (collision(playerShip.bounds, lazers[i].bounds)) {
					AssetsManager.playSound(AssetsManager.explosionSound);
					microGameState = MicroGameState.Lost;
					playerShip.health -= lazers[i].damage;
					lazers[i].active = false;
					collision = true;
				}
				
				for (int j = 0; j < totalEnemyShips; j++) {
					if (enemyShips[j] != null) {
						// Case 1: Enemy hit by Lazer.
						if (collision(enemyShips[j].bounds, lazers[i].bounds)) {
							AssetsManager.playSound(AssetsManager.explosionSound);
							enemyShips[j].health -= lazers[i].damage;
							lazers[i].active = false;
							collision = true;
						}
					}
				}
			}
		}
		
		// Case 2 & 3: Player or Enemy hit by Enemy.
		for (int i = 0; i < totalEnemyShips; i++) {
			if (enemyShips[i] != null) {
				// Case 2: Player hit by Enemy.
				if (collision(playerShip.bounds, enemyShips[i].bounds)) {
					AssetsManager.playSound(AssetsManager.explosionSound);
					microGameState = MicroGameState.Lost;
					playerShip.health = 0;
					enemyShips[i].health = 0;
					collision = true;
				}
	
				for (int j = 0; j < totalEnemyShips; j++) {
					if (enemyShips[j] != null) {
						// Case 3: Enemy hit by Enemy
						if (enemyShips[i] != enemyShips[j]) {
							if (collision(enemyShips[i].bounds, enemyShips[j].bounds)) {
								AssetsManager.playSound(AssetsManager.explosionSound);
								enemyShips[i].health = 0;
								enemyShips[j].health = 0;
								collision = true;
							}
						}
					}
				}
			}
		}
		
//		for (Lazer lazer : lazerList) {
//			// Case 0: Player hit by Lazer.
//			if (collision(playerShip.bounds, lazer.bounds)) {
//				AssetsManager.playSound(AssetsManager.explosionSound);
//				microGameState = MicroGameState.Lost;
//				playerShip.health -= lazer.damage;
//				lazer.active = false;
//				collision = true;
//			}
//
//			for (Ship enemyShip: enemyShipList) {
//				// Case 1: Enemy hit by Lazer.
//				if (collision(enemyShip.bounds, lazer.bounds)) {
//					AssetsManager.playSound(AssetsManager.explosionSound);
//					enemyShip.health -= lazer.damage;
//					lazer.active = false;
//					collision = true;
//				}	
//			}
//		}
//		
//		// Case 2 & 3: Player or Enemy hit by Enemy.
//		for (Ship enemyShip : enemyShipList) {
//			// Case 2: Player hit by Enemy.
//			if (collision(playerShip.bounds, enemyShip.bounds)) {
//				AssetsManager.playSound(AssetsManager.explosionSound);
//				microGameState = MicroGameState.Lost;
//				playerShip.health = 0;
//				enemyShip.health = 0;
//				collision = true;
//			}
//
//			for (Ship otherEnemyShip: enemyShipList) {
//				// Case 3: Enemy hit by Enemy
//				if (enemyShip != otherEnemyShip)
//					if (collision(enemyShip.bounds, otherEnemyShip.bounds)) {
//						AssetsManager.playSound(AssetsManager.explosionSound);
//						enemyShip.health = 0;
//						otherEnemyShip.health = 0;
//						collision = true;
//					}
//			}
//		}
		
		return collision;
	}

	public boolean wallCollisionTest() {
		boolean collision = false;
		
		// Case 5: Any object goes off screen.
		if (collision(playerShip.bounds, leftWall)) {
			playerShip.bounds.lowerLeft.x = leftWall.lowerLeft.x + leftWall.width + 1;
			playerShip.setAcceleration(0, 0);
			playerShip.setVelocity(0, 0);
			collision = true;
		}
		else if (collision(playerShip.bounds, rightWall)) {
			playerShip.bounds.lowerLeft.x = rightWall.lowerLeft.x - playerShip.bounds.width - 1;
			playerShip.setAcceleration(0, 0);
			playerShip.setVelocity(0, 0);
			collision = true;
		}
		
		return collision;
	}
	
	// Checks for collision.
	public boolean collision(Rectangle objOne, Rectangle objTwo) {
		float objOneX = objOne.lowerLeft.x;
		float objOneY = objOne.lowerLeft.y;
		float objTwoX = objTwo.lowerLeft.x;
		float objTwoY = objTwo.lowerLeft.y;

		if (objTwoY <= objOneY + objOne.height)
			if (objTwoY + objTwo.height >= objOneY)
				if (objTwoX <= objOneX + objOne.width)
					if (objTwoX + objTwo.width >= objOneX)
						return true;

		return false;
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

	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------

	@Override
	public void drawRunningBackground() {
		batcher.drawSprite(backgroundBounds, trafficBackgroundRegion);
		batcher.drawSprite(backgroundBounds2, trafficBackgroundRegion);
	}

	@Override
	public void drawRunningObjects() {
		// Draw Player Ship.
		playerShip.draw(batcher);
		
		for (int i = 0; i < totalEnemyShips; i++)
			if (enemyShips[i] != null)
				enemyShips[i].draw(batcher);
		
		for (int i = 0; i < totalLazers; i++)
			if (lazers[i] != null)
				lazers[i].draw(batcher);
		
//		// Draw Enemy Ships.
//		for (Ship enemyShip : enemyShipList)
//			enemyShip.draw(batcher);
//		
//		// Draw Lazers.
//		for (Lazer lazer : lazerList)
//			lazer.draw(batcher);
	}

	@Override
	public void drawRunningBounds() {// TODO: Add Proper Running Bounds.
		// Bounding Boxes
		batcher.beginBatch(AssetsManager.boundOverlay);
		
		// Lazer Button Bounds.
		batcher.drawSprite(lazerButtonBoundsOne, AssetsManager.boundOverlayRegion);
		batcher.drawSprite(lazerButtonBoundsTwo, AssetsManager.boundOverlayRegion);

		batcher.endBatch();
	}

}
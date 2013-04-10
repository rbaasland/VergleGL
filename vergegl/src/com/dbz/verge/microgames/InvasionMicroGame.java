package com.dbz.verge.microgames;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import android.util.Log;

import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;
import com.dbz.verge.microgames.objects.Lazer;
import com.dbz.verge.microgames.objects.Ship;

// TODO: *** Need to implement Speed/Levels.
// TODO: Hit detection on walls causes Jitter.
public class InvasionMicroGame extends MicroGame {

	// --------------
	// --- Fields ---
	// --------------

	// Assets
	public static Texture invasionTexture;
	public static TextureRegion invasionBackgroundRegion;
	public static TextureRegion invasionPlayerShipRegion;
	public static TextureRegion invasionPlayerLazerRegion;
	public static TextureRegion invasionEnemyShipRegion;
	public static TextureRegion invasionEnemyLazerRegion;

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
	private static final Rectangle lazerButtonBoundsOne = new Rectangle(0, 0, 1280, 640);
	private static final Rectangle lazerButtonBoundsTwo = new Rectangle(0, 0, 1120, 800);
	
	private static final Rectangle topWall = new Rectangle(0, 800, 1280, 20);
	private static final Rectangle leftWall = new Rectangle(-20, 0, 20, 800);
	private static final Rectangle rightWall = new Rectangle(1280, 0, 20, 800);
	private static final Rectangle bottomWall = new Rectangle(0, -20, 1280, 20);
	
	private static final Rectangle topNullZone = new Rectangle(-500, 1112, 2280, 100);
	private static final Rectangle leftNullZone = new Rectangle(-600, -312, 100, 1425);
	private static final Rectangle rightNullZone = new Rectangle(1780, -312, 100, 1425);
	private static final Rectangle bottomNullZone = new Rectangle(-500, -412, 2280, 100);
	
	private Rectangle backgroundBoundsOne = new Rectangle(0, 0, 1280, 800);
	private Rectangle backgroundBoundsTwo = new Rectangle(0, 800, 1280, 800);

	// Game Variables.
	public static final int MAX_ENEMY_SHIPS = 3;
	public int enemyShipsActive = 0;
	public int enemyShipIndex = 0;

	public static final int MAX_LAZERS = 100;
	public int lazerIndex = 0;
	
	public Rectangle spawn1_1 = new Rectangle(250, 800, 115, 100);
	public Rectangle spawn1_2 = new Rectangle(500, 800, 115, 100);
	public Rectangle spawn1_3 = new Rectangle(750, 800, 115, 100);
	
	public Rectangle spawn2_1 = new Rectangle(0, 800, 115, 100);
	public Rectangle spawn2_2 = new Rectangle(150, 800, 115, 100);
	public Rectangle spawn2_3 = new Rectangle(300, 800, 115, 100);
	
	public int spawnZoneIndex = 0;
	public int spawnIndex = 0;
	public Rectangle spawnZones[][] = { {spawn1_1, spawn1_2, spawn1_3}, 
										{spawn2_1, spawn2_2, spawn2_3} };
	
	// Game Objects.
	public Ship playerShip = new Ship(480, 50);
	public Ship enemyShips[] = new Ship[MAX_ENEMY_SHIPS];
	public Lazer lazers[] = new Lazer[MAX_LAZERS];
	
	// Player Ship Physics. // TODO: Try to implement in Ship object.
	public boolean rightPeak = false;
	public boolean leftPeak = false;
	
	// -------------------
	// --- Constructor ---
	// -------------------
	// TODO: REORGANIZE ALL FILES.
	public InvasionMicroGame() {
		randomizeCarsLanes();		// TODO: *** ???
		baseMicroGameTime = 10.0f;
		accelerometerEnabled = true;
		singleTouchEnabled = true;
		
		playerShip.playerControlled = true;
		playerShip.bounds.width = 125;
		playerShip.bounds.height = 125;
	}

	@Override
	public void load() {
		invasionTexture = new Texture("invasion.png");
		invasionBackgroundRegion = new TextureRegion(invasionTexture, 0, 0, 1280, 800);
		invasionPlayerShipRegion = new TextureRegion(invasionTexture, 1300, 20, 280, 280);
		invasionPlayerLazerRegion = new TextureRegion(invasionTexture, 1820, 10, 100, 100);
		invasionEnemyShipRegion = new TextureRegion(invasionTexture, 1600, 20, 210, 180);
		invasionEnemyLazerRegion = new TextureRegion(invasionTexture, 1930, 10, 100, 100);
	}

	@Override
	public void unload() { invasionTexture.dispose(); }

	@Override
	public void reload() { invasionTexture.reload(); }

	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {		
		updateBackground();
		updatePlayerShip(deltaTime);
		updateEnemyShips(deltaTime);
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
        		
        		if (lazerIndex >= MAX_LAZERS)
        			lazerIndex = 0;
        		lazers[lazerIndex] = playerShip.fireLazer();
        		lazerIndex++;
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
	public void reset() {	// TODO: *** Handle Resets.
		super.reset();
		lanes.clear();
		randomizeCarsLanes();
	}

	// Shuffle car lanes and put in queue
	public void randomizeCarsLanes() {	//TODO: *** ???
		
		for (int i = 0; i < lanePosition.length; i++) {
			int randTemp = rand.nextInt(lanePosition.length - 1);
			float temp = lanePosition[i];
			lanePosition[i] = lanePosition[randTemp];
			lanePosition[randTemp] = temp;
		}
		for (int i = 0; i < lanePosition.length; i++)
			lanes.add(lanePosition[i]);
	}
	
	public void generateEnemyShip() {
		if (enemyShipIndex >= MAX_ENEMY_SHIPS)
			enemyShipIndex = 0;
		if (spawnIndex >= MAX_ENEMY_SHIPS)
			spawnIndex = 0;
		
		float x = spawnZones[spawnZoneIndex][spawnIndex].lowerLeft.x;
		float y = spawnZones[spawnZoneIndex][spawnIndex].lowerLeft.y;
		spawnIndex++;
		
		enemyShips[enemyShipIndex] = new Ship(x, y);
		enemyShipsActive++;
		enemyShipIndex++;
	}
	
	// Moves the background
	public void updateBackground() { // TODO: Change background handling.
		float accelX = 1; //(30-game.getInput().getAccelX()); // Accelerometer max X value is 10 so background scrolls at least 20
		backgroundSpeedY = (int) (accelX * speedScalar[speed - 1]);
		if (backgroundBoundsOne.lowerLeft.y >= -backgroundBoundsOne.height + backgroundSpeedY )
			backgroundBoundsOne.lowerLeft.y -= backgroundSpeedY;
		else
			backgroundBoundsOne.lowerLeft.y = backgroundBoundsTwo.lowerLeft.y + backgroundBoundsOne.height - backgroundSpeedY;
		if (backgroundBoundsTwo.lowerLeft.y >= -backgroundBoundsTwo.height + backgroundSpeedY)
			backgroundBoundsTwo.lowerLeft.y -= backgroundSpeedY;
		else
			backgroundBoundsTwo.lowerLeft.y = backgroundBoundsOne.lowerLeft.y + backgroundBoundsOne.height - backgroundSpeedY;	
	}

	public void updatePlayerShip(float deltaTime) {
		if (!playerShipWallCollisionTest())			
			getPlayerShipAccelerometerInput();
		else {
			playerShip.setAcceleration(0, 0);
			playerShip.setVelocity(0, 0);
		}
		
		playerShip.update(deltaTime);
	}
	
	public void getPlayerShipAccelerometerInput() {
		float x = game.getInput().getAccelY() / 4;
		
		// Case 0: Turn from Right to Left.
		if (x < playerShip.getAccelerationX() && !rightPeak) {
			rightPeak = true;
			leftPeak = false;
			playerShip.reverseAcceleration();
		}
		
		// Case 1: Turn from Left to Right.
		else if (x > playerShip.getAccelerationX() && !leftPeak) {
			leftPeak = true;
			rightPeak = false;
			playerShip.reverseAcceleration();
		}
				
		// Case 2: Accelerometer is centered (generally), then clear acceleration.
		if (x > -0.75f && x < 0.75f)
			playerShip.setAcceleration(0.0f, 0.0f);
		else 
			playerShip.setAcceleration(x*2.0f, 0.0f);
	}
	
	public void updateEnemyShips(float deltaTime) {
		if (enemyShipsActive < MAX_ENEMY_SHIPS)
			generateEnemyShip();
		
		for (int i = 0; i < MAX_ENEMY_SHIPS; i++) {
			if (enemyShips[i] != null) {
//				if (enemyShipWallCollisionTest(enemyShips[i])) {
//					if (!enemyShips[i].spawning) {
//						playerShip.setAcceleration(0, 0);
//						playerShip.setVelocity(0, 0);
//					}		
//				}	
				
				if (enemyShips[i].active) {
					enemyShips[i].update(deltaTime);
					
					if (enemyShips[i].aiFireLazer) {	//TODO: Clean this up.
		        		if (lazerIndex >= MAX_LAZERS)
		        			lazerIndex = 0;
		        		lazers[lazerIndex] = enemyShips[i].fireLazer();
		        		lazers[lazerIndex].playerLazer = false;
		        		lazerIndex++;
		        		enemyShips[i].aiFireLazer = false;
					}
				}
				else {
					enemyShips[i] = null;
					enemyShipsActive--;
				}
			}
		}
	}
	
	public void updateLazers() {
		for (int i = 0; i < MAX_LAZERS; i++) {
			if (lazers[i] != null) {
				if (lazerWallCollisionTest(lazers[i]))
					lazers[i].active = false;
				
				if (lazers[i].active)
					lazers[i].update();
				else
					lazers[i] = null;
			}
		}
	}
	
	public boolean objectCollisionsTest() {
		boolean collision = false;
		
		// Collision Cases 0 & 1: Player or Enemy hit by Lazer.
		for (int i = 0; i < MAX_LAZERS; i++) {
			if (lazers[i] != null) {
				// Collision Case 0: Player hit by Lazer.
				if (collision(playerShip.bounds, lazers[i].bounds)) {
					AssetsManager.playSound(AssetsManager.explosionSound);
					microGameState = MicroGameState.Lost;
					playerShip.health -= lazers[i].damage;
					lazers[i].active = false;
					collision = true;
				}
				
				for (int j = 0; j < MAX_ENEMY_SHIPS; j++) {
					if (enemyShips[j] != null) {
						// Collision Case 1: Enemy hit by Lazer.
						if (collision(enemyShips[j].bounds, lazers[i].bounds)) {
							AssetsManager.playSound(AssetsManager.explosionSound);
							enemyShips[j].health -= lazers[i].damage;
							lazers[i].active = false;
							collision = true;
						}
					}
				}
			}		// TODO: Could probably combine these loops better...
		}
		
		// Collision Cases 2 & 3: Player or Enemy hit by Enemy.
		for (int i = 0; i < MAX_ENEMY_SHIPS; i++) {
			if (enemyShips[i] != null) {
				// Collision Case NULL: Enemy enters a NULL Zone.
				if (collision(enemyShips[i].bounds, topNullZone) || collision(enemyShips[i].bounds, bottomNullZone) ||
					collision(enemyShips[i].bounds, leftNullZone) || collision(enemyShips[i].bounds, rightNullZone)) {
					enemyShips[i].active = false;
					break;
				}
					
				// Collision Case 2: Player hit by Enemy.
				if (collision(playerShip.bounds, enemyShips[i].bounds)) {
					AssetsManager.playSound(AssetsManager.explosionSound);
					microGameState = MicroGameState.Lost;
					playerShip.health = 0;
					enemyShips[i].health = 0;
					collision = true;
				}
	
				for (int j = 0; j < MAX_ENEMY_SHIPS; j++) {
					if (enemyShips[j] != null) {
						// Collision Case 3: Enemy hit by Enemy
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
		
		return collision;
	}

	public boolean playerShipWallCollisionTest() {
		boolean collision = true;
		
		// Collision Case 4: Player Ship collides with Wall (Edge of Screen).
		if (collision(playerShip.bounds, leftWall))
			playerShip.bounds.lowerLeft.x = leftWall.lowerLeft.x + leftWall.width + 1;
		else if (collision(playerShip.bounds, rightWall))
			playerShip.bounds.lowerLeft.x = rightWall.lowerLeft.x - playerShip.bounds.width - 1;
		else
			collision = false;
		
		return collision;
	}
	
	public boolean enemyShipWallCollisionTest(Ship enemyShip) {
		boolean collision = true;
		
		// Collision Case 5: Enemy Ship collides with Wall (Edge of Screen).
		if (collision(enemyShip.bounds, topWall))
			enemyShip.bounds.lowerLeft.y = topWall.lowerLeft.y - enemyShip.bounds.height - 1;
		else if (collision(enemyShip.bounds, leftWall))
			enemyShip.bounds.lowerLeft.x = leftWall.lowerLeft.x + leftWall.width + 1;
		else if (collision(enemyShip.bounds, rightWall))
			enemyShip.bounds.lowerLeft.x = rightWall.lowerLeft.x - enemyShip.bounds.width - 1;
		else if (collision(enemyShip.bounds, bottomWall)) 
			enemyShip.bounds.lowerLeft.y = bottomWall.lowerLeft.y + bottomWall.height + 1;
		else
			collision = false;
		
		return collision;
	}
	
	public boolean lazerWallCollisionTest(Lazer lazer) {	// TODO: Code can definitely be generalized to be reused for PlayerShip, etc.
		boolean collision = true;
		
		// Collision Case 6: Lazer collides with Wall (Edge of Screen).
		if (collision(lazer.bounds, topWall))
			lazer.bounds.lowerLeft.y = topWall.lowerLeft.y - lazer.bounds.height - 1;
		else if (collision(lazer.bounds, leftWall))
			lazer.bounds.lowerLeft.x = leftWall.lowerLeft.x + leftWall.width + 1;
		else if (collision(lazer.bounds, rightWall))
			lazer.bounds.lowerLeft.x = rightWall.lowerLeft.x - lazer.bounds.width - 1;
		else if (collision(lazer.bounds, bottomWall)) 
			lazer.bounds.lowerLeft.y = bottomWall.lowerLeft.y + bottomWall.height + 1;
		else
			collision = false;
		
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
		batcher.beginBatch(invasionTexture);
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
		batcher.drawSprite(backgroundBoundsOne, invasionBackgroundRegion);
		batcher.drawSprite(backgroundBoundsTwo, invasionBackgroundRegion);
	}

	@Override
	public void drawRunningObjects() {
		// Draw Player Ship.
		playerShip.draw(batcher);
		
		// Draw Enemy Ships.
		for (int i = 0; i < MAX_ENEMY_SHIPS; i++)
			if (enemyShips[i] != null)
				enemyShips[i].draw(batcher);
		
		// Draw Lazers.
		for (int i = 0; i < MAX_LAZERS; i++)
			if (lazers[i] != null)
				lazers[i].draw(batcher);

	}

	@Override
	public void drawRunningBounds() { // TODO: Add Proper Running Bounds.
		// Bounding Boxes
		batcher.beginBatch(AssetsManager.boundOverlay);
		
		// Lazer Button Bounds.
		batcher.drawSprite(lazerButtonBoundsOne, AssetsManager.boundOverlayRegion);
		batcher.drawSprite(lazerButtonBoundsTwo, AssetsManager.boundOverlayRegion);

		batcher.endBatch();
	}

}
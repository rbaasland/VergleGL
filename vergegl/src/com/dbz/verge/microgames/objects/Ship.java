package com.dbz.verge.microgames.objects;

import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.microgames.InvasionMicroGame;

// TODO: Pull out into GameObject class.
public class Ship {	// TODO: Add collision inside of GameObjects.
	// Physics.
	// private static final Vector2 FRICTION = new Vector2(0, 0);	// TODO: Implement in World with Vector2f.
																	// *Won't be needed in Space.*
	// private static final Vector2 GRAVITY = new Vector2(0, 9.8); // TODO: Implement in World with Vector2f.
	private static final Vector2 MAX_ACCELERATION = new Vector2(10, 0);
	private static final Vector2 MAX_DECELERATION = new Vector2(-10, 0);
	private static final Vector2 MAX_VELOCITY = new Vector2(10, 0);
	private Vector2 acceleration = new Vector2(0, 0);
	private Vector2 velocity = new Vector2(0, 0);
	private int width = 80, height = 170;
	public Rectangle bounds;

	// Game Logic.
	public boolean playerControlled = false;
	public boolean active = true;
	public int health = 100;
	private int lazerAmmo = 100;					// TODO: Lazer Overheat instead?

	// Color of Ship / Sprite to Draw.

	public Ship(int positionX, int positionY) {
		this.bounds = new Rectangle(positionX, positionY, width, height);
	}
	
	public void update(float deltaTime) {
		applyAcceleration();
		
		// Updates Ship Game Logic.
		if (health <= 0)
			active = false;
		
		// Updates AI-driven ships.
		if (!playerControlled)
			updateAI(deltaTime);
	}
	
	public void updateAI(float deltaTime) {}
	
	public void setAcceleration(int x, int y) {
		acceleration.x = x;
		acceleration.y = y;
	}
	
	public void addAcceleration(int x, int y) {
		acceleration.x += x;
		acceleration.y += y;
		
		// Caps Acceleration at Max Acceleration.
		if (acceleration.x > MAX_ACCELERATION.x)
			acceleration.x = MAX_ACCELERATION.x;
		if (acceleration.y > MAX_ACCELERATION.y)
			acceleration.y = MAX_ACCELERATION.y;
		
		// Caps Deceleration at Max Deceleration.
		if (acceleration.x < MAX_DECELERATION.x)
			acceleration.x = MAX_DECELERATION.x;
		if (acceleration.y < MAX_DECELERATION.y)
			acceleration.y = MAX_DECELERATION.y;
	}

	public void reverseAcceleration() {
		acceleration.x = -acceleration.x;
		acceleration.y = -acceleration.y;
	}
	
	private void applyAcceleration() {	// TODO: Move above Acceleration setters?
		velocity.add(acceleration);
		
		// Caps the Velocity at the Max Velocity.
		if (velocity.x > MAX_VELOCITY.x)
			velocity.x = MAX_VELOCITY.x;
		if (velocity.y > MAX_VELOCITY.y)
			velocity.y = MAX_VELOCITY.y;
		
		if (velocity.x < -MAX_VELOCITY.x)
			velocity.x = -MAX_VELOCITY.x;
		if (velocity.y < -MAX_VELOCITY.y)
			velocity.y = -MAX_VELOCITY.y;
		
		// Updates Ship Position.
		bounds.lowerLeft.add(velocity);
	}
	
	public void setVelocity(int x, int y) {
		velocity.x = x;
		velocity.y = y;
	}
	
	public Lazer fireLazer() {
		Lazer lazer = null;
		
		if (lazerAmmo >= 0) {
			lazer = new Lazer(bounds);
//			InvasionMicroGame.lazerList.add();	 // TODO: Do something about this..
			AssetsManager.playSound(AssetsManager.hitSound);
			lazerAmmo--;
		} 
		
		return lazer;
	}
	
	// TODO: Do something about this..
	public void draw(SpriteBatcher batcher) { 
		batcher.drawSprite(bounds, InvasionMicroGame.trafficMonsterCarRegion); 
	}
}

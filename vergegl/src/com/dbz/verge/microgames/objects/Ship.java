package com.dbz.verge.microgames.objects;

import android.util.Log;

import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.microgames.InvasionMicroGame;

// TODO: Implement mass, force to replace current acceleration?
// TODO: Pull out into GameObject class.
public class Ship {	// TODO: Add collision inside of GameObjects.
	// Physics.
	 private static final float FRICTION = 0.9f;	// TODO: Implement in World with Vector2f. Understand More.
																	// *Won't be needed in Space.*
	// private static final Vector2 GRAVITY = new Vector2(0, 9.8); // TODO: Implement in World
	private static final Vector2 MAX_ACCELERATION = new Vector2(10.0f, 10.0f);
	private static final Vector2 MAX_DECELERATION = new Vector2(-10.0f, -10.0f);
	private static final Vector2 MAX_VELOCITY = new Vector2(20.0f, 20.0f);
	private Vector2 acceleration = new Vector2(0.0f, 0.0f);
	private Vector2 velocity = new Vector2(0.0f, 0.0f);
	private float mass = 1.0f;
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
		applyPhysics();
		
		// Updates Ship Game Logic.
		if (health <= 0)
			active = false;
		
		// Updates AI-driven ships.
		if (!playerControlled)
			updateAI(deltaTime);
	}
	
	public void updateAI(float deltaTime) {
//		addAcceleration(0.0f, -0.01f);
	}
	
	public void setAcceleration(float x, float y) {
		acceleration.x = x;
		acceleration.y = y;
	}
	
	public void addAcceleration(float x, float y) {
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
		
		Log.d("AccelerationX", "Y: "+ acceleration.y);
	}

	public void reverseAcceleration() {
		acceleration.x = -acceleration.x;
		acceleration.y = -acceleration.y;
	}
	
	public float getAccelerationX() {
		return acceleration.x;
	}
	
	private void applyPhysics() {	// TODO: Move above Acceleration setters?
		velocity.add(acceleration);
		velocity.mul(FRICTION);	// TODO: Make 0.1 - 1.0 (higher = more friction)
		
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
	
	public void setVelocity(float x, float y) {
		velocity.x = x;
		velocity.y = y;
	}
	
	public Lazer fireLazer() {
		Lazer lazer = null;
		
		if (lazerAmmo >= 0) {
			lazer = new Lazer(bounds);
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

package com.dbz.verge.microgames.objects;

import android.util.Log;

import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.microgames.InvasionMicroGame;

public class Lazer {
	// Physics.
	public Rectangle bounds;
	private int width = 25, height = 50;
	private Vector2 velocity = new Vector2(0, 3);
	private Vector2 acceleration = new Vector2(0, 3);

	// Game Logic.
	public boolean active = true;
	public int damage = 100;
	
	public Lazer(Rectangle shooterBounds) {
		float x = shooterBounds.lowerLeft.x + (shooterBounds.width / 2) - (width / 2);	
		float y = shooterBounds.lowerLeft.y + shooterBounds.height; // + (shooterBounds.height / 2);
		bounds = new Rectangle(x, y, width, height);
	}
	
	public void update() {
		velocity.add(acceleration);
		bounds.lowerLeft.add(velocity);
	}

	public void reverseAcceleration() {
		acceleration.x = -acceleration.x;
		acceleration.y = -acceleration.y;
	}
	
	public void setVelocity(float x, float y) {
		velocity.x = x;
		velocity.y = y;
	}
	
	// TODO: Do something about this...
	public void draw(SpriteBatcher batcher) { 
		batcher.drawSprite(bounds, InvasionMicroGame.invasionShipRegion); 
	}
}
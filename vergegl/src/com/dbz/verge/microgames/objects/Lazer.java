package com.dbz.verge.microgames.objects;

import android.util.Log;

import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.microgames.InvasionMicroGame;

public class Lazer {
	// Physics.
	public Rectangle bounds;
	private int width = 25, height = 25;
	private Vector2 velocity;
	private Vector2 acceleration;

	// Game Logic.
	public boolean active = true;
	public int damage = 100;
	public boolean playerLazer = true;
	
	public Lazer(Rectangle shooterBounds, boolean isPlayer) { // TODO: Clean this up.
		if (isPlayer) {
			float x = shooterBounds.lowerLeft.x + (shooterBounds.width / 2) - (width / 2);	
			float y = shooterBounds.lowerLeft.y + shooterBounds.height;
			bounds = new Rectangle(x, y, width, height);
			velocity = new Vector2(0, 3);
			acceleration = new Vector2(0, 3);
		} else {
			float x = shooterBounds.lowerLeft.x + (shooterBounds.width / 2) - (width / 2);	
			float y = shooterBounds.lowerLeft.y - height;
			bounds = new Rectangle(x, y, width, height);
			velocity = new Vector2(0.0f, -3.0f);
			acceleration = new Vector2(0.0f, 0.0f);
		}
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
		if (playerLazer)
			batcher.drawSprite(bounds, InvasionMicroGame.invasionPlayerLazerRegion);
		else
			batcher.drawSprite(bounds, InvasionMicroGame.invasionEnemyLazerRegion);
	}
}
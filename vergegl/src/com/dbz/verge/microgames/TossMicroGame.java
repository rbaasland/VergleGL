package com.dbz.verge.microgames;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// TODO: Add comments.
// TODO: Add speed implementation.
// TODO: Add level implementation.
// TODO: Fix jerky back board collision detection.

public class TossMicroGame extends MicroGame {

	// --------------
	// --- Fields ---
	// --------------

	final private int gravity = 1;
	private float velocityX = 0;
	private float velocityY = 0;
	private int ballStartX = 1050;
	private int ballStartY = 150;
	private boolean touch = false;

	// Animation scalar based on speed variable.
	private float animationScalar[] = { 1.0f, 1.5f, 2.0f };

	private Rectangle ball = new Rectangle(ballStartX, ballStartY, 80, 80);
	private Rectangle backBoard = new Rectangle(200, 325, 25, 215);
	private Rectangle hoop = new Rectangle(250, 325, 75, 50);
	private Rectangle court = new Rectangle(0, 0, 1280, 150);
	private Rectangle freeThrow = new Rectangle(800, 0, 50, 800);
	private ArrayList<Rectangle> previousBalls = new ArrayList<Rectangle>();

	// -------------------
	// --- Constructor ---
	// -------------------

	public TossMicroGame(Game game) {
		super(game);
	}

	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based win.
		 if (lostTimeBased(deltaTime)){
		 Assets.playSound(Assets.hitSound);
		 return;
		 }
		 if (collision(ball, hoop)){
		 Assets.playSound(Assets.highJumpSound);
		 microGameState = MicroGameState.Won;
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
			if (targetTouchDown(event, touchPoint, ball)) {
				velocityX = 0;
				velocityY = 0;
				touch = true;
			}
			if (touch)
				if (targetTouchUp(event, touchPoint, ball)
						|| event.type == TouchEvent.TOUCH_UP) {
					ballDirection(ball, previousBalls, deltaTime);
					previousBalls.clear();
					touch = false;
				}
			// Touching and dragging your balls
			if (touch)
				if (targetTouchDragged(event, touchPoint, ball)
						|| event.type == TouchEvent.TOUCH_DRAGGED) {
					previousBalls.add(new Rectangle(touchPoint.x - ball.width
							/ 2, touchPoint.y - ball.height / 2, ball.width,
							ball.height));
					ball.lowerLeft.x = touchPoint.x - ball.width / 2;
					ball.lowerLeft.y = touchPoint.y - ball.height / 2;
					if (collision(ball, freeThrow)) {
						touch = false;
						ball.lowerLeft.set(ballStartX, ballStartY);
					}
				}
			// Non-Unique, Super Class Bounds (TOUCH_UP) Check.
			if (event.type == TouchEvent.TOUCH_UP) {
				super.updateRunning(touchPoint);
			}
			Log.d("touch", String.valueOf(event.type));
		}
		if (!touch) {
			moveBall();
		}
	}

	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------

	@Override
	public void reset() {
		super.reset();

	}

	public void ballDirection(Rectangle current, List<Rectangle> previous,
			float deltaTime) {
		float directionX = 0;
		float directionY = 0;
		if (previous.size() >= 5)
			for (int i = previous.size() - 5; i < previous.size(); i++) {
				directionX += current.lowerLeft.x - previous.get(i).lowerLeft.x;
				directionY += current.lowerLeft.y - previous.get(i).lowerLeft.y;
			}
		directionX /= 5;
		directionY /= 5;
		velocityX = directionX * .5f;
		velocityY = directionY * .5f;
	}

	public void moveBall() {
		if (velocityX > 0)
			velocityX -= .05f;
		if (velocityX < 0)
			velocityX += .05f;

		if (collision(ball, backBoard))
			velocityX = -velocityX;

		if (ball.lowerLeft.x < 1280 && ball.lowerLeft.x > -80)
			ball.lowerLeft.x += velocityX;
		else {
			ball.lowerLeft.set(ballStartX, ballStartY);
			velocityX = 0;
		}

		velocityY -= gravity;

		if (collision(ball, court)) {
			ball.lowerLeft.set(ballStartX, ballStartY);
			return;
		}
		if (!collision(ball, court))
			ball.lowerLeft.y += velocityY;

	}

	// Checks for collision.
	public boolean collision(Rectangle ball, Rectangle obstacle) {
		float obstacleX = obstacle.lowerLeft.x;
		float obstacleY = obstacle.lowerLeft.y;
		float ballX = ball.lowerLeft.x;
		float ballY = ball.lowerLeft.y;

		if (obstacleY <= ballY + ball.height)
			if (obstacleY + obstacle.height >= ballY)
				if (obstacleX <= ballX + ball.width)
					if (obstacleX + obstacle.width >= ballX)
						return true;

		return false;
	}

	// -------------------
	// --- Draw Method ---
	// -------------------

	@Override
	public void presentRunning() {
		batcher.beginBatch(Assets.toss);
		drawRunningBackground();
		drawRunningObjects();
		batcher.endBatch();
		//drawRunningBounds();
		drawInstruction("Toss!");
		super.presentRunning();
	}

	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------

	@Override
	public void drawRunningBackground() {
		batcher.drawSprite(0, 0, 1280, 800, Assets.tossBackgroundRegion);
	}

	@Override
	public void drawRunningObjects() {
		batcher.drawSprite(ball, Assets.tossBallRegion);
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
		batcher.drawSprite(ball, Assets.boundOverlayRegion);
		batcher.drawSprite(hoop, Assets.boundOverlayRegion);
		batcher.drawSprite(backBoard, Assets.boundOverlayRegion);
		batcher.drawSprite(court, Assets.boundOverlayRegion);
		batcher.drawSprite(freeThrow, Assets.boundOverlayRegion);
		batcher.endBatch();
	}

}

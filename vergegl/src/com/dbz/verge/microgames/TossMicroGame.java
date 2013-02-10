package com.dbz.verge.microgames;

import java.util.ArrayList;
import java.util.List;

import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;

public class TossMicroGame extends MicroGame {

	// --------------
	// --- Fields ---
	// --------------

	// Assets
	public static Texture toss;
    public static TextureRegion tossBallRegion;
    public static TextureRegion tossBackgroundRegion;
	
	// Physics variables
	final private int gravity = 1;
	private float velocityX = 0;
	private float velocityY = 0;
	private int ballStartX = 1050;
	private int ballStartY = 150;

	// Logic control of touching the ball
	private boolean touch = false;

	private int requiredBasketCount[] = { 1, 2, 3 };
	private int basketCount = 0;
	private Rectangle ball = new Rectangle(ballStartX, ballStartY, 80, 80);
	private Rectangle backBoard = new Rectangle(200, 325, 25, 215);
	private Rectangle hoop = new Rectangle(250, 325, 75, 50);
	private Rectangle court = new Rectangle(0, 0, 1280, 150);
	private Rectangle freeThrow = new Rectangle(500, 0, 50, 800);
	private ArrayList<Rectangle> previousBalls = new ArrayList<Rectangle>();

	// -------------------
	// --- Constructor ---
	// -------------------

	public TossMicroGame() {
		// Extend allowed time.
		baseMicroGameTime = 10.0f;
	}
	
	@Override
	public void load() {
		toss = new Texture("toss.png");
        tossBallRegion = new TextureRegion(toss, 0, 800, 80, 80);
        tossBackgroundRegion = new TextureRegion(toss, 0, 0, 1280, 800);
	}
	
	@Override
	public void unload() {
		toss.dispose();
		
	}

	@Override
	public void reload() {
		toss.reload();
		
	}
	
	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
		// Checks for loss based on time.
		if (lostTimeBased(deltaTime)) {
			AssetsManager.playSound(AssetsManager.hitSound);
			return;
		}
		// Checks for collision based win
		// If the ball touches the hoop
		if (collision(ball, hoop)) {
			basketCount++;
			if (basketCount == requiredBasketCount[level - 1]) {
				AssetsManager.playSound(AssetsManager.highJumpSound);
				microGameState = MicroGameState.Won;
			}
			ball.lowerLeft.set(ballStartX, ballStartY);
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
			// Checks if the ball was touched
			if (targetTouchDown(event, touchPoint, ball)) {
				velocityX = 0;
				velocityY = 0;
				touch = true;
			}
			// Checks if the ball has stopped being touched
			// Finds ball's direction
			if (touch)
				if (targetTouchUp(event, touchPoint, ball)
						|| event.type == TouchEvent.TOUCH_UP) {
					ballDirection(ball, previousBalls);
					previousBalls.clear();
					touch = false;
				}
			// Checks for the ball being dragged
			// Stores position of ball while being dragged used to calculate
			// direction
			if (touch)
				if (targetTouchDragged(event, touchPoint, ball)
						|| event.type == TouchEvent.TOUCH_DRAGGED) {
					previousBalls.add(new Rectangle(touchPoint.x - ball.width
							/ 2, touchPoint.y - ball.height / 2, ball.width,
							ball.height));
					ball.lowerLeft.x = touchPoint.x - ball.width / 2;
					ball.lowerLeft.y = touchPoint.y - ball.height / 2;
					// Stops the ball from being dragged into the hoop
					if (collision(ball, freeThrow)) {
						touch = false;
						ball.lowerLeft.set(ballStartX, ballStartY);
					}
				}
			// Non-Unique, Super Class Bounds (TOUCH_UP) Check.
			if (event.type == TouchEvent.TOUCH_UP) {
				super.updateRunning(touchPoint);
			}
		}
		// moves ball while not being touched
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
		velocityX = 0;
		velocityY = 0;
		ball.lowerLeft.set(ballStartX, ballStartY);
		touch = false;
		basketCount = 0;
	}

	// Calculates the direction the ball has been thrown by finding the slope as
	// the ball is dragged
	// Also sets velocity based on the slope
	public void ballDirection(Rectangle current, List<Rectangle> previous) {
		float directionX = 0;
		float directionY = 0;
		// Adds some of the last points dragged
		if (previous.size() >= 5)
			for (int i = previous.size() - 5; i < previous.size(); i++) {
				directionX += current.lowerLeft.x - previous.get(i).lowerLeft.x;
				directionY += current.lowerLeft.y - previous.get(i).lowerLeft.y;
			}
		// Averages dragged points
		directionX /= 5;
		directionY /= 5;
		// sets velocity for X and Y based on slope
		// the speed of game alters the speed of ball
		velocityX = directionX * .5f * speedScalar[speed - 1];
		velocityY = directionY * .5f;
	}

	public void moveBall() {
		// Slows down ball's X velocity
		if (velocityX > 0)
			velocityX -= .05f;
		if (velocityX < 0)
			velocityX += .05f;

		// Reverses X direction if the ball collides with backboard
		if (collision(ball, backBoard)) {
			// corrects buggy effect if ball is dropped on top of back board
			// collision box
			ball.lowerLeft.x = backBoard.width + backBoard.lowerLeft.x;
			velocityX = -velocityX;
		}
		// Moves ball's X direction based on velocity
		// Resets ball to spawn position if ball is out of the bounds of the
		// screen
		if (ball.lowerLeft.x < 1280 && ball.lowerLeft.x > -80)
			ball.lowerLeft.x += velocityX;
		else {
			ball.lowerLeft.set(ballStartX, ballStartY);
		}

		// Applies a gravity effect to ball
		velocityY -= gravity;

		// Resets ball to spawn position if it collides with the court
		if (collision(ball, court)) {
			ball.lowerLeft.set(ballStartX, ballStartY);
			return;
		}
		// Moves ball Y's direction based on velocity
		if (!collision(ball, court))
			ball.lowerLeft.y += velocityY;
		
		// Resets ball if thrown out of bounds
		if(ball.lowerLeft.y < 0)
			ball.lowerLeft.set(ballStartX, ballStartY);
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
		batcher.beginBatch(toss);
		drawRunningBackground();
		drawRunningObjects();
		batcher.endBatch();
		// drawRunningBounds();
		drawInstruction("Toss!");
		super.presentRunning();
	}

	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------

	@Override
	public void drawRunningBackground() {
		batcher.drawSprite(0, 0, 1280, 800, tossBackgroundRegion);
	}

	@Override
	public void drawRunningObjects() {
		batcher.drawSprite(ball, tossBallRegion);
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(AssetsManager.boundOverlay);
		batcher.drawSprite(ball, AssetsManager.boundOverlayRegion);
		batcher.drawSprite(hoop, AssetsManager.boundOverlayRegion);
		batcher.drawSprite(backBoard, AssetsManager.boundOverlayRegion);
		batcher.drawSprite(court, AssetsManager.boundOverlayRegion);
		batcher.drawSprite(freeThrow, AssetsManager.boundOverlayRegion);
		batcher.endBatch();
	}

}

package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.gl.Animation;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

//TODO: Comment code. Try to match the standard that is created with other MicroGame comments.
//Add light to update at end of circuit
public class CircuitMicroGame extends MicroGame {

	// --------------
	//  Inner Classes
	// --------------

	//private inner class to manage each gap in the circuit
	private class CircuitGap {

		public boolean isClosed;
		public Animation connector;
		public Rectangle bounds;

		/**
		 * @param isClosed true if connection has been completed
		 * @param connector anim of textures to fill gap
		 * @param bounds rectangle bounds
		 */
		public CircuitGap(boolean isClosed, Animation connector,
				Rectangle bounds) {

			this.isClosed = isClosed;
			this.connector = connector;
			this.bounds = bounds;

		}
	}

	//private inner class to manage each intercept point on the spark's path through the circuit
	private class SparkIntercept {

		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		public static final int DOWN = 2;
		public static final int UP = 3;

		public int direction;
		public Rectangle bounds;


		public SparkIntercept(Rectangle intercept, int direction) {
			super();
			this.direction = direction;
			this.bounds = intercept;
		}


	}

	//private inner class to manage the spark
	private class Spark{

		//bounds for spark
		public Rectangle bounds;
		public Vector2 startCoordinates; //store starting coordinates for spark
		public int currentDirection;

		//vars set explicitly in class for ease of readability/modification
		public int speed = 16; //works best in power of 2
		public boolean isFired = false;
		public int directionChangeCount = 0;

		//used for spark animation. later, TODO: remove these and pass deltaTime into animation getKeyFrame()
		public int animationIndex = 0;
		private int animationDelayCounter = 3;

		/**
		 * 
		 * @param bounds bounds for spark
		 * @param initialDirection initial move direction for start when circuit is complete
		 */
		public Spark(Rectangle bounds, int initialDirection) {
			this.bounds = bounds;
			startCoordinates = bounds.lowerLeft.cpy();
			this.currentDirection = initialDirection;
		}
		
		private void resetSpark(){
			isFired = false;
			directionChangeCount = 0;
			animationIndex = 0;
			animationDelayCounter = 3;
			bounds.lowerLeft.set(startCoordinates);
		}
		
		private void changeSparkDirection(int direction){

			currentDirection = direction;

		}

		private void moveSpark(){

			switch(currentDirection){

			case SparkIntercept.LEFT:
				moveSparkLeft();
				break;
			case SparkIntercept.RIGHT:
				moveSparkRight();
				break;
			case SparkIntercept.DOWN:
				moveSparkDown();
				break;
			case SparkIntercept.UP:
				moveSparkUp();
				break;
			}

			updateAnimationIndex();
		}

		private void fireSpark() {
			currentDirection = SparkIntercept.RIGHT;
			moveSpark();
			isFired = true;
		}

		private void moveSparkRight() {
			bounds.lowerLeft.x += speed;
		}

		private void moveSparkLeft() {
			bounds.lowerLeft.x -= speed;
		}

		private void moveSparkDown() {
			bounds.lowerLeft.y -= speed;
		}

		private void moveSparkUp(){
			bounds.lowerLeft.y += speed;
		}

		//used to prevent array out of bounds. One MUST know how many textures in the spark animation
		private void updateAnimationIndex(){

			if(animationDelayCounter == 0){
				if (animationIndex == 0)
					animationIndex = 1;
				else animationIndex = 0;

				animationDelayCounter = 3;

			} else animationDelayCounter--;

		}

	}

	// --------------
	// --- Fields ---
	// --------------
	//used to store the different number of required gaps in a difficulty level
	//private int[] requiredGapCount = {2, 3, 4};

	// Bounds for touch detection.
	//Noted: Added 40 pixels to each y value to move the circuit lines up
	private Rectangle gapOneBounds = new Rectangle(512, 567, 128, 35); //first horizontal gap
	private Rectangle gapTwoBounds = new Rectangle(640, 60, 128, 35); //second horizontal gap
	private Rectangle gapThreeBounds = new Rectangle(752, 316, 35, 128); //1st vertical gap
	private Rectangle gapFourBounds = new Rectangle(1008, 213, 35, 128); //2nd vertical gap

	//Array of CircitGaps on the circuit
	private CircuitGap[] circuitGaps = initCircuitGaps(); //Note: this is called again to set to the appropriate level
	private boolean isFirstRun = true; //used to set circuitGaps appropriately based on level.

	//private Rectangle sparkBounds = new Rectangle(1, 620, 128, 128);
	Spark spark = new Spark(new Rectangle(1, 660, 128, 128), SparkIntercept.RIGHT); //spark start location

	//create bounds at each "turn" in the circuit
	private Rectangle turnOneBounds = new Rectangle(190, 660, 128,128); //first turn in circuit
	private Rectangle turnTwoBounds = new Rectangle(190, 532, 128, 128); //2nd turn in circuit
	private Rectangle turnThreeBounds = new Rectangle(702, 532, 128, 128); //...
	private Rectangle turnFourBounds = new Rectangle(702, 148, 128, 128);
	private Rectangle turnFiveBounds = new Rectangle(446, 148, 128, 128); 
	private Rectangle turnSixBounds = new Rectangle(446, 20, 128, 128); 
	private Rectangle turnSevenBounds = new Rectangle(958, 20, 128, 128); 
	private Rectangle turnEightBounds = new Rectangle(958, 532, 128, 128); 
	private Rectangle turnNineBounds = new Rectangle(1086, 532, 128, 128); 

	//create array of intercepts for iteration
	private SparkIntercept[] sparkIntercepts = {
			new SparkIntercept(turnOneBounds, SparkIntercept.DOWN), 
			new SparkIntercept(turnTwoBounds, SparkIntercept.RIGHT), 
			new SparkIntercept(turnThreeBounds, SparkIntercept.DOWN), 
			new SparkIntercept(turnFourBounds, SparkIntercept.LEFT), 
			new SparkIntercept(turnFiveBounds,SparkIntercept.DOWN), 
			new SparkIntercept(turnSixBounds, SparkIntercept.RIGHT),
			new SparkIntercept(turnSevenBounds, SparkIntercept.UP), 
			new SparkIntercept(turnEightBounds, SparkIntercept.RIGHT),
			new SparkIntercept(turnNineBounds, SparkIntercept.RIGHT) 
	};
	
	int interceptTotal = sparkIntercepts.length;

	// -------------------
	// --- Constructor ---
	// -------------------

	public CircuitMicroGame(Game game) {
		super(game);

		// Extend allowed time for testing.
		totalMicroGameTime = 10.0f;
	}

	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based loss.
		if (lostTimeBased(deltaTime)) {
			Assets.playSound(Assets.hitSound);
			return;
		}

		//loads gaps based on current level.
		if(isFirstRun){ //TODO: Code SMELL
			circuitGaps = initCircuitGaps();  
			isFirstRun = false;
		}

		//check if spark fired
		if(spark.isFired){

			if(collision(spark, sparkIntercepts[spark.directionChangeCount])){
				spark.changeSparkDirection(sparkIntercepts[spark.directionChangeCount].direction); //changes behavior of moveSpark()

				if(spark.directionChangeCount == interceptTotal-1){
					Assets.playSound(Assets.highJumpSound);
					microGameState = MicroGameState.Won;
					return;
				}

				spark.directionChangeCount++;
			}

			spark.moveSpark();
			return;
		}

		//check for circuit completion, if complete - fire spark
		if(isCircuitComplete()){
			spark.fireSpark();
			return;	
		}
		
		//get touches from screen
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		
		//iterate though all touches, check if any of the touches are in the gaps of the circuit
		for(TouchEvent touchEvent : touchEvents) {
			touchPoint.set(touchEvent.x, touchEvent.y);
			guiCam.touchToWorld(touchPoint);

			//Sets isClosed to true if gap is touched. 
			for (CircuitGap gap : circuitGaps){
				if(!gap.isClosed)
					if(targetTouchDown(touchEvent, touchPoint, gap.bounds)){
						gap.isClosed = true;
						Assets.playSound(Assets.hitSound);
					}
			}

			//Tests for non-unique touch events, which is currently pause only.
			if (touchEvents.get(0).type == TouchEvent.TOUCH_UP)
				super.updateRunning(touchPoint);
		}

	}

	// -----------------------------
	// --- Utility Update Method ---
	// -----------------------------

	@Override
	public void reset() {
		super.reset();
		//reset class members
		isFirstRun = true; //ensure's gaps are reloaded on next run
		//spark.bounds.lowerLeft.set(1, 620); //put spark back to original position
		spark.resetSpark();
		//reset spark members
		//spark.isFired = false;
		//spark.directionChangeCount = 0;
		//spark.animationIndex = 0;
		//spark.animationDelayCounter = 3;
		
	}

	/*
	 * Used to initialize the active gaps based on current level. Could probably use some refactoring. 
	 */
	private CircuitGap[] initCircuitGaps(){

		CircuitGap[] circuitGaps = new CircuitGap[4];

		switch(level){

		case 1: 
			circuitGaps[0] = new CircuitGap(false, Assets.circuitHorizontalGapAnim, gapOneBounds);
			circuitGaps[1] = new CircuitGap(false, Assets.circuitHorizontalGapAnim, gapTwoBounds);
			circuitGaps[2] = new CircuitGap(true, Assets.circuitVerticalGapAnim, gapThreeBounds);
			circuitGaps[3] = new CircuitGap(true, Assets.circuitVerticalGapAnim, gapFourBounds);
			break;

		case 2: 
			circuitGaps[0] = new CircuitGap(false, Assets.circuitHorizontalGapAnim, gapOneBounds);
			circuitGaps[1] = new CircuitGap(false, Assets.circuitHorizontalGapAnim, gapTwoBounds);
			circuitGaps[2] = new CircuitGap(false, Assets.circuitVerticalGapAnim, gapThreeBounds);
			circuitGaps[3] = new CircuitGap(true, Assets.circuitVerticalGapAnim, gapFourBounds);
			break;

		case 3:
			circuitGaps[0] = new CircuitGap(false, Assets.circuitHorizontalGapAnim, gapOneBounds);
			circuitGaps[1] = new CircuitGap(false, Assets.circuitHorizontalGapAnim, gapTwoBounds);
			circuitGaps[2] = new CircuitGap(false, Assets.circuitVerticalGapAnim, gapThreeBounds);
			circuitGaps[3] = new CircuitGap(false, Assets.circuitVerticalGapAnim, gapFourBounds);
			break;
		}

		return circuitGaps;
	}
	
	private boolean isCircuitComplete() {

		for(CircuitGap gap : circuitGaps) {
			if(!gap.isClosed)
				return false;
		}

		return true;
	}

	//due to nature of the beast... and to save time, 
	//handle the collision detection based on sparks direction
	public boolean collision(Spark spark, SparkIntercept sIntercept) {

		Rectangle sparkBounds = spark.bounds;
		Rectangle intercept = sIntercept.bounds;
		boolean collision = false;

		switch(spark.currentDirection){

		case SparkIntercept.LEFT:
			if(sparkBounds.lowerLeft.x <= intercept.lowerLeft.x)
				collision = true;
			break;

		case SparkIntercept.RIGHT:
			if(intercept.lowerLeft.x <= sparkBounds.lowerLeft.x)
				collision = true;
			break;

		case SparkIntercept.DOWN:
			if(sparkBounds.lowerLeft.y <= intercept.lowerLeft.y)
				collision = true;
			break;

		case SparkIntercept.UP:
			if(intercept.lowerLeft.y <= sparkBounds.lowerLeft.y)
				collision = true;
			break;	
		}

		return collision;

	}

	// -------------------
	// --- Draw Method ---
	// -------------------

	@Override
	public void presentRunning() {

		drawRunningBackground();
		drawRunningObjects();
		//drawRunningBounds();
		drawInstruction("Connect the Circuit!");
		super.presentRunning();
	}

	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------

	@Override
	public void drawRunningBackground() {
		// Draw background.
		batcher.beginBatch(Assets.circuitBackground);
		batcher.drawSprite(0, 0, 1280, 800, Assets.circuitBackgroundRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningObjects() {
		// Draw circuits
		batcher.beginBatch(Assets.circuit);
		batcher.drawSprite(0, 40, 1280, 800, Assets.circuitLinesRegion);

		//draw the appropriate connector when gap is closed
		for(CircuitGap gap : circuitGaps){
			if(gap.isClosed)
				batcher.drawSprite(gap.bounds, gap.connector.getKeyFrame(1));
			else batcher.drawSprite(gap.bounds, gap.connector.getKeyFrame(0));
		}

		if(spark.isFired){
			batcher.drawSprite(spark.bounds, 
					Assets.circuitSparkAnim.getKeyFrame(spark.animationIndex));
		}

		batcher.endBatch();
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
		batcher.drawSprite(spark.bounds, Assets.boundOverlayRegion); 
		batcher.drawSprite(turnOneBounds, Assets.boundOverlayRegion); 
		batcher.drawSprite(turnTwoBounds, Assets.boundOverlayRegion); 
		batcher.drawSprite(turnThreeBounds, Assets.boundOverlayRegion); 
		batcher.drawSprite(turnFourBounds, Assets.boundOverlayRegion);
		batcher.drawSprite(turnFiveBounds, Assets.boundOverlayRegion); 
		batcher.drawSprite(turnSixBounds, Assets.boundOverlayRegion);
		batcher.endBatch();
	}

}

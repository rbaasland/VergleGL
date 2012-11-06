package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.gl.Animation;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

//TODO: Comment code. Try to match the standard that is created with other MicroGame comments.
public class CircuitMicroGame extends MicroGame {
    
	//private inner class to manage each gap in the circuit
	private class CircuitGap {
		
		public boolean isClosed;
		public Animation connector;
		public Rectangle bounds;
		
		/**
		 * 
		 * @param isClosed true if connection has been completed
		 * @param connector anim of textures to fill gap
		 * @param bounds rectangle bounds
		 */
		public CircuitGap(boolean isClosed, Animation connector,
				Rectangle bounds) {
			super();
			this.isClosed = isClosed;
			this.connector = connector;
			this.bounds = bounds;
			
		}
	}
	
	
	private class SparkIntercept {
		
		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		public static final int DOWN = 2;
		public static final int UP = 3;
		
		public int direction;
		public Rectangle intercept;
		
		
		public SparkIntercept(Rectangle intercept, int direction) {
			super();
			this.direction = direction;
			this.intercept = intercept;
		}
		
		
	}
	
	//====================================================================
	//====Spark Stuff=====================================================
	//====================================================================
									  			//0, 620, 128, 128
	private Rectangle sparkBounds = new Rectangle(1, 620, 128, 128); //spark start location
	private Rectangle turnOneBounds = new Rectangle(190, 620, 128,128); //first turn in circuit
	private Rectangle turnTwoBounds = new Rectangle(190, 492, 128, 128); //2nd turn in circuit
	private Rectangle turnThreeBounds = new Rectangle(702, 492, 128, 128); //...
	private Rectangle turnFourBounds = new Rectangle(702, 108, 128, 128);
	private Rectangle turnFiveBounds = new Rectangle(446, 108, 128, 128); 
	private Rectangle turnSixBounds = new Rectangle(446, -20, 128, 128); 
	private Rectangle turnSevenBounds = new Rectangle(958, -20, 128, 128); 
	private Rectangle turnEightBounds = new Rectangle(958, 492, 128, 128); 
	private Rectangle turnNineBounds = new Rectangle(1086, 492, 128, 128); 
	
	
	private SparkIntercept[] sparkIntercepts = {new SparkIntercept(turnOneBounds, SparkIntercept.DOWN), 
												new SparkIntercept(turnTwoBounds, SparkIntercept.RIGHT), 
												new SparkIntercept(turnThreeBounds, SparkIntercept.DOWN), 
												new SparkIntercept(turnFourBounds, SparkIntercept.LEFT), 
												new SparkIntercept(turnFiveBounds,SparkIntercept.DOWN), 
												new SparkIntercept(turnSixBounds, SparkIntercept.RIGHT),
												new SparkIntercept(turnSevenBounds, SparkIntercept.UP), 
												new SparkIntercept(turnEightBounds, SparkIntercept.RIGHT),
												new SparkIntercept(turnNineBounds, SparkIntercept.RIGHT)};
	
	private float sparkSpeed = 1.1f;
	boolean sparkFired = false;
	int sparkInterceptTotal = sparkIntercepts.length;
    int sparkDirectionChangeCount = 0;
	
	private void changeSparkDirection(int direction){
		
		switch(direction){
		
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
	}
	
	private void fireSpark() {
		moveSparkLeft();
		sparkFired = true;
	}
		
	private void moveSparkRight() {
		sparkBounds.lowerLeft.x *= sparkSpeed;
	}
	
	private void moveSparkLeft() {
		sparkBounds.lowerLeft.x *= -sparkSpeed;
	}
	
	private void moveSparkDown() {
		sparkBounds.lowerLeft.y *= sparkSpeed;
	}
	
	private void moveSparkUp(){
		sparkBounds.lowerLeft.y *= -sparkSpeed;
	}
	
	
	
	
	//TODO: Problems with bound checking.... 
	public boolean collision(Rectangle spark, Rectangle intercept) {
		//from lazer

			if(intercept.lowerLeft.x <= spark.lowerLeft.x)
				return true;
			if(intercept.lowerLeft.y <= spark.lowerLeft.y)
				return true;
			
			return false;	
		
		/*
		float obstacleX = intercept.lowerLeft.x;
		float obstacleY = intercept.lowerLeft.y;
		float carX = spark.lowerLeft.x;
		float carY = spark.lowerLeft.y;
		
		if (obstacleY <= spark.height)
			if (obstacleY + intercept.height >= carY)
				if (obstacleX <= carX + spark.width)
					if (obstacleX + intercept.width >= carX)
						return true;
		
		return false;
*/
			
	}
	
	//====================================================================
	//====END OF Spark Stuff==============================================
	//====================================================================
	
	
	// --------------
	// --- Fields ---
	// --------------
    //used to store the different number of required gaps in a difficulty level
	private int[] requiredGapCount = {2, 3, 4};
	
	// Bounds for touch detection.
    private Rectangle gapOneBounds = new Rectangle(512, 527, 128, 35); //first horizontal gap
	private Rectangle gapTwoBounds = new Rectangle(640, 20, 128, 35); //second horizontal gap
	private Rectangle gapThreeBounds = new Rectangle(752, 276, 35, 128); //1st vertical gap
	private Rectangle gapFourBounds = new Rectangle(1008, 173, 35, 128); //2nd vertical gap
	
	// Boolean to track target touching.
	//private boolean touchingFistOne = false; 
	//private boolean touchingFistTwo = false;
	
	
	//creates CircitGaps based on current level.
	private CircuitGap[] circuitGaps = initCircitGaps();
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
		
		//check if spark fired
		if(sparkFired){
			
			if(collision(sparkBounds, sparkIntercepts[sparkDirectionChangeCount].intercept)){
				changeSparkDirection(sparkIntercepts[sparkDirectionChangeCount].direction);
				
				if(sparkDirectionChangeCount == sparkInterceptTotal-1){
					Assets.playSound(Assets.highJumpSound);
					microGameState = MicroGameState.Won;
				}
				
					sparkDirectionChangeCount++;
					return;
			}
		}
		
		
		//check for circuit completion, if complete - fire spark
		if(isCircuitComplete()){
			fireSpark();
    		return;	
		}
		
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
		for(TouchEvent touchEvent : touchEvents) {
			 touchPoint.set(touchEvent.x, touchEvent.y);
			 guiCam.touchToWorld(touchPoint);
			 
			/*
			 * 	Checks for touch in each incomplete circuit connection.
			 *  Sets isClosed to true if gap is touched. 
			*/
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
		//touchingFistOne = false;
		//touchingFistTwo = false;
		circuitGaps = initCircitGaps(); //resets vars to current level
		
	}
	
	/*
	 * Used to initialize the active gaps based on current level. Could probably use some refactoring. 
	 */
	  private CircuitGap[] initCircitGaps(){

		   CircuitGap[] circuitGaps = new CircuitGap[4];

		   switch(level){//TODO: does not work... this is called before the level is actually set.

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
	  
		
	   private boolean isCircuitComplete(){
		   
		   for(CircuitGap gap : circuitGaps){
	   			if(!gap.isClosed)
	   				return false;
		   }
	   	
	   		 return true;
	   }
		
	
	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		
		drawRunningBackground();
		drawRunningObjects();
		drawRunningBounds();
		drawInstruction("Connect the Circuit!" + String.valueOf(sparkBounds.lowerLeft.x) + String.valueOf(sparkBounds.lowerLeft.y));
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
		batcher.drawSprite(0, 0, 1280, 800, Assets.circuitLinesRegion);
		
		//draw the appropriate connector when gap is closed
		for(CircuitGap gap : circuitGaps){
			if(gap.isClosed)
				batcher.drawSprite(gap.bounds, gap.connector.getKeyFrame(1));
			else batcher.drawSprite(gap.bounds, gap.connector.getKeyFrame(0));
		}
		
		batcher.drawSprite(sparkBounds, Assets.circuitSparkState1Region);

		batcher.endBatch();
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(sparkBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(turnOneBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(turnTwoBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(turnThreeBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(turnFourBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(turnFiveBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.drawSprite(turnSixBounds, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.endBatch();
	}
	
}

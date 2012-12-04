package com.dbz.verge.microgames;

import java.util.Arrays;
import java.util.List;

import com.dbz.framework.DynamicGameObject;
import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

//TODO: Comment code. Try to match the standard that is created with other MicroGame comments.
//		Add light to update at end of circuit
//		This could be more understandable and maintainable if I created a Circuit Piece class 

public class CircuitMicroGame extends MicroGame {

	// --------------
	// --- Fields ---
	// --------------
	//used to store the different number of required gaps in a difficulty level
	//private int[] requiredGapCount = {2, 3, 4};
	
	// Used to determine win Condition (Number of direction changes per level)
	private int directionPointsInLevel[] = {12, 16, 19}; 

	// Animation scalar based on speed variable.
	//private float animationScalar[] = { 1.0f, 1.1f, 1.2f };
	
	//Circuit Arrays

	// Bounding rectangle for each circuit piece
	private Rectangle[] circuitPieces = {new Rectangle(190, 800-94, 405, 195), new Rectangle(240, 800-478, 328, 383), 
			new Rectangle(508, 800-612, 325, 122), new Rectangle(1022, 800-287, 557, 480)};

	// Corresponding textures for circuit piece
	private TextureRegion[] circuitTextures = {Assets.circuitLine1, Assets.circuitLine2, Assets.circuitLine3, Assets.circuitLine4}; //temp? put in assets?


	//Spark Direction Vectors

	//Direction points for each change in direction in the circuit.
	private Vector2[] directionPoints = 
			// Circuit one
		{new Vector2(24-16, 800-32), new Vector2(128-16, 800-178), new Vector2(350-16, 800-181), new Vector2(382-16, 800-127), 
			// Circuit two
			new Vector2(382,800-382), new Vector2(274,800-397), new Vector2(204,800-296), new Vector2(91,800-296), new Vector2(94,800-491),new Vector2(195,800-573),new Vector2(195,800-612), new Vector2(127,800-643),
			// Circuit three
			new Vector2(362,800-640), new Vector2(403,800-612), new Vector2(611,800-611), new Vector2(640,800-568),
			// Circuit four
			new Vector2(894+32,800-511) ,new Vector2(895+32,800-126), new Vector2(1272,800-126)};

	// 1-1 Correspondence to direction points - used to check if DirectionPoint starts a gap.
	private boolean doesDirectionPointStartCircuitGap[] = 
		{false, false, false, true,
			false, false, false, false, false, false, false, true,
			false, false, false, true,
			false, false, false}; 

	//Touch Specific

	// End nodes for touch points
	private Rectangle[] circuitNodes = 
		{new Rectangle(382-16, 800-127, 190,190),  new Rectangle(382,800-382, 190,190),    //gap 1
			new Rectangle(127,800-643, 190,190), new Rectangle(362, 800-640, 190,190),     //gap 2
			new Rectangle(640,800-568, 190,190), new Rectangle(894+32,800-511, 190,190)};  //gap 3

	// 1-1 Correspondence to circuit nodes - maintains list of whether or not its touched
	private boolean[] isCircuitNodeTouched = {false, false, false, false, false, false}; 


	//General
	public int sparkSpeed = 10; 	   // moved outside of spark class for easy changin'
	private int directionIndex = 0;    // index for direction vector array
	private int circuitNodesIndex = 0; // index tracks the current pair of nodes for touch detection
	private boolean circuitCompleted = false; // if multi-touch connects circuit

	// Vector pathway calculation
	private float rateOfChange = 0; 
	private float distance = 0;
	private float r = 0;

	Spark spark = new Spark(24-16, 800-32); //spark start location


	// -------------------
	// --- Constructor ---
	// -------------------

	public CircuitMicroGame(Game game) {
		super(game);

		//totalMicroGameTime = 10; //note, time based loss/win doesn't apply to this game.
		speedScalar = new float[] { 1.0f, 1.1f, 1.2f }; //TODO adjust sparkSpeed to work w/ Microgame.speedScalar
		// Calculate rate of change for first two vectors
		calculateRateofChange(); //TODO bug, spark starts slow because level isn't updated until after constructor call. In general, needs to be fixed.
								//IDEA, add level as a param to microgame constructor, then call super.updateLevel so we don't have to rework a bunch of code.
	}

	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {

		if(doesDirectionPointStartCircuitGap[directionIndex]) // if between a gap
			if(!circuitCompleted){				// if circuit not complete, game lost
				Assets.playSound(Assets.hitSound);
				microGameState = MicroGameState.Lost;
				return;
			}

		// logic to move spark -- See formula for getPathVector
		if(r == 1){ // pre calculations for moving to next pathway point - if r = 1, reached destination vector

			// Activate touch bounds for next gap when directionVector(previous) starts circuit gap
			if(doesDirectionPointStartCircuitGap[directionIndex] == true && circuitNodesIndex < 4) 
				circuitNodesIndex += 2;

			directionIndex++;				

			// if last last node, win
			if(directionIndex == directionPointsInLevel[level-1]-1){ 
				Assets.playSound(Assets.highJumpSound);
				microGameState = MicroGameState.Won;
				return;
			}

			calculateRateofChange();
			r = 0;

		}else {
			r += rateOfChange;

			// ensure we don't overshoot destination vector
			if(r > 1)	
				r = 1;

			// update spark position
			spark.position.set(getPathVector(r, directionPoints[directionIndex], 
					directionPoints[directionIndex+1]));
			spark.bounds.lowerLeft.set(spark.position.sub(Spark.SPARK_WIDTH / 2, Spark.SPARK_WIDTH / 2));
		}

		// get touches from screen
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

		for(TouchEvent touchEvent : touchEvents) {
			touchPoint.set(touchEvent.x, touchEvent.y);
			guiCam.touchToWorld(touchPoint);

			//circuit nodes are handled in pairs, hence + 2
			for (int i = circuitNodesIndex; i < circuitNodesIndex + 2 ; i++){

				if(targetTouchDownCenterCoords(touchEvent, touchPoint, circuitNodes[i])) {
					Assets.playSound(Assets.hitSound);
					isCircuitNodeTouched[i] = true;
				}

				if(touchEvent.type == TouchEvent.TOUCH_UP)
					isCircuitNodeTouched[i] = false;

			}

			//if both are true, circuit is complete
			if (isCircuitNodeTouched[circuitNodesIndex] && isCircuitNodeTouched[circuitNodesIndex+1]){
				circuitCompleted = true;
			} else circuitCompleted = false;

			//Tests for non-unique touch events, which is currently pause only.
			if (touchEvents.get(0).type == TouchEvent.TOUCH_UP)
				super.updateRunning(touchPoint);
		}

		//spark.update(deltaTime);//used to update animation.
	} 

	/*
	 * Formula for calculation set of points between two vectors.
	 * p = (1-r)u + (r)v -- p is a vector between r and v, calculated by constant r.
	 * Note: When r = 0, p = u, when r = 1, p = v
	 * TODO Very powerful formulation, should extract to Vector2.java
	 */
	private Vector2 getPathVector(float r, Vector2 vSource, Vector2 vDestination){
		return vSource.cpy().mul(1 - r).add(vDestination.cpy().mul(r));
	}


	/*
	 * Calculates the rate at which the vector should move from one vector to the next
	 */
	private void calculateRateofChange(){
		distance = directionPoints[directionIndex].dist(directionPoints[directionIndex+1]);
		rateOfChange = ((sparkSpeed *= speedScalar[speed-1])/distance);
	}

	// -----------------------------
	// --- Utility Update Method ---
	// -----------------------------

	@Override
	public void reset() {
		super.reset();
		//reset class members
		spark.resetSpark();
		Arrays.fill(isCircuitNodeTouched, false);
		sparkSpeed = 10; 	 
		directionIndex = 0;    
		circuitNodesIndex = 0; 
		circuitCompleted = false; 
		calculateRateofChange();
		r = 0;
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

		//level implementation
		for(int i = 0; i < level+1; i++){
			batcher.drawSpriteCenterCoords(circuitPieces[i], circuitTextures[i]);
		}

		if(!circuitCompleted || doesDirectionPointStartCircuitGap[directionIndex] == false){ //show spark only when on circuit
			spark.updateAnimationIndex();
			batcher.drawSprite(spark.bounds, Assets.circuitSparkAnim.getKeyFrame(spark.animationIndex)); 	
			//works but animation is slow
			//batcher.drawSprite(spark.bounds, Assets.circuitSparkAnim.getKeyFrame(spark.stateTime, Animation.ANIMATION_LOOPING));	
		}

		batcher.endBatch();
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
		batcher.drawSprite(spark.bounds, Assets.boundOverlayRegion);

		for (Vector2 point : directionPoints)
			batcher.drawSpriteCenterCoords(new Rectangle(point.x, point.y ,32, 32), Assets.boundOverlayRegion);

		for (Rectangle r : circuitNodes)
			batcher.drawSpriteCenterCoords(r, Assets.boundOverlayRegion);

		batcher.endBatch();
	}

	// --------------------
	// --- Game Objects ---
	// --------------------
	//private inner class to manage the spark
	private class Spark extends DynamicGameObject {

		private static final float SPARK_WIDTH = 128;
		private static final float SPARK_HEIGHT= 128;

		private Vector2 startCoordinates; //store starting coordinates for spark	
		private float stateTime = 0; //used for animation
		private int animationIndex = 0;
		private int animationDelayCounter = 3;

		//x,y is the center of the object
		public Spark(float x, float y) {
			super(x, y, SPARK_WIDTH, SPARK_HEIGHT);
			startCoordinates = super.position.cpy();
		}

		public void update(float deltaTime) {
			stateTime += deltaTime;
		}

		private void resetSpark(){
			animationIndex = 0;
			animationDelayCounter = 3;
			position.set(startCoordinates);
			bounds.lowerLeft.set(position).sub(SPARK_WIDTH / 2, SPARK_HEIGHT / 2);

		}

		// Used to prevent array out of bounds when iterating through animation.
		// One MUST know how many textures in the spark animation.
		private void updateAnimationIndex() {

			if(animationDelayCounter == 0){
				if (animationIndex == 0)
					animationIndex = 1;
				else animationIndex = 0;

				animationDelayCounter = 3;

			} else animationDelayCounter--;
		}
	}
}
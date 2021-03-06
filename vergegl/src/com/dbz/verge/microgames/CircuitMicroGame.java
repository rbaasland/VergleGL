package com.dbz.verge.microgames;

import java.util.Arrays;
import java.util.List;

import com.dbz.framework.DynamicGameObject;
import com.dbz.framework.gl.Animation;
import com.dbz.framework.gl.LineBatcher;
import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Circle;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;

//TODO: Comment code. Try to match the standard that is created with other MicroGame comments.
//		Add light to update at end of circuit
//		This could be more understandable and maintainable if I created a Circuit Piece class 

public class CircuitMicroGame extends MicroGame {

	// --------------
	// --- Fields ---
	// --------------
	
	// Assets
	public static Texture circuitBackground;
    public static TextureRegion circuitBackgroundRegion;
    public static Texture circuit;
    public static TextureRegion circuitLine1;
    public static TextureRegion circuitLine2;
    public static TextureRegion circuitLine3;
    public static TextureRegion circuitLine4;
    public static TextureRegion circuitSparkState1Region;
    public static TextureRegion circuitSparkState2Region;
    public static Animation circuitSparkAnim;
    
    private boolean isFirstRun = true;
	
	// Used to determine win Condition (Number of direction changes per level)
	private int directionPointsInLevel[] = {12, 16, 19};
	
	//Circuit Arrays
	
	// Bounding rectangle for each circuit piece
	private Rectangle[] circuitPieces = {new Rectangle(190+64, 800-94, 405+128, 195), new Rectangle(240+64, 800-478, 328+128, 383), 
			new Rectangle(508+64+64, 800-612, 325+128, 122), new Rectangle(1022+64+64, 800-287-64, 557, 480)};

	// Corresponding textures for circuit piece
	private TextureRegion[] circuitTextures = new TextureRegion[4]; //temp? put in assets?

	//Spark Direction Vectors
	
	//Direction points for each change in direction in the circuit.
	private Vector2[] directionPoints = 
			// Circuit one
		{new Vector2(24-16, 800-32), new Vector2(128-16, 800-178), new Vector2(350-16+128-16, 800-181), new Vector2(382-16+128-8, 800-127), 
			// Circuit two
			new Vector2(382+128-8,800-382), new Vector2(274+32+8,800-397), new Vector2(204,800-296), new Vector2(91,800-296), new Vector2(94,800-491),new Vector2(195,800-573),new Vector2(195,800-612), new Vector2(127,800-643),
			// Circuit three
			new Vector2(362+64+8,800-640), new Vector2(403+64+32,800-612), new Vector2(611+64+96-8,800-611), new Vector2(640+64+96-8,800-568),
			// Circuit four
			new Vector2(894+32+64+8+64,800-511-64+16) ,new Vector2(895+32+64+8+64,800-126-32-16), new Vector2(1272+64+8+64,800-126-64-16)};

	// 1-1 Correspondence to direction points - used to check if DirectionPoint starts a gap.
	private boolean doesDirectionPointStartCircuitGap[] = 
		{false, false, false, true,
			false, false, false, false, false, false, false, true,
			false, false, false, true,
			false, false, false}; 

	//Touch Specific

	// End nodes for touch points
	private Rectangle[] circuitNodes = 
		{new Rectangle(382-16+128-8, 800-127, 190,190),  new Rectangle(382+128-8,800-382, 190,190),    //gap 1
			new Rectangle(127,800-643, 190,190), new Rectangle(362+64,800-640, 190,190),     //gap 2
			new Rectangle(640+64+96-8,800-568, 190,190), new Rectangle(894+32+64+8+64,800-511-64+16, 190,190)};  //gap 3

	// 1-1 Correspondence to circuit nodes - maintains list of whether or not its touched
	private boolean[] isCircuitNodeTouched = {false, false, false, false, false, false}; 


	//General
	public int sparkSpeed = 6; 	   // moved outside of spark class for easy changin'
	private int directionIndex = 0;    // index for direction vector array
	private int circuitNodesIndex = 0; // index tracks the current pair of nodes for touch detection
	private boolean circuitCompleted = false; // if multi-touch connects circuit

	// Vector pathway calculation
	private float rateOfChange = 0; 
	private float distance = 0;
	private float r = 0;

	Spark spark = new Spark(24-16, 800-32); //spark start location
	//Spark spark = new Spark(100, 700); //for testing

	// -------------------
	// --- Constructor ---
	// -------------------

	public CircuitMicroGame() { 
		speedScalar = new float[] { 1.05f, 1.2f, 1.3f };
		multiTouchEnabled = true;
	}
	
	@Override
	public void load() {
		circuitBackground = new Texture("circuit_background.png");
        circuitBackgroundRegion = new TextureRegion(circuitBackground, 0, 0, 1280, 800);
          
        circuit = new Texture("circuit_items.png");
        circuitSparkState1Region = new TextureRegion(circuit, 0,896,128,128);
        circuitSparkState2Region = new TextureRegion(circuit, 128,896,128,128);
        circuitSparkAnim = new Animation(0.2f, circuitSparkState1Region, circuitSparkState2Region);
        
        //Circuit parts TODO draw lines based on vectors instead of images
        circuitLine1 = new TextureRegion(circuit, 0, 0, 544, 195);
        circuitLine2 = new TextureRegion(circuit, 0, 256, 448, 383);
        circuitLine3 = new TextureRegion(circuit, 304, 896, 456, 122);
        circuitLine4 = new TextureRegion(circuit, 512, 384, 512, 512);
        
    	circuitTextures[0] = circuitLine1;
    	circuitTextures[1] = circuitLine2;
    	circuitTextures[2] = circuitLine3;
    	circuitTextures[3] = circuitLine4;
	}

	@Override
	public void unload() {
		circuitBackground.dispose();
		circuit.dispose();

	}

	@Override
	public void reload() {
		circuitBackground.reload();
		circuit.reload();
		
	}
	
	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
		
		if(isFirstRun){
			calculateRateofChange(); // Calculate rate of change for first two vectors (used to be in constructor)
			isFirstRun = false;
		}
		
		if(doesDirectionPointStartCircuitGap[directionIndex]) // if between a gap
			if(!circuitCompleted){				// if circuit not complete, game lost
				AssetsManager.playSound(AssetsManager.hitSound);
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
				AssetsManager.playSound(AssetsManager.highJumpSound);
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
					AssetsManager.playSound(AssetsManager.hitSound);
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
		rateOfChange = ((sparkSpeed * speedScalar[speed-1])/distance);
	}

	// -----------------------------
	// --- Utility Update Method ---
	// -----------------------------

	@Override
	public void reset() {
		super.reset();
		//reset class members
		isFirstRun = true;
		spark.resetSpark();
		Arrays.fill(isCircuitNodeTouched, false);
		sparkSpeed = 6; 	 
		directionIndex = 0;    
		circuitNodesIndex = 0; 
		circuitCompleted = false; 
		calculateRateofChange();
		r = 0;
	}
	
	// -------------------
	// --- Draw Method ---
	// -------------------
	//ThickLineBatcher tlb = new ThickLineBatcher(glGraphics, 100);
	LineBatcher lineBatcher = new LineBatcher(glGraphics, 300, 2);
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
		batcher.beginBatch(circuitBackground);
		batcher.drawSprite(0, 0, 1280, 800, circuitBackgroundRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningObjects() {
		// Draw circuits
		batcher.beginBatch(circuit);
		
		//level implementation
		for(int i = 0; i < level+1; i++){
			batcher.drawSpriteCenterCoords(circuitPieces[i], circuitTextures[i]);
		}
		
		//draw spark
		if(!circuitCompleted || doesDirectionPointStartCircuitGap[directionIndex] == false){ //show spark only when on circuit
			spark.updateAnimationIndex();
			batcher.drawSprite(spark.bounds, circuitSparkAnim.getKeyFrame(spark.animationIndex)); //these used with spark texture
			
			 if(spark.frameCounter == 0){
				lineBatcher.beginBatch();
				spark.drawSparkLightning();

				lineBatcher.endBatch();
				spark.frameCounter = 2;
			 } 
			 spark.frameCounter--; 
		} else { //else, draw lightning between nodes
				lineBatcher.beginBatch();
				lineBatcher.drawLightning(circuitNodes[circuitNodesIndex].lowerLeft, 
													circuitNodes[circuitNodesIndex+1].lowerLeft, 120, 20);
				lineBatcher.endBatch();	
		}
			//works but animation is slow
			//batcher.drawSprite(spark.bounds, Assets.circuitSparkAnim.getKeyFrame(spark.stateTime, Animation.ANIMATION_LOOPING));	
		batcher.endBatch();
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(AssetsManager.boundOverlay);
		batcher.drawSprite(spark.bounds, AssetsManager.boundOverlayRegion);

		for (Vector2 point : directionPoints)
			batcher.drawSpriteCenterCoords(new Rectangle(point.x, point.y ,32, 32), AssetsManager.boundOverlayRegion);

		for (Rectangle r : circuitNodes)
			batcher.drawSpriteCenterCoords(r, AssetsManager.boundOverlayRegion);

		batcher.endBatch();
	}

	// --------------------
	// --- Game Objects ---
	// --------------------
	//private inner class to manage the spark
	private class Spark extends DynamicGameObject {

		private static final float SPARK_WIDTH = 128;
		private static final float SPARK_HEIGHT= 128;
		private static final float SPARK_RADIUS= 64;

		private Vector2 startCoordinates; //store starting coordinates for spark	
//		private float stateTime = 0; //used for animation
		private int animationIndex = 0;
		private int animationDelayCounter = 3;
		private int frameCounter = 2; //used to slow down spark visual

		//x,y is the center of the object
		public Spark(float x, float y) {
			super(x, y, SPARK_WIDTH, SPARK_HEIGHT);
			startCoordinates = super.position.cpy();
		}

//		public void update(float deltaTime) {
//			stateTime += deltaTime;
//		}

		private void resetSpark(){
			animationIndex = 0;
			animationDelayCounter = 3;
			position.set(startCoordinates);
			bounds.lowerLeft.set(position).sub(SPARK_WIDTH / 2, SPARK_HEIGHT / 2);

		}

		// Used to prevent array out of bounds when iterating through animation.
		// One MUST know how many textures in the spark animation.
		//TODO figure out how to use the animation class to handle animation
		private void updateAnimationIndex() {

			if(animationDelayCounter == 0){
				if (animationIndex == 0)
					animationIndex = 1;
				else animationIndex = 0;

				animationDelayCounter = 3;

			} else animationDelayCounter--;
		}
		
		/** Draw's a circular spark.  
		 * BeginBatch(), endbatch() still must be called before and after this function call*/
		public void drawSparkLightning(){
			
			Vector2[] points = Circle.genVerticesInCircle(position.add(SPARK_WIDTH / 2, SPARK_HEIGHT / 2), SPARK_RADIUS, 20); 
			//TODO fix code such that we don't need to use vector addition to line up sparks.
			
			//20 points
			lineBatcher.drawLightning(points[0], points[10], 90, 10);
			lineBatcher.drawLightning(points[1], points[11], 90, 10);
			lineBatcher.drawLightning(points[2], points[12], 90, 10);
			lineBatcher.drawLightning(points[3], points[13], 90, 10);
			lineBatcher.drawLightning(points[4], points[14], 90, 10);
			lineBatcher.drawLightning(points[5], points[15], 90, 10);
			lineBatcher.drawLightning(points[6], points[16], 90, 10);
			lineBatcher.drawLightning(points[7], points[17], 90, 10);
			lineBatcher.drawLightning(points[8], points[18], 90, 10);
			lineBatcher.drawLightning(points[9], points[19], 90, 10);
			
			/*//6 points
			lineBatcher.drawLightning(points[0], points[3], 80, 10);
			lineBatcher.drawLightning(points[1], points[4], 80, 10);
			lineBatcher.drawLightning(points[2], points[5], 80, 10);
			*/
			
			/*// 8points
			lineBatcher.drawLightning(points[0], points[5], 90, 10);
			lineBatcher.drawLightning(points[1], points[6], 90, 10);
			lineBatcher.drawLightning(points[3], points[8], 90, 10);
			lineBatcher.drawLightning(points[4], points[9], 90, 10);
			 */
		}
	}
}
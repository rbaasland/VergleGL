package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// TODO: Scale animation with speed level
public class DirtBikeMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------
	
	//number of obstacles per level
	private int[] levelObstacles = { 0, 1, 2 };
	
	//bike movement y-range
	private final int GROUND_LEVEL = 275;
	private final int MAX_JUMP_HEIGHT = 600;
	
	//drop rate (in pixels) per frame
	private float gravity = 12; 
	private float rotation = 0;

	//Bounds for obstacles
	private Rectangle obstaclesBounds = new Rectangle(660,225,200,160);
	private Rectangle obstacles2Bounds = new Rectangle(290,225,50,200);
	private Rectangle[] obstacles = {obstaclesBounds, obstacles2Bounds};
	
	// Bounds for dirt bike.
	//private Rectangle dirtBikeBounds = new Rectangle(0,225,256,256);
	private Rectangle dirtBikeRWheelBounds = new Rectangle(40,275,100,100);
	private Rectangle dirtBikeFWheelBounds = new Rectangle(235,275,100,100);
	private Rectangle dirtBikeFrameBounds = new Rectangle(0,260,250,220);
	private Rectangle gasBounds = new Rectangle(980,20,160,160);
	private Rectangle jumpBounds = new Rectangle(140,20,160,160);
	
	boolean gasOn=false;
	boolean hasJumped = false;
	boolean disableJumpButton = false;
	
	// -------------------
	// --- Constructor ---
	// ------------------- 
	
    public DirtBikeMicroGame(Game game) {
        super(game);
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
  		if (dirtBikeRWheelBounds.lowerLeft.x > 1200) {
			Assets.playSound(Assets.highJumpSound);
			microGameState = MicroGameState.Won;
			return;
		}
  		
  		
        for(int j=0; j < levelObstacles[level-1]; j++){
        	Rectangle tempFWheel = new Rectangle ((dirtBikeFWheelBounds.lowerLeft.x - (dirtBikeFWheelBounds.width-20)/2),(dirtBikeFWheelBounds.lowerLeft.y - (dirtBikeFWheelBounds.height-20)/2),(dirtBikeFWheelBounds.width-20),(dirtBikeFWheelBounds.height-20));
        	Rectangle tempRWheel = new Rectangle ((dirtBikeRWheelBounds.lowerLeft.x - (dirtBikeRWheelBounds.width-20)/2),(dirtBikeRWheelBounds.lowerLeft.y - (dirtBikeRWheelBounds.height-20)/2),(dirtBikeRWheelBounds.width-20),(dirtBikeRWheelBounds.height-20));
            if(collision(tempRWheel, obstacles[j]) || collision(tempFWheel, obstacles[j])){
            	microGameState = MicroGameState.Lost;
            	Assets.playSound(Assets.hitSound);
            	return;
            }

        }

		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);
	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
        	touchPoint.set(event.x, event.y);
        	// Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
	                
			//TODO: weird... the logic for touch_up only seems to work for the DINC, not the DINC 2
	        	//bug, when jumping while hitting the gas the bike doesn't move forward. AND works ok on jons phone.
	        if (targetTouchDragged(event, touchPoint, gasBounds)) {
	        	//used to ensure touch up on gas only affects gas
	        	if(event.type == TouchEvent.TOUCH_UP){ 
	        		gasOn=false;
	        		return;
	        	}
				gasOn=true;	
	        }
	        
	        
	        if(!disableJumpButton)
	        	if (!hasJumped && targetTouchDragged(event, touchPoint, jumpBounds)){
	        		hasJumped = true;
	        		disableJumpButton = true;
	        }


	     // Tests for non-unique touch events, which is currently pause only.
	    if (event.type == TouchEvent.TOUCH_UP)
	    	 super.updateRunning(touchPoint); 
	    }  
	    
        if(gasOn==true){
        	moveDirtBike();
        }
        
        if(hasJumped){
        	if(dirtBikeFrameBounds.lowerLeft.y <= MAX_JUMP_HEIGHT)
        		jump();
        	else hasJumped = false; 
        }
        
        if (!hasJumped && dirtBikeFWheelBounds.lowerLeft.y >= GROUND_LEVEL)
        	applyGravity();
        
        if(dirtBikeFWheelBounds.lowerLeft.y <= GROUND_LEVEL){
        	disableJumpButton = false;
        }
        
	}
	
	public void moveDirtBike() {
		dirtBikeFrameBounds.lowerLeft.x += 16 * speedScalar[level-1];
		dirtBikeFWheelBounds.lowerLeft.x += 16 * speedScalar[level-1];
		dirtBikeRWheelBounds.lowerLeft.x += 16 * speedScalar[level-1];
		rotation -= 80;
	}

	public void applyGravity(){
		dirtBikeFrameBounds.lowerLeft.y -= gravity;
		dirtBikeFWheelBounds.lowerLeft.y -= gravity;
		dirtBikeRWheelBounds.lowerLeft.y -= gravity;
		
	}
	
	public void jump(){
		dirtBikeFrameBounds.lowerLeft.y += 24;
		dirtBikeFWheelBounds.lowerLeft.y += 24;
		dirtBikeRWheelBounds.lowerLeft.y += 24;
	}
	
    // Checks for collision-based loss.
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
       
	// -----------------------------
	// --- Utility Update Method ---
	// -----------------------------
	
	@Override
	public void reset() {
		super.reset();
	}
	
	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		drawRunningBackground();
		drawRunningObjects();
		drawRunningBounds();
		drawInstruction("GO!");
		super.presentRunning();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	@Override
	public void drawRunningBackground() {
		// Draw background.
		batcher.beginBatch(Assets.dirtBikeBackground);
		batcher.drawSprite(0, 0, 1280, 800, Assets.dirtBikeBackgroundRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningObjects() {
		// dirt bike and gas
		batcher.beginBatch(Assets.dirtBikeBackground);
		batcher.drawSprite(dirtBikeFrameBounds, Assets.dirtBikeFrameRegion);
		batcher.drawSprite(dirtBikeRWheelBounds.lowerLeft.x,dirtBikeRWheelBounds.lowerLeft.y,dirtBikeRWheelBounds.width,dirtBikeRWheelBounds.height,rotation,Assets.dirtBikeWheelRegion);
		batcher.drawSprite(dirtBikeFWheelBounds.lowerLeft.x,dirtBikeFWheelBounds.lowerLeft.y,dirtBikeFWheelBounds.width,dirtBikeFWheelBounds.height,rotation,Assets.dirtBikeWheelRegion);
		batcher.drawSprite(gasBounds, Assets.dirtBikeGasPedalRegion);
		batcher.drawSprite(jumpBounds, Assets.dirtBikeJumpButtonRegion);
		if(levelObstacles[level-1] != 0)
			batcher.drawSprite(obstaclesBounds, Assets.dirtBikeObstacleOneRegion);
		if(levelObstacles[level-1] == 2)
			batcher.drawSprite(obstacles2Bounds, Assets.dirtBikeObstacleTwoRegion);
		batcher.endBatch();	
	}
	
	@Override
	public void drawRunningBounds() {}
}

package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

public class DirtBikeMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------
	
	//number of obstacles per level
	private int[] levelObstacles = { 0, 1, 2 };
	
	//bike movement y-range
	private final int GROUND_LEVEL = 225;
	private final int MAX_JUMP_HEIGHT = 600;
	
	//drop rate (in pixels) per frame
	private float gravity = 12; 

	//Bounds for obstacles
	private Rectangle obstaclesBounds = new Rectangle(660,225,200,150);
	private Rectangle obstacles2Bounds = new Rectangle(275,225,50,200);
	private Rectangle[] obstacles = {obstaclesBounds, obstacles2Bounds};
	
	// Bounds for dirt bike.
	private Rectangle dirtBikeBounds = new Rectangle(0,225,256,256);
	private Rectangle gasBounds = new Rectangle(1050,20,160,160);
	private Rectangle jumpBounds = new Rectangle(0,20,160,160);
	
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
  		if (dirtBikeBounds.lowerLeft.x > 1200) {
			Assets.playSound(Assets.highJumpSound);
			microGameState = MicroGameState.Won;
			return;
		}
  		
  		
        for(int j=0; j < levelObstacles[level-1]; j++){
        	
            if(collision(dirtBikeBounds, obstacles[j])){
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
        	if(dirtBikeBounds.lowerLeft.y <= MAX_JUMP_HEIGHT)
        		jump();
        	else hasJumped = false; 
        }
        
        if (!hasJumped && dirtBikeBounds.lowerLeft.y >= GROUND_LEVEL)
        	applyGravity();
        
        if(dirtBikeBounds.lowerLeft.y <= GROUND_LEVEL){
        	disableJumpButton = false;
        }
        
	}
	
	public void moveDirtBike() {
		dirtBikeBounds.lowerLeft.x += 16 * speedScalar[level-1] ;
	}

	
	public void applyGravity(){
		dirtBikeBounds.lowerLeft.y -= gravity;
		
	}
	
	public void jump(){
		dirtBikeBounds.lowerLeft.y += 24;
	}
	
    // Checks for collision-based loss.
   	public boolean collision(Rectangle bike, Rectangle obstacle) {
   		float obstacleX = obstacle.lowerLeft.x;
   		float obstacleY = obstacle.lowerLeft.y;
   		float carX = bike.lowerLeft.x;
   		float carY = bike.lowerLeft.y;
   		
   		if (obstacleY <= bike.height)
   			if (obstacleY + obstacle.height >= carY)
   				if (obstacleX <= carX + bike.width)
   					if (obstacleX + obstacle.width >= carX)
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
		batcher.drawSprite(dirtBikeBounds, Assets.dirtBikeRegion);
		batcher.drawSprite(gasBounds, Assets.gasPedalRegion);
		batcher.drawSprite(jumpBounds, Assets.gasPedalRegion);
		batcher.endBatch();
		
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		if(levelObstacles[level-1] != 0){
			batcher.beginBatch(Assets.boundOverlay);
		
		for(int j=0; j < levelObstacles[level-1]; j++){
			batcher.drawSprite(obstacles[j], Assets.boundOverlayRegion);
		}
		
		batcher.endBatch();
		}
	}
	
}

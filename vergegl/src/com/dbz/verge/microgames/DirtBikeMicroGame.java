package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;
import com.dbz.verge.MicroGame.MicroGameState;

// TODO: Scale animation with speed level
public class DirtBikeMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------
	
	// Assets
	public static Texture dirtBikeBackground;
    public static TextureRegion dirtBikeBackgroundRegion;
    public static TextureRegion dirtBikeRegion;
    public static TextureRegion dirtBikeGasPedalRegion;
    public static TextureRegion dirtBikeJumpButtonRegion;
    public static TextureRegion dirtBikeFrameRegion;
    public static TextureRegion dirtBikeWheelRegion;
    public static TextureRegion dirtBikeObstacleOneRegion;
    public static TextureRegion dirtBikeObstacleTwoRegion;
    public static TextureRegion dirtBikeFinishFlagRegion;
    public static TextureRegion dirtBikeManRegion;
    public static TextureRegion trainRegion;
    
	
	//number of obstacles per level
	private int[] levelObstacles = { 0, 1, 2 };
	
	//bike movement y-range
	private final int GROUND_LEVEL = 275;
	private final int MAX_JUMP_HEIGHT = 700;
	private final int MAX_MAN_JUMP = 500;
	private final int TRAIN_TOP = 250;
	
	//drop rate (in pixels) per frame
	private float gravity = 12; 
	private float rotation = 0;
	private float backgroundSpeedY;
	private float trainDisance;

	//Bounds for obstacles
	private Rectangle obstaclesBounds = new Rectangle(1000,225,200,160);
	private Rectangle obstacles2Bounds = new Rectangle(3000,225,50,200);
	private Rectangle flagBounds = new Rectangle(3500,225,155,230);
	private Rectangle[] obstacles = {obstaclesBounds, obstacles2Bounds};
	
	
	// Bounds for dirt bike.
	private Rectangle dirtBikeRWheelBounds = new Rectangle(40,275,100,100);
	private Rectangle dirtBikeFWheelBounds = new Rectangle(235,275,100,100);
	private Rectangle dirtBikeFrameBounds = new Rectangle(0,260,250,220);
	private Rectangle gasBounds = new Rectangle(980,20,160,160);
	private Rectangle jumpBounds = new Rectangle(140,20,160,160);
	
	//Bounds for alternate
	private Rectangle manBounds = new Rectangle(0,250,75,110);
	private Rectangle jumpBoundsAlt = new Rectangle(0,0,1280,800);
	private Rectangle trainBounds = new Rectangle(-100,-20,945,280);
	private Rectangle trainBounds2 = new Rectangle(1000,-20,945,280);
	
	//Bounds for backgrounds
	private Rectangle backgroundBounds = new Rectangle(0, 0, 1280, 800);
	private Rectangle backgroundBounds2 = new Rectangle(1280, 0, 1280, 800);
	
	boolean gasOn=true;
	boolean hasJumped = false;
	boolean disableJumpButton = false;
	
	// -------------------
	// --- Constructor ---
	// ------------------- 
	
    public DirtBikeMicroGame() { multiTouchEnabled = true; }
    
    public void load() {
    	dirtBikeBackground = new Texture("DirtBikeScreen.png");
        dirtBikeBackgroundRegion = new TextureRegion(dirtBikeBackground,0,0,1300,700);
        if (version == 0) {
            dirtBikeRegion = new TextureRegion(dirtBikeBackground, 1400,1,256,256);
            dirtBikeGasPedalRegion = new TextureRegion(dirtBikeBackground,1440,300,160,160);
            dirtBikeJumpButtonRegion = new TextureRegion(dirtBikeBackground,1440,460,160,160);
            dirtBikeFrameRegion = new TextureRegion(dirtBikeBackground,0,720,250,220);
            dirtBikeWheelRegion = new TextureRegion(dirtBikeBackground,270,720,100,100);
            dirtBikeObstacleOneRegion = new TextureRegion(dirtBikeBackground,400,720,200,160);
            dirtBikeObstacleTwoRegion = new TextureRegion(dirtBikeBackground,630,730,50,200);
            dirtBikeFinishFlagRegion = new TextureRegion(dirtBikeBackground,690,720,155,230);
        }
        if (version == 1) {
            dirtBikeManRegion = new TextureRegion(dirtBikeBackground,860,725,75,110);
            trainRegion = new TextureRegion(dirtBikeBackground,940,720,945,280);
        }
    }
    
	@Override
	public void unload() {
		dirtBikeBackground.dispose();	
	}

	@Override
	public void reload() {
		dirtBikeBackground.reload();
	}

	// ---------------------
	// --- Update Method ---
	// ---------------------
    
	@Override
	public void updateRunning(float deltaTime) {
		if (version == 0) {
			// Checks for time-based loss.
			if (lostTimeBased(deltaTime)) {
				AssetsManager.playSound(AssetsManager.hitSound);
				return;
			}
			
			moveBackground();
			
	  		if ((dirtBikeRWheelBounds.lowerLeft.x + dirtBikeRWheelBounds.width) > flagBounds.lowerLeft.x) {
				AssetsManager.playSound(AssetsManager.highJumpSound);
				microGameState = MicroGameState.Won;
				return;
			}
	  		
	        for(int j=0; j < levelObstacles[level-1]; j++){
	        	Rectangle tempFWheel = new Rectangle ((dirtBikeFWheelBounds.lowerLeft.x - (dirtBikeFWheelBounds.width-20)/2),(dirtBikeFWheelBounds.lowerLeft.y - (dirtBikeFWheelBounds.height-20)/2),(dirtBikeFWheelBounds.width-20),(dirtBikeFWheelBounds.height-20));
	        	Rectangle tempRWheel = new Rectangle ((dirtBikeRWheelBounds.lowerLeft.x - (dirtBikeRWheelBounds.width-20)/2),(dirtBikeRWheelBounds.lowerLeft.y - (dirtBikeRWheelBounds.height-20)/2),(dirtBikeRWheelBounds.width-20),(dirtBikeRWheelBounds.height-20));
	            if(collision(tempRWheel, obstacles[j]) || collision(tempFWheel, obstacles[j])){
	            	microGameState = MicroGameState.Lost;
	            	AssetsManager.playSound(AssetsManager.hitSound);
	            	return;
	            }

	        }

		}
		if (version == 1) {
			// Checks for time-based win.
			if (wonTimeBased(deltaTime)) {
				AssetsManager.playSound(AssetsManager.highJumpSound);
				return;
			}
			
	  		moveBackground();
	  		moveTrain();
	        if(!collision(manBounds, trainBounds) && !collision(manBounds, trainBounds2)){
	        	microGameState = MicroGameState.Lost;
	        	AssetsManager.playSound(AssetsManager.hitSound);
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
	        
	        if (version == 0) {        
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
	        if (version == 1) {
	    	    if (version == 1) {
	    	        if(!disableJumpButton)
	    	        if (!hasJumped && targetTouchDragged(event, touchPoint, jumpBoundsAlt)) {
	    	            hasJumped = true;
	    	            disableJumpButton = true;
	    	        }

	    	     // Tests for non-unique touch events, which is currently pause only.
	    	    if (event.type == TouchEvent.TOUCH_UP)
	    	    	 super.updateRunning(touchPoint); 
	    	    }
	    	    
	            if(gasOn==true){
	            	moveMan();
	            }
	            
	            if(hasJumped){
	            	if(manBounds.lowerLeft.y <= MAX_MAN_JUMP)
	            		jump();
	            	else 
	            		hasJumped = false; 
	            }
	            
	            if (!hasJumped && manBounds.lowerLeft.y >= TRAIN_TOP)
	            	applyGravity();

	            if(manBounds.lowerLeft.y <= TRAIN_TOP){
	            	disableJumpButton = false;
	            }
	        }
	    }//End of FOR  
	    if (version == 0) {
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
	    if (version == 1) {
	        if(gasOn==true){
	        	moveMan();
	        }
	        
	        if(hasJumped){
	        	if(manBounds.lowerLeft.y <= MAX_MAN_JUMP)
	        		jump();
	        	else 
	        		hasJumped = false; 
	        }
	        
	        if (!hasJumped && manBounds.lowerLeft.y >= TRAIN_TOP)
	        	applyGravity();

	        if(manBounds.lowerLeft.y <= TRAIN_TOP){
	        	disableJumpButton = false;
	        }
	    } 
	}
	
	public void moveBackground() {
		float accelX = (30-game.getInput().getAccelX()); // Accelerometer max X value is 10 so background scrolls at least 20
		backgroundSpeedY = (int) (accelX * speedScalar[speed - 1]);
		if (backgroundBounds.lowerLeft.x >= -backgroundBounds.width + backgroundSpeedY )
			backgroundBounds.lowerLeft.x -= backgroundSpeedY;
		else
			backgroundBounds.lowerLeft.x = backgroundBounds2.lowerLeft.x + backgroundBounds.width - backgroundSpeedY;

		if (backgroundBounds2.lowerLeft.x >= -backgroundBounds2.width + backgroundSpeedY)
			backgroundBounds2.lowerLeft.x -= backgroundSpeedY;
		else
			backgroundBounds2.lowerLeft.x = backgroundBounds.lowerLeft.x + backgroundBounds.width - backgroundSpeedY;
				
	}
	
	public void moveTrain() {
		trainDisance = 175 * speedScalar[level-1];
		float accelX = (30-game.getInput().getAccelX()); // Accelerometer max X value is 10 so background scrolls at least 20
		backgroundSpeedY = 15 * speedScalar[speed - 1];
		if (trainBounds.lowerLeft.x >= -trainBounds.width + backgroundSpeedY)
			trainBounds.lowerLeft.x -= backgroundSpeedY;
		else
			trainBounds.lowerLeft.x = trainBounds2.lowerLeft.x + trainBounds.width + trainDisance;

		if (trainBounds2.lowerLeft.x >= -trainBounds2.width + backgroundSpeedY)
			trainBounds2.lowerLeft.x -= backgroundSpeedY;
		else
			trainBounds2.lowerLeft.x = trainBounds.lowerLeft.x + trainBounds.width + trainDisance;
				
	}
	
	public void moveDirtBike() {
		if ((dirtBikeFrameBounds.lowerLeft.x + dirtBikeFrameBounds.width) < 500) {
		    dirtBikeFrameBounds.lowerLeft.x += 16 * speedScalar[level-1];
		    dirtBikeFWheelBounds.lowerLeft.x += 16 * speedScalar[level-1];
		    dirtBikeRWheelBounds.lowerLeft.x += 16 * speedScalar[level-1];
	    } else {
		    obstaclesBounds.lowerLeft.x -= 16 * speedScalar[level-1];
		    obstacles2Bounds.lowerLeft.x -= 16 * speedScalar[level-1];
		    flagBounds.lowerLeft.x -= 16 * speedScalar[level-1];
	    }
	    rotation -= 80;
	}

	public void moveMan() {
		// Bounds checking so car doesn't fly off screen
		if (manBounds.lowerLeft.x >= 0 && (manBounds.lowerLeft.x + manBounds.width <= 1280))
			manBounds.lowerLeft.x += (int) game.getInput().getAccelY() * 2 * speedScalar[speed - 1];
		if (manBounds.lowerLeft.x <= 0)
			manBounds.lowerLeft.x = 0;
		if ((manBounds.lowerLeft.x + manBounds.width) >= 1280)
			manBounds.lowerLeft.x = 1280 - manBounds.width;
		flagBounds.lowerLeft.x -= 16 * speedScalar[level-1];
	}
	
	public void applyGravity(){
		if (version == 0) {
			dirtBikeFrameBounds.lowerLeft.y -= gravity;
			dirtBikeFWheelBounds.lowerLeft.y -= gravity;
			dirtBikeRWheelBounds.lowerLeft.y -= gravity;
		}
		if (version == 1) {
			manBounds.lowerLeft.y -= gravity;
		}
		
	}
	
	public void jump(){
		if (version == 0) {
			dirtBikeFrameBounds.lowerLeft.y += 24;
			dirtBikeFWheelBounds.lowerLeft.y += 24;
			dirtBikeRWheelBounds.lowerLeft.y += 24;
		}
		if (version == 1) {
			manBounds.lowerLeft.y += 24;
		}
	}
	
    // Checks for collision-based loss.
	public boolean collision(Rectangle ball, Rectangle obstacle) {
		float obstacleX = obstacle.lowerLeft.x;
		float obstacleY = obstacle.lowerLeft.y;
		float ballX = ball.lowerLeft.x;
		float ballY = ball.lowerLeft.y;

		if (version == 0){
			if (obstacleY <= ballY + ball.height)
				if (obstacleY + obstacle.height >= ballY)
					if (obstacleX <= ballX + ball.width)
						if (obstacleX + obstacle.width >= ballX)
							return true;
	
			return false;
		}
		if (version == 1) {
			if (ballY == 238) {
				if (obstacleY <= ballY + ball.height)
					if (obstacleY + obstacle.height >= ballY)
						if (obstacleX <= ballX + ball.width)
							if (obstacleX + obstacle.width >= ballX)
								return true;
				return false;
			}
			return true;
		}
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
		if (version == 0) 
		    drawInstruction("GO!");
		if (version == 1)
			drawInstruction("RUN!");
		super.presentRunning();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	@Override
	public void drawRunningBackground() {
		// Draw background.
		batcher.beginBatch(dirtBikeBackground);
	    batcher.drawSprite(backgroundBounds, dirtBikeBackgroundRegion);
		batcher.drawSprite(backgroundBounds2, dirtBikeBackgroundRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningObjects() {
		// dirt bike and gas
		batcher.beginBatch(dirtBikeBackground);
		if (version == 0) {
			batcher.drawSprite(flagBounds, dirtBikeFinishFlagRegion); 
			batcher.drawSprite(dirtBikeFrameBounds, dirtBikeFrameRegion);
			batcher.drawSprite(dirtBikeRWheelBounds.lowerLeft.x,dirtBikeRWheelBounds.lowerLeft.y,dirtBikeRWheelBounds.width,dirtBikeRWheelBounds.height,rotation, dirtBikeWheelRegion);
			batcher.drawSprite(dirtBikeFWheelBounds.lowerLeft.x,dirtBikeFWheelBounds.lowerLeft.y,dirtBikeFWheelBounds.width,dirtBikeFWheelBounds.height,rotation, dirtBikeWheelRegion);
			batcher.drawSprite(gasBounds, dirtBikeGasPedalRegion);
			batcher.drawSprite(jumpBounds, dirtBikeJumpButtonRegion);
			if(levelObstacles[level-1] != 0)
				batcher.drawSprite(obstaclesBounds, dirtBikeObstacleOneRegion);
			if(levelObstacles[level-1] == 2)
				batcher.drawSprite(obstacles2Bounds, dirtBikeObstacleTwoRegion);
		}
		if (version == 1) {
			batcher.drawSprite(trainBounds, trainRegion);
			batcher.drawSprite(trainBounds2, trainRegion);
			batcher.drawSprite(manBounds, dirtBikeManRegion);
		}
		batcher.endBatch();	
	}
	
	@Override
	public void drawRunningBounds() {}
}

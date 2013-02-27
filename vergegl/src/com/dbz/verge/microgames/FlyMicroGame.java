package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;

// TODO: Make fly's movement random.
//		 Generate more flies for higher difficulties.
//		 Splat texture on death
public class FlyMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------

	// Assets
	public static Texture flyBackground;
    public static TextureRegion flyBackgroundRegion;
    public static Texture fly;
    public static TextureRegion flyRegion;
	
	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredSwatCount[] = { 1, 2, 3 };
	private int flySwatCount = 0;

	// Speed variables for fly movement.
	private float speedX = 10; 
	private float speedY = 0;
	
	// Bounds for touch detection.
	private Rectangle flyBounds = new Rectangle(600, 60, 80, 60);
	
	// TODO: Extract boolean setup to Sound class.
	private boolean ambiencePlaying = false;
	
	// -------------------
	// --- Constructor ---
	// -------------------
	
    public FlyMicroGame() { singleTouchEnabled = true; }
    
    @Override
    public void load() {
    	flyBackground = new Texture("flybackground.png");
        flyBackgroundRegion = new TextureRegion(flyBackground, 0, 0, 1280, 800);
        fly = new Texture("fly.png");
        flyRegion = new TextureRegion(fly, 0, 0, 80, 60);
    }
    
	@Override
	public void unload() {
		flyBackground.dispose();
		fly.dispose();
		
	}

	@Override
	public void reload() {
		flyBackground.reload();
		fly.reload();
		
	}
	
	// ---------------------
	// --- Update Method ---
	// ---------------------
    
	@Override
	public void updateRunning(float deltaTime) {
		
		// TODO: Add ambience.
//		if (!ambiencePlaying) {
//			Assets.playSound(Assets.flyBuzzSound);
//			ambiencePlaying = true;
//		}
		
		// Checks for time-based loss.
		if (lostTimeBased(deltaTime)) {
			AssetsManager.playSound(AssetsManager.hitSound);
//			Assets.flyBuzzSound.stop(); // TODO: Use same format as Asset.playSound(), i.e. Assets.stopSound().
			return;
		}
		
		// Moves fly in a pre-defined way.
		moveFly();
		
		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);
	        
	        // Skip handling if the TouchEvent is TOUCH_DRAGGED.
	        if(event.type == TouchEvent.TOUCH_DRAGGED)
	            continue;
	        
	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
        	touchPoint.set(event.x, event.y);
        	// Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
	        // TODO: Make this TOUCH_DRAGGED to make the MicroGame more forgiving. (?)
	        // Fly Bounds (TOUCH_DOWN) Check.
        	if (targetTouchDown(event, touchPoint, flyBounds)) {
        		flySwatCount++;
        		if (flySwatCount == requiredSwatCount[level-1]) {
        			AssetsManager.playSound(AssetsManager.highJumpSound);
//        			Assets.flyBuzzSound.stop(); // TODO: Use same format as Asset.playSound(), i.e. Assets.stopSound().
        			microGameState = MicroGameState.Won;
        		}
        		else if (flySwatCount < requiredSwatCount[level-1])
        			AssetsManager.playSound(AssetsManager.punchSound);
        		return;
        	}
	        
        	// Non-Unique, Super Class Bounds (TOUCH_UP) Check.
	        if (event.type == TouchEvent.TOUCH_UP)
	        	super.updateRunning(touchPoint);
	    }   
	}
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	@Override
	public void reset() {
		super.reset();
		flySwatCount = 0;
		speedX = 10;
		speedY = 0;
		flyBounds.lowerLeft.set(600, 60);
	}
	
	// TODO: Make movement randomized.
	// Moves fly in a predefined manner.
	public void moveFly() {
		float x = flyBounds.lowerLeft.x;
		float y = flyBounds.lowerLeft.y;
		
		if (x >= 1200) {
			speedX = -10.0f;
			speedY = 5.0f;
		}
		else if (y >= 720) {
			speedX = -10.0f;
			speedY = -5.0f;
		}
		else if (x <= 80) {
			speedX = 10.0f;
			speedY = -5.0f;
		}
		else if (y <= 80) {
			speedX = 10.0f;
			speedY = 5.0f;
		}
		
//		if (x >= 1200) {
//			speedX = -10.0f;
//			speedY = 5.0f;
//		} else if (x <= 80) {
//			speedX = 10.0f;
//			speedY = -5.0f;
//		} else if (x >= 580 && x <= 620) {
//			if (speedY > 0)
//				speedY = -5.0f;
//			else 
//				speedY = 5.0f;
//		}
		x += speedX * speedScalar[speed-1];
		y += speedY * speedScalar[speed-1];
		
		flyBounds.lowerLeft.set(x, y);
	}

	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		drawRunningBackground();
		drawRunningObjects();
		// drawRunningBounds();
		drawInstruction("Pet the Fly!");
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawRunningBackground() {
		// Draw the background.
		batcher.beginBatch(flyBackground);
		batcher.drawSprite(0, 0, 1280, 800, flyBackgroundRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningObjects() {
		// Draw the fly.
		batcher.beginBatch(fly);
		batcher.drawSprite(flyBounds, flyRegion);
		batcher.endBatch();
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(AssetsManager.boundOverlay);
	    batcher.drawSprite(flyBounds, AssetsManager.boundOverlayRegion); // Fly Bounding Box
	    batcher.endBatch();
	}
	
}

package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;

// TODO: Add water sprite generation on touch.
// 		 Need water splashing and  fire-dousing sound effects.
//		 Need to update building asset to display as "The Life Family" [Reference to Portal 2]
public class FireMicroGame extends MicroGame {
    
	// --------------
	// --- Fields ---
	// --------------
	
	// Assets
	public static Texture fire;
    public static TextureRegion fireBackgroundRegion;
    public static TextureRegion fireWindowRegion;
    public static TextureRegion clearWindowRegion;
	
	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredWaterCount[] = { 10, 20, 30 };
	private int fireOneCount = 0; 
	private int fireTwoCount = 0;
	private int fireThreeCount = 0;
	
	// Boolean used to track which targets have been touched.
	private boolean clearedFireOne = false;
	private boolean clearedFireTwo = false;
	private boolean clearedFireThree = false;
	
	// Bounds for touch detection.
	private Rectangle fireOneBounds = new Rectangle(220, 280, 180, 260);
	private Rectangle fireTwoBounds = new Rectangle(550, 280, 180, 260);
	private Rectangle fireThreeBounds = new Rectangle(880, 280, 180, 260);
	
	// TODO: Extract boolean setup to Sound class.
	private boolean ambiencePlaying = false;
	
	private float soundCooldown = 0.0f;
	
	// -------------------
	// --- Constructor ---
	// -------------------
	
    public FireMicroGame(Game game) {
        super(game);
        load();
    }
    
    public void load(){
    	fire = new Texture(game, "firehouse.png");
        fireBackgroundRegion = new TextureRegion(fire, 0, 0, 1280, 800);
        fireWindowRegion = new TextureRegion(fire, 1300, 20, 180, 260);
        clearWindowRegion = new TextureRegion(fire, 1500, 20, 180, 260);
    }

	// ---------------------
	// --- Update Method ---
	// ---------------------
    
	@Override
	public void updateRunning(float deltaTime) {
		soundCooldown += deltaTime;
		
		// TODO: Add ambience.
//		if (!ambiencePlaying) {
//			Assets.playSound(Assets.burningSound);
//			ambiencePlaying = true;
//		}
		
		// Checks for time-based loss.
		if (lostTimeBased(deltaTime)) {
			AssetsManager.playSound(AssetsManager.hitSound);
//			Assets.burningSound.stop();	// TODO: Use same format as Asset.playSound(), i.e. Assets.stopSound().
			return;
		}
		
		// Tests for Multi-Area win.
		if (clearedFireOne && clearedFireTwo && clearedFireThree) {
			AssetsManager.playSound(AssetsManager.highJumpSound);
//			Assets.burningSound.stop();	// TODO: Use same format as Asset.playSound(), i.e. Assets.stopSound().
			microGameState = MicroGameState.Won;
    		return;	
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
	        
	        // Fire One Bounds (TOUCH_DOWN/TOUCH_DRAGGED) Check.
	        if (targetTouchDragged(event, touchPoint, fireOneBounds)) {
        		fireOneCount++;
        		if (fireOneCount == requiredWaterCount[level-1])
        			clearedFireOne = true;
        		if (soundCooldown > 0.075f) {
	        		if (fireOneCount <= requiredWaterCount[level-1]) {
	        			AssetsManager.playSound(AssetsManager.splashSound);
	        			soundCooldown = 0;
	        		}
        		}
        		return;
        	}
        	
	        // Fire Two Bounds (TOUCH_DOWN/TOUCH_DRAGGED) Check.
	        if (targetTouchDragged(event, touchPoint, fireTwoBounds)) {
        		fireTwoCount++;
        		if (fireTwoCount == requiredWaterCount[level-1])
        			clearedFireTwo = true;
        		if (soundCooldown > 0.075f) {
	        		if (fireTwoCount <= requiredWaterCount[level-1]) {
	        			AssetsManager.playSound(AssetsManager.splashSound);
	        			soundCooldown = 0;
        			}
        		}
        		return;
        	}
        	
        	// Fire Three Bounds (TOUCH_DOWN/TOUCH_DRAGGED) Check.
        	if (targetTouchDragged(event, touchPoint, fireThreeBounds)) {
        		fireThreeCount++;
        		if (fireThreeCount == requiredWaterCount[level-1])
        			clearedFireThree = true;
        		if (soundCooldown > 0.075f) {
	        		if (fireThreeCount <= requiredWaterCount[level-1]) {
	        			AssetsManager.playSound(AssetsManager.splashSound);
	        			soundCooldown = 0;
	        		}
        		}
        		return;
        	}
	        
        	// Non-Unique, Super Class Bounds (TOUCH_UP) Check.
	        if (event.type == TouchEvent.TOUCH_UP)
	        	super.updateRunning(touchPoint);
	    }   
	}

	// ------------------------------
	// --- Utility Update Method ----
	// ------------------------------
	
	@Override
	public void reset() {
		super.reset();
		fireOneCount = 0;
		fireTwoCount = 0;
		fireThreeCount = 0;
		clearedFireOne = false;
		clearedFireTwo = false;
		clearedFireThree = false;
	}
	
	// -------------------
	// --- Draw Method ---
	// -------------------
	
	@Override
	public void presentRunning() {
		batcher.beginBatch(fire);
		drawRunningBackground();
		drawRunningObjects();
		batcher.endBatch();		
		// drawRunningBounds();
		drawInstruction("Put out the Fire!");
		super.presentRunning();
	}
	
	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------
	
	@Override
	public void drawRunningBackground() {
		batcher.drawSprite(0, 0, 1280, 800, fireBackgroundRegion);
	}
	
	@Override
	public void drawRunningObjects() {
		// Draw target #1.
		if (fireOneCount < requiredWaterCount[level-1]) 
			batcher.drawSprite(fireOneBounds, fireWindowRegion);
		else
			batcher.drawSprite(fireOneBounds,  clearWindowRegion);
		
		// Draw target #2.
		if (fireTwoCount < requiredWaterCount[level-1])
			batcher.drawSprite(fireTwoBounds, fireWindowRegion);
		else
			batcher.drawSprite(fireTwoBounds,  clearWindowRegion);
		
		// Draw target #3.
		if (fireThreeCount < requiredWaterCount[level-1]) 
			batcher.drawSprite(fireThreeBounds, fireWindowRegion);
		else
			batcher.drawSprite(fireThreeBounds, clearWindowRegion);
	}
	
	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(AssetsManager.boundOverlay);
	    batcher.drawSprite(fireOneBounds, AssetsManager.boundOverlayRegion); // fireOne Bounding Box
	    batcher.drawSprite(fireTwoBounds, AssetsManager.boundOverlayRegion); // fireTwo Bounding Box
	    batcher.drawSprite(fireThreeBounds, AssetsManager.boundOverlayRegion); // fireThree Bounding Box
	    batcher.endBatch();
	}
	
}

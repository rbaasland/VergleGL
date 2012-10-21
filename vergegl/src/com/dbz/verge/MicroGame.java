package com.dbz.verge;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.gl.Camera2D;
import com.dbz.framework.gl.FPSCounter;
import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.impl.GLScreen;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;

// Will use this class to implement the extra features of MicroGame
// That aren't shared with the Screen subclass.
// *Might make this inherit off of a GameScreen instance?*
public abstract class MicroGame extends GLScreen {
	
	// --------------
	// --- Fields ---
	// --------------
	
	public enum MicroGameState {
		Ready,
		Paused,
		Running,
		Won,
		Lost,
		// Transition // ***???***
	}
	
	public MicroGameState microGameState;
	
    // OpenGL Related Objects
    public Camera2D guiCam;
    public SpriteBatcher batcher;
    public FPSCounter fpsCounter;
    
    // TouchPoint Vector and Bounding Boxes
    public Vector2 touchPoint;
    public Rectangle readyBounds;
    public Rectangle pauseToggleBounds;
    public Rectangle backArrowBounds;
    
    // *Possible Difficulty Level Implementation.*
    // *Could also try to use a class, struct or enum.*
//    public final static int levelOne = 0;
//    public final static int levelTwo = 1;
//    public final static int levelThree = 2;
    
    // Used to track running time for the game's timer.
    public float totalRunningTime;
    public float totalAllowedTime;
    
    // -------------------
	// --- Constructor ---
    // -------------------
	public MicroGame(Game game) {
		super(game);
        microGameState = MicroGameState.Ready;
        
        guiCam = new Camera2D(glGraphics, 1280, 800);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 1000);
        fpsCounter = new FPSCounter();
        
        readyBounds = new Rectangle(160, 160, 960, 480);
        pauseToggleBounds = new Rectangle(1130, 640, 160, 160);
        backArrowBounds = new Rectangle(0, 0, 150, 150);
 
        totalRunningTime = 0;
        totalAllowedTime = 5.0f;
	}

	// ----------------------
	// --- Update Methods ---
	// ----------------------
	
	@Override
	public void update(float deltaTime) {
	    if(deltaTime > 0.1f)
	        deltaTime = 0.1f;
	    
	    switch(microGameState) {
	    case Ready:
	        updateReady();
	        break;
	    case Running:
	        updateRunning(deltaTime);
	        break;
	    case Paused:
	        updatePaused();
	        break;
	    case Won:
	        updateWon();
	        break;
	    case Lost:
	        updateLost();
	        break;
	    }
	}
	
	public void updateReady() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        if(OverlapTester.pointInRectangle(readyBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            microGameState = MicroGameState.Running;
	            return;     
	        }
	        
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new GameGridScreen(game));
	            return;     
	        }
	    }
	}
		
	public void updatePaused() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;

	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            microGameState = MicroGameState.Running;
	            return;
	        }
	        
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new GameGridScreen(game));
	            return;     
	        }
	    }
	}
	
	public abstract void updateRunning(float deltaTime);
	
	// * Currently only used to test if the game was paused during the run state. *
	// * Later, it may be used to test other non-unique touch events during the run state. *
	public void updateRunning(float deltaTime, Vector2 touchPoint) {
		// Tests to see if the pause toggle was pressed.
		if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            microGameState = MicroGameState.Paused;
	            return;
	    }
	}
	
	// * Note: For now we are going to have non-unique won and lost states for each microgame.
	// However, it would be pretty awesome to have ones that are unique later. *
	
	public void updateWon() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new GameGridScreen(game));
	            return;     
	        }
	    }
	}
	
	public void updateLost() {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new GameGridScreen(game));
	            return;     
	        }
	    }
	}

	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	// Checks for time-based loss.
	public boolean lostTimeBased() {
		if (totalRunningTime > totalAllowedTime) {
			Assets.playSound(Assets.hitSound);
			microGameState = MicroGameState.Lost;
			return true;
		}
		else
			return false;
	}
	
	public boolean targetTouched(TouchEvent event, Vector2 touchPoint, Rectangle targetBounds) {
		// Test for single-touch inside target bounds.
		if (event.type == TouchEvent.TOUCH_DOWN)
	    	if(OverlapTester.pointInRectangle(targetBounds, touchPoint))
		        return true; 
	
		return false;
	}
	
	// --------------------
	// --- Draw Methods ---
	// --------------------
	
	@Override
	public void present(float deltaTime) {
	    GL10 gl = glGraphics.getGL();
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    gl.glEnable(GL10.GL_TEXTURE_2D);
	    
	    guiCam.setViewportAndMatrices();
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    
	    // **We will need to compile all assets into one sprite sheet to support single batch.**
	    // batcher.beginBatch(Assets.items); 
	    switch(microGameState) {
	    case Ready:
	        presentReady();
	        break;
	    case Running:
	        presentRunning();
	        break;
	    case Paused:
	        presentPaused();
	        break;
	    case Won:
	        presentWon();
	        break;
	    case Lost:
	        presentLost();
	        break;
	    }
	    //batcher.endBatch();
	    
	    gl.glDisable(GL10.GL_BLEND);
	    fpsCounter.logFrame();
	}
	
	public void presentReady() {
		// Temporary ready message.
		batcher.beginBatch(Assets.items);
		batcher.drawSprite(600, 500, 192, 32, Assets.ready);
		batcher.endBatch();
		
	    // Back arrow drawn.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
        batcher.endBatch();
	    
	    // Bounding Boxes
//	    batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(160, 160, 960, 480, Assets.boundOverlayRegion); // Ready Bounding Box
//	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentPaused() {
		// Temporary pause message, need rest of menu.
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, "- PAUSED -", 600, 500);
		batcher.endBatch();
		
		// Draw unpause symbol.
		batcher.beginBatch(Assets.pauseToggle);
		batcher.drawSprite(1130, 640, 160, 160, Assets.unpauseRegion);
		batcher.endBatch();
		
	    // Back arrow drawn.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
        batcher.endBatch();
	    
	    // Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.drawSprite(1130, 640, 160, 160, Assets.boundOverlayRegion); // Pause Toggle Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentRunning() {
		// Draw timer.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, String.format("%.2f", totalRunningTime), 600, 100);
		batcher.endBatch();
	    
		// Draw pause symbol.
		batcher.beginBatch(Assets.pauseToggle);
		batcher.drawSprite(1130, 640, 160, 160, Assets.pauseRegion);
		batcher.endBatch();
	    
	    // Bounding Boxes
//		batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(1130, 640, 160, 160, Assets.boundOverlayRegion); // Pause Toggle Bounding Box
//	    batcher.endBatch();
	}
	
	// * Note: For now we are going to have non-unique won and lost states for each microgame.
	// However, it would be pretty awesome to have ones that are unique later. *
	
	public void presentWon() {
		// Temporary win message.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "You Win!", 600, 500);
		batcher.endBatch();
		
		// Back arrow drawn.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
        batcher.endBatch();
		
		// Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentLost() {
		// Temporary lose message.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "You Lose!", 600, 500);
		batcher.endBatch();
		
		// Back arrow drawn.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
        batcher.endBatch();

		// Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	public void drawInstruction(String string) {
		// Could make instruction temporary this way...
//		if (totalRunningTime < 3) {
//			batcher.beginBatch(Assets.items);
//			Assets.font.drawText(batcher, "BROFIST!", 600, 700);
//			batcher.endBatch();
//		}
		// ...or could just dedicate screen space for it for the entire microgame.
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, string, 600, 700);
		batcher.endBatch();
	}
	
	public abstract void drawRunningBounds();
	
	// --------------------------------
	// --- Android State Management ---
	// --------------------------------
	
	@Override
	public void pause() {
		if(microGameState == MicroGameState.Running)
        	microGameState = MicroGameState.Paused;
	}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}
}

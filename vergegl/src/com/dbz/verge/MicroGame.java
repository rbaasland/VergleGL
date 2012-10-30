package com.dbz.verge;

import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.gl.Camera2D;
import com.dbz.framework.gl.FPSCounter;
import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.impl.GLScreen;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;

// *** Need to look into efficient garbage collection with the rapid creation of our MicroGames. ***
public abstract class MicroGame extends GLScreen {
	
	// --------------
	// --- Fields ---
	// --------------
	
	public enum MicroGameState {
		Ready,
		Paused,
		Running,
		Won,
		Lost
	}
	
	public MicroGameState microGameState = MicroGameState.Ready;
	
    // OpenGL Related Objects
    public Camera2D guiCam = new Camera2D(glGraphics, 1280, 800);
    public SpriteBatcher batcher = new SpriteBatcher(glGraphics, 1000);
    public FPSCounter fpsCounter = new FPSCounter(); 
    
    // TouchPoint Vector and Bounding Boxes
    public Vector2 touchPoint = new Vector2();
    public Rectangle readyBounds = new Rectangle(160, 160, 960, 480);
    public Rectangle pauseToggleBounds = new Rectangle(1130, 640, 160, 160);
    public Rectangle backArrowBounds = new Rectangle(0, 0, 150, 150);
    
    // *Possible Difficulty Level Implementation.*
    // *Could also try to use a class, struct or enum.*
    public int level = 1;
    
    // Tracks running time for the game's timer.
    public float totalRunningTime = 0;
    public float totalMicroGameTime = 5.0f;
    
    // Booleans used to enable UI components (Used when launched from GameGrid)
    public boolean pauseEnabled = true;
    public boolean backArrowEnabled = true;
    
    // -------------------
	// --- Constructor ---
    // -------------------
	public MicroGame(Game game) {
		super(game);    
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
	    case Paused:
	        updatePaused();
	        break;
	    case Running:
	        updateRunning(deltaTime);
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
	        
	        if (backArrowEnabled) { 
		        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            game.setScreen(new GameGridMenuScreen(game));
		            return;     
		        }
	        }
	    }
	}
	
	// *** If pause is disabled, we shouldn't be able to get here. ***
	public void updatePaused() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;

	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        if (pauseEnabled) {
		        if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            microGameState = MicroGameState.Running;
		            return;
		        }
	        }
	        
	        if (backArrowEnabled) {
		        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            game.setScreen(new GameGridMenuScreen(game));
		            return;     
		        }
	        }
	    }
	}
	
	public abstract void updateRunning(float deltaTime);
	
	// * Currently only used to test if the game was paused during the run state. *
	// * Later, it may be used to test other non-unique touch events during the run state. *
	public void updateRunning(Vector2 touchPoint) {		
		if(pauseEnabled) {
			// Tests if pause toggle was pressed.
			if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            microGameState = MicroGameState.Paused;
		            return;
		    }
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
	        
	        if (backArrowEnabled) {
		        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            game.setScreen(new GameGridMenuScreen(game));
		            return;     
		        }
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
	        
	        if (backArrowEnabled) {
		        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            game.setScreen(new GameGridMenuScreen(game));
		            return;     
		        }
	        }
	    }
	}

	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	// Checks for time-based loss.
	public boolean lostTimeBased() {
		if (totalRunningTime > totalMicroGameTime) {
			microGameState = MicroGameState.Lost;
			return true;
		}
		else
			return false;
	}
	
	// Checks for time-based win.
	public boolean wonTimeBased() {
		if (totalRunningTime > totalMicroGameTime) {
			microGameState = MicroGameState.Won;
			return true;
		}
		else
			return false;
	}
	//Used for single touch
	public boolean targetTouched(TouchEvent event, Vector2 touchPoint, Rectangle targetBounds) {
		// Test for single-touch inside target bounds.
		if (event.type == TouchEvent.TOUCH_DOWN)
	    	if(OverlapTester.pointInRectangle(targetBounds, touchPoint))
		        return true; 
	
		return false;
	}
	

	/**
	 * Accepts list of TouchEvents and a variable number of rectangles to check for a touch in each rectangle.
	 * @return true if touches occur in all rectangles 
	 */
	//TODO:
	//Making more efficient
			//pass size of list
			//pass the number of rectangles
			//pass boolean array to limit runtime allocation of size(# of rectangles)
	public boolean  targetsMultiTouched(List<TouchEvent> touchEvents, Rectangle ... targetBounds){
		
		int totalScreenTouches = touchEvents.size();
		int numTargets = targetBounds.length;
		int targetTouchCount = 0; //counter for each rectangle touched
		
		//mark rectangles that have been touched.
		boolean[] targetMarked = new boolean[numTargets];
		Arrays.fill(targetMarked, false);
		
		//current touch reference from TouchEvent list
		TouchEvent currTouchEvent;
		int currTouchPointer;
		
		//Iterate though touch events, parse out touchpoint from each touch event
		 for(int i = 0; i < totalScreenTouches; i++) {
			 currTouchEvent =  touchEvents.get(i);
			 currTouchPointer = currTouchEvent.pointer;
			 touchPoint.set(currTouchEvent.x, currTouchEvent.y);
			 guiCam.touchToWorld(touchPoint);
			 
			 //check touchpoint for overlap in each rectangle -- mark each rectangle that has been touched
			 if(game.getInput().isTouchDown(currTouchPointer))//make sure touch is touchDown, not up
				 for (int j = 0; j < numTargets; j++)
					 if(!targetMarked[j] && OverlapTester.pointInRectangle(targetBounds[j], touchPoint)){ 
						 targetMarked[j] = true;
						 targetTouchCount++;
					 }
		 }
		
		 if(targetTouchCount == numTargets)
			 return true;
		 
		 return false;	
	}
	
	public boolean targetDragged(TouchEvent event, Vector2 touchPoint, Rectangle targetBounds) {
		// Test for single-touch inside target bounds.
		if (event.type == TouchEvent.TOUCH_DOWN || event.type == TouchEvent.TOUCH_DRAGGED);
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
	    case Paused:
	        presentPaused();
	        break;
	    case Running:
	        presentRunning();
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
		
		if (backArrowEnabled) {
		    // Draws the back arrow.
	        batcher.beginBatch(Assets.backArrow);
	        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
	        batcher.endBatch();
		}
	    
	    // Bounding Boxes
//	    batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(160, 160, 960, 480, Assets.boundOverlayRegion); // Ready Bounding Box
//	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	// *** If pause is disabled, we shouldn't be able to get here. ***
	public void presentPaused() {
		// Temporary pause message, need rest of menu.
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, "- PAUSED -", 600, 500);
		batcher.endBatch();		
		
		if (pauseEnabled) {
			// Draws the unpause symbol.
			batcher.beginBatch(Assets.pauseToggle);
			batcher.drawSprite(1130, 640, 160, 160, Assets.unpauseRegion);
			batcher.endBatch();
		}
		
	    
		if (backArrowEnabled) {
			// Draws the back arrow.
	        batcher.beginBatch(Assets.backArrow);
	        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
	        batcher.endBatch();
		}
	    
	    // Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.drawSprite(1130, 640, 160, 160, Assets.boundOverlayRegion); // Pause Toggle Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentRunning() {
		// Draw the timer.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, String.format("%.2f", totalMicroGameTime-totalRunningTime), 600, 100);
		batcher.endBatch();
	    
		if (pauseEnabled) {
			// Draws the pause symbol.
			batcher.beginBatch(Assets.pauseToggle);
			batcher.drawSprite(1130, 640, 160, 160, Assets.pauseRegion);
			batcher.endBatch();
		}
	    
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
		
		if (backArrowEnabled) {
			// Draws the back arrow.
	        batcher.beginBatch(Assets.backArrow);
	        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
	        batcher.endBatch();
		}
		
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
		
		if (backArrowEnabled) {
			// Draws the back arrow.
	        batcher.beginBatch(Assets.backArrow);
	        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
	        batcher.endBatch();
		}

		// Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	// *** drawRunningBackground ***
	public abstract void drawBackground();
	
	// *** drawRunningObjects ***
	public abstract void drawObjects();
	
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

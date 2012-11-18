package com.dbz.verge;

import java.util.Arrays;
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
import com.dbz.verge.menus.GameGridMenu;

// TODO: Add unique won/lost states for each MicroGame.
//		 Combine assets into single sprite sheet to allow for single batcher calls.
//		 Implement speed and difficulty level in a standard fashion.
//		 Extract Bounding Boxes draw calls (in each present()) to their own method.	 
//		 ^^^ Note: Doing this in GameScreen as well, should be the same. ^^^ 
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
    public Rectangle pauseToggleBounds = new Rectangle(1140, 660, 140, 140);
    public Rectangle backArrowBounds = new Rectangle(5, 5, 140, 140);
    public Rectangle soundToggleBounds = new Rectangle(1135, 5, 140, 140);
    
    // *Possible Difficulty Level Implementation.*
    // *Could also try to use a class, struct or enum.*
    public int level = 1;
    
    // *Possible Speed Implementation.*
    // *Could also try to use a class, struct or enum.*
    public int speed = 1;
    
    // Tracks running time for the game's timer.
    public float totalRunningTime = 0;
    public float totalMicroGameTime[] = new float[] {5.0f, 4.25f, 3.5f};
    
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
		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);
	        
	        // Skip handling if the TouchEvent isn't TOUCH_UP.
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
	        touchPoint.set(event.x, event.y);
	        // Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
	        // Ready Bounds Check.
	        if(OverlapTester.pointInRectangle(readyBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            microGameState = MicroGameState.Running;
	            return;     
	        }
	        
	        // If Back Arrow is Enabled...
	        if (backArrowEnabled) { 
	        	// ... Back Arrow Bounds Check.
		        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            game.setScreen(new GameGridMenu(game));
		            return;     
		        }
	        }
	        
	        // Sound Toggle Bounds Check.
	        if(OverlapTester.pointInRectangle(soundToggleBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            Settings.soundEnabled = !Settings.soundEnabled;
	            if(Settings.soundEnabled) 
	                Assets.music.play();
	            else
	                Assets.music.pause();
	        }
	    }
	}
	
	// *** If pause is disabled, we shouldn't be able to get here. ***
	// TODO: Handle This Exception.
	//		 Exception: If the Android State Management System calls pause().
	public void updatePaused() {
		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);
	        
	        // Skip handling if the TouchEvent isn't TOUCH_UP.
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
	        touchPoint.set(event.x, event.y);
	        // Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
	        // Pause Toggle Bounds Check.
			if (OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
				Assets.playSound(Assets.clickSound);
				microGameState = MicroGameState.Running;
				return;
			}
	        
	        // If Back Arrow is Enabled...
	        if (backArrowEnabled) { 
	        	// ... Back Arrow Bounds Check.
		        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            game.setScreen(new GameGridMenu(game));
		            return;     
		        }
	        }
	        
	        // Sound Toggle Bounds Check.
	        if(OverlapTester.pointInRectangle(soundToggleBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            Settings.soundEnabled = !Settings.soundEnabled;
	            if(Settings.soundEnabled) 
	                Assets.music.play();
	            else
	                Assets.music.pause();
	        }
	    }
	}
	
	// Overridden in subclasses to handle unique update conditions.
	public abstract void updateRunning(float deltaTime);
	
	// * Currently only used to test if the game was paused during the run state. *
	// * Later, it may be used to test other non-unique touch events during the run state. *
	public void updateRunning(Vector2 touchPoint) {
		// If Pause Toggle is enabled...
		if(pauseEnabled) {
			// ... Pause Toggle Bounds Check.
			if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            microGameState = MicroGameState.Paused;
		            return;
		    }
		}
	}
	
	public void updateWon() {
		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);
	        
	        // Skip handling if the TouchEvent isn't TOUCH_UP.
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
	        touchPoint.set(event.x, event.y);
	        // Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
	        // If Back Arrow is Enabled...
	        if (backArrowEnabled) { 
	        	// ... Back Arrow Bounds Check.
		        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            game.setScreen(new GameGridMenu(game));
		            return;     
		        }
	        }
	    }
	}
	
	public void updateLost() {
		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);
	        
	        // Skip handling if the TouchEvent isn't TOUCH_UP.
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
	        touchPoint.set(event.x, event.y);
	        // Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
	        // If Back Arrow is Enabled...
	        if (backArrowEnabled) { 
	        	// ... Back Arrow Bounds Check.
		        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
		            Assets.playSound(Assets.clickSound);
		            game.setScreen(new GameGridMenu(game));
		            return;     
		        }
	        }
	    }
	}

	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	// Resets MicroGame to avoid reallocating memory.
	// * Each MicroGame subclass needs to override to handle their unique variables. *
	public void reset() {
		totalRunningTime = 0;
	}
	
	// Checks for time-based loss.
	public boolean lostTimeBased(float deltaTime) {
		totalRunningTime += deltaTime;
		
		if (totalRunningTime > totalMicroGameTime[speed-1]) {
			microGameState = MicroGameState.Lost;
			return true;
		}
		else
			return false;
	}
	
	// Checks for time-based win.
	public boolean wonTimeBased(float deltaTime) {
		totalRunningTime += deltaTime;
		
		if (totalRunningTime > totalMicroGameTime[speed-1]) {
			microGameState = MicroGameState.Won;
			return true;
		}
		else
			return false;
	}
	
	// Checks if a TOUCH_UP event is in targetBounds.
	public boolean targetTouchUp(TouchEvent event, Vector2 touchPoint, Rectangle targetBounds) {
		// Test for single-touch inside target bounds.
		if (event.type == TouchEvent.TOUCH_UP)
			if (OverlapTester.pointInRectangle(targetBounds, touchPoint))
				return true;

		return false;
	}
	
	// Checks if a TOUCH_DOWN event is in targetBounds.
	public boolean targetTouchDown(TouchEvent event, Vector2 touchPoint, Rectangle targetBounds) {
		// Test for single-touch inside target bounds.
		if (event.type == TouchEvent.TOUCH_DOWN)
	    	if(OverlapTester.pointInRectangle(targetBounds, touchPoint))
		        return true; 
	
		return false;
	}
	
	// Checks if a TOUCH_DOWN or TOUCH_DRAGGED event is in targetBounds.
	public boolean targetTouchDragged(TouchEvent event, Vector2 touchPoint, Rectangle targetBounds) {
		// Test for single-touch inside target bounds.
		if (event.type == TouchEvent.TOUCH_DOWN || event.type == TouchEvent.TOUCH_DRAGGED);
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
	public boolean targetsMultiTouched(List<TouchEvent> touchEvents, Rectangle ... targetBounds){
		
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

	
	// --------------------
	// --- Draw Methods ---
	// --------------------
	
	@Override
	public void present(float deltaTime) {
	    GL10 gl = glGraphics.getGL();
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    guiCam.setViewportAndMatrices();
	    
	    // Prepares matrix for binding. 
        // (Tells OpenGL to apply the texture to the triangles we render.)
	    gl.glEnable(GL10.GL_TEXTURE_2D);  
	    
	    // Tells OpenGL to apply alpha blending to all triangles rendered until disabled. (pg 341)
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    
	    // TODO:
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
		// Draws Ready Message.
		batcher.beginBatch(Assets.items);
		batcher.drawSprite(600, 500, 192, 32, Assets.ready);
		batcher.endBatch();
		
		if (backArrowEnabled) {
		    // Draws the Back Arrow.
	        batcher.beginBatch(Assets.backArrow);
	        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
	        batcher.endBatch();
		}
		
		// Draws Sound Toggle.
        batcher.beginBatch(Assets.soundToggle);
        batcher.drawSprite(soundToggleBounds, Settings.soundEnabled?Assets.soundOnRegion:Assets.soundOffRegion);
        batcher.endBatch();
	    
	    // Bounding Boxes
//	    batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(readyBounds, Assets.boundOverlayRegion); // Ready Bounding Box
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	// *** If pause is disabled, we shouldn't be able to get here. ***
	// TODO: Handle This Exception.
	//		 Exception: If the Android State Management System calls pause().
	public void presentPaused() {
		// Draws the Pause Message.
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, "- PAUSED -", 600, 500);
		batcher.endBatch();		
		
		// If Pause is enabled...
		if (pauseEnabled) {
			// ... Draws the Unpause Symbol.
			batcher.beginBatch(Assets.pauseToggle);
			batcher.drawSprite(pauseToggleBounds, Assets.unpauseRegion);
			batcher.endBatch();
		}
	    
		// If Back Arrow is enabled...
		if (backArrowEnabled) {
			// ... Draws the back arrow.
	        batcher.beginBatch(Assets.backArrow);
	        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
	        batcher.endBatch();
		}
		
		// Draws Sound Toggle.
        batcher.beginBatch(Assets.soundToggle);
        batcher.drawSprite(soundToggleBounds, Settings.soundEnabled?Assets.soundOnRegion:Assets.soundOffRegion);
        batcher.endBatch();
	    
	    // Bounding Boxes
//		batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.drawSprite(pauseToggleBounds, Assets.boundOverlayRegion); // Pause Toggle Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentRunning() {
		// Draw the Timer.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, String.format("%.2f", totalMicroGameTime[speed-1]-totalRunningTime), 600, 100);
		batcher.endBatch();
	    
		// If Pause is enabled...
		if (pauseEnabled) {
			// ... Draws the Pause symbol.
			batcher.beginBatch(Assets.pauseToggle);
			batcher.drawSprite(pauseToggleBounds, Assets.pauseRegion);
			batcher.endBatch();
		}
	    
	    // Bounding Boxes
//		batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(pauseToggleBounds, Assets.boundOverlayRegion); // Pause Toggle Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentWon() {
		// Draws the Win message.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "You Win!", 600, 500);
		batcher.endBatch();
		
		// If Back Arrow is enabled...
		if (backArrowEnabled) {
			// ... Draws the Back Arrow.
	        batcher.beginBatch(Assets.backArrow);
	        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
	        batcher.endBatch();
		}
		
		// Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentLost() {
		// Draws the Lose message.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "You Lose!", 600, 500);
		batcher.endBatch();
		
		// If Back Arrow is enabled...
		if (backArrowEnabled) {
			// ... Draws the Back Arrow.
			batcher.beginBatch(Assets.backArrow);
			batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
			batcher.endBatch();
		}

		// Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	public abstract void drawRunningBackground();
	
	public abstract void drawRunningObjects();
	
	public abstract void drawRunningBounds();
	
	public void drawInstruction(String string) {
		// Could make instruction temporary this way...
//		if (totalRunningTime < 3) {
//			batcher.beginBatch(Assets.items);
//			Assets.font.drawText(batcher, string, 600, 700);
//			batcher.endBatch();
//		}
		// ...or could just dedicate screen space for it for the entire MicroGame.
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, string, 600, 700);
		batcher.endBatch();
	}
	
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
	
	@Override
	public void onBackPressed(){
		
		switch(microGameState){
		
		//TODO: Add condition where if Ready State, back to previous menu.
		case Ready:
			game.setScreen(new GameGridMenu(game));
		
		case Running:  //cases to pause
			microGameState = MicroGameState.Paused;
			break;

		case Paused:   //cases to resume			
			microGameState = MicroGameState.Running;
			break;

		default:
			break;
		}
		
	}
}

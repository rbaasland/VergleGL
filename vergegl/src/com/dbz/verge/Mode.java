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
import com.dbz.verge.MicroGame.MicroGameState;
import com.dbz.verge.menus.PlayMenu;
import com.dbz.verge.microgames.BroFistMicroGame;
import com.dbz.verge.microgames.FireMicroGame;
import com.dbz.verge.microgames.FlyMicroGame;
import com.dbz.verge.microgames.TrafficMicroGame;
import com.dbz.verge.microgames.CircuitMicroGame;
import com.dbz.verge.microgames.LazerBallMicroGame;

// TODO: Make game speed level affect the transition and MicroGame win/loss state time.
//		 Enable Pausing during the individual MicroGames to resolve issue regarding Android State Managment.
//		 Extract Bounding Boxes draw calls (in each present()) to their own method.
//		 ^^^ Note: Doing this in MicroGame as well, should be the same. ^^^ 
public abstract class Mode extends GLScreen {
	
	// --------------
	// --- Fields ---
	// --------------
	
	public enum ModeState {
		Ready,
		Paused,
		Transition,
		Running,
		Won,
		Lost
	}
	
	public ModeState modeState = ModeState.Ready;
	public ModeState previousModeState = ModeState.Ready;
	
    // OpenGL Related Objects
    public Camera2D guiCam = new Camera2D(glGraphics, 1280, 800);
    public SpriteBatcher batcher  = new SpriteBatcher(glGraphics, 1000);
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
    
    // Tracks transition time.
    public float totalTransitionTime = 0;
    public float transitionTimeLimit = 3.0f;
    
    // Allows time for individual MicroGame wins/loses to show.
    public float totalTimeOver = 0;
    public float timeOverLimit = 2.0f;
    
    // Tracks rounds completed, and level/speed increase rates.
    public int currentRound = 1;
    public int roundsToLevelUp = 6;
    public int roundsToSpeedUp = 3;
    
    // Array of all possible MicroGames.
    // * Initialized in Constructor to avoid possible conflicts with Game variable. *
    public MicroGame microGames[];
    
    // Index for the current MicroGame.
    public int microGameIndex = 0;
    
    // -------------------
	// --- Constructor ---
    // -------------------
	public Mode(Game game) {
		super(game);
		
		// Initialize MicroGame set.
		microGames = new MicroGame[] { new BroFistMicroGame(game), new FlyMicroGame(game), new FireMicroGame(game),
									   new TrafficMicroGame(game), new CircuitMicroGame(game), new LazerBallMicroGame(game) };

		// Disables BackArrow and Pause UI elements for all MicroGames in the set.
//		for (int i = 0; i < microGames.length; i++) {
//			microGames[i].backArrowEnabled = false;
//			microGames[i].pauseEnabled = false;
//		}
	}

	// ----------------------
	// --- Update Methods ---
	// ----------------------
	
	@Override
	public void update(float deltaTime) {
	    if(deltaTime > 0.1f)
	        deltaTime = 0.1f;
	    
	    switch(modeState) {
	    case Ready:
	        updateReady();
	        break;
	    case Paused:
	        updatePaused();
	        break;
	    case Transition:
	    	updateTransition(deltaTime);
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
	            modeState = ModeState.Transition;
	            return;     
	        }
	        
	        // Back Arrow Bounds Check.
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new PlayMenu(game));
	            return;     
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
	        if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            if(previousModeState == ModeState.Transition)  //new logic for paused state changes
	            	modeState = ModeState.Transition;
	            else if(previousModeState == ModeState.Running)
	            		modeState = ModeState.Running;
	            else if(previousModeState == ModeState.Ready)
	            		modeState = ModeState.Transition;
	            return;
	        }
	        
	        // Back Arrow Bounds Check.
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new PlayMenu(game));
	            return;
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

	// Prepares the next MicroGame for launching.
	public void updateTransition(float deltaTime) {
		// Collects total time spent in Transition state.
		totalTransitionTime += deltaTime;

		// After the time limit has past, switch to running state.
		if (totalTransitionTime >= transitionTimeLimit) {
			
			totalTransitionTime = 0;
			modeState = ModeState.Running;
			previousModeState = modeState; //TODO: seems counter intuitive, but it tells game how to handle pause
			setupNextMicroGame();	// TODO: Setup MicroGame for running during transition instead of at the end.
			return;
		}
		
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
	        if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            totalTransitionTime = 0;
	            previousModeState = modeState; //transition
	            modeState = ModeState.Paused;
	            return;
	        }  
	    }
	}
	
	// Launches the MicroGame and waits for it to finish.
	public void updateRunning(float deltaTime) {
		microGames[microGameIndex].update(deltaTime);
		
		if (microGames[microGameIndex].microGameState == MicroGameState.Paused){
			
		}
		
		if (microGames[microGameIndex].microGameState == MicroGameState.Won) {
			totalTimeOver += deltaTime;
			if (totalTimeOver >= timeOverLimit) {
				totalTimeOver = 0;
				updateMicroGameWon(); //TODO Use better technique to solve back button issue See updateLost()/updateWon() in microgame for quick fix.
			}
		}
		else if (microGames[microGameIndex].microGameState == MicroGameState.Lost) {
			totalTimeOver += deltaTime;
			if (totalTimeOver >= timeOverLimit) {
				totalTimeOver = 0;
				updateMicroGameLost(); //TODO Use better technique to solve back button issue See updateLost()/updateWon() in microgame for quick fix.
			}
		}
	}
	
	public abstract void updateMicroGameWon();
	
	public abstract void updateMicroGameLost();
	
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
	        
	        /// Back Arrow Bounds Check.
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new PlayMenu(game));
	            return;     
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
	        
	        /// Back Arrow Bounds Check.
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new PlayMenu(game));
	            return;     
	        }
	    }
	}
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------

	public void setupNextMicroGame() {
		// Increases difficulty level based on rounds completed.
		if ((currentRound-1) % roundsToLevelUp == 0 && currentRound != 1)
			if (level != 3)
				level++;

		// Increases speed level based on rounds completed.
		if ((currentRound-1) % roundsToSpeedUp == 0 && currentRound != 1)
			if (speed != 3)
				speed++;

		// Resets the MicroGame, and sets its state to Running to skip the Ready state.
		microGames[microGameIndex].reset();
		microGames[microGameIndex].microGameState = MicroGameState.Running;
		microGames[microGameIndex].level = level;
		microGames[microGameIndex].speed = speed;
		//here... reference to current screen
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
	    switch(modeState) {
	    case Ready:
	        presentReady();
	        break;
	    case Paused:
	        presentPaused();
	        break;
	    case Transition:
	    	presentTransition();
	    	break;
	    case Running:
	        presentRunning(deltaTime);
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
		batcher.beginBatch(Assets.vergeFont);
		Assets.terminalFont.drawTextCentered(batcher, "Ready?", 640, 500, 1.75f);
		batcher.endBatch();
		
	    // Draws Back Arrow.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
        batcher.endBatch();
        
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
	
	public void presentPaused() {
		// Draws Paused Message.
		batcher.beginBatch(Assets.vergeFont);
		Assets.terminalFont.drawTextCentered(batcher, "- PAUSED -", 640, 500, 1.75f);
		batcher.endBatch();
		
		// Draw unpause symbol.
		batcher.beginBatch(Assets.pauseToggle);
		batcher.drawSprite(pauseToggleBounds, Assets.unpauseRegion);
		batcher.endBatch();
		
	    // Draws Back Arrow.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
        batcher.endBatch();
        
        // Draws Sound Toggle.
        batcher.beginBatch(Assets.soundToggle);
        batcher.drawSprite(soundToggleBounds, Settings.soundEnabled?Assets.soundOnRegion:Assets.soundOffRegion);
        batcher.endBatch();
	    
	    // Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.drawSprite(pauseToggleBounds, Assets.boundOverlayRegion); // Pause Toggle Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentTransition() {
		// Draws background.
//		batcher.beginBatch(Assets.broFistBackground);
//		batcher.drawSprite(0, 0, 1280, 800, Assets.broFistBackgroundRegion);
//		batcher.endBatch();
		
		// Draws the mid game status report.
		presentStatusReport();
		
		// Draws the pause symbol.
		batcher.beginBatch(Assets.pauseToggle);
		batcher.drawSprite(pauseToggleBounds, Assets.pauseRegion);
		batcher.endBatch();
	}
	
	public void presentRunning(float deltaTime) {
		microGames[microGameIndex].present(deltaTime);
	}
	
	public void presentWon() {
		// Draws the win message.
		batcher.beginBatch(Assets.vergeFont);
		Assets.terminalFont.drawTextCentered(batcher, "A Winner is You!", 640, 500, 1.5f);
		batcher.endBatch();
		
		// Draws the end game status report.
		presentStatusReport();
		
		// Draws the back arrow.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
        batcher.endBatch();
		
		// Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentLost() {
		// Draws the lose message.	
		batcher.beginBatch(Assets.vergeFont);
		Assets.terminalFont.drawTextCentered(batcher, "You Lost The Game!", 640, 500, 1.5f);
		batcher.endBatch();
		
		// Draws the end game status report.
		presentStatusReport();
		
		// Draws the back arrow.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
        batcher.endBatch();

		// Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	// TODO: Call shared lines via super.presentStatusReport() (???)
	public abstract void presentStatusReport();
	
	// --------------------------------
	// --- Android State Management ---
	// --------------------------------
	
	@Override
	public void pause() {
		if(modeState == ModeState.Running)
        	modeState = ModeState.Paused;
	}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}
	
	@Override
	public void onBackPressed(){
		
		switch (modeState){


		case Transition: //cases to pause
			modeState = ModeState.Paused;
			break;
		case Running:
			modeState = ModeState.Paused;
			break;

		//The if-else-if here needed because the microgames are screens w/in a screen
			//to ensure the game resumes at the correct point, we add 2 sub-cases 
			//in respect to previous gameState

		case Paused:   //cases to resume
			if(previousModeState == ModeState.Running)
				modeState = ModeState.Running;

			else if(previousModeState == ModeState.Transition || previousModeState == ModeState.Ready)
					modeState = ModeState.Transition;
			break;

		case Ready:
		case Won:
		case Lost:
			game.setScreen(new PlayMenu(game));
			break;

		default:
			break;
			
		}
		
	}
}

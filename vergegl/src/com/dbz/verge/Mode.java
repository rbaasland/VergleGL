package com.dbz.verge;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.dbz.framework.BluetoothManager;
import com.dbz.framework.BluetoothManager.ControlThread;
import com.dbz.framework.gl.Camera2D;
import com.dbz.framework.gl.FPSCounter;
import com.dbz.framework.gl.Screen;
import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.MicroGame.MicroGameState;
import com.dbz.verge.menus.GameGridMenu;
import com.dbz.verge.menus.HelpMenu;
import com.dbz.verge.menus.MainMenu;
import com.dbz.verge.menus.SinglePlayerMenu;
import com.dbz.verge.microgames.BroFistMicroGame;
import com.dbz.verge.microgames.FireMicroGame;
import com.dbz.verge.microgames.FlyMicroGame;
import com.dbz.verge.microgames.TossMicroGame;
import com.dbz.verge.microgames.InvasionMicroGame;
import com.dbz.verge.microgames.CircuitMicroGame;
import com.dbz.verge.microgames.LazerBallMicroGame;
import com.dbz.verge.modes.SurvivalMode;

// TODO: Make game speed level affect the transition and MicroGame win/loss state time.
//		 Extract Bounding Boxes draw calls (in each present()) to their own method.
//		 ^^^ Note: Doing this in MicroGame as well, should be the same. ^^^ 
public abstract class Mode extends Screen {
	
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
	
	public static boolean modeActive = false; // Used to determine if mode is running (see MicroGame.java)
	
	public static ModeState modeState = ModeState.Transition; //static so microgame can change ModeState
	public ModeState previousModeState = ModeState.Transition;
	
    // OpenGL Related Objects
    public Camera2D guiCam = new Camera2D(glGraphics, 854, 480);
    public SpriteBatcher batcher  = new SpriteBatcher(glGraphics, 1000);
    public FPSCounter fpsCounter = new FPSCounter();
    
    // TouchPoint Vector and Bounding Boxes
    public Vector2 touchPoint = new Vector2();
    public Rectangle readyBounds = new Rectangle(100, 0, 650, 480);
    public Rectangle pauseToggleBounds = new Rectangle(760, 395, 93, 85);
    public Rectangle backArrowBounds = new Rectangle(3, 3, 93, 85);
    public Rectangle soundToggleBounds = new Rectangle(757, 3, 93, 85);
    public Rectangle helpBounds = new Rectangle(3, 395, 93, 85);
    
    // 'Meter' Window Bar Percentages.
    public float cmpBar = 0.0f;		// Determined by speed.
    public float memBar = 0.0f;		// Determined by level.
    public float netBar = 0.0f;		// Determined by currentRound.
    public float tmpBar = 0.0f;		// Determined all three above (%33.3 percent each)
    
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
    public int totalRounds = 10;	// ***Currently unused, except for NET meter percentage.***
    
    // Array of all possible MicroGames.
    // * Initialized in Constructor to avoid possible conflicts with Game variable. *
    public MicroGame microGames[];
    
    // Index for the current MicroGame.
    public int microGameIndex = 0;
    
    public boolean loadComplete = false;
    
    public static boolean isMultiplayer = false;
    
	public BluetoothManager bluetoothManager;
    
    // -------------------
	// --- Constructor ---
    // -------------------
	public Mode() {
		
		modeActive = true;
		modeState = ModeState.Transition; //make sure static var is set correctly
		
		// Initialize MicroGame set.
		microGames = new MicroGame[] { new BroFistMicroGame(), new FlyMicroGame(), new FireMicroGame(),
										   new InvasionMicroGame(), new CircuitMicroGame(), new LazerBallMicroGame(),
										   new TossMicroGame() };
		updateMeterBarPercentages();
		
		if (Mode.isMultiplayer) {
			bluetoothManager = new BluetoothManager();
			bluetoothManager.startThreads();
		}

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
	            AssetsManager.playSound(AssetsManager.clickSound);
	            modeState = ModeState.Transition;
	            return;     
	        }
	        
	        // Back Arrow Bounds Check.
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            AssetsManager.playSound(AssetsManager.clickSound);
	            if (Mode.isMultiplayer){
	            	game.setScreen(new MainMenu());
	            	bluetoothManager.endThreads(); //end discovery, stop all threads
	            } else game.setScreen(new SinglePlayerMenu());
	            return;     
	        }
	        
	        // Sound Toggle Bounds Check.
	        if(OverlapTester.pointInRectangle(soundToggleBounds, touchPoint)) {
	            AssetsManager.playSound(AssetsManager.clickSound);
	            Settings.soundEnabled = !Settings.soundEnabled;
	            if(Settings.soundEnabled) 
	                AssetsManager.music.play();
	            else
	                AssetsManager.music.pause();
	        }
	        
	        // Help Bounds Check.
	        if (OverlapTester.pointInRectangle(helpBounds, touchPoint)) {
	            AssetsManager.playSound(AssetsManager.clickSound);
	            game.setScreen(new HelpMenu());
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
	            AssetsManager.playSound(AssetsManager.clickSound);
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
	            AssetsManager.playSound(AssetsManager.clickSound);
	            if (Mode.isMultiplayer) {
	            	bluetoothManager.endThreads();
	            	game.setScreen(new MainMenu());
	            }
	            else game.setScreen(new SinglePlayerMenu());
	            return;
	        }
	        
	        // Sound Toggle Bounds Check.
	        if(OverlapTester.pointInRectangle(soundToggleBounds, touchPoint)) {
	            AssetsManager.playSound(AssetsManager.clickSound);
	            Settings.soundEnabled = !Settings.soundEnabled;
	            if(Settings.soundEnabled) 
	                AssetsManager.music.play();
	            else
	                AssetsManager.music.pause();
	        }
	        
	        // Help Bounds Check.
	        if (OverlapTester.pointInRectangle(helpBounds, touchPoint)) {
	            AssetsManager.playSound(AssetsManager.clickSound);
	            game.setScreen(new HelpMenu());
	        }
	    }
	}

	// Prepares the next MicroGame for launching.
	public void updateTransition(float deltaTime) {
		// Collects total time spent in Transition state.
		totalTransitionTime += deltaTime;

		if (!loadComplete)
			loadNextMicroGame();
		// After the time limit has past and load has completed, switch to running state.
		else if (totalTransitionTime >= transitionTimeLimit) {
			totalTransitionTime = 0;
			modeState = ModeState.Running;
			previousModeState = modeState; // TODO: seems counter intuitive, but it tells game how to handle pause
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
	            AssetsManager.playSound(AssetsManager.clickSound);
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
		
		if (microGames[microGameIndex].microGameState == MicroGameState.Paused) {}
		
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
	
	public void updateMicroGameWon() {
		loadComplete = false;
		currentRound++;
	}
	
	public void updateMicroGameLost() {
		loadComplete = false;
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
	        
	        // Back Arrow Bounds Check.
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            AssetsManager.playSound(AssetsManager.clickSound);
	            if (Mode.isMultiplayer){
	            	game.setScreen(new MainMenu());
	            	bluetoothManager.endThreads(); //end discovery, stop all threads
	            }
	            else game.setScreen(new SinglePlayerMenu());
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
	        
	        // Back Arrow Bounds Check.
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            AssetsManager.playSound(AssetsManager.clickSound);
	            if (Mode.isMultiplayer){
	            	game.setScreen(new MainMenu());
            		bluetoothManager.endThreads(); //end discovery, stop all threads
	            }
	            else game.setScreen(new SinglePlayerMenu());
	            return;
	        }
	    }
	}
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------

	public void loadNextMicroGame() {
		// Increases difficulty level based on rounds completed.
		if ((currentRound-1) % roundsToLevelUp == 0 && currentRound != 1)
			if (level != 3)
				level++;

		// Increases speed level based on rounds completed.
		if ((currentRound-1) % roundsToSpeedUp == 0 && currentRound != 1)
			if (speed != 3)
				speed++;
		
		// Resets the MicroGame, and sets its state to Running to skip the Ready state.
		AssetsManager.loadMicrogame(microGames[microGameIndex]); //unloads previous mg, loads current mg
		microGames[microGameIndex].level = level;
		microGames[microGameIndex].speed = speed;
		microGames[microGameIndex].reset();
		microGames[microGameIndex].microGameState = MicroGameState.Running;
		//here... reference to current screen
		
		loadComplete = true;
		updateMeterBarPercentages();

	}

	public void updateMeterBarPercentages() {
		// Update 'Meters' bar percentages.
		cmpBar = ((float)speed) / 3.0f;
		memBar = ((float)level) / 3.0f;
		netBar = ((float)currentRound) / totalRounds;
		if (netBar > 1.0f)
			netBar = 1.0f;
		tmpBar = (cmpBar * 0.3333f) + (memBar * 0.3333f) + (netBar * 0.3333f);
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
		drawWindowContent();
		
		// Draws 'Meters' window text and Ready Message. 
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		drawWindowText();
		AssetsManager.vergeFont.drawTextLeft(batcher, "Ready?", 315, 170);
		batcher.endBatch();
		
	    // Draws Back Arrow.
        batcher.beginBatch(AssetsManager.backArrow);
        batcher.drawSprite(backArrowBounds, AssetsManager.backArrowRegion);
        batcher.endBatch();
        
    	// Draws Help Button.
    	batcher.beginBatch(AssetsManager.background);
    	batcher.drawSprite(helpBounds, AssetsManager.helpMenuButtonRegion);
    	batcher.endBatch();

        // Draws Sound Toggle.
        batcher.beginBatch(AssetsManager.soundToggle);
        batcher.drawSprite(soundToggleBounds, Settings.soundEnabled?AssetsManager.soundOnRegion:AssetsManager.soundOffRegion);
        batcher.endBatch();
	    
	    // Bounding Boxes
//	    batcher.beginBatch(AssetsManager.boundOverlay);
//	    batcher.drawSprite(readyBounds, AssetsManager.boundOverlayRegion); // Ready Bounding Box
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentPaused() {
		drawWindowContent();

		// Draws 'Meters' window text and Paused Message. 
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		drawWindowText();
		AssetsManager.vergeFont.drawTextLeft(batcher, "- PAUSED -", 315, 170);
		batcher.endBatch();
		
		// Draw unpause symbol.
		batcher.beginBatch(AssetsManager.pauseToggle);
		batcher.drawSprite(pauseToggleBounds, AssetsManager.unpauseRegion);
		batcher.endBatch();
		
	    // Draws Back Arrow.
        batcher.beginBatch(AssetsManager.backArrow);
        batcher.drawSprite(backArrowBounds, AssetsManager.backArrowRegion);
        batcher.endBatch();
        
    	// Draws Help Button.
    	batcher.beginBatch(AssetsManager.background);
    	batcher.drawSprite(helpBounds, AssetsManager.helpMenuButtonRegion);
    	batcher.endBatch();
        
        // Draws Sound Toggle.
        batcher.beginBatch(AssetsManager.soundToggle);
        batcher.drawSprite(soundToggleBounds, Settings.soundEnabled?AssetsManager.soundOnRegion:AssetsManager.soundOffRegion);
        batcher.endBatch();
	    
	    // Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.drawSprite(pauseToggleBounds, Assets.boundOverlayRegion); // Pause Toggle Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentTransition() {
		drawWindowContent();
		
		// Draws 'Meters' window text. 
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		drawWindowText();
		batcher.endBatch();
		
		// Draws the mid game status report.
		presentStatusReport(170);
		
		// Draw bluetooth connection status
		if(Mode.isMultiplayer)
			drawConnectionStatus(20);
		
		// Draws the pause symbol.
//		batcher.beginBatch(AssetsManager.pauseToggle);
//		batcher.drawSprite(pauseToggleBounds, AssetsManager.pauseRegion);
//		batcher.endBatch();
	}
	
	public void presentRunning(float deltaTime) {
		microGames[microGameIndex].present(deltaTime);
	}
	
	public void presentWon() {
		drawWindowContent();
		
		// Draws 'Meters' window text and Win Message.
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		drawWindowText();
		AssetsManager.vergeFont.drawTextLeft(batcher, "A Winner is You!", 315, 170);
		batcher.endBatch();
		
		// Draws the end game status report.
		presentStatusReport(140);
		
		// Draws the back arrow.
        batcher.beginBatch(AssetsManager.backArrow);
        batcher.drawSprite(backArrowBounds, AssetsManager.backArrowRegion);
        batcher.endBatch();
		
		// Bounding Boxes
//      batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//	    batcher.endBatch();
	}
	
	public void presentLost() {
		drawWindowContent();
		
		// Draws 'Meters' window text and Lose Message. 
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		drawWindowText();
		AssetsManager.vergeFont.drawTextLeft(batcher, "You Lost The Game!", 315, 170);
		batcher.endBatch();
		
		// Draws the end game status report.
		presentStatusReport(140);
		
		// Draws the back arrow.
        batcher.beginBatch(AssetsManager.backArrow);
        batcher.drawSprite(backArrowBounds, AssetsManager.backArrowRegion);
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
	public abstract void presentStatusReport(int startY);
	
	public void drawWindowContent() {
		// Draws background and 'Meters' window content.
		batcher.beginBatch(AssetsManager.transition);
		batcher.drawSprite(0, 0, 854, 480, AssetsManager.transitionBackgroundRegion);

		if (cmpBar <= 0.66) {
			batcher.drawSprite(28, 174, 239, 26, AssetsManager.meterGreenBarEmptyRegion);			// CMP Bar. (1st)
			batcher.drawSprite(28, 174, (239 * cmpBar), 26, AssetsManager.meterGreenBarFillRegion);
		} else if (cmpBar <= 0.85) {
			batcher.drawSprite(28, 174, 239, 26, AssetsManager.meterYellowBarEmptyRegion);	
			batcher.drawSprite(28, 174, (239 * cmpBar), 26, AssetsManager.meterYellowBarFillRegion);
		} else {
			batcher.drawSprite(28, 174, 239, 26, AssetsManager.meterRedBarEmptyRegion);			
			batcher.drawSprite(28, 174, (239 * cmpBar), 26, AssetsManager.meterRedBarFillRegion);
		}
		batcher.drawSprite(22, 172, 250, 31, AssetsManager.meterBarOutlineRegion);
		
		if (memBar <= 0.66) {
			batcher.drawSprite(28, 124, 239, 26, AssetsManager.meterGreenBarEmptyRegion);			// MEM Bar. (2nd)
			batcher.drawSprite(28, 124, (239 * memBar), 26, AssetsManager.meterGreenBarFillRegion);
		} else if (memBar <= 0.85) {
			batcher.drawSprite(28, 124, 239, 26, AssetsManager.meterYellowBarEmptyRegion);			
			batcher.drawSprite(28, 124, (239 * memBar), 26, AssetsManager.meterYellowBarFillRegion);
		} else {
			batcher.drawSprite(28, 124, 239, 26, AssetsManager.meterRedBarEmptyRegion);			
			batcher.drawSprite(28, 124, (239 * memBar), 26, AssetsManager.meterRedBarFillRegion);
		}
		batcher.drawSprite(22, 122, 250, 31, AssetsManager.meterBarOutlineRegion);
		
		if (netBar <= 0.66) {
			batcher.drawSprite(28, 74, 239, 26, AssetsManager.meterGreenBarEmptyRegion);				// NET Bar. (3rd)
			batcher.drawSprite(28, 74, (239 * netBar), 26, AssetsManager.meterGreenBarFillRegion); 	
		} else if (netBar <= 0.85) {
			batcher.drawSprite(28, 74, 239, 26, AssetsManager.meterYellowBarEmptyRegion);
			batcher.drawSprite(28, 74, (239 * netBar), 26, AssetsManager.meterYellowBarFillRegion); 	
		} else {
			batcher.drawSprite(28, 74, 239, 26, AssetsManager.meterRedBarEmptyRegion);				
			batcher.drawSprite(28, 74, (239 * netBar), 26, AssetsManager.meterRedBarFillRegion); 	
		}
		batcher.drawSprite(22, 72, 250, 31, AssetsManager.meterBarOutlineRegion);
		
		if (tmpBar <= 0.66) {
			batcher.drawSprite(28, 24, 239, 26, AssetsManager.meterGreenBarEmptyRegion);				// TMP Bar. (4th)
			batcher.drawSprite(28, 24, (239 * tmpBar), 26, AssetsManager.meterGreenBarFillRegion); 	
		} else if (tmpBar <= 0.85) {
			batcher.drawSprite(28, 24, 239, 26, AssetsManager.meterYellowBarEmptyRegion);			
			batcher.drawSprite(28, 24, (239 * tmpBar), 26, AssetsManager.meterYellowBarFillRegion); 	
		} else {
			batcher.drawSprite(28, 24, 239, 26, AssetsManager.meterRedBarEmptyRegion);			
			batcher.drawSprite(28, 24, (239 * tmpBar), 26, AssetsManager.meterRedBarFillRegion); 	
		}
		batcher.drawSprite(22, 22, 250, 31, AssetsManager.meterBarOutlineRegion);
		// batcher.endBatch();
		
		// Draws MicroGame Indicators.
		if (microGames[microGameIndex].singleTouchEnabled && loadComplete)
			batcher.drawSprite(607, 363, 85, 85, AssetsManager.singleTouchOnIndicatorRegion);	// Originally 612, 368, 75, 75
		else
			batcher.drawSprite(607, 363, 85, 85, AssetsManager.singleTouchOffIndicatorRegion);

		if (microGames[microGameIndex].multiTouchEnabled && loadComplete)
			batcher.drawSprite(720, 363, 85, 85, AssetsManager.multiTouchOnIndicatorRegion);
		else
			batcher.drawSprite(720, 363, 85, 85, AssetsManager.multiTouchOffIndicatorRegion);

		if (microGames[microGameIndex].accelerometerEnabled && loadComplete)
			batcher.drawSprite(607, 262, 85, 85, AssetsManager.accelerometerOnIndicatorRegion);
		else
			batcher.drawSprite(607, 262, 85, 85, AssetsManager.accelerometerOffIndicatorRegion);
			
		if (microGames[microGameIndex].gesturesEnabled && loadComplete)
			batcher.drawSprite(720, 262, 85, 85, AssetsManager.gesturesOnIndicatorRegion);
		else
			batcher.drawSprite(720, 262, 85, 85, AssetsManager.gesturesOffIndicatorRegion);

		batcher.endBatch();
	}
	
	//connection status variables
	String progressDots = "";
	int delayCounter = 0;
	int delayCounterMax = 10; 
	
	/** Used to display bluetooth connection status to screen*/
	public void drawConnectionStatus(int startY) {
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		
		//if searching or connecting, append progress string
		if(BluetoothManager.connectionStatus.equals("Searching") || BluetoothManager.connectionStatus.equals("Connecting")){
			if(delayCounter++ == delayCounterMax){ //dot is drawn every 10 frames
				progressDots+= ".";
				delayCounter = 0;
				if(progressDots.length() == 4)
					progressDots = "";
			}
			AssetsManager.vergeFont.drawTextLeft(batcher, BluetoothManager.connectionStatus + progressDots, 315, startY);	
		} else AssetsManager.vergeFont.drawTextLeft(batcher, BluetoothManager.connectionStatus, 315, startY); //else no dots

		batcher.endBatch();
	}
	
	public void drawWindowText() {
		// Draws 'Meters' window text.
		AssetsManager.vergeFont.drawTextLeft(batcher, "CMP", 20, 172);
		AssetsManager.vergeFont.drawTextLeft(batcher, "MEM", 20, 122);
		AssetsManager.vergeFont.drawTextLeft(batcher, "NET", 20, 72);
		AssetsManager.vergeFont.drawTextLeft(batcher, "TMP", 20, 22);
	}
	
	// --------------------------------
	// --- Android State Management ---
	// --------------------------------
	
	@Override
	public void pause() {
		if(modeState == ModeState.Running)
        	modeState = ModeState.Paused;
	}

	@Override
	public void resume() {
		modeActive = true;
	}

	@Override
	public void dispose() {
		modeActive = false;
	}
	
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
			if (Mode.isMultiplayer) {
				game.setScreen(new MainMenu());
				bluetoothManager.endThreads(); //end discovery, stop all threads
			} else 
				game.setScreen(new SinglePlayerMenu());
			break;

		default:
			break;
			
		}
		
	}
}

package com.dbz.verge;

import java.util.List;
import java.util.Random;

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
import com.dbz.verge.microgames.BroFistMicroGame;
import com.dbz.verge.microgames.FireMicroGame;
import com.dbz.verge.microgames.FlyMicroGame;
import com.dbz.verge.microgames.TrafficMicroGame;
import com.dbz.verge.microgames.CircuitMicroGame;
import com.dbz.verge.microgames.LazerBallMicroGame;

// TODO: Make class abstract, and extract necessary code to SurvivalGameScreen.
// 		 Implement speed increase after every 5 games.
//		 Implement difficulty increase every 10 games.
public class GameScreen extends GLScreen {
	
	// --------------
	// --- Fields ---
	// --------------
	
	public enum GameState {
		Ready,
		Paused,
		Transition,
		Running,
		Won,
		Lost
	}
	
	public GameState gameState = GameState.Ready;
	
    // OpenGL Related Objects
    public Camera2D guiCam = new Camera2D(glGraphics, 1280, 800);
    public SpriteBatcher batcher  = new SpriteBatcher(glGraphics, 1000);
    public FPSCounter fpsCounter = new FPSCounter();
    
    // TouchPoint Vector and Bounding Boxes
    public Vector2 touchPoint = new Vector2();
    public Rectangle readyBounds = new Rectangle(160, 160, 960, 480);
    public Rectangle pauseToggleBounds = new Rectangle(1130, 640, 160, 160);
    public Rectangle backArrowBounds = new Rectangle(0, 0, 150, 150);
    
    // *Possible Difficulty Level Implementation.*
    // *Could also try to use a class, struct or enum.*
    public int level = 1;
    
    // Tracks transition time.
    public float totalTransitionTime = 0;
    public float transitionTimeLimit = 2.0f;
    
    // Allows time for individual MicroGame wins/loses to show.
    public float totalTimeOver = 0;
    public float timeOverLimit = 2.0f;
    
    // Tracks win and loss conditions for game mode.
    public int winCount = 0;
    public int requiredWins = 9;
    public int lives = 3;
    
    // Array of all possible MicroGames.
    // * Initialized in Constructor to avoid possible conflicts with Game variable. *
    public MicroGame microGames[];
    
    // Index for the current MicroGame.
    public int microGameIndex = 0;
    
    // Random number generator used for randomizing games.
    public Random random = new Random();
    
    // Keeps track of randomized indexes.
    public int indexHistory[];
    
    // -------------------
	// --- Constructor ---
    // -------------------
	public GameScreen(Game game) {
		super(game);
		
		// Initialize MicroGame set.
		microGames = new MicroGame[] { new BroFistMicroGame(game), new FlyMicroGame(game), new FireMicroGame(game),
									   new TrafficMicroGame(game), new CircuitMicroGame(game), new LazerBallMicroGame(game) };
		indexHistory = new int[microGames.length];
		clearIndexHistory();
		// Disables BackArrow and Pause UI elements for all MicroGames in the set.
		for (int i = 0; i < microGames.length; i++) {
			microGames[i].backArrowEnabled = false;
			microGames[i].pauseEnabled = false;
		}
	}

	// ----------------------
	// --- Update Methods ---
	// ----------------------
	
	@Override
	public void update(float deltaTime) {
	    if(deltaTime > 0.1f)
	        deltaTime = 0.1f;
	    
	    switch(gameState) {
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
	            gameState = GameState.Transition;
	            return;     
	        }
	        
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new PlayMenuScreen(game));
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
	            gameState = GameState.Transition;
	            return;
	        }
	        
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new PlayMenuScreen(game));
	            return;
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
			gameState = GameState.Running;
			setupNextMicroGame();
			return;
		}
		
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;

	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        // Tests if pause toggle was pressed.
	        if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            totalTransitionTime = 0;
	            gameState = GameState.Paused;
	            return;
	        }
	        
	    }
	}
	
	// Launches the MicroGame and waits for it to finish.
	public void updateRunning(float deltaTime) {
		microGames[microGameIndex].update(deltaTime);
		
		if (microGames[microGameIndex].microGameState == MicroGameState.Won) {
			totalTimeOver += deltaTime;
			if (totalTimeOver >= timeOverLimit) {
				totalTimeOver = 0;
				winCount++;
				if (winCount >= requiredWins) {
					gameState = GameState.Won;
					return;
				}
				else {
					gameState = GameState.Transition;
					return;
				}
			}
		}
		else if (microGames[microGameIndex].microGameState == MicroGameState.Lost) {
			totalTimeOver += deltaTime;
			if (totalTimeOver >= timeOverLimit) {
				totalTimeOver = 0;
				lives--;
				if (lives <= 0) {
					gameState = GameState.Lost;
					return;
				}
				else {
					gameState = GameState.Transition;
					return;
				}
			}

		}
	}
	
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
	            game.setScreen(new PlayMenuScreen(game));
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
	            game.setScreen(new PlayMenuScreen(game));
	            return;     
	        }
	    }
	}
	
	// -----------------------------
	// --- Utility Update Method ---
	// -----------------------------

	public void setupNextMicroGame() {
		// Checks the indexHistory for fullness
		if (!checkIndex(-1))
			clearIndexHistory();

		// Randomizes the microGameIndex (Dependent)
		do
		{
			microGameIndex = random.nextInt(microGames.length);
		} while(checkIndex(microGameIndex));
		
		// Resets the MicroGame, and sets it's state to Running to skip the Ready state.
		microGames[microGameIndex].reset();
		microGames[microGameIndex].microGameState = MicroGameState.Running;
	}
	
	
	//Returns false if the index wasn't found in the array
	public boolean checkIndex(int index)
	{
		for(int i = 0; i < indexHistory.length; i++)
		{
			if(indexHistory[i] == index)
				return true;
			else if (indexHistory[i] == -1) {
				indexHistory[i] = microGameIndex;
				break;
			}
		}
		return false;
	}
	
	// Clears indexHistory
	public void clearIndexHistory()
	{
		for (int i = 0; i < indexHistory.length; i++)
			indexHistory[i] = -1;
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
	    switch(gameState) {
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
	
	public void presentTransition() {
		// Draws background.
		batcher.beginBatch(Assets.broFistBackground);
		batcher.drawSprite(0, 0, 1280, 800, Assets.broFistBackgroundRegion);
		batcher.endBatch();
		
		// Draws the pause symbol.
		batcher.beginBatch(Assets.pauseToggle);
		batcher.drawSprite(1130, 640, 160, 160, Assets.pauseRegion);
		batcher.endBatch();
	}
	
	public void presentRunning(float deltaTime) {
		microGames[microGameIndex].present(deltaTime);
	}
	
	public void presentWon() {
		// Temporary win message.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "You Win The Game!", 600, 500);
		batcher.endBatch();
		
		// Draws the back arrow.
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
	    Assets.font.drawText(batcher, "You Lost The Game!", 600, 500);
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
	
	// *** drawRunningBackground ***
	public void drawBackground() {}
	
	// *** drawRunningObjects ***
	public void drawObjects() {}
	
	// *** Implement to draw an icon of the type of MicroGame that is coming up. ***
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
	
	public void drawRunningBounds() {}
	
	// --------------------------------
	// --- Android State Management ---
	// --------------------------------
	
	@Override
	public void pause() {
		if(gameState == GameState.Running)
        	gameState = GameState.Paused;
	}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}
}

package com.dbz.verge;

import java.util.List;
import java.util.Random;

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
//		 Extract Bounding Boxes draw calls (in each present()) to their own method.
//		 ^^^ Note: Doing this in MicroGame as well, should be the same. ^^^ 
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
    
    // *Possible Speed Implementation.*
    // *Could also try to use a class, struct or enum.*
    public int speed = 1;
    
    // Tracks transition time.
    public float totalTransitionTime = 0;
    public float transitionTimeLimit = 2.0f;
    
    // Allows time for individual MicroGame wins/loses to show.
    public float totalTimeOver = 0;
    public float timeOverLimit = 2.0f;
    
    // Tracks win and loss conditions for game mode.
    public int winCount = 0;
    public int requiredWins = 12;
    public int lives = 3;
    
    // Tracks rounds completed, and level/speed increase rates.
    public int currentRound = 1;
    public int roundsToLevel = 6;
    public int roundsToSpeed = 3;
    
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
				currentRound++;
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
				currentRound++;
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
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------

	public void setupNextMicroGame() {
		// Checks the indexHistory for fullness
		if (!checkIndex(-1))
			clearIndexHistory();

		// Randomizes the microGameIndex (Dependent)
		do
		{
			microGameIndex = random.nextInt(microGames.length);
		} while(checkIndex(microGameIndex));
		for(int i = 0; i < indexHistory.length; i++)
			Log.d("indexHistory", "Index History = " + indexHistory[i]);
		
		// Increases difficulty level based on rounds completed.
		if (currentRound % roundsToLevel == 0 && currentRound != 1)
			if (level != 3)
				level++;
		
		// Increases speed level based on rounds completed.
		if (currentRound % roundsToSpeed == 0 && currentRound != 1)
			if (speed != 3)
				speed++;
		
		// Resets the MicroGame, and sets it's state to Running to skip the Ready state.
		microGames[microGameIndex].reset();
		microGames[microGameIndex].microGameState = MicroGameState.Running;
		microGames[microGameIndex].level = level;
		microGames[microGameIndex].speed = speed;
	}
	
	
	// Returns false if the index wasn't found in the array
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
	// TODO Updating
	public void presentTransition() {
		// Draws background.
		batcher.beginBatch(Assets.broFistBackground);
		batcher.drawSprite(0, 0, 1280, 800, Assets.broFistBackgroundRegion);
		batcher.endBatch();
		
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "Level: " + String.valueOf(level), 600, 550);
	    Assets.font.drawText(batcher, "Speed: " + String.valueOf(speed), 600, 500);
	    Assets.font.drawText(batcher, "Lives: " + String.valueOf(lives), 600, 450);
	    Assets.font.drawText(batcher, "Win Count: " + String.valueOf(winCount), 600, 400);
	    Assets.font.drawText(batcher, "Current Round: " + String.valueOf(currentRound), 600, 350);
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

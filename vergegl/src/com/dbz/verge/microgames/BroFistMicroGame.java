package com.dbz.verge.microgames;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.gl.Camera2D;
import com.dbz.framework.gl.FPSCounter;
import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.impl.MicroGame;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.Assets;
import com.dbz.verge.GameGridScreen;
import com.dbz.verge.World;
import com.dbz.verge.WorldRenderer;
import com.dbz.verge.World.WorldListener;

public class BroFistMicroGame extends MicroGame {

	// *** Fields ***
	
	// Used to track state of the microgame.
    MicroGameState microGameState;

    // OpenGL Related Objects
    Camera2D guiCam;
    SpriteBatcher batcher;
    FPSCounter fpsCounter;
    
    // World Objects
    World world;
    WorldListener worldListener;
    WorldRenderer renderer;   
    
    // TouchPoint Vector and Bounding Boxes
    Vector2 touchPoint;
    Rectangle readyBounds;
    Rectangle pauseToggleBounds;
    Rectangle backArrowBounds;
    Rectangle brofistBounds; 
    
    // Used to track running time for the game's timer.
    float totalRunningTime;
    
    // *** Constructor ***
    
    public BroFistMicroGame(Game game) {
        super(game);
        microGameState = MicroGameState.Ready;

        guiCam = new Camera2D(glGraphics, 1280, 800);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 1000);
        fpsCounter = new FPSCounter();
        
        worldListener = new WorldListener() {
            @Override
            public void jump() {            
                Assets.playSound(Assets.jumpSound);
            }

            @Override
            public void highJump() {
                Assets.playSound(Assets.highJumpSound);
            }

            @Override
            public void hit() {
                Assets.playSound(Assets.hitSound);
            }

            @Override
            public void coin() {
                Assets.playSound(Assets.coinSound);
            }                      
        };
        world = new World(worldListener);
        renderer = new WorldRenderer(glGraphics, batcher, world);

        readyBounds = new Rectangle(160, 160, 960, 480);
        pauseToggleBounds = new Rectangle(1130, 640, 160, 160);
        backArrowBounds = new Rectangle(0, 0, 150, 150);
        brofistBounds = new Rectangle(480, 280, 320, 240);
 
        totalRunningTime = 0;
    }

    // *** Update Methods ***
    
	@Override
	public void update(float deltaTime) {
	    if(deltaTime > 0.1f)
	        deltaTime = 0.1f;
	    
	    switch(microGameState) {
	    case Ready: // *** Might need to make this MicroGameState.Ready ***
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
	
	@Override
	public void updateReady() {	    
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);		// ***???***
	        guiCam.touchToWorld(touchPoint);		// ***???***

	        // Need Change DifficultyBounds (Not entirely sure here.)
	        
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
	
	@Override
	public void updateRunning(float deltaTime) {
		totalRunningTime += deltaTime;
		
		if (totalRunningTime > 5.0f) {
			Assets.playSound(Assets.hitSound); // Play losing sound here.
			microGameState = MicroGameState.Lost;
			return;
		}
		
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);		// ***???***
	        guiCam.touchToWorld(touchPoint);		// ***???***
	        
	        if(OverlapTester.pointInRectangle(brofistBounds, touchPoint)) {
	            Assets.playSound(Assets.coinSound); // Play winning sound here.
	            microGameState = MicroGameState.Won;
	            return;
	        }
	        if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            microGameState = MicroGameState.Paused;
	            return;
	        }
	    }

//  *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** **
//  *** WORLD UPDATES, WILL ONLY USE IN MICROGAMES THAT USE WORLDS ***
//  *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** **	    
//	    world.update(deltaTime, game.getInput().getAccelX());
//	    if(world.score != lastScore) {
//	        lastScore = world.score;
//	        scoreString = "" + lastScore;
//	    }
	        
//	    if(world.state == World.WORLD_STATE_NEXT_LEVEL) {
//	        state = GAME_LEVEL_END;        
//	    }

	}
	
	@Override
	public void updatePaused() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	     // Need RetryBounds, Change DifficultyLevelBounds
	        
	        touchPoint.set(event.x, event.y);		// ***???***
	        guiCam.touchToWorld(touchPoint);		// ***???***
	        
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
	
	@Override
	public void updateWon() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);		// ***???***
	        guiCam.touchToWorld(touchPoint);		// ***???***

	        // Need RetryBounds, Change DifficultyLevelBounds
	        
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new GameGridScreen(game));
	            return;     
	        }
	    }
	}
	
	@Override
	public void updateLost() {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);		// ***???***
	        guiCam.touchToWorld(touchPoint);		// ***???***
	        
	        // Need RetryBounds, Change DifficultyLevelBounds
	        
	        if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new GameGridScreen(game));
	            return;     
	        }
	    }
	}
	
	// *** Draw Methods ***

	@Override
	public void present(float deltaTime) {
	    GL10 gl = glGraphics.getGL();
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    gl.glEnable(GL10.GL_TEXTURE_2D);
	    
	    // renderer.render(); // ***???***
	    
	    guiCam.setViewportAndMatrices();
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    //batcher.beginBatch(Assets.items); // NEED TO COMPILE ALL REQUIRE ASSETS IN ONE SPRITE SHEET FOR THIS.
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
	
	@Override
	public void presentReady() {
//		batcher.beginBatch(Assets.items);
//	    batcher.drawSprite(160, 240, 192, 32, Assets.ready);
//	    batcher.endBatch();
	    
	    // Need change difficulty region (not entirely sure)
	    // batcher.drawSprite(0, 0, 0, 0, Assets.readyRegion);
		
	    // Back arrow drawn.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
        batcher.endBatch();
	    
	    // Bounding Boxes
	    batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(160, 160, 960, 480, Assets.boundOverlayRegion); // Ready Bounding Box
	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
	    batcher.endBatch();
	}
	
	@Override
	public void presentRunning() {
		
		// Could make instruction temporary this way...
//		if (totalRunningTime < 3) {
//			batcher.beginBatch(Assets.items);
//			Assets.font.drawText(batcher, "BROFIST!", 600, 700);
//			batcher.endBatch();
//		}
		// ...or could just dedicate screen space for it for the entire microgame.
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, "BROFIST!", 600, 700);
		batcher.endBatch();
		
		// Draw Brofist.
		batcher.beginBatch(Assets.brofist);
		batcher.drawSprite(480, 280, 320, 240, Assets.brofistRegion);
		batcher.endBatch();
		
		// Draw timer.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, String.format("%.2f", totalRunningTime), 600, 100);
		batcher.endBatch();
	    
		// Draw pause symbol.
		batcher.beginBatch(Assets.pauseToggle);
		batcher.drawSprite(1130, 640, 160, 160, Assets.pauseRegion);
		batcher.endBatch();
	    
	    // Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(1130, 640, 160, 160, Assets.boundOverlayRegion); // Pause Toggle Bounding Box
	    batcher.drawSprite(480, 280, 320, 240, Assets.boundOverlayRegion); // Brofist Bounding Box
	    batcher.endBatch();
	}
	
	@Override
	public void presentPaused() {
	    // Need change difficulty region, and retry.
		
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
        batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
	    batcher.drawSprite(1130, 640, 160, 160, Assets.boundOverlayRegion); // Pause Toggle Bounding Box
	    batcher.endBatch();
	}
	
	@Override
	public void presentWon() {	
		// Need change difficulty region, and retry.
		
		// Temporary win message.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "You Win!", 600, 500);
		batcher.endBatch();
		
		// Back arrow drawn.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
        batcher.endBatch();
		
		// Bounding Boxes
        batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
	    batcher.endBatch();
	}
	
	@Override
	public void presentLost() {
		// Need change difficulty region, and retry.
		
		// Temporary lose message.
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "You Lose!", 600, 500);
		batcher.endBatch();
		
		// Back arrow drawn.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
        batcher.endBatch();

		// Bounding Boxes
        batcher.beginBatch(Assets.boundOverlay);
	    batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
	    batcher.endBatch();
	}

	// *** Android State Management ***
	
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

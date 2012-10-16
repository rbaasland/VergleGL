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
//	  static final int GAME_READY = 0;    
//    static final int GAME_RUNNING = 1;
//    static final int GAME_PAUSED = 2;
//    static final int GAME_LEVEL_END = 3;
//    static final int GAME_OVER = 4;
    
    MicroGameState microGameState;
    // int state;
    Camera2D guiCam;
    Vector2 touchPoint;
    SpriteBatcher batcher;    
    World world;
    WorldListener worldListener;
    WorldRenderer renderer;    
    Rectangle pauseBounds;
    Rectangle resumeBounds;
    Rectangle quitBounds;
    int lastScore;
    String scoreString;    
    FPSCounter fpsCounter;
    
    public BroFistMicroGame(Game game) {
        super(game);
        microGameState = MicroGameState.Ready;
        // state = GAME_READY;
        guiCam = new Camera2D(glGraphics, 1280, 800);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 1000);
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
        pauseBounds = new Rectangle(1280- 64, 800- 64, 64, 64);
        resumeBounds = new Rectangle(160 - 96, 240, 192, 36);
        quitBounds = new Rectangle(160 - 96, 240 - 36, 192, 36);
        lastScore = 0;
        scoreString = "score: 0";
        fpsCounter = new FPSCounter();
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
	        updatePaused(deltaTime);
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
	    if(game.getInput().getTouchEvents().size() > 0)
	        // state = GAME_RUNNING;
	    	microGameState = MicroGameState.Running;
	}
	
	@Override
	public void updateRunning(float deltaTime) {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);		// ***???***
	        guiCam.touchToWorld(touchPoint);		// ***???***
	        
	        if(OverlapTester.pointInRectangle(pauseBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            // state = GAME_PAUSED;
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
	public void updatePaused(float deltaTime) {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);		// ***???***
	        guiCam.touchToWorld(touchPoint);		// ***???***
	        
	        if(OverlapTester.pointInRectangle(resumeBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            // state = GAME_RUNNING;
	            microGameState = MicroGameState.Running;
	            return;
	        }
	        
	        if(OverlapTester.pointInRectangle(quitBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            game.setScreen(new GameGridScreen(game));
	            return;     
	        }
	    }
	}
	
	@Override
	public void updateWon() {}
	
	@Override
	public void updateLost() {}
	
	// *** Draw Methods ***

	@Override
	public void present(float deltaTime) {
	    GL10 gl = glGraphics.getGL();
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    gl.glEnable(GL10.GL_TEXTURE_2D);
	    
	    renderer.render();
	    
	    guiCam.setViewportAndMatrices();
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    batcher.beginBatch(Assets.items);
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
	    batcher.endBatch();
	    gl.glDisable(GL10.GL_BLEND);
	    fpsCounter.logFrame();
	}
	
	@Override
	public void presentReady() {
	    batcher.drawSprite(160, 240, 192, 32, Assets.ready);
	}
	
	@Override
	public void presentRunning() {
	    batcher.drawSprite(1280 - 32, 800 - 32, 64, 64, Assets.pause);
	    Assets.font.drawText(batcher, scoreString, 16, 800-20);
	}
	
	@Override
	public void presentPaused() {        
	    batcher.drawSprite(160, 240, 192, 96, Assets.pauseMenu);
	    Assets.font.drawText(batcher, scoreString, 16, 800-20);
	}
	
	@Override
	public void presentWon() {}
	
	@Override
	public void presentLost() {}

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

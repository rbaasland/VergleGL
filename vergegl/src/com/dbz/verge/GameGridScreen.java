package com.dbz.verge;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.gl.Camera2D;
import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.impl.GLScreen;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;
import com.dbz.verge.microgames.BroFistMicroGame;
import com.dbz.verge.microgames.FireMicroGame;
import com.dbz.verge.microgames.FlyMicroGame;
import com.dbz.verge.microgames.TrafficMicroGame;

public class GameGridScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Vector2 touchPoint;
    
    // Bounding boxes
    Rectangle soundToggleBounds;
    Rectangle backArrowBounds;
    Rectangle nextPageBounds;
    Rectangle prevPageBounds;
    Rectangle firstMicroGameBounds;
    Rectangle secondMicroGameBounds;
    Rectangle thirdMicroGameBounds;
    Rectangle fourthMicroGameBounds;
    Rectangle fifthMicroGameBounds;
    Rectangle sixthMicroGameBounds;

    public GameGridScreen(Game game) {
        super(game);
        guiCam = new Camera2D(glGraphics, 1280, 800);
        batcher = new SpriteBatcher(glGraphics, 100);
        touchPoint = new Vector2(); 
        
        // Define bounding boxes.
        soundToggleBounds = new Rectangle(1120, 0, 160, 160);
        backArrowBounds = new Rectangle(0, 0, 160, 160);
        nextPageBounds = new Rectangle(345, 30, 75, 100);
        prevPageBounds = new Rectangle(860, 30, 75, 100);
        firstMicroGameBounds = new Rectangle(315, 435, 170, 170);
        secondMicroGameBounds = new Rectangle(555, 435, 170, 170);
        thirdMicroGameBounds = new Rectangle(795, 435, 170, 170);
        fourthMicroGameBounds = new Rectangle(315, 200, 170, 170);
        fifthMicroGameBounds = new Rectangle(555, 200, 170, 170);
        sixthMicroGameBounds = new Rectangle(795, 200, 170, 170);          
    }       

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);                        
            if(event.type == TouchEvent.TOUCH_UP) {
                touchPoint.set(event.x, event.y);
                guiCam.touchToWorld(touchPoint);
                
                if(OverlapTester.pointInRectangle(soundToggleBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    Settings.soundEnabled = !Settings.soundEnabled;
                    if(Settings.soundEnabled) 
                        Assets.music.play();
                    else
                        Assets.music.pause();
                }
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
                if (OverlapTester.pointInRectangle(nextPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// increment page.
                	return;
                }
                if (OverlapTester.pointInRectangle(prevPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// decrement page.
                	return;
                }
                if (OverlapTester.pointInRectangle(firstMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	game.setScreen(new BroFistMicroGame(game));
                	return;
                }
                if (OverlapTester.pointInRectangle(secondMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	game.setScreen(new FlyMicroGame(game));
                	// set screen to microgamescreen.
                	return;
                }
                if (OverlapTester.pointInRectangle(thirdMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	game.setScreen(new FireMicroGame(game));
                	// set screen to microgamescreen.
                	return;
                }
                if (OverlapTester.pointInRectangle(fourthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	game.setScreen(new TrafficMicroGame(game));
                	// set screen to microgamescreen.
                	return;
                }
                if (OverlapTester.pointInRectangle(fifthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// game.setScreen(new CircuitMicroGame(game));
                	// set screen to microgamescreen.
                	return;
                }
                if (OverlapTester.pointInRectangle(sixthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// set screen to microgamescreen.
                	return;
                }     
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        GL10 gl = glGraphics.getGL();        
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        guiCam.setViewportAndMatrices();
        
        gl.glEnable(GL10.GL_TEXTURE_2D);//Prepare matrix for binding -- We need to bind the texture, and we need to tell OpenGL ES
		//that it should actually apply the texture to all triangles we render.
        
        // Background image drawn.
        batcher.beginBatch(Assets.gameGridBackground);
        batcher.drawSprite(0, 0, 1280, 800, Assets.gameGridBackgroundRegion);
        batcher.endBatch();
        
        gl.glEnable(GL10.GL_BLEND); //pdf page 341 //tells OpenGL ES that it should apply alpha blending to all triangles rendered until disabled
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        // Draws icons.
        batcher.beginBatch(Assets.gameGridIcons);
        batcher.drawSprite(0, 0, 1024, 800, Assets.gameGridIconsRegion);
        batcher.endBatch();
        
        // Volume Toggle drawn.
        batcher.beginBatch(Assets.soundToggle);
        batcher.drawSprite(1120, 0, 160, 160, Settings.soundEnabled?Assets.soundOnRegion:Assets.soundOffRegion);
        batcher.endBatch();
        
        // Back arrow drawn.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(0, 0, 160, 160, Assets.backArrowRegion);
        batcher.endBatch(); 
        
        // Drawing Bounding Boxes.
//        batcher.beginBatch(Assets.boundOverlay);
//        
//        batcher.drawSprite(1120, 0, 160, 160, Assets.boundOverlayRegion); // SoundToggle Bounding Box
//        batcher.drawSprite(0, 0, 160, 160, Assets.boundOverlayRegion); // Back Arrow Bounding Box
//        batcher.drawSprite(345, 30, 75, 100, Assets.boundOverlayRegion); // Next Page Bounding Box
//        batcher.drawSprite(860, 30, 75, 100, Assets.boundOverlayRegion); // Previous Page Bounding Box
//        batcher.drawSprite(315, 435, 170, 170, Assets.boundOverlayRegion); // 1st MicroGame Bounding Box
//        batcher.drawSprite(555, 435, 170, 170, Assets.boundOverlayRegion); // 2nd MicroGame Bounding Box
//        batcher.drawSprite(795, 435, 170, 170, Assets.boundOverlayRegion); // 3rd MicroGame Bounding Box
//        batcher.drawSprite(315, 200, 170, 170, Assets.boundOverlayRegion); // 4th MicroGame Bounding Box
//        batcher.drawSprite(555, 200, 170, 170, Assets.boundOverlayRegion); // 5th MicroGame Bounding Box
//        batcher.drawSprite(795, 200, 170, 170, Assets.boundOverlayRegion); // 6th MicroGame Bounding Box
//        
//        batcher.endBatch();
        
        gl.glDisable(GL10.GL_BLEND);
    }
    
    // Android State Management
    
    @Override
    public void pause() {
        Settings.save(game.getFileIO());
    }

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
}

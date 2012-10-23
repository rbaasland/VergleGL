package com.dbz.verge;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.dbz.bc.HelpScreen;
import com.dbz.bc.HighScoresScreen;
import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.gl.Camera2D;
import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.impl.GLScreen;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.framework.math.Vector2;

public class MainMenuScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Vector2 touchPoint;
    
    // Bounding boxes
    Rectangle soundToggleBounds;
    Rectangle playBounds;
    Rectangle highscoresBounds;
    Rectangle helpBounds;

    public MainMenuScreen(Game game) {
        super(game);
        guiCam = new Camera2D(glGraphics, 1280, 800);
        batcher = new SpriteBatcher(glGraphics, 100);
        touchPoint = new Vector2();     
        
        // Define bounding boxes.
        soundToggleBounds = new Rectangle(1120, 0, 160, 160);
        playBounds = new Rectangle(350, 510, 580, 100);
        highscoresBounds = new Rectangle(350, 350, 580, 100);
        helpBounds = new Rectangle(350, 190, 580, 100);                        
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
//                    if(Settings.soundEnabled) 
//                        Assets.music.play();
//                    else
//                        Assets.music.pause();
                }
                if(OverlapTester.pointInRectangle(playBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new GameGridScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(highscoresBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
//                    game.setScreen(new HighScoresScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(helpBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
//                    game.setScreen(new HelpScreen(game));
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
        
        //here background image is drawn
        batcher.beginBatch(Assets.background);
        batcher.drawSprite(0, 0, 1280, 800, Assets.backgroundRegion); // previously 640, 400, 1280, 800 
        batcher.endBatch();
        
        gl.glEnable(GL10.GL_BLEND); //pdf page 341 //tells OpenGL ES that it should apply alpha blending to all triangles rendered until disabled
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);         
        
        // Main Menu Buttons drawn.
        batcher.beginBatch(Assets.mainMenuButtons);
        batcher.drawSprite(0, 0, 1280, 800, Assets.mainMenuButtonsRegion);
        batcher.endBatch();
        
        // Volume Toggle drawn.
        batcher.beginBatch(Assets.soundToggle);
        batcher.drawSprite(1120, 0, 160, 160, Settings.soundEnabled?Assets.soundOnRegion:Assets.soundOffRegion);
        batcher.endBatch();
        
//        // Drawing Bounding Boxes.
//        batcher.beginBatch(Assets.boundOverlay);
//        batcher.drawSprite(1120, 0, 160, 160, Assets.boundOverlayRegion); // SoundToggle Bounding Box
//        batcher.drawSprite(350, 510, 580, 100, Assets.boundOverlayRegion); // 1st Button Bounding Box
//        batcher.drawSprite(350, 350, 580, 100, Assets.boundOverlayRegion); // 2nd Button Bounding Box
//        batcher.drawSprite(350, 190, 580, 100, Assets.boundOverlayRegion); // 3rd Button Bounding Box
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

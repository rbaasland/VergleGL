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

//*** Bounding Box (Rectangle) Logic is LowerLeft ***
//*** Draw calls are centered. ***

public class MainMenuScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Rectangle soundBounds;
    Rectangle playBounds;
    Rectangle highscoresBounds;
    Rectangle helpBounds;
    Vector2 touchPoint;

    public MainMenuScreen(Game game) {
        super(game);
        guiCam = new Camera2D(glGraphics, 1280, 800);
        batcher = new SpriteBatcher(glGraphics, 100);
        soundBounds = new Rectangle(1120, 0, 160, 160);
        playBounds = new Rectangle(350, 510, 580, 100);
        highscoresBounds = new Rectangle(350, 350, 580, 100);
        helpBounds = new Rectangle(350, 190, 580, 100);
        touchPoint = new Vector2();               
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
                
                if(OverlapTester.pointInRectangle(playBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new GameScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(highscoresBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new HighscoresScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(helpBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new HelpScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(soundBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    Settings.soundEnabled = !Settings.soundEnabled;
                    if(Settings.soundEnabled) 
                        Assets.music.play();
                    else
                        Assets.music.pause();
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
        batcher.drawSprite(640, 400, 1280, 800, Assets.backgroundRegion);
        batcher.endBatch();
        
        gl.glEnable(GL10.GL_BLEND); //pdf page 341 //tells OpenGL ES that it should apply alpha blending to all triangles rendered until disabled
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);         
        
        // Main Menu Buttons drawn.
        batcher.beginBatch(Assets.mainMenu);
        batcher.drawSprite(640, 400, 1280, 800, Assets.mainMenuRegion);
        batcher.endBatch();
        
        // Volume Toggle drawn.
        batcher.beginBatch(Assets.soundToggle);
        batcher.drawSprite(1200, 80, 160, 160, Settings.soundEnabled?Assets.soundOnRegion:Assets.soundOffRegion);
        batcher.endBatch();
        
        // Drawing Bounding Boxes.
        batcher.beginBatch(Assets.boundOverlay);
        batcher.drawSprite(1200, 80, 160, 160, Assets.boundOverlayRegion); // SoundToggle Bounding Box
        batcher.drawSprite(640, 560, 580, 100, Assets.boundOverlayRegion); // 1st Button Bounding Box
        batcher.drawSprite(640, 400, 580, 100, Assets.boundOverlayRegion); // 2nd Button Bounding Box
        batcher.drawSprite(640, 240, 580, 100, Assets.boundOverlayRegion); // 3rd Button Bounding Box
        batcher.endBatch();
        
        // batcher.beginBatch(Assets.items); //bind textures, by binding this, we don't have to bind each asset individually
        // batcher.drawSprite(160, 480 - 10 - 71, 274, 142, Assets.logo);
        // batcher.drawSprite(160, 200, 300, 110, Assets.mainMenu);
        // batcher.drawSprite(32, 32, 64, 64, Settings.soundEnabled?Assets.soundOn:Assets.soundOff); //damn sexy line of code here. 
        // batcher.endBatch(); //draw vertices?, then unbind() textures
        
        gl.glDisable(GL10.GL_BLEND);
    }
    
    @Override
    public void pause() {        
        Settings.save(game.getFileIO());
    }

    @Override
    public void resume() {}       

    @Override
    public void dispose() {}
}

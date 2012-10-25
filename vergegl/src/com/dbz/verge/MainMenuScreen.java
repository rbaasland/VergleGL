package com.dbz.verge;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;

public class MainMenuScreen extends MenuScreen {
	
	// --------------
	// --- Fields ---
	// --------------
    
    // Bounding Boxes.
    private Rectangle playBounds = new Rectangle(350, 510, 580, 100);
    private Rectangle highScoresBounds = new Rectangle(350, 350, 580, 100);
    private Rectangle helpBounds = new Rectangle(350, 190, 580, 100);

    // -------------------
 	// --- Constructor ---
    // -------------------
    public MainMenuScreen(Game game) {
        super(game);                    
    }       

    // ---------------------
 	// --- Update Method ---
 	// ---------------------
    @Override
    public void update(float deltaTime) {
    	// Gets all TouchEvents and stores them in a list.
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        
        // Cycles through and tests all touch events.
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
        	// Gets a single TouchEvent from the list.
            TouchEvent event = touchEvents.get(i);    
            
            if(event.type == TouchEvent.TOUCH_UP) {
            	// Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
                touchPoint.set(event.x, event.y);
                // Sends the vector to the OpenGL Camera for handling.
                guiCam.touchToWorld(touchPoint);
                
                // Play Button Bounds Check.
                if(OverlapTester.pointInRectangle(playBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new PlayMenuScreen(game));
                    return;
                }
                
                // High Scores Button Bounds Check.
                if(OverlapTester.pointInRectangle(highScoresBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    // game.setScreen(new HighScoresScreen(game));
                    return;
                }
                
                // Help Button Bounds Check.
                if(OverlapTester.pointInRectangle(helpBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    //  game.setScreen(new HelpScreen(game));
                    return;
                }
                
                // Checks for general MenuScreen events.
    	        if (event.type == TouchEvent.TOUCH_UP)
    	        	super.update(touchPoint);
            }
        }
    }
    
    // ----------------------------
 	// --- Utility Draw Methods ---
 	// ----------------------------
    
    @Override
    public void drawBackground() {
        batcher.beginBatch(Assets.background);
        batcher.drawSprite(0, 0, 1280, 800, Assets.backgroundRegion);
        batcher.endBatch();
    }
    
    @Override
    public void drawObjects() {
        // Draws Main Menu Buttons.
        batcher.beginBatch(Assets.mainMenuButtons);
        batcher.drawSprite(0, 0, 1280, 800, Assets.mainMenuButtonsRegion);
        batcher.endBatch();
        
        super.drawObjects();
    }
    
    @Override
    public void drawBounds() {
      batcher.beginBatch(Assets.boundOverlay);     
      batcher.drawSprite(playBounds, Assets.boundOverlayRegion); 		// Play Button Bounding Box
      batcher.drawSprite(highScoresBounds, Assets.boundOverlayRegion);  // HighScores Button Bounding Box
      batcher.drawSprite(helpBounds, Assets.boundOverlayRegion); 		// Help Button Bounding Box
      super.drawBounds();
      batcher.endBatch();
    }

}

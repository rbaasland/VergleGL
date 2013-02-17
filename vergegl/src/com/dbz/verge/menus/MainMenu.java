package com.dbz.verge.menus;

import java.util.List;

import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.Menu;

public class MainMenu extends Menu {
	
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
    public MainMenu() {             
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
            
            // Only handle if TouchEvent is TOUCH_UP.
            if(event.type == TouchEvent.TOUCH_UP) {
            	// Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
                touchPoint.set(event.x, event.y);
                // Sends the vector to the OpenGL Camera for handling.
                guiCam.touchToWorld(touchPoint);
                
                // Play Button Bounds Check.
                if(OverlapTester.pointInRectangle(playBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    game.setScreen(new PlayMenu());
                    return;
                }
                
                // High Scores Button Bounds Check.
                if(OverlapTester.pointInRectangle(highScoresBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    game.setScreen(new HighScoreScreen());
                    return;
                }
                
                // Help Button Bounds Check.
                if(OverlapTester.pointInRectangle(helpBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    //  game.setScreen(new HelpScreen(game));
                    return;
                }
                
                // Non-Unique, Super Class Bounds Check.
    	        super.update(touchPoint);
            }
        }
    }
    
    // ----------------------------
 	// --- Utility Draw Methods ---
 	// ----------------------------
    
    @Override
    public void drawBackground() {
        batcher.beginBatch(AssetsManager.background);
        batcher.drawSprite(0, 0, 1280, 800, AssetsManager.backgroundRegion);
        batcher.endBatch();
    }
    
    @Override
    public void drawObjects() {
        // Draws Main Menu Buttons.
        batcher.beginBatch(AssetsManager.mainMenuButtons);
        batcher.drawSprite(0, 0, 1280, 800, AssetsManager.mainMenuButtonsRegion);
        batcher.endBatch();
        
        super.drawObjects();
    }
    
    @Override
    public void drawBounds() {
      batcher.beginBatch(AssetsManager.boundOverlay);     
      batcher.drawSprite(playBounds, AssetsManager.boundOverlayRegion); 		// Play Button Bounding Box
      batcher.drawSprite(highScoresBounds, AssetsManager.boundOverlayRegion);  // HighScores Button Bounding Box
      batcher.drawSprite(helpBounds, AssetsManager.boundOverlayRegion); 		// Help Button Bounding Box
      super.drawBounds();
      batcher.endBatch();
    }

}

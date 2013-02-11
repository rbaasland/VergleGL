package com.dbz.verge.menus;

import java.util.List;

import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.Menu;
import com.dbz.verge.modes.SurvivalMode;
import com.dbz.verge.modes.TimeAttackMode;

public class PlayMenu extends Menu {
	
	// --------------
	// --- Fields ---
	// --------------
    
    // Bounding Boxes.
    private Rectangle survivalBounds = new Rectangle(350, 510, 580, 100);
    private Rectangle timeAttackBounds = new Rectangle(350, 350, 580, 100);
    private Rectangle gameGridBounds = new Rectangle(350, 190, 580, 100);
    private Rectangle backArrowBounds = new Rectangle(5, 5, 140, 140);

    // -------------------
 	// --- Constructor ---
    // -------------------
    
    public PlayMenu() {}       

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
                
                // Survival Button Bounds Check.   
                if(OverlapTester.pointInRectangle(survivalBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    game.setScreen(new SurvivalMode());
                    return;
                }
                
                // Time Attack Button Bounds Check.
                if(OverlapTester.pointInRectangle(timeAttackBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    game.setScreen(new TimeAttackMode());
                    return;
                }
                
                // Game Grid Button Bounds Check.
                if(OverlapTester.pointInRectangle(gameGridBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    game.setScreen(new GameGridMenu());
                    return;
                }
                
                // Back Arrow Bounds Check.
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    game.setScreen(new MainMenu());
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
    
    public void drawBackground() {
        batcher.beginBatch(AssetsManager.background);
        batcher.drawSprite(0, 0, 1280, 800, AssetsManager.backgroundRegion);
        batcher.endBatch();
    }
    
    public void drawObjects() {
        // Draws Play Menu Buttons.
        batcher.beginBatch(AssetsManager.playMenuButtons);
        batcher.drawSprite(0, 0, 1280, 800, AssetsManager.playMenuButtonsRegion);
        batcher.endBatch();
        
        // Draws Back Arrow.
        batcher.beginBatch(AssetsManager.backArrow);
        batcher.drawSprite(backArrowBounds, AssetsManager.backArrowRegion);
        batcher.endBatch(); 
        
        super.drawObjects();
    }
    
    public void drawBounds() {
      batcher.beginBatch(AssetsManager.boundOverlay);     
      batcher.drawSprite(survivalBounds, AssetsManager.boundOverlayRegion); 	// Survival Button Bounding Box
      batcher.drawSprite(timeAttackBounds, AssetsManager.boundOverlayRegion);  // Time Attack Bounding Box
      batcher.drawSprite(gameGridBounds, AssetsManager.boundOverlayRegion); 	// Game Grid Button Bounding Box
      batcher.drawSprite(backArrowBounds, AssetsManager.boundOverlayRegion); 	// Back Arrow Bounding Box
      super.drawBounds();
      batcher.endBatch();
    }
    
    @Override
    public void onBackPressed(){
    	game.setScreen(new MainMenu());
    }
    
}

package com.dbz.verge;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;

public class PlayMenuScreen extends MenuScreen {
	
	// --------------
	// --- Fields ---
	// --------------
    
    // Bounding Boxes.
    private Rectangle gameGridBounds = new Rectangle(350, 510, 580, 100);
    private Rectangle survivalBounds = new Rectangle(350, 350, 580, 100);
    private Rectangle timeAttackBounds = new Rectangle(350, 190, 580, 100);
    private Rectangle backArrowBounds = new Rectangle(0, 0, 160, 160);

    // -------------------
 	// --- Constructor ---
    // -------------------
    public PlayMenuScreen(Game game) {
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
                
                // Game Grid Button Bounds Check.
                if(OverlapTester.pointInRectangle(gameGridBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new GameGridMenuScreen(game));
                    return;
                }
                
                // Survival Button Bounds Check.
                if(OverlapTester.pointInRectangle(survivalBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new GameScreen(game));
                    return;
                }
                
                // Time Attack Button Bounds Check.
                if(OverlapTester.pointInRectangle(timeAttackBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    //  game.setScreen(new HelpScreen(game));
                    return;
                }
                
                // Back Arrow Bounds Check.
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new MainMenuScreen(game));
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
    
    public void drawBackground() {
        batcher.beginBatch(Assets.background);
        batcher.drawSprite(0, 0, 1280, 800, Assets.backgroundRegion);
        batcher.endBatch();
    }
    
    public void drawObjects() {
        // Draws Play Menu Buttons.
        batcher.beginBatch(Assets.playMenuButtons);
        batcher.drawSprite(0, 0, 1280, 800, Assets.playMenuButtonsRegion);
        batcher.endBatch();
        
        // Draws Back Arrow.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
        batcher.endBatch(); 
        
        super.drawObjects();
    }
    
    public void drawBounds() {
      batcher.beginBatch(Assets.boundOverlay);     
      batcher.drawSprite(gameGridBounds, Assets.boundOverlayRegion); 	// Game Grid Button Bounding Box
      batcher.drawSprite(survivalBounds, Assets.boundOverlayRegion);  	// Survival Button Bounding Box
      batcher.drawSprite(timeAttackBounds, Assets.boundOverlayRegion); 	// Time Attack Bounding Box
      batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); 	// Back Arrow Bounding Box
      super.drawBounds();
      batcher.endBatch();
    }

}

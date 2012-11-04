package com.dbz.verge;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.microgames.BroFistMicroGame;
import com.dbz.verge.microgames.CircuitMicroGame;
import com.dbz.verge.microgames.FireMicroGame;
import com.dbz.verge.microgames.FlyMicroGame;
import com.dbz.verge.microgames.LazerBallMicroGame;
import com.dbz.verge.microgames.TrafficMicroGame;

public class GameGridMenuScreen extends MenuScreen {
   
	// --------------
	// --- Fields ---
	// --------------
	
	// Page Variables.
    private int currentPage = 1;
    private int numOfPages = 2;
    
    // Bounding Boxes.
    private Rectangle firstMicroGameBounds = new Rectangle(315, 435, 170, 170);
    private Rectangle secondMicroGameBounds = new Rectangle(555, 435, 170, 170);
    private Rectangle thirdMicroGameBounds = new Rectangle(795, 435, 170, 170);
    private Rectangle fourthMicroGameBounds = new Rectangle(315, 200, 170, 170);
    private Rectangle fifthMicroGameBounds = new Rectangle(555, 200, 170, 170);
    private Rectangle sixthMicroGameBounds = new Rectangle(795, 200, 170, 170);
    private Rectangle backArrowBounds = new Rectangle(0, 0, 160, 160);
    private Rectangle nextPageBounds = new Rectangle(345, 30, 75, 100);
    private Rectangle prevPageBounds = new Rectangle(860, 30, 75, 100);
    // Overlay variable.
    private boolean levelOverlay = false;
    private MicroGame selectedMicroGame;
    private Rectangle readyBounds = new Rectangle(0, 640, 160, 160);
    private Rectangle overlayBounds = new Rectangle(160, 160, 960, 480);
    
    // -------------------
 	// --- Constructor ---
    // -------------------
    public GameGridMenuScreen(Game game) {
        super(game); 
    }       

    // ---------------------
 	// --- Update Method ---
 	// ---------------------
    @Override
    public void update(float deltaTime) {
    	//Redirect to level overlay
    	if (levelOverlay)
    		updateLevelOverlay(deltaTime);
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
                
                // First MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(firstMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new BroFistMicroGame(game);
                	else if (currentPage == 2)
                		selectedMicroGame = new TrafficMicroGame(game); // Replace with new microgame.
                	levelOverlay = true;
                	return;
                }
                
                // Second MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(secondMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new FlyMicroGame(game);
                	levelOverlay = true;
                	return;
                }
                
                // Third MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(thirdMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new FireMicroGame(game);
                	levelOverlay = true;
                	return;
                }
                
                // Fourth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(fourthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new TrafficMicroGame(game);
                	levelOverlay = true;
                	return;
                }
                
                // Fifth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(fifthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new CircuitMicroGame(game);
                	levelOverlay = true;
                	return;
                }
                
                // Sixth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(sixthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new LazerBallMicroGame(game);
                	levelOverlay = true;
                	return;
                }
                
                // Back Arrow Bounds Check.
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new PlayMenuScreen(game));
                    return;
                }
                
                // Next Page Bounds Check.
                if (OverlapTester.pointInRectangle(nextPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// increment page.
                	if (currentPage+1 > numOfPages)
                		currentPage = 1;
                	else
                		currentPage++;
                	return;
                }
                
                // Previous Page Bounds Check.
                if (OverlapTester.pointInRectangle(prevPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// decrement page.
                	if (currentPage-1 < 1)
                		currentPage = numOfPages;
                	else
                		currentPage--;
                	return;
                }
                
                // Checks for general MenuScreen events.
    	        if (event.type == TouchEvent.TOUCH_UP)
    	        	super.update(touchPoint);
            }
        }
    }
    
    public void updateLevelOverlay(float deltaTime)
    {
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
                
                // Ready Bounds Check.
                if(OverlapTester.pointInRectangle(readyBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(selectedMicroGame);
                    return;
                }
                
                // Back Arrow Bounds Check.
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new PlayMenuScreen(game));
                    return;
                }
                
                // Next Page Bounds Check.
                if (OverlapTester.pointInRectangle(nextPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// increment page.
                	if (selectedMicroGame.level+1 > 3)
                		selectedMicroGame.level = 1;
                	else
                		selectedMicroGame.level++;
                	return;
                }
                
                // Previous Page Bounds Check.
                if (OverlapTester.pointInRectangle(prevPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// decrement page.
                	if (selectedMicroGame.level-1 < 1)
                		selectedMicroGame.level = 3;
                	else
                		selectedMicroGame.level--;
                	return;
                }
                
                // Checks for general MenuScreen events.
    	        if (event.type == TouchEvent.TOUCH_UP)
    	        	super.update(touchPoint);
    	        
    	        // If TOUCH_UP wasn't in any bounds, remove overlay state.
    	        levelOverlay = false;
            }
        }	
    }
    
    // ----------------------------
 	// --- Utility Draw Methods ---
 	// ----------------------------
    
    public void drawBackground() {
    	batcher.beginBatch(Assets.gameGridBackground);
        batcher.drawSprite(0, 0, 1280, 800, Assets.gameGridBackgroundRegion);
        batcher.endBatch();
    }
    
    public void drawObjects() {
        // Draws Game Grid Icons.
    	if (currentPage == 1) {
    		batcher.beginBatch(Assets.gameGridIcons);
        	batcher.drawSprite(0, 0, 1024, 800, Assets.gameGridIconsRegion);
        	batcher.endBatch();
    	}
        
        // Draws Back Arrow.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
        batcher.endBatch(); 
        
        // Draws Page Number.
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, String.valueOf(currentPage), 600, 50);
		batcher.endBatch();
        
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, String.valueOf(levelOverlay), 600, 750);
		batcher.endBatch();
		
		if(levelOverlay) {
			batcher.beginBatch(Assets.items);
			Assets.font.drawText(batcher, "Level: " + String.valueOf(selectedMicroGame.level), 600, 700);
			batcher.endBatch();
			
			batcher.beginBatch(Assets.boundOverlay);
			batcher.drawSprite(readyBounds, Assets.boundOverlayRegion);
			batcher.drawSprite(overlayBounds, Assets.boundOverlayRegion);
			batcher.endBatch();
		}
		
        super.drawObjects();
    }
    
    public void drawBounds() {
        batcher.beginBatch(Assets.boundOverlay);
        batcher.drawSprite(firstMicroGameBounds, Assets.boundOverlayRegion);	// 1st MicroGame Bounding Box
        batcher.drawSprite(secondMicroGameBounds, Assets.boundOverlayRegion); 	// 2nd MicroGame Bounding Box
        batcher.drawSprite(thirdMicroGameBounds, Assets.boundOverlayRegion); 	// 3rd MicroGame Bounding Box
        batcher.drawSprite(fourthMicroGameBounds, Assets.boundOverlayRegion); 	// 4th MicroGame Bounding Box
        batcher.drawSprite(fifthMicroGameBounds, Assets.boundOverlayRegion); 	// 5th MicroGame Bounding Box
        batcher.drawSprite(sixthMicroGameBounds, Assets.boundOverlayRegion); 	// 6th MicroGame Bounding Box
        batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); 	// Back Arrow Bounding Box
        batcher.drawSprite(nextPageBounds, Assets.boundOverlayRegion); 		// Next Page Bounding Box
        batcher.drawSprite(prevPageBounds, Assets.boundOverlayRegion); 		// Previous Page Bounding Box
        super.drawBounds();
        batcher.endBatch();
    }
}
